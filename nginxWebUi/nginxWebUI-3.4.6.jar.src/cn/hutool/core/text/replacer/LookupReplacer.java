/*    */ package cn.hutool.core.text.replacer;
/*    */ 
/*    */ import cn.hutool.core.text.StrBuilder;
/*    */ import java.util.HashMap;
/*    */ import java.util.HashSet;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
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
/*    */ 
/*    */ public class LookupReplacer
/*    */   extends StrReplacer
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 30 */   private final Map<String, String> lookupMap = new HashMap<>();
/* 31 */   private final Set<Character> prefixSet = new HashSet<>(); private final int minLength;
/*    */   public LookupReplacer(String[]... lookup) {
/* 33 */     int minLength = Integer.MAX_VALUE;
/* 34 */     int maxLength = 0;
/*    */ 
/*    */     
/* 37 */     for (String[] pair : lookup) {
/* 38 */       String key = pair[0];
/* 39 */       this.lookupMap.put(key, pair[1]);
/* 40 */       this.prefixSet.add(Character.valueOf(key.charAt(0)));
/* 41 */       int keySize = key.length();
/* 42 */       if (keySize > maxLength) {
/* 43 */         maxLength = keySize;
/*    */       }
/* 45 */       if (keySize < minLength) {
/* 46 */         minLength = keySize;
/*    */       }
/*    */     } 
/* 49 */     this.maxLength = maxLength;
/* 50 */     this.minLength = minLength;
/*    */   }
/*    */   private final int maxLength;
/*    */   
/*    */   protected int replace(CharSequence str, int pos, StrBuilder out) {
/* 55 */     if (this.prefixSet.contains(Character.valueOf(str.charAt(pos)))) {
/* 56 */       int max = this.maxLength;
/* 57 */       if (pos + this.maxLength > str.length()) {
/* 58 */         max = str.length() - pos;
/*    */       }
/*    */ 
/*    */       
/* 62 */       for (int i = max; i >= this.minLength; i--) {
/* 63 */         CharSequence subSeq = str.subSequence(pos, pos + i);
/* 64 */         String result = this.lookupMap.get(subSeq.toString());
/* 65 */         if (null != result) {
/* 66 */           out.append(result);
/* 67 */           return i;
/*    */         } 
/*    */       } 
/*    */     } 
/* 71 */     return 0;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\replacer\LookupReplacer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */