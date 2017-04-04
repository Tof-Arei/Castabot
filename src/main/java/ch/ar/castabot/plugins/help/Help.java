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
package ch.ar.castabot.plugins.help;

import ch.ar.castabot.plugins.Plugin;
import ch.ar.castabot.plugins.PluginException;
import ch.ar.castabot.plugins.PluginResponse;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 *
 * @author Arei
 */
public class Help extends Plugin {
    public Help(String[] args, TextChannel source, User user) {
        super(args, source, user);
    }
    
    @Override
    public PluginResponse run() throws PluginException {
        String ret = "Commandes disponibles: \r\n";
        ret += "- audio : Permet de jouer de la musique sur le canal audio General. \r\n";
        ret += "- cards : Permet d'utiliser un jeu de cartes virtuel. \r\n";
        ret += "- dice : Permet d'utiliser des dés virtuels. \r\n";
        ret += "- help : affice la liste des commandes.  \r\n";
        ret += "Nb: Utiliser une commande avec l'argument -h (ou --help) permet d'afficher les informations sur la commande. \r\n";
        ret += "Utiliser le caractère & (ou lancer la commande via message privé) permet d'effectuer un lancement de commande secret. \r\n";
        
        throw new PluginException("HELP", ret);
    }
}
