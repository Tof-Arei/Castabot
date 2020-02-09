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
package ch.ar.castabot.client.plugins;

import java.awt.Color;
import java.io.File;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 *
 * @author Arei
 */
public class PluginResponse {
    private String text;
    private File file;
    private Color embedColor;
    private EmbedBuilder embedBuilder;
    
    public PluginResponse() {
        
    }
    
    public PluginResponse(String text, File file) {
        this.text = text;
        this.file = file;
    }
    
    public PluginResponse(String text) {
        this.text = text;
    }
    
    public PluginResponse(String text, Color embedColor) {
        this.text = text;
        this.embedColor = embedColor;
    }
    
    public PluginResponse(File file) {
        this.file = file;
    }
    
    public void addEmbedField(String caption, String text, boolean inline) {
        getEmbedBuilder().addField(new MessageEmbed.Field(caption, text, inline));
    }

    public String printText() {
        return (text == null) ? "" : "\r\n" + text;
    }
    
    private EmbedBuilder getEmbedBuilder() {
        if (embedBuilder == null) {
            embedBuilder = new EmbedBuilder();
            embedBuilder.setColor(embedColor);
        }
        return embedBuilder;
    }
    
    public String getText() {
        return text;
    }

    public File getFile() {
        return file;
    }
    
    public MessageEmbed getEmbed() {
       if (embedBuilder != null) {
           return embedBuilder.build();
       } else {
           return null;
       }
    }
    
    public void setEmbedColor(Color embedColor) {
        this.embedColor = embedColor;
    }
}
