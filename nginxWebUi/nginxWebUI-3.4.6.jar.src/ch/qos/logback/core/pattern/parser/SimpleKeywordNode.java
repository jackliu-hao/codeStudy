/*    */ package ch.qos.logback.core.pattern.parser;
/*    */ 
/*    */ import java.util.List;
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
/*    */ public class SimpleKeywordNode
/*    */   extends FormattingNode
/*    */ {
/*    */   List<String> optionList;
/*    */   
/*    */   SimpleKeywordNode(Object value) {
/* 23 */     super(1, value);
/*    */   }
/*    */   
/*    */   protected SimpleKeywordNode(int type, Object value) {
/* 27 */     super(type, value);
/*    */   }
/*    */   
/*    */   public List<String> getOptions() {
/* 31 */     return this.optionList;
/*    */   }
/*    */   
/*    */   public void setOptions(List<String> optionList) {
/* 35 */     this.optionList = optionList;
/*    */   }
/*    */   
/*    */   public boolean equals(Object o) {
/* 39 */     if (!super.equals(o)) {
/* 40 */       return false;
/*    */     }
/*    */     
/* 43 */     if (!(o instanceof SimpleKeywordNode)) {
/* 44 */       return false;
/*    */     }
/* 46 */     SimpleKeywordNode r = (SimpleKeywordNode)o;
/*    */     
/* 48 */     return (this.optionList != null) ? this.optionList.equals(r.optionList) : ((r.optionList == null));
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 53 */     return super.hashCode();
/*    */   }
/*    */   
/*    */   public String toString() {
/* 57 */     StringBuilder buf = new StringBuilder();
/* 58 */     if (this.optionList == null) {
/* 59 */       buf.append("KeyWord(" + this.value + "," + this.formatInfo + ")");
/*    */     } else {
/* 61 */       buf.append("KeyWord(" + this.value + ", " + this.formatInfo + "," + this.optionList + ")");
/*    */     } 
/* 63 */     buf.append(printNext());
/* 64 */     return buf.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\ch\qos\logback\core\pattern\parser\SimpleKeywordNode.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */