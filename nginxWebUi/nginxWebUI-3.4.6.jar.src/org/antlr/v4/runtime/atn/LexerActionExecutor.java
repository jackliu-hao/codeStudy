/*     */ package org.antlr.v4.runtime.atn;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import org.antlr.v4.runtime.CharStream;
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
/*     */ public class LexerActionExecutor
/*     */ {
/*     */   private final LexerAction[] lexerActions;
/*     */   private final int hashCode;
/*     */   
/*     */   public LexerActionExecutor(LexerAction[] lexerActions) {
/*  66 */     this.lexerActions = lexerActions;
/*     */     
/*  68 */     int hash = MurmurHash.initialize();
/*  69 */     for (LexerAction lexerAction : lexerActions) {
/*  70 */       hash = MurmurHash.update(hash, lexerAction);
/*     */     }
/*     */     
/*  73 */     this.hashCode = MurmurHash.finish(hash, lexerActions.length);
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static LexerActionExecutor append(LexerActionExecutor lexerActionExecutor, LexerAction lexerAction) {
/*  92 */     if (lexerActionExecutor == null) {
/*  93 */       return new LexerActionExecutor(new LexerAction[] { lexerAction });
/*     */     }
/*     */     
/*  96 */     LexerAction[] lexerActions = Arrays.<LexerAction>copyOf(lexerActionExecutor.lexerActions, lexerActionExecutor.lexerActions.length + 1);
/*  97 */     lexerActions[lexerActions.length - 1] = lexerAction;
/*  98 */     return new LexerActionExecutor(lexerActions);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LexerActionExecutor fixOffsetBeforeMatch(int offset) {
/* 131 */     LexerAction[] updatedLexerActions = null;
/* 132 */     for (int i = 0; i < this.lexerActions.length; i++) {
/* 133 */       if (this.lexerActions[i].isPositionDependent() && !(this.lexerActions[i] instanceof LexerIndexedCustomAction)) {
/* 134 */         if (updatedLexerActions == null) {
/* 135 */           updatedLexerActions = (LexerAction[])this.lexerActions.clone();
/*     */         }
/*     */         
/* 138 */         updatedLexerActions[i] = new LexerIndexedCustomAction(offset, this.lexerActions[i]);
/*     */       } 
/*     */     } 
/*     */     
/* 142 */     if (updatedLexerActions == null) {
/* 143 */       return this;
/*     */     }
/*     */     
/* 146 */     return new LexerActionExecutor(updatedLexerActions);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public LexerAction[] getLexerActions() {
/* 154 */     return this.lexerActions;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void execute(Lexer lexer, CharStream input, int startIndex) {
/* 177 */     boolean requiresSeek = false;
/* 178 */     int stopIndex = input.index();
/*     */     try {
/* 180 */       for (LexerAction lexerAction : this.lexerActions) {
/* 181 */         if (lexerAction instanceof LexerIndexedCustomAction) {
/* 182 */           int offset = ((LexerIndexedCustomAction)lexerAction).getOffset();
/* 183 */           input.seek(startIndex + offset);
/* 184 */           lexerAction = ((LexerIndexedCustomAction)lexerAction).getAction();
/* 185 */           requiresSeek = (startIndex + offset != stopIndex);
/*     */         }
/* 187 */         else if (lexerAction.isPositionDependent()) {
/* 188 */           input.seek(stopIndex);
/* 189 */           requiresSeek = false;
/*     */         } 
/*     */         
/* 192 */         lexerAction.execute(lexer);
/*     */       } 
/*     */     } finally {
/*     */       
/* 196 */       if (requiresSeek) {
/* 197 */         input.seek(stopIndex);
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 204 */     return this.hashCode;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object obj) {
/* 209 */     if (obj == this) {
/* 210 */       return true;
/*     */     }
/* 212 */     if (!(obj instanceof LexerActionExecutor)) {
/* 213 */       return false;
/*     */     }
/*     */     
/* 216 */     LexerActionExecutor other = (LexerActionExecutor)obj;
/* 217 */     return (this.hashCode == other.hashCode && Arrays.equals((Object[])this.lexerActions, (Object[])other.lexerActions));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\antlr\v4\runtime\atn\LexerActionExecutor.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */