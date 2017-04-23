/*
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
import ch.ar.castabot.plugins.Command;
import ch.ar.castabot.plugins.PluginResponse;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Arei
 */
public class CastabotClient extends ListenerAdapter {
    private static Castabot castabot;
    
    private CastabotClient() {}
    
    public static void main(String[] args) {
        try {    
            castabot = new Castabot();
 
            JDA jda = new JDABuilder(AccountType.BOT).setToken(castabot.getConfig().getProperty("bot_token")).buildBlocking();
            jda.addEventListener(new CastabotClient());
            
            PlayerManager playerManager = (PlayerManager) castabot.getPluginSettings().getValue("audio", "playerManager");
            AudioSourceManagers.registerRemoteSources(playerManager);
            AudioSourceManagers.registerLocalSource(playerManager);
            System.out.println("Castabot™ prêt!");
        } catch (LoginException | IllegalArgumentException | InterruptedException | RateLimitedException ex) {
            Logger.getLogger(Castabot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    // Check if the message is worth answering to (ie. message is a command)
    private boolean isMessageWorthAnsweringTo(Message message) {
        boolean ret = false;
        if(!message.getChannel().getName().equals("general")) {
            if (message.getAttachments().isEmpty()) {
                JSONArray cmdChars = castabot.getSettings().getJSONArray("cmd_chars");
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
    
    private void handleCommand(Message message) {
        Command command = new Command(message.getGuild(), message.getAuthor(), message);
        for (PluginResponse response : command.execute()) {
            if (command.isSecret() || message.getPrivateChannel() != null) {
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
    
    public synchronized static MusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        Map<Long, MusicManager> musicManagers = (Map<Long, MusicManager>) castabot.getPluginSettings().getValue("audio", "musicManagers");
        AudioPlayerManager playerManager = (AudioPlayerManager) castabot.getPluginSettings().getValue("audio", "playerManager");
        MusicManager musicManager = musicManagers.get(guildId);
        
        if (musicManager == null) {
            musicManager = new MusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }
        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        
        if (event.isFromType(ChannelType.PRIVATE)) {
            // Print bot PMs
            System.out.printf("[PM] %s: %s\n", event.getAuthor().getName(), event.getMessage().getContent());
            // Handle commands and stuff
            if (isMessageWorthAnsweringTo(message)) {
                handleCommand(message);
            }
        } else {
            // Print channel messages
            System.out.printf("[%s][%s] %s: %s\n", event.getGuild().getName(), event.getTextChannel().getName(), event.getMember().getEffectiveName(), event.getMessage().getContent());
            // Handle commands and stuff
            if (isMessageWorthAnsweringTo(message)) {
                handleCommand(message);
            }
        }
    }
    
    public static Castabot getCastabot() {
        return castabot;
    }
}
