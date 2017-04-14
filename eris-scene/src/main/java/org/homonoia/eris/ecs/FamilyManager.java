package org.homonoia.eris.ecs;

import org.homonoia.eris.core.Context;
import org.homonoia.eris.core.Contextual;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Copyright (c) 2015-2016 Homonoia Studios.
 *
 * @author alexparlett
 * @since 15/07/2016
 */
public class FamilyManager extends Contextual {

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

    private Supplier<Family> createFamily(final Set<Class<? extends Component>> key) {
        return () -> {
            Family value = new Family(getContext(), key);
            families.put(key, value);
            return value;
        };
    }
}
