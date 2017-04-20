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
package ch.ar.castabot.env.pc.plugins.roll;

import ch.ar.castabot.env.pc.PseudoCode;
import ch.ar.castabot.plugins.roll.Dice;
import java.util.Map;

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
        
        if (getAllDiceObj() != null) {
            switch (type) {
                case "normal":
                    for (int i = 0; i < getAllDiceObj().size(); i++) {
                        Dice dice = (Dice) getAllDiceObj().get(i);
                        if (!dice.isBonus()) {
                            ret += dice.getValue();
                        }
                    }
                    break;
                case "bonus":
                    for (int i = 0; i < getAllDiceObj().size(); i++) {
                        Dice dice = (Dice) getAllDiceObj().get(i);
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
        Dice ret = null;
        
        Map<Integer, Object> lstDiceObj = getAllDiceObj();
        if (lstDiceObj.size() > 0 && index <= lstDiceObj.size()) {
            ret = (Dice) lstDiceObj.get(index);
        }
        
        return ret;
    }
    
    public Map<Integer, Object> getAllDiceObj() {
        return lstObject.get(Dice.class.getName());
    }
    
    private Dice getLastDice(boolean bonus) {
        Dice ret = null;
        
        Map<Integer, Object> lstDiceObj = getAllDiceObj();
        if (lstDiceObj.size() > 0) {
            for (int i = lstDiceObj.size()-1; i >= 0; i--) {
                Dice dice = (Dice) lstDiceObj.get(i);
                if (dice.isBonus() == bonus) {
                    ret = dice;
                    break;
                }
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
