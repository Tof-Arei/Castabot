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

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.reflections.Reflections;

/**
 *
 * @author Arei
 */
public class PseudoCode {
    protected String formula;
    protected Map<String, Map<Integer, Object>> lstObject = new HashMap<>();
    
    private static Set<Class<? extends PseudoCode>> lstModules;
    
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
                    Class<?> clazz = getModule(splitFormula[0]);
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
    
    private static Class getModule(String className) throws IOException, ClassNotFoundException {
        Class ret = null;
        
        if (lstModules == null) {
            Reflections reflections = new Reflections("ch.ar.castabot.env.pc");
            lstModules = reflections.getSubTypesOf(PseudoCode.class);
        }
        Iterator itClasses = lstModules.iterator();
        while (itClasses.hasNext()) {
            Class clazz = (Class) itClasses.next();
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