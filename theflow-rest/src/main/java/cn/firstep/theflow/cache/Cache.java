package cn.firstep.theflow.cache;

/**
 * @param <K>
 * @param <V>
 * @author Alvin4u
 */
public interface Cache<K, V> {

    String get(K key);

    String put(K key, V value, long ttl);

    void remove(K key);
}
