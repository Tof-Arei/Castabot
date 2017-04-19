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
import java.util.ArrayList;

/**
 *
 * @author Arei
 */
public class RollResult {
    private String caption;
    private int total = 0;
    private ArrayList<ArrayList<Dice>> lstDice = new ArrayList<>();
    private ArrayList<FixedValue> lstFixed = new ArrayList<>();
    private ArrayList<Dice> lstExplode = new ArrayList<>();
    
    public RollResult() {
        
    }
    
    public void calculateGlobalTotal() {
        for (ArrayList<Dice> lstSubDice : lstDice) {
            for (Dice dice : lstSubDice) {
                if (dice.isNegative()) {
                    total -= dice.getValue();
                } else {
                    total += dice.getValue();
                }
            }
        }
        for (FixedValue fixed : lstFixed) {
            if (fixed.isNegative()) {
                total -= fixed.getValue();
            } else {
                total += fixed.getValue();
            }
        }
        for (Dice dice : lstExplode) {
            total += dice.getValue();
        }
        
        if (total <= 0) {
            total = 1;
        }
    }

    public String getCaption() {
        return caption;
    }
    
    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        if (total <= 0) {
            total = 1;
        }
        this.total = total;
    }
    
    public ArrayList<ArrayList<Dice>> getLstDice() {
        return lstDice;
    }
    
    public void addDice(Dice dice) {
        ArrayList<Dice> lstTmp = new ArrayList<>();
        lstTmp.add(dice);
        this.lstDice.add(lstTmp);
    }
    
    public void addDices(ArrayList<ArrayList<Dice>> lstDice) {
        this.lstDice.addAll(lstDice);
    }

    public ArrayList<FixedValue> getLstFixed() {
        return lstFixed;
    }
    
    public void addFixedValue(FixedValue fixedValue) {
        lstFixed.add(fixedValue);
    }
    
    public void addFixedValues(ArrayList<FixedValue> lstFixed) {
        this.lstFixed.addAll(lstFixed);
    }
    
    public ArrayList<Dice> getLstExplode() {
        return lstExplode;
    }
    
    public void addExplode(Dice dice) {
        lstExplode.add(dice);
    }
    
    public void addExplodes(ArrayList<Dice> lstExplode) {
        this.lstExplode.addAll(lstExplode);
    }
}