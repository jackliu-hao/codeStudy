/*    */ package freemarker.ext.beans;
/*    */ 
/*    */ import java.util.AbstractList;
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
/*    */ 
/*    */ class NonPrimitiveArrayBackedReadOnlyList
/*    */   extends AbstractList
/*    */ {
/*    */   private final Object[] array;
/*    */   
/*    */   NonPrimitiveArrayBackedReadOnlyList(Object[] array) {
/* 29 */     this.array = array;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object get(int index) {
/* 34 */     return this.array[index];
/*    */   }
/*    */ 
/*    */   
/*    */   public int size() {
/* 39 */     return this.array.length;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\NonPrimitiveArrayBackedReadOnlyList.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */