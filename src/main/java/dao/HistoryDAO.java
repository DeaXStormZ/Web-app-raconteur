package dao;

import model.Choice;
import model.History;
import model.Story;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

public class HistoryDAO extends AbstractDataBaseDAO {
    public HistoryDAO(DataSource ds) {
        super(ds);
    }

    public void addChoiceToHistory(int idStory, int idParagraph, int idChoice, String login, int position) {
        try (Connection conn = getConn()) {
            PreparedStatement st = conn.prepareStatement("INSERT INTO historyElement VALUES (?, ?, ?, ?, ?)");
            st.setInt(1, idChoice);
            st.setInt(2, idParagraph);
            st.setInt(3, idStory);
            st.setString(4, login);
            st.setInt(5, position);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erreur BD " + e.getMessage(), e);
        }
    }

    public void addChoiceToHistory(int idStory, int idParagraph, int idChoice, String login, History history) {
        addChoiceToHistory(idStory, idParagraph, idChoice, login, history.getHistory().size() + 1);
    }

    /**
     * Switches a choice for a new one, deleting every subsequent choices in the history
     *
     * @param newIdChoice
     * @param position
     * @param history
     */
    public void modifyChoice(String login, int newIdChoice, int position, History history) {
        try (Connection conn = getConn()) {
            PreparedStatement st = conn.prepareStatement("UPDATE historyElement SET IDCHOICE = ? " +
                    "WHERE IDPARAGRAPH = ? and IDSTORY = ? AND LOGIN = ?");
            st.setInt(1, newIdChoice);
            int idParagraph = getIdParagraph(login, position, history);
            st.setInt(2, idParagraph);
            st.setInt(3, history.getIdStory());
            st.setString(4, login);
            st.executeUpdate();
            deleteChoicesFrom(login, position + 1, history);
        } catch (SQLException e) {
            throw new DAOException("Erreur BD " + e.getMessage(), e);
        }
    }


    /**
     * Gets the id of the paragraph where the choice at given position in history is situated
     *
     * @param position
     * @param history
     * @return
     */
    public int getIdParagraph(String login, int position, History history) {
        try (Connection c = getConn()) {
            PreparedStatement st = c.prepareStatement("SELECT IDPARAGRAPH FROM historyElement " +
                    "WHERE LOGIN = ? AND IDSTORY = ? AND POSITION = ?");
            st.setString(1, login);
            st.setInt(2, history.getIdStory());
            st.setInt(3, position);
            ResultSet rs = st.executeQuery();
            rs.next();
            int idParagraph = rs.getInt("idParagraph");
            c.close();
            return idParagraph;
        } catch (SQLException e) {
            throw new DAOException("Erreur BD " + e.getMessage(), e);
        }
    }

    /**
     * Deletes every choice starting from a given position
     *
     * @param position
     * @param history
     */
    private void deleteChoicesFrom(String login, int position, History history) {
        try (Connection c = getConn()) {
            PreparedStatement st = c.prepareStatement("DELETE FROM historyElement " +
                    "WHERE IDSTORY = ? AND LOGIN = ? AND POSITION >= ?");
            st.setInt(1, history.getIdStory());
            st.setString(2, login);
            st.setInt(3, position);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erreur BD " + e.getMessage(), e);
        }
    }


    /**
     * Adds an history to the database
     *
     * @param login
     * @param history
     */
    public void addHistory(String login, int idStory, History history) {
        Choice currChoice = null;
        for (int i = 0; i < history.getHistory().size(); i++) {
            currChoice = history.getHistory().get(i);
            addChoiceToHistory(idStory, currChoice.getIdNextParagraph(), currChoice.getIdChoice(), login, i + 1);
        }
    }

    /**
     * Updates the history in the database, adding the new choices not already present in the database.
     * The old history cannot have been modified : see modifyChoice for this operation
     *
     * @param login
     * @param newHistory
     */
    public void updateHistory(String login, int idStory, History newHistory) {
        History oldHistory = getHistory(login, idStory);
        Choice currentChoice = null;
        for (int i = oldHistory.getHistory().size(); i < newHistory.getHistory().size(); i++) {
            currentChoice = newHistory.getHistory().get(i);
            addChoiceToHistory(idStory, currentChoice.getIdNextParagraph(), currentChoice.getIdChoice(), login, i + 1);
        }
    }


    /**
     * @param login
     * @param idStory
     * @return the history of the user on this story
     */
    public History getHistory(String login, int idStory) {
        try (Connection c = getConn()) {
            ChoiceDAO choiceDAO = new ChoiceDAO(getDataSource());
            PreparedStatement st = c.prepareStatement("SELECT * FROM historyElement WHERE LOGIN = ? AND IDSTORY = ?");
            st.setString(1, login);
            st.setInt(2, idStory);
            ResultSet rs = st.executeQuery();

            HashMap<Integer, Choice> choices = new HashMap<>();
            int idParagraph;
            int idChoice;
            int position;
            while (rs.next()) {
                idParagraph = rs.getInt("idParagraph");
                idChoice = rs.getInt("idChoice");
                position = rs.getInt("position");
                choices.put(position, choiceDAO.getChoice(idStory, idParagraph, idChoice));
            }
            ArrayList<Choice> history = new ArrayList<>();
            for (int i = 0; i < choices.size(); i++) {
                history.add(choices.get(i));
            }
            StoryDAO storyDAO = new StoryDAO(getDataSource());
            c.close();
            return new History(idStory, history);
        } catch (SQLException e) {
            throw new DAOException("Erreur BD " + e.getMessage(), e);
        }
    }

    /**
     * @param login
     * @return All histories of the user on the stories he reads
     */
    public HashMap<Story, History> getHistories(String login) {
        try (Connection c = getConn()) {
            PreparedStatement st = c.prepareStatement("SELECT DISTINCT IDSTORY FROM historyElement " +
                    "WHERE LOGIN = ?");
            ResultSet rs = st.executeQuery();
            HashMap<Story, History> histories = new HashMap<>();
            int idStory;
            StoryDAO storyDAO = new StoryDAO(getDataSource());

            while (rs.next()) {
                idStory = rs.getInt("idStory");
                histories.put(storyDAO.getStory(idStory), getHistory(login, idStory));
            }
            c.close();
            return histories;
        } catch (SQLException e) {
            throw new DAOException("Erreur BD " + e.getMessage(), e);
        }
    }
}
