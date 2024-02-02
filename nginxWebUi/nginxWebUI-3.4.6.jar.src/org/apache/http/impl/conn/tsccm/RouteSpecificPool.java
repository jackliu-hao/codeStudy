/*     */ package org.apache.http.impl.conn.tsccm;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.LinkedList;
/*     */ import java.util.ListIterator;
/*     */ import java.util.Queue;
/*     */ import org.apache.commons.logging.Log;
/*     */ import org.apache.commons.logging.LogFactory;
/*     */ import org.apache.http.conn.OperatedClientConnection;
/*     */ import org.apache.http.conn.params.ConnPerRoute;
/*     */ import org.apache.http.conn.routing.HttpRoute;
/*     */ import org.apache.http.util.Args;
/*     */ import org.apache.http.util.Asserts;
/*     */ import org.apache.http.util.LangUtils;
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
/*     */ @Deprecated
/*     */ public class RouteSpecificPool
/*     */ {
/*  56 */   private final Log log = LogFactory.getLog(getClass());
/*     */ 
/*     */ 
/*     */   
/*     */   protected final HttpRoute route;
/*     */ 
/*     */ 
/*     */   
/*     */   protected final int maxEntries;
/*     */ 
/*     */ 
/*     */   
/*     */   protected final ConnPerRoute connPerRoute;
/*     */ 
/*     */ 
/*     */   
/*     */   protected final LinkedList<BasicPoolEntry> freeEntries;
/*     */ 
/*     */   
/*     */   protected final Queue<WaitingThread> waitingThreads;
/*     */ 
/*     */   
/*     */   protected int numEntries;
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public RouteSpecificPool(HttpRoute route, int maxEntries) {
/*  84 */     this.route = route;
/*  85 */     this.maxEntries = maxEntries;
/*  86 */     this.connPerRoute = new ConnPerRoute()
/*     */       {
/*     */         public int getMaxForRoute(HttpRoute unused) {
/*  89 */           return RouteSpecificPool.this.maxEntries;
/*     */         }
/*     */       };
/*  92 */     this.freeEntries = new LinkedList<BasicPoolEntry>();
/*  93 */     this.waitingThreads = new LinkedList<WaitingThread>();
/*  94 */     this.numEntries = 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RouteSpecificPool(HttpRoute route, ConnPerRoute connPerRoute) {
/* 105 */     this.route = route;
/* 106 */     this.connPerRoute = connPerRoute;
/* 107 */     this.maxEntries = connPerRoute.getMaxForRoute(route);
/* 108 */     this.freeEntries = new LinkedList<BasicPoolEntry>();
/* 109 */     this.waitingThreads = new LinkedList<WaitingThread>();
/* 110 */     this.numEntries = 0;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final HttpRoute getRoute() {
/* 120 */     return this.route;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public final int getMaxEntries() {
/* 130 */     return this.maxEntries;
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
/*     */   public boolean isUnused() {
/* 143 */     return (this.numEntries < 1 && this.waitingThreads.isEmpty());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getCapacity() {
/* 153 */     return this.connPerRoute.getMaxForRoute(this.route) - this.numEntries;
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
/*     */   public final int getEntryCount() {
/* 165 */     return this.numEntries;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public BasicPoolEntry allocEntry(Object state) {
/* 175 */     if (!this.freeEntries.isEmpty()) {
/* 176 */       ListIterator<BasicPoolEntry> it = this.freeEntries.listIterator(this.freeEntries.size());
/* 177 */       while (it.hasPrevious()) {
/* 178 */         BasicPoolEntry entry = it.previous();
/* 179 */         if (entry.getState() == null || LangUtils.equals(state, entry.getState())) {
/* 180 */           it.remove();
/* 181 */           return entry;
/*     */         } 
/*     */       } 
/*     */     } 
/* 185 */     if (getCapacity() == 0 && !this.freeEntries.isEmpty()) {
/* 186 */       BasicPoolEntry entry = this.freeEntries.remove();
/* 187 */       entry.shutdownEntry();
/* 188 */       OperatedClientConnection conn = entry.getConnection();
/*     */       try {
/* 190 */         conn.close();
/* 191 */       } catch (IOException ex) {
/* 192 */         this.log.debug("I/O error closing connection", ex);
/*     */       } 
/* 194 */       return entry;
/*     */     } 
/* 196 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void freeEntry(BasicPoolEntry entry) {
/* 207 */     if (this.numEntries < 1) {
/* 208 */       throw new IllegalStateException("No entry created for this pool. " + this.route);
/*     */     }
/*     */     
/* 211 */     if (this.numEntries <= this.freeEntries.size()) {
/* 212 */       throw new IllegalStateException("No entry allocated from this pool. " + this.route);
/*     */     }
/*     */     
/* 215 */     this.freeEntries.add(entry);
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
/*     */   public void createdEntry(BasicPoolEntry entry) {
/* 228 */     Args.check(this.route.equals(entry.getPlannedRoute()), "Entry not planned for this pool");
/* 229 */     this.numEntries++;
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
/*     */ 
/*     */   
/*     */   public boolean deleteEntry(BasicPoolEntry entry) {
/* 245 */     boolean found = this.freeEntries.remove(entry);
/* 246 */     if (found) {
/* 247 */       this.numEntries--;
/*     */     }
/* 249 */     return found;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void dropEntry() {
/* 260 */     Asserts.check((this.numEntries > 0), "There is no entry that could be dropped");
/* 261 */     this.numEntries--;
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
/*     */   public void queueThread(WaitingThread wt) {
/* 274 */     Args.notNull(wt, "Waiting thread");
/* 275 */     this.waitingThreads.add(wt);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean hasThread() {
/* 286 */     return !this.waitingThreads.isEmpty();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public WaitingThread nextThread() {
/* 296 */     return this.waitingThreads.peek();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void removeThread(WaitingThread wt) {
/* 306 */     if (wt == null) {
/*     */       return;
/*     */     }
/*     */     
/* 310 */     this.waitingThreads.remove(wt);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\impl\conn\tsccm\RouteSpecificPool.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */