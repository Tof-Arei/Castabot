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
import ch.ar.castabot.plugins.roll.FixedValue;
import java.util.List;

/**
 *
 * @author Arei
 */
public class FXVA extends PseudoCode {
    public FXVA(String formula) {
        super(formula);
    }
    private int all(){
        int ret = 0;
        List<FixedValue> lstFixed = (List<FixedValue>) getAllObjects(FixedValue.class.getName());
        if (lstFixed != null) {
            for (FixedValue fixed : lstFixed) {
                String bound = (fixed.isNegative()) ? "-" : "+";
                ret += Integer.parseInt(bound+fixed.getValue());
            }
        }
        return ret;
    }
    
    private FixedValue getFixed(int index) {
        return (FixedValue) getObject(FixedValue.class.getName(), index);
    }
    
    @Override
    public String calculate() {
        String ret = "NULL";
        String[] splitFormula = formula.split(";");
        FixedValue fixed = null;
        switch (splitFormula[1]) {
            case "value":
                fixed = getFixed(Integer.parseInt(splitFormula[2]));
                if (fixed != null) {
                    ret = String.valueOf(fixed.getValue());
                }
                break;
            case "all":
                ret = String.valueOf(all());
                break;
        }
        
        return ret;
    }
}
