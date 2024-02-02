/*    */ package cn.hutool.core.text.escape;
/*    */ 
/*    */ import cn.hutool.core.text.replacer.LookupReplacer;
/*    */ import cn.hutool.core.text.replacer.ReplacerChain;
/*    */ import cn.hutool.core.text.replacer.StrReplacer;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class XmlUnescape
/*    */   extends ReplacerChain
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 15 */   protected static final String[][] BASIC_UNESCAPE = InternalEscapeUtil.invert(XmlEscape.BASIC_ESCAPE);
/*    */   
/* 17 */   protected static final String[][] OTHER_UNESCAPE = new String[][] { { "&apos;", "'" } };
/*    */ 
/*    */ 
/*    */   
/*    */   public XmlUnescape() {
/* 22 */     super(new StrReplacer[0]);
/* 23 */     addChain((StrReplacer)new LookupReplacer(BASIC_UNESCAPE));
/* 24 */     addChain(new NumericEntityUnescaper());
/* 25 */     addChain((StrReplacer)new LookupReplacer(OTHER_UNESCAPE));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\escape\XmlUnescape.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */