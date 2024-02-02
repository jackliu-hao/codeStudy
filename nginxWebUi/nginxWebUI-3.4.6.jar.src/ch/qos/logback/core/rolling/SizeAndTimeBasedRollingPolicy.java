/*    */ package ch.qos.logback.core.rolling;
/*    */ 
/*    */ import ch.qos.logback.core.util.FileSize;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class SizeAndTimeBasedRollingPolicy<E>
/*    */   extends TimeBasedRollingPolicy<E>
/*    */ {
/*    */   FileSize maxFileSize;
/*    */   
/*    */   public void start() {
/* 14 */     SizeAndTimeBasedFNATP<E> sizeAndTimeBasedFNATP = new SizeAndTimeBasedFNATP<E>(SizeAndTimeBasedFNATP.Usage.EMBEDDED);
/* 15 */     if (this.maxFileSize == null) {
/* 16 */       addError("maxFileSize property is mandatory.");
/*    */       return;
/*    */     } 
/* 19 */     addInfo("Archive files will be limited to [" + this.maxFileSize + "] each.");
/*    */ 
/*    */     
/* 22 */     sizeAndTimeBasedFNATP.setMaxFileSize(this.maxFileSize);
/* 23 */     this.timeBasedFileNamingAndTriggeringPolicy = sizeAndTimeBasedFNATP;
/*    */     
/* 25 */     if (!isUnboundedTotalSizeCap() && this.totalSizeCap.getSize() < this.maxFileSize.getSize()) {
/* 26 */       addError("totalSizeCap of [" + this.totalSizeCap + "] is smaller than maxFileSize [" + this.maxFileSize + "] which is non-sensical");
/*    */       
/*    */       return;
/*    */     } 
/*    */     
/* 31 */     super.start();
/*    */   }
/*    */ 
/*    */   
/*    */   public void setMaxFileSize(FileSize aMaxFileSize) {
/* 36 */     this.maxFileSize = aMaxFileSize;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 41 */     return "c.q.l.core.rolling.SizeAndTimeBasedRollingPolicy@" + hashCode();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\rolling\SizeAndTimeBasedRollingPolicy.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */