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

import java.io.File;
import net.dv8tion.jda.core.entities.MessageEmbed;

/**
 *
 * @author Arei
 */
public class PluginResponse {
    private String text;
    private MessageEmbed embed;
    private File file;
    private final String targetId;
    
    public PluginResponse(String text, MessageEmbed embed, File file, String targetId) {
        this.text = text;
        this.file = file;
        this.targetId = targetId;
    }
    
    public PluginResponse(String text, String targetId) {
        this.text = text;
        this.targetId = targetId;
    }
    
    public PluginResponse(MessageEmbed embed, String targetId) {
        this.embed = embed;
        this.targetId = targetId;
    }
    
    public PluginResponse(File file, String targetId) {
        this.file = file;
        this.targetId = targetId;
    }

    public String printText() {
        return (text == null) ? "" : "\r\n" + text;
    }
    
    public String getText() {
        return text;
    }
    
    public MessageEmbed getEmbed() {
        return embed;
    }

    public File getFile() {
        return file;
    }
    
    public String getTargetId() {
        return targetId;
    }
}
