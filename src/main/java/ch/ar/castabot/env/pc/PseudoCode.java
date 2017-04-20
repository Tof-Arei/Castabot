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
package ch.ar.castabot.env.pc;

import ch.ar.castabot.Castabot;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Arei
 */
public class PseudoCode {
    protected String formula;
    protected Map<String, Map<Integer, Object>> lstObject = new HashMap<>();
    
    public PseudoCode(String formula) {
        this.formula = formula;
    }
    
    public PseudoCode() {
        this.formula = null;
    }
    
    public String calculate() {
        return null;
    }
    
    public String evaluate() {
        return evaluate(formula);
    }
    
    // {IF;{dice_groups};>;1;{SUM;{dice_1:val};{dice_2:val}};{dice_1:val}}
    // {IF;{dice_groups};>;1;{SUM;3;5};3}   {IF;{dice_groups};>;1;{SUM;3;null};3}}
    // {IF;2;>;1;8;3}                       {IF;1;>;1;3;3}
    // {8}                                  {3}
    private String evaluate(String formula) {
        String ret = null;
        
        if (formula.length() > 0) {
            formula = formula.substring(1);
            formula = formula.substring(0, formula.length()-1);
            String[] splitFormula = formula.split(";");
            
            int posOpen = 0, posClose = formula.length(), cursor = 0;
            boolean subFormulaEnd = false;      
            for (char ch : formula.toCharArray()) {
                switch (ch) {
                    case '{':
                        posOpen = cursor;
                        break;
                    case '}':
                        posClose = cursor;
                        subFormulaEnd = true;
                        break;
                }
                if (subFormulaEnd) {
                    String subFormula = formula.substring(posOpen, posClose+1);
                    String evaluatedSubFormula = evaluate(subFormula);
                    formula = formula.replace(subFormula, evaluatedSubFormula);
                    ret = evaluate("{"+formula+"}");
                    break;
                }
                cursor++;
            }

            if (ret == null) {
                try {
                    Class<?> clazz = getModule("ch.ar.castabot.env.pc", splitFormula[0]);
                    if (clazz != null) {
                        Class[] types = {String.class};
                        Constructor<?> constructor = clazz.getConstructor(types);
                        Object[] classArgs = {formula};
                        PseudoCode pc = (PseudoCode) constructor.newInstance(classArgs);
                        pc.setObjects(lstObject);
                        ret = pc.calculate();
                    } else {
                        ret = formula;
                    }
                } catch (IOException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException ex) {
                    Logger.getLogger(PseudoCode.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
 
        return ret;
    }
    
    private Class getModule(String packName, String className) throws IOException, ClassNotFoundException {
        Class ret = null;

        for (Class clazz : Castabot.getClasses(packName, PseudoCode.class.getClassLoader())) {
            if (clazz.getName().endsWith(className)) {
                ret = clazz;
                break;
            }
        }
        
        return ret;
    }
    
    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }
    
    public Map<String, Map<Integer, Object>> getLstObject() {
        return lstObject;
    }
    
    public void setObjects(Map<String, Map<Integer, Object>> lstObject) {
        this.lstObject = lstObject;
    }
    
    public void addObject(int index, Object object) {
        Map<Integer, Object> subLstObject = lstObject.get(object.getClass().getName());
        if (subLstObject != null) {
            subLstObject.put(index, object);
        } else {
            subLstObject = new HashMap<>();
            subLstObject.put(index, object);
            lstObject.put(object.getClass().getName(), subLstObject);
        }
    }
}