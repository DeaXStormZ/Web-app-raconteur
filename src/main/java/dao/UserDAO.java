package dao;

import model.User;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO extends AbstractDataBaseDAO {
    public UserDAO(DataSource ds) {
        super(ds);
    }

    /**
     * check the combination of login and password
     *
     * @return wether the password is correct for the given login or not
     */
    public Boolean isLoginValid(String login, String password) {
        try (Connection c = getConn()) {
            PreparedStatement st = c.prepareStatement("SELECT login FROM REGISTEREDUSER WHERE login = ? AND password = ?");
            st.setString(1, login);
            st.setString(2, password);
            ResultSet rs = st.executeQuery();
            boolean next = rs.next();
            c.close();
            return next;
        } catch (SQLException e) {
            throw new DAOException("Erreur BD : " + e.getMessage(), e);
        }
    }

    public Boolean isLoginExisting(String login) {
        try (Connection c = getConn()) {
            PreparedStatement st = c.prepareStatement("SELECT login FROM REGISTEREDUSER WHERE login = ?");
            st.setString(1, login);
            ResultSet rs = st.executeQuery();
            boolean next = rs.next();
            c.close();
            return next;
        } catch (SQLException e) {
            throw new DAOException("Erreur BD : " + e.getMessage(), e);
        }
    }

    public User getUser(String login) {
        // Les autres attributs peuvent être obtenus par appel aux autres méthodes de userDAO
        return new User(login);
    }


    /**
     * Adds a user (login and password) to the database
     *
     * @param user
     */
    public void addUser(User user) {
        try (Connection c = getConn()) {
            // c.setAutoCommit(false);
            // c.setSavepoint();
            // c.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);

            PreparedStatement st = c.prepareStatement("INSERT INTO REGISTEREDUSER VALUES (?, ?)");
            st.setString(1, user.getLogin());
            st.setString(2, user.getPassword());

            st.executeUpdate();
            // c.commit();
        } catch (SQLException e) {
            //try {
            //    getConn().rollback();
            //} catch (SQLException e2) {
            //    throw new DAOException("Erreur BD : " + e2.getMessage());
            //}
            throw new DAOException("Erreur BD : " + e.getMessage());
        }
    }

    /**
     * Removes a user from the database
     *
     * @param user
     */
    public void removeUser(User user) {
        try (Connection c = getConn()) {
            PreparedStatement st = c.prepareStatement("DELETE FROM REGISTEREDUSER WHERE LOGIN = ?");
            st.setString(1, user.getLogin());
            st.executeUpdate();
        } catch (SQLException e) {
            throw new DAOException("Erreur BD : " + e.getMessage());
        }
    }

    /**
     * get the author of the story
     *
     * @param idStory
     * @return returns a user object, representing the author of the given story
     */
    public User getAuthor(int idStory) {
        try (Connection c = getConn()) {
            PreparedStatement st = c.prepareStatement("SELECT AUTHOR FROM STORY WHERE IDSTORY = ?");
            st.setInt(1, idStory);
            ResultSet rs = st.executeQuery();
            rs.next();
            String authorLogin = rs.getString("author");
            c.close();
            return new User(authorLogin);
        } catch (SQLException e) {
            throw new DAOException("Erreur BD " + e.getMessage(), e);
        }
    }
}
