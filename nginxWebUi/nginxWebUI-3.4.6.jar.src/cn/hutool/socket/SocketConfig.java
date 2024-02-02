/*     */ package cn.hutool.socket;
/*     */ 
/*     */ import cn.hutool.core.util.RuntimeUtil;
/*     */ import java.io.Serializable;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SocketConfig
/*     */   implements Serializable
/*     */ {
/*     */   private static final long serialVersionUID = 1L;
/*  18 */   private static final int CPU_COUNT = RuntimeUtil.getProcessorCount();
/*     */ 
/*     */   
/*  21 */   private int threadPoolSize = CPU_COUNT;
/*     */ 
/*     */   
/*     */   private long readTimeout;
/*     */ 
/*     */   
/*     */   private long writeTimeout;
/*     */   
/*  29 */   private int readBufferSize = 8192;
/*     */   
/*  31 */   private int writeBufferSize = 8192;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getThreadPoolSize() {
/*  39 */     return this.threadPoolSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setThreadPoolSize(int threadPoolSize) {
/*  48 */     this.threadPoolSize = threadPoolSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getReadTimeout() {
/*  57 */     return this.readTimeout;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setReadTimeout(long readTimeout) {
/*  66 */     this.readTimeout = readTimeout;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public long getWriteTimeout() {
/*  75 */     return this.writeTimeout;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWriteTimeout(long writeTimeout) {
/*  84 */     this.writeTimeout = writeTimeout;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getReadBufferSize() {
/*  92 */     return this.readBufferSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setReadBufferSize(int readBufferSize) {
/* 100 */     this.readBufferSize = readBufferSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getWriteBufferSize() {
/* 108 */     return this.writeBufferSize;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWriteBufferSize(int writeBufferSize) {
/* 116 */     this.writeBufferSize = writeBufferSize;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\socket\SocketConfig.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */