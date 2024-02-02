package net.jodah.expiringmap;

public interface EntryLoader<K, V> {
   V load(K var1);
}
