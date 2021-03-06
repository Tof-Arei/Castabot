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
package ch.ar.castabot.plugins.cards;

import ch.ar.castabot.Castabot;
import ch.ar.castabot.env.pc.PseudoCode;
import ch.ar.castabot.plugins.Plugin;
import ch.ar.castabot.plugins.PluginException;
import ch.ar.castabot.client.plugins.PluginResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Arei
 */
public class Cards extends Plugin {
    public Cards(String[] args, Map<String, String> hmParams) {
        super(args, hmParams);
    }
    
    private String init(String deckName) throws PluginException {
        Deck deck = new Deck(deckName);
        Castabot.getCastabot().getPluginSettings(hmParams.get("guildId")).setValue("cards", "deck", deck);
        String ret = "Jeu de cartes initié avec le deck ["+deck.getName()+"] et mélangé.";
        
        PseudoCode pc = new PseudoCode(deck.getActivateAction());
        String eval = pc.evaluate();
        if (eval != null) {
            ret += "\r\n" + eval;
        }
        return ret;
    }

    @Override
    public List<PluginResponse> run() throws PluginException {
        List<PluginResponse> ret = new ArrayList<>();
        Deck deck = (Deck) Castabot.getCastabot().getPluginSettings(hmParams.get("guildId")).getValue("cards", "deck");
        switch (args[0]) {
            case "deck" :
                if (args.length > 1) {
                    ret.add(new PluginResponse(init(args[1])));
                }
                break;
            case "draw" :
                Card card = deck.draw();
                ret.add(new PluginResponse(card.print()+"\r\n"+card.getUrl()));
                break;
            case "shuffle" :
                deck.shuffle();
                ret.add(new PluginResponse("Jeu de cartes mélangé."));
                break;
        }
        
        return ret;
    }
}
