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
    private final ArrayList<Dice> availableDices = new ArrayList<>();
    private final ArrayList<Rolltype> lstRolltypes = new ArrayList<>();

    public Rules(String name) {
        this.name = name;
        loadRules();
    }
    
    private void loadRules() {
        byte[] rawRules;
        try {
            rawRules = Files.readAllBytes(Paths.get("data/plugins/roll/rules.json"));
            JSONObject rulesConfig = new JSONObject(new String(rawRules)).getJSONObject(name);
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
                        rawRolltype.getString("bonus_dice"), rawRolltype.getBoolean("explode"), rawRolltype.getString("explode_action"), 
                        rawRolltype.getString("arg").charAt(0), rawRolltype.getString("format"), rawRolltype.getString("total")));
            }
        } catch (IOException ex) {
            Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public Rolltype getUsedRolltype(char arg) {
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
    
    public boolean isRollValid(String str, ArrayList<ArrayList<Dice>> lstDice, char arg) {
        boolean ret = false;
        Rolltype rolltype = getUsedRolltype(arg);
        
        Pattern patShort = Pattern.compile(rolltype.getFormat());
        Matcher matShort = patShort.matcher(str);
        if (matShort.find()) {
            ret = true;
        }
        
        for (ArrayList<Dice> lstSubDice : lstDice) {
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
    
    public RollResult getRollResults(ArrayList<ArrayList<Dice>> lstDice, ArrayList<FixedValue> lstFixed, char arg) {
        RollResult ret = new RollResult();
        ret.addDices(lstDice);
        ret.addFixedValues(lstFixed);
        
        Rolltype rolltype = getUsedRolltype(arg);
        // Add bonus dice(s) if available
        if (rolltype.getBonusDice().length() > 0) {
            Dice bonusDice = new Dice("Bonus", Integer.parseInt(rolltype.getBonusDice().split("d")[1]), false);
            bonusDice.setBonus(true);
            ret.addDice(bonusDice);
        }
        
        
        // Check for critical success/failure
        boolean criticalFailure = false;
        boolean criticalSuccess = false;
        PseudoCode pcRoll = new PseudoCode();
        int nbDice = 0;
        for (ArrayList<Dice> lstSubDice : ret.getLstDice()) {
            for (Dice dice : lstSubDice) {
                pcRoll.addObject(nbDice, dice);
            }
        }
        
        pcRoll.setFormula(rolltype.getCriticalFailure());
        if (Boolean.parseBoolean(pcRoll.evaluate())) {
            criticalFailure = true;
        }
        pcRoll.setFormula(rolltype.getCriticalSuccess());
        if (Boolean.parseBoolean(pcRoll.evaluate())) {
            criticalSuccess = true;
        }
        
        // Do potential dice explosion and handle critical failure
        String caption = "Lancer:";
        if (criticalSuccess) {
            caption = "Réussite critique:";
            if (rolltype.canExplode()) {
                caption += " Avec éxplosion(s)!";
                pcRoll.setFormula(rolltype.getExplodeAction());
                String[] explodeResult = pcRoll.evaluate().split("-");
                for (int i = 0; i < explodeResult.length; i++) {
                    String[] singleResult = explodeResult[i].split("/");
                    Dice explodeDice = new Dice("Ex", Integer.parseInt(singleResult[1]), false);
                    explodeDice.setValue(Integer.parseInt(singleResult[0]));
                    ret.addExplode(explodeDice);
                }
            }
        } else if (criticalFailure) {
            caption = "Échec critique:";
        }
        ret.setCaption(caption);
        
        // Finally calculate the total and return the RollResult
        ret.calculateTotal();
        
        return ret;
    }
    
    public String getName() {
        return name;
    }
}
