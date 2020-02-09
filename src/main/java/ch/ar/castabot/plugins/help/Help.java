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
package ch.ar.castabot.plugins.help;

import ch.ar.castabot.plugins.Plugin;
import ch.ar.castabot.plugins.PluginException;
import ch.ar.castabot.plugins.PluginResponse;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Arei
 */
public class Help extends Plugin {
    public Help(String[] args, String guildId, String channelId, String userId) {
        super(args, guildId, channelId, userId);
    }
    
    @Override
    public List<PluginResponse> run() throws PluginException {
        List<PluginResponse> ret = new ArrayList<>();
        
        String retStr = "Commandes disponibles: \r\n";
        retStr += "- audio : Permet de jouer de la musique sur le canal audio General. \r\n";
        retStr += "- cards : Permet d'utiliser un jeu de cartes virtuel. \r\n";
        retStr += "- roll : Permet d'utiliser des dés virtuels. \r\n";
        retStr += "- help : affice la liste des commandes.  \r\n";
        retStr += "Nb: Utiliser une commande avec l'argument -h (ou --help) permet d'afficher les informations sur la commande. \r\n";
        retStr += "Utiliser le caractère & (ou lancer la commande via message privé) permet d'effectuer un lancement de commande secret. \r\n";
        ret.add(new PluginResponse(retStr, userId));
        
        return ret;
    }
}
