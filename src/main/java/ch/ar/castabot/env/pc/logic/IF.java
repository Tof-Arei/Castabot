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
package ch.ar.castabot.env.pc.logic;

import ch.ar.castabot.env.pc.Operation;
import ch.ar.castabot.env.pc.PseudoCode;

/**
 *
 * @author Arei
 */
public class IF extends PseudoCode {
    public IF(String formula) {
        super(formula);
    }

    @Override
    public String calculate() {
        String[] splitFormula = formula.split(";");
        String cond1 = splitFormula[1];
        String operator = splitFormula[2];
        String cond2 = splitFormula[3];
        String ifTrue = splitFormula[4];
        String ifFalse = splitFormula[5];
        
        Operation operation = new Operation(cond1, operator, cond2);
        if (operation.getLogicResult()) {
            return ifTrue;
        } else {
            return ifFalse;
        }
    }
}
