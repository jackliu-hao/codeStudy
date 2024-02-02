/*     */ package org.h2.util;
/*     */ 
/*     */ import java.io.PrintWriter;
/*     */ import java.io.StringWriter;
/*     */ import java.lang.ref.PhantomReference;
/*     */ import java.lang.ref.ReferenceQueue;
/*     */ import java.util.Collections;
/*     */ import java.util.HashSet;
/*     */ import java.util.Set;
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
/*     */ public class CloseWatcher
/*     */   extends PhantomReference<Object>
/*     */ {
/*  26 */   private static final ReferenceQueue<Object> queue = new ReferenceQueue();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  32 */   private static final Set<CloseWatcher> refs = Collections.synchronizedSet(new HashSet<>());
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String openStackTrace;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private AutoCloseable closeable;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CloseWatcher(Object paramObject, ReferenceQueue<Object> paramReferenceQueue, AutoCloseable paramAutoCloseable) {
/*  48 */     super(paramObject, paramReferenceQueue);
/*  49 */     this.closeable = paramAutoCloseable;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static CloseWatcher pollUnclosed() {
/*     */     while (true) {
/*  59 */       CloseWatcher closeWatcher = (CloseWatcher)queue.poll();
/*  60 */       if (closeWatcher == null) {
/*  61 */         return null;
/*     */       }
/*  63 */       if (refs != null) {
/*  64 */         refs.remove(closeWatcher);
/*     */       }
/*  66 */       if (closeWatcher.closeable != null) {
/*  67 */         return closeWatcher;
/*     */       }
/*     */     } 
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
/*     */   public static CloseWatcher register(Object paramObject, AutoCloseable paramAutoCloseable, boolean paramBoolean) {
/*  83 */     CloseWatcher closeWatcher = new CloseWatcher(paramObject, queue, paramAutoCloseable);
/*  84 */     if (paramBoolean) {
/*  85 */       Exception exception = new Exception("Open Stack Trace");
/*  86 */       StringWriter stringWriter = new StringWriter();
/*  87 */       exception.printStackTrace(new PrintWriter(stringWriter));
/*  88 */       closeWatcher.openStackTrace = stringWriter.toString();
/*     */     } 
/*  90 */     refs.add(closeWatcher);
/*  91 */     return closeWatcher;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void unregister(CloseWatcher paramCloseWatcher) {
/* 100 */     paramCloseWatcher.closeable = null;
/* 101 */     refs.remove(paramCloseWatcher);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getOpenStackTrace() {
/* 110 */     return this.openStackTrace;
/*     */   }
/*     */   
/*     */   public AutoCloseable getCloseable() {
/* 114 */     return this.closeable;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h\\util\CloseWatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */