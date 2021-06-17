/*
 * Mikibits Utility Classes (Java)
 * Reusable classes and class wrappers to add/fix features.
 * ------------------------------------------------------------------
 * DbTable - Base class used to define Database tables.
 * ------------------------------------------------------------------
 * Author:      Miki Marshall (mikibits.com)
 * Created: 2020.01.01
 * Version: 2020.01.11
 *
 * Notes:
 */

package mikilib;

import java.util.ArrayList;
import java.util.HashMap;

public class DbTable {
    private Database db;
    protected String name;
    private ArrayList<HashMap<String, String>> records;
    private int recNo;

    /**
     * Constructor - Create/define a database table.
     * @param db   - The database.
     * @param name - Table name.
     */
    public DbTable(Database db, String name) {
        this.db = db;
        this.name = name;
        records = new ArrayList<>();
        recNo = -1;
    }

    /**
     * Create the table in the database.
     * @param sql - SQL field definitions part.
     */
    protected void create(String sql) {
        db.createTable(name + " (\n" + sql);
    }

    /**
     * Replace any single quotes with two, to avoid fracking the SQL.
     * @implNote - used externally to clean up data before building SQL (for now).
     * @param data - Incoming SQL.
     * @return - Dequoted SQL.
     */
    public String dequote(String data) {
        return data.replaceAll("'", "''");
    }

    /**
     * Move pointer to first record loaded.
     * @return - true if record there (not empty).
     */
    protected boolean firstRecord() {
        recNo = 0;
        return validRecord();
    }

    /**
     * getInt - Retrieve column value from current record.
     * @param column - column to retrieve.
     * @return - Integer value.
     */
    public Integer getInt(String column) {
        return Integer.valueOf(getStr(column));
    }

    /**
     * getLong - Retrieve column value from current record.
     * @param column - column to retrieve.
     * @return - Long value.
     */
    public Long getLong(String column) {
        return Long.valueOf(getStr(column));
    }

    /**
     * getInt - Retrieve column value from current record.
     * @param column - column to retrieve.
     * @return - Integer value.
     */
    public String getStr(String column) {
        if (validRecord()) {
            // Retrieve data by column name, restoring single quotes
            String value = records.get(recNo).get(column);
            return requote(value);
        }
        return "";
    }

    /**
     * Create an index for this table.
     * @param sql - SQL for index definition (only).
     */
    protected void index(String sql) {
        db.createIndex(sql);
    }

    /**
     * Insert a record into the table.
     * @param sql - SQL of fields and data values.
     * @return - ID of new record.
     */
    protected int insert(String sql) {
        return db.insert(name + " " + sql);
    }

    /**
     * Increment the row of currently loaded records.
     * @return - true if this is a valid row (false if empty or eof).
     */
    protected boolean nextRecord() {
        recNo++;
        return validRecord();
    }

    /**
     * Restore any dequoted single quotes to data before returning it.
     * @implNote - used internally by getStr(), which is good enough.
     * @param data - the dequoted data.
     * @return - data with single quotes restored.
     */
    private String requote(String data) {
        return data.replaceAll("''", "'");
    }

    /**
     * Select record(s) from this table.
     * @param sql- SQL (all but the word SELECT).
     * @return - true if records found/loaded.
     */
    protected boolean select(String sql) {
        // Load records into a HashMap list and reset the record pointer
        records = db.select(sql);
        return firstRecord();
    }

    /**
     * Update this table.
     * @param sql - SQL of fields and values.
     */
    protected void update(String sql) {
        db.update(name + " SET " + sql);
    }

    /**
     * Checks if records are loaded and we are pointed at one.
     * @return - true pointed at a valid record (false if empty or eof).
     */
    public boolean validRecord() {
        return ((records != null) && (recNo >= 0) && (recNo < records.size()));
    }
}
