/*    */ package com.mysql.cj;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
/*    */ import java.util.function.Consumer;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class NativeQueryAttributesBindings
/*    */   implements QueryAttributesBindings
/*    */ {
/* 37 */   private List<NativeQueryAttributesBindValue> bindAttributes = new ArrayList<>();
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setAttribute(String name, Object value) {
/* 44 */     this.bindAttributes.add(new NativeQueryAttributesBindValue(name, value));
/*    */   }
/*    */ 
/*    */   
/*    */   public int getCount() {
/* 49 */     return this.bindAttributes.size();
/*    */   }
/*    */ 
/*    */   
/*    */   public QueryAttributesBindValue getAttributeValue(int index) {
/* 54 */     return this.bindAttributes.get(index);
/*    */   }
/*    */ 
/*    */   
/*    */   public void runThroughAll(Consumer<QueryAttributesBindValue> bindAttribute) {
/* 59 */     this.bindAttributes.forEach(bindAttribute::accept);
/*    */   }
/*    */ 
/*    */   
/*    */   public void clearAttributes() {
/* 64 */     this.bindAttributes.clear();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\NativeQueryAttributesBindings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */