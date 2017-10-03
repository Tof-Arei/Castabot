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
package ch.ar.castabot.plugins.admin;

import ch.ar.castabot.CastabotClient;
import ch.ar.castabot.plugins.Plugin;
import ch.ar.castabot.plugins.PluginException;
import ch.ar.castabot.plugins.PluginResponse;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author christophe
 */
public class Admin extends Plugin {
    public Admin(String[] args, String guildId, String channelId, String userId) {
        super(args, guildId, channelId, userId);
    }
    
    @Override
    public List<PluginResponse> run() throws PluginException {
        List<PluginResponse> ret = new ArrayList<>();
        
        switch (args[0]) {
            case "reload":
                CastabotClient.getCastabot().initSettings(CastabotClient.getGuild(guildId));
                ret.add(new PluginResponse("Configuration du serveur [" + CastabotClient.getGuild(guildId).getName() + "] rechargée.", userId));
                break;
        }
        
        return ret;
    }
}
