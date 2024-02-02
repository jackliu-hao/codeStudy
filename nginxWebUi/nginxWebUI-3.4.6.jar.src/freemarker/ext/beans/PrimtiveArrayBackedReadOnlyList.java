/*    */ package freemarker.ext.beans;
/*    */ 
/*    */ import java.lang.reflect.Array;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ class PrimtiveArrayBackedReadOnlyList
/*    */   extends AbstractList
/*    */ {
/*    */   private final Object array;
/*    */   
/*    */   PrimtiveArrayBackedReadOnlyList(Object array) {
/* 34 */     this.array = array;
/*    */   }
/*    */ 
/*    */   
/*    */   public Object get(int index) {
/* 39 */     return Array.get(this.array, index);
/*    */   }
/*    */ 
/*    */   
/*    */   public int size() {
/* 44 */     return Array.getLength(this.array);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\ext\beans\PrimtiveArrayBackedReadOnlyList.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */