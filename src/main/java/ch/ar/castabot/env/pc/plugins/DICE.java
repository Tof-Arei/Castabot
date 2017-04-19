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
package ch.ar.castabot.env.pc.plugins;

import ch.ar.castabot.env.pc.PseudoCode;
import ch.ar.castabot.plugins.roll.Dice;

/**
 *
 * @author Arei
 */
public class DICE extends PseudoCode {
    public DICE(String formula) {
        super(formula);
    }
    
    @Override
    public String calculate() {
        String ret = "NULL";
        String[] splitFormula = formula.split(";");
        Dice dice;
        switch (splitFormula[1]) {
            case "value":
                dice = (Dice) lstObject.get(Integer.parseInt(splitFormula[2]));
                if (dice != null) {
                    ret = String.valueOf(dice.getValue());
                }
                break;
            case "max":
                dice = (Dice) lstObject.get(Integer.parseInt(splitFormula[2]));
                if (dice != null) {
                    ret = String.valueOf(dice.getMax());
                }
                break;
            case "roll":
                /*ret = "";
                for (int i = 1; i < splitFormula.length; i++) {
                    dice = (Dice) lstObject.get(Integer.parseInt(splitFormula[i]));
                    dice.roll();
                    ret += dice.getValue() + "/" + dice.getMax();
                }*/
                break;
            case "reroll":
                ret = "";
                for (int i = 1; i < splitFormula.length; i++) {
                    dice = (Dice) lstObject.get(Integer.parseInt(splitFormula[i]));
                    Dice reroll = new Dice(dice.getMax(), dice.isNegative());
                    reroll.roll();
                    ret += reroll.getValue() + "/" + reroll.getMax();
                }
                break;
        }
        
        return ret;
    }
}
