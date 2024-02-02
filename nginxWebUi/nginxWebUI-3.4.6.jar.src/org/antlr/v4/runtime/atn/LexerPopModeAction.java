/*     */ package org.antlr.v4.runtime.atn;
/*     */ 
/*     */ import org.antlr.v4.runtime.Lexer;
/*     */ import org.antlr.v4.runtime.misc.MurmurHash;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class LexerPopModeAction
/*     */   implements LexerAction
/*     */ {
/*  49 */   public static final LexerPopModeAction INSTANCE = new LexerPopModeAction();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LexerActionType getActionType() {
/*  63 */     return LexerActionType.POP_MODE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isPositionDependent() {
/*  72 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void execute(Lexer lexer) {
/*  82 */     lexer.popMode();
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  87 */     int hash = MurmurHash.initialize();
/*  88 */     hash = MurmurHash.update(hash, getActionType().ordinal());
/*  89 */     return MurmurHash.finish(hash, 1);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/*  95 */     return (obj == this);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 100 */     return "popMode";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\LexerPopModeAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */