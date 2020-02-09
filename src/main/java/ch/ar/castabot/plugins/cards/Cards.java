/*
 *                GLWT(Good Luck With That) Public License
 *                  Copyright (c) Everyone, except Author
 * 
 * Everyone is permitted to copy, distribute, modify, merge, sell, publish,
 * sublicense or whatever they want with this software but at their OWN RISK.
 * 
 *                             Preamble
 * 
 * The author has absolutely no clue what the code in this project does.
 * It might just work or not, there is no third option.
 * 
 * 
 *                 GOOD LUCK WITH THAT PUBLIC LICENSE
 *    TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION, AND MODIFICATION
 * 
 *   0. You just DO WHATEVER YOU WANT TO as long as you NEVER LEAVE A
 * TRACE TO TRACK THE AUTHOR of the original product to blame for or hold
 * responsible.
 * 
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * 
 * Good luck and Godspeed.
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
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.VoiceChannel;

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
