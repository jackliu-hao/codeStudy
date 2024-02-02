/*    */ package ch.qos.logback.classic.turbo;
/*    */ 
/*    */ import ch.qos.logback.classic.Level;
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
/*    */ public class MDCValueLevelPair
/*    */ {
/*    */   private String value;
/*    */   private Level level;
/*    */   
/*    */   public String getValue() {
/* 29 */     return this.value;
/*    */   }
/*    */   
/*    */   public void setValue(String name) {
/* 33 */     this.value = name;
/*    */   }
/*    */   
/*    */   public Level getLevel() {
/* 37 */     return this.level;
/*    */   }
/*    */   
/*    */   public void setLevel(Level level) {
/* 41 */     this.level = level;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\classic\turbo\MDCValueLevelPair.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */