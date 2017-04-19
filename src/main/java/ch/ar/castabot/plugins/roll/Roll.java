/*
 * Copyright 2017 Arei.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.ar.castabot.plugins.roll;

import ch.ar.castabot.CastabotClient;
import ch.ar.castabot.plugins.Plugin;
import ch.ar.castabot.plugins.PluginException;
import ch.ar.castabot.plugins.PluginResponse;
import java.text.DecimalFormat;
import java.util.ArrayList;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.TextChannel;
/**
 *
 * @author Arei
 */
public class Roll extends Plugin {
    private Rules rules = new Rules(CastabotClient.getCastabot().getPluginSettings().getValue("roll", "rules"));
    
    
    public Roll(String[] args, TextChannel source, User user) {
        super(args, source, user);
    }
    
    private void rules(String rules) {
        this.rules = new Rules(rules);
        CastabotClient.getCastabot().getPluginSettings().setValue("roll", "rules", rules);
    }
    
    // !1d8+1d6 (savage hit)
    // !1d4+1d6-2 (savage hit)
    // !2d6 (savage action)
    // !2d4+1d8 (savage action)
    
    // 1. Players rolls a set of dice.
    // 2. Bot parses the roll, according to the active rules and optional argument :
    //      - Are the dices authorized?
    //      - Is the roll format valid?
    // 3a. (OK) Returns error to the user.
    // 3b. (KO) Bot execute the roll, check the results and :
    //      - Do rerolls if any, states criticals if any.
    // 4b. Bot finally outputs the roll result to the user
    private PluginResponse roll() throws PluginException {
        PluginResponse ret;
        
        String str = args[0].replaceAll("D", "d");
        ArrayList<FixedValue> lstFixed = new ArrayList<>();
        ArrayList<ArrayList<Dice>> lstDice = new ArrayList<>();
        
        // Extract argument if any
        char[] strChars = str.toCharArray();
        char arg = Character.MIN_VALUE;
        if (Character.isLetter(strChars[strChars.length-1])) {
            arg = (Character.isLetter(strChars[strChars.length-1])) ? strChars[strChars.length-1] : null;
            str = str.replace(arg, Character.MIN_VALUE).trim();
        }
        
        // Exctract the raw dices
        ArrayList<String> lstRawDices = new ArrayList<>();
        String d = "+";
        for (char ch : str.toCharArray()) {
            if (ch == '+' || ch == '-') {
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
            ArrayList<Dice> lstSubDice = new ArrayList<>();
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
            throw new PluginException("ROLL1", "Format du jet invalide: \r\n ["+str+"]");
        }
        
        // Do the rolls
        for (ArrayList<Dice> lstSubDice : lstDice) {
            for (Dice dice : lstSubDice) {
                dice.roll();
            }
        }
        
        // Analyse rolls, get result
        RollResult rollResult = rules.getRollResults(lstDice, lstFixed, arg);
        String response = rollResult.getCaption();
        
        // Re-group same dices from original roll
        ArrayList<ArrayList<Dice>> groupDice = new ArrayList<>();
        
        // Output original roll
        String originalRoll = "";
        for (ArrayList<Dice> lstSubDice : rollResult.getLstDice()) {
            int nbDice = lstSubDice.size();
            Dice tmpDice = lstSubDice.get(0);
            int maxDice = tmpDice.getMax();
            originalRoll +=  "[" + tmpDice.getCaption() + nbDice+"d"+maxDice + "=";
            for (Dice dice : lstSubDice) {
                String bound = (dice.isNegative()) ? "-" : "+";
                originalRoll += "(" + bound + dice.getValue() + ")";
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
        
        DecimalFormat df = new DecimalFormat("###.#");
        response += "{"+originalRoll+"} \r\n";
        if (!explosion.equals("")) {
            response += "Explosion: {"+explosion+"} \r\n";
        }
        response += "Total: {"+rollResult.getTotal()+"}";
        
        // Send response to user
        ret = new PluginResponse(response, user);
        
        return ret;
    }
    
    /*private String roll() throws PluginException {
        ArrayList<PluginResponse> ret = new ArrayList<>();
        String str = args[0].replaceAll("D", "d");
        ArrayList<String> lstDice = new ArrayList<>();
        String d = "+";
        for (char ch : str.toCharArray()) {
            if (ch == '+' || ch == '-') {
                lstDice.add(d);
                d = "";
                d += ch;
            } else {
                d += ch;
            }
        }
        lstDice.add(d);
        
        str = "";
        String retStr = "";
        for (String rawDice : lstDice) {
           String bound = rawDice.toCharArray()[0]+"";
           String[] dice = rawDice.replace(bound, "").split("d");
           if (dice.length > 1) {
               retStr += "["+rawDice+"=";
                for (int i = 0; i < Integer.parseInt(dice[0]); i++) {
                    try {
                        //int val = (new SplittableRandom()).nextInt(Integer.parseInt(dice[1])) + 1;
                        int val = generateWebInteger(1, Integer.parseInt(dice[1]));
                        str += bound+val;
                        retStr += "("+val+")";
                    } catch (IllegalArgumentException e) {
                        throw new PluginException("DICE1", "["+args[0]+"] n'est pas un jet valide.");
                    }
                }
                retStr += "]";
           } else {
               str += bound+dice[0];
               retStr += "["+bound+dice[0]+"]";
           }
        }
        
        Evaluator eval = new Evaluator();
        String total = "0";
        try {
            total = eval.evaluate(str);
        } catch (EvaluationException ex) {
            throw new PluginException("DICE1", "[" + args[0] + "] n'est pas un jet valide.");
        }

        DecimalFormat df = new DecimalFormat("###.#");
        return "Lancer: {"+retStr+"} \r\n Total: ["+df.format(Double.parseDouble(total))+"]";
    }*/
    
    @Override
    public ArrayList<PluginResponse> run() throws PluginException {
        ArrayList<PluginResponse> ret = new ArrayList<>();
        switch (args[0]) {
            case "rules":
                if (args.length > 1) {
                    rules(args[1]);
                } else {
                    rules("default");
                }
                ret.add(new PluginResponse("Activation des règles de roll ["+rules.getName()+"].", user));
                break;
            default:
                //ret.add(new PluginResponse(roll(), user));
                ret.add(roll());
        }
        return ret;
    }
}