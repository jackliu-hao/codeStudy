/*     */ package org.apache.http.conn;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import org.apache.http.util.Args;
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
/*     */ 
/*     */ 
/*     */ @Deprecated
/*     */ public class BasicEofSensorWatcher
/*     */   implements EofSensorWatcher
/*     */ {
/*     */   protected final ManagedClientConnection managedConn;
/*     */   protected final boolean attemptReuse;
/*     */   
/*     */   public BasicEofSensorWatcher(ManagedClientConnection conn, boolean reuse) {
/*  59 */     Args.notNull(conn, "Connection");
/*  60 */     this.managedConn = conn;
/*  61 */     this.attemptReuse = reuse;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean eofDetected(InputStream wrapped) throws IOException {
/*     */     try {
/*  69 */       if (this.attemptReuse) {
/*     */ 
/*     */         
/*  72 */         wrapped.close();
/*  73 */         this.managedConn.markReusable();
/*     */       } 
/*     */     } finally {
/*  76 */       this.managedConn.releaseConnection();
/*     */     } 
/*  78 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean streamClosed(InputStream wrapped) throws IOException {
/*     */     try {
/*  86 */       if (this.attemptReuse) {
/*     */ 
/*     */         
/*  89 */         wrapped.close();
/*  90 */         this.managedConn.markReusable();
/*     */       } 
/*     */     } finally {
/*  93 */       this.managedConn.releaseConnection();
/*     */     } 
/*  95 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean streamAbort(InputStream wrapped) throws IOException {
/* 102 */     this.managedConn.abortConnection();
/* 103 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\conn\BasicEofSensorWatcher.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */