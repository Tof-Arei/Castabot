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
package ch.ar.castabot.plugins.admin;

import ch.ar.castabot.client.CastabotClient;
import ch.ar.castabot.plugins.Plugin;
import ch.ar.castabot.plugins.PluginException;
import ch.ar.castabot.client.plugins.PluginResponse;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author christophe
 */
public class Admin extends Plugin {
    public Admin(String[] args, String guildId, String channelId, String userId) {
        super(args, guildId, channelId, userId);
    }
    
    @Override
    public List<PluginResponse> run() throws PluginException {
        List<PluginResponse> ret = new ArrayList<>();
        
        switch (args[0]) {
            case "reload":
                CastabotClient.getCastabot().initSettings(CastabotClient.getGuild(guildId).getId(), CastabotClient.getGuild(guildId).getName());
                ret.add(new PluginResponse("Configuration du serveur [" + CastabotClient.getGuild(guildId).getName() + "] rechargée."));
                break;
        }
        
        return ret;
    }
}
