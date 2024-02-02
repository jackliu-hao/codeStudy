/*    */ package org.antlr.v4.runtime.dfa;
/*    */ 
/*    */ import org.antlr.v4.runtime.VocabularyImpl;
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
/*    */ public class LexerDFASerializer
/*    */   extends DFASerializer
/*    */ {
/*    */   public LexerDFASerializer(DFA dfa) {
/* 37 */     super(dfa, VocabularyImpl.EMPTY_VOCABULARY);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   protected String getEdgeLabel(int i) {
/* 43 */     return "'" + (char)i + "'";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\dfa\LexerDFASerializer.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */