/*
 *                GLWT(Good Luck With That) Public License
 *                  Copyright (c) Everyone, except Author
 * 
 * Everyone is permitted to copy, distribute, modify, merge, sell, publish,
 * sublicense or whatever they want with this software but at their OWN RISK.
 * 
 *                             Preamble
 * 
 * The author has absolutely no clue what the code in this project does.
 * It might just work or not, there is no third option.
 * 
 * 
 *                 GOOD LUCK WITH THAT PUBLIC LICENSE
 *    TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION, AND MODIFICATION
 * 
 *   0. You just DO WHATEVER YOU WANT TO as long as you NEVER LEAVE A
 * TRACE TO TRACK THE AUTHOR of the original product to blame for or hold
 * responsible.
 * 
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * 
 * Good luck and Godspeed.
 */
package ch.ar.castabot;

import ch.ar.castabot.env.audio.PlayerManager;
import ch.ar.castabot.plugins.Command;
import ch.ar.castabot.plugins.PluginException;
import ch.ar.castabot.plugins.PluginResponse;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.ChannelType;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.GuildBanEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 *
 * @author Arei
 */
public class CastabotClient extends ListenerAdapter {
    private static JDABuilder jdaBuilder;
    private static JDA jda;
    private static Castabot castabot;
    
    public static void main(String[] args) {
        /*try {    
            castabot = new Castabot();
            jda = new JDABuilder(AccountType.BOT).setToken(castabot.getConfig().getProperty("bot_token")).addEventListener(new CastabotClient()).buildBlocking();
            
            for (Guild guild : jda.getGuilds()) {
                try {
                    castabot.initSettings(guild);
                    registerAudioManager(guild);
                    jda.getPresence().setGame(Game.of(GameType.DEFAULT, "Chat-Bot"));
                } catch (PluginException ex) {
                    Logger.getLogger(CastabotClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            System.out.println("Castabot™ prêt!");
        } catch (LoginException | IllegalArgumentException | InterruptedException ex) {
            Logger.getLogger(Castabot.class.getName()).log(Level.SEVERE, null, ex);
        }*/
        
        try {
            castabot = new Castabot();
            
            jdaBuilder= new JDABuilder(castabot.getConfig().getProperty("bot_token"));
            jdaBuilder.setActivity(Activity.playing("Chat Bot"));
            jda = jdaBuilder.addEventListeners(new CastabotClient()).build();
            jda.awaitReady();
            
            for (Guild guild : jda.getGuilds()) {
                try {
                    castabot.initSettings(guild);
                    registerAudioManager(guild);
                } catch (PluginException ex) {
                    Logger.getLogger(CastabotClient.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
            
            System.out.println("Castabot™ prêt!");
        } catch (LoginException | InterruptedException ex) {
            Logger.getLogger(CastabotClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void handleCommand(Message message) {
        Command command = new Command(message.getGuild().getId(), message.getChannel().getId(), message.getAuthor().getId(), message.getContentDisplay());
        if (command.isWorthAnAnswer()) {
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
            sendMessage(channel, target, message, PM);
            channel.sendFile(file);
            //channel.sendFile(file, message).queue();
        }
    }
    
    private void sendPrivateMessage(User target, final Message message) {
        target.openPrivateChannel().queue((channel) -> {
            channel.sendMessage(message).queue();
        });
    }
    
    private void sendPrivateFile(User target, final File file, final Message message) {
        target.openPrivateChannel().queue((channel) -> {
            channel.sendMessage(message).queue();
            channel.sendFile(file);
            //channel.sendFile(file, message).queue();
        });
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
            System.out.printf("[PM] %s: %s\n", event.getAuthor().getName(), message.getContentDisplay());
        } else {
            // Print channel messages
            System.out.printf("[%s][%s] %s: %s\n", event.getGuild().getName(), event.getTextChannel().getName(), event.getMember().getEffectiveName(), message.getContentDisplay());
            // Handle commands and stuff
            if (message.getContentDisplay().length() > 0) {
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
