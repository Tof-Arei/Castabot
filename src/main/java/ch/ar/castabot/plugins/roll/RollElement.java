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
public abstract class RollElement {
    protected String caption;
    protected int value;
    protected boolean negative;
    
    public RollElement(String caption, int value, boolean negative) {
        this.caption = caption;
        this.value = value;
        this.negative = negative;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getValue() {
        if (negative) {
            return value *= -1;
        } else {
            return value;
        }
    }
    
    public void setValue(int value) {
        this.value = value;
    }
    
    public boolean isNegative() {
        return negative;
    }
}
