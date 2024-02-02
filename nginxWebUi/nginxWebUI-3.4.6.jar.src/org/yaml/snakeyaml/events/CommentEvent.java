/*    */ package org.yaml.snakeyaml.events;
/*    */ 
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
/*    */ 
/*    */ 
/*    */ public final class CommentEvent
/*    */   extends Event
/*    */ {
/*    */   private final CommentType type;
/*    */   private final String value;
/*    */   
/*    */   public CommentEvent(CommentType type, String value, Mark startMark, Mark endMark) {
/* 29 */     super(startMark, endMark);
/* 30 */     if (type == null) throw new NullPointerException("Event Type must be provided."); 
/* 31 */     this.type = type;
/* 32 */     if (value == null) throw new NullPointerException("Value must be provided."); 
/* 33 */     this.value = value;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getValue() {
/* 45 */     return this.value;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public CommentType getCommentType() {
/* 54 */     return this.type;
/*    */   }
/*    */ 
/*    */   
/*    */   protected String getArguments() {
/* 59 */     return super.getArguments() + "type=" + this.type + ", value=" + this.value;
/*    */   }
/*    */ 
/*    */   
/*    */   public Event.ID getEventId() {
/* 64 */     return Event.ID.Comment;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\yaml\snakeyaml\events\CommentEvent.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */