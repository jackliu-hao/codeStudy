package com.wf.captcha.base;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

public abstract class ArithmeticCaptchaAbstract extends Captcha {
   private String arithmeticString;

   public ArithmeticCaptchaAbstract() {
      this.setLen(2);
   }

   protected char[] alphas() {
      StringBuilder sb = new StringBuilder();

      for(int i = 0; i < this.len; ++i) {
         sb.append(num(10));
         if (i < this.len - 1) {
            int type = num(1, 4);
            if (type == 1) {
               sb.append("+");
            } else if (type == 2) {
               sb.append("-");
            } else if (type == 3) {
               sb.append("x");
            }
         }
      }

      ScriptEngineManager manager = new ScriptEngineManager();
      ScriptEngine engine = manager.getEngineByName("javascript");

      try {
         this.chars = String.valueOf(engine.eval(sb.toString().replaceAll("x", "*")));
      } catch (ScriptException var5) {
         var5.printStackTrace();
      }

      sb.append("=?");
      this.arithmeticString = sb.toString();
      return this.chars.toCharArray();
   }

   public String getArithmeticString() {
      this.checkAlpha();
      return this.arithmeticString;
   }

   public void setArithmeticString(String arithmeticString) {
      this.arithmeticString = arithmeticString;
   }
}
