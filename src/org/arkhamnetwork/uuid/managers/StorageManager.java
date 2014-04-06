/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.uuid.managers;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.arkhamnetwork.uuid.ArkhamUUIDLogger;

/**
 *
 * @author devan_000
 */
public class StorageManager {

    static ArkhamUUIDLogger plugin = ArkhamUUIDLogger.get();

    public static void checkAndCreatePlayer(String accountName, String uuid) {
        try {
            if (!plugin.DB.databaseContainsUUID(uuid)) {
                plugin.DB.createAccountStorage(accountName, uuid);
            }
        } catch (SQLException ex) {
            Logger.getLogger(StorageManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static boolean playerHasChangedName(String playerName, String UUID) {
        try {
            String loggedAccount = plugin.DB.getStoredAccountNameFromUUID(UUID);
            if (loggedAccount == null) {
                return false;
            }

            if (loggedAccount.equalsIgnoreCase(playerName)) {
                return false;
            }
        } catch (SQLException ex) {
        }
        return true;
    }

    public static boolean playerNameHasChangedFromUUID(String UUID, String playerName) {
        try {
            String loggedAccountUUID = plugin.DB.getStoredUUIDFromAccount(playerName);
            if (loggedAccountUUID == null) {
                return false;
            }

            if (loggedAccountUUID.equalsIgnoreCase(UUID)) {
                return false;
            }
        } catch (SQLException ex) {
        }
        return true;
    }
}
