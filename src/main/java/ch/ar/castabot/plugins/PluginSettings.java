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
package ch.ar.castabot.plugins;

import ch.ar.castabot.Castabot;
import ch.ar.castabot.plugins.cards.Deck;
import ch.ar.castabot.plugins.roll.Rules;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Arei
 */
public class PluginSettings {
    private final Map<String, Map<String, Object>> lstSetting = new HashMap<>();

    public PluginSettings() {
        try {
            initSettings();
        } catch (ClassNotFoundException | PluginException ex) {
            Logger.getLogger(Castabot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void initSettings() throws ClassNotFoundException, PluginException {
        Map<String, Object> cardsSetting = new HashMap<>();
        cardsSetting.put("deck", new Deck("default"));
        lstSetting.put("cards", cardsSetting);

        Map<String, Object> rollSetting = new HashMap<>();
        rollSetting.put("rules", new Rules("default"));
        lstSetting.put("roll", rollSetting);
    }

    public void addValue(String plugin, String setting, Object value) {
        lstSetting.get(plugin).put(setting, value);
    }

    public void setValue(String plugin, String setting, Object value) {
        lstSetting.get(plugin).replace(setting, value);
    }

    public Object getValue(String plugin, String setting) {
        return lstSetting.get(plugin).get(setting);
    }
}
