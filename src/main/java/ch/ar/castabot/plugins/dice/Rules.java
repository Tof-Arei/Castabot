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
package ch.ar.castabot.plugins.dice;

import ch.ar.castabot.plugins.Plugin;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author Arei
 */
public class Rules {
    private String name;
    private JSONObject rulesConfig;
    
    public Rules(String name) {
        this.name = name;
        
        byte[] rawRules;
        try {
            rawRules = Files.readAllBytes(Paths.get("data/plugins/dice/rules.json"));
            rulesConfig = new JSONObject(new String(rawRules));
        } catch (IOException ex) {
            Logger.getLogger(Plugin.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        parseRules();
    }
    
    private void parseRules() {
        
    }
    
    public String getName() {
        return name;
    }
}
