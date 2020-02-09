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

import ch.ar.castabot.CastabotClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Arei
 */
public class Dice extends RollElement {
    private boolean bonus = false;
    private final int max;
    
    public Dice(int max, boolean negative) {
        super("", 0, negative);
        this.max = max;
    }
    
    public Dice(String caption, int max, boolean negative) {
        super(caption, 0, negative);
        this.max = max;
    }
    
    private int generateWebInteger(int min, int max) {
        int ret = 0;
        try {
            String strUrl = CastabotClient.getCastabot().getConfig().getProperty("web_root") + "rand.php?min=" + String.valueOf(min) + "&max=" + String.valueOf(max);
            strUrl = strUrl.replace("$min", String.valueOf(min)).replace("$max", String.valueOf(max));
            URL url = new URL(strUrl);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                ret = Integer.parseInt(inputLine);
            }
            in.close();
        } catch (MalformedURLException ex) {
            Logger.getLogger(Roll.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Roll.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return ret;
    }
    
    public void roll() {
        value = (ThreadLocalRandom.current()).nextInt(max) + 1;
        //value = generateWebInteger(1, max);
    }

    public int getMax() {
        return max;
    }
    
    public boolean isBonus() {
        return bonus;
    }
    
    public void setBonus(boolean bonus) {
        this.bonus = bonus;
    }
}
