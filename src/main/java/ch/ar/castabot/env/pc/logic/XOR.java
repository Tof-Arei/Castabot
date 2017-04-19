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
package ch.ar.castabot.env.pc.logic;

import ch.ar.castabot.env.pc.Operation;
import ch.ar.castabot.env.pc.PseudoCode;

/**
 *
 * @author Arei
 */
public class XOR extends PseudoCode {
    public XOR(String formula) {
        super(formula);
    }

    @Override
    public String calculate() {
        String ret = "FALSE";
        String[] splitFormula = formula.split(";");
        int trueCount = 0;
        for (int i = 1; i <= ((splitFormula.length - 1) / 3); i++) {
            String cond1 = splitFormula[((i-1)*2)+i];
            String operator = splitFormula[((i-1)*2)+(i+1)];
            String cond2 = splitFormula[((i-1)*2)+(i+2)];
            
            Operation operation = new Operation(cond1, operator, cond2);
            if (operation.getLogicResult()) {
                ret = "TRUE";
                trueCount++;
            }
            if (trueCount > 1) {
                ret = "FALSE";
                break;
            }
        }
        return ret;
    }
}
