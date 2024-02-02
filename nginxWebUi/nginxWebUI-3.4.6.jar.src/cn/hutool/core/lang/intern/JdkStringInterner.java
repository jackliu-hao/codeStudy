/*    */ package cn.hutool.core.lang.intern;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JdkStringInterner
/*    */   implements Interner<String>
/*    */ {
/*    */   public String intern(String sample) {
/* 12 */     if (null == sample) {
/* 13 */       return null;
/*    */     }
/* 15 */     return sample.intern();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\intern\JdkStringInterner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */