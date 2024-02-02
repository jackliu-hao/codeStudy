/*    */ package io.undertow.attribute;
/*    */ 
/*    */ import io.undertow.server.HttpServerExchange;
/*    */ import java.util.ArrayDeque;
/*    */ import java.util.Deque;
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
/*    */ public class PathParameterAttribute
/*    */   implements ExchangeAttribute
/*    */ {
/*    */   private final String parameter;
/*    */   
/*    */   public PathParameterAttribute(String parameter) {
/* 37 */     this.parameter = parameter;
/*    */   }
/*    */ 
/*    */   
/*    */   public String readAttribute(HttpServerExchange exchange) {
/* 42 */     Deque<String> res = (Deque<String>)exchange.getPathParameters().get(this.parameter);
/* 43 */     if (res == null)
/* 44 */       return null; 
/* 45 */     if (res.isEmpty())
/* 46 */       return ""; 
/* 47 */     if (res.size() == 1) {
/* 48 */       return res.getFirst();
/*    */     }
/* 50 */     StringBuilder sb = new StringBuilder("[");
/* 51 */     int i = 0;
/* 52 */     for (String s : res) {
/* 53 */       sb.append(s);
/* 54 */       if (++i != res.size()) {
/* 55 */         sb.append(", ");
/*    */       }
/*    */     } 
/* 58 */     sb.append("]");
/* 59 */     return sb.toString();
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public void writeAttribute(HttpServerExchange exchange, String newValue) throws ReadOnlyAttributeException {
/* 65 */     ArrayDeque<String> value = new ArrayDeque<>();
/* 66 */     value.add(newValue);
/* 67 */     exchange.getPathParameters().put(this.parameter, value);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 72 */     return "%{p," + this.parameter + "}";
/*    */   }
/*    */   
/*    */   public static final class Builder
/*    */     implements ExchangeAttributeBuilder
/*    */   {
/*    */     public String name() {
/* 79 */       return "Path Parameter";
/*    */     }
/*    */ 
/*    */     
/*    */     public ExchangeAttribute build(String token) {
/* 84 */       if (token.startsWith("%{p,") && token.endsWith("}")) {
/* 85 */         String qp = token.substring(4, token.length() - 1);
/* 86 */         return new PathParameterAttribute(qp);
/*    */       } 
/* 88 */       return null;
/*    */     }
/*    */ 
/*    */     
/*    */     public int priority() {
/* 93 */       return 0;
/*    */     }
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\attribute\PathParameterAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */