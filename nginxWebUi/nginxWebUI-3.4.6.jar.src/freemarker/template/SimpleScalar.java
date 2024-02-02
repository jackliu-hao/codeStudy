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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class SimpleScalar
/*    */   implements TemplateScalarModel, Serializable
/*    */ {
/*    */   private final String value;
/*    */   
/*    */   public SimpleScalar(String value) {
/* 48 */     this.value = value;
/*    */   }
/*    */ 
/*    */   
/*    */   public String getAsString() {
/* 53 */     return (this.value == null) ? "" : this.value;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 59 */     return this.value;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static SimpleScalar newInstanceOrNull(String s) {
/* 68 */     return (s != null) ? new SimpleScalar(s) : null;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\template\SimpleScalar.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */