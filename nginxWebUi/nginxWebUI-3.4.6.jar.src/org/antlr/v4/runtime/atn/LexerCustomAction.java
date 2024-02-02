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
/*     */ public final class LexerCustomAction
/*     */   implements LexerAction
/*     */ {
/*     */   private final int ruleIndex;
/*     */   private final int actionIndex;
/*     */   
/*     */   public LexerCustomAction(int ruleIndex, int actionIndex) {
/*  65 */     this.ruleIndex = ruleIndex;
/*  66 */     this.actionIndex = actionIndex;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getRuleIndex() {
/*  75 */     return this.ruleIndex;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getActionIndex() {
/*  84 */     return this.actionIndex;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LexerActionType getActionType() {
/*  94 */     return LexerActionType.CUSTOM;
/*     */   }
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
/*     */   public boolean isPositionDependent() {
/* 110 */     return true;
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
/* 121 */     lexer.action(null, this.ruleIndex, this.actionIndex);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 126 */     int hash = MurmurHash.initialize();
/* 127 */     hash = MurmurHash.update(hash, getActionType().ordinal());
/* 128 */     hash = MurmurHash.update(hash, this.ruleIndex);
/* 129 */     hash = MurmurHash.update(hash, this.actionIndex);
/* 130 */     return MurmurHash.finish(hash, 3);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 135 */     if (obj == this) {
/* 136 */       return true;
/*     */     }
/* 138 */     if (!(obj instanceof LexerCustomAction)) {
/* 139 */       return false;
/*     */     }
/*     */     
/* 142 */     LexerCustomAction other = (LexerCustomAction)obj;
/* 143 */     return (this.ruleIndex == other.ruleIndex && this.actionIndex == other.actionIndex);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\LexerCustomAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */