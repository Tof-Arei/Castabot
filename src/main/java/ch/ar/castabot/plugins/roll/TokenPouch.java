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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Arei
 */
public class TokenPouch {
    List<Token> lstTokenIn = new ArrayList<>();
    List<Token> lstTokenOut = new ArrayList<>();
    
    public boolean hasToken(Token token) {
        return hasToken(token, true);
    }
    
    public boolean hasToken(Token token, boolean in) { 
        return countToken(token, in) > 0;
    }
    
    public int countToken(Token token) {
        return countToken(token, true);
    }
    
    public int countToken(Token token, boolean in) {
        int ret = 0;
        for (Token iToken : getTokens(in)) {
            if (iToken.compareTo(token) == 0) {
                ret++;
            }
        }
        return ret;
    }
    
    private List<Token> getTokens(boolean in) {
        List<Token> lstToken;
        if (in) {
            lstToken = lstTokenIn;
        } else {
            lstToken = lstTokenOut;
        }
        return lstToken;
    }
    
    public Token getRandomToken() {
        Token ret = null;
        if (lstTokenIn.size() > 0) {
            int val = (ThreadLocalRandom.current()).nextInt(lstTokenIn.size()) + 0;
            ret = lstTokenIn.get(val);
            removeToken(ret);
        }
        return ret;
    }
    
    public int countTokens() {
        return countTokens(true);
    }
    
    public int countTokens(boolean in) {
        return getTokens(in).size();
    }
    
    public void addToken(Token token) {
        lstTokenIn.add(token);
    }
    
    public void addTokens(List<Token> lstToken) {
        lstTokenIn.addAll(lstToken);
    }
    
    public void removeToken(Token token) {
        lstTokenIn.remove(token);
        lstTokenOut.add(token);
    }
}
