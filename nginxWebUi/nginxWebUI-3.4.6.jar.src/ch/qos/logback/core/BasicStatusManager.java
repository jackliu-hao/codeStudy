/*     */ package ch.qos.logback.core;
/*     */ 
/*     */ import ch.qos.logback.core.helpers.CyclicBuffer;
/*     */ import ch.qos.logback.core.spi.LogbackLock;
/*     */ import ch.qos.logback.core.status.Status;
/*     */ import ch.qos.logback.core.status.StatusListener;
/*     */ import ch.qos.logback.core.status.StatusManager;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ public class BasicStatusManager
/*     */   implements StatusManager
/*     */ {
/*     */   public static final int MAX_HEADER_COUNT = 150;
/*     */   public static final int TAIL_SIZE = 150;
/*  31 */   int count = 0;
/*     */ 
/*     */   
/*  34 */   protected final List<Status> statusList = new ArrayList<Status>();
/*  35 */   protected final CyclicBuffer<Status> tailBuffer = new CyclicBuffer(150);
/*  36 */   protected final LogbackLock statusListLock = new LogbackLock();
/*     */   
/*  38 */   int level = 0;
/*     */ 
/*     */   
/*  41 */   protected final List<StatusListener> statusListenerList = new ArrayList<StatusListener>();
/*  42 */   protected final LogbackLock statusListenerListLock = new LogbackLock();
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
/*     */   public void add(Status newStatus) {
/*  59 */     fireStatusAddEvent(newStatus);
/*     */     
/*  61 */     this.count++;
/*  62 */     if (newStatus.getLevel() > this.level) {
/*  63 */       this.level = newStatus.getLevel();
/*     */     }
/*     */     
/*  66 */     synchronized (this.statusListLock) {
/*  67 */       if (this.statusList.size() < 150) {
/*  68 */         this.statusList.add(newStatus);
/*     */       } else {
/*  70 */         this.tailBuffer.add(newStatus);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public List<Status> getCopyOfStatusList() {
/*  77 */     synchronized (this.statusListLock) {
/*  78 */       List<Status> tList = new ArrayList<Status>(this.statusList);
/*  79 */       tList.addAll(this.tailBuffer.asList());
/*  80 */       return tList;
/*     */     } 
/*     */   }
/*     */   
/*     */   private void fireStatusAddEvent(Status status) {
/*  85 */     synchronized (this.statusListenerListLock) {
/*  86 */       for (StatusListener sl : this.statusListenerList) {
/*  87 */         sl.addStatusEvent(status);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public void clear() {
/*  93 */     synchronized (this.statusListLock) {
/*  94 */       this.count = 0;
/*  95 */       this.statusList.clear();
/*  96 */       this.tailBuffer.clear();
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getLevel() {
/* 101 */     return this.level;
/*     */   }
/*     */   
/*     */   public int getCount() {
/* 105 */     return this.count;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean add(StatusListener listener) {
/* 113 */     synchronized (this.statusListenerListLock) {
/* 114 */       if (listener instanceof ch.qos.logback.core.status.OnConsoleStatusListener) {
/* 115 */         boolean alreadyPresent = checkForPresence(this.statusListenerList, listener.getClass());
/* 116 */         if (alreadyPresent)
/* 117 */           return false; 
/*     */       } 
/* 119 */       this.statusListenerList.add(listener);
/*     */     } 
/* 121 */     return true;
/*     */   }
/*     */   
/*     */   private boolean checkForPresence(List<StatusListener> statusListenerList, Class<?> aClass) {
/* 125 */     for (StatusListener e : statusListenerList) {
/* 126 */       if (e.getClass() == aClass)
/* 127 */         return true; 
/*     */     } 
/* 129 */     return false;
/*     */   }
/*     */   
/*     */   public void remove(StatusListener listener) {
/* 133 */     synchronized (this.statusListenerListLock) {
/* 134 */       this.statusListenerList.remove(listener);
/*     */     } 
/*     */   }
/*     */   
/*     */   public List<StatusListener> getCopyOfStatusListenerList() {
/* 139 */     synchronized (this.statusListenerListLock) {
/* 140 */       return new ArrayList<StatusListener>(this.statusListenerList);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\BasicStatusManager.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */