package dao;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Classe abstraite permettant de factoriser du code pour les DAO
 * basées sur JDBC
 */
public abstract class AbstractDataBaseDAO {

    private final DataSource dataSource;

    protected AbstractDataBaseDAO(DataSource ds) {
        this.dataSource = ds;
    }

    protected Connection getConn() throws SQLException {
        return dataSource.getConnection();
    }

    public DataSource getDataSource() {
        return dataSource;
    }
}
