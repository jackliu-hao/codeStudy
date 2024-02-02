/*    */ package org.yaml.snakeyaml.comments;
/*    */ 
/*    */ import org.yaml.snakeyaml.error.Mark;
/*    */ import org.yaml.snakeyaml.events.CommentEvent;
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
/*    */ public class CommentLine
/*    */ {
/*    */   private Mark startMark;
/*    */   private Mark endMark;
/*    */   private String value;
/*    */   private CommentType commentType;
/*    */   
/*    */   public CommentLine(CommentEvent event) {
/* 31 */     this(event.getStartMark(), event.getEndMark(), event.getValue(), event.getCommentType());
/*    */   }
/*    */   
/*    */   public CommentLine(Mark startMark, Mark endMark, String value, CommentType commentType) {
/* 35 */     this.startMark = startMark;
/* 36 */     this.endMark = endMark;
/* 37 */     this.value = value;
/* 38 */     this.commentType = commentType;
/*    */   }
/*    */   
/*    */   public Mark getEndMark() {
/* 42 */     return this.endMark;
/*    */   }
/*    */   
/*    */   public Mark getStartMark() {
/* 46 */     return this.startMark;
/*    */   }
/*    */   
/*    */   public CommentType getCommentType() {
/* 50 */     return this.commentType;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getValue() {
/* 59 */     return this.value;
/*    */   }
/*    */   
/*    */   public String toString() {
/* 63 */     return "<" + getClass().getName() + " (type=" + getCommentType() + ", value=" + getValue() + ")>";
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\yaml\snakeyaml\comments\CommentLine.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */