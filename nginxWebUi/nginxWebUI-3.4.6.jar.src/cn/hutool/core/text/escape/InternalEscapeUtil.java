/*    */ package cn.hutool.core.text.escape;
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
/*    */ class InternalEscapeUtil
/*    */ {
/*    */   public static String[][] invert(String[][] array) {
/* 17 */     String[][] newarray = new String[array.length][2];
/* 18 */     for (int i = 0; i < array.length; i++) {
/* 19 */       newarray[i][0] = array[i][1];
/* 20 */       newarray[i][1] = array[i][0];
/*    */     } 
/* 22 */     return newarray;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\escape\InternalEscapeUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */