/*    */ package cn.hutool.core.math;
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
/*    */ public class BitStatusUtil
/*    */ {
/*    */   public static int add(int states, int stat) {
/* 22 */     check(new int[] { states, stat });
/* 23 */     return states | stat;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static boolean has(int states, int stat) {
/* 34 */     check(new int[] { states, stat });
/* 35 */     return ((states & stat) == stat);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static int remove(int states, int stat) {
/* 46 */     check(new int[] { states, stat });
/* 47 */     if (has(states, stat)) {
/* 48 */       return states ^ stat;
/*    */     }
/* 50 */     return states;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public static int clear() {
/* 59 */     return 0;
/*    */   }
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
/*    */   private static void check(int... args) {
/* 72 */     for (int arg : args) {
/* 73 */       if (arg < 0) {
/* 74 */         throw new IllegalArgumentException(arg + " 必须大于等于0");
/*    */       }
/* 76 */       if ((arg & 0x1) == 1)
/* 77 */         throw new IllegalArgumentException(arg + " 不是偶数"); 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\math\BitStatusUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */