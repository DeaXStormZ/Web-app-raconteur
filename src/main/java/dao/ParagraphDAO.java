package dao;

import model.Choice;
import model.Paragraph;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ParagraphDAO extends AbstractDataBaseDAO {

    /**
     * @param ds
     */
    public ParagraphDAO(DataSource ds) {
        super(ds);
    }

    /**
     * @param idStory
     * @param idParagraph
     * @return the paragraph with the idStory and idParagraph
     */
    public Paragraph getParagraph(int idStory, int idParagraph) {
        try (Connection conn = getConn()) {
            PreparedStatement st = conn.prepareStatement(
                    "SELECT * FROM PARAGRAPH WHERE IDSTORY = ? AND IDPARAGRAPH = ?");
            ResultSet rs = paragraphQuery(st, idStory, idParagraph);
            if (!rs.next())
                return null;

            String text = rs.getString("text");
            String title = rs.getString("ParagraphTitle");
            String authorLogin = rs.getString("Author");

            PreparedStatement stConclusion = conn.prepareStatement("SELECT * FROM CONCLUSIONPARAGRAPH " +
                    "WHERE IDSTORY = ? AND IDPARAGRAPH = ?");
            boolean conclusion = paragraphQuery(stConclusion, idStory, idParagraph).next();

            PreparedStatement stValidated = conn.prepareStatement("SELECT * FROM VALIDATEDPARAGRAPH " +
                    "WHERE IDSTORY = ? AND IDPARAGRAPH = ?");
            boolean validated = paragraphQuery(stValidated, idStory, idParagraph).next();

            List<Choice> choices = new ArrayList<Choice>();
            st = conn.prepareStatement("SELECT * FROM CHOICE " +
                    "WHERE IDSTORY = ? AND IDPARAGRAPH = ?");
            st.setInt(1, idStory);
            st.setInt(2, idParagraph);
            rs = st.executeQuery();
            while (rs.next()) {
                choices.add(new Choice(idStory, rs.getInt("idChoice"),
                        rs.getInt("condition"),
                        rs.getInt("nextParagraph"),
                        idParagraph,
                        false));
            }
            UserDAO userDAO = new UserDAO(getDataSource());

            Paragraph result = new Paragraph(idParagraph, conclusion, validated, text, title, userDAO.getUser(authorLogin), choices);

            conn.close();
            return result;
        } catch (SQLException e) {
            throw new DAOException("Erreur BD" + e.getMessage(), e);
        }
    }

    /**
     * Ajoute un paragraphe dans la base de donnée
     * @param idStory
     * @param title
     * @param text
     * @param author
     * @return l'id du paragraphe ajouté
     */
    public int addParagraph(int idStory, String title, String text, String author) {
        try (Connection c = getConn()) {
            PreparedStatement st = c.prepareStatement("INSERT INTO PARAGRAPH (idstory, paragraphtitle, text, author) VALUES (?, ?, ?, ?)");
            st.setInt(1, idStory);
            st.setString(2, title);
            Clob clob = c.createClob();
            clob.setString(1, text);
            st.setClob(3, clob);
            st.setString(4, author);
            st.executeUpdate();
            st = c.prepareStatement("SELECT IDPARAGRAPH FROM PARAGRAPH where IDSTORY = ? and PARAGRAPHTITLE = ? and AUTHOR = ?");
            st.setInt(1, idStory);
            st.setString(2, title);
            st.setString(3, author);
            ResultSet rs = st.executeQuery();
            rs.next();
            int anInt = rs.getInt("idParagraph");
            c.close();
            return anInt;
        } catch (SQLException e) {
            throw new DAOException("Erreur BD" + e.getMessage(), e);
        }
    }

    /**
     * Execute une query sur une table paragraph. idStory doit être en position 1 et idParagraph en position 2
     *
     * @param st
     * @param idStory
     * @param idParagraph
     * @return
     */
    private ResultSet paragraphQuery(PreparedStatement st, int idStory, int idParagraph) {
        try {
            st.setInt(1, idStory);
            st.setInt(2, idParagraph);
            return st.executeQuery();
        } catch (SQLException e) {
            throw new DAOException("Erreur BD" + e.getMessage(), e);
        }
    }

    public void setText(int idStory, int idParagraph, String text) {
        try (Connection conn = getConn()) {
            PreparedStatement st = conn.prepareStatement(
                    "UPDATE PARAGRAPH SET text = ? WHERE IDSTORY = ? and IDPARAGRAPH = ?");
            st.setString(1, text);
            st.setInt(2, idStory);
            st.setInt(3, idParagraph);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erreur BD " + e.getMessage(), e);
        }
    }

    /**
     * makes the paragraph a possible conclusion
     *
     * @param idStory
     * @param idParagraph
     */
    public void conclude(int idStory, int idParagraph) {
        try (Connection c = getConn()) {
            PreparedStatement st = c.prepareStatement("INSERT INTO CONCLUSIONPARAGRAPH VALUES (?, ?)");
            st.setInt(1, idStory);
            st.setInt(2, idParagraph);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erreur BD " + e.getMessage(), e);
        }
    }

    /**
     * validate a paragraph
     *
     * @param idStory
     * @param idParagraph
     */
    public void validate(int idStory, int idParagraph) {
        try (Connection c = getConn()) {
            PreparedStatement st = c.prepareStatement("INSERT INTO VALIDATEDPARAGRAPH VALUES (?, ?)");
            st.setInt(1, idStory);
            st.setInt(2, idParagraph);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erreur BD " + e.getMessage(), e);
        }
    }

    public List<Paragraph> getParagraphsWithOneChoice(int idStory, Paragraph firstParagraph) {
        List<Paragraph> result = new ArrayList<Paragraph>();
        while (firstParagraph.getChoices().size() == 1) {
            result.add(firstParagraph);
            firstParagraph = getParagraph(idStory, firstParagraph.getChoices().get(0).getIdNextParagraph());
        }
        result.add(firstParagraph);
        return result;
    }

    public List<Paragraph> getParagraphs(int idStory) {
        ArrayList<Paragraph> result = new ArrayList<>();
        try (Connection conn = getConn()) {
            PreparedStatement st = conn.prepareStatement("SELECT * FROM PARAGRAPH WHERE IDSTORY = ?");
            st.setInt(1, idStory);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                result.add(getParagraph(idStory, rs.getInt("idParagraph")));
            }
            return result;
        } catch (SQLException e) {
            throw new DAOException("Erreur BD " + e.getMessage(), e);
        }
    }

    public void removeParagraph(int idStory, int idParagraph) {
        ChoiceDAO choiceDAO = new ChoiceDAO(getDataSource());
        for (Choice choice : getParagraph(idStory, idParagraph).getChoices()) {
            choiceDAO.removeChoice(idStory, idParagraph, choice.getIdChoice());
        }
        try (Connection c = getConn()) {
            PreparedStatement st = c.prepareStatement("DELETE FROM PARAGRAPH WHERE IDSTORY = ? and IDPARAGRAPH = ?");
            st.setInt(1, idStory);
            st.setInt(2, idParagraph);
            st.executeQuery();
        } catch (SQLException e) {
            throw new DAOException("Erreur BD : " + e.getMessage());
        }
    }
}

