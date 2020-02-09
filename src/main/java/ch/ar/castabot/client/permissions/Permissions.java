/*
 *                GLWT(Good Luck With That) Public License
 *                  Copyright (c) Everyone, except Author
 * 
 * Everyone is permitted to copy, distribute, modify, merge, sell, publish,
 * sublicense or whatever they want with this software but at their OWN RISK.
 * 
 *                             Preamble
 * 
 * The author has absolutely no clue what the code in this project does.
 * It might just work or not, there is no third option.
 * 
 * 
 *                 GOOD LUCK WITH THAT PUBLIC LICENSE
 *    TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION, AND MODIFICATION
 * 
 *   0. You just DO WHATEVER YOU WANT TO as long as you NEVER LEAVE A
 * TRACE TO TRACK THE AUTHOR of the original product to blame for or hold
 * responsible.
 * 
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * 
 * Good luck and Godspeed.
 */
package ch.ar.castabot.client.permissions;

import ch.ar.castabot.Castabot;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.api.entities.Role;
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
    
    public RolePermission getRolePermission(String guildId, String userId) {
        // Get @everyone permissions
        RolePermission ret = (RolePermission) getPermission(UserPermission.TYPE_ROLE, "@everyone");
        // Look for role specific permissions
        for (Role role : Castabot.getCastabotClient().getMember(guildId, userId).getRoles()) {
            for (RolePermission rPermission : (List<RolePermission>) getSpecificPermissions(UserPermission.TYPE_ROLE)) {
                if (rPermission.getTarget().equals(role.getName())) {
                    if (rPermission.getPriority() > ret.getPriority()) {
                        ret = rPermission;
                    }
                }
            }
        }
        return ret;
    }
    
    public UserPermission getUserPermissions(String userId) {
        UserPermission ret = null;
        // Look for user speicific permissions
        for (UserPermission userPermission : getSpecificPermissions(UserPermission.TYPE_USER)) {
            if (userPermission.getTarget().equals(userId)) {
                ret = userPermission;
                break;
            }
        }
        
        return ret;
    }
    
    private void initPermissions(JSONObject objPermissions) {
        if (objPermissions.has("roles")) {
            JSONObject objRolePermissions = objPermissions.getJSONObject("roles");
            for (String roleKey : objRolePermissions.keySet()) {
                JSONObject objRolePermission = objRolePermissions.getJSONObject(roleKey);
                RolePermission rolePermission = new RolePermission(roleKey, objPermissions.getJSONObject("roles"), objRolePermission);
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
    
    public List<? extends UserPermission> getSpecificPermissions(int type) {
        List<UserPermission> ret = new ArrayList<>();
        for (UserPermission permission : lstPermission) {
            if (permission.getType() == type) {
                ret.add(permission);
            }
        }
        return ret;
    }

    public List<UserPermission> getPermissions() {
        return lstPermission;
    }
}
