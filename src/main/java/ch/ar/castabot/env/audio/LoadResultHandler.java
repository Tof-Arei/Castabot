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
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.TextChannel;

/**
 *
 * @author Arei
 */
public class LoadResultHandler implements AudioLoadResultHandler {
    private final TextChannel channel;
    private final MusicManager musicManager;
    private final String trackUrl;
    
    public LoadResultHandler(TextChannel channel, MusicManager musicManager, String trackUrl) {
        this.channel = channel;
        this.musicManager = musicManager;
        this.trackUrl = trackUrl;
    }
    
    @Override
    public void trackLoaded(AudioTrack track) {
        channel.sendMessage("Lecture de la piste audio: " + track.getInfo().title).queue();
        CastabotClient.getCastabot().play(channel.getGuild(), musicManager, track);
    }

    @Override
    public void playlistLoaded(AudioPlaylist playlist) {
        AudioTrack firstTrack = playlist.getSelectedTrack();

        if (firstTrack == null) {
            firstTrack = playlist.getTracks().get(0);
        }

        channel.sendMessage("Ajout à la file: " + firstTrack.getInfo().title + " (Premier morceau de la playliste: " + playlist.getName() + ")").queue();
        CastabotClient.getCastabot().play(channel.getGuild(), musicManager, firstTrack);
    }

    @Override
    public void noMatches() {
        channel.sendMessage("Morceau introuvable: " + trackUrl).queue();
    }

    @Override
    public void loadFailed(FriendlyException exception) {
        channel.sendMessage("Impossible de lire: " + exception.getMessage()).queue();
    }
}
