/*    */ package cn.hutool.http;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public enum GlobalInterceptor
/*    */ {
/* 11 */   INSTANCE;
/*    */   GlobalInterceptor() {
/* 13 */     this.requestInterceptors = new HttpInterceptor.Chain<>();
/* 14 */     this.responseInterceptors = new HttpInterceptor.Chain<>();
/*    */   }
/*    */ 
/*    */   
/*    */   private final HttpInterceptor.Chain<HttpResponse> responseInterceptors;
/*    */   
/*    */   private final HttpInterceptor.Chain<HttpRequest> requestInterceptors;
/*    */   
/*    */   public synchronized GlobalInterceptor addRequestInterceptor(HttpInterceptor<HttpRequest> interceptor) {
/* 23 */     this.requestInterceptors.addChain(interceptor);
/* 24 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public synchronized GlobalInterceptor addResponseInterceptor(HttpInterceptor<HttpResponse> interceptor) {
/* 34 */     this.responseInterceptors.addChain(interceptor);
/* 35 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public GlobalInterceptor clear() {
/* 44 */     clearRequest();
/* 45 */     clearResponse();
/* 46 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public synchronized GlobalInterceptor clearRequest() {
/* 55 */     this.requestInterceptors.clear();
/* 56 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public synchronized GlobalInterceptor clearResponse() {
/* 65 */     this.responseInterceptors.clear();
/* 66 */     return this;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   HttpInterceptor.Chain<HttpRequest> getCopiedRequestInterceptor() {
/* 75 */     HttpInterceptor.Chain<HttpRequest> copied = new HttpInterceptor.Chain<>();
/* 76 */     for (HttpInterceptor<HttpRequest> interceptor : this.requestInterceptors) {
/* 77 */       copied.addChain(interceptor);
/*    */     }
/* 79 */     return copied;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   HttpInterceptor.Chain<HttpResponse> getCopiedResponseInterceptor() {
/* 88 */     HttpInterceptor.Chain<HttpResponse> copied = new HttpInterceptor.Chain<>();
/* 89 */     for (HttpInterceptor<HttpResponse> interceptor : this.responseInterceptors) {
/* 90 */       copied.addChain(interceptor);
/*    */     }
/* 92 */     return copied;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\GlobalInterceptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */