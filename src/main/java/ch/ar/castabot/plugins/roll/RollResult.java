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

import java.util.ArrayList;

/**
 *
 * @author Arei
 */
public class RollResult {
    private String caption;
    private String total = "0";
    private ArrayList<ArrayList<Dice>> lstDice = new ArrayList<>();
    private ArrayList<FixedValue> lstFixed = new ArrayList<>();
    private ArrayList<Dice> lstExplode = new ArrayList<>();
    
    public RollResult() {
        
    }
    
    public void calculateGlobalTotal() {
        int subTotal = 0;
        for (ArrayList<Dice> lstSubDice : lstDice) {
            for (Dice dice : lstSubDice) {
                if (dice.isNegative()) {
                    subTotal -= dice.getValue();
                } else {
                    subTotal += dice.getValue();
                }
            }
        }
        for (FixedValue fixed : lstFixed) {
            if (fixed.isNegative()) {
                subTotal -= fixed.getValue();
            } else {
                subTotal += fixed.getValue();
            }
        }
        for (Dice dice : lstExplode) {
            subTotal += dice.getValue();
        }
        
        if (subTotal <= 0) {
            subTotal = 1;
        }
        total = String.valueOf(subTotal);
    }

    public String getCaption() {
        return caption;
    }
    
    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
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
