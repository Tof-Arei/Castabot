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

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Arei
 */
public class RollResult {
    private String caption;
    private boolean criticalFailure = false;
    private boolean criticalSuccess = false;
    private String total = "0";
    private final List<List<Dice>> lstDice = new ArrayList<>();
    private final List<FixedValue> lstFixed = new ArrayList<>();
    private final List<Dice> lstExplode = new ArrayList<>();
    
    public RollResult() {
        
    }
    
    public void calculateGlobalTotal() {
        int subTotal = 0;
        for (List<Dice> lstSubDice : lstDice) {
            for (Dice dice : lstSubDice) {
                if (dice.isNegative()) {
                    subTotal -= dice.getValue();
                } else {
                    subTotal += dice.getValue();
                }
            }
        }
        for (FixedValue fixed : lstFixed) {
            if (fixed.isNegative()) {
                subTotal -= fixed.getValue();
            } else {
                subTotal += fixed.getValue();
            }
        }
        for (Dice dice : lstExplode) {
            subTotal += dice.getValue();
        }
        
        if (subTotal <= 0) {
            subTotal = 1;
        }
        total = String.valueOf(subTotal);
    }

    public String getCaption() {
        return caption;
    }
    
    public void setCaption(String caption) {
        this.caption = caption;
    }

    public String getTotal() {
        try {
            if (Integer.parseInt(total) <= 0) {
                return "1";
            }
        } catch (NumberFormatException ex) {}
            
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }
    
    public List<List<Dice>> getLstDice() {
        return lstDice;
    }
    
    public void addDice(Dice dice) {
        List<Dice> lstTmp = new ArrayList<>();
        lstTmp.add(dice);
        this.lstDice.add(lstTmp);
    }
    
    public void addDices(List<List<Dice>> lstDice) {
        this.lstDice.addAll(lstDice);
    }

    public List<FixedValue> getLstFixed() {
        return lstFixed;
    }
    
    public void addFixedValue(FixedValue fixedValue) {
        lstFixed.add(fixedValue);
    }
    
    public void addFixedValues(List<FixedValue> lstFixed) {
        this.lstFixed.addAll(lstFixed);
    }
    
    public List<Dice> getLstExplode() {
        return lstExplode;
    }
    
    public void addExplode(Dice dice) {
        lstExplode.add(dice);
    }
    
    public void addExplodes(List<Dice> lstExplode) {
        this.lstExplode.addAll(lstExplode);
    }

    public boolean isCriticalFailure() {
        return criticalFailure;
    }

    public void setCriticalFailure(boolean criticalFailure) {
        this.criticalFailure = criticalFailure;
    }

    public boolean isCriticalSuccess() {
        return criticalSuccess;
    }

    public void setCriticalSuccess(boolean criticalSuccess) {
        this.criticalSuccess = criticalSuccess;
    }
}
