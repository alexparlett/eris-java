package org.homonoia.eris.ecs;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;
import org.homonoia.eris.scripting.ScriptBinding;
import org.homonoia.eris.scripting.ScriptEngine;

import java.util.*;
import java.util.function.Supplier;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 15/07/2016
 */
public class FamilyManager extends Contextual implements ScriptBinding {

    private Map<Set<Class<? extends Component>>, Family> families = new HashMap<>();

    /**
     * Instantiates a new Contextual.
     *
     * @param context the context
     */
    public FamilyManager(final Context context) {
        super(context);
    }

    public <T extends Component> Family get(Class<T>... components) {
        Set<Class<? extends Component>> key = new HashSet<>(Arrays.asList(components));
        return Optional.ofNullable(families.get(key))
                .orElseGet(createFamily(key));
    }

    @Override
    public void bind(ScriptEngine scriptEngine) {
        scriptEngine.bindClass(Entity.class);
        scriptEngine.bindClass(Component.class);
        scriptEngine.bindClass(ScriptComponent.class);
        scriptEngine.bindClass(Family.class);
        scriptEngine.bindGlobal("familyManager", this);
    }

    private Supplier<Family> createFamily(final Set<Class<? extends Component>> key) {
        return () -> {
            Family value = new Family(getContext(), key);
            families.put(key, value);
            return value;
        };
    }
}
