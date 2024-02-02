/*    */ package com.mysql.cj.conf;
/*    */ 
/*    */ import com.mysql.cj.Messages;
/*    */ import com.mysql.cj.exceptions.ExceptionFactory;
/*    */ import com.mysql.cj.exceptions.ExceptionInterceptor;
/*    */ import com.mysql.cj.util.StringUtils;
/*    */ import java.util.Arrays;
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
/*    */ public class EnumPropertyDefinition<T extends Enum<T>>
/*    */   extends AbstractPropertyDefinition<T>
/*    */ {
/*    */   private static final long serialVersionUID = -3297521968759540444L;
/*    */   private Class<T> enumType;
/*    */   
/*    */   public EnumPropertyDefinition(PropertyKey key, T defaultValue, boolean isRuntimeModifiable, String description, String sinceVersion, String category, int orderInCategory) {
/* 47 */     super(key, defaultValue, isRuntimeModifiable, description, sinceVersion, category, orderInCategory);
/* 48 */     if (defaultValue == null) {
/* 49 */       throw ExceptionFactory.createException("Enum property '" + key.getKeyName() + "' cannot be initialized with null.");
/*    */     }
/* 51 */     this.enumType = defaultValue.getDeclaringClass();
/*    */   }
/*    */ 
/*    */   
/*    */   public String[] getAllowableValues() {
/* 56 */     return (String[])Arrays.stream((Object[])this.enumType.getEnumConstants()).map(Enum::toString).sorted().toArray(x$0 -> new String[x$0]);
/*    */   }
/*    */ 
/*    */   
/*    */   public T parseObject(String value, ExceptionInterceptor exceptionInterceptor) {
/*    */     try {
/* 62 */       return Enum.valueOf(this.enumType, value.toUpperCase());
/* 63 */     } catch (Exception e) {
/* 64 */       throw ExceptionFactory.createException(
/* 65 */           Messages.getString("PropertyDefinition.1", new Object[] {
/* 66 */               getName(), StringUtils.stringArrayToString(getAllowableValues(), "'", "', '", "' or '", "'"), value
/*    */             }), e, exceptionInterceptor);
/*    */     } 
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public RuntimeProperty<T> createRuntimeProperty() {
/* 78 */     return new EnumProperty<>(this);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\conf\EnumPropertyDefinition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */