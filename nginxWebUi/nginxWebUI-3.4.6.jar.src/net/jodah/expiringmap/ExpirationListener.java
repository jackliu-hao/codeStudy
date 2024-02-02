package net.jodah.expiringmap;

public interface ExpirationListener<K, V> {
  void expired(K paramK, V paramV);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\net\jodah\expiringmap\ExpirationListener.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */