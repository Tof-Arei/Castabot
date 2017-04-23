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
package ch.ar.castabot.env.permissions;

import java.util.HashMap;
import java.util.Map;
import org.json.JSONObject;

/**
 *
 * @author Arei
 */
public class CommandPermission {
    private final String command;
    private final Map<String, Boolean> hmArg = new HashMap<>();
    
    public CommandPermission(String command, JSONObject objCommandPermission) {
        this.command = command;
        for (String argKey : objCommandPermission.getJSONObject("args").keySet()) {
            boolean objArgPermission = objCommandPermission.getJSONObject("args").getBoolean(argKey);
            hmArg.put(argKey, objArgPermission);
        }
    }
    
    public void addArgs(Map<String, Boolean> newArg) {
       for (String argKey : newArg.keySet()) {
           if (hmArg.get(argKey) == null) {
               hmArg.put(argKey, newArg.get(argKey));
           }
       }
    }
    
    public boolean getArgPermission(String arg) {
        if (hmArg.get(arg) != null) {
            return hmArg.get(arg);
        } else {
            if (hmArg.get("default") != null) {
                return hmArg.get("default");
            }
            return false;
        }
    }

    public String getCommand() {
        return command;
    }

    public Map<String, Boolean> getArgs() {
        return hmArg;
    }
}
