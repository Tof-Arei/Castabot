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
package ch.ar.castabot.client.audio;

import ch.ar.castabot.client.CastabotClient;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.managers.AudioManager;


/**
 *
 * @author Arei
 */
public class PlayerManager extends DefaultAudioPlayerManager {
    private final Guild guild;
    
    public PlayerManager(Guild guild) {
        this.guild = guild;
    }
    
    public void loadAndPlay(String guildId, String channelId, String trackUrl) {
        MusicManager musicManager = (MusicManager) CastabotClient.getMusicManager(guild.getId());
        LoadResultHandler lrHandler = new LoadResultHandler(CastabotClient.getTextChannel(guildId, channelId), trackUrl);
        loadItemOrdered(musicManager, trackUrl, lrHandler);
    }
    
    public String playlistLoaded(AudioPlaylist playlist) {
        AudioTrack firstTrack = playlist.getSelectedTrack();
        if (firstTrack == null) {
            firstTrack = playlist.getTracks().get(0);
        }

        return "Ajout à la file: " + firstTrack.getInfo().title + " (Premier morceau de la playliste: " + playlist.getName() + ")";
    }
    
    public String play(MusicManager musicManager, AudioTrack track) {
        connectToFirstVoiceChannel(guild.getAudioManager());
        musicManager.getScheduler().queue(track);
        return "Chargement de la piste: " + track.getInfo().title;
    }

    public String pause() {
        MusicManager musicManager = (MusicManager) CastabotClient.getMusicManager(guild.getId());
        musicManager.getPlayer().setPaused(true);
        return "Piste suspendue.";
    }
    
    public String resume() {
        MusicManager musicManager = (MusicManager) CastabotClient.getMusicManager(guild.getId());
        musicManager.getPlayer().setPaused(false);
        return "Piste rétablie.";
    }
    
    public String loop() {
        MusicManager musicManager = (MusicManager) CastabotClient.getMusicManager(guild.getId());
        musicManager.getScheduler().loopTrack();
        return "Piste mise en boucle.";
    }
    
    public String skip() {
        MusicManager musicManager = (MusicManager) CastabotClient.getMusicManager(guild.getId());
        musicManager.getScheduler().nextTrack();
        AudioTrack track = musicManager.getPlayer().getPlayingTrack();
        if (track != null) {
            return "Passage à la piste suivante: " + track.getInfo().title;
        } else {
            return "Plus de pistes en mémoire.";
        }
    }
    
    public String stop() {
        skip();
        disconnectFromVoiceChannel(guild.getAudioManager());
        return "Arrêt du lecteur.";
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
}
