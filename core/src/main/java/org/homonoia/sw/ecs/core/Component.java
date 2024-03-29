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

import com.badlogic.gdx.utils.Disposable;
import lombok.Data;

/**
 * Interface for all Components. A Component is intended as a spec holder and provides spec to be processed in an
 * {@link EntitySystem}. But do as you wish.
 * @author Stefan Bachmann
 */
@Data
public abstract class Component implements Disposable {
    private Entity entity;

    public final void addedToEntityInternal(Entity entity) {
        this.entity = entity;
        this.addedToEntity(entity);
    }

    public final void removedFromEntityInternal() {
        this.entity = null;
        this.removedFromEntity();
    }

    protected void addedToEntity(Entity entity) {

    }

    protected void removedFromEntity() {

    }

    @Override
    public void dispose() {

    }
}
