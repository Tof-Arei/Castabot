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
package ch.ar.castabot.env.pc.plugins.roll;

import ch.ar.castabot.env.pc.PseudoCode;
import ch.ar.castabot.plugins.roll.Rules;
import ch.ar.castabot.plugins.roll.Token;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Arei
 */
public class RULE extends PseudoCode {
    public RULE(String formula) {
        super(formula);
    }
    
    private Rules getRules(int index) {
        Rules ret = null;
        
        Map<Integer, Object> lstRulesObj = getAllFixedObj();
        if (lstRulesObj.size() > 0 && index <= lstRulesObj.size()) {
            ret = (Rules) lstRulesObj.get(index);
        }
        
        return ret;
    }
    
    public Map<Integer, Object> getAllFixedObj() {
        return lstObject.get(Rules.class.getName());
    }
    
    private String help(int index) {
        Rules rules = getRules(index);
        return rules.getHelp();
    }
    
    public String name(int index) {
        Rules rules = getRules(index);
        return rules.getName();
    }
    
    private String tokens(String query) {
        //String ret = "[" + query + "]\r\n";
        String ret = "\r\n";
        String[] splitQuery = query.split("/");
        Rules rules = getRules(0);
        int argLimit = 1;
        if (query.split(":").length > 1) {
            argLimit = Integer.parseInt(query.split(":")[1]);
            splitQuery = query.replace(":"+argLimit, "").split("/");
        }
        List<Token> lstToken = new ArrayList<>();
        for (int i = 0; i < splitQuery.length; i++) {
            lstToken.add(rules.getToken(Integer.parseInt(splitQuery[i])));
        }
        
        Collections.sort(lstToken, new Comparator<Token>() {
            @Override
            public int compare(Token t1, Token t2) {
                return t1.getName().compareTo(t2.getName());
            }
        });
        
        Map<Token, Integer> hmTokens = new HashMap<>();
        int tokenCount = 1;
        Token currentToken = lstToken.get(0);
        for (int i = 1; i < lstToken.size(); i++) {
            Token nextToken = lstToken.get(i);
            if (currentToken.getDesc().equals(nextToken.getDesc())) {
                tokenCount++;
            } else {
                hmTokens.put(currentToken, tokenCount);
                currentToken = nextToken;
                tokenCount = 1;
            }
        }
        hmTokens.put(currentToken, tokenCount);
        hmTokens = handleTokenLimits(hmTokens, argLimit);
        
        for (Map.Entry entry : hmTokens.entrySet()) {
            Token token = (Token) entry.getKey();
            ret += "  (" + entry.getValue()  + ") " + token.getDesc() + "\r\n";
        }
        
        return ret;
    }
    
    private Map<Token, Integer> handleTokenLimits(Map<Token, Integer> hmTokens, int argLimit) {
        Map<Token, Integer> ret = new HashMap<>(hmTokens);
        for (Map.Entry entry : ret.entrySet()) {
            Token token = (Token) entry.getKey();
            int nbToken = (int) entry.getValue();
            int diff = nbToken - (token.getLimit() * argLimit);
            if (diff > 0) {
                Rules rules = getRules(0);
                ret.put(token, ret.get(token)-diff);
                for (int i = 0; i < diff; i++) {
                    int newTokenId = (ThreadLocalRandom.current()).nextInt(rules.getTokens().size()) + rules.getMinTokenValue();
                    Token newToken = rules.getToken(newTokenId);
                    if (ret.get(newToken) != null) {
                        ret.put(newToken, ret.get(newToken)+1);
                    } else {
                        ret.put(newToken, 1);
                    }
                }

                return handleTokenLimits(ret, argLimit);
            }
        }
        return ret;
    }
    
    @Override
    public String calculate() {
        String ret = "NULL";
        String[] splitFormula = formula.split(";");
        switch (splitFormula[1]) {
            case "help":
                ret = help(Integer.parseInt(splitFormula[2]));
                break;
            case "name":
                ret = name(Integer.parseInt(splitFormula[2]));
                break;
            case "tokens":
                ret = tokens(splitFormula[2]);
                break;
        }
        
        return ret;
    }
}
