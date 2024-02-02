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
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class LexerIndexedCustomAction
/*     */   implements LexerAction
/*     */ {
/*     */   private final int offset;
/*     */   private final LexerAction action;
/*     */   
/*     */   public LexerIndexedCustomAction(int offset, LexerAction action) {
/*  68 */     this.offset = offset;
/*  69 */     this.action = action;
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
/*     */   public int getOffset() {
/*  81 */     return this.offset;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LexerAction getAction() {
/*  90 */     return this.action;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LexerActionType getActionType() {
/* 101 */     return this.action.getActionType();
/*     */   }
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
/*     */   
/*     */   public void execute(Lexer lexer) {
/* 122 */     this.action.execute(lexer);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 127 */     int hash = MurmurHash.initialize();
/* 128 */     hash = MurmurHash.update(hash, this.offset);
/* 129 */     hash = MurmurHash.update(hash, this.action);
/* 130 */     return MurmurHash.finish(hash, 2);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 135 */     if (obj == this) {
/* 136 */       return true;
/*     */     }
/* 138 */     if (!(obj instanceof LexerIndexedCustomAction)) {
/* 139 */       return false;
/*     */     }
/*     */     
/* 142 */     LexerIndexedCustomAction other = (LexerIndexedCustomAction)obj;
/* 143 */     return (this.offset == other.offset && this.action.equals(other.action));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\LexerIndexedCustomAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */