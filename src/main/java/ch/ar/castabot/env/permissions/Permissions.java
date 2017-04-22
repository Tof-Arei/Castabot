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

import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

/**
 *
 * @author Arei
 */
public class Permissions {
    private final List<UserPermission> lstPermission = new ArrayList<>();
    
    public Permissions(JSONObject objPermissions) {
        initPermissions(objPermissions);
    }
    
    private void initPermissions(JSONObject objPermissions) {
        if (objPermissions.has("roles")) {
            JSONObject objRolePermissions = objPermissions.getJSONObject("roles");
            for (String roleKey : objRolePermissions.keySet()) {
                JSONObject objRolePermission = objRolePermissions.getJSONObject(roleKey);
                RolePermission rolePermission = new RolePermission(roleKey, objRolePermission);
                lstPermission.add(rolePermission);
            }
        }
        
        if (objPermissions.has("users")) {
            JSONObject objUserPermissions = objPermissions.getJSONObject("users");
            for (String userKey : objUserPermissions.keySet()) {
                JSONObject objUserPermission = objUserPermissions.getJSONObject(userKey);
                UserPermission userPermission = new UserPermission(userKey, objUserPermission);
                lstPermission.add(userPermission);
            }
        }
    }
    
    public UserPermission getPermission(int type, String key) {
        UserPermission ret = null;
        for (UserPermission userPermission : lstPermission) {
            if (userPermission.getType() == type && userPermission.getTarget().equals(key)) {
                ret = userPermission;
                break;
            }
        }
        return ret;
    }
    
    public List<? extends UserPermission> getLstSpecificPermission(int type) {
        List<UserPermission> ret = new ArrayList<>();
        for (UserPermission permission : lstPermission) {
            if (permission.getType() == type) {
                ret.add(permission);
            }
        }
        return ret;
    }

    public List<UserPermission> getLstPermission() {
        return lstPermission;
    }
}
