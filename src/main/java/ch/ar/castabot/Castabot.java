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

import ch.ar.castabot.audio.MusicManager;
import ch.ar.castabot.audio.LoadResultHandler;
import ch.ar.castabot.plugins.Plugin;
import ch.ar.castabot.plugins.PluginException;
import ch.ar.castabot.plugins.PluginResponse;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
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
import net.dv8tion.jda.core.entities.impl.MemberImpl;
import net.dv8tion.jda.core.entities.impl.MessageImpl;
import net.dv8tion.jda.core.managers.AudioManager;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * @todo better entropy options; choose between pseudo-random and true-random
 * @todo better permissions (add permissions by group/user to global command permissions)
 * @author Arei
 */
public class Castabot {
    private static final Properties config = new Properties();
    private static JSONObject settings;
    private static JSONObject permissions;
    
    private AudioPlayerManager playerManager;
    private Map<Long, MusicManager> musicManagers;

    public Castabot() {
         try {
            config.load(new FileInputStream("data/config/config.properties"));
            System.out.println("Démarrage du Castabot™ v"+config.getProperty("bot_version"));
            
            System.out.println("Préchauffage de la machine à café.");
            byte[] rawPerms = Files.readAllBytes(Paths.get("data/config/settings.json"));
            settings = new JSONObject(new String(rawPerms));
            permissions = settings.getJSONObject("permissions");
            
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

    /*
    * Check if the message is worth answering to (ie. message is a command)
    */
    public boolean isMessageWorthAnsweringTo(Message message) {
        boolean ret = false;
        JSONArray cmdChars = settings.getJSONArray("cmd_chars");
        for (int i = 0; i < cmdChars.length(); i++) {
            JSONArray cmdChar = cmdChars.getJSONArray(i);
            if (message.getContent().substring(0, 1).equals(cmdChar.get(1))) {
                ret = true;
                break;
            } 
        }
        return ret;
    }
    
    /*
    * Parse the message, extract the command and arguments or return an error
    */
    public void parseCommand(Message message) {
        String strChar = message.getContent().substring(0, 1);
        String command = null;
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
                
                args = new String[matShort.groupCount()];
                for (int j = 0; j < matShort.groupCount(); j++) {
                    args[j] = matShort.group(j+1);
                }
                
                break;
            }
        }
        
        if (!isShort) {
            // Extract command and arguments
            String[] arrCmd = message.getContent().split(" ");
            if (arrCmd.length > 0) {
                command = arrCmd[0].substring(1);
            }
            if (arrCmd.length > 1) {
                args = new String[arrCmd.length - 1];
                System.arraycopy(arrCmd, 1, args, 0, args.length);
            }
        } 
        
        PluginResponse response = null;
        // Check if the command exists
        JSONObject permCommands = settings.getJSONObject("commands");
        if (permCommands.has(command)) {
            // Check if the user is allowed to use the command
            if (checkPermission(message.getAuthor(), command, args)) {
                response = executeCommand(command, args, message.getTextChannel(), message.getAuthor());
            } else {
                sendPrivateMessage(message.getAuthor(), "<@"+message.getAuthor().getId()+"> Vous ne pouvez pas utiliser la commande ["+command+"].");
                return;
            }
        } else {
            sendPrivateMessage(message.getAuthor(), "<@"+message.getAuthor().getId()+"> La commande ["+command+"] n'éxiste pas. Commande [help] disponible.");
            return;
        }
        
        if (response != null) {
            if (isSecret || message.getPrivateChannel() != null) {
                if (response.getFile() != null) {
                    Message msg= null;
                    if (response.getText() != null) {
                        MessageBuilder msgBuild = new MessageBuilder();
                        msgBuild.append("<@"+message.getAuthor().getId()+"> "+response.getText());
                        msg = msgBuild.build();
                    }
                    sendPrivateFile(message.getAuthor(), response.getFile(), msg);
                } else {
                    sendPrivateMessage(message.getAuthor(), "<@"+message.getAuthor().getId()+"> "+response.getText());
                }
            } else {
                if (response.getFile() != null) {
                    Message msg= null;
                    if (response.getText() != null) {
                        MessageBuilder msgBuild = new MessageBuilder();
                        msgBuild.append("<@"+message.getAuthor().getId()+"> "+response.getText());
                        msg = msgBuild.build();
                    }
                    try {
                        message.getChannel().sendFile(response.getFile(), msg).queue();
                    } catch (IOException ex) {
                        Logger.getLogger(Castabot.class.getName()).log(Level.SEVERE, null, ex);
                    }
                } else {
                    message.getChannel().sendMessage("<@"+message.getAuthor().getId()+"> "+response.getText()).queue();
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
    
    /*
    * Check the user/group permissions before allowing command execution
    */
    private boolean checkPermission(User user, String command, String[] args) {
        boolean ret = false;
        // Check global permissions implement group/user permissions
        JSONObject permGlobal = permissions.getJSONObject("global");
        if (permGlobal.has(command)) {
            ret = permGlobal.getBoolean(command);
        }
        return ret;
    }
    
    /*private boolean checkPermission(Message message, String command, String[] args) {
        // Check groupPermissions
        boolean ret = checkGroupPermission(message, command, args);
        // Check individual permissions (Overrides group permissions)
        JSONObject userPermission = getUserPermission(message.getAuthor(), command, args);
        if (userPermission != null) {
            ret = checkCommandPermission(userPermission, command, args);
        }
        return ret;
    }
    
    private boolean checkGroupPermission(Message message, String command, String[] args) { 
        boolean ret = false;  
        JSONObject rolesObj = permissions.getJSONObject("roles");
        Member member = new MemberImpl(message.getGuild(), message.getAuthor());
        boolean[] rolePermissions = new boolean[member.getRoles().size()];
        for (Role role : member.getRoles()) {
            if (rolesObj.has(role.getName())) {
                JSONObject permRole = rolesObj.getJSONObject(role.getName());
                rolePermissions[role.getPosition()] = checkCommandPermission(permRole, command, args);
            } else {
                rolePermissions[role.getPosition()] = false;
            }
        }
        for (int i = 0; i < rolePermissions.length; i++) {
            if (rolePermissions[i]) {
                ret = true;
                break;
            }
        }
        
        return ret;
    }
    
    private JSONObject getUserPermission(User user, String command, String[] args) {
        JSONObject ret = null;
        JSONObject usersObj = permissions.getJSONObject("users");
        if (usersObj.has(user.getId())) {
            JSONObject userObj = usersObj.getJSONObject(user.getId());
            if (userObj.has(command)) {
                ret = userObj.getJSONObject(command);
            }
        }
        return ret;
    }
    
    private boolean checkCommandPermission(JSONObject source, String command, String[] args) {
        boolean ret = false;
        if (source.has(command)) {
            JSONObject permCommand = source.getJSONObject(command);
            ret = permCommand.getBoolean(args[0]);
        }
        return ret;
    }*/
    
    /*
    * Actual command execution.
    */
    private PluginResponse executeCommand(String command, String[] args, TextChannel source, User user) {
        PluginResponse ret = null;
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
                    ret = new PluginResponse(permCommands.getString(command));
                }
            }
            if (ret == null) {
                ret = instance.run();
                if (ret == null) {
                    throw new PluginException("PLG-?", "Erreur durant l'exécution de la commande ["+command+"].");
                }
            }
        } catch (PluginException e) {
            ret = new PluginResponse(e.getMessage());
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
    
    /*
    * Getters and setters
    */
    public Properties getConfig() {
        return config;
    }
    
    public JSONObject getPermissions() {
        return settings;
    }
    
    public AudioPlayerManager getPlayerManager() {
        return playerManager;
    }
}