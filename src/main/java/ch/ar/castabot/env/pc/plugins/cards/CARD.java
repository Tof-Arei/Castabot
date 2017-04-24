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
package ch.ar.castabot.env.pc.plugins.cards;

import ch.ar.castabot.CastabotClient;
import ch.ar.castabot.env.pc.PseudoCode;
import ch.ar.castabot.plugins.PluginException;
import ch.ar.castabot.plugins.cards.Card;
import ch.ar.castabot.plugins.cards.Deck;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.dv8tion.jda.core.entities.Guild;

/**
 *
 * @author Arei
 */
public class CARD extends PseudoCode {
    public CARD(String formula) {
        super(formula);
    }
    
    private String init(String deckName) {
        String ret = "";
        try {
            Guild guild = (Guild) getObject("Guild", 0);
            Deck deck = new Deck(deckName);
            deck.shuffle();
            CastabotClient.getCastabot().getPluginSettings(guild).setValue("cards", "deck", deck);
            ret += "Jeu de cartes initié avec le deck ["+deck.getName()+"] et mélangé.";
        } catch (PluginException ex) {
            Logger.getLogger(CARD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }
    
    private String shuffle() {
        String ret = "";
        try {
            Guild guild = (Guild) getObject("Guild", 0);
            Deck deck = (Deck) CastabotClient.getCastabot().getPluginSettings(guild).getValue("cards", "deck");
            deck.shuffle();
            ret += "Jeu de cartes mélangé.";
        } catch (PluginException ex) {
            Logger.getLogger(CARD.class.getName()).log(Level.SEVERE, null, ex);
        }
        return ret;
    }
    
    @Override
    public String calculate() {
        String ret = "";
        String[] splitFormula = formula.split(";");
        Card card = null;
        switch (splitFormula[1]) {
            case "init":
                ret = init(splitFormula[2]);
                break;
            case "shuffle":
                ret = shuffle();
                break;
        }
        
        return ret;
    }
}
