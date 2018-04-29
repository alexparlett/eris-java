package org.homonoia.sw.mvc.component.sfx.dto;

import com.github.czyzby.lml.parser.action.ActorConsumer;
import org.homonoia.sw.mvc.component.sfx.MusicService;

/** Returns current sound state on invocation.
 *
 * @author MJ */
public class CurrentSoundStateAction implements ActorConsumer<Boolean, Object> {
    private final MusicService musicService;

    public CurrentSoundStateAction(final MusicService musicService) {
        this.musicService = musicService;
    }

    @Override
    public Boolean consume(final Object actor) {
        return musicService.isSoundEnabled();
    }
}