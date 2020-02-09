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

import ch.ar.castabot.client.CastabotClient;
import ch.ar.castabot.env.pc.PseudoCode;
import ch.ar.castabot.plugins.Plugin;
import ch.ar.castabot.plugins.PluginException;
import ch.ar.castabot.client.plugins.PluginResponse;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 *
 * @author Arei
 */
public class Roll extends Plugin {
    public Roll(String[] args, String guildId, String channelId, String userId) {
        super(args, guildId, channelId, userId);
    }
    
    // 1. Players rolls a set of dice.
    // 2. Bot parses the roll, according to the active rules and optional argument :
    //      - Are the dices authorized?
    //      - Is the roll format valid?
    // 3a. (KO) Returns error to the user.
    // 3b. (OK) Bot execute the roll, check the results and :
    //      - Do rerolls if any, states criticals if any.
    // 4b. Bot finally outputs the roll result to the user
    private PluginResponse roll(String[] args) throws PluginException {
        Rules rules = (Rules) CastabotClient.getCastabot().getPluginSettings(guildId).getValue("roll", "rules");
        String str = "";
        for (int i = 1; i < args.length;i++) {
            str += args[i] + " ";
        }
        str = str.replaceAll("D", "d").trim();
        List<FixedValue> lstFixed = new ArrayList<>();
        List<List<Dice>> lstDice = new ArrayList<>();
        
        // Extract argument if any
        char[] strChars = str.trim().toCharArray();
        char arg = Character.MIN_VALUE;
        if (Character.isLetter(strChars[strChars.length-1])) {
            arg = (Character.isLetter(strChars[strChars.length-1])) ? strChars[strChars.length-1] : null;
            str = str.substring(0, str.length()-1).trim();
        }
        
        // Exctract the raw dices
        List<String> lstRawDices = new ArrayList<>();
        String d = "+";
        for (char ch : str.trim().toCharArray()) {
            if (ch == '+' || ch == '-' || ch == ' ') {
                lstRawDices.add(d);
                d = "";
                d += ch;
            } else {
                d += ch;
            }
        }
        lstRawDices.add(d);
        
        // Make them into actual dices
        for (String rawDice : lstRawDices) {
            String bound = rawDice.toCharArray()[0]+"";
            String[] tmpDice = rawDice.replace(bound, "").split("d");
            List<Dice> lstSubDice = new ArrayList<>();
            if (tmpDice.length > 1) {
                for (int i = 0; i < Integer.parseInt(tmpDice[0]); i++) {
                    Dice dice = new Dice(Integer.parseInt(tmpDice[1]), (bound.equals("-")));
                    lstSubDice.add(dice);
                }
                lstDice.add(lstSubDice);
            } else {
                FixedValue fixed = new FixedValue(Integer.parseInt(tmpDice[0]), (bound.equals("-")));
                lstFixed.add(fixed);
            }
        }
        
        // Check roll validity (according to extracted argument)
        if (!rules.isRollValid(str, lstDice, arg)) {
            String errRoll = str;
            if (arg != Character.MIN_VALUE) {
                errRoll += " " + arg;
            }
            throw new PluginException("ROLL1", "Format du jet invalide: \r\n ["+errRoll+"]");
        }
        
        // Do the rolls
        for (List<Dice> lstSubDice : lstDice) {
            for (Dice dice : lstSubDice) {
                dice.roll();
            }
        }
        
        // Analyse rolls, get result
        RollResult rollResult = rules.getRollResults(lstDice, lstFixed, arg);
        
        // Output original roll
        String originalRoll = "";
        for (List<Dice> lstSubDice : rollResult.getLstDice()) {
            int nbDice = lstSubDice.size();
            Dice tmpDice = lstSubDice.get(0);
            int maxDice = tmpDice.getMax();
            String bound = (tmpDice.isNegative()) ? "-" : "+";
            originalRoll +=  "[" + tmpDice.getCaption() + bound + nbDice+"d"+maxDice + "=";
            for (Dice dice : lstSubDice) {
                originalRoll += "(" + dice.getValue() + ")";
            }
            originalRoll += "]";
        }
        for (FixedValue fixed : rollResult.getLstFixed()) {
            String bound = (fixed.isNegative()) ? "-" : "+";
            originalRoll += "[" + bound + fixed.getValue() + "]";
        }
        
        // Output explosions if any
        String explosion = "";
        for (Dice dice : rollResult.getLstExplode()) {
            explosion += "[1d" + dice.getMax() + "=(" + dice.getValue() + ")]";
        }
        
        // Send response to user
        EmbedBuilder embBuild = new EmbedBuilder();
        if (rollResult.isCriticalFailure()) {
            embBuild.setColor(Color.RED);
        } else if (rollResult.isCriticalSuccess()) {
            embBuild.setColor(Color.GREEN);
        }
        embBuild.addField(new MessageEmbed.Field(rollResult.getCaption(), originalRoll, false));
        if (!explosion.equals("")) {
            embBuild.addField(new MessageEmbed.Field("Explosion", explosion, false));
        }
        embBuild.addField(new MessageEmbed.Field("Total", rollResult.getTotal(), false));
        
        return new PluginResponse(embBuild.build());
    }
    
    private String rules(String rulesName) {
        Rules rules = new Rules(rulesName);
        CastabotClient.getCastabot().getPluginSettings(guildId).setValue("roll", "rules", rules);
        String ret = "Activation des règles de roll ["+rules.getName()+"].";
        
        PseudoCode pc = new PseudoCode(rules.getActivateAction());
        pc.addObject("Guild", CastabotClient.getGuild(guildId).getId());
        String eval = pc.evaluate();
        if (eval != null) {
            ret += "\r\n" + eval;
        }
        return ret;
    }
    
    private String fill() {
        Rules rules = (Rules) CastabotClient.getCastabot().getPluginSettings(guildId).getValue("roll", "rules");
        PseudoCode pc = new PseudoCode();
        pc.addObject("Guild", CastabotClient.getGuild(guildId).getId());
        
        TokenPouch tokenPouch = new TokenPouch();
        List<Token> lstToken = new ArrayList<>();
        for (Token token : rules.getAvailableTokens()) {
            addToken(lstToken, tokenPouch, token, rules, pc);
        }
        
        CastabotClient.getCastabot().getPluginSettings(guildId).setValue("roll", "tokenPouch", tokenPouch);
        return "Poche à token remplies selon les règles: [" + rules.getName() + "].\r\n Tokens générés: [" + tokenPouch.countTokens() + "].";
    }
    
    private void addToken(List<Token> lstToken, TokenPouch tokenPouch, Token token, Rules rules, PseudoCode pc) {
        pc.setFormula(token.getLimit());
        int max = Integer.parseInt(pc.evaluate());
        for (int i = 0; i < max; i++) {
            if (tokenPouch.hasToken(token)) {
                if (tokenPouch.countToken(token) < max) {
                    tokenPouch.addToken(token);
                } else {
                    int val = (ThreadLocalRandom.current()).nextInt(rules.getAvailableTokens().size()) + 0;
                    addToken(lstToken, tokenPouch, rules.getAvailableTokens().get(val), rules, pc);
                }
            } else {
                tokenPouch.addToken(token);
            }
        }
    }
    
    private String token(int nb) {
        String ret = "";
        TokenPouch tokenPouch = (TokenPouch) CastabotClient.getCastabot().getPluginSettings(guildId).getValue("roll", "tokenPouch");
        if (nb <= tokenPouch.countTokens()) {
            Map<Token, Integer> hmToken = new HashMap<>();
            for (int i = 0; i < nb; i++) {
                Token token = tokenPouch.getRandomToken();
                if (hmToken.get(token) != null) {
                    int count = hmToken.get(token);
                    hmToken.put(token, count+1);
                } else {
                    hmToken.put(token, 1);
                }
            }
            for (Token token : hmToken.keySet()) {
                ret += "(" + hmToken.get(token) + ")" + token.getDesc() + "\r\n";
            }
        } else {
            ret = "Plus assez de tokens dans le sac.\r\n [" + tokenPouch.countTokens() + "] tokens restant.";
        }
        
        return ret;
    }
    
    @Override
    public List<PluginResponse> run() throws PluginException {
        List<PluginResponse> ret = new ArrayList<>();
        switch (args[0]) {
            case "fill":
                ret.add(new PluginResponse(fill()));
                break;
            case "rules":
                if (args.length > 1) {
                    ret.add(new PluginResponse(rules(args[1])));
                }
                break;
            case "roll":
                if (args.length > 1) {
                    ret.add(roll(args));
                }
                break;
            case "token":
                if (args.length > 1) {
                    ret.add(new PluginResponse(token(Integer.parseInt(args[1]))));
                } else {
                    ret.add(new PluginResponse(token(1)));
                }
                break;
        }
        return ret;
    }
}
