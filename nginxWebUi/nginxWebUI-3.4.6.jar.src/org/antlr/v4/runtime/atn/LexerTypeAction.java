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
/*     */ public class LexerTypeAction
/*     */   implements LexerAction
/*     */ {
/*     */   private final int type;
/*     */   
/*     */   public LexerTypeAction(int type) {
/*  51 */     this.type = type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getType() {
/*  59 */     return this.type;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LexerActionType getActionType() {
/*  68 */     return LexerActionType.TYPE;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isPositionDependent() {
/*  77 */     return false;
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
/*  88 */     lexer.setType(this.type);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  93 */     int hash = MurmurHash.initialize();
/*  94 */     hash = MurmurHash.update(hash, getActionType().ordinal());
/*  95 */     hash = MurmurHash.update(hash, this.type);
/*  96 */     return MurmurHash.finish(hash, 2);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 101 */     if (obj == this) {
/* 102 */       return true;
/*     */     }
/* 104 */     if (!(obj instanceof LexerTypeAction)) {
/* 105 */       return false;
/*     */     }
/*     */     
/* 108 */     return (this.type == ((LexerTypeAction)obj).type);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 113 */     return String.format("type(%d)", new Object[] { Integer.valueOf(this.type) });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\LexerTypeAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */