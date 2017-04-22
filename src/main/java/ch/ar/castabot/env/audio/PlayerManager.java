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
package ch.ar.castabot.env.audio;

import ch.ar.castabot.CastabotClient;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import java.util.Map;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

/**
 *
 * @author Arei
 */
public class PlayerManager extends DefaultAudioPlayerManager {
    private synchronized MusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        Map<Long, MusicManager> musicManagers = (Map<Long, MusicManager>) CastabotClient.getCastabot().getPluginSettings().getValue("audio", "musicManagers");
        AudioPlayerManager playerManager = (AudioPlayerManager) CastabotClient.getCastabot().getPluginSettings().getValue("audio", "playerManager");
        MusicManager musicManager = musicManagers.get(guildId);
        
        if (musicManager == null) {
            musicManager = new MusicManager(playerManager);
            musicManagers.put(guildId, musicManager);
        }
        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }
    
    public void loadAndPlay(TextChannel channel, String trackUrl) {
        MusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        loadItemOrdered(musicManager, trackUrl, new LoadResultHandler(channel, musicManager, trackUrl));
    }
    
    public void play(Guild guild, MusicManager musicManager, AudioTrack track) {
        connectToFirstVoiceChannel(guild.getAudioManager());
        musicManager.scheduler.queue(track);
    }

    public void pause(TextChannel channel) {
        MusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.player.setPaused(true);
    }
    
    public void resume(TextChannel channel) {
        MusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.player.setPaused(false);
    }
    
    public void loop(TextChannel channel) {
        MusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.loopTrack();
    }
    
    public void skipTrack(TextChannel channel) {
        MusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.nextTrack();
        
        channel.sendMessage("Passage à la piste suivante.").queue();
    }
    
    public void stop(TextChannel channel) {
        skipTrack(channel);
        disconnectFromVoiceChannel(channel.getGuild().getAudioManager());
    }
    
    private static void connectToFirstVoiceChannel(AudioManager audioManager) {
        if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
            for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
                audioManager.openAudioConnection(voiceChannel);
                break;
            }
        }
    }
    
    private static void disconnectFromVoiceChannel(AudioManager audioManager) {
        if (audioManager.isConnected() || audioManager.isAttemptingToConnect()) {
            audioManager.closeAudioConnection();
        }
    }
}
