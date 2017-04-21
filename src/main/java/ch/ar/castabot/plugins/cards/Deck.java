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
package ch.ar.castabot.plugins.cards;

import ch.ar.castabot.plugins.PluginException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.json.JSONObject;

/**
 *
 * @author Arei
 */
public class Deck {
    private String name;
    private String activateAction;
    private String imgDeck;
     
    private final List<Card> lstCardsIn = new ArrayList<>();
    private final List<Card> lstCardsOut = new ArrayList<>();
   
    public Deck(String name) throws PluginException {
        this.name = name;
        try {
            init();
        } catch (IOException ex) {
            throw new PluginException("CARDS-1", "Le deck ["+name+"] n'éxiste pas.");
        }
    }
    
    private void init() throws PluginException, IOException {
        byte[] rawDecks = Files.readAllBytes(Paths.get("data/plugins/cards/decks.json"));
        JSONObject objDeck = new JSONObject(new String(rawDecks));
        
        if (objDeck.has(name)) {
            fill(objDeck.getJSONObject(name));
            shuffle();
        } else {
            throw new PluginException("CARDS-1", "Le deck ["+name+"] n'éxiste pas.");
        }
    }
    
    private void fill(JSONObject objDeck) {
        fill(objDeck, false);
    }
    
    private void fill(JSONObject objDeck, boolean out) {
        activateAction = objDeck.getString("activate_action");
        imgDeck = objDeck.getString("img_deck");
        for (String format : objDeck.getString("format").split(";")) {
            String[] splitFormat = format.split("-");
            for (int i = 0; i < Integer.parseInt(splitFormat[1]); i++) {
                String[] rawCard = new String[3];
                rawCard[0] = splitFormat[0];
                rawCard[1] = (rawCard[0].equals("0")) ? "0" : String.valueOf(i+1);
                rawCard[2] = "";
                
                for (int j = 0; j < objDeck.getJSONArray("descs").length(); j++) {
                    JSONObject objDesc = objDeck.getJSONArray("descs").getJSONObject(j);
                    if (objDesc.has(rawCard[0]+"-"+rawCard[1])) {
                        rawCard[2] = objDesc.getString(rawCard[0]+"-"+rawCard[1]);
                    }
                }
                
                Card card = new Card(Integer.parseInt(rawCard[0]), Integer.parseInt(rawCard[1]), rawCard[2]);
                if (!out) {
                    lstCardsIn.add(card);
                } else {
                    lstCardsOut.add(card);
                }
            }
        }
    }
    
    public Card draw() throws PluginException {
        Card ret = null;
        if (lstCardsIn.size() > 0) {
            Card card = lstCardsIn.get(0);
            lstCardsIn.remove(card);
            lstCardsOut.add(card);
            ret = card;
        } else {
            throw new PluginException("CARDS-3", "Plus de cartes, veuillez mélanger ou réinitialiser le deck.");
        }
        
        return ret;
    }
    
    public void shuffle() throws PluginException {
        if (lstCardsIn.size() > 0 || lstCardsOut.size() > 0) {
            lstCardsIn.addAll(lstCardsOut);
            lstCardsOut.clear();
            Collections.shuffle(lstCardsIn);
        } else {
            throw new PluginException("CARDS-2", "Le deck est vide, veuillez le réinitialiser.");
        }
    }
    
    public int getNbCardsLeft() {
        return lstCardsIn.size();
    }
    
    public String getName() {
        return name;
    }
    
    public String getActivateAction() {
        return activateAction;
    }
    
    public String getImgDeck() {
        return imgDeck;
    }
}
