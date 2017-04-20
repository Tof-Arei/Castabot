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

import ch.ar.castabot.CastabotClient;
import ch.ar.castabot.plugins.PluginException;
import ch.ar.castabot.plugins.PluginResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import org.json.JSONObject;

/**
 *
 * @author Arei
 */
public class Deck {
    private static final List<Card> lstCardsIn = new ArrayList<>();
    private static final List<Card> lstCardsOut = new ArrayList<>();
    private static String name;
    
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
        JSONObject dbDecks = new JSONObject(new String(rawDecks));
        
        if (dbDecks.has(name)) {
            this.name = name;
            fill(dbDecks.getJSONArray(name).toList());
            shuffle();
        } else {
            throw new PluginException("CARDS-1", "Le deck ["+name+"] n'éxiste pas.");
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
    
    public List<PluginResponse> drawAll(TextChannel source) throws PluginException {
        List<PluginResponse> ret = new ArrayList<>();
        List<Member> lstMember = new ArrayList<>();
        for (VoiceChannel voiceChannel : source.getGuild().getVoiceChannels()) {
            for (Member member : voiceChannel.getMembers()) {
                lstMember.add(member);
            }
        }
        
        if (lstCardsIn.size() >= lstMember.size()) {
            Collections.shuffle(lstMember);
            for (Member member : lstMember) {
                if (!member.getUser().isBot() && member.getOnlineStatus() == OnlineStatus.ONLINE) {
                    Card card = draw();
                    ret.add(new PluginResponse(card.print()+"\r\n"+CastabotClient.getCastabot().getConfig().getProperty("web_root")+"files/cards/default/"+card+".png", member.getUser()));
                }
            }
        } else {
            throw new PluginException("CARDS-4", "Plus assez de cartes pour tout les joueurs. Veuillez mélanger.");
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
}
