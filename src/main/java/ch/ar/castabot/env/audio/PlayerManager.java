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
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.managers.AudioManager;

/**
 *
 * @author Arei
 */
public class PlayerManager extends DefaultAudioPlayerManager {
    private final Guild guild;
    
    public PlayerManager(Guild guild) {
        this.guild = guild;
    }
    
    public void loadAndPlay(TextChannel channel, String trackUrl) {
        MusicManager musicManager = (MusicManager) CastabotClient.getCastabot().getPluginSettings(guild.getId()).getValue("audio", "musicManager");
        LoadResultHandler lrHandler = new LoadResultHandler(channel, trackUrl);
        loadItemOrdered(musicManager, trackUrl, lrHandler);
    }
    
    public String playlistLoaded(AudioPlaylist playlist) {
        AudioTrack firstTrack = playlist.getSelectedTrack();
        if (firstTrack == null) {
            firstTrack = playlist.getTracks().get(0);
        }

        return "Ajout à la file: " + firstTrack.getInfo().title + " (Premier morceau de la playliste: " + playlist.getName() + ")";
    }
    
    public String play(MusicManager musicManager, AudioTrack track) {
        connectToFirstVoiceChannel(guild.getAudioManager());
        musicManager.getScheduler().queue(track);
        return "Chargement de la piste: " + track.getInfo().title;
    }

    public String pause() {
        MusicManager musicManager = (MusicManager) CastabotClient.getCastabot().getPluginSettings(guild.getId()).getValue("audio", "musicManager");
        musicManager.getPlayer().setPaused(true);
        return "Piste suspendue.";
    }
    
    public String resume() {
        MusicManager musicManager = (MusicManager) CastabotClient.getCastabot().getPluginSettings(guild.getId()).getValue("audio", "musicManager");
        musicManager.getPlayer().setPaused(false);
        return "Piste rétablie.";
    }
    
    public String loop() {
        MusicManager musicManager = (MusicManager) CastabotClient.getCastabot().getPluginSettings(guild.getId()).getValue("audio", "musicManager");
        musicManager.getScheduler().loopTrack();
        return "Piste mise en boucle.";
    }
    
    public String skip() {
        MusicManager musicManager = (MusicManager) CastabotClient.getCastabot().getPluginSettings(guild.getId()).getValue("audio", "musicManager");
        musicManager.getScheduler().nextTrack();
        AudioTrack track = musicManager.getPlayer().getPlayingTrack();
        if (track != null) {
            return "Passage à la piste suivante: " + track.getInfo().title;
        } else {
            return "Plus de pistes en mémoire.";
        }
    }
    
    public String stop() {
        skip();
        //MusicManager musicManager = CastabotClient.getGuildAudioPlayer(channel.getGuild());
        //musicManager.getPlayer().destroy();
        //PlayerManager playerManager = (PlayerManager) CastabotClient.getCastabot().getPluginSettings(channel.getGuild()).getValue("audio", "playerManager");
        //CastabotClient.getCastabot().getPluginSettings(channel.getGuild()).setValue("audio", "musicManager", new MusicManager(playerManager));
        disconnectFromVoiceChannel(guild.getAudioManager());
        return "Arrêt du lecteur.";
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
