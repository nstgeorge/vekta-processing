import org.junit.Test;
import org.reflections.ReflectionUtils;
import org.reflections.Reflections;
import vekta.connection.message.Message;
import vekta.world.WorldState;

import java.io.Externalizable;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static processing.core.PApplet.println;

public class SerializationTest {
	private static final Reflections REFLECTIONS = new Reflections("vekta");

	/**
	 * Ensure that all fields on serializable world objects are also serializable.
	 */
	@Test
	public void testSerialization() throws Exception {
		Set<Class<?>> next = new HashSet<>();
		Set<Class<?>> current = new HashSet<>();
		Set<Class<?>> visited = new HashSet<>();

		next.add(WorldState.class); // Singleplayer save/load states
		next.add(Message.class); // Multiplayer messages

		while(!next.isEmpty()) {
			current.addAll(next);
			next.clear();
			for(Class<?> type : current) {
				println(type);////
				boolean isExternalizable = Externalizable.class.isAssignableFrom(type);
				if(!isExternalizable && !Serializable.class.isAssignableFrom(type) && !type.isEnum() && !type.isPrimitive()) {
					Set<Class<?>> references = new HashSet<>();
					for(Class<?> past : visited) {
						// Find field references of type
						getFieldTypes(past)
								.filter(t -> t == type && t != past)
								.findFirst()
								.ifPresent(references::add);
					}
					// Provide debugging information
					throw new Exception("Non-serializable class: " + type.getName()
							+ (!references.isEmpty() ? " (Referenced by " + references.stream()
							.map(Class::getName)
							.collect(Collectors.joining(", ")) + ")" : ""));
				}
				visited.add(type);
				if(!isExternalizable) {
					Stream.concat(
							getFieldTypes(type), // Check instance fields
							REFLECTIONS.getSubTypesOf(type).stream()) // Check subclasses
							.forEach(t -> {
								if(!visited.contains(t) && !t.getName().startsWith("java.util.") && !t.getName().startsWith("java.lang.")) {
									next.add(t);
								}
							});
				}
			}
			current.clear();
		}
	}

	@SuppressWarnings("unchecked")
	private Stream<Class<?>> getFieldTypes(Class<?> type) {
		return ReflectionUtils.getAllFields(type).stream() // Check fields
				.filter(f -> !Modifier.isTransient(f.getModifiers()) && !Modifier.isStatic(f.getModifiers()))
				.map(Field::getType);
	}
}
