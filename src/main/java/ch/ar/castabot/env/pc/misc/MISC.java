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
package ch.ar.castabot.env.pc.misc;

import ch.ar.castabot.CastabotClient;
import ch.ar.castabot.env.pc.PseudoCode;
import net.dv8tion.jda.api.entities.Guild;


/**
 *
 * @author Arei
 */
public class MISC extends PseudoCode {
    public MISC(String formula) {
        super(formula);
    }
    
    private int players(String roleName) {
        Guild guild = (Guild) getObject("Guild", 0);
        return CastabotClient.getAvailablePlayers(guild.getId(), roleName);
    }
    
    @Override
    public String calculate() {
        String ret = "NULL";
        String[] splitFormula = formula.split(";");
        switch (splitFormula[1]) {
            case "players":
                ret = String.valueOf(players(splitFormula[2]));
                break;
        }
        return ret;
    }
}
