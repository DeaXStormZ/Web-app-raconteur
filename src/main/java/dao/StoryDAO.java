package dao;

import model.Paragraph;
import model.Story;
import model.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StoryDAO extends AbstractDataBaseDAO {
    public StoryDAO(DataSource ds) {
        super(ds);
    }

    public int addStory(String title, String author, String headParagraphTitle, String text) {
        try (Connection conn = getConn()) {
            PreparedStatement st = conn.prepareStatement("INSERT INTO STORY (storytitle, author) VALUES (?, ?)");
            st.setString(1, title);
            st.setString(2, author);
            st.executeUpdate();
            st = conn.prepareStatement("select IDSTORY from STORY where STORYTITLE = ? and AUTHOR = ?");
            st.setString(1, title);
            st.setString(2, author);
            ResultSet rs = st.executeQuery();
            rs.next();

            ParagraphDAO paragraphDAO = new ParagraphDAO(getDataSource());
            int idStory = rs.getInt("idStory");
            int idHeadParagraph = paragraphDAO.addParagraph(idStory, headParagraphTitle, text, author);
            st = conn.prepareStatement("UPDATE STORY SET HEADPARAGRAPH = ? where IDSTORY = ?");
            st.setInt(1, idHeadParagraph);
            st.setInt(2, idStory);
            st.executeUpdate();
            conn.close();
            return idStory;
        } catch (SQLException e) {
            throw new DAOException("Erreur BD " + e.getMessage(), e);
        }
    }

    /**
     * @param idStory
     * @return a story object corresponding to the given Story id.
     */
    public Story getStory(int idStory) {
        UserDAO userDAO = new UserDAO(super.getDataSource());
        ParagraphDAO paragraphDAO = new ParagraphDAO(super.getDataSource());
        try (Connection conn = getConn()) {
            PreparedStatement st = conn.prepareStatement("SELECT * FROM PUBLISHEDSTORY WHERE IDSTORY = ?");
            st.setInt(1, idStory);
            ResultSet rs = st.executeQuery();
            boolean isPublished = rs.next();
            st = conn.prepareStatement("SELECT * FROM STORY WHERE IDSTORY = ?");
            st.setInt(1, idStory);
            rs = st.executeQuery();
            Story result = null;
            if (rs.next()) {
                result = new Story(rs.getInt("idStory"), rs.getString("storyTitle"),
                        userDAO.getUser(rs.getString("AUTHOR")), getContributors(idStory),
                        isPublished,
                        paragraphDAO.getParagraph(rs.getInt("idStory"), rs.getInt("headParagraph")));
            }
            conn.close();
            return result;
        } catch (SQLException e) {
            throw new DAOException("Erreur BD " + e.getMessage(), e);
        }

    }

    /**
     * @param isPublished set to true : returns publihed stories; set to false : returns unpublished stories
     * @return
     */
    public ArrayList<Story> getPublishedStories(boolean isPublished) {//FIXME leftjoin not doing what is needed
        ArrayList<Story> result = new ArrayList<Story>();
        UserDAO userDAO = new UserDAO(super.getDataSource());
        ParagraphDAO paragraphDAO = new ParagraphDAO(super.getDataSource());
        try (Connection conn = getConn()) {
            PreparedStatement st = conn.prepareStatement("SELECT * FROM STORY" + ((isPublished) ? "" : "LEFT") + " JOIN PUBLISHEDSTORY P on STORY.IDSTORY = P.IDSTORY");
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                Story story =
                        new Story(rs.getInt("idStory"), rs.getString("storyTitle"),
                                userDAO.getUser(rs.getString("AUTHOR")), getContributors(rs.getInt("idStory")),
                                isPublished,
                                paragraphDAO.getParagraph(rs.getInt("idStory"), rs.getInt("headParagraph")));
                result.add(story);
            }
            conn.close();
            return result;
        } catch (SQLException e) {
            throw new DAOException("Erreur BD " + e.getMessage(), e);
        }
    }

    private ArrayList<User> getContributors(int idStory) {
        ArrayList<User> result = new ArrayList<>();
        UserDAO userDAO = new UserDAO(getDataSource());
        try (Connection conn = getConn()) {
            PreparedStatement st = conn.prepareStatement("SELECT LOGIN FROM STORY JOIN ISACONTRIBUTOR I on STORY.IDSTORY = I.IDSTORY WHERE I.IDSTORY = ?");
            st.setInt(1, idStory);
            ResultSet rs = st.executeQuery();

            while (rs.next()) {
                result.add(userDAO.getUser(rs.getString("login")));
            }
            conn.close();
            return result;
        } catch (SQLException e) {
            throw new DAOException("Erreur BD : " + e.getMessage());
        }
    }

    /**
     * @param login
     * @return The stories written originally by the user
     */
    public ArrayList<Story> getAuthored(String login) {
        return getStories(login);
    }

    public List<Story> getContributed(String login) {
        List<Story> result = new ArrayList<>();
        UserDAO userDAO = new UserDAO(getDataSource());
        for (Story story : getAllStories()) {
            if (getContributors(story.getIdStory()).contains(userDAO.getUser(login))) {
                result.add(story);
            }
        }
        return result;
    }

    /**
     * @return all the stories from the database
     */
    public ArrayList<Story> getAllStories() {
        return getStories(null);
    }

    private ArrayList<Story> getStories(String login) {
        ArrayList<Story> result = new ArrayList<>();
        UserDAO userDAO = new UserDAO(super.getDataSource());
        ParagraphDAO paragraphDAO = new ParagraphDAO(super.getDataSource());
        try (Connection c = getConn()) {
            PreparedStatement stStories;
            if (login == null) {
                stStories = c.prepareStatement("SELECT * from STORY");
            } else {
                stStories = c.prepareStatement("SELECT * from STORY WHERE AUTHOR = ?");
                stStories.setString(1, login);
            }
            ResultSet rs = stStories.executeQuery();

            while (rs.next()) {
                Story story =
                        new Story(rs.getInt("idStory"), rs.getString("storyTitle"),
                                userDAO.getUser(rs.getString("AUTHOR")), getContributors(rs.getInt("idStory")),
                                true, //FIXME put te right parameter with a left join
                                paragraphDAO.getParagraph(rs.getInt("idStory"), rs.getInt("headParagraph")));
                result.add(story);
            }
            c.close();
            return result;
        } catch (SQLException e) {
            throw new DAOException("Erreur BD : " + e.getMessage());
        }
    }

    /**
     * @param ids : Story ids
     * @return A list of stories corresponding to the given ids
     */
    private ArrayList<Story> getStoriesFromIds(ArrayList<Integer> ids) {
        ArrayList<Story> stories = new ArrayList<>();
        for (int id : ids) {
            stories.add(getStory(id));
        }
        return stories;
    }

    /**
     * Set the story as published in the database
     *
     * @param story : Needs to be an unpublished story
     */
    public void publish(Story story) {
        try (Connection c = getConn()) {
            PreparedStatement st = c.prepareStatement("INSERT INTO PUBLISHEDSTORY VALUES (?)");
            st.setInt(1, story.getIdStory());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erreur BD : " + e.getMessage());
        }
    }

    /**
     * Unpublishes a story in the database
     *
     * @param story : Needs to be an published story
     */
    public void unpublish(Story story) {
        try (Connection c = getConn()) {
            PreparedStatement st = c.prepareStatement("DELETE FROM PUBLISHEDSTORY WHERE IDSTORY = ?");
            st.setInt(1, story.getIdStory());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erreur BD : " + e.getMessage());
        }
    }

    /**
     * Adds a contributor to a story in the database
     *
     * @param story
     * @param contributor
     */
    public void addContributor(Story story, User contributor) {
        try (Connection c = getConn()) {
            PreparedStatement st = c.prepareStatement("INSERT INTO ISACONTRIBUTOR VALUES (?, ?)");
            executeContributorModification(story, contributor, st);
        } catch (SQLException e) {
            throw new DAOException("Erreur BD : " + e.getMessage());
        }
    }

    /**
     * Removes a contributor from a story in the database
     *
     * @param story
     * @param contributor
     */
    public void removeContributor(Story story, User contributor) {
        try (Connection c = getConn()) {
            PreparedStatement st = c.prepareStatement("DELETE FROM ISACONTRIBUTOR WHERE LOGIN = ? AND IDSTORY = ?");
            executeContributorModification(story, contributor, st);
        } catch (SQLException e) {
            throw new DAOException("Erreur BD : " + e.getMessage());
        }
    }

    private void executeContributorModification(Story story, User contributor, PreparedStatement st) throws SQLException {
        st.setString(1, contributor.getLogin());
        st.setInt(2, story.getIdStory());
        st.executeUpdate();
    }

    public void removeStory(int idStory) {
        ParagraphDAO paragraphDAO = new ParagraphDAO(getDataSource());
        for (Paragraph paragraph : paragraphDAO.getParagraphs(idStory)) {
            paragraphDAO.removeParagraph(idStory, paragraph.getIdParagraph());
        }
        try (Connection c = getConn()) {
            PreparedStatement st = c.prepareStatement("DELETE FROM STORY WHERE IDSTORY = ?");
            st.setInt(1, idStory);
            st.executeQuery();
        } catch (SQLException e) {
            throw new DAOException("Erreur BD : " + e.getMessage());
        }
    }
}
