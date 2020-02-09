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
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Arei
 */
public class TokenPouch {
    List<Token> lstTokenIn = new ArrayList<>();
    List<Token> lstTokenOut = new ArrayList<>();
    
    public boolean hasToken(Token token) {
        return hasToken(token, true);
    }
    
    public boolean hasToken(Token token, boolean in) { 
        return countToken(token, in) > 0;
    }
    
    public int countToken(Token token) {
        return countToken(token, true);
    }
    
    public int countToken(Token token, boolean in) {
        int ret = 0;
        for (Token iToken : getTokens(in)) {
            if (iToken.compareTo(token) == 0) {
                ret++;
            }
        }
        return ret;
    }
    
    private List<Token> getTokens(boolean in) {
        List<Token> lstToken;
        if (in) {
            lstToken = lstTokenIn;
        } else {
            lstToken = lstTokenOut;
        }
        return lstToken;
    }
    
    public Token getRandomToken() {
        Token ret = null;
        if (lstTokenIn.size() > 0) {
            int val = (ThreadLocalRandom.current()).nextInt(lstTokenIn.size()) + 0;
            ret = lstTokenIn.get(val);
            removeToken(ret);
        }
        return ret;
    }
    
    public int countTokens() {
        return countTokens(true);
    }
    
    public int countTokens(boolean in) {
        return getTokens(in).size();
    }
    
    public void addToken(Token token) {
        lstTokenIn.add(token);
    }
    
    public void addTokens(List<Token> lstToken) {
        lstTokenIn.addAll(lstToken);
    }
    
    public void removeToken(Token token) {
        lstTokenIn.remove(token);
        lstTokenOut.add(token);
    }
}
