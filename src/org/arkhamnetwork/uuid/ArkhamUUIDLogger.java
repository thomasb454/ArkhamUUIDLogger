/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.arkhamnetwork.uuid;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import net.craftminecraft.bungee.bungeeyaml.bukkitapi.file.FileConfiguration;
import net.craftminecraft.bungee.bungeeyaml.bukkitapi.file.YamlConfiguration;
import net.md_5.bungee.api.plugin.Plugin;
import org.arkhamnetwork.uuid.listener.LoginListener;
import org.arkhamnetwork.uuid.sql.Database;
import org.arkhamnetwork.uuid.sql.MySQL;

/**
 *
 * @author devan_000
 */
public class ArkhamUUIDLogger extends Plugin {
    
    public static ArkhamUUIDLogger plugin;
    
    public static ArkhamUUIDLogger get() {
        return plugin;
    }

    public MySQL MYSQL;
    public Connection C = null;
    public Database DB;

    @Override
    public void onEnable() {
        plugin = this;

        log("=[ Plugin version " + getDescription().getVersion() + " starting ]=");

        if (!this.configExists("config.yml")) {
            this.saveResource("config.yml", false);
        }

        if (!setupMySQL()) {
            log("MySQL could not connect. Plugin will not work");
            return;
        }
        
        getProxy().getPluginManager().registerListener(plugin, new LoginListener());
        
        log("=[ Plugin version " + getDescription().getVersion() + " started ]=");
    }

    @Override
    public void onDisable() {
        log("=[ Plugin version " + getDescription().getVersion() + " stopping ]=");

        log("=[ Plugin version " + getDescription().getVersion() + " shutdown ]=");
    }

    private boolean setupMySQL() {
        MYSQL = new MySQL(this.getConfig("config.yml"));
        C = (Connection) MYSQL.open();

        if (MYSQL.shutdown) {
            return false;
        }

        DB = new Database(C, MYSQL);

        try {
            setupSQLTables();
        } catch (IOException | SQLException e) {
            log("Error with SQL connection");
            return false;
        }

        return true;
    }

    private void setupSQLTables() throws IOException, SQLException {
        URL resource = Resources.getResource(ArkhamUUIDLogger.class, "/tables.sql");
        String[] databaseStructure = Resources.toString(resource, Charsets.UTF_8).split(";");

        if (databaseStructure.length == 0) {
            return;
        }
        Statement statement = null;
        try {
            C.setAutoCommit(false);
            statement = C.createStatement();

            for (String query : databaseStructure) {
                query = query.trim();

                if (query.isEmpty()) {
                    continue;
                }

                statement.execute(query);
            }
            C.commit();
        } finally {
            C.setAutoCommit(true);
            if (statement != null && !statement.isClosed()) {
                statement.close();
            }
        }
    }

    public void log(String message) {
        getLogger().info(message);
    }

    public InputStream getResource(String filename) {
        if (filename == null) {
            throw new IllegalArgumentException("Filename cannot be null");
        }

        try {
            URL url = this.getClass().getClassLoader().getResource(filename);

            if (url == null) {
                return null;
            }

            URLConnection connection = url.openConnection();
            connection.setUseCaches(false);
            return connection.getInputStream();
        } catch (IOException ex) {
            return null;
        }
    }
    
    
    public void saveResource(String resourcePath, boolean replace) {
        if (resourcePath == null || resourcePath.equals("")) {
            throw new IllegalArgumentException("ResourcePath cannot be null or empty");
        }

        resourcePath = resourcePath.replace('\\', '/');
        InputStream in = getResource(resourcePath);
        if (in == null) {
            throw new IllegalArgumentException("The embedded resource '" + resourcePath + "' cannot be found");
        }

        File outFile = new File(getDataFolder(), resourcePath);
        int lastIndex = resourcePath.lastIndexOf('/');
        File outDir = new File(getDataFolder(), resourcePath.substring(0, lastIndex >= 0 ? lastIndex : 0));

        if (!outDir.exists()) {
            outDir.mkdirs();
        }

        try {
            if (!outFile.exists() || replace) {
                OutputStream out = new FileOutputStream(outFile);
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                out.close();
                in.close();
            } else {
            }
        } catch (IOException ex) {
        }
    }
    
    public FileConfiguration getConfig(String configName) {
        return YamlConfiguration.loadConfiguration(new File(getDataFolder() + File.separator + configName));
    }

    public void saveConfig(String configName, FileConfiguration config) {
        try {
            config.save(new File(getDataFolder() + File.separator + configName));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public boolean configExists(String configName) {
        return new File(getDataFolder() + File.separator + configName).exists();
    }
}
