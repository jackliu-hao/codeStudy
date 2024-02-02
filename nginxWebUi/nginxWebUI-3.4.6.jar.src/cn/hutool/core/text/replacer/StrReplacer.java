/*    */ package cn.hutool.core.text.replacer;
/*    */ 
/*    */ import cn.hutool.core.lang.Replacer;
/*    */ import cn.hutool.core.text.StrBuilder;
/*    */ import java.io.Serializable;
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
/*    */ public abstract class StrReplacer
/*    */   implements Replacer<CharSequence>, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public CharSequence replace(CharSequence t) {
/* 30 */     int len = t.length();
/* 31 */     StrBuilder builder = StrBuilder.create(len);
/* 32 */     int pos = 0;
/*    */     
/* 34 */     while (pos < len) {
/* 35 */       int consumed = replace(t, pos, builder);
/* 36 */       if (0 == consumed) {
/*    */         
/* 38 */         builder.append(t.charAt(pos));
/* 39 */         pos++;
/*    */       } 
/* 41 */       pos += consumed;
/*    */     } 
/* 43 */     return (CharSequence)builder;
/*    */   }
/*    */   
/*    */   protected abstract int replace(CharSequence paramCharSequence, int paramInt, StrBuilder paramStrBuilder);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\replacer\StrReplacer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */