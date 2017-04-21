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
import ch.ar.castabot.env.audio.LoadResultHandler;
import ch.ar.castabot.env.pc.PseudoCode;
import ch.ar.castabot.env.permissions.CommandPermission;
import ch.ar.castabot.env.permissions.Permissions;
import ch.ar.castabot.env.permissions.RolePermission;
import ch.ar.castabot.env.permissions.UserPermission;
import ch.ar.castabot.plugins.Plugin;
import ch.ar.castabot.plugins.PluginException;
import ch.ar.castabot.plugins.PluginResponse;
import ch.ar.castabot.plugins.cards.Deck;
import ch.ar.castabot.plugins.roll.Rules;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.jar.JarEntry;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @todo replace cards.json file Using PluginSettings object
 * @todo actual permissions (add permissions by group/user to global command permissions)
 * @author Arei
 */
public class Castabot {
    private static final Properties config = new Properties();
    private static JSONObject settings;
    //private static JSONObject permissions;
    private static Permissions permissions;
    
    private AudioPlayerManager playerManager;
    private Map<Long, MusicManager> musicManagers;
    
    private PluginSettings pluginSetting = new PluginSettings();

    public Castabot() {
         try {
            config.load(new FileInputStream("data/config/config.properties"));
            System.out.println("Démarrage du Castabot™ v"+config.getProperty("bot_version"));
            
            System.out.println("Préchauffage de la machine à café.");
            byte[] rawPerms = Files.readAllBytes(Paths.get("data/config/settings.json"));
            settings = new JSONObject(new String(rawPerms));
            //permissions = settings.getJSONObject("permissions");
            initPermissions();
            
            System.out.println("Peignage de la moustache.");
            musicManagers = new HashMap<>();
            playerManager = new DefaultAudioPlayerManager();
            AudioSourceManagers.registerRemoteSources(playerManager);
            AudioSourceManagers.registerLocalSource(playerManager);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Castabot.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Castabot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void initPermissions() {
        JSONObject objPermissions = settings.getJSONObject("permissions");
        
        List<UserPermission> lstPermission = new ArrayList<>();
        if (objPermissions.has("roles")) {
            JSONObject objRolePermissions = objPermissions.getJSONObject("roles");
            for (String roleKey : objRolePermissions.keySet()) {
                JSONObject objRolePermission = objRolePermissions.getJSONObject(roleKey);
                RolePermission rolePermission = new RolePermission(roleKey, objRolePermission);
                lstPermission.add(rolePermission);
            }
        }
        
        if (objPermissions.has("users")) {
            JSONObject objUserPermissions = objPermissions.getJSONObject("users");
            for (String userKey : objUserPermissions.keySet()) {
                JSONObject objUserPermission = objUserPermissions.getJSONObject(userKey);
                UserPermission userPermission = new UserPermission(userKey, objUserPermission);
                lstPermission.add(userPermission);
            }
        }
        
        permissions = new Permissions(lstPermission);
    }

    // Check if the message is worth answering to (ie. message is a command)
    public boolean isMessageWorthAnsweringTo(Message message) {
        boolean ret = false;
        if(!message.getChannel().getName().equals("general")) {
            if (message.getAttachments().isEmpty()) {
                JSONArray cmdChars = settings.getJSONArray("cmd_chars");
                for (int i = 0; i < cmdChars.length(); i++) {
                    JSONArray cmdChar = cmdChars.getJSONArray(i);
                    if (message.getContent().substring(0, 1).equals(cmdChar.get(1))) {
                        ret = true;
                        break;
                    } 
                }
            }
        }
        
        return ret;
    }
    
    // Parse the message, extract the command and arguments or return an error
    public void parseCommand(Message message) {
        String strChar = message.getContent().substring(0, 1);
        String command = null;
        String[] strArgs = message.getContent().substring(1).split(" ");
        String[] args = null;
        boolean isSecret = false;
        
        // Check if the command is secret
        JSONArray cmdChars = settings.getJSONArray("cmd_chars");
        for (int i = 0; i < cmdChars.length(); i++) {
            JSONArray cmdChar = cmdChars.getJSONArray(i);
            if (strChar.equals(cmdChar.getString(1))) {
                if (cmdChar.getString(0).equals("secret")) {
                    isSecret = true;
                    break;
                }
            }
        }
        
        // Check if the command is a short
        boolean isShort = false;
        JSONArray cmdShorts = settings.getJSONArray("cmd_shorts");
        for (int i = 0; i < cmdShorts.length(); i++) {
            JSONArray cmdShort = cmdShorts.getJSONArray(i);
            String shortCommand = message.getContent().substring(1);
            Pattern patShort = Pattern.compile(cmdShort.getString(1));
            Matcher matShort = patShort.matcher(shortCommand);
            
            if (matShort.find()) {
                isShort = true;
                command = cmdShort.getString(0);
                
                args = new String[strArgs.length];
                for (int j = 0; j < strArgs.length; j++) {
                    args[j] = strArgs[j];
                }
                
                break;
            }
        }
        
        if (!isShort) {
            // Extract command and arguments
            command = strArgs[0];
            args = new String[strArgs.length-1];
            for (int j = 1; j < strArgs.length; j++) {
                args[j-1] = strArgs[j];
            }
        } 
        
        List<PluginResponse> lstResponse = new ArrayList<>();
        // Check if the command exists
        JSONObject permCommands = settings.getJSONObject("commands");
        if (permCommands.has(command)) {
            // Check if the user is allowed to use the command
            if (checkPermission(message.getGuild(), message.getAuthor(), command, args)) {
                lstResponse = executeCommand(command, args, message.getTextChannel(), message.getAuthor());
            } else {
                //sendPrivateMessage(message.getAuthor(), "<@"+message.getAuthor().getId()+"> Vous ne pouvez pas utiliser la commande ["+command+"].");
                return;
            }
        } else {
            //sendPrivateMessage(message.getAuthor(), "<@"+message.getAuthor().getId()+"> La commande ["+command+"] n'éxiste pas. Commande [help] disponible.");
            return;
        }
        
        if (!lstResponse.isEmpty()) {
            for (PluginResponse response : lstResponse) {
                if (isSecret || message.getPrivateChannel() != null) {
                    if (response.getFile() != null) {
                        Message msg= null;
                        if (response.getText() != null) {
                            MessageBuilder msgBuild = new MessageBuilder();
                            msgBuild.append("<@"+response.getTarget().getId()+"> "+response.getText());
                            msg = msgBuild.build();
                        }
                        sendPrivateFile(response.getTarget(), response.getFile(), msg);
                    } else {
                        sendPrivateMessage(response.getTarget(), "<@"+response.getTarget().getId()+"> "+response.getText());
                    }
                } else {
                    if (response.getFile() != null) {
                        Message msg= null;
                        if (response.getText() != null) {
                            MessageBuilder msgBuild = new MessageBuilder();
                            msgBuild.append("<@"+response.getTarget().getId()+"> "+response.getText());
                            msg = msgBuild.build();
                        }
                        try {
                            message.getChannel().sendFile(response.getFile(), msg).queue();
                        } catch (IOException ex) {
                            Logger.getLogger(Castabot.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        message.getChannel().sendMessage("<@"+response.getTarget().getId()+"> "+response.getText()).queue();
                    }
                }
            }
        }
    }
    
    private void sendPrivateMessage(User target, final String message) {
        if (!target.hasPrivateChannel()) {
            target.openPrivateChannel().queue(
                success -> {
                    target.getPrivateChannel().sendMessage(message).queue();
                }
            );
        } else {
            target.getPrivateChannel().sendMessage(message).queue();
        }
    }
    
    private void sendPrivateFile(User target, final File file, final Message message) {
        if (!target.hasPrivateChannel()) {
            target.openPrivateChannel().queue(
                success -> {
                    try {
                        target.getPrivateChannel().sendFile(file, message).queue();
                    } catch (IOException ex) {
                        Logger.getLogger(Castabot.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            );
        } else {
            try {
                target.getPrivateChannel().sendFile(file, message).queue();
            } catch (IOException ex) {
                Logger.getLogger(Castabot.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private boolean checkPermission(Guild guild, User user, String command, String[] args) {
        // Check for user specific permissions
        CommandPermission retCommandPermission = null;
        for (UserPermission userPermission : permissions.getLstSpecificPermission(UserPermission.TYPE_USER)) {
            if (userPermission.getTarget().equals(user.getId())) {
                retCommandPermission = userPermission.getCommandPermission(command);
                break;
            }
        }
        if (retCommandPermission != null) {
            return retCommandPermission.getArgPermission(args[0]);
        }
        
        // Nothing found, check for role specific permissions
        int priority = 0;
        for (Role role : guild.getMember(user).getRoles()) {
            RolePermission rolePermission = (RolePermission) permissions.getPermission(UserPermission.TYPE_ROLE, role.getName());
            if (rolePermission != null) {
                if (role.getName().equals(rolePermission.getTarget())) {
                    if (rolePermission.getPriority() > priority) {
                        priority = rolePermission.getPriority();
                        retCommandPermission = rolePermission.getCommandPermission(command);
                    }
                }
            }
        }
        if (retCommandPermission != null) {
            return retCommandPermission.getArgPermission(args[0]);
        }
        
        // Still nothing found, then it must be false
        return false;
    }
    
    // Actual command execution
    private List<PluginResponse> executeCommand(String command, String[] args, TextChannel source, User user) {
        List<PluginResponse> ret = new ArrayList<>();
        // Heres comes the ugly bit
        try {
            if (args == null) {
                args = new String[]{};
            }    
            
            String className = command.substring(0, 1).toUpperCase() + command.substring(1);
            Class<?> clazz = Class.forName("ch.ar.castabot.plugins."+command+"."+className);
            Class[] types = {String[].class, TextChannel.class, User.class};
            Constructor<?> constructor = clazz.getConstructor(types);
            Object[] classArgs = {args, source, user};
            Plugin instance = (Plugin) constructor.newInstance(classArgs);
            
            if (args.length > 0) {
                if (args[0].equals("-h") || args[0].equals("--help")) {
                    JSONObject permCommands = settings.getJSONObject("commands");
                    Rules rules = new Rules((String) CastabotClient.getCastabot().getPluginSettings().getValue("roll", "rules"));
                    PseudoCode pc = new PseudoCode(permCommands.getString(command));
                    pc.addObject(0, rules);
                    ret.add(new PluginResponse(pc.evaluate(), user));
                }
            }
            if (ret.isEmpty()) {
                ret = instance.run();
                if (ret == null) {
                    throw new PluginException("PLG-?", "Erreur durant l'exécution de la commande ["+command+"].");
                }
            }
        } catch (PluginException e) {
            ret.add(new PluginResponse(e.getMessage(), user));
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Castabot.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return ret;
    }
    
    private synchronized MusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        MusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new MusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }
    
    public void loadAndPlay(TextChannel channel, String trackUrl) {
        MusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        playerManager.loadItemOrdered(musicManager, trackUrl, new LoadResultHandler(channel, musicManager, trackUrl));
    }
    
    public void play(Guild guild, MusicManager musicManager, AudioTrack track) {
        connectToFirstVoiceChannel(guild.getAudioManager());
        musicManager.scheduler.queue(track);
    }

    public void pause(TextChannel channel) {
        MusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.player.setPaused(true);
    }
    
    public void resume(TextChannel channel) {
        MusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.player.setPaused(false);
    }
    
    public void loop(TextChannel channel) {
        MusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.loopTrack();
    }
    
    public void skipTrack(TextChannel channel) {
        MusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.nextTrack();
        
        channel.sendMessage("Passage à la piste suivante.").queue();
    }
    
    public void stop(TextChannel channel) {
        disconnectFromVoiceChannel(channel.getGuild().getAudioManager());
    }

    private static void connectToFirstVoiceChannel(AudioManager audioManager) {
        if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
            for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
                audioManager.openAudioConnection(voiceChannel);
                break;
            }
        }
    }
    
    private static void disconnectFromVoiceChannel(AudioManager audioManager) {
        if (audioManager.isConnected() || audioManager.isAttemptingToConnect()) {
            audioManager.closeAudioConnection();
        }
    }
    
    public Properties getConfig() {
        return config;
    }
    
    public JSONObject getPermissions() {
        return settings;
    }
    
    public AudioPlayerManager getPlayerManager() {
        return playerManager;
    }
    
    public PluginSettings getPluginSettings() {
        return pluginSetting;
    }
    
    public class PluginSettings {
        private final Map<String, Map<String, Object>> lstSetting = new HashMap<>();
        
        public PluginSettings() {
            try {
                initSettings();
            } catch (ClassNotFoundException | PluginException ex) {
                Logger.getLogger(Castabot.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        private void initSettings() throws ClassNotFoundException, PluginException {
            Map<String, Object> cardsSetting = new HashMap<>();
            cardsSetting.put("deck", new Deck("default"));
            lstSetting.put("cards", cardsSetting);
            
            Map<String, Object> rollSetting = new HashMap<>();
            rollSetting.put("rules", "default");
            lstSetting.put("roll", rollSetting);
        }
        
        public void addValue(String plugin, String setting, Object value) {
            lstSetting.get(plugin).put(setting, value);
        }
        
        public void setValue(String plugin, String setting, Object value) {
            lstSetting.get(plugin).replace(setting, value);
        }
        
        public Object getValue(String plugin, String setting) {
            return lstSetting.get(plugin).get(setting);
        }
    }
}