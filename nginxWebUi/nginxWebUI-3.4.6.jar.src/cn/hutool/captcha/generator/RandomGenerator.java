/*    */ package cn.hutool.captcha.generator;
/*    */ 
/*    */ import cn.hutool.core.util.RandomUtil;
/*    */ import cn.hutool.core.util.StrUtil;
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
/*    */ public class RandomGenerator
/*    */   extends AbstractGenerator
/*    */ {
/*    */   private static final long serialVersionUID = -7802758587765561876L;
/*    */   
/*    */   public RandomGenerator(int count) {
/* 22 */     super(count);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public RandomGenerator(String baseStr, int length) {
/* 32 */     super(baseStr, length);
/*    */   }
/*    */ 
/*    */   
/*    */   public String generate() {
/* 37 */     return RandomUtil.randomString(this.baseStr, this.length);
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean verify(String code, String userInputCode) {
/* 42 */     if (StrUtil.isNotBlank(userInputCode)) {
/* 43 */       return StrUtil.equalsIgnoreCase(code, userInputCode);
/*    */     }
/* 45 */     return false;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\captcha\generator\RandomGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */