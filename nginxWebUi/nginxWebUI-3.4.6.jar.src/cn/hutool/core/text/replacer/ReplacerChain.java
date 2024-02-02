/*    */ package cn.hutool.core.text.replacer;
/*    */ 
/*    */ import cn.hutool.core.lang.Chain;
/*    */ import cn.hutool.core.text.StrBuilder;
/*    */ import java.util.Iterator;
/*    */ import java.util.LinkedList;
/*    */ import java.util.List;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ReplacerChain
/*    */   extends StrReplacer
/*    */   implements Chain<StrReplacer, ReplacerChain>
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 19 */   private final List<StrReplacer> replacers = new LinkedList<>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public ReplacerChain(StrReplacer... strReplacers) {
/* 27 */     for (StrReplacer strReplacer : strReplacers) {
/* 28 */       addChain(strReplacer);
/*    */     }
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Iterator<StrReplacer> iterator() {
/* 35 */     return this.replacers.iterator();
/*    */   }
/*    */ 
/*    */   
/*    */   public ReplacerChain addChain(StrReplacer element) {
/* 40 */     this.replacers.add(element);
/* 41 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   protected int replace(CharSequence str, int pos, StrBuilder out) {
/* 46 */     int consumed = 0;
/* 47 */     for (StrReplacer strReplacer : this.replacers) {
/* 48 */       consumed = strReplacer.replace(str, pos, out);
/* 49 */       if (0 != consumed) {
/* 50 */         return consumed;
/*    */       }
/*    */     } 
/* 53 */     return consumed;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\replacer\ReplacerChain.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */