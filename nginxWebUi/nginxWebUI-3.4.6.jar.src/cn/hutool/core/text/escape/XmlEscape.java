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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class XmlEscape
/*    */   extends ReplacerChain
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/* 24 */   protected static final String[][] BASIC_ESCAPE = new String[][] { { "\"", "&quot;" }, { "&", "&amp;" }, { "<", "&lt;" }, { ">", "&gt;" } };
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public XmlEscape() {
/* 35 */     super(new StrReplacer[0]);
/* 36 */     addChain((StrReplacer)new LookupReplacer(BASIC_ESCAPE));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\text\escape\XmlEscape.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */