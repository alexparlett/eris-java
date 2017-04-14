package org.homonoia.sw.application;

import lombok.extern.slf4j.Slf4j;
import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.events.frame.BeginFrame;
import org.homonoia.sw.state.State;
import org.homonoia.sw.state.events.StateChange;
import org.homonoia.sw.state.events.StateCreate;
import org.homonoia.sw.state.events.StateDelete;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static java.util.Objects.nonNull;

/**
 * Copyright (c) 2015-2017 Homonoia Studios.
 *
 * @author alexparlett
 * @since 13/04/2017
 */
@Slf4j
public class StateMachine extends Contextual {

    private Map<Long, State> states = new HashMap<>();
    private Map<Long, State> awaitingCreate = new HashMap<>();
    private Map<Long, State> awaitingDelete = new HashMap<>();
    private Long currentState;
    private Long nextState;

    public StateMachine(Context context) {
        super(context);
        context.registerBean(this);
        subscribe(this::handleFrame, BeginFrame.class);
        subscribe(this::handleCreate, StateCreate.class);
        subscribe(this::handleChange, StateChange.class);
        subscribe(this::handleDelete, StateDelete.class);
    }

    public <T extends State> T findState(Long id) {
        return (T) states.get(id);
    }

    private void handleCreate(StateCreate evt) {
        if (evt.getId() > 0L) {
            if (!states.containsKey(evt.getId()) && !awaitingCreate.containsKey(evt.getId())) {
                awaitingCreate.put(evt.getId(), evt.getState());
            } else {
                log.error("State {} of {} already exists.", evt.getState().getClass(), evt.getId());
            }
        } else {
            log.error("Could not create State {} of {}, invalid ID.", evt.getState().getClass(), evt.getId());
        }
    }

    private void handleChange(StateChange evt) {
        if (evt.getId() > 0L) {
            if (states.containsKey(evt.getId()) || awaitingCreate.containsKey(evt.getId())) {
                nextState = evt.getId();
            } else {
                log.error("Could not change to State {}, does not exist.", evt.getId());
            }
        } else {
            log.error("Could not change to State {}, invalid ID.", evt.getId());
        }
    }

    private void handleDelete(StateDelete evt) {
        if (evt.getId() > 0L) {
            if (states.containsKey(evt.getId()) && !Objects.equals(evt.getId(), currentState) && !Objects.equals(evt.getId(), nextState)) {
                awaitingDelete.put(evt.getId(), states.remove(evt.getId()));
            } else {
                log.error("Cannot delete State {} either current, next or doesn't exist.", evt.getId());
            }
        } else {
            log.error("Could not delete State {}, invalid ID.", evt.getId());
        }
    }

    private void handleFrame(BeginFrame evt) {
        if (!awaitingCreate.isEmpty() || nonNull(nextState) || !awaitingDelete.isEmpty()) {
            createStates();
            switchToNext();
            deleteStates();
        }
    }

    private void createStates() {
        if (!awaitingCreate.isEmpty()) {
            awaitingCreate.forEach((id,state) -> {
                state.create();
                states.put(id, state);
            });
            awaitingCreate.clear();
        }
    }

    private void switchToNext() {
        if (nonNull(nextState)) {
            if (states.containsKey(nextState)) {
                State newActive = states.get(nextState);

                if (nonNull(currentState) && states.containsKey(currentState)) {
                    states.get(currentState).stop();
                }

                currentState = nextState;
                nextState = null;
                newActive.start();
            }
            else {
                log.error("Could not change to State {}, does not exist.", nextState);
            }
        }
    }

    private void deleteStates() {
        if (!awaitingDelete.isEmpty()) {
            awaitingDelete.forEach((id,state) -> {
                state.delete();
            });
            awaitingDelete.clear();
        }
    }
}
