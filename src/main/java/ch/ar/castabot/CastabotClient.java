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

import ch.ar.castabot.env.audio.PlayerManager;
import ch.ar.castabot.plugins.Command;
import ch.ar.castabot.plugins.PluginException;
import ch.ar.castabot.plugins.PluginResponse;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.MessageBuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.GuildJoinEvent;
import net.dv8tion.jda.core.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 *
 * @author Arei
 */
public class CastabotClient extends ListenerAdapter {
    private static JDA jda;
    private static Castabot castabot;
    
    public static void main(String[] args) {
        try {    
            castabot = new Castabot();
            jda = new JDABuilder(AccountType.BOT).setToken(castabot.getConfig().getProperty("bot_token")).addEventListener(new CastabotClient()).buildBlocking();
            
            for (Guild guild : jda.getGuilds()) {
                try {
                    castabot.initSettings(guild);
                    registerAudioManager(guild);
                } catch (PluginException ex) {
                    Logger.getLogger(CastabotClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            System.out.println("Castabot™ prêt!");
        } catch (LoginException | IllegalArgumentException | InterruptedException | RateLimitedException ex) {
            Logger.getLogger(Castabot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void handleCommand(Message message) {
        Command command = new Command(message.getGuild().getId(), message.getChannel().getId(), message.getAuthor().getId(), message.getContent());
        if (command.isWorthAnswer()) {
            for (PluginResponse response : command.execute()) {
                // Handle message type
                switch (command.getType()) {
                    case Command.TYPE_NORMAL:
                        // Send in channel
                        sendResponseMessage(message.getTextChannel(), message.getAuthor(), response, false);
                        break;
                    case Command.TYPE_SECRET:
                        // Send PM
                        sendResponseMessage(message.getTextChannel(), message.getAuthor(), response, true);
                        break;
                    case Command.TYPE_BOTH:
                        // Send both in channel and PM
                        sendResponseMessage(message.getTextChannel(), message.getAuthor(), response, false);
                        sendResponseMessage(message.getTextChannel(), message.getAuthor(), response, true);
                        break;
                }
            }
        }
    }
    
    private void sendResponseMessage(TextChannel channel, User target, PluginResponse response, boolean PM) {
        MessageBuilder msgBuild = new MessageBuilder();
        msgBuild.append("<@"+target.getId()+"> "+response.printText());
        msgBuild.setEmbed(response.getEmbed());
        if (response.getFile() != null) {
            sendFile(channel, target, response.getFile(), msgBuild.build(), PM);
        } else {
            sendMessage(channel, target, msgBuild.build(), PM);
        }
    }
    
    private void sendMessage(TextChannel channel, User target, final Message message, boolean PM) {
        if (PM) {
            sendPrivateMessage(target, message);
        } else {
            channel.sendMessage(message).queue();
        }
    }
    
    private void sendFile(TextChannel channel, User target, final File file, final Message message, boolean PM) {
        if (PM) {
            sendPrivateFile(target, file, message);
        } else {
            try {
                channel.sendFile(file, message).queue();
            } catch (IOException ex) {
                Logger.getLogger(CastabotClient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    private void sendPrivateMessage(User target, final Message message) {
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
    
    public static Guild getGuild(String guildId) {
        Guild ret = null;
        for (Guild guild : jda.getGuilds()) {
            if (guild.getId().equals(guildId)) {
                ret = guild;
                break;
            }
        }
        return ret;
    }
    
    public static TextChannel getTextChannel(String guildId, String channelId) {
        TextChannel ret = null;
        for (TextChannel channel : getGuild(guildId).getTextChannels()) {
            if (channel.getId().equals(channelId)) {
                ret = channel;
                break;
            }
        }
        return ret;
    }
    
    public static Member getMember(String guildId, String userId) {
        Member ret = null;
        for (Member member : getGuild(guildId).getMembers()) {
            if (member.getUser().getId().equals(userId)) {
                ret = member;
                break;
            }
        }
        return ret;
    }
    
    public static int getAvailablePlayers(String guildId, String roleName) {
        int ret = 0;
        for (VoiceChannel voiceChannel : getGuild(guildId).getVoiceChannels()) {
            for (Member member : voiceChannel.getMembers()) {
                if (!member.getUser().isBot() && hasRole(member, roleName)) {
                    ret++;
                }
            }
        }
        return ret;
    }
    
    private static boolean hasRole(Member member, String roleName) {
        boolean ret = false;
        for (Role iRole : member.getRoles()) {
            if (roleName.equals(iRole.getName())) {
                ret = true;
                break;
            }
        }
        return ret;
    }
    
    public synchronized static void registerAudioManager(Guild guild) {
        PlayerManager playerManager = (PlayerManager) castabot.getPluginSettings(guild.getId()).getValue("audio", "playerManager");
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }
    
    @Override
    public void onGuildJoin(GuildJoinEvent event) {
        try {
            castabot.initSettings(event.getGuild());
        } catch (PluginException ex) {
            Logger.getLogger(CastabotClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void onGuildLeave(GuildLeaveEvent event) {
        castabot.deleteSettings(event.getGuild().getId());
    }
    
    @Override
    public void onGuildBan(GuildBanEvent event) {
        castabot.deleteSettings(event.getGuild().getId());
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        
        if (event.isFromType(ChannelType.PRIVATE)) {
            // Print bot PMs
            System.out.printf("[PM] %s: %s\n", event.getAuthor().getName(), event.getMessage().getContent());
        } else {
            // Print channel messages
            System.out.printf("[%s][%s] %s: %s\n", event.getGuild().getName(), event.getTextChannel().getName(), event.getMember().getEffectiveName(), event.getMessage().getContent());
            // Handle commands and stuff
            if (message.getContent().length() > 0) {
                handleCommand(message);
            }
        }
    }

    public static JDA getJda() {
        return jda;
    }
    
    public static Castabot getCastabot() {
        return castabot;
    }
}
