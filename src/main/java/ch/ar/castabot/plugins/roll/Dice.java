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
package ch.ar.castabot.plugins.roll;

import ch.ar.castabot.CastabotClient;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Arei
 */
public class Dice extends RollElement {
    private boolean bonus = false;
    private final int max;
    
    public Dice(int max, boolean negative) {
        super("", 0, negative);
        this.max = max;
    }
    
    public Dice(String caption, int max, boolean negative) {
        super(caption, 0, negative);
        this.max = max;
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
            Logger.getLogger(Roll.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Roll.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return ret;
    }
    
    public void roll() {
        value = (ThreadLocalRandom.current()).nextInt(max) + 1;
        //value = generateWebInteger(1, max);
    }

    public int getMax() {
        return max;
    }
    
    public boolean isBonus() {
        return bonus;
    }
    
    public void setBonus(boolean bonus) {
        this.bonus = bonus;
    }
}
