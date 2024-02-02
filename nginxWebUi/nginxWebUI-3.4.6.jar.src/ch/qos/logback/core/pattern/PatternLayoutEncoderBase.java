/*    */ package ch.qos.logback.core.pattern;
/*    */ 
/*    */ import ch.qos.logback.core.Layout;
/*    */ import ch.qos.logback.core.encoder.LayoutWrappingEncoder;
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
/*    */ public class PatternLayoutEncoderBase<E>
/*    */   extends LayoutWrappingEncoder<E>
/*    */ {
/*    */   String pattern;
/*    */   protected boolean outputPatternAsHeader = false;
/*    */   
/*    */   public String getPattern() {
/* 27 */     return this.pattern;
/*    */   }
/*    */   
/*    */   public void setPattern(String pattern) {
/* 31 */     this.pattern = pattern;
/*    */   }
/*    */   
/*    */   public boolean isOutputPatternAsHeader() {
/* 35 */     return this.outputPatternAsHeader;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setOutputPatternAsHeader(boolean outputPatternAsHeader) {
/* 45 */     this.outputPatternAsHeader = outputPatternAsHeader;
/*    */   }
/*    */   
/*    */   public boolean isOutputPatternAsPresentationHeader() {
/* 49 */     return this.outputPatternAsHeader;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setOutputPatternAsPresentationHeader(boolean outputPatternAsHeader) {
/* 56 */     addWarn("[outputPatternAsPresentationHeader] property is deprecated. Please use [outputPatternAsHeader] option instead.");
/* 57 */     this.outputPatternAsHeader = outputPatternAsHeader;
/*    */   }
/*    */ 
/*    */   
/*    */   public void setLayout(Layout<E> layout) {
/* 62 */     throw new UnsupportedOperationException("one cannot set the layout of " + getClass().getName());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\pattern\PatternLayoutEncoderBase.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */