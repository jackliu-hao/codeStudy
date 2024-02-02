/*     */ package io.undertow.servlet.util;
/*     */ 
/*     */ import io.undertow.servlet.UndertowServletLogger;
/*     */ import io.undertow.servlet.api.SessionPersistenceManager;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.ObjectInputStream;
/*     */ import java.io.ObjectOutputStream;
/*     */ import java.util.Date;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
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
/*     */ public class InMemorySessionPersistence
/*     */   implements SessionPersistenceManager
/*     */ {
/*  40 */   private static final Map<String, Map<String, SessionEntry>> data = new ConcurrentHashMap<>();
/*     */ 
/*     */   
/*     */   public void persistSessions(String deploymentName, Map<String, SessionPersistenceManager.PersistentSession> sessionData) {
/*     */     try {
/*  45 */       Map<String, SessionEntry> serializedData = new HashMap<>();
/*  46 */       for (Map.Entry<String, SessionPersistenceManager.PersistentSession> sessionEntry : sessionData.entrySet()) {
/*  47 */         Map<String, byte[]> data = (Map)new HashMap<>();
/*  48 */         for (Map.Entry<String, Object> sessionAttribute : (Iterable<Map.Entry<String, Object>>)((SessionPersistenceManager.PersistentSession)sessionEntry.getValue()).getSessionData().entrySet()) {
/*     */           try {
/*  50 */             ByteArrayOutputStream out = new ByteArrayOutputStream();
/*  51 */             ObjectOutputStream objectOutputStream = new ObjectOutputStream(out);
/*  52 */             objectOutputStream.writeObject(sessionAttribute.getValue());
/*  53 */             objectOutputStream.close();
/*  54 */             data.put(sessionAttribute.getKey(), out.toByteArray());
/*  55 */           } catch (Exception e) {
/*  56 */             UndertowServletLogger.ROOT_LOGGER.failedToPersistSessionAttribute(sessionAttribute.getKey(), sessionAttribute.getValue(), sessionEntry.getKey(), e);
/*     */           } 
/*     */         } 
/*  59 */         serializedData.put(sessionEntry.getKey(), new SessionEntry(((SessionPersistenceManager.PersistentSession)sessionEntry.getValue()).getExpiration(), data));
/*     */       } 
/*  61 */       InMemorySessionPersistence.data.put(deploymentName, serializedData);
/*  62 */     } catch (Exception e) {
/*  63 */       UndertowServletLogger.ROOT_LOGGER.failedToPersistSessions(e);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Map<String, SessionPersistenceManager.PersistentSession> loadSessionAttributes(String deploymentName, ClassLoader classLoader) {
/*     */     try {
/*  71 */       long time = System.currentTimeMillis();
/*  72 */       this; Map<String, SessionEntry> data = InMemorySessionPersistence.data.remove(deploymentName);
/*  73 */       if (data != null) {
/*  74 */         Map<String, SessionPersistenceManager.PersistentSession> ret = new HashMap<>();
/*  75 */         for (Map.Entry<String, SessionEntry> sessionEntry : data.entrySet()) {
/*  76 */           if ((sessionEntry.getValue()).expiry.getTime() > time) {
/*  77 */             Map<String, Object> session = new HashMap<>();
/*  78 */             for (Map.Entry<String, byte[]> sessionAttribute : (Iterable<Map.Entry<String, byte[]>>)(sessionEntry.getValue()).data.entrySet()) {
/*  79 */               ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(sessionAttribute.getValue()));
/*  80 */               session.put(sessionAttribute.getKey(), in.readObject());
/*     */             } 
/*  82 */             ret.put(sessionEntry.getKey(), new SessionPersistenceManager.PersistentSession((sessionEntry.getValue()).expiry, session));
/*     */           } 
/*     */         } 
/*  85 */         return ret;
/*     */       } 
/*  87 */     } catch (Exception e) {
/*  88 */       UndertowServletLogger.ROOT_LOGGER.failedtoLoadPersistentSessions(e);
/*     */     } 
/*  90 */     return null;
/*     */   }
/*     */ 
/*     */   
/*     */   public void clear(String deploymentName) {}
/*     */   
/*     */   static final class SessionEntry
/*     */   {
/*     */     private final Date expiry;
/*     */     private final Map<String, byte[]> data;
/*     */     
/*     */     private SessionEntry(Date expiry, Map<String, byte[]> data) {
/* 102 */       this.expiry = expiry;
/* 103 */       this.data = data;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servle\\util\InMemorySessionPersistence.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */