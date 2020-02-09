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
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 *
 * @author Arei
 */
public class LoadResultHandler implements AudioLoadResultHandler {
    private final PlayerManager playerManager;
    private final TextChannel channel;
    private final MusicManager musicManager;
    private final String trackUrl;
    private String message;

    public LoadResultHandler(String guildId, String channelId, String trackUrl) {
        this.playerManager = (PlayerManager) CastabotClient.getPlayerManager(guildId);
        this.channel = CastabotClient.getTextChannel(guildId, channelId);
        this.musicManager = (MusicManager) CastabotClient.getMusicManager(guildId);
        this.trackUrl = trackUrl;
    }
    
    @Override
    public void trackLoaded(AudioTrack track) {
        //channel.sendMessage(playerManager.play(musicManager, track)).queue();
        message = playerManager.play(musicManager, track);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        playerManager.playlistLoaded(playlist);
    }

    @Override
    public void noMatches() {
        //channel.sendMessage("Morceau introuvable: " + trackUrl).queue();
        message = "Morceau introuvable: " + trackUrl;
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        //channel.sendMessage("Impossible de lire: " + exception.getMessage()).queue();
        message = "Impossible de lire: " + exception.getMessage();
    }
    
    public String getMessage() {
        return message;
    }
}
