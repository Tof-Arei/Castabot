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
    
    public void addArgs(Map<String, Boolean> newArg, boolean erase) {
       for (String argKey : newArg.keySet()) {
           if (hmArg.get(argKey) == null) {
               hmArg.put(argKey, newArg.get(argKey));
           } else if (erase) {
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
