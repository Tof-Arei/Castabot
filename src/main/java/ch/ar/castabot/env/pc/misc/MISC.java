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
package ch.ar.castabot.env.pc.misc;

import ch.ar.castabot.CastabotClient;
import ch.ar.castabot.env.pc.PseudoCode;
import net.dv8tion.jda.core.entities.Guild;

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
        return CastabotClient.getAvailablePlayers(guild, roleName);
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
