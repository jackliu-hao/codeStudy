/*     */ package org.apache.http.impl.cookie;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import org.apache.http.annotation.Contract;
/*     */ import org.apache.http.annotation.ThreadingBehavior;
/*     */ import org.apache.http.cookie.CommonCookieAttributeHandler;
/*     */ import org.apache.http.cookie.CookieAttributeHandler;
/*     */ import org.apache.http.cookie.CookieSpec;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.Asserts;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @Contract(threading = ThreadingBehavior.SAFE)
/*     */ public abstract class AbstractCookieSpec
/*     */   implements CookieSpec
/*     */ {
/*     */   private final Map<String, CookieAttributeHandler> attribHandlerMap;
/*     */   
/*     */   public AbstractCookieSpec() {
/*  64 */     this.attribHandlerMap = new ConcurrentHashMap<String, CookieAttributeHandler>(10);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected AbstractCookieSpec(HashMap<String, CookieAttributeHandler> map) {
/*  72 */     Asserts.notNull(map, "Attribute handler map");
/*  73 */     this.attribHandlerMap = new ConcurrentHashMap<String, CookieAttributeHandler>(map);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected AbstractCookieSpec(CommonCookieAttributeHandler... handlers) {
/*  81 */     this.attribHandlerMap = new ConcurrentHashMap<String, CookieAttributeHandler>(handlers.length);
/*  82 */     for (CommonCookieAttributeHandler handler : handlers) {
/*  83 */       this.attribHandlerMap.put(handler.getAttributeName(), handler);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public void registerAttribHandler(String name, CookieAttributeHandler handler) {
/*  95 */     Args.notNull(name, "Attribute name");
/*  96 */     Args.notNull(handler, "Attribute handler");
/*  97 */     this.attribHandlerMap.put(name, handler);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected CookieAttributeHandler findAttribHandler(String name) {
/* 109 */     return this.attribHandlerMap.get(name);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected CookieAttributeHandler getAttribHandler(String name) {
/* 121 */     CookieAttributeHandler handler = findAttribHandler(name);
/* 122 */     Asserts.check((handler != null), "Handler not registered for " + name + " attribute");
/*     */     
/* 124 */     return handler;
/*     */   }
/*     */   
/*     */   protected Collection<CookieAttributeHandler> getAttribHandlers() {
/* 128 */     return this.attribHandlerMap.values();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\cookie\AbstractCookieSpec.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */