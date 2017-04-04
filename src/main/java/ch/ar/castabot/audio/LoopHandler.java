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
package ch.ar.castabot.audio;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.TrackMarker;
import com.sedmelluq.discord.lavaplayer.track.TrackMarkerHandler;

/**
 *
 * @author Arei
 */
public class LoopHandler implements TrackMarkerHandler {
    private final AudioPlayer player;
    private final AudioTrack track;
    private final long marker;
    
    public LoopHandler(AudioPlayer player, AudioTrack track, long marker) {
        this.player = player;
        this.track = track;
        this.marker = marker;
    }
    
    private void loopTrack() {
        player.playTrack(track.makeClone());
        track.setMarker(new TrackMarker(marker, this));
    }
    
    @Override
    public void handle(MarkerState ms) {
        loopTrack();
    }
}
