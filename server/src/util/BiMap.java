package util;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class BiMap<K, V> extends HashMap<K, V> implements Map<K, V> {
	
	private static final long serialVersionUID = -3397417736449682169L;
	private Map<K, V> keys;
	private Map<V, K> values;
	
	public BiMap() {
		this.keys = new HashMap<>();
		this.values = new HashMap<>();
	}
	
	public BiMap(Map<K, V> keys, Map<V, K> values) {
		this.keys = keys;
		this.values = values;
	}

	@Override
	public void clear() {
		keys.clear();
		values.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return keys.containsKey(key);
	}

	@Override
	public boolean containsValue(Object value) {
		return keys.containsValue(value);
	}

	@Override
	public Set<Map.Entry<K, V>> entrySet() {
		return keys.entrySet();
	}

	@Override
	public V get(Object key) {
		return keys.get(key);
	}

	@Override
	public boolean isEmpty() {
		return keys.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return keys.keySet();
	}

	@Override
	public V put(K key, V value) {
		K k = (K)key;
		V v = (V)value;
		
		values.put(v, k);
		return keys.put(k, v);
	}

	@Override
	public void putAll(Map<? extends K,? extends V> m) {
		for (Iterator<? extends Map.Entry<? extends K, ? extends V>> i = m.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry<? extends K, ? extends V> e = i.next();
			put(e.getKey(), e.getValue());
		}
	}

	@Override
	public V remove(Object key) {
		V value = keys.remove(key);
		values.remove(value);
		return value;
	}

	@Override
	public int size() {
		return keys.size();
	}

	@Override
	public Collection<V> values() {
		return keys.values();
	}
	
	public BiMap<V, K> inverse() {
		return new BiMap<>(values, keys);
	}
}
