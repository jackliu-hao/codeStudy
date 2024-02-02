package net.jodah.expiringmap;

public interface ExpirationListener<K, V> {
   void expired(K var1, V var2);
}
