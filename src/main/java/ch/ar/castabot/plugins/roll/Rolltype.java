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
package ch.ar.castabot.plugins.roll;

/**
 *
 * @author Arei
 */
public class Rolltype {
    private final String name;
    private final String desc;
    private final boolean dflt;
    private final String criticalFailure;
    private final String criticalSuccess;
    private final String bonusDice;
    private final boolean explode;
    private final boolean explodeRecursive;
    private final String explodeAction;
    private final char arg;
    private final String format;
    private final String total;

    public Rolltype(String name, String desc, boolean dflt, String criticalFailure, String criticalSuccess, 
            String bonusDice, boolean explode, boolean explodeRecursive, String explodeAction, char arg, String format, String total) {
        this.name = name;
        this.desc = desc;
        this.dflt = dflt;
        this.criticalFailure = criticalFailure;
        this.criticalSuccess = criticalSuccess;
        this.bonusDice = bonusDice;
        this.explode = explode;
        this.explodeRecursive = explodeRecursive;
        this.explodeAction = explodeAction;
        this.arg = arg;
        this.format = format;
        this.total = total;
    }

    public String getName() {
        return name;
    }
    
    public String getDesc() {
        return desc;
    }

    public boolean isDefault() {
        return dflt;
    }

    public String getCriticalFailure() {
        return criticalFailure;
    }

    public String getCriticalSuccess() {
        return criticalSuccess;
    }
    
    public String getBonusDice() {
        return bonusDice;
    }
    
    public boolean canExplode() {
        return explode;
    }
    
    public boolean isExplosionRecursive() {
        return explodeRecursive;
    }
    
    public String getExplodeAction() {
        return explodeAction;
    }
    
    public char getArg() {
        return arg;
    }   
    
    public String getFormat() {
        return format;
    }

    public String getTotal() {
        return total;
    }
}
