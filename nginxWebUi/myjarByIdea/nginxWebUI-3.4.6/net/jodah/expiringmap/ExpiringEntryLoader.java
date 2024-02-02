package net.jodah.expiringmap;

public interface ExpiringEntryLoader<K, V> {
   ExpiringValue<V> load(K var1);
}
