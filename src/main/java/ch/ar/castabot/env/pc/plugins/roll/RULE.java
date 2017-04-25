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
import ch.ar.castabot.plugins.PluginSettings;
import ch.ar.castabot.plugins.roll.Rules;

/**
 *
 * @author Arei
 */
public class RULE extends PseudoCode {
    public RULE(String formula) {
        super(formula);
    }
    
    private Rules getRules(int index) {
        return (Rules) this.getObject(Rules.class.getName(), index);
    }
    
    private String help(int index) {
        Rules rules = getRules(index);
        return rules.getHelp();
    }
    
    public String name(int index) {
        Rules rules = getRules(index);
        return rules.getName();
    }
    
    private String rules(String rulesName) {
        Rules rules = new Rules(rulesName);
        PluginSettings pluginSettings = (PluginSettings) hmObject.get("pluginSettings");
        pluginSettings.setValue("roll", "rules", rules);
        //CastabotClient.getCastabot().getPluginSettings().setValue("roll", "rules", rules);
        String ret = "Activation des règles de roll ["+rules.getName()+"].";
        return ret;
    }
    
    @Override
    public String calculate() {
        String ret = "NULL";
        String[] splitFormula = formula.split(";");
        switch (splitFormula[1]) {
            case "rules":
                ret = rules(splitFormula[2]);
                break;
            case "help":
                ret = help(Integer.parseInt(splitFormula[2]));
                break;
            case "name":
                ret = name(Integer.parseInt(splitFormula[2]));
                break;
        }
        
        return ret;
    }
}
