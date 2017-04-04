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
package ch.ar.castabot.plugins.dice;

import ch.ar.castabot.plugins.Plugin;
import ch.ar.castabot.plugins.PluginException;
import ch.ar.castabot.plugins.PluginResponse;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.SplittableRandom;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.TextChannel;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

/**
 *
 * @author Arei
 */
public class Dice extends Plugin {
    public Dice(String[] args, TextChannel source, User user) {
        super(args, source, user);
    }
    
    @Override
    public PluginResponse run() throws PluginException {
        String str = args[0].replace("!", "");
        ArrayList<String> lstDice = new ArrayList<>();
        String d = "+";
        for (char ch : str.toCharArray()) {
            if (ch == '+' || ch == '-') {
                lstDice.add(d);
                d = "";
                d += ch;
            } else {
                d += ch;
            }
        }
        lstDice.add(d);
        
        str = "";
        String retStr = "";
        for (String rawDice : lstDice) {
           String bound = rawDice.toCharArray()[0]+"";
           String[] dice = rawDice.replace(bound, "").split("d");
           if (dice.length > 1) {
               retStr += "["+rawDice+"=";
                for (int i = 0; i < Integer.parseInt(dice[0]); i++) {
                    try {
                        int val = (new SplittableRandom()).nextInt(Integer.parseInt(dice[1])) + 1;
                        str += bound+val;
                        retStr += "("+val+")";
                    } catch (IllegalArgumentException e) {
                        throw new PluginException("DICE1", "["+args[0]+"] n'est pas un jet valide.");
                    }
                }
                retStr += "]";
           } else {
               str += bound+dice[0];
               retStr += "["+bound+dice[0]+"]";
           }
        }
        
        Evaluator eval = new Evaluator();
        String total = "0";
        try {
            total = eval.evaluate(str);
        } catch (EvaluationException ex) {
            throw new PluginException("DICE1", "[" + args[0] + "] n'est pas un jet valide.");
        }
        
        DecimalFormat df = new DecimalFormat("###.#");
        return new PluginResponse("Lancer: {"+retStr+"} \r\n Total: ["+df.format(Double.parseDouble(total))+"]");
    }
}
