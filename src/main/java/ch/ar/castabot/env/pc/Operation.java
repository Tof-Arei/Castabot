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
package ch.ar.castabot.env.pc;

/**
 *
 * @author Arei
 */
public class Operation {
    private String cond1;
    private final String operator;
    private String cond2;
    
    public Operation(String cond1, String operator, String cond2) {
        this.cond1 = cond1;
        this.operator = operator;
        this.cond2 = cond2;
    }
    
    private void parseNull() {
        if (cond1 == null) {
            cond1 = "0";
        } else if (cond1.equals("NULL")) {
            cond1 = "0";
        }
        if (cond2 == null) {
            cond2 = "0";
        } else if (cond2.equals("NULL")) {
            cond2 = "0";
        }
    } 
    
    private boolean isMathValid() {
        boolean ret = true;
        try {
            Integer.parseInt(cond1);
            Integer.parseInt(cond2);
        } catch (NumberFormatException ex) {
            ret = false;
        }
        return ret;
    }
    
    public boolean getLogicResult() {
        boolean ret = false;
        parseNull();
        switch (operator) {
            // Logic block
            case "==":
                ret = Integer.parseInt(cond1) == Integer.parseInt(cond2);
                break;
            case "!=":
                ret = Integer.parseInt(cond1) != Integer.parseInt(cond2);
                break;
            case ">=":
                ret = Integer.parseInt(cond1) >= Integer.parseInt(cond2);
                break;
            case ">":
                ret = Integer.parseInt(cond1) > Integer.parseInt(cond2);
                break;
            case "<=":
                ret = Integer.parseInt(cond1) <= Integer.parseInt(cond2);
                break;
            case "<":
                ret = Integer.parseInt(cond1) < Integer.parseInt(cond2);
        }
        return ret;
    }
    
    public double getMathResult() {
        double ret = 0;
        parseNull();
        if (isMathValid()) {
            switch (operator) {
                // Math block
                case "+":
                    ret = Integer.parseInt(cond1) + Integer.parseInt(cond2);
                    break;
                case "-":
                    ret = Integer.parseInt(cond1) - Integer.parseInt(cond2);
                    break;
                case "*":
                    ret = Integer.parseInt(cond1) * Integer.parseInt(cond2);
                    break;
                case "/":
                    ret = Integer.parseInt(cond1) / Integer.parseInt(cond2);
            }
        }
        return ret;
    }
}
