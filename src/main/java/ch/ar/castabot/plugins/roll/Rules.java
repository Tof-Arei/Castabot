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
    private String help;
    private final List<Token> availableTokens = new ArrayList<>();
    private final List<Dice> availableDices = new ArrayList<>();
    private final List<Rolltype> lstRolltypes = new ArrayList<>();

    public Rules(String name) {
        this.name = name;
        this.help = null;
        loadRules();
    }
    
    private void loadRules() {
        byte[] rawRules;
        try {
            rawRules = Files.readAllBytes(Paths.get("data/plugins/roll/rules.json"));
            JSONObject rulesConfig = new JSONObject(new String(rawRules)).getJSONObject(name);
            help = rulesConfig.getString("help");
            JSONObject rawTokens = rulesConfig.getJSONObject("tokens");
            for (String key : rawTokens.keySet()) {
                JSONObject rawToken = rawTokens.getJSONObject(key);
                JSONArray rawValues = rawToken.getJSONArray("values");
                Integer[] values = rawValues.toList().toArray(new Integer[rawValues.length()]);
                Token token = new Token(key, rawToken.getString("desc"), values, rawToken.getInt("limit"));
                //Token token = new Token(Integer.parseInt(key), rawToken.getString("desc"), rawToken.getInt("limit"));
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
        pcRoll.addObject(0, this);
        
        int index = 0;
        for (List<Dice> lstSubDice : ret.getLstDice()) {
            for (Dice dice : lstSubDice) {
                pcRoll.addObject(index, dice);
                index++;
            }
        }
        index = 0;
        for (FixedValue fixed : ret.getLstFixed()) {
            pcRoll.addObject(index, fixed);
            index++;
        }
        
        // Check for critical success/failure
        boolean criticalFailure = false;
        boolean criticalSuccess = false;
        
        pcRoll.setFormula(rolltype.getCriticalFailure());
        if (Boolean.parseBoolean(pcRoll.evaluate())) {
            criticalFailure = true;
        }
        pcRoll.setFormula(rolltype.getCriticalSuccess());
        if (Boolean.parseBoolean(pcRoll.evaluate())) {
            criticalSuccess = true;
        }
        
        // Do potential dice explosion and handle critical failure
        String caption = rolltype.getDesc() + ": ";
        if (criticalSuccess) {
            caption += " (Réussite critique!) ";
            if (rolltype.canExplode()) {
                explosion(pcRoll, rolltype, ret);
            }
        } else if (criticalFailure) {
            caption += " (Échec critique!) ";
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
        int nbDice = pcRoll.getLstObject().get(Dice.class.getName()).size();
        pcRoll.setFormula(rolltype.getExplodeAction());
        String[] explodeResult = pcRoll.evaluate().split("-");
        for (int i = 0; i < explodeResult.length; i++) {
            String[] singleResult = explodeResult[i].split("/");
            Dice explodeDice = new Dice("(EX) ", Integer.parseInt(singleResult[1]), false);
            explodeDice.setBonus(Boolean.parseBoolean(singleResult[2]));
            explodeDice.setValue(Integer.parseInt(singleResult[0]));
            ret.addExplode(explodeDice);
            pcRoll.addObject(nbDice, explodeDice);
            nbDice++;
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

    public String getHelp() {
        return help;
    }
    
    public Token getToken(int value) {
        Token ret = null;
        for (Token token : availableTokens) {
            if (token.hasValue(value)) {
                ret = token;
                break;
            }
        }
        return ret;
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
