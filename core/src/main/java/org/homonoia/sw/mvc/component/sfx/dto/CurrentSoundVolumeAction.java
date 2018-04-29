package org.homonoia.sw.mvc.component.sfx.dto;

import com.github.czyzby.lml.parser.action.ActorConsumer;
import org.homonoia.sw.mvc.component.sfx.MusicService;

/** Returns current sound volume on invocation.
 *
 * @author MJ */
public class CurrentSoundVolumeAction implements ActorConsumer<Float, Object> {
    private final MusicService musicService;

    public CurrentSoundVolumeAction(final MusicService musicService) {
        this.musicService = musicService;
    }

    @Override
    public Float consume(final Object actor) {
        return musicService.getSoundVolume();
    }
}