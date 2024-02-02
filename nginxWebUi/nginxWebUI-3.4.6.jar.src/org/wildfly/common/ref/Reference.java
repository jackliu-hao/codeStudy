/*     */ package org.wildfly.common.ref;
/*     */ 
/*     */ import java.util.EnumSet;
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
/*     */ public interface Reference<T, A>
/*     */ {
/*     */   T get();
/*     */   
/*     */   A getAttachment();
/*     */   
/*     */   void clear();
/*     */   
/*     */   Type getType();
/*     */   
/*     */   public enum Type
/*     */   {
/*  71 */     STRONG,
/*     */ 
/*     */ 
/*     */     
/*  75 */     WEAK,
/*     */ 
/*     */ 
/*     */     
/*  79 */     PHANTOM,
/*     */ 
/*     */ 
/*     */     
/*  83 */     SOFT,
/*     */ 
/*     */ 
/*     */     
/*  87 */     NULL;
/*     */ 
/*     */     
/*  90 */     private static final int fullSize = (values()).length;
/*     */ 
/*     */ 
/*     */     
/*     */     static {
/*     */     
/*     */     }
/*     */ 
/*     */     
/*     */     public static boolean isFull(EnumSet<Type> set) {
/* 100 */       return (set != null && set.size() == fullSize);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean in(Type v1) {
/* 111 */       return (this == v1);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean in(Type v1, Type v2) {
/* 123 */       return (this == v1 || this == v2);
/*     */     }
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
/*     */     public boolean in(Type v1, Type v2, Type v3) {
/* 136 */       return (this == v1 || this == v2 || this == v3);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean in(Type... values) {
/* 147 */       if (values != null) for (Type value : values) {
/* 148 */           if (this == value) return true; 
/*     */         }  
/* 150 */       return false;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\wildfly\common\ref\Reference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */