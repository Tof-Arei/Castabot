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

import org.json.JSONObject;

/**
 *
 * @author Arei
 */
public class RolePermission extends UserPermission {
    private final int priority;
    private final String xtends;
    
    public RolePermission(String target, JSONObject rolesPermission, JSONObject objRolePermission) {
        super(target, objRolePermission);
        type = TYPE_ROLE;
        priority = objRolePermission.getInt("priority");
        xtends = objRolePermission.getString("extends");
        
        if (rolesPermission.has(xtends)) {
            addPermission(new RolePermission(xtends, rolesPermission, rolesPermission.getJSONObject(xtends)), false);
        }
    }
    
    public int getPriority() {
        return priority;
    }
    
    public String getExtends() {
        return xtends;
    }
}
