/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.arkhamnetwork.uuid.listener;

import java.sql.SQLException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import org.arkhamnetwork.uuid.ArkhamUUIDLogger;
import org.arkhamnetwork.uuid.managers.StorageManager;

/**
 *
 * @author devan_000
 */
public class LoginListener implements Listener {

    static ArkhamUUIDLogger plugin = ArkhamUUIDLogger.get();

    @EventHandler
    public void onLogin(LoginEvent event) {
        String playerName = event.getConnection().getName();
        String UUID = event.getConnection().getUUID();
        try {
            if (playerName == null || UUID == null || plugin.DB == null || plugin.C == null || plugin.C.isClosed()) {
                event.setCancelReason(ChatColor.RED + "We could not validate your UUID with our server.");
                event.setCancelled(true);
                return;
            }

            StorageManager.checkAndCreatePlayer(playerName, UUID);

            if (StorageManager.playerHasChangedName(playerName, UUID)) {
                String oldAccountName = plugin.DB.getStoredAccountNameFromUUID(UUID);
                event.setCancelReason(ChatColor.AQUA + "It appears as if you have changed your account name. Please change your account name back to " + ChatColor.GOLD + oldAccountName + ChatColor.AQUA + " or we cannot let you login to arkhamnetwork. We do not support account name changing yet.");
                event.setCancelled(true);
            }
        } catch (SQLException ex) {
            event.setCancelReason(ChatColor.RED + "We could not validate your UUID with our server.");
            event.setCancelled(true);
        }
    }

}
