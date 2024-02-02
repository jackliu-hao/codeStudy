/*    */ package org.noear.solon.core.aspect;
/*    */ 
/*    */ import java.util.Collections;
/*    */ import java.util.LinkedHashMap;
/*    */ import java.util.List;
/*    */ import java.util.Map;
/*    */ import org.noear.solon.core.wrap.MethodHolder;
/*    */ import org.noear.solon.core.wrap.ParamWrap;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class Invocation
/*    */ {
/*    */   private final Object target;
/*    */   private final Object[] args;
/*    */   private Map<String, Object> argsMap;
/*    */   private final MethodHolder method;
/*    */   private final List<InterceptorEntity> interceptors;
/* 23 */   private int interceptorIndex = 0;
/*    */   
/*    */   public Invocation(Object target, Object[] args, MethodHolder method, List<InterceptorEntity> interceptors) {
/* 26 */     this.target = target;
/* 27 */     this.args = args;
/* 28 */     this.method = method;
/* 29 */     this.interceptors = interceptors;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object target() {
/* 36 */     return this.target;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object[] args() {
/* 43 */     return this.args;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Map<String, Object> argsAsMap() {
/* 50 */     if (this.argsMap == null) {
/* 51 */       Map<String, Object> tmp = new LinkedHashMap<>();
/*    */       
/* 53 */       ParamWrap[] params = this.method.getParamWraps();
/*    */       
/* 55 */       for (int i = 0, len = params.length; i < len; i++) {
/* 56 */         tmp.put(params[i].getName(), this.args[i]);
/*    */       }
/*    */ 
/*    */       
/* 60 */       this.argsMap = Collections.unmodifiableMap(tmp);
/*    */     } 
/*    */ 
/*    */     
/* 64 */     return this.argsMap;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public MethodHolder method() {
/* 71 */     return this.method;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object invoke() throws Throwable {
/* 78 */     return ((InterceptorEntity)this.interceptors.get(this.interceptorIndex++)).doIntercept(this);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\aspect\Invocation.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */