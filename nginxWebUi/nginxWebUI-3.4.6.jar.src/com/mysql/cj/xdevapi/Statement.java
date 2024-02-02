/*     */ package com.mysql.cj.xdevapi;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.CompletableFuture;
/*     */ import java.util.stream.IntStream;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public interface Statement<STMT_T, RES_T>
/*     */ {
/*     */   public enum LockContention
/*     */   {
/*  54 */     DEFAULT,
/*     */ 
/*     */ 
/*     */     
/*  58 */     NOWAIT,
/*     */ 
/*     */ 
/*     */     
/*  62 */     SKIP_LOCKED;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default STMT_T clearBindings() {
/*  85 */     throw new UnsupportedOperationException("This statement doesn't support bound parameters");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default STMT_T bind(String argName, Object value) {
/*  98 */     throw new UnsupportedOperationException("This statement doesn't support bound parameters");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default STMT_T bind(Map<String, Object> values) {
/* 110 */     clearBindings();
/* 111 */     values.entrySet().forEach(e -> bind((String)e.getKey(), e.getValue()));
/* 112 */     return (STMT_T)this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   default STMT_T bind(List<Object> values) {
/* 124 */     clearBindings();
/* 125 */     IntStream.range(0, values.size()).forEach(i -> bind(String.valueOf(i), values.get(i)));
/* 126 */     return (STMT_T)this;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   STMT_T bind(Object... values) {
/* 137 */     return bind(Arrays.asList(values));
/*     */   }
/*     */   
/*     */   RES_T execute();
/*     */   
/*     */   CompletableFuture<RES_T> executeAsync();
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\Statement.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */