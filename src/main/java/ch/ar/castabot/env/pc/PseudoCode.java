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

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.reflections.Reflections;

/**
 *
 * @author Arei
 */
public class PseudoCode {
    protected String formula;
    protected Map<String, List<Object>> hmObject = new HashMap<>();
    
    private static Set<Class<? extends PseudoCode>> lstModules;
    
    public PseudoCode(String formula) {
        this.formula = formula;
    }
    
    public PseudoCode() {
        this.formula = null;
    }
    
    public String calculate() {
        return null;
    }
    
    public String evaluate() {
        return evaluate(formula);
    }
    
    private String evaluate(String formula) {
        String ret = null;
        
        if (formula.length() > 0) {
            if (formula.contains("{")) {
                formula = formula.substring(1);
            }
            if (formula.contains("}")) {
                formula = formula.substring(0, formula.length()-1);
            }
            
            String[] splitFormula = formula.split(";");
            
            int posOpen = 0, posClose = formula.length(), cursor = 0;
            boolean subFormulaEnd = false;      
            for (char ch : formula.toCharArray()) {
                switch (ch) {
                    case '{':
                        posOpen = cursor;
                        break;
                    case '}':
                        posClose = cursor;
                        subFormulaEnd = true;
                        break;
                }
                if (subFormulaEnd) {
                    String subFormula = formula.substring(posOpen, posClose+1);
                    String evaluatedSubFormula = evaluate(subFormula);
                    formula = formula.replace(subFormula, evaluatedSubFormula);
                    ret = evaluate("{"+formula+"}");
                    break;
                }
                cursor++;
            }

            if (ret == null) {
                try {
                    Class<?> clazz = getModule(splitFormula[0]);
                    if (clazz != null) {
                        Class[] types = {String.class};
                        Constructor<?> constructor = clazz.getConstructor(types);
                        Object[] classArgs = {formula};
                        PseudoCode pc = (PseudoCode) constructor.newInstance(classArgs);
                        pc.setObjects(hmObject);
                        ret = pc.calculate();
                    } else {
                        ret = formula;
                    }
                } catch (IOException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException ex) {
                    Logger.getLogger(PseudoCode.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
 
        return ret;
    }
    
    private static Class getModule(String className) throws IOException, ClassNotFoundException {
        Class ret = null;
        
        if (lstModules == null) {
            Reflections reflections = new Reflections("ch.ar.castabot.env.pc");
            lstModules = reflections.getSubTypesOf(PseudoCode.class);
        }
        for (Class clazz : lstModules) {
            if (clazz.getName().endsWith("." + className)) {
                ret = clazz;
                break;
            }
        }
        
        return ret;
    }
    
    public String getFormula() {
        return formula;
    }

    public void setFormula(String formula) {
        this.formula = formula;
    }
    
    protected Map<String, List<Object>> getObjects() {
        return hmObject;
    }
    
    protected Object getObject(String key, int index) {
        return getAllObjects(key).get(index);
    }
    
    protected Object getLastObject(String key) {
        Object ret = null;
        List<? extends Object> lstObject = getAllObjects(key);
        if (lstObject.size() > 0) {
            ret = lstObject.get(lstObject.size()-1);
        }
        return ret;
    }
    
    public List<? extends Object> getAllObjects(String key) {
        return (List<? extends Object>)(Object) hmObject.get(key);
    }
    
    public void setObjects(Map<String, List<Object>> lstObject) {
        this.hmObject = lstObject;
    }
    
    public void addObject(String key, Object object) {
        if (hmObject.get(key) != null) {
            hmObject.get(key).add(object);
        } else {
            List<Object> lstObject = new ArrayList<>();
            lstObject.add(object);
            hmObject.put(key, lstObject);
        }
    }
}