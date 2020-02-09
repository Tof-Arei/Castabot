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
package ch.ar.castabot.plugins;

import ch.ar.castabot.Castabot;
import ch.ar.castabot.CastabotClient;
import ch.ar.castabot.env.pc.PseudoCode;
import ch.ar.castabot.env.permissions.RolePermission;
import ch.ar.castabot.env.permissions.UserPermission;
import ch.ar.castabot.plugins.roll.Rules;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Arei
 */
public class Command {
    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_SECRET = 1;
    public static final int TYPE_BOTH = 2;
    
    private final String guildId;
    private final String channelId;
    private final String userId;
    private final String message;
    
    private String command = null;
    private String desc = null;
    private String[] args = null;
    private int type = -1;
    
    private final JSONObject settings = CastabotClient.getCastabot().getSettings();
    
    public Command(String guildId, String channelId, String userId, String message) {
        this.guildId = guildId;
        this.userId = userId;
        this.channelId = channelId;
        this.message = message;
        parseCommand();
    }
    
    public boolean isWorthAnAnswer() {
        JSONObject rawCommands = settings.getJSONObject("command_settings").getJSONObject("commands");
        return type >= 0 && rawCommands.has(command);
    }
    
    // Parse the message, extract the command and arguments
    private void parseCommand() {
        String strChar = message.substring(0, 1);
        String[] strArgs = message.substring(1).split(" ");
        JSONObject rawCommandSettings = settings.getJSONObject("command_settings");
        
        // Check the commande type
        JSONObject cmdChars = rawCommandSettings.getJSONObject("chars");
        for (String charKey : cmdChars.keySet()) {
            if (strChar.equals(charKey)) {
                type = Integer.parseInt(cmdChars.getString(charKey));
            }
        }
        
        // Check if the command is a short
        boolean isShort = false;
        JSONObject rawCommands = rawCommandSettings.getJSONObject("commands");
        for (String commandKey : rawCommands.keySet()) {
            JSONObject rawCommand = rawCommands.getJSONObject(commandKey);
            JSONObject rawShorts = rawCommand.getJSONObject("shorts");
            for (String argKey : rawShorts.keySet()) {
                JSONArray arrShorts = rawShorts.getJSONArray(argKey);
                for (int i = 0; i < arrShorts.length(); i++) {
                    String cmdShort = arrShorts.getString(i);
                    String shortCommand = message.substring(1);
                    Pattern patShort = Pattern.compile(cmdShort);
                    Matcher matShort = patShort.matcher(shortCommand);
                    
                    // If command is a short, extract command and arguments
                    if (matShort.find()) {
                        isShort = true;
                        command = commandKey;
                        // Was the argument specified in the short command?
                        boolean argFound = false;
                        for (String arg : strArgs) {
                            if (arg.equals(argKey)) {
                                argFound = true;
                                break;
                            }
                        }
                        int start = 0;
                        if (argFound) {
                            args = new String[strArgs.length];
                        } else {
                            args = new String[strArgs.length+1];
                            args[0] = argKey;
                            start = 1;
                        }
                        for (int j = 0; j < strArgs.length; j++) {
                            args[j+start] = strArgs[j];
                        }

                        break;
                    }
                }
            }
        }
        
        if (!isShort) {
            // Extract command and arguments
            command = strArgs[0];
            args = new String[strArgs.length-1];
            for (int j = 1; j < strArgs.length; j++) {
                args[j-1] = strArgs[j];
            }
        }
    }
    
    public List<PluginResponse> execute() {
        List<PluginResponse> ret = new ArrayList<>();
        // Check permissions and execute command
        if (checkPermissions()) {
            ret = executePlugin();
        }
        return ret;
    }
    
    private List<PluginResponse> executePlugin() {
        List<PluginResponse> ret = new ArrayList<>();
        // Heres comes the ugly bit
        try {
            if (args == null) {
                args = new String[]{};
            }    
            
            String className = command.substring(0, 1).toUpperCase() + command.substring(1);
            Class<?> clazz = Class.forName("ch.ar.castabot.plugins."+command+"."+className);
            Class[] types = {String[].class, String.class, String.class, String.class};
            Constructor<?> constructor = clazz.getConstructor(types);
            Object[] classArgs = {args, guildId, channelId, userId};
            Plugin instance = (Plugin) constructor.newInstance(classArgs);
            
            if (args.length > 0) {
                if (args[0].equals("-h") || args[0].equals("--help")) {
                    JSONObject rawCommands = settings.getJSONObject("command_settings").getJSONObject("commands");
                    if (rawCommands.has(command)) {
                        Rules rules = (Rules) CastabotClient.getCastabot().getPluginSettings(guildId).getValue("roll", "rules");
                        PseudoCode pc = new PseudoCode(rawCommands.getJSONObject(command).getString("desc"));
                        pc.addObject(Rules.class.getName(), rules);
                        ret.add(new PluginResponse(pc.evaluate(), userId));
                    }
                }
            }
            if (ret.isEmpty()) {
                ret = instance.run();
                if (ret == null) {
                    throw new PluginException("PLUG-?", "Erreur durant l'exécution de la commande ["+command+"].");
                }
            }
        } catch (PluginException e) {
            ret.add(new PluginResponse(e.getMessage(), userId));
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Castabot.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return ret;
    }
    
    private boolean checkPermissions() {
        RolePermission rolePermission = CastabotClient.getCastabot().getPermissions(guildId).getRolePermission(CastabotClient.getMember(guildId, userId));
        UserPermission userPermission = CastabotClient.getCastabot().getPermissions(guildId).getUserPermissions(CastabotClient.getMember(guildId, userId));
        
        boolean ret = rolePermission.getCommandPermission(command).getArgPermission(args[0]);
        if (ret == false && userPermission != null) {
            ret = userPermission.getCommandPermission(command).getArgPermission(args[0]);
        }
        return ret;
    }

    public String getCommand() {
        return command;
    }

    public String[] getArgs() {
        return args;
    }
    
    public int getType() {
        return type;
    }
}
