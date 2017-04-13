package org.homonoia.eris.state;

/**
 * Copyright (c) 2015-2017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 13/04/2017
 */
public interface State {
    void create();
    void start();
    void stop();
    void delete();
}
