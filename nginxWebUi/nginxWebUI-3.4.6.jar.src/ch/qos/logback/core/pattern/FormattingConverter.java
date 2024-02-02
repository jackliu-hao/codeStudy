/*    */ package ch.qos.logback.core.pattern;
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
/*    */ public abstract class FormattingConverter<E>
/*    */   extends Converter<E>
/*    */ {
/*    */   static final int INITIAL_BUF_SIZE = 256;
/*    */   static final int MAX_CAPACITY = 1024;
/*    */   FormatInfo formattingInfo;
/*    */   
/*    */   public final FormatInfo getFormattingInfo() {
/* 24 */     return this.formattingInfo;
/*    */   }
/*    */   
/*    */   public final void setFormattingInfo(FormatInfo formattingInfo) {
/* 28 */     if (this.formattingInfo != null) {
/* 29 */       throw new IllegalStateException("FormattingInfo has been already set");
/*    */     }
/* 31 */     this.formattingInfo = formattingInfo;
/*    */   }
/*    */ 
/*    */   
/*    */   public final void write(StringBuilder buf, E event) {
/* 36 */     String s = convert(event);
/*    */     
/* 38 */     if (this.formattingInfo == null) {
/* 39 */       buf.append(s);
/*    */       
/*    */       return;
/*    */     } 
/* 43 */     int min = this.formattingInfo.getMin();
/* 44 */     int max = this.formattingInfo.getMax();
/*    */     
/* 46 */     if (s == null) {
/* 47 */       if (0 < min) {
/* 48 */         SpacePadder.spacePad(buf, min);
/*    */       }
/*    */       return;
/*    */     } 
/* 52 */     int len = s.length();
/*    */     
/* 54 */     if (len > max) {
/* 55 */       if (this.formattingInfo.isLeftTruncate()) {
/* 56 */         buf.append(s.substring(len - max));
/*    */       } else {
/* 58 */         buf.append(s.substring(0, max));
/*    */       } 
/* 60 */     } else if (len < min) {
/* 61 */       if (this.formattingInfo.isLeftPad()) {
/* 62 */         SpacePadder.leftPad(buf, s, min);
/*    */       } else {
/* 64 */         SpacePadder.rightPad(buf, s, min);
/*    */       } 
/*    */     } else {
/* 67 */       buf.append(s);
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\pattern\FormattingConverter.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */