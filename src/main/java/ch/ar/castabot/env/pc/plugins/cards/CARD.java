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
package ch.ar.castabot.env.pc.plugins.cards;

import ch.ar.castabot.Castabot;
import ch.ar.castabot.env.pc.PseudoCode;
import ch.ar.castabot.plugins.PluginException;
import ch.ar.castabot.plugins.cards.Card;
import ch.ar.castabot.plugins.cards.Deck;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Arei
 */
public class CARD extends PseudoCode {
    public CARD(String formula) {
        super(formula);
    }
    
    private String init(String deckName) {
        String ret = "";
        try {
            String guildId = (String) getObject("Guild", 0);
            Deck deck = new Deck(deckName);
            deck.shuffle();
            Castabot.getCastabot().getPluginSettings(guildId).setValue("cards", "deck", deck);
            ret += "Jeu de cartes initié avec le deck ["+deck.getName()+"] et mélangé.";
        } catch (PluginException ex) {
            Logger.getLogger(CARD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }
    
    private String shuffle() {
        String ret = "";
        try {
            String guildId = (String) getObject("Guild", 0);
            Deck deck = (Deck) Castabot.getCastabot().getPluginSettings(guildId).getValue("cards", "deck");
            deck.shuffle();
            ret += "Jeu de cartes mélangé.";
        } catch (PluginException ex) {
            Logger.getLogger(CARD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }
    
    @Override
    public String calculate() {
        String ret = "";
        String[] splitFormula = formula.split(";");
        Card card = null;
        switch (splitFormula[1]) {
            case "init":
                ret = init(splitFormula[2]);
                break;
            case "shuffle":
                ret = shuffle();
                break;
        }
        
        return ret;
    }
}
