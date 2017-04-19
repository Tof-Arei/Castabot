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

/**
 *
 * @author Arei
 */
public class Rolltype {
    private final String name;
    private final String desc;
    private final boolean dflt;
    private final String criticalFailure;
    private final String criticalSuccess;
    private final String bonusDice;
    private final boolean explode;
    private final String explodeAction;
    private final char arg;
    private final String format;
    private final String total;

    public Rolltype(String name, String desc, boolean dflt, String criticalFailure, String criticalSuccess, 
            String bonusDice, boolean explode, String explodeAction, char arg, String format, String total) {
        this.name = name;
        this.desc = desc;
        this.dflt = dflt;
        this.criticalFailure = criticalFailure;
        this.criticalSuccess = criticalSuccess;
        this.bonusDice = bonusDice;
        this.explode = explode;
        this.explodeAction = explodeAction;
        this.arg = arg;
        this.format = format;
        this.total = total;
    }

    public String getName() {
        return name;
    }
    
    public String getDesc() {
        return desc;
    }

    public boolean isDefault() {
        return dflt;
    }

    public String getCriticalFailure() {
        return criticalFailure;
    }

    public String getCriticalSuccess() {
        return criticalSuccess;
    }
    
    public String getBonusDice() {
        return bonusDice;
    }
    
    public boolean canExplode() {
        return explode;
    }
    
    public String getExplodeAction() {
        return explodeAction;
    }
    
    public char getArg() {
        return arg;
    }   
    
    public String getFormat() {
        return format;
    }

    public String getTotal() {
        return total;
    }
}
