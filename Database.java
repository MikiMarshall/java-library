/*
 * Mikibits Utility Classes (Java)
 * Reusable classes and class wrappers to add/fix features.
 * ------------------------------------------------------------------
 * Database - A managed SQLite database wrapper.
 * ------------------------------------------------------------------
 * Author:      Miki Marshall (mikibits.com)
 * Created: 2018.02.17
 * Version: 2020.01.11
 *
 * Notes:
 */

package mikilib;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;

public class Database {
    // Constants
    private static final String DB_CONNECT_PREFIX = "jdbc:sqlite:";

    // Fields
    private File file;
    private String dbFile;

    /**
     * Constructor. Creates a database instance with a file name.
     * @param filename      The database file.
     */
    public Database(String filename) {
        this.file = new File(filename);

        // Create connection string from file name
        dbFile = DB_CONNECT_PREFIX + filename;

        // Activate SQLite foreign key support
        query("PRAGMA foreign_keys = ON;");
    }

    /**
     * Open a connection to do fun database stuff. Only called internally,
     * since it does not close the connection in this scope [danger Will Robinson].
     * @implNote ALWAYS call in Try-with-References, which automagically closes it.
     * @return a Connection object.
     */
    public Connection connect() {
        Connection conn = null;
        try {
            // Get connection to database
            conn = DriverManager.getConnection(dbFile);
        }
        catch (SQLException e) {
            Debug.out("Database.connect())", e.getMessage());
        }
        // Return for use by class method
        return conn;
    }

    /**
     * Simple wrapper for creating tables, omitting the expected
     * statement prelude.
     * @param sql       The table and field definition, minus the initial command.
     */
    public void createIndex(String sql) {
        query("CREATE UNIQUE INDEX IF NOT EXISTS " + sql);
    }

    /**
     * Simple wrapper for creating tables, omitting the expected
     * statement prelude.
     * @param sql       The table and field definition, minus the initial command.
     */
    public void createTable(String sql) {
        query("CREATE TABLE IF NOT EXISTS " + sql);
    }

    public void delete(String sql) {
        query("DELETE FROM " + sql);
    }

    public boolean recordExists(String table, String context) {
        // Attempt to retrieve a record
        ArrayList<HashMap<String, String>> record = select("*",
                table, context);

        // Record exists if something came back
        return ((record != null) && (!record.isEmpty()));
    }

    /**
     * For things like window titles, return the a string version of our current
     * file name.
     * @return      file name as String.
     */
    public String getFileName() {
        return file.getName();
    }

    /**
     * getNewKey - Retrieve the new recID, if a new record was created.
     * @param st - Connection statement from previous doSQL call.
     * @return - new recordID, or -1 = no new record created.
     */
    public int getNewKey(Statement st) {
        // Check if a new key/rowID was generated and return it
        try (ResultSet rs = st.getGeneratedKeys()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        catch (SQLException e) {
            Debug.out("Database.getNewKey()", e);
        }

        return -1;
    }

    public int insert(String sql) {
        return query("INSERT INTO " + sql);
    }

    /**
     * Return the current timestamp (date and time) in an SQL Date
     * friendly format.
     * @return      timestamp as Date.
     */
    public static Date now() {
        java.util.Date utilDate = new java.util.Date();
        return (new Date(utilDate.getTime()));
    }

    /**
     * Execute SQL statements that do not need to return a result set.
     * @param sql - SQL statement to execute.
     * @return the new records ID, if this generated a new record.
     */
    public int query(String sql) {
        // Open connection and do the thing
        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            // Create statement and execute
            stmt.execute(sql);
            return getNewKey(stmt);
        }
        catch (SQLException e) {
            Debug.out("Database.query(" + sql + ")", e);
        }

        return -1;
    }

    /**
     * select - run a Select query on the database.
     * @param fields - fields to select.
     * @param table - select from table.
     * @param context - what goes after WHERE.
     * @return - a result set, or null on error.
     */
    public ArrayList<HashMap<String, String>> select(String fields, String table,
                            String context) {
        String sql = fields + "FROM " + table +
                " WHERE " + context + ";";
        return select(sql);
    }

    public ArrayList<HashMap<String, String>> select(String sql) {
        // Attempt to select record(s)
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT " + sql)) {
            // Since result sets don't survive out of scope...
            if (rs.next()) {
                // Get the column setup from the first record metadata
                ResultSetMetaData md = rs.getMetaData();
                int colCount = md.getColumnCount();

                // Offload the data into something that survives
                ArrayList<HashMap<String, String>> result = new ArrayList<>();
                do {                                                    // rows
                    HashMap<String, String> rec = new HashMap<>(colCount);
                    for (int colNo = 1; colNo <= colCount; colNo++) {    // columns
                        // Store column data by name, restoring single quotes
                        rec.put(md.getColumnName(colNo), rs.getString(colNo));
                    }
                    result.add(rec);
                } while (rs.next());

                // Return records
                return result;
            }
        }
        catch (SQLException e) {
            Debug.out("Database.select(" + sql + ")", e);
        }
        // No record found
        return null;
    }

    public void update(String sql) {
        query("UPDATE " + sql);
    }
}
