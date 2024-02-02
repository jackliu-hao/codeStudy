/*    */ package cn.hutool.http;
/*    */ 
/*    */ import java.util.Iterator;
/*    */ import java.util.LinkedList;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ @FunctionalInterface
/*    */ public interface HttpInterceptor<T extends HttpBase<T>>
/*    */ {
/*    */   void process(T paramT);
/*    */   
/*    */   public static class Chain<T extends HttpBase<T>>
/*    */     implements cn.hutool.core.lang.Chain<HttpInterceptor<T>, Chain<T>>
/*    */   {
/* 32 */     private final List<HttpInterceptor<T>> interceptors = new LinkedList<>();
/*    */ 
/*    */     
/*    */     public Chain<T> addChain(HttpInterceptor<T> element) {
/* 36 */       this.interceptors.add(element);
/* 37 */       return this;
/*    */     }
/*    */ 
/*    */     
/*    */     public Iterator<HttpInterceptor<T>> iterator() {
/* 42 */       return this.interceptors.iterator();
/*    */     }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */     
/*    */     public Chain<T> clear() {
/* 52 */       this.interceptors.clear();
/* 53 */       return this;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\HttpInterceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */