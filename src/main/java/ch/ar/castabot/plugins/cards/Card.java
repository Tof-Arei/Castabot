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
import java.io.File;

/**
 *
 * @author Arei
 */
public class Card {
    // Mmmmmmmmhhh, Java <3
    public static final int VALUE_JOKER = 0;
    public static final String VALUE_JOKER_S = "Joker";
    public static final int VALUE_ACE = 1;
    public static final String VALUE_ACE_S = "As";
    public static final int VALUE_JACK = 11;
    public static final String VALUE_JACK_S = "Valet";
    public static final int VALUE_QUEEN = 12;
    public static final String VALUE_QUEEN_S = "Dame";
    public static final int VALUE_KING = 13;
    public static final String VALUE_KING_S = "Roi";
    
    public static final int COLOR_SPADE = 1;
    public static final String COLOR_SPADE_S = "Pique";
    public static final int COLOR_CLUB = 2;
    public static final String COLOR_CLUB_S = "Trèfle";
    public static final int COLOR_HEART = 3;
    public static final String COLOR_HEART_S = "Coeur";
    public static final int COLOR_DIAMOND = 4;
    public static final String COLOR_DIAMOND_S = "Carreau";
    
    private final Deck deck;
    
    private final int color;
    private final int value;
    private final String desc;
    
    public Card(Deck deck, int color, int value, String desc) {
        this.deck = deck;
        this.color = color;
        this.value = value;
        this.desc = desc;
    }
    
    public String print() {
        String ret = "";
        switch (value) {
            case VALUE_JOKER:
                ret += VALUE_JOKER_S;
                break;
            case VALUE_ACE:
                ret += VALUE_ACE_S + " de ";
                break;
            case VALUE_JACK:
                ret += VALUE_JACK_S + " de ";
                break;
            case VALUE_QUEEN:
                ret += VALUE_QUEEN_S + " de ";
                break;
            case VALUE_KING:
                ret += VALUE_KING_S + " de ";
                break;
            default:
                ret += String.valueOf(value) + " de ";
                break;
        }
        switch (color) {
            case COLOR_SPADE:
                ret += COLOR_SPADE_S;
                break;
            case COLOR_CLUB:
                ret += COLOR_CLUB_S;
                break;
            case COLOR_HEART:
                ret += COLOR_HEART_S;
                break;
            case COLOR_DIAMOND:
                ret += COLOR_DIAMOND_S;
                break;
        }
        if (!desc.equals("")) {
            ret += "\r\n" + desc;
        }
        return ret;
    }
    
    public File getFile() {
        return new File("data/plugins/cards/" + deck.getImgDeck() + "/" + this + ".png");
    }
    
    public String getUrl() {
        String webRoot = CastabotClient.getCastabot().getConfig().getProperty("web_root");
        return webRoot + "files/cards/" + deck.getImgDeck() + "/" + this + ".png";
    }
    
    public String getDesc() {
        return desc;
    }

    public int getColor() {
        return color;
    }

    public int getValue() {
        return value;
    }
    
    @Override
    public String toString() {
        return color+"-"+value;
    }
}
