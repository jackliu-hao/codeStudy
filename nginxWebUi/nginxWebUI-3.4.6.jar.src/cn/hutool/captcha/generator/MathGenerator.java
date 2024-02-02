/*    */ package cn.hutool.captcha.generator;
/*    */ 
/*    */ import cn.hutool.core.math.Calculator;
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
/*    */ 
/*    */ public class MathGenerator
/*    */   implements CodeGenerator
/*    */ {
/*    */   private static final long serialVersionUID = -5514819971774091076L;
/*    */   private static final String operators = "+-*";
/*    */   private final int numberLength;
/*    */   
/*    */   public MathGenerator() {
/* 26 */     this(2);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MathGenerator(int numberLength) {
/* 35 */     this.numberLength = numberLength;
/*    */   }
/*    */ 
/*    */   
/*    */   public String generate() {
/* 40 */     int limit = getLimit();
/* 41 */     String number1 = Integer.toString(RandomUtil.randomInt(limit));
/* 42 */     String number2 = Integer.toString(RandomUtil.randomInt(limit));
/* 43 */     number1 = StrUtil.padAfter(number1, this.numberLength, ' ');
/* 44 */     number2 = StrUtil.padAfter(number2, this.numberLength, ' ');
/*    */     
/* 46 */     return StrUtil.builder()
/* 47 */       .append(number1)
/* 48 */       .append(RandomUtil.randomChar("+-*"))
/* 49 */       .append(number2)
/* 50 */       .append('=').toString();
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean verify(String code, String userInputCode) {
/*    */     int result;
/*    */     try {
/* 57 */       result = Integer.parseInt(userInputCode);
/* 58 */     } catch (NumberFormatException e) {
/*    */       
/* 60 */       return false;
/*    */     } 
/*    */     
/* 63 */     int calculateResult = (int)Calculator.conversion(code);
/* 64 */     return (result == calculateResult);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public int getLength() {
/* 73 */     return this.numberLength * 2 + 2;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   private int getLimit() {
/* 82 */     return Integer.parseInt("1" + StrUtil.repeat('0', this.numberLength));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\captcha\generator\MathGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */