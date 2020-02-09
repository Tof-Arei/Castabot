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

import java.io.File;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 *
 * @author Arei
 */
public class PluginResponse {
    private String text;
    private MessageEmbed embed;
    private File file;
    
    public PluginResponse(String text, MessageEmbed embed, File file) {
        this.text = text;
        this.file = file;
    }
    
    public PluginResponse(String text) {
        this.text = text;
    }
    
    public PluginResponse(MessageEmbed embed) {
        this.embed = embed;
    }
    
    public PluginResponse(File file) {
        this.file = file;
    }

    public String printText() {
        return (text == null) ? "" : "\r\n" + text;
    }
    
    public String getText() {
        return text;
    }
    
    public MessageEmbed getEmbed() {
        return embed;
    }

    public File getFile() {
        return file;
    }
}
