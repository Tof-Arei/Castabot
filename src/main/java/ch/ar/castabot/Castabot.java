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
import net.dv8tion.jda.core.entities.Guild;
import org.json.JSONObject;

/**
 * @todo even more refactoring : message/privateMessage handling/sending
 * @todo even more settings.json refactoring (merge command with command_shorts)
 * @todo make help command react with permissions
 * @author Arei
 */
public class Castabot {
    private final Properties config = new Properties();
    private JSONObject settings;
    private Permissions permissions;
    private Map<Guild, PluginSettings> hmGuildSettings = new HashMap<>();

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
    
    public void initSettings(Guild guild) throws PluginException {
        PluginSettings pluginSettings = new PluginSettings();
        Map<String, Object> audioSettings = new HashMap<>();
        audioSettings.put("musicManagers", new HashMap<>());
        audioSettings.put("playerManager", new PlayerManager());
        pluginSettings.addSetting("audio", audioSettings);
        
        Map<String, Object> cardsSettings = new HashMap<>();
        cardsSettings.put("deck", new Deck("default"));
        pluginSettings.addSetting("cards", cardsSettings);
        
        Map<String, Object> rollSettings = new HashMap<>();
        rollSettings.put("rules", new Rules("default"));
        rollSettings.put("tokenPouch", new TokenPouch());
        pluginSettings.addSetting("roll", rollSettings);
        
        hmGuildSettings.put(guild, pluginSettings);
        CastabotClient.registerAudioManager(guild);
    }
    
    public void deleteSettings(Guild guild) {
        hmGuildSettings.remove(guild);
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
    
    public PluginSettings getPluginSettings(Guild guild) {
        return hmGuildSettings.get(guild);
    }
    
    public PluginSettings getPluginSettings(String guildName) {
        PluginSettings ret = null;
        for (Guild guild : hmGuildSettings.keySet()) {
            if (guild.getName().equals(guildName)) {
                ret = getPluginSettings(guild);
                break;
            }
        }
        return ret;
    }
}