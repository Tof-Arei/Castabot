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
package ch.ar.castabot;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.security.auth.login.LoginException;
import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.exceptions.PermissionException;
import net.dv8tion.jda.core.exceptions.RateLimitedException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;

/**
 *
 * @author Arei
 */
public class CastabotClient extends ListenerAdapter {
    private static Castabot castabot;
    
    public static void main(String[] args) {
        try {    
            castabot = new Castabot();
            
            JDA jda = new JDABuilder(AccountType.BOT).setToken(castabot.getConfig().getProperty("bot_token")).buildBlocking();
            jda.addEventListener(new CastabotClient());
            
            System.out.println("Castabot™ prêt!");
        } catch (LoginException | IllegalArgumentException | InterruptedException | RateLimitedException ex) {
            Logger.getLogger(Castabot.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        Message message = event.getMessage();
        
        if (event.isFromType(ChannelType.PRIVATE)) {
            // Print bot PMs
            System.out.printf("[PM] %s: %s\n", event.getAuthor().getName(),
                                    event.getMessage().getContent());
            // Handle commands and stuff
            if (castabot.isMessageWorthAnsweringTo(message)) {
                try {
                    castabot.parseCommand(message);
                    System.gc();
                } catch (PermissionException ex) {
                    Logger.getLogger(Castabot.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            // Print channel messages
            System.out.printf("[%s][%s] %s: %s\n", event.getGuild().getName(),
                        event.getTextChannel().getName(), event.getMember().getEffectiveName(),
                        event.getMessage().getContent());
            // Handle commands and stuff
            if (castabot.isMessageWorthAnsweringTo(message)) {
                try {
                    castabot.parseCommand(message);
                    System.gc();
                } catch (PermissionException ex) {
                    Logger.getLogger(Castabot.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }
    
    public static Castabot getCastabot() {
        return castabot;
    }
}
