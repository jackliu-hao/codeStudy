/*    */ package cn.hutool.captcha.generator;
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
/*    */ public abstract class AbstractGenerator
/*    */   implements CodeGenerator
/*    */ {
/*    */   private static final long serialVersionUID = 8685744597154953479L;
/*    */   protected final String baseStr;
/*    */   protected final int length;
/*    */   
/*    */   public AbstractGenerator(int count) {
/* 26 */     this("abcdefghijklmnopqrstuvwxyz0123456789", count);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public AbstractGenerator(String baseStr, int length) {
/* 36 */     this.baseStr = baseStr;
/* 37 */     this.length = length;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getLength() {
/* 46 */     return this.length;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\captcha\generator\AbstractGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */