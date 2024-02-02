/*    */ package io.undertow.servlet.predicate;
/*    */ 
/*    */ import io.undertow.predicate.Predicate;
/*    */ import io.undertow.predicate.PredicateBuilder;
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import io.undertow.servlet.handlers.ServletRequestContext;
/*    */ import java.util.HashMap;
/*    */ import java.util.HashSet;
/*    */ import java.util.Map;
/*    */ import java.util.Set;
/*    */ import javax.servlet.DispatcherType;
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
/*    */ 
/*    */ 
/*    */ public class DispatcherTypePredicate
/*    */   implements Predicate
/*    */ {
/* 40 */   public static final DispatcherTypePredicate FORWARD = new DispatcherTypePredicate(DispatcherType.FORWARD);
/* 41 */   public static final DispatcherTypePredicate INCLUDE = new DispatcherTypePredicate(DispatcherType.INCLUDE);
/* 42 */   public static final DispatcherTypePredicate REQUEST = new DispatcherTypePredicate(DispatcherType.REQUEST);
/* 43 */   public static final DispatcherTypePredicate ASYNC = new DispatcherTypePredicate(DispatcherType.ASYNC);
/* 44 */   public static final DispatcherTypePredicate ERROR = new DispatcherTypePredicate(DispatcherType.ERROR);
/*    */   
/*    */   private final DispatcherType dispatcherType;
/*    */ 
/*    */   
/*    */   public DispatcherTypePredicate(DispatcherType dispatcherType) {
/* 50 */     this.dispatcherType = dispatcherType;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean resolve(HttpServerExchange value) {
/* 55 */     return (((ServletRequestContext)value.getAttachment(ServletRequestContext.ATTACHMENT_KEY)).getDispatcherType() == this.dispatcherType);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 60 */     return "dispatcher( " + this.dispatcherType.toString() + " )";
/*    */   }
/*    */   
/*    */   public static class Builder
/*    */     implements PredicateBuilder
/*    */   {
/*    */     public String name() {
/* 67 */       return "dispatcher";
/*    */     }
/*    */ 
/*    */     
/*    */     public Map<String, Class<?>> parameters() {
/* 72 */       Map<String, Class<?>> params = new HashMap<>();
/* 73 */       params.put("value", String.class);
/* 74 */       return params;
/*    */     }
/*    */ 
/*    */     
/*    */     public Set<String> requiredParameters() {
/* 79 */       Set<String> params = new HashSet<>();
/* 80 */       params.add("value");
/* 81 */       return params;
/*    */     }
/*    */ 
/*    */     
/*    */     public String defaultParameter() {
/* 86 */       return "value";
/*    */     }
/*    */ 
/*    */     
/*    */     public Predicate build(Map<String, Object> config) {
/* 91 */       String value = (String)config.get("value");
/* 92 */       return new DispatcherTypePredicate(DispatcherType.valueOf(value));
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\predicate\DispatcherTypePredicate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */