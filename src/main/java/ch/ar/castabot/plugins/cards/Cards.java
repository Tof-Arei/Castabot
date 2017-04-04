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

import ch.ar.castabot.plugins.Plugin;
import ch.ar.castabot.plugins.PluginException;
import ch.ar.castabot.plugins.PluginResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.TextChannel;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Arei
 */
public class Cards extends Plugin {
    private final ArrayList<Card> lstCardsIn = new ArrayList<>();
    private final ArrayList<Card> lstCardsOut = new ArrayList<>();
    private String deck;
    
    public Cards(String[] args, TextChannel source, User user) throws IOException, Exception {
        super(args, source, user);
        load();
    }
    
    private void load() throws IOException, Exception {
        byte[] rawCards = Files.readAllBytes(Paths.get("data/plugins/cards/cards.json"));
        if (rawCards.length > 1) {
            JSONObject dbCards = new JSONObject(new String(rawCards));
            
            if (dbCards.has("in")) {
                JSONArray inCards = dbCards.getJSONArray("in");
                fill(inCards.toList());
                if (dbCards.has("out")) {
                    JSONArray outCards = dbCards.getJSONArray("out");
                    fill(outCards.toList(), true);
                }
            }
        }
    }
    
    private void fill(List<Object> rawDeck) {
        fill(rawDeck, false);
    }
    
    private void fill(List<Object> rawDeck, boolean out) {
        for (Object rawCard : rawDeck) {
            String[] card = ((String) rawCard).split("-");
            int color = Integer.parseInt(card[0]);
            int value = Integer.parseInt(card[1]);
            if (!out) {
                lstCardsIn.add(new Card(color, value));
            } else {
                lstCardsOut.add(new Card(color, value));
            }
        }
    }
    
    private void save() throws FileNotFoundException {
        PrintWriter out = new PrintWriter("data/plugins/cards/cards.json");
        JSONObject dbCards = new JSONObject();
        JSONArray dbCardsIn = new JSONArray();
        for (Card card : lstCardsIn) {
            dbCardsIn.put(card.toString());
        }
        JSONArray dbCardsOut = new JSONArray();
        for (Card card : lstCardsOut) {
            dbCardsOut.put(card.toString());
        }
        dbCards.put("in", dbCardsIn);
        dbCards.put("out", dbCardsOut);
        out.write(dbCards.toString());
        out.close();
    }
    
    private void init(String deck) throws PluginException, IOException {
        byte[] rawDecks = Files.readAllBytes(Paths.get("data/plugins/cards/decks.json"));
        JSONObject dbDecks = new JSONObject(new String(rawDecks));
        
        if (dbDecks.has(deck)) {
            this.deck = deck;
            fill(dbDecks.getJSONArray(deck).toList());
            shuffle();
        } else {
            throw new PluginException("CARDS-1", "Le deck ["+deck+"] n'éxiste pas.");
        }
    }
    
    private Card draw() throws FileNotFoundException, PluginException {
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
    
    private void shuffle() throws PluginException {
        if (lstCardsIn.size() > 0 || lstCardsOut.size() > 0) {
            lstCardsIn.addAll(lstCardsOut);
            lstCardsOut.clear();
            Collections.shuffle(lstCardsIn);
        } else {
            throw new PluginException("CARDS-2", "Le deck est vide, veuillez le réinitialiser.");
        }
    }

    @Override
    public PluginResponse run() throws PluginException {
        String ret = null;
        Card card = null;
        try {
            switch (args[0]) {
                case "init" :
                    if (args.length > 1) {
                        init(args[1]);
                    } else {
                        init("default");
                    }
                    ret = "Jeu de cartes initié avec le deck ["+deck+"] et mélangé.";
                    save();
                    break;
                case "draw" :
                    card = draw();
                    ret = card.print();
                    save();
                    break;
                case "shuffle" :
                    shuffle();
                    ret = "Jeu de cartes mélangé.";
                    save();
                    break;
            }
        } catch (IOException ex) {
            Logger.getLogger(Cards.class.getName()).log(Level.SEVERE, null, ex);
        }
        if (card != null) {
            return new PluginResponse(ret, card.getFile());
        } else {
            return new PluginResponse(ret);
        }
    }
}