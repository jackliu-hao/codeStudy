/*    */ package org.noear.solon.core.util;
/*    */ 
/*    */ import java.lang.reflect.ParameterizedType;
/*    */ import java.lang.reflect.Type;
/*    */ import java.util.Arrays;
/*    */ import java.util.Objects;
/*    */ import java.util.StringJoiner;
/*    */ 
/*    */ public class ParameterizedTypeImpl
/*    */   implements ParameterizedType
/*    */ {
/*    */   private final Type[] actualTypeArguments;
/*    */   private final Class<?> rawType;
/*    */   private final Type ownerType;
/*    */   
/*    */   public ParameterizedTypeImpl(Class<?> rawType, Type[] actualTypeArguments, Type ownerType) {
/* 17 */     this.actualTypeArguments = actualTypeArguments;
/* 18 */     this.rawType = rawType;
/* 19 */     this.ownerType = (ownerType != null) ? ownerType : rawType.getDeclaringClass();
/*    */   }
/*    */ 
/*    */   
/*    */   public Type[] getActualTypeArguments() {
/* 24 */     return this.actualTypeArguments;
/*    */   }
/*    */ 
/*    */   
/*    */   public Type getRawType() {
/* 29 */     return this.rawType;
/*    */   }
/*    */ 
/*    */   
/*    */   public Type getOwnerType() {
/* 34 */     return this.ownerType;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean equals(Object o) {
/* 39 */     if (o instanceof ParameterizedType) {
/* 40 */       ParameterizedType that = (ParameterizedType)o;
/*    */       
/* 42 */       if (this == that) {
/* 43 */         return true;
/*    */       }
/* 45 */       Type thatOwner = that.getOwnerType();
/* 46 */       Type thatRawType = that.getRawType();
/*    */       
/* 48 */       return (Objects.equals(this.ownerType, thatOwner) && 
/* 49 */         Objects.equals(this.rawType, thatRawType) && 
/* 50 */         Arrays.equals((Object[])this.actualTypeArguments, (Object[])that
/* 51 */           .getActualTypeArguments()));
/*    */     } 
/* 53 */     return false;
/*    */   }
/*    */ 
/*    */   
/*    */   public int hashCode() {
/* 58 */     return Arrays.hashCode((Object[])this.actualTypeArguments) ^ 
/* 59 */       Objects.hashCode(this.ownerType) ^ 
/* 60 */       Objects.hashCode(this.rawType);
/*    */   }
/*    */   
/*    */   public String toString() {
/* 64 */     StringBuilder sb = new StringBuilder();
/*    */     
/* 66 */     if (this.ownerType != null)
/* 67 */     { sb.append(this.ownerType.getTypeName());
/*    */       
/* 69 */       sb.append("$");
/*    */       
/* 71 */       if (this.ownerType instanceof ParameterizedTypeImpl) {
/*    */ 
/*    */         
/* 74 */         sb.append(this.rawType.getName().replace(((ParameterizedTypeImpl)this.ownerType).rawType.getName() + "$", ""));
/*    */       } else {
/*    */         
/* 77 */         sb.append(this.rawType.getSimpleName());
/*    */       }  }
/* 79 */     else { sb.append(this.rawType.getName()); }
/*    */     
/* 81 */     if (this.actualTypeArguments != null) {
/* 82 */       StringJoiner sj = new StringJoiner(", ", "<", ">");
/* 83 */       sj.setEmptyValue("");
/* 84 */       for (Type t : this.actualTypeArguments) {
/* 85 */         sj.add(t.getTypeName());
/*    */       }
/* 87 */       sb.append(sj.toString());
/*    */     } 
/*    */     
/* 90 */     return sb.toString();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\noear\solon\cor\\util\ParameterizedTypeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */