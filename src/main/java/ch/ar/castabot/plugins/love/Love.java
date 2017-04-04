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
package ch.ar.castabot.plugins.love;

import ch.ar.castabot.plugins.Plugin;
import ch.ar.castabot.plugins.PluginException;
import ch.ar.castabot.plugins.PluginResponse;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 *
 * @author Arei
 */
public class Love extends Plugin {
    public Love(String[] args, TextChannel source, User user) {
        super(args, source, user);
    }
    
    private boolean isKyuuji() {
        return user.getId().equals("251426460873654272");
    }
    
    private String love() throws PluginException {
        if (isKyuuji()) {
            return "Kyuukyuu!!! ❤ ❤ ❤";
        } else {
            throw new PluginException("LV0", "Désolé, mais je n'ai d'yeux que pour Kyuuji!");
        }
    }

    @Override
    public PluginResponse run() throws PluginException {
        String ret = null;
        switch (args[0]) {
            case "love":
                ret = love();
                break;
        }
        return new PluginResponse(ret);
    }
}