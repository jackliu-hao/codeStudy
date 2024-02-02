/*    */ package org.apache.http.config;
/*    */ 
/*    */ import java.util.HashMap;
/*    */ import java.util.Locale;
/*    */ import java.util.Map;
/*    */ import org.apache.http.util.Args;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class RegistryBuilder<I>
/*    */ {
/*    */   private final Map<String, I> items;
/*    */   
/*    */   public static <I> RegistryBuilder<I> create() {
/* 46 */     return new RegistryBuilder<I>();
/*    */   }
/*    */ 
/*    */   
/*    */   RegistryBuilder() {
/* 51 */     this.items = new HashMap<String, I>();
/*    */   }
/*    */   
/*    */   public RegistryBuilder<I> register(String id, I item) {
/* 55 */     Args.notEmpty(id, "ID");
/* 56 */     Args.notNull(item, "Item");
/* 57 */     this.items.put(id.toLowerCase(Locale.ROOT), item);
/* 58 */     return this;
/*    */   }
/*    */   
/*    */   public Registry<I> build() {
/* 62 */     return new Registry<I>(this.items);
/*    */   }
/*    */ 
/*    */   
/*    */   public String toString() {
/* 67 */     return this.items.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\http\config\RegistryBuilder.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */