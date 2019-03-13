package vekta;

import org.reflections.ReflectionUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import static vekta.Vekta.*;

public abstract class Syncable<T extends Syncable> implements Serializable {
	private static final Field MODIFIER_FIELD;
	private static final Field ID_FIELD;
	private static final Field REMOTE_FIELD;

	static {
		try {
			// Provide access to updating field modifiers (to remove `final`)
			MODIFIER_FIELD = Field.class.getDeclaredField("modifiers");
			MODIFIER_FIELD.setAccessible(true);

			// Define fields to ignore during sync
			ID_FIELD = Syncable.class.getDeclaredField("id");
			REMOTE_FIELD = Syncable.class.getDeclaredField("remote");
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}

	private final long id = randomID();

	private boolean remote = false;

	/**
	 * Determine whether this object was provided by a remote client.
	 */
	public final boolean isRemote() {
		return remote;
	}

	/**
	 * Indicate that this object is being managed remotely.
	 */
	public void setRemote(boolean remote) {
		this.remote = remote;
	}

	/**
	 * Return an immutable key corresponding to this object.
	 */
	public final long getSyncID() {
		return id;
	}

	/**
	 * Determine whether changes to this object should be propagated to remote clients.
	 */
	public boolean shouldSendChanges() {
		return true;
	}

	public void sendChanges() {
		getWorld().sendChanges(this);
	}

	/**
	 * Return a data object used for synchronization.
	 */
	@SuppressWarnings("unchecked")
	public T getSyncData() {
		return (T)this;
	}

	public boolean shouldSyncField(Field field) {
		return true;
	}

	public void onAddRemote() {
		onSync(getSyncData());
	}

	@SuppressWarnings("unchecked")
	public void onSync(T data) {
		try {
			// Brute-force update for now
			for(Field field : ReflectionUtils.getAllFields(getClass())) {
				// Skip undesired fields
				if(!shouldSyncField(field) && !field.equals(ID_FIELD) && !field.equals(REMOTE_FIELD)) {
					continue;
				}

				// Beat the devil out of the field
				field.setAccessible(true);
				MODIFIER_FIELD.setInt(field, field.getModifiers() & ~Modifier.FINAL);

				// Recursively replace Syncable objects
				Object object = field.get(data);
				if(object instanceof Syncable) {
//					println("#$@%^&:::", this, object, register((Syncable)object));
					field.set(this, register((Syncable)object));
				}
				else if(object instanceof Collection) {
					Collection collection = (Collection)object;
					List buffer = new ArrayList(collection);
					collection.clear();
					for(Object elem : buffer) {
						// TODO: refactor to distinguish between sync and register
						collection.add(elem instanceof Syncable ? register((Syncable)elem) : elem);
					}
				}
				else if(object instanceof Map) {
					Map map = (Map)object;
					Map buffer = new HashMap(map);
					map.clear();
					for(Object key : map.keySet()) {
						Object value = buffer.get(key);
						map.put(key instanceof Syncable ? register((Syncable)key) : key,
								value instanceof Syncable ? register((Syncable)value) : value);
					}
				}
			}
		}
		catch(Exception e) {
			throw new RuntimeException(e);
		}
	}
}
