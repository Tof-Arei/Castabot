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
public class Token implements Comparable<Token> {
    private final String name;
    private final String desc;
    private final Integer[] values;
    private final String limit;
    
    public Token(String name, String desc, Integer[] values, String limit) {
        this.name = name;
        this.desc = desc;
        this.values = values;
        this.limit = limit;
    }
    
    public boolean hasValue(int value) {
        boolean ret = false;
        for (Integer val : values) {
            if (val == value) {
                ret = true;
                break;
            }
        }
        return ret;
    }
    
    @Override
    public int compareTo(Token token) {
        return name.compareTo(token.getName());
    }
    
    public String getName() {
        return name;
    }

    public Integer[] getValues() {
        return values;
    }

    public String getDesc() {
        return desc;
    }

    public String getLimit() {
        return limit;
    }
}
