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
package ch.ar.castabot.env.logging;

/**
 *
 * @author christophe
 */
public class CLogger {
    public class Level {
        public static final int DISABLED = -1;
        public static final int EVERYTHING = 0;
        public static final int CHANNELS = 1;
        public static final int PM = 2;
    }
    
    public class Content {
        public static final int EVERYTHING = 0;
        public static final int SYSTEM = 1;
        public static final int MESSAGES = 2;
        public static final int COMMANDS = 3;
    }
    
    private int level = -1;
    private int content = 0;
    
    public CLogger(int level, int content) {
        this.level = level;
        this.content = content;
    }
}
