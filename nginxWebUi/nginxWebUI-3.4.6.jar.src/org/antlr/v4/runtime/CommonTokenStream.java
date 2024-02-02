/*     */ package org.antlr.v4.runtime;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CommonTokenStream
/*     */   extends BufferedTokenStream
/*     */ {
/*  65 */   protected int channel = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CommonTokenStream(TokenSource tokenSource) {
/*  74 */     super(tokenSource);
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
/*     */   public CommonTokenStream(TokenSource tokenSource, int channel) {
/*  88 */     this(tokenSource);
/*  89 */     this.channel = channel;
/*     */   }
/*     */ 
/*     */   
/*     */   protected int adjustSeekIndex(int i) {
/*  94 */     return nextTokenOnChannel(i, this.channel);
/*     */   }
/*     */ 
/*     */   
/*     */   protected Token LB(int k) {
/*  99 */     if (k == 0 || this.p - k < 0) return null;
/*     */     
/* 101 */     int i = this.p;
/* 102 */     int n = 1;
/*     */     
/* 104 */     while (n <= k) {
/*     */       
/* 106 */       i = previousTokenOnChannel(i - 1, this.channel);
/* 107 */       n++;
/*     */     } 
/* 109 */     if (i < 0) return null; 
/* 110 */     return this.tokens.get(i);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Token LT(int k) {
/* 116 */     lazyInit();
/* 117 */     if (k == 0) return null; 
/* 118 */     if (k < 0) return LB(-k); 
/* 119 */     int i = this.p;
/* 120 */     int n = 1;
/*     */     
/* 122 */     while (n < k) {
/*     */       
/* 124 */       if (sync(i + 1)) {
/* 125 */         i = nextTokenOnChannel(i + 1, this.channel);
/*     */       }
/* 127 */       n++;
/*     */     } 
/*     */     
/* 130 */     return this.tokens.get(i);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getNumberOfOnChannelTokens() {
/* 135 */     int n = 0;
/* 136 */     fill();
/* 137 */     for (int i = 0; i < this.tokens.size(); i++) {
/* 138 */       Token t = this.tokens.get(i);
/* 139 */       if (t.getChannel() == this.channel) n++; 
/* 140 */       if (t.getType() == -1)
/*     */         break; 
/* 142 */     }  return n;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\CommonTokenStream.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */