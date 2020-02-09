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
