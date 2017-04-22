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
import ch.ar.castabot.env.audio.PlayerManager;
import ch.ar.castabot.plugins.Plugin;
import ch.ar.castabot.plugins.PluginException;
import ch.ar.castabot.plugins.PluginResponse;
import java.util.ArrayList;
import java.util.List;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 *
 * @author Arei
 */
public class Audio extends Plugin {
    private PlayerManager playerManager = (PlayerManager) CastabotClient.getCastabot().getPluginSettings().getValue("audio", "playerManager");
    
    public Audio(String[] args, TextChannel source, User user) {
        super(args, source, user);
    }
    
    @Override
    public List<PluginResponse> run() throws PluginException {
        List<PluginResponse> ret = new ArrayList<>();
        switch (args[0]) {
            case "play":
                playerManager.loadAndPlay(source, args[1]);
                break;
            case "pause":
                ret.add(new PluginResponse(playerManager.pause(source), user));
                break;
            case "resume":
                ret.add(new PluginResponse(playerManager.resume(source), user));
                break;
            case "loop":
                ret.add(new PluginResponse(playerManager.loop(source), user));
                break;
            case "skip":
                ret.add(new PluginResponse(playerManager.skip(source), user));
                break;
            case "stop":
                ret.add(new PluginResponse(playerManager.stop(source), user));
                break;
        }
        return ret;
    }
}
