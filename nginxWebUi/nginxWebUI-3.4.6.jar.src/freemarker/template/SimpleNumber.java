/*    */ package freemarker.template;
/*    */ 
/*    */ import java.io.Serializable;
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
/*    */ public final class SimpleNumber
/*    */   implements TemplateNumberModel, Serializable
/*    */ {
/*    */   private final Number value;
/*    */   
/*    */   public SimpleNumber(Number value) {
/* 39 */     this.value = value;
/*    */   }
/*    */   
/*    */   public SimpleNumber(byte val) {
/* 43 */     this.value = Byte.valueOf(val);
/*    */   }
/*    */   
/*    */   public SimpleNumber(short val) {
/* 47 */     this.value = Short.valueOf(val);
/*    */   }
/*    */   
/*    */   public SimpleNumber(int val) {
/* 51 */     this.value = Integer.valueOf(val);
/*    */   }
/*    */   
/*    */   public SimpleNumber(long val) {
/* 55 */     this.value = Long.valueOf(val);
/*    */   }
/*    */   
/*    */   public SimpleNumber(float val) {
/* 59 */     this.value = Float.valueOf(val);
/*    */   }
/*    */   
/*    */   public SimpleNumber(double val) {
/* 63 */     this.value = Double.valueOf(val);
/*    */   }
/*    */ 
/*    */   
/*    */   public Number getAsNumber() {
/* 68 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 73 */     return this.value.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\SimpleNumber.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */