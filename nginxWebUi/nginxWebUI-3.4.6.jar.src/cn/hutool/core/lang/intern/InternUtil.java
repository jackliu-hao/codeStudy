/*    */ package cn.hutool.core.lang.intern;
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
/*    */ public class InternUtil
/*    */ {
/*    */   public static <T> Interner<T> createWeakInterner() {
/* 18 */     return new WeakInterner<>();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Interner<String> createJdkInterner() {
/* 28 */     return new JdkStringInterner();
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static Interner<String> createStringInterner(boolean isWeak) {
/* 38 */     return isWeak ? createWeakInterner() : createJdkInterner();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\lang\intern\InternUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */