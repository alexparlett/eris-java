/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package org.homonoia.sw.ecs.core;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Bits;
import com.badlogic.gdx.utils.Disposable;
import io.reactivex.subjects.PublishSubject;
import org.homonoia.sw.ecs.signals.ComponentAddedSignal;
import org.homonoia.sw.ecs.signals.ComponentRemovedSignal;
import org.homonoia.sw.ecs.signals.Signal;
import org.homonoia.sw.collections.Bag;
import org.homonoia.sw.collections.ImmutableArray;

/**
 * Simple containers of {@link Component}s that give them "spec". The component's spec is then processed by {@link EntitySystem}s.
 * @author Stefan Bachmann
 */
public class Entity implements Disposable {
    public int id;
    /** A flag that can be used to bit mask this entity. Up to the user to manage. */
    public int flags;
    public final PublishSubject<Signal> publisher = PublishSubject.create();

	boolean scheduledForRemoval;
	boolean removing;
	ComponentOperationHandler componentOperationHandler;

	private Bag<Component> components;
	private Array<Component> componentsArray;
	private ImmutableArray<Component> immutableComponentsArray;
	private Bits componentBits;
	private Bits familyBits;

	/** Creates an empty Entity. */
	public Entity () {
		components = new Bag<Component>();
		componentsArray = new Array<Component>(false, 16);
		immutableComponentsArray = new ImmutableArray<Component>(componentsArray);
		componentBits = new Bits();
		familyBits = new Bits();
		flags = 0;
	}

	/**
	 * Adds a {@link Component} to this Entity. If a {@link Component} of the same type already exists, it'll be replaced.
	 * @return The Entity for easy chaining
	 */
	public Entity add (Component component) {
		if (addInternal(component)) {
			if (componentOperationHandler != null) {
				componentOperationHandler.add(this);
			}
			else {
				notifyComponentAdded();
			}
		}
		
		return this;
	}

	/**
	 * Adds a {@link Component} to this Entity. If a {@link Component} of the same type already exists, it'll be replaced.
	 * @return The Component for direct component manipulation (e.g. PooledComponent)
	 */
	public Component addAndReturn(Component component) {
		add(component);
		return component;
	}

	/**
	 * Removes the {@link Component} of the specified type. Since there is only ever one component of one type, we don't need an
	 * instance reference.
	 * @return The removed {@link Component}, or null if the Entity did no contain such a component.
	 */
	public Component remove (Class<? extends Component> componentClass) {
		ComponentType componentType = ComponentType.getFor(componentClass);
		int componentTypeIndex = componentType.getIndex();
		Component removeComponent = components.get(componentTypeIndex);

		if (removeComponent != null && removeInternal(componentClass)) {
			if (componentOperationHandler != null) {
				componentOperationHandler.remove(this, removeComponent);
			}
			else {
				notifyComponentRemoved(removeComponent);
			}
		}

		return removeComponent;
	}

	/** Removes all the {@link Component}'s from the Entity. */
	public void removeAll () {
		while (componentsArray.size > 0) {
			remove(componentsArray.get(0).getClass());
		}
	}

	/** @return immutable collection with all the Entity {@link Component}s. */
	public ImmutableArray<Component> getComponents () {
		return immutableComponentsArray;
	}

	/**
	 * Retrieve a component from this {@link Entity} by class. <em>Note:</em> the preferred way of retrieving {@link Component}s is
	 * using {@link ComponentMapper}s. This method is provided for convenience; using a ComponentMapper provides O(1) access to
	 * components while this method provides only O(logn).
	 * @param componentClass the class of the component to be retrieved.
	 * @return the instance of the specified {@link Component} attached to this {@link Entity}, or null if no such
	 *         {@link Component} exists.
	 */
	public <T extends Component> T getComponent (Class<T> componentClass) {
		return getComponent(ComponentType.getFor(componentClass));
	}

	/**
	 * Internal use.
	 * @return The {@link Component} object for the specified class, null if the Entity does not have any components for that class.
	 */
	@SuppressWarnings("unchecked")
	<T extends Component> T getComponent (ComponentType componentType) {
		int componentTypeIndex = componentType.getIndex();

		if (componentTypeIndex < components.getCapacity()) {
			return (T) components.get(componentType.getIndex());
		} else {
			return null;
		}
	}

	/**
	 * @return Whether or not the Entity has a {@link Component} for the specified class.
	 */
	public boolean hasComponent (ComponentType componentType) {
		return componentBits.get(componentType.getIndex());
	}

	/**
	 * @return Whether or not the Entity has a {@link Component} for the specified class.
	 */
	public <T extends Component> boolean hasComponent (Class<T> componentClass) {
		return hasComponent(ComponentType.getFor(componentClass));
	}

	/**
	 * @return This Entity's component bits, describing all the {@link Component}s it contains.
	 */
	Bits getComponentBits () {
		return componentBits;
	}

	/** @return This Entity's {@link Family} bits, describing all the {@link EntitySystem}s it currently is being processed by. */
	Bits getFamilyBits () {
		return familyBits;
	}

	/**
	 * @param component
	 * @return whether or not the component was added.
	 */
	boolean addInternal (Component component) {
		Class<? extends Component> componentClass = component.getClass();
		Component oldComponent = getComponent(componentClass);

		if (component == oldComponent) {
			return false;
		}

		if (oldComponent != null) {
			removeInternal(componentClass);
		}

		int componentTypeIndex = ComponentType.getIndexFor(componentClass);
		components.set(componentTypeIndex, component);
		componentsArray.add(component);
		componentBits.set(componentTypeIndex);
		component.addedToEntityInternal(this);
		
		return true;
	}

	/**
	 * @param componentClass
	 * @return whether or not a component with the specified class was found and removed.
	 */
	boolean removeInternal (Class<? extends Component> componentClass) {
		ComponentType componentType = ComponentType.getFor(componentClass);
		int componentTypeIndex = componentType.getIndex();
		Component removeComponent = components.get(componentTypeIndex);

		if (removeComponent != null) {
			components.set(componentTypeIndex, null);
			componentsArray.removeValue(removeComponent, true);
			componentBits.clear(componentTypeIndex);
			removeComponent.removedFromEntityInternal();
			
			return true;
		}
		
		return false;
	}
	
	void notifyComponentAdded() {
        if(!publisher.hasComplete()) {
            publisher.onNext(ComponentAddedSignal.builder().item(this).build());
        }
	}
	
	void notifyComponentRemoved(Component removeComponent) {
	    if(!publisher.hasComplete()) {
            publisher.onNext(ComponentRemovedSignal.builder()
                    .item(this)
                    .component(removeComponent)
                    .build());
        }
	}

	/** @return true if the entity is scheduled to be removed */
	public boolean isScheduledForRemoval () {
		return scheduledForRemoval;
	}

	@Override
	public void dispose() {
		while (componentsArray.size > 0) {
			Component component = componentsArray.first();
			remove(component.getClass());
			component.dispose();
		}
        publisher.onComplete();
	}
}
