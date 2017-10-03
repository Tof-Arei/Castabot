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
public class UserPermission {
    public static final int TYPE_ROLE = 1;
    public static final int TYPE_USER = 2;
    
    protected int type;
    protected String target;
    protected final Map<String, CommandPermission> hmCommandPermission = new HashMap<>();
    
    public UserPermission(String target, JSONObject objUserPermission) {
        type = TYPE_USER;
        this.target = target;
        for (String commandKey : objUserPermission.getJSONObject("commands").keySet()) {
            JSONObject objCommandPermission = objUserPermission.getJSONObject("commands").getJSONObject(commandKey);
            CommandPermission commandPermission = new CommandPermission(commandKey, objCommandPermission);
            hmCommandPermission.put(commandKey, commandPermission);
        }
    }

    protected void addPermission(UserPermission userPermission, boolean erase) {
        for (String commandKey : userPermission.getCommandPermissions().keySet()) {
            if (getCommandPermission(commandKey) == null) {
                hmCommandPermission.put(commandKey, userPermission.getCommandPermission(commandKey));
            }
            getCommandPermission(commandKey).addArgs(userPermission.getCommandPermission(commandKey).getArgs(), erase);
        }
    }
    
    public CommandPermission getCommandPermission(String command) {
        return hmCommandPermission.get(command);
    }
    
    public Map<String, CommandPermission> getCommandPermissions() {
        return hmCommandPermission;
    }
    
    public int getType() {
        return type;
    }

    public String getTarget() {
        return target;
    }
}
