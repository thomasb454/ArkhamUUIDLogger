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

        Statement statement = null;
        ResultSet rs = null;

        try {
            statement = c.createStatement();
            statement.executeQuery("SELECT * FROM playerstorage WHERE uuid='" + uuid + "'");
            rs = statement.getResultSet();

            while (rs.next()) {
                return true;
            }
        } finally {
            if (statement != null && !statement.isClosed()) {
                statement.close();
            }

            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
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

        Statement statement = null;

        try {
            statement = c.createStatement();
            statement.execute("INSERT INTO playerstorage (`uuid`, `playername`) VALUES ('" + UUID + "', '" + playerName + "');");
        } finally {
            if (statement != null && !statement.isClosed()) {
                statement.close();
            }
        }
    }

    public String getStoredAccountNameFromUUID(String UUID) throws SQLException {
        if (!mysql.checkConnection()) {
            c = mysql.open();
        }

        if (c == null) {
            return null;
        }

        Statement statement = null;
        ResultSet rs = null;

        try {
            statement = c.createStatement();
            statement.executeQuery("SELECT playername FROM playerstorage WHERE uuid='" + UUID + "'");
            rs = statement.getResultSet();

            while (rs.next()) {
                return rs.getString("playername");
            }
        } finally {
            if (statement != null && !statement.isClosed()) {
                statement.close();
            }

            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        }

        return null;
    }

    public String getStoredUUIDFromAccount(String playerName) throws SQLException {
        if (!mysql.checkConnection()) {
            c = mysql.open();
        }

        if (c == null) {
            return null;
        }

        Statement statement = null;
        ResultSet rs = null;

        try {
            statement = c.createStatement();
            statement.executeQuery("SELECT uuid FROM playerstorage WHERE playername='" + playerName + "'");
            rs = statement.getResultSet();

            while (rs.next()) {
                return rs.getString("uuid");
            }
        } finally {
            if (statement != null && !statement.isClosed()) {
                statement.close();
            }

            if (rs != null && !rs.isClosed()) {
                rs.close();
            }
        }

        return null;
    }
}
