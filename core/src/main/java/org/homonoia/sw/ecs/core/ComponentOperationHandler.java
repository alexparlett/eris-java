package org.homonoia.sw.ecs.core;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Pool;


class ComponentOperationHandler {
	private BooleanInformer delayed;
	private ComponentOperationPool operationPool = new ComponentOperationPool();;
 	private Array<ComponentOperation> operations = new Array<ComponentOperation>();;

 	public ComponentOperationHandler(BooleanInformer delayed) {
 		this.delayed = delayed;
 	}
 	
	public void add(Entity entity) {
		if (delayed.value()) {
			ComponentOperation operation = operationPool.obtain();
			operation.makeAdd(entity);
			operations.add(operation);
		}
		else {
			entity.notifyComponentAdded();
		}
	}

	public void remove(Entity entity, Component removeComponent) {
		if (delayed.value()) {
			ComponentOperation operation = operationPool.obtain();
			operation.makeRemove(entity, removeComponent);
			operations.add(operation);
		}
		else {
			entity.notifyComponentRemoved(removeComponent);
		}
	}

	public void processOperations() {
		for (int i = 0; i < operations.size; ++i) {
			ComponentOperation operation = operations.get(i);

			switch(operation.type) {
				case Add:
					operation.entity.notifyComponentAdded();
					break;
				case Remove:
					operation.entity.notifyComponentRemoved(operation.component);
					break;
				default: break;
			}

			operationPool.free(operation);
		}

		operations.clear();
	}

	private static class ComponentOperation implements Pool.Poolable {
		private Component component;

		public enum Type {
			Add,
			Remove,
		}

		public Type type;
		public Entity entity;

		public void makeAdd(Entity entity) {
			this.type = Type.Add;
			this.entity = entity;
		}

		public void makeRemove(Entity entity, Component component) {
			this.component = component;
			this.type = Type.Remove;
			this.entity = entity;
		}

		@Override
		public void reset() {
			entity = null;
		}
	}
	
	private static class ComponentOperationPool extends Pool<ComponentOperation> {
		@Override
		protected ComponentOperation newObject() {
			return new ComponentOperation();
		}
	}
	
	interface BooleanInformer {
		public boolean value();
	}
}