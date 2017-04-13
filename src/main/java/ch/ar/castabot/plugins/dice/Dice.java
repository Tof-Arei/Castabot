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

import ch.ar.castabot.CastabotClient;
import ch.ar.castabot.plugins.Plugin;
import ch.ar.castabot.plugins.PluginException;
import ch.ar.castabot.plugins.PluginResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.TextChannel;
import net.sourceforge.jeval.EvaluationException;
import net.sourceforge.jeval.Evaluator;

/**
 *
 * @author Arei
 */
public class Dice extends Plugin {
    private Rules rules = new Rules("default");
    
    
    public Dice(String[] args, TextChannel source, User user) {
        super(args, source, user);
    }
    
    private void rules(String rules) {
        this.rules = new Rules(rules);
    }
    
    private int generateWebInteger(int min, int max) {
        int ret = 0;
        try {
            String strUrl = CastabotClient.getCastabot().getConfig().getProperty("web_root") + "rand.php?min=" + String.valueOf(min) + "&max=" + String.valueOf(max);
            strUrl = strUrl.replace("$min", String.valueOf(min)).replace("$max", String.valueOf(max));
            URL url = new URL(strUrl);
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
            
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                ret = Integer.parseInt(inputLine);
            }
            in.close();
        } catch (MalformedURLException ex) {
            Logger.getLogger(Dice.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Dice.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return ret;
    }
    
    private String roll() throws PluginException {
        ArrayList<PluginResponse> ret = new ArrayList<>();
        String str = args[0].replaceAll("D", "d");
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
                        //int val = (new SplittableRandom()).nextInt(Integer.parseInt(dice[1])) + 1;
                        int val = generateWebInteger(1, Integer.parseInt(dice[1]));
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
        return "Lancer: {"+retStr+"} \r\n Total: ["+df.format(Double.parseDouble(total))+"]";
    }
    
    @Override
    public ArrayList<PluginResponse> run() throws PluginException {
        ArrayList<PluginResponse> ret = new ArrayList<>();
        switch (args[0]) {
            case "rules":
                if (args.length > 1) {
                    rules(args[1]);
                } else {
                    rules("default");
                }
                
                ret.add(new PluginResponse("Activation des règles de roll ["+rules.getName()+"].", user));
                break;
            default:
                ret.add(new PluginResponse(roll(), user));
        }
        return ret;
    }
}
