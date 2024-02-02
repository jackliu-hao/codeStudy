/*    */ package org.antlr.v4.runtime.tree.pattern;
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
/*    */ class TextChunk
/*    */   extends Chunk
/*    */ {
/*    */   private final String text;
/*    */   
/*    */   public TextChunk(String text) {
/* 51 */     if (text == null) {
/* 52 */       throw new IllegalArgumentException("text cannot be null");
/*    */     }
/*    */     
/* 55 */     this.text = text;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public final String getText() {
/* 65 */     return this.text;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String toString() {
/* 76 */     return "'" + this.text + "'";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\tree\pattern\TextChunk.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */