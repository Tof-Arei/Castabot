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

import ch.ar.castabot.plugins.PluginException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONObject;

/**
 *
 * @author Arei
 */
public class Deck {
    private String name;
    private String activateAction;
    private String imgDeck;
     
    private final List<Card> lstCardsIn = new ArrayList<>();
    private final List<Card> lstCardsOut = new ArrayList<>();
   
    public Deck(String name) throws PluginException {
        this.name = name;
        try {
            init();
        } catch (IOException ex) {
            throw new PluginException("CARDS-1", "Le deck ["+name+"] n'éxiste pas.");
        }
    }
    
    private void init() throws PluginException, IOException {
        byte[] rawDecks = Files.readAllBytes(Paths.get("data/plugins/cards/decks.json"));
        JSONObject objDeck = new JSONObject(new String(rawDecks));
        
        if (objDeck.has(name)) {
            fill(objDeck.getJSONObject(name));
            shuffle();
        } else {
            throw new PluginException("CARDS-1", "Le deck ["+name+"] n'éxiste pas.");
        }
    }
    
    private void fill(JSONObject objDeck) {
        fill(objDeck, false);
    }
    
    private void fill(JSONObject objDeck, boolean out) {
        activateAction = objDeck.getString("activate_action");
        imgDeck = objDeck.getString("img_deck");
        // Generate cards
        List<Card> lstCard = new ArrayList<>();
        for (String format : objDeck.getString("cards").split(";")) {
            String[] splitFormat = format.split("-");
            String[] splitCards = splitFormat[1].split(":");
            int color = Integer.parseInt(splitFormat[0]);
            int beg = Integer.parseInt(splitCards[0]);
            int end = Integer.parseInt(splitCards[1]);
            for (int i = beg; i <= end; i++) {
                Card card = new Card(this, color, i);
                lstCard.add(card);
            }
        }
        
        // Generate joker(s)
        String[] splitJokers = objDeck.getString("joker").split(";");
        int nbJokers = Integer.parseInt(splitJokers[1]);
        for (int i = 0; i < nbJokers; i++) {
            String[] rawJoker = splitJokers[0].split("-");
            Card card = new Card(this, Integer.parseInt(rawJoker[0]), Integer.parseInt(rawJoker[1]));
            lstCard.add(card);
        }
        
        // Add descriptions
        for (Card card : lstCard) {
            for (int j = 0; j < objDeck.getJSONArray("descs").length(); j++) {
                JSONObject objDesc = objDeck.getJSONArray("descs").getJSONObject(j);
                if (objDesc.has(card.toString())) {
                    card.setDesc(objDesc.getString(card.toString()));
                }
            }
        }
        
        // Put generated cards in the deck
        if (!out) {
            lstCardsIn.addAll(lstCard);
        } else {
            lstCardsOut.addAll(lstCard);
        }
    }
    
    public Card draw() throws PluginException {
        Card ret = null;
        if (lstCardsIn.size() > 0) {
            Card card = lstCardsIn.get(0);
            lstCardsIn.remove(card);
            lstCardsOut.add(card);
            ret = card;
        } else {
            throw new PluginException("CARDS-3", "Plus de cartes, veuillez mélanger ou réinitialiser le deck.");
        }
        
        return ret;
    }
    
    public void shuffle() throws PluginException {
        if (lstCardsIn.size() > 0 || lstCardsOut.size() > 0) {
            lstCardsIn.addAll(lstCardsOut);
            lstCardsOut.clear();
            Collections.shuffle(lstCardsIn);
        } else {
            throw new PluginException("CARDS-2", "Le deck est vide, veuillez le réinitialiser.");
        }
    }
    
    public int getNbCardsLeft() {
        return lstCardsIn.size();
    }
    
    public String getName() {
        return name;
    }
    
    public String getActivateAction() {
        return activateAction;
    }
    
    public String getImgDeck() {
        return imgDeck;
    }
}
