/*    */ package cn.hutool.core.text.escape;
/*    */ 
/*    */ import cn.hutool.core.text.StrBuilder;
/*    */ import cn.hutool.core.text.replacer.StrReplacer;
/*    */ import cn.hutool.core.util.CharUtil;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NumericEntityUnescaper
/*    */   extends StrReplacer
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   protected int replace(CharSequence str, int pos, StrBuilder out) {
/* 18 */     int len = str.length();
/*    */     
/* 20 */     if (str.charAt(pos) == '&' && pos < len - 2 && str.charAt(pos + 1) == '#') {
/* 21 */       int start = pos + 2;
/* 22 */       boolean isHex = false;
/* 23 */       char firstChar = str.charAt(start);
/* 24 */       if (firstChar == 'x' || firstChar == 'X') {
/* 25 */         start++;
/* 26 */         isHex = true;
/*    */       } 
/*    */ 
/*    */       
/* 30 */       if (start == len) {
/* 31 */         return 0;
/*    */       }
/*    */       
/* 34 */       int end = start;
/* 35 */       while (end < len && CharUtil.isHexChar(str.charAt(end))) {
/* 36 */         end++;
/*    */       }
/* 38 */       boolean isSemiNext = (end != len && str.charAt(end) == ';');
/* 39 */       if (isSemiNext) {
/*    */         int entityValue;
/*    */         try {
/* 42 */           if (isHex) {
/* 43 */             entityValue = Integer.parseInt(str.subSequence(start, end).toString(), 16);
/*    */           } else {
/* 45 */             entityValue = Integer.parseInt(str.subSequence(start, end).toString(), 10);
/*    */           } 
/* 47 */         } catch (NumberFormatException nfe) {
/* 48 */           return 0;
/*    */         } 
/* 50 */         out.append((char)entityValue);
/* 51 */         return 2 + end - start + (isHex ? 1 : 0) + 1;
/*    */       } 
/*    */     } 
/* 54 */     return 0;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\escape\NumericEntityUnescaper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */