/*     */ package org.noear.solon.sessionstate.local;
/*     */ 
/*     */ import java.util.Collection;
/*     */ import org.noear.solon.Utils;
/*     */ import org.noear.solon.boot.web.SessionStateBase;
/*     */ import org.noear.solon.core.handle.Context;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class LocalSessionState
/*     */   extends SessionStateBase
/*     */ {
/*  14 */   private static int _expiry = 7200;
/*  15 */   private static String _domain = null;
/*     */ 
/*     */   
/*     */   static {
/*  19 */     if (SessionProp.session_timeout > 0) {
/*  20 */       _expiry = SessionProp.session_timeout;
/*     */     }
/*     */     
/*  23 */     if (SessionProp.session_state_domain != null)
/*  24 */       _domain = SessionProp.session_state_domain; 
/*     */   }
/*     */   
/*  27 */   private static ScheduledStore _store = new ScheduledStore(_expiry);
/*     */   
/*     */   private Context ctx;
/*     */ 
/*     */   
/*     */   protected LocalSessionState(Context ctx) {
/*  33 */     this.ctx = ctx;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected String cookieGet(String key) {
/*  40 */     return this.ctx.cookie(key);
/*     */   }
/*     */ 
/*     */   
/*     */   protected void cookieSet(String key, String val) {
/*  45 */     if (SessionProp.session_state_domain_auto && 
/*  46 */       _domain != null && 
/*  47 */       this.ctx.uri().getHost().indexOf(_domain) < 0) {
/*  48 */       this.ctx.cookieSet(key, val, null, _expiry);
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/*  54 */     this.ctx.cookieSet(key, val, _domain, _expiry);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String sessionId() {
/*  64 */     String _sessionId = (String)this.ctx.attr("sessionId", null);
/*     */     
/*  66 */     if (_sessionId == null) {
/*  67 */       _sessionId = sessionIdGet(false);
/*  68 */       this.ctx.attrSet("sessionId", _sessionId);
/*     */     } 
/*     */     
/*  71 */     return _sessionId;
/*     */   }
/*     */ 
/*     */   
/*     */   public String sessionChangeId() {
/*  76 */     sessionIdGet(true);
/*  77 */     this.ctx.attrSet("sessionId", null);
/*  78 */     return sessionId();
/*     */   }
/*     */ 
/*     */   
/*     */   public Collection<String> sessionKeys() {
/*  83 */     return _store.keys();
/*     */   }
/*     */ 
/*     */   
/*     */   public Object sessionGet(String key) {
/*  88 */     return _store.get(sessionId(), key);
/*     */   }
/*     */ 
/*     */   
/*     */   public void sessionSet(String key, Object val) {
/*  93 */     if (val == null) {
/*  94 */       sessionRemove(key);
/*     */     } else {
/*  96 */       _store.put(sessionId(), key, val);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void sessionRemove(String key) {
/* 102 */     _store.remove(sessionId(), key);
/*     */   }
/*     */ 
/*     */   
/*     */   public void sessionClear() {
/* 107 */     _store.clear(sessionId());
/*     */   }
/*     */ 
/*     */   
/*     */   public void sessionReset() {
/* 112 */     sessionClear();
/* 113 */     sessionChangeId();
/*     */   }
/*     */ 
/*     */   
/*     */   public void sessionRefresh() {
/* 118 */     String sid = sessionIdPush();
/*     */     
/* 120 */     if (!Utils.isEmpty(sid)) {
/* 121 */       _store.delay(sessionId());
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean replaceable() {
/* 128 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\sessionstate\local\LocalSessionState.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */