package org.homonoia.sw.service;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.github.czyzby.autumn.annotation.Destroy;
import com.github.czyzby.autumn.annotation.Initiate;
import org.homonoia.sw.mvc.config.AutumnActionPriority;

/** Manages currently played UI theme and sound settings.
 *
 * @author MJ */
public class MusicService {
    private static final float MIN_VOLUME = 0f;
    private static final float MAX_VOLUME = 1f;

    private float musicVolume = MAX_VOLUME;
    private float soundVolume = MAX_VOLUME;
    private boolean musicEnabled = true;
    private boolean soundEnabled = true;

    private Music currentTheme;

    @Initiate
    private void initiate() {
    }

    /** @return current volume of music, [0, 1]. */
    public float getMusicVolume() {
        return musicVolume;
    }

    /** @param musicVolume will become current volume of music. If a registered theme is currently playing, it's volume
     *            will be adjusted. */
    public void setMusicVolume(final float musicVolume) {
        this.musicVolume = normalizeVolume(musicVolume);
        if (currentTheme != null) {
            currentTheme.setVolume(musicVolume);
        }
    }

    /** @param musicEnabled true to enable, false to disable. If a current theme is registered, it will stopped or
     *            started according to this setting. */
    public void setMusicEnabled(final boolean musicEnabled) {
        this.musicEnabled = musicEnabled;
        if (currentTheme != null) {
            if (musicEnabled) {
                if (!currentTheme.isPlaying()) {
                    currentTheme.play();
                }
            } else if (currentTheme.isPlaying()) {
                currentTheme.stop();
            }
        }
    }

    /** @return true if music is currently enabled. */
    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    /** @param volume should be normalized.
     * @return float value in range of [0, 1]. */
    public static float normalizeVolume(final float volume) {
        return Math.max(MIN_VOLUME, Math.min(MAX_VOLUME, volume));
    }

    /** @return current volume of sound effects. */
    public float getSoundVolume() {
        return soundVolume;
    }

    /** @param soundVolume will become current volume of sound effects. Note that currently played sounds will not be
     *            affected. */
    public void setSoundVolume(final float soundVolume) {
        this.soundVolume = normalizeVolume(soundVolume);
    }

    /** @param soundEnabled true to enable, false to disable. Note that currently played sounds will not be turned
     *            off. */
    public void setSoundEnabled(final boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
    }


    /** @return true if sounds are currently enabled. */
    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    /** @param sound will be played with the currently set sound volume, provided that sounds are turned on. */
    public void play(final Sound sound) {
        if (soundEnabled) {
            sound.play(soundVolume);
        }
    }

    /** @return currently played music theme, provided that it was properly registered. */
    public Music getCurrentTheme() {
        return currentTheme;
    }

    /** @param currentTheme will be set as the current theme and have its volume changed. If music is enabled, will be
     *            played. */
    public void playCurrentTheme(final Music currentTheme) {
        playCurrentTheme(currentTheme, true);
    }

    /** @param currentTheme will be set as the current theme. If music is enabled, will be played.
     * @param forceVolume if true, music volume will be set to stored preference. */
    public void playCurrentTheme(final Music currentTheme, final boolean forceVolume) {
        this.currentTheme = currentTheme;
        if (forceVolume) {
            currentTheme.setVolume(musicVolume);
        }
        if (musicEnabled) {
            currentTheme.play();
        }
    }

    /** Clears current theme, stopping it from playing. */
    public void clearCurrentTheme() {
        if (currentTheme != null) {
            if (currentTheme.isPlaying()) {
                currentTheme.stop();
            }
            currentTheme = null;
        }
    }

    @Destroy(priority = AutumnActionPriority.VERY_LOW_PRIORITY)
    private void destroy() {
    }
}