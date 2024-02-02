/*    */ package org.yaml.snakeyaml.tokens;
/*    */ 
/*    */ import java.util.Objects;
/*    */ import org.yaml.snakeyaml.comments.CommentType;
/*    */ import org.yaml.snakeyaml.error.Mark;
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
/*    */ public final class CommentToken
/*    */   extends Token
/*    */ {
/*    */   private final CommentType type;
/*    */   private final String value;
/*    */   
/*    */   public CommentToken(CommentType type, String value, Mark startMark, Mark endMark) {
/* 28 */     super(startMark, endMark);
/* 29 */     Objects.requireNonNull(type);
/* 30 */     this.type = type;
/* 31 */     Objects.requireNonNull(value);
/* 32 */     this.value = value;
/*    */   }
/*    */   
/*    */   public CommentType getCommentType() {
/* 36 */     return this.type;
/*    */   }
/*    */   
/*    */   public String getValue() {
/* 40 */     return this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public Token.ID getTokenId() {
/* 45 */     return Token.ID.Comment;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\yaml\snakeyaml\tokens\CommentToken.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */