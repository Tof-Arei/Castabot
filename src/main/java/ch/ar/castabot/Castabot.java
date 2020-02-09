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
package ch.ar.castabot;

import ch.ar.castabot.env.permissions.Permissions;
import ch.ar.castabot.plugins.PluginException;
import ch.ar.castabot.plugins.PluginSettings;
import ch.ar.castabot.plugins.cards.Deck;
import ch.ar.castabot.plugins.roll.Rules;
import ch.ar.castabot.plugins.roll.TokenPouch;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 * @todo audio (multi-server stop bug)
 * @todo better command output (embeds)
 * @todo make help command react with permissions
 * @todo still more refactoring: 
 *          - Make plugins and PC modules testable
 *          - Separate JDA, lavaplayer and related tasks from Castabot, Plugins and PC modules.
 *            (Use PluginManager to retrieve needed Discord-related objects with bare Strings as arguments instead of Guild object)
 *          - Leave everything Discord-related related in CastabotClient
 * @author Arei
 */
public class Castabot {
    private final Properties config = new Properties();
    private JSONObject settings;
    private volatile Map<String, Permissions> hmGuildPermissions = new HashMap<>();
    private volatile Map<String, PluginSettings> hmGuildSettings = new HashMap<>();

    public Castabot() {
         try {
            config.load(new FileInputStream("data/config/config.properties"));
            System.out.println("Démarrage du Castabot™ v"+config.getProperty("bot_version"));
            
            System.out.println("Préchauffage de la machine à café.");
            byte[] rawFile = Files.readAllBytes(Paths.get("data/config/settings.json"));
            settings = new JSONObject(new String(rawFile));
            
            System.out.println("Peignage de la moustache.");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Castabot.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Castabot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void initSettings(String guildId, String guildName) throws PluginException {
        // Try to find the server specific permission files
        File file = new File("data/config/permissions/"+guildId+".json");
        try {
            if (file.exists()) {
                byte[] rawFile = Files.readAllBytes(Paths.get("data/config/permissions/"+guildId+".json"));
                Permissions permissions = new Permissions(new JSONObject(new String(rawFile)));
                hmGuildPermissions.put(guildId, permissions);
            } else {
                // If nothing found, create a new permission file copying the default file
                byte[] rawFile = Files.readAllBytes(Paths.get("data/config/permissions/default.json"));
                String rawPermissions = new String(rawFile).replace("{server-name}", guildName);
                FileOutputStream fos = new FileOutputStream("data/config/permissions/"+guildId+".json");
                fos.write(rawPermissions.getBytes());

                Permissions permissions = new Permissions(new JSONObject(rawPermissions));
                hmGuildPermissions.put(guildId, permissions);
            }
        } catch (IOException ex) {
            Logger.getLogger(Castabot.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // Check if settings are already registered, if so, delete them (reload)
        PluginSettings pluginSettings = hmGuildSettings.get(guildId);
        if (pluginSettings != null) {
            hmGuildSettings.remove(guildId);
        }
        
        pluginSettings = new PluginSettings();
        Map<String, Object> cardsSettings = new HashMap<>();
        cardsSettings.put("deck", new Deck("default"));
        pluginSettings.addSetting("cards", cardsSettings);
        
        Map<String, Object> rollSettings = new HashMap<>();
        rollSettings.put("rules", new Rules("default"));
        rollSettings.put("tokenPouch", new TokenPouch());
        pluginSettings.addSetting("roll", rollSettings);
        
        hmGuildSettings.put(guildId, pluginSettings);
    }
    
    public void deleteSettings(String guildId) {
        hmGuildSettings.remove(guildId);
    }
    
    public Properties getConfig() {
        return config;
    }
    
    public JSONObject getSettings() {
        return settings;
    }
    
    public Permissions getPermissions(String guildId) {
        return hmGuildPermissions.get(guildId);
    }
    
    public PluginSettings getPluginSettings(String guildId) {
        return hmGuildSettings.get(guildId);
    }
}