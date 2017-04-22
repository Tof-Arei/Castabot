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
import ch.ar.castabot.env.pc.PseudoCode;
import ch.ar.castabot.env.permissions.Permissions;
import ch.ar.castabot.plugins.PluginSettings;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;
import org.reflections.Reflections;

/**
 * @author Arei
 */
public class Castabot {
    private final Properties config = new Properties();
    private JSONObject settings;
    private Permissions permissions;
    private PluginSettings pluginSettings = new PluginSettings();

    public Castabot() {
         try {
            config.load(new FileInputStream("data/config/config.properties"));
            System.out.println("Démarrage du Castabot™ v"+config.getProperty("bot_version"));
            
            System.out.println("Préchauffage de la machine à café.");
            byte[] rawPerms = Files.readAllBytes(Paths.get("data/config/settings.json"));
            settings = new JSONObject(new String(rawPerms));
            permissions = new Permissions(settings.getJSONObject("permissions"));
            
            System.out.println("Peignage de la moustache.");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Castabot.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Castabot.class.getName()).log(Level.SEVERE, null, ex);
        }
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
    
    public PluginSettings getPluginSettings() {
        return pluginSettings;
    }
}