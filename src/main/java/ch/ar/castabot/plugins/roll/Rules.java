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
package ch.ar.castabot.plugins.roll;

import ch.ar.castabot.env.pc.PseudoCode;
import ch.ar.castabot.plugins.Plugin;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Arei
 */
public class Rules {
    private final String name;
    private String activateAction;
    private String help;
    private final TokenPouch tokenPouch = new TokenPouch();
    private final List<Token> availableTokens = new ArrayList<>();
    
    private final List<Dice> availableDices = new ArrayList<>();
    private final List<Rolltype> lstRolltypes = new ArrayList<>();

    public Rules(String name) {
        this.name = name;
        loadRules();
    }
    
    private void loadRules() {
        try {
            byte[] rawRules = Files.readAllBytes(Paths.get("data/plugins/roll/rules.json"));
            JSONObject rulesConfig = new JSONObject(new String(rawRules)).getJSONObject(name);
            activateAction = rulesConfig.getString("activate_action");
            help = rulesConfig.getString("help");
            JSONObject rawTokens = rulesConfig.getJSONObject("tokens");
            for (String key : rawTokens.keySet()) {
                JSONObject rawToken = rawTokens.getJSONObject(key);
                JSONArray rawValues = rawToken.getJSONArray("values");
                Integer[] values = rawValues.toList().toArray(new Integer[rawValues.length()]);
                Token token = new Token(key, rawToken.getString("desc"), values, rawToken.getString("limit"));
                availableTokens.add(token);
            }
            JSONArray dices = rulesConfig.getJSONArray("dices");
            for (int i = 0; i < dices.length(); i++) {
                String[] rawDice = dices.getString(i).split("d");
                availableDices.add(new Dice(Integer.parseInt(rawDice[1]), false));
            }
            JSONArray rawRolltypes = rulesConfig.getJSONArray("rolltypes");
            for (int i = 0; i < rawRolltypes.length(); i++) {
                JSONObject rawRolltype = rawRolltypes.getJSONObject(i);
                lstRolltypes.add(new Rolltype(rawRolltype.getString("name"), rawRolltype.getString("desc"),
                        rawRolltype.getBoolean("default"), rawRolltype.getString("critical_failure"), rawRolltype.getString("critical_success"), 
                        rawRolltype.getString("bonus_dice"), rawRolltype.getBoolean("explode"), rawRolltype.getBoolean("explode_recursive"), 
                        rawRolltype.getString("explode_action"), rawRolltype.getString("arg").charAt(0), rawRolltype.getString("format"), 
                        rawRolltype.getString("total")));
            }
        } catch (IOException ex) {
            Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Rolltype getUsedRolltype(char arg) {
        arg = Character.toLowerCase(arg);
        Rolltype ret = null;
        for (Rolltype rollType : lstRolltypes) {
            if (arg == Character.MIN_VALUE && rollType.isDefault()) {
                ret =  rollType;
                break;
            } else if (arg == rollType.getArg()) {
                ret = rollType;
                break;
            }
        }
        return ret;
    }
    
    public boolean isRollValid(String str, List<List<Dice>> lstDice, char arg) {
        boolean ret = true;
        Rolltype rolltype = getUsedRolltype(arg);
        if (rolltype == null) {
            return false;
        }
        
        if (rolltype.getFormat().length() > 0) {
            Pattern patShort = Pattern.compile(rolltype.getFormat());
            Matcher matShort = patShort.matcher(str);
            if (!matShort.matches()) {
                ret = false;
            }
        }
        
        for (List<Dice> lstSubDice : lstDice) {
            for (Dice dice : lstSubDice) {
                boolean foundDice = false;
                for (Dice avDice : availableDices) {
                    if (dice.getMax() == avDice.getMax()) {
                        foundDice = true;
                    }
                }

                if (availableDices.size() > 0 && !foundDice) {
                    ret = false;
                    break;
                }
            }
        }
        
        return ret;
    }
    
    public RollResult getRollResults(List<List<Dice>> lstDice, List<FixedValue> lstFixed, char arg) {
        Rolltype rolltype = getUsedRolltype(arg);
        // Add bonus dice(s) to roll if available
        if (rolltype.getBonusDice().length() > 0) {
            Dice bonusDice = new Dice("(B) ", Integer.parseInt(rolltype.getBonusDice().split("d")[1]), false);
            bonusDice.setBonus(true);
            bonusDice.roll();
            List<Dice> lstBonusDice = new ArrayList<>();
            lstBonusDice.add(bonusDice);
            lstDice.add(lstBonusDice);
        }
        
        // Prepare the RollResult
        RollResult ret = new RollResult();
        ret.addDices(lstDice);
        ret.addFixedValues(lstFixed);
        
        // Prepare the PseudoCode interpreter
        PseudoCode pcRoll = new PseudoCode();
        pcRoll.addObject(Rules.class.getName(), this);
        
        for (List<Dice> lstSubDice : ret.getLstDice()) {
            for (Dice dice : lstSubDice) {
                pcRoll.addObject(Dice.class.getName(), dice);
            }
        }
        for (FixedValue fixed : ret.getLstFixed()) {
            pcRoll.addObject(FixedValue.class.getName(), fixed);
        }
        
        // Check for critical success/failure
        pcRoll.setFormula(rolltype.getCriticalFailure());
        if (Boolean.parseBoolean(pcRoll.evaluate())) {
            ret.setCriticalFailure(true);
        }
        pcRoll.setFormula(rolltype.getCriticalSuccess());
        if (Boolean.parseBoolean(pcRoll.evaluate())) {
            ret.setCriticalSuccess(true);
        }
        
        // Do potential dice explosion and handle critical failure
        String caption = rolltype.getDesc() + " ";
        if (ret.isCriticalSuccess()) {
            caption += " (Réussite critique!)\r\n";
            if (rolltype.canExplode()) {
                explosion(pcRoll, rolltype, ret);
            }
        } else if (ret.isCriticalFailure()) {
            caption += " (Échec critique!)\r\n";
        }
        ret.setCaption(caption);
        
        // Finally calculate the total and return the RollResult
        pcRoll.setFormula(rolltype.getTotal());
        String strTotal = pcRoll.evaluate();
        if (strTotal != null) {
            ret.setTotal(strTotal);
        } else {
            ret.calculateGlobalTotal();
        }
        
        return ret;
    }
    
    private void explosion(PseudoCode pcRoll, Rolltype rolltype, RollResult ret) {
        pcRoll.setFormula(rolltype.getExplodeAction());
        String[] explodeResult = pcRoll.evaluate().split("-");
        for (int i = 0; i < explodeResult.length; i++) {
            String[] singleResult = explodeResult[i].split("/");
            Dice explodeDice = new Dice("(EX) ", Integer.parseInt(singleResult[1]), false);
            explodeDice.setBonus(Boolean.parseBoolean(singleResult[2]));
            explodeDice.setValue(Integer.parseInt(singleResult[0]));
            ret.addExplode(explodeDice);
            pcRoll.addObject(Dice.class.getName(), explodeDice);
        }
        if (rolltype.isExplosionRecursive()) {
            pcRoll.setFormula(rolltype.getCriticalSuccess());
            if (Boolean.parseBoolean(pcRoll.evaluate())) {
                explosion(pcRoll, rolltype, ret);
            }
        }
    }
    
    public String getName() {
        return name;
    }
    
    public String getActivateAction() {
        return activateAction;
    }

    public String getHelp() {
        return help;
    }
    
    public List<Token> getAvailableTokens(){
        return availableTokens;
    }
    
    public TokenPouch getTokenPouch() {
        return tokenPouch;
    }
    
    public int getMinTokenValue() {
        int ret = 99;
        for (Token token : availableTokens) {
            for (Integer val : token.getValues()) {
                if (val < ret) {
                    ret = val;
                }
            }
        }
        return ret;
    }

    public List<Token> getTokens() {
        return availableTokens;
    }
}
