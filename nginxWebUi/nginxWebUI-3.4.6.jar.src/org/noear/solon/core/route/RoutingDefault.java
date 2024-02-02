/*    */ package org.noear.solon.core.route;
/*    */ 
/*    */ import org.noear.solon.core.SignalType;
/*    */ import org.noear.solon.core.handle.MethodType;
/*    */ import org.noear.solon.core.util.PathAnalyzer;
/*    */ 
/*    */ public class RoutingDefault<T>
/*    */   implements Routing<T> {
/*    */   private final PathAnalyzer rule;
/*    */   private final int index;
/*    */   private final String path;
/*    */   private final T target;
/*    */   private final MethodType method;
/*    */   
/*    */   public RoutingDefault(String path, MethodType method, T target) {
/* 16 */     this(path, method, 0, target);
/*    */   }
/*    */   
/*    */   public RoutingDefault(String path, MethodType method, int index, T target) {
/* 20 */     this.rule = PathAnalyzer.get(path);
/*    */     
/* 22 */     this.method = method;
/* 23 */     this.path = path;
/* 24 */     this.index = index;
/* 25 */     this.target = target;
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
/*    */   public int index() {
/* 37 */     return this.index;
/*    */   }
/*    */ 
/*    */   
/*    */   public String path() {
/* 42 */     return this.path;
/*    */   }
/*    */ 
/*    */   
/*    */   public T target() {
/* 47 */     return this.target;
/*    */   }
/*    */ 
/*    */   
/*    */   public MethodType method() {
/* 52 */     return this.method;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean matches(MethodType method2, String path2) {
/* 60 */     if (MethodType.ALL == this.method)
/* 61 */       return matches0(path2); 
/* 62 */     if (MethodType.HTTP == this.method) {
/* 63 */       if (method2.signal == SignalType.HTTP) {
/* 64 */         return matches0(path2);
/*    */       }
/* 66 */     } else if (method2 == this.method) {
/* 67 */       return matches0(path2);
/*    */     } 
/*    */     
/* 70 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   private boolean matches0(String path2) {
/* 75 */     if ("**".equals(this.path) || "/**".equals(this.path)) {
/* 76 */       return true;
/*    */     }
/*    */ 
/*    */     
/* 80 */     if (this.path.equals(path2)) {
/* 81 */       return true;
/*    */     }
/*    */ 
/*    */     
/* 85 */     return this.rule.matches(path2);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\core\route\RoutingDefault.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */