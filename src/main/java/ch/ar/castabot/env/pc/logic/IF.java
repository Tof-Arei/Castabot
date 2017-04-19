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
