package net.jodah.expiringmap;

public interface EntryLoader<K, V> {
  V load(K paramK);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\net\jodah\expiringmap\EntryLoader.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */