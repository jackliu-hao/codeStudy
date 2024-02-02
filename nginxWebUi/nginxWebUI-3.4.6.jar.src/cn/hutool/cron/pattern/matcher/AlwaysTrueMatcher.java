/*    */ package cn.hutool.cron.pattern.matcher;
/*    */ 
/*    */ import cn.hutool.core.util.StrUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class AlwaysTrueMatcher
/*    */   implements PartMatcher
/*    */ {
/* 12 */   public static AlwaysTrueMatcher INSTANCE = new AlwaysTrueMatcher();
/*    */ 
/*    */   
/*    */   public boolean match(Integer t) {
/* 16 */     return true;
/*    */   }
/*    */ 
/*    */   
/*    */   public int nextAfter(int value) {
/* 21 */     return value;
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 26 */     return StrUtil.format("[Matcher]: always true.", new Object[0]);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cron\pattern\matcher\AlwaysTrueMatcher.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */