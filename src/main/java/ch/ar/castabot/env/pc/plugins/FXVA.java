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
import ch.ar.castabot.plugins.roll.FixedValue;
import java.util.HashMap;

/**
 *
 * @author Arei
 */
public class FXVA extends PseudoCode {
    public FXVA(String formula) {
        super(formula);
    }
    
    private int all(){
        int ret = 0;
        for (int i = 0; i < getAllFixedObj().size(); i++) {
            FixedValue fixed = (FixedValue) getAllFixedObj().get(i);
            String bound = (fixed.isNegative()) ? "-" : "+";
            ret += Integer.parseInt(bound+fixed.getValue());
        }
        return ret;
    }
    
    private FixedValue getFixed(int index) {
        FixedValue ret = null;
        
        HashMap<Integer, Object> lstFixedObj = getAllFixedObj();
        if (lstFixedObj.size() > 0 && index <= lstFixedObj.size()) {
            ret = (FixedValue) lstFixedObj.get(index);
        }
        
        return ret;
    }
    
    public HashMap<Integer, Object> getAllFixedObj() {
        return lstObject.get(FixedValue.class.getName());
    }
    
    private FixedValue getLastFixed() {
        FixedValue ret = null;
        
        HashMap<Integer, Object> lstFixedObj = getAllFixedObj();
        if (lstFixedObj.size() > 0) {
            for (int i = lstFixedObj.size()-1; i >= 0; i--) {
                FixedValue fixed = (FixedValue) lstFixedObj.get(i);
                ret = fixed;
                break;
            }
        }
        
        return ret;
    }
    
    @Override
    public String calculate() {
        String ret = "NULL";
        String[] splitFormula = formula.split(";");
        FixedValue fixed = null;
        switch (splitFormula[1]) {
            case "value":
                fixed = getFixed(Integer.parseInt(splitFormula[2]));
                if (fixed != null) {
                    ret = String.valueOf(fixed.getValue());
                }
                break;
            case "all":
                ret = String.valueOf(all());
                break;
        }
        
        return ret;
    }
}
