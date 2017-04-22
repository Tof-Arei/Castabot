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
package ch.ar.castabot.plugins;

import ch.ar.castabot.Castabot;
import ch.ar.castabot.CastabotClient;
import ch.ar.castabot.env.pc.PseudoCode;
import ch.ar.castabot.env.permissions.CommandPermission;
import ch.ar.castabot.env.permissions.RolePermission;
import ch.ar.castabot.env.permissions.UserPermission;
import ch.ar.castabot.plugins.roll.Rules;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author Arei
 */
public class Command {
    private final Guild guild;
    private final User user;
    private final Message message;
    
    private String command = null;
    private String[] args = null;
    private boolean secret = false;
    
    private final JSONObject settings = CastabotClient.getCastabot().getSettings();
    
    public Command(Guild guild, User user, Message message) {
        this.guild = guild;
        this.user = user;
        this.message = message;
        parseCommand();
    }
    
    // Parse the message, extract the command and arguments
    private void parseCommand() {
        String strChar = message.getContent().substring(0, 1);
        String[] strArgs = message.getContent().substring(1).split(" ");
        
        // Check if the command is secret
        JSONArray cmdChars = settings.getJSONArray("cmd_chars");
        for (int i = 0; i < cmdChars.length(); i++) {
            JSONArray cmdChar = cmdChars.getJSONArray(i);
            if (strChar.equals(cmdChar.getString(1))) {
                if (cmdChar.getString(0).equals("secret")) {
                    secret = true;
                    break;
                }
            }
        }
        
        // Check if the command is a short
        boolean isShort = false;
        JSONArray cmdShorts = settings.getJSONArray("cmd_shorts");
        for (int i = 0; i < cmdShorts.length(); i++) {
            JSONArray cmdShort = cmdShorts.getJSONArray(i);
            String shortCommand = message.getContent().substring(1);
            Pattern patShort = Pattern.compile(cmdShort.getString(1));
            Matcher matShort = patShort.matcher(shortCommand);
            
            if (matShort.find()) {
                isShort = true;
                command = cmdShort.getString(0);
                
                args = new String[strArgs.length];
                for (int j = 0; j < strArgs.length; j++) {
                    args[j] = strArgs[j];
                }
                
                break;
            }
        }
        
        if (!isShort) {
            // Extract command and arguments
            command = strArgs[0];
            args = new String[strArgs.length-1];
            for (int j = 1; j < strArgs.length; j++) {
                args[j-1] = strArgs[j];
            }
        }
    }
    
    public List<PluginResponse> execute() {
        List<PluginResponse> ret = new ArrayList<>();
        // Check if the command exists
        JSONObject permCommands = settings.getJSONObject("commands");
        if (permCommands.has(command)) {
            if (checkPermissions()) {
                ret = executePlugin();
            }
        }
        return ret;
    }
    
    private List<PluginResponse> executePlugin() {
        List<PluginResponse> ret = new ArrayList<>();
        // Heres comes the ugly bit
        try {
            if (args == null) {
                args = new String[]{};
            }    
            
            String className = command.substring(0, 1).toUpperCase() + command.substring(1);
            Class<?> clazz = Class.forName("ch.ar.castabot.plugins."+command+"."+className);
            Class[] types = {String[].class, TextChannel.class, User.class};
            Constructor<?> constructor = clazz.getConstructor(types);
            Object[] classArgs = {args, message.getChannel(), user};
            Plugin instance = (Plugin) constructor.newInstance(classArgs);
            
            if (args.length > 0) {
                if (args[0].equals("-h") || args[0].equals("--help")) {
                    JSONObject permCommands = settings.getJSONObject("commands");
                    Rules rules = (Rules) CastabotClient.getCastabot().getPluginSettings().getValue("roll", "rules");
                    PseudoCode pc = new PseudoCode(permCommands.getString(command));
                    pc.addObject(0, rules);
                    ret.add(new PluginResponse(pc.evaluate(), user));
                }
            }
            if (ret.isEmpty()) {
                ret = instance.run();
                if (ret == null) {
                    throw new PluginException("PLG-?", "Erreur durant l'exécution de la commande ["+command+"].");
                }
            }
        } catch (PluginException e) {
            ret.add(new PluginResponse(e.getMessage(), user));
        } catch (ClassNotFoundException | NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            Logger.getLogger(Castabot.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return ret;
    }
    
    private boolean checkPermissions() {
        // Check for user specific permissions
        CommandPermission retCommandPermission = null;
        for (UserPermission userPermission : CastabotClient.getCastabot().getPermissions().getLstSpecificPermission(UserPermission.TYPE_USER)) {
            if (userPermission.getTarget().equals(user.getId())) {
                retCommandPermission = userPermission.getCommandPermission(command);
                break;
            }
        }
        if (retCommandPermission != null) {
            return retCommandPermission.getArgPermission(args[0]);
        }
        
        // Nothing found, check for role specific permissions
        int priority = 0;
        for (Role role : guild.getMember(user).getRoles()) {
            RolePermission rolePermission = (RolePermission) CastabotClient.getCastabot().getPermissions().getPermission(UserPermission.TYPE_ROLE, role.getName());
            if (rolePermission != null) {
                if (role.getName().equals(rolePermission.getTarget())) {
                    if (rolePermission.getPriority() > priority) {
                        priority = rolePermission.getPriority();
                        retCommandPermission = rolePermission.getCommandPermission(command);
                    }
                }
            }
        }
        if (retCommandPermission != null) {
            return retCommandPermission.getArgPermission(args[0]);
        }
        
        // Still nothing found, then it must be false
        return false;
    }

    public String getCommand() {
        return command;
    }

    public String[] getArgs() {
        return args;
    }
    
    public boolean isSecret() {
        return secret;
    }
}