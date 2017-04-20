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
package ch.ar.castabot.env.pc.math;

import ch.ar.castabot.env.pc.PseudoCode;

/**
 *
 * @author Arei
 */
public class MUL extends PseudoCode {
    public MUL(String formula) {
        super(formula);
    }

    @Override
    public String calculate() {
        int total = 1;
        String[] splitFormula = formula.split(";");
        for (int i = 1; i < splitFormula.length;i++) {
            if (!splitFormula[i].equals("NULL")) {
                total *= Integer.parseInt(splitFormula[i]);
            } else {
                total *= 1;
            }
        }
        return String.valueOf(total);
    }
}
