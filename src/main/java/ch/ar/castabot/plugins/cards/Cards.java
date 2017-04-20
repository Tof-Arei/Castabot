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
import ch.ar.castabot.plugins.Plugin;
import ch.ar.castabot.plugins.PluginException;
import ch.ar.castabot.plugins.PluginResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
/**
 *
 * @author Arei
 */
public class Cards extends Plugin {
    public Cards(String[] args, TextChannel source, User user) throws IOException, Exception {
        super(args, source, user);
    }
    
    private List<PluginResponse> drawAll() throws PluginException {
        List<PluginResponse> ret = new ArrayList<>();
        List<Member> lstMember = new ArrayList<>();
        for (VoiceChannel voiceChannel : source.getGuild().getVoiceChannels()) {
            for (Member member : voiceChannel.getMembers()) {
                lstMember.add(member);
            }
        }
        
        Deck deck = (Deck) CastabotClient.getCastabot().getPluginSettings().getValue("cards", "deck");
        if (deck.getNbCardsLeft() >= lstMember.size()) {
            Collections.shuffle(lstMember);
            for (Member member : lstMember) {
                if (!member.getUser().isBot() && member.getOnlineStatus() == OnlineStatus.ONLINE) {
                    Card card = deck.draw();
                    ret.add(new PluginResponse(card.print()+"\r\n"+CastabotClient.getCastabot().getConfig().getProperty("web_root")+"files/cards/default/"+card+".png", member.getUser()));
                }
            }
        } else {
            throw new PluginException("CARDS-4", "Plus assez de cartes pour tout les joueurs. Veuillez mélanger.");
        }
        
        return ret;
    }

    @Override
    public List<PluginResponse> run() throws PluginException {
        List<PluginResponse> ret = new ArrayList<>();
        Deck deck = (Deck) CastabotClient.getCastabot().getPluginSettings().getValue("cards", "deck");
        switch (args[0]) {
            case "deck" :
                CastabotClient.getCastabot().getPluginSettings().setValue("cards", "deck", new Deck(args[1]));
                ret.add(new PluginResponse("Jeu de cartes initié avec le deck ["+deck+"] et mélangé.", user));
                break;
            case "draw" :
                Card card = deck.draw();
                ret.add(new PluginResponse(card.print()+"\r\n"+CastabotClient.getCastabot().getConfig().getProperty("web_root")+"files/cards/default/"+card+".png", user));
                break;
            case "drawall":
                ret = drawAll();
                break;
            case "shuffle" :
                deck.shuffle();
                ret.add(new PluginResponse("Jeu de cartes mélangé.", user));
                break;
        }
        
        return ret;
    }
}
