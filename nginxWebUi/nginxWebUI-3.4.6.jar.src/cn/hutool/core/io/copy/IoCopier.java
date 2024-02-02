/*    */ package cn.hutool.core.io.copy;
/*    */ 
/*    */ import cn.hutool.core.io.StreamProgress;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class IoCopier<S, T>
/*    */ {
/*    */   protected final int bufferSize;
/*    */   protected final long count;
/*    */   protected StreamProgress progress;
/*    */   protected boolean flushEveryBuffer;
/*    */   
/*    */   public IoCopier(int bufferSize, long count, StreamProgress progress) {
/* 41 */     this.bufferSize = (bufferSize > 0) ? bufferSize : 8192;
/* 42 */     this.count = (count <= 0L) ? Long.MAX_VALUE : count;
/* 43 */     this.progress = progress;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public abstract long copy(S paramS, T paramT);
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected int bufferSize(long count) {
/* 62 */     return (int)Math.min(this.bufferSize, count);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public IoCopier<S, T> setFlushEveryBuffer(boolean flushEveryBuffer) {
/* 73 */     this.flushEveryBuffer = flushEveryBuffer;
/* 74 */     return this;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\copy\IoCopier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */