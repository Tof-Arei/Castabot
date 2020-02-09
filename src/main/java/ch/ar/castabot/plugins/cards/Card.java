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

import ch.ar.castabot.client.CastabotClient;
import java.io.File;

/**
 *
 * @author Arei
 */
public class Card {
    // Mmmmmmmmhhh, Java <3
    public static final int VALUE_JOKER = 0;
    public static final String VALUE_JOKER_S = "Joker";
    public static final int VALUE_JACK = 11;
    public static final String VALUE_JACK_S = "Valet";
    public static final int VALUE_QUEEN = 12;
    public static final String VALUE_QUEEN_S = "Dame";
    public static final int VALUE_KING = 13;
    public static final String VALUE_KING_S = "Roi";
    public static final int VALUE_ACE = 14;
    public static final String VALUE_ACE_S = "As";
    
    public static final int COLOR_SPADE = 1;
    public static final String COLOR_SPADE_S = "Pique";
    public static final int COLOR_CLUB = 2;
    public static final String COLOR_CLUB_S = "Trèfle";
    public static final int COLOR_HEART = 3;
    public static final String COLOR_HEART_S = "Coeur";
    public static final int COLOR_DIAMOND = 4;
    public static final String COLOR_DIAMOND_S = "Carreau";
    
    private final Deck deck;
    
    private final int color;
    private final int value;
    private String desc;
    
    public Card(Deck deck, int color, int value) {
        this.deck = deck;
        this.color = color;
        this.value = value;
    }
    
    public String print() {
        String ret = "";
        switch (value) {
            case VALUE_JOKER:
                ret += VALUE_JOKER_S;
                break;
            case VALUE_ACE:
                ret += VALUE_ACE_S + " de ";
                break;
            case VALUE_JACK:
                ret += VALUE_JACK_S + " de ";
                break;
            case VALUE_QUEEN:
                ret += VALUE_QUEEN_S + " de ";
                break;
            case VALUE_KING:
                ret += VALUE_KING_S + " de ";
                break;
            default:
                ret += String.valueOf(value) + " de ";
                break;
        }
        switch (color) {
            case COLOR_SPADE:
                ret += COLOR_SPADE_S;
                break;
            case COLOR_CLUB:
                ret += COLOR_CLUB_S;
                break;
            case COLOR_HEART:
                ret += COLOR_HEART_S;
                break;
            case COLOR_DIAMOND:
                ret += COLOR_DIAMOND_S;
                break;
        }
        if (desc != null) {
            ret += "\r\n" + desc;
        }
        return ret;
    }
    
    public File getFile() {
        return new File("data/plugins/cards/" + deck.getImgDeck() + "/" + this + ".png");
    }
    
    public String getUrl() {
        String webRoot = CastabotClient.getCastabot().getConfig().getProperty("web_root");
        return webRoot + "files/cards/" + deck.getImgDeck() + "/" + this + ".png";
    }
    
    public String getDesc() {
        return desc;
    }
    
    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getColor() {
        return color;
    }

    public int getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return color+"-"+value;
    }
}
