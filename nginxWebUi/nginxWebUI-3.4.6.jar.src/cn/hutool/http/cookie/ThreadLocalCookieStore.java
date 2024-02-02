/*    */ package cn.hutool.http.cookie;
/*    */ 
/*    */ import java.net.CookieManager;
/*    */ import java.net.CookieStore;
/*    */ import java.net.HttpCookie;
/*    */ import java.net.URI;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ThreadLocalCookieStore
/*    */   implements CookieStore
/*    */ {
/* 19 */   private static final ThreadLocal<CookieStore> STORES = new ThreadLocal<CookieStore>()
/*    */     {
/*    */       protected synchronized CookieStore initialValue()
/*    */       {
/* 23 */         return (new CookieManager()).getCookieStore();
/*    */       }
/*    */     };
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CookieStore getCookieStore() {
/* 33 */     return STORES.get();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ThreadLocalCookieStore removeCurrent() {
/* 42 */     STORES.remove();
/* 43 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public void add(URI uri, HttpCookie cookie) {
/* 48 */     getCookieStore().add(uri, cookie);
/*    */   }
/*    */ 
/*    */   
/*    */   public List<HttpCookie> get(URI uri) {
/* 53 */     return getCookieStore().get(uri);
/*    */   }
/*    */ 
/*    */   
/*    */   public List<HttpCookie> getCookies() {
/* 58 */     return getCookieStore().getCookies();
/*    */   }
/*    */ 
/*    */   
/*    */   public List<URI> getURIs() {
/* 63 */     return getCookieStore().getURIs();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean remove(URI uri, HttpCookie cookie) {
/* 68 */     return getCookieStore().remove(uri, cookie);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean removeAll() {
/* 73 */     return getCookieStore().removeAll();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\cookie\ThreadLocalCookieStore.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */