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
import ch.ar.castabot.env.pc.PseudoCode;
import ch.ar.castabot.plugins.Plugin;
import ch.ar.castabot.plugins.PluginException;
import ch.ar.castabot.plugins.PluginResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;

/**
 *
 * @author Arei
 */
public class Cards extends Plugin {
    public Cards(String[] args, String guildId, String channelId, String userId) {
        super(args, guildId, channelId, userId);
    }
    
    private List<PluginResponse> drawAll() throws PluginException {
        List<PluginResponse> ret = new ArrayList<>();
        List<Member> lstMember = new ArrayList<>();
        String targetChannel = null;
        if (args.length > 1) {
            targetChannel = args[1];
        }
        
        for (VoiceChannel voiceChannel : CastabotClient.getGuild(guildId).getVoiceChannels()) {
            boolean draw = true;
            if (targetChannel != null) {
                if (!targetChannel.equals(voiceChannel.getName())) {
                    draw = false;
                }
            }
            if (draw) {
                for (Member member : voiceChannel.getMembers()) { 
                    if (!member.getUser().isBot()) {
                        lstMember.add(member);
                    }
                }
            }
        }
        
        Deck deck = (Deck) CastabotClient.getCastabot().getPluginSettings(guildId).getValue("cards", "deck");
        if (deck.getNbCardsLeft() >= lstMember.size()) {
            Collections.shuffle(lstMember);
            for (Member member : lstMember) {
                Card card = deck.draw();
                ret.add(new PluginResponse(card.print()+"\r\n"+CastabotClient.getCastabot().getConfig().getProperty("web_root")+"files/cards/default/"+card+".png", member.getUser().getId()));
            }
        } else {
            throw new PluginException("CARDS-4", "Plus assez de cartes pour tout les joueurs. Veuillez mélanger.");
        }
        
        return ret;
    }
    
    private String init(String deckName) throws PluginException {
        Deck deck = new Deck(deckName);
        CastabotClient.getCastabot().getPluginSettings(guildId).setValue("cards", "deck", deck);
        String ret = "Jeu de cartes initié avec le deck ["+deck.getName()+"] et mélangé.";
        
        PseudoCode pc = new PseudoCode(deck.getActivateAction());
        String eval = pc.evaluate();
        if (eval != null) {
            ret += "\r\n" + eval;
        }
        return ret;
    }

    @Override
    public List<PluginResponse> run() throws PluginException {
        List<PluginResponse> ret = new ArrayList<>();
        Deck deck = (Deck) CastabotClient.getCastabot().getPluginSettings(guildId).getValue("cards", "deck");
        switch (args[0]) {
            case "deck" :
                if (args.length > 1) {
                    ret.add(new PluginResponse(init(args[1]), userId));
                }
                break;
            case "draw" :
                Card card = deck.draw();
                ret.add(new PluginResponse(card.print()+"\r\n"+card.getUrl(), userId));
                break;
            case "drawall":
                ret = drawAll();
                break;
            case "shuffle" :
                deck.shuffle();
                ret.add(new PluginResponse("Jeu de cartes mélangé.", userId));
                break;
        }
        
        return ret;
    }
}
