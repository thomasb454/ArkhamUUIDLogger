/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.uuid.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author devan_000
 */
public class Database {

    Connection c = null;
    MySQL mysql = null;

    public Database(Connection c, MySQL mysql) {
        this.c = c;
        this.mysql = mysql;
    }

    public boolean databaseContainsUUID(String uuid) throws SQLException {
        if (!mysql.checkConnection()) {
            c = mysql.open();
        }

        if (c == null) {
            return false;
        }

        Statement statement = c.createStatement();
        statement.executeQuery("SELECT * FROM playerstorage WHERE uuid='" + uuid + "'");
        ResultSet rs = statement.getResultSet();

        while (rs.next()) {
            return true;
        }
        return false;
    }

    public void createAccountStorage(String playerName, String UUID) throws SQLException {
        if (!mysql.checkConnection()) {
            c = mysql.open();
        }

        if (c == null) {
            return;
        }

        Statement statement = c.createStatement();
        statement.execute("INSERT INTO playerstorage (`uuid`, `playername`) VALUES ('" + UUID + "', '" + playerName + "');");
    }

    public String getStoredAccountNameFromUUID(String UUID) throws SQLException {
        if (!mysql.checkConnection()) {
            c = mysql.open();
        }

        if (c == null) {
            return null;
        }

        Statement statement = c.createStatement();
        statement.executeQuery("SELECT playername FROM playerstorage WHERE uuid='" + UUID + "'");
        ResultSet rs = statement.getResultSet();

        while (rs.next()) {
            return rs.getString("playername");
        }

        return null;
    }
}
