/*    */ package cn.hutool.json;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class JSONObjectIter
/*    */   implements Iterable<JSONObject>
/*    */ {
/*    */   Iterator<Object> iterator;
/*    */   
/*    */   public JSONObjectIter(Iterator<Object> iterator) {
/* 16 */     this.iterator = iterator;
/*    */   }
/*    */ 
/*    */   
/*    */   public Iterator<JSONObject> iterator() {
/* 21 */     return new Iterator<JSONObject>()
/*    */       {
/*    */         public boolean hasNext()
/*    */         {
/* 25 */           return JSONObjectIter.this.iterator.hasNext();
/*    */         }
/*    */ 
/*    */         
/*    */         public JSONObject next() {
/* 30 */           return (JSONObject)JSONObjectIter.this.iterator.next();
/*    */         }
/*    */ 
/*    */         
/*    */         public void remove() {
/* 35 */           JSONObjectIter.this.iterator.remove();
/*    */         }
/*    */       };
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\json\JSONObjectIter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */