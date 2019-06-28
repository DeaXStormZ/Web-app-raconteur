package dao;

import model.Choice;
import model.History;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChoiceDAO extends AbstractDataBaseDAO {

    public static final String ID_PARAGRAPH = "idParagraph";
    public static final String ID_CHOICE = "idChoice";

    public ChoiceDAO(DataSource ds) {
        super(ds);
    }

    /**
     * set a condition to an existing choice
     *
     * @param idStory
     * @param idParagraph
     * @param idChoice
     * @param condition
     */
    public void setCondition(int idStory, int idParagraph, int idChoice, int condition) {
        try (
                Connection conn = getConn();
                PreparedStatement st = conn.prepareStatement(
                        "UPDATE Choice SET condition = ? WHERE IDSTORY = ? and IDPARAGRAPH = ? and IDCHOICE = ?")
        ) {
            st.setInt(1, condition);
            st.setInt(2, idStory);
            st.setInt(3, idParagraph);
            st.setInt(4, idChoice);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erreur BD " + e.getMessage(), e);
        }
    }

    /**
     * get the object choice designed by the following parameters
     *
     * @param idStory
     * @param idParagraph
     * @param idChoice
     * @return
     */
    public Choice getChoice(int idStory, int idParagraph, int idChoice) {
        try (Connection conn = getConn()) {
            PreparedStatement st = conn.prepareStatement("SELECT * FROM CHOICE " +
                    "WHERE IDCHOICE = ? AND IDPARAGRAPH = ? AND IDSTORY = ?");
            ResultSet rs = choiceQuery(st, idStory, idParagraph, idChoice);
            if (!rs.next())
                return null;
            int condition = rs.getInt("condition");
            int idNextParagraph = rs.getInt("nextParagraph");
            int idCurrentParagraph = rs.getInt(ID_PARAGRAPH);

            PreparedStatement stLocked = conn.prepareStatement("SELECT * FROM LOCKEDCHOICE " +
                    "WHERE IDSTORY = ? AND IDPARAGRAPH = ? AND IDCHOICE = ?");
            ResultSet rsLocked = choiceQuery(stLocked, idStory, idParagraph, idChoice);
            boolean isLocked = rsLocked.next();
            conn.close();
            ParagraphDAO paragraphDAO = new ParagraphDAO(getDataSource());
            conn.close();
            return new Choice(idStory, idChoice, condition, idNextParagraph,
                    idCurrentParagraph, isLocked);
        } catch (SQLException e) {
            throw new DAOException("Erreur BD" + e.getMessage(), e);
        }
    }

    private ResultSet choiceQuery(PreparedStatement st, int idStory, int idParagraph, int idChoice) {
        try {
            st.setInt(1, idChoice);
            st.setInt(2, idParagraph);
            st.setInt(3, idStory);
            return st.executeQuery();
        } catch (SQLException e) {
            throw new DAOException("Erreur BD" + e.getMessage(), e);
        }
    }


    public void addChoice(int idStory, int idParagraph, int condition, int idNextParagraph) {
        try (
                Connection conn = getConn();
                PreparedStatement st = conn.prepareStatement
                        ("INSERT INTO CHOICE (IDSTORY, IDPARAGRAPH, CONDITION, NEXTPARAGRAPH) VALUES (?, ?, ?, ?)");
        ) {
            st.setInt(1, idStory);
            st.setInt(2, idParagraph);
            st.setInt(3, condition);
            st.setInt(4, idNextParagraph);
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erreur BD " + e.getMessage(), e);
        }
    }

    /**
     * get all the choices (showing also the conditional choices which condition is not satisfied)
     *
     * @param idStory
     * @param idParagraph
     * @return
     */
    public ArrayList<Choice> getChoices(int idStory, int idParagraph) {
        ArrayList<Choice> result = new ArrayList<>();
        try (Connection conn = getConn()) {
            PreparedStatement st = conn.prepareStatement("SELECT * FROM CHOICE WHERE IDPARAGRAPH = ? AND IDSTORY = ?");
            st.setInt(1, idParagraph);
            st.setInt(2, idStory);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                result.add(getChoice(idStory, idParagraph, rs.getInt(ID_CHOICE)));
            }
            return result;
        } catch (SQLException e) {
            throw new DAOException("Erreur BD " + e.getMessage(), e);
        }
    }

    /**
     * get the available choices (hiding the conditional choices which condition is not satisfied)
     *
     * @param idStory
     * @param idParagraph
     * @param history
     * @return
     */
    public ArrayList<Choice> getAvailableChoices(int idStory, int idParagraph, History history) {
        ArrayList<Choice> result = getChoices(idStory, idParagraph);
        for (Choice choice : result) {
            if (history != null) {
                if (choice.getCondition() > 0 & !history.getHistory().contains(getChoice(idStory, choice.getIdNextParagraph(), choice.getIdChoice()))) {
                    result.remove(choice);
                }
                if (choice.getCondition() < 0 & history.getHistory().contains(getChoice(idStory, choice.getIdNextParagraph(), choice.getIdChoice()))) {
                    result.remove(choice);
                }
            }
        }
        return result;
    }

    public List<Choice> getUnlockedChoices(int idStory) {
        ArrayList<Choice> result = new ArrayList<>();
        try (Connection conn = getConn()) {
            PreparedStatement st = conn.prepareStatement("SELECT * FROM CHOICE LEFT JOIN LOCKEDCHOICE L on CHOICE.IDSTORY = L.IDSTORY and CHOICE.IDPARAGRAPH = L.IDPARAGRAPH and CHOICE.IDCHOICE = L.IDCHOICE" +
                    " AND CHOICE.IDSTORY = ?");
            st.setInt(1, idStory);
            ResultSet rs = st.executeQuery();
            while (rs.next()) {
                result.add(getChoice(idStory, rs.getInt(ID_PARAGRAPH), rs.getInt(ID_CHOICE)));
            }
            return result;
        } catch (SQLException e) {
            throw new DAOException("Erreur BD " + e.getMessage(), e);
        }
    }

    public void removeChoice(int idStory, int idParagraph, int idChoice) {
        try (Connection c = getConn()) {
            PreparedStatement st = c.prepareStatement("DELETE FROM CHOICE WHERE IDSTORY = ? and IDPARAGRAPH = ? and IDCHOICE = ?");
            st.setInt(1, idStory);
            st.setInt(2, idParagraph);
            st.setInt(3, idChoice);
            st.executeQuery();
        } catch (SQLException e) {
            throw new DAOException("Erreur BD : " + e.getMessage());
        }
    }
}
