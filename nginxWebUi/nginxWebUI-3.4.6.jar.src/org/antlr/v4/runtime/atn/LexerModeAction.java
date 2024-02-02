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
/*     */ public final class LexerModeAction
/*     */   implements LexerAction
/*     */ {
/*     */   private final int mode;
/*     */   
/*     */   public LexerModeAction(int mode) {
/*  51 */     this.mode = mode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getMode() {
/*  60 */     return this.mode;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LexerActionType getActionType() {
/*  69 */     return LexerActionType.MODE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isPositionDependent() {
/*  78 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void execute(Lexer lexer) {
/*  89 */     lexer.mode(this.mode);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  94 */     int hash = MurmurHash.initialize();
/*  95 */     hash = MurmurHash.update(hash, getActionType().ordinal());
/*  96 */     hash = MurmurHash.update(hash, this.mode);
/*  97 */     return MurmurHash.finish(hash, 2);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 102 */     if (obj == this) {
/* 103 */       return true;
/*     */     }
/* 105 */     if (!(obj instanceof LexerModeAction)) {
/* 106 */       return false;
/*     */     }
/*     */     
/* 109 */     return (this.mode == ((LexerModeAction)obj).mode);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 114 */     return String.format("mode(%d)", new Object[] { Integer.valueOf(this.mode) });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\LexerModeAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */