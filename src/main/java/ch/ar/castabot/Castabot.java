/*
 * Castabot, a Java discord bot (with a mustache!)
 * Copyright 2017 Arei.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.ar.castabot;

import ch.ar.castabot.env.audio.MusicManager;
import ch.ar.castabot.env.audio.PlayerManager;
import ch.ar.castabot.env.permissions.Permissions;
import ch.ar.castabot.plugins.PluginException;
import ch.ar.castabot.plugins.PluginSettings;
import ch.ar.castabot.plugins.cards.Deck;
import ch.ar.castabot.plugins.roll.Rules;
import ch.ar.castabot.plugins.roll.TokenPouch;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 * @todo audio (multi-server stop bug)
 * @todo better command output (embeds)
 * @todo make help command react with permissions
 * @todo still more refactoring: 
 *          - Make plugins and PC modules testable
 *          - Separate JDA, lavaplayer and related tasks from Castabot, Plugins and PC modules.
 *            (Use PluginManager to retrieve needed Discord-related objects with bare Strings as arguments instead of Guild object)
 *          - Leave everything Discord-related related in CastabotClient
 * @author Arei
 */
public class Castabot {
    private final Properties config = new Properties();
    private JSONObject settings;
    private Permissions permissions;
    private volatile Map<String, PluginSettings> hmGuildSettings = new HashMap<>();

    public Castabot() {
         try {
            config.load(new FileInputStream("data/config/config.properties"));
            System.out.println("Démarrage du Castabot™ v"+config.getProperty("bot_version"));
            
            System.out.println("Préchauffage de la machine à café.");
            byte[] rawFile = Files.readAllBytes(Paths.get("data/config/settings.json"));
            settings = new JSONObject(new String(rawFile));
            rawFile = Files.readAllBytes(Paths.get("data/config/permissions.json"));
            permissions = new Permissions(new JSONObject(new String(rawFile)));
            
            System.out.println("Peignage de la moustache.");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Castabot.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Castabot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void initSettings(String guildId) throws PluginException {
        PluginSettings pluginSettings = new PluginSettings();
        Map<String, Object> audioSettings = new HashMap<>();
        PlayerManager playerManager = new PlayerManager(CastabotClient.getGuild(guildId));
        audioSettings.put("musicManager", new MusicManager(playerManager));
        audioSettings.put("playerManager", playerManager);
        pluginSettings.addSetting("audio", audioSettings);
        
        Map<String, Object> cardsSettings = new HashMap<>();
        cardsSettings.put("deck", new Deck("default"));
        pluginSettings.addSetting("cards", cardsSettings);
        
        Map<String, Object> rollSettings = new HashMap<>();
        rollSettings.put("rules", new Rules("default"));
        rollSettings.put("tokenPouch", new TokenPouch());
        pluginSettings.addSetting("roll", rollSettings);
        
        hmGuildSettings.put(guildId, pluginSettings);
    }
    
    public void deleteSettings(String guildId) {
        hmGuildSettings.remove(guildId);
    }
    
    public Properties getConfig() {
        return config;
    }
    
    public JSONObject getSettings() {
        return settings;
    }
    
    public Permissions getPermissions() {
        return permissions;
    }
    
    public PluginSettings getPluginSettings(String guildId) {
        return hmGuildSettings.get(guildId);
    }
    
    /*public PluginSettings getPluginSettings(String guildName) {
        PluginSettings ret = null;
        for (Guild guild : hmGuildSettings.keySet()) {
            if (guild.getName().equals(guildName)) {
                ret = getPluginSettings(guild);
                break;
            }
        }
        return ret;
    }*/
}