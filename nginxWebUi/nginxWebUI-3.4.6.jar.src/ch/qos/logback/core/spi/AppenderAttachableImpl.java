/*     */ package ch.qos.logback.core.spi;
/*     */ 
/*     */ import ch.qos.logback.core.Appender;
/*     */ import ch.qos.logback.core.util.COWArrayList;
/*     */ import java.util.Iterator;
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
/*     */ public class AppenderAttachableImpl<E>
/*     */   implements AppenderAttachable<E>
/*     */ {
/*  29 */   private final COWArrayList<Appender<E>> appenderList = new COWArrayList((Object[])new Appender[0]);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addAppender(Appender<E> newAppender) {
/*  37 */     if (newAppender == null) {
/*  38 */       throw new IllegalArgumentException("Null argument disallowed");
/*     */     }
/*  40 */     this.appenderList.addIfAbsent(newAppender);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int appendLoopOnAppenders(E e) {
/*  47 */     int size = 0;
/*  48 */     Appender[] arrayOfAppender = (Appender[])this.appenderList.asTypedArray();
/*  49 */     int len = arrayOfAppender.length;
/*  50 */     for (int i = 0; i < len; i++) {
/*  51 */       arrayOfAppender[i].doAppend(e);
/*  52 */       size++;
/*     */     } 
/*  54 */     return size;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Iterator<Appender<E>> iteratorForAppenders() {
/*  64 */     return this.appenderList.iterator();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Appender<E> getAppender(String name) {
/*  74 */     if (name == null) {
/*  75 */       return null;
/*     */     }
/*  77 */     for (Appender<E> appender : this.appenderList) {
/*  78 */       if (name.equals(appender.getName())) {
/*  79 */         return appender;
/*     */       }
/*     */     } 
/*  82 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isAttached(Appender<E> appender) {
/*  92 */     if (appender == null) {
/*  93 */       return false;
/*     */     }
/*  95 */     for (Appender<E> a : this.appenderList) {
/*  96 */       if (a == appender)
/*  97 */         return true; 
/*     */     } 
/*  99 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void detachAndStopAllAppenders() {
/* 106 */     for (Appender<E> a : this.appenderList) {
/* 107 */       a.stop();
/*     */     }
/* 109 */     this.appenderList.clear();
/*     */   }
/*     */   
/* 112 */   static final long START = System.currentTimeMillis();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean detachAppender(Appender<E> appender) {
/* 119 */     if (appender == null) {
/* 120 */       return false;
/*     */     }
/*     */     
/* 123 */     boolean result = this.appenderList.remove(appender);
/* 124 */     return result;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean detachAppender(String name) {
/* 132 */     if (name == null) {
/* 133 */       return false;
/*     */     }
/* 135 */     boolean removed = false;
/* 136 */     for (Appender<E> a : this.appenderList) {
/* 137 */       if (name.equals(a.getName())) {
/* 138 */         removed = this.appenderList.remove(a);
/*     */         break;
/*     */       } 
/*     */     } 
/* 142 */     return removed;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\spi\AppenderAttachableImpl.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */