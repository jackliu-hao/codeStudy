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
/*     */ public final class LexerChannelAction
/*     */   implements LexerAction
/*     */ {
/*     */   private final int channel;
/*     */   
/*     */   public LexerChannelAction(int channel) {
/*  52 */     this.channel = channel;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int getChannel() {
/*  61 */     return this.channel;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LexerActionType getActionType() {
/*  70 */     return LexerActionType.CHANNEL;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isPositionDependent() {
/*  79 */     return false;
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
/*  90 */     lexer.setChannel(this.channel);
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  95 */     int hash = MurmurHash.initialize();
/*  96 */     hash = MurmurHash.update(hash, getActionType().ordinal());
/*  97 */     hash = MurmurHash.update(hash, this.channel);
/*  98 */     return MurmurHash.finish(hash, 2);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 103 */     if (obj == this) {
/* 104 */       return true;
/*     */     }
/* 106 */     if (!(obj instanceof LexerChannelAction)) {
/* 107 */       return false;
/*     */     }
/*     */     
/* 110 */     return (this.channel == ((LexerChannelAction)obj).channel);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 115 */     return String.format("channel(%d)", new Object[] { Integer.valueOf(this.channel) });
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\LexerChannelAction.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */