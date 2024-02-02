/*    */ package cn.hutool.core.text.escape;
/*    */ 
/*    */ import cn.hutool.core.text.replacer.LookupReplacer;
/*    */ import cn.hutool.core.text.replacer.StrReplacer;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Html4Unescape
/*    */   extends XmlUnescape
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 14 */   protected static final String[][] ISO8859_1_UNESCAPE = InternalEscapeUtil.invert(Html4Escape.ISO8859_1_ESCAPE);
/* 15 */   protected static final String[][] HTML40_EXTENDED_UNESCAPE = InternalEscapeUtil.invert(Html4Escape.HTML40_EXTENDED_ESCAPE);
/*    */ 
/*    */   
/*    */   public Html4Unescape() {
/* 19 */     addChain((StrReplacer)new LookupReplacer(ISO8859_1_UNESCAPE));
/* 20 */     addChain((StrReplacer)new LookupReplacer(HTML40_EXTENDED_UNESCAPE));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\escape\Html4Unescape.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */