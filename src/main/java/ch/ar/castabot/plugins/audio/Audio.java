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
package ch.ar.castabot.plugins.audio;

import ch.ar.castabot.CastabotClient;
import ch.ar.castabot.plugins.Plugin;
import ch.ar.castabot.plugins.PluginException;
import ch.ar.castabot.plugins.PluginResponse;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 *
 * @author christophe
 */
public class Audio extends Plugin {    
    public Audio(String[] args, TextChannel source, User user) {
        super(args, source, user);
    }
    
    private void play() {
        CastabotClient.getCastabot().loadAndPlay(source, args[1]);
    }
    
    private void pause() {
        CastabotClient.getCastabot().pause(source);
    }
    
    private void resume() {
        CastabotClient.getCastabot().resume(source);
    }
    
    private void loop() {
        CastabotClient.getCastabot().loop(source);
    }
    
    private void skip() {
        CastabotClient.getCastabot().skipTrack(source);
    }
    
    private void stop() {
        CastabotClient.getCastabot().stop(source);
    }
    
    @Override
    public PluginResponse run() throws PluginException {
        String ret = "Commande audio: [" + args[0] + "]";
        switch (args[0]) {
            case "play":
                play();
                break;
            case "pause":
                pause();
                break;
            case "resume":
                resume();
                break;
            case "loop":
                loop();
                break;
            case "skip":
                skip();
                break;
            case "stop":
                stop();
                break;
        }
        
        return new PluginResponse(ret);
    }
}