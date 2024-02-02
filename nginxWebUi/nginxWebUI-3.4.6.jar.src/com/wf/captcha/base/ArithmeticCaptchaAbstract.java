/*    */ package com.wf.captcha.base;
/*    */ 
/*    */ import javax.script.ScriptEngine;
/*    */ import javax.script.ScriptEngineManager;
/*    */ import javax.script.ScriptException;
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class ArithmeticCaptchaAbstract
/*    */   extends Captcha
/*    */ {
/*    */   private String arithmeticString;
/*    */   
/*    */   public ArithmeticCaptchaAbstract() {
/* 15 */     setLen(2);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   protected char[] alphas() {
/* 25 */     StringBuilder sb = new StringBuilder();
/* 26 */     for (int i = 0; i < this.len; i++) {
/* 27 */       sb.append(num(10));
/* 28 */       if (i < this.len - 1) {
/* 29 */         int type = num(1, 4);
/* 30 */         if (type == 1) {
/* 31 */           sb.append("+");
/* 32 */         } else if (type == 2) {
/* 33 */           sb.append("-");
/* 34 */         } else if (type == 3) {
/* 35 */           sb.append("x");
/*    */         } 
/*    */       } 
/*    */     } 
/* 39 */     ScriptEngineManager manager = new ScriptEngineManager();
/* 40 */     ScriptEngine engine = manager.getEngineByName("javascript");
/*    */     try {
/* 42 */       this.chars = String.valueOf(engine.eval(sb.toString().replaceAll("x", "*")));
/* 43 */     } catch (ScriptException e) {
/* 44 */       e.printStackTrace();
/*    */     } 
/* 46 */     sb.append("=?");
/* 47 */     this.arithmeticString = sb.toString();
/* 48 */     return this.chars.toCharArray();
/*    */   }
/*    */   
/*    */   public String getArithmeticString() {
/* 52 */     checkAlpha();
/* 53 */     return this.arithmeticString;
/*    */   }
/*    */   
/*    */   public void setArithmeticString(String arithmeticString) {
/* 57 */     this.arithmeticString = arithmeticString;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\wf\captcha\base\ArithmeticCaptchaAbstract.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */