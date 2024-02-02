/*     */ package com.google.protobuf;
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
/*     */ 
/*     */ public final class RpcUtil
/*     */ {
/*     */   public static <Type extends Message> RpcCallback<Type> specializeCallback(RpcCallback<Message> originalCallback) {
/*  48 */     return (RpcCallback)originalCallback;
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
/*     */   public static <Type extends Message> RpcCallback<Message> generalizeCallback(final RpcCallback<Type> originalCallback, final Class<Type> originalClass, final Type defaultInstance) {
/*  70 */     return new RpcCallback<Message>()
/*     */       {
/*     */         public void run(Message parameter) {
/*     */           Message message;
/*     */           try {
/*  75 */             message = originalClass.cast(parameter);
/*  76 */           } catch (ClassCastException ignored) {
/*  77 */             message = (Message)RpcUtil.copyAsType((Type)defaultInstance, parameter);
/*     */           } 
/*  79 */           originalCallback.run(message);
/*     */         }
/*     */       };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static <Type extends Message> Type copyAsType(Type typeDefaultInstance, Message source) {
/*  91 */     return (Type)typeDefaultInstance.newBuilderForType().mergeFrom(source).build();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static <ParameterType> RpcCallback<ParameterType> newOneTimeCallback(final RpcCallback<ParameterType> originalCallback) {
/* 101 */     return new RpcCallback<ParameterType>()
/*     */       {
/*     */         private boolean alreadyCalled = false;
/*     */         
/*     */         public void run(ParameterType parameter) {
/* 106 */           synchronized (this) {
/* 107 */             if (this.alreadyCalled) {
/* 108 */               throw new RpcUtil.AlreadyCalledException();
/*     */             }
/* 110 */             this.alreadyCalled = true;
/*     */           } 
/*     */           
/* 113 */           originalCallback.run(parameter);
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   public static final class AlreadyCalledException
/*     */     extends RuntimeException {
/*     */     private static final long serialVersionUID = 5469741279507848266L;
/*     */     
/*     */     public AlreadyCalledException() {
/* 123 */       super("This RpcCallback was already called and cannot be called multiple times.");
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\RpcUtil.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */