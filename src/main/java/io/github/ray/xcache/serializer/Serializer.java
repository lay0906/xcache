package io.github.ray.xcache.serializer;

public interface Serializer {
	<T> String serialize(T t);

	<T> T deserialize(String v, Class<T> clazz);
}
