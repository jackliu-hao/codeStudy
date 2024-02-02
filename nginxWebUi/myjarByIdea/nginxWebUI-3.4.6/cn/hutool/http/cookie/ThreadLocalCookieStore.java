package cn.hutool.http.cookie;

import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.List;

public class ThreadLocalCookieStore implements CookieStore {
   private static final ThreadLocal<CookieStore> STORES = new ThreadLocal<CookieStore>() {
      protected synchronized CookieStore initialValue() {
         return (new CookieManager()).getCookieStore();
      }
   };

   public CookieStore getCookieStore() {
      return (CookieStore)STORES.get();
   }

   public ThreadLocalCookieStore removeCurrent() {
      STORES.remove();
      return this;
   }

   public void add(URI uri, HttpCookie cookie) {
      this.getCookieStore().add(uri, cookie);
   }

   public List<HttpCookie> get(URI uri) {
      return this.getCookieStore().get(uri);
   }

   public List<HttpCookie> getCookies() {
      return this.getCookieStore().getCookies();
   }

   public List<URI> getURIs() {
      return this.getCookieStore().getURIs();
   }

   public boolean remove(URI uri, HttpCookie cookie) {
      return this.getCookieStore().remove(uri, cookie);
   }

   public boolean removeAll() {
      return this.getCookieStore().removeAll();
   }
}
