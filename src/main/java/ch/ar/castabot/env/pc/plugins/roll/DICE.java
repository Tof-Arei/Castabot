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
package ch.ar.castabot.env.pc.plugins.roll;

import ch.ar.castabot.env.pc.PseudoCode;
import ch.ar.castabot.plugins.roll.Dice;
import java.util.List;

/**
 *
 * @author Arei
 */
public class DICE extends PseudoCode {
    public DICE(String formula) {
        super(formula);
    }
    
    private int all(String type) {
        int ret = 0;
        
        List<Dice> lstDice = (List<Dice>) getAllObjects(Dice.class.getName());
        if (lstDice != null) {
            switch (type) {
                case "normal":
                    for (int i = 0; i < lstDice.size(); i++) {
                        Dice dice = (Dice) lstDice.get(i);
                        if (!dice.isBonus()) {
                            ret += dice.getValue();
                        }
                    }
                    break;
                case "bonus":
                    for (int i = 0; i < lstDice.size(); i++) {
                        Dice dice = (Dice) lstDice.get(i);
                        if (dice.isBonus()) {
                            ret += dice.getValue();
                        }
                    }
                    break;
            }
        }
        
        return ret;
    }
    
    private Dice getDice(int index) {
        return (Dice) getObject(Dice.class.getName(), index);
    }
    
    private Dice getLastDice(boolean bonus) {
        Dice ret = null;
        List<Dice> lstDice = (List<Dice>) getAllObjects(Dice.class.getName());
        for (int i = lstDice.size()-1; i >= 0; i--) {
            Dice dice = lstDice.get(i);
            if (dice.isBonus() == bonus) {
                ret = dice;
                break;
            }
        }
        return ret;
    }
    
    @Override
    public String calculate() {
        String ret = "NULL";
        String[] splitFormula = formula.split(";");
        Dice dice = null;
        switch (splitFormula[1]) {
            case "value":
                switch (splitFormula[2]) {
                    case "normal":
                        dice = getLastDice(false);
                        break;
                    case "bonus":
                        dice = getLastDice(true);
                        break;
                    default:
                        dice = getDice(Integer.parseInt(splitFormula[2]));
                }
                if (dice != null) {
                    ret = String.valueOf(dice.getValue());
                }
                break;
            case "max":
                switch (splitFormula[2]) {
                    case "normal":
                        dice = getLastDice(false);
                        break;
                    case "bonus":
                        dice = getLastDice(true);
                        break;
                    default:
                        dice = getDice(Integer.parseInt(splitFormula[2]));
                }
                if (dice != null) {
                    ret = String.valueOf(dice.getMax());
                }
                break;
            case "all":
                ret = String.valueOf(all(splitFormula[2]));
                break;
            case "roll":
                /*ret = "";
                for (int i = 2; i < splitFormula.length; i++) {
                    String[] splitDice = splitFormula[i].split("d");
                    for (int j = 0; j < splitDice.length; j++) {
                        Dice roll = new Dice(dice.getMax(), dice.isNegative());
                        roll.setBonus(dice.isBonus());
                        roll.roll();
                        ret += roll.getValue() + "/" + roll.getMax() + "/" + roll.isBonus() + "-";
                    }
                }*/
                break;
            case "reroll":
                ret = "";
                for (int i = 2; i < splitFormula.length; i++) {
                    switch (splitFormula[i]) {
                        case "normal":
                            dice = getLastDice(false);
                            break;
                        case "bonus":
                            dice = getLastDice(true);
                            break;
                        default:
                            dice = getDice(Integer.parseInt(splitFormula[i])); 
                    }
                    if (dice != null) {
                        Dice reroll = new Dice(dice.getMax(), dice.isNegative());
                        reroll.setBonus(dice.isBonus());
                        reroll.roll();
                        ret += reroll.getValue() + "/" + reroll.getMax() + "/" + reroll.isBonus() + "-";
                    }
                }
                break;
            case "token":
                ret = "";
                int nbToken = Integer.parseInt(splitFormula[2]);
                for (int i = 0; i < nbToken; i++) {
                    int subTotal = 0;
                    String[] tokenRawRoll = splitFormula[3].split("d");
                    for (int j = 0; j < Integer.parseInt(tokenRawRoll[0]); j++) {
                        Dice tokenDice = new Dice(Integer.parseInt(tokenRawRoll[1]), false);
                        tokenDice.roll();
                        subTotal += tokenDice.getValue();
                    }
                    
                    
                    ret += subTotal + "/";
                }
                ret = ret.substring(0, ret.length()-1);
                if (splitFormula[4] != null) {
                    if (!splitFormula[4].equals("NULL")) {
                        ret += ":" + splitFormula[4];
                    }
                }
                break;
        }
        
        return ret;
    }
}
