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
/*    */ public class BooleanPropertyDefinition
/*    */   extends AbstractPropertyDefinition<Boolean>
/*    */ {
/*    */   private static final long serialVersionUID = -7288366734350231540L;
/*    */   
/*    */   public enum AllowableValues
/*    */   {
/* 44 */     TRUE(true), FALSE(false), YES(true), NO(false);
/*    */     
/*    */     private boolean asBoolean;
/*    */     
/*    */     AllowableValues(boolean booleanValue) {
/* 49 */       this.asBoolean = booleanValue;
/*    */     }
/*    */     
/*    */     public boolean asBoolean() {
/* 53 */       return this.asBoolean;
/*    */     }
/*    */   }
/*    */ 
/*    */   
/*    */   public BooleanPropertyDefinition(PropertyKey key, Boolean defaultValue, boolean isRuntimeModifiable, String description, String sinceVersion, String category, int orderInCategory) {
/* 59 */     super(key, defaultValue, isRuntimeModifiable, description, sinceVersion, category, orderInCategory);
/*    */   }
/*    */ 
/*    */   
/*    */   public String[] getAllowableValues() {
/* 64 */     return getBooleanAllowableValues();
/*    */   }
/*    */ 
/*    */   
/*    */   public Boolean parseObject(String value, ExceptionInterceptor exceptionInterceptor) {
/* 69 */     return booleanFrom(getName(), value, exceptionInterceptor);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public RuntimeProperty<Boolean> createRuntimeProperty() {
/* 79 */     return new BooleanProperty(this);
/*    */   }
/*    */   
/*    */   public static Boolean booleanFrom(String name, String value, ExceptionInterceptor exceptionInterceptor) {
/*    */     try {
/* 84 */       return Boolean.valueOf(AllowableValues.valueOf(value.toUpperCase()).asBoolean());
/* 85 */     } catch (Exception e) {
/* 86 */       throw ExceptionFactory.createException(
/* 87 */           Messages.getString("PropertyDefinition.1", new Object[] {
/* 88 */               name, StringUtils.stringArrayToString(getBooleanAllowableValues(), "'", "', '", "' or '", "'"), value
/*    */             }), e, exceptionInterceptor);
/*    */     } 
/*    */   }
/*    */   
/*    */   public static String[] getBooleanAllowableValues() {
/* 94 */     return (String[])Arrays.<AllowableValues>stream(AllowableValues.values()).map(Enum::toString).toArray(x$0 -> new String[x$0]);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\conf\BooleanPropertyDefinition.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */