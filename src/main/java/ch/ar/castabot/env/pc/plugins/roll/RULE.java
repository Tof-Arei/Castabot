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
package ch.ar.castabot.env.pc.plugins.roll;

import ch.ar.castabot.env.pc.PseudoCode;
import ch.ar.castabot.plugins.PluginSettings;
import ch.ar.castabot.plugins.roll.Rules;

/**
 *
 * @author Arei
 */
public class RULE extends PseudoCode {
    public RULE(String formula) {
        super(formula);
    }
    
    private Rules getRules(int index) {
        return (Rules) this.getObject(Rules.class.getName(), index);
    }
    
    private String help(int index) {
        Rules rules = getRules(index);
        return rules.getHelp();
    }
    
    public String name(int index) {
        Rules rules = getRules(index);
        return rules.getName();
    }
    
    private String rules(String rulesName) {
        Rules rules = new Rules(rulesName);
        PluginSettings pluginSettings = (PluginSettings) hmObject.get("pluginSettings");
        pluginSettings.setValue("roll", "rules", rules);
        //CastabotClient.getCastabot().getPluginSettings().setValue("roll", "rules", rules);
        String ret = "Activation des règles de roll ["+rules.getName()+"].";
        return ret;
    }
    
    @Override
    public String calculate() {
        String ret = "NULL";
        String[] splitFormula = formula.split(";");
        switch (splitFormula[1]) {
            case "rules":
                ret = rules(splitFormula[2]);
                break;
            case "help":
                ret = help(Integer.parseInt(splitFormula[2]));
                break;
            case "name":
                ret = name(Integer.parseInt(splitFormula[2]));
                break;
        }
        
        return ret;
    }
}
