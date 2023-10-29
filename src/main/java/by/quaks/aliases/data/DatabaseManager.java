package by.quaks.aliases.data;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DatabaseManager {
    private ConnectionSource connectionSource;

    public DatabaseManager(String databaseUrl) {
        try {
            connectionSource = new JdbcConnectionSource(databaseUrl);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initializeTables(Class<? extends ITable>... tableClasses) {
        try {
            for (Class<? extends ITable> tableClass : tableClasses) {
                TableUtils.createTableIfNotExists(connectionSource, tableClass);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public <T, ID> Dao<T, ID> getDao(Class<T> clazz) {
        try {
            return DaoManager.createDao(connectionSource, clazz);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ConnectionSource getConnectionSource() {
        return connectionSource;
    }
}
