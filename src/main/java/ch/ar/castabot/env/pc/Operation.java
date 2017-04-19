/*
 * Copyright 2017 Arei.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.ar.castabot.env.pc;

/**
 *
 * @author Arei
 */
public class Operation {
    private final String cond1;
    private final String operator;
    private final String cond2;
    
    public Operation(String cond1, String operator, String cond2) {
        this.cond1 = cond1;
        this.operator = operator;
        this.cond2 = cond2;
    }
    
    private boolean isValid() {
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
        if (isValid()) {
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
        }
        return ret;
    }
    
    public double getMathResult() {
        double ret = 0;
        if (isValid()) {
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
