/*     */ package cn.hutool.extra.ssh;
/*     */ 
/*     */ import cn.hutool.core.lang.SimpleCache;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import com.jcraft.jsch.Session;
/*     */ import java.lang.invoke.SerializedLambda;
/*     */ import java.util.HashMap;
/*     */ import java.util.Iterator;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public enum JschSessionPool
/*     */ {
/*  17 */   INSTANCE;
/*     */   
/*     */   private final SimpleCache<String, Session> cache;
/*     */   
/*     */   JschSessionPool() {
/*  22 */     this.cache = new SimpleCache(new HashMap<>());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Session get(String key) {
/*  31 */     return (Session)this.cache.get(key);
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
/*     */   
/*     */   public Session getSession(String sshHost, int sshPort, String sshUser, String sshPass) {
/*  44 */     String key = StrUtil.format("{}@{}:{}", new Object[] { sshUser, sshHost, Integer.valueOf(sshPort) });
/*  45 */     return (Session)this.cache.get(key, Session::isConnected, () -> JschUtil.openSession(sshHost, sshPort, sshUser, sshPass));
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
/*     */ 
/*     */   
/*     */   public Session getSession(String sshHost, int sshPort, String sshUser, String prvkey, byte[] passphrase) {
/*  59 */     String key = StrUtil.format("{}@{}:{}", new Object[] { sshUser, sshHost, Integer.valueOf(sshPort) });
/*  60 */     return (Session)this.cache.get(key, Session::isConnected, () -> JschUtil.openSession(sshHost, sshPort, sshUser, prvkey, passphrase));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void put(String key, Session session) {
/*  70 */     this.cache.put(key, session);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close(String key) {
/*  79 */     Session session = get(key);
/*  80 */     if (session != null && session.isConnected()) {
/*  81 */       session.disconnect();
/*     */     }
/*  83 */     this.cache.remove(key);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void remove(Session session) {
/*  93 */     if (null != session) {
/*  94 */       Iterator<Map.Entry<String, Session>> iterator = this.cache.iterator();
/*     */       
/*  96 */       while (iterator.hasNext()) {
/*  97 */         Map.Entry<String, Session> entry = iterator.next();
/*  98 */         if (session.equals(entry.getValue())) {
/*  99 */           iterator.remove();
/*     */           break;
/*     */         } 
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void closeAll() {
/* 111 */     for (Map.Entry<String, Session> entry : this.cache) {
/* 112 */       Session session = entry.getValue();
/* 113 */       if (session != null && session.isConnected()) {
/* 114 */         session.disconnect();
/*     */       }
/*     */     } 
/* 117 */     this.cache.clear();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\extra\ssh\JschSessionPool.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */