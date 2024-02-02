/*    */ package org.h2.expression;
/*    */ 
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.value.TypeInfo;
/*    */ import org.h2.value.Value;
/*    */ import org.h2.value.ValueJson;
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
/*    */ public final class Format
/*    */   extends Operation1
/*    */ {
/*    */   private final FormatEnum format;
/*    */   
/*    */   public enum FormatEnum
/*    */   {
/* 25 */     JSON;
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Format(Expression paramExpression, FormatEnum paramFormatEnum) {
/* 31 */     super(paramExpression);
/* 32 */     this.format = paramFormatEnum;
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getValue(SessionLocal paramSessionLocal) {
/* 37 */     return getValue(this.arg.getValue(paramSessionLocal));
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Value getValue(Value paramValue) {
/* 48 */     switch (paramValue.getValueType()) {
/*    */       case 0:
/* 50 */         return (Value)ValueJson.NULL;
/*    */       case 1:
/*    */       case 2:
/*    */       case 3:
/*    */       case 4:
/* 55 */         return (Value)ValueJson.fromJson(paramValue.getString());
/*    */     } 
/* 57 */     return paramValue.convertTo(TypeInfo.TYPE_JSON);
/*    */   }
/*    */ 
/*    */ 
/*    */   
/*    */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 63 */     this.arg = this.arg.optimize(paramSessionLocal);
/* 64 */     if (this.arg.isConstant()) {
/* 65 */       return ValueExpression.get(getValue(paramSessionLocal));
/*    */     }
/* 67 */     if (this.arg instanceof Format && this.format == ((Format)this.arg).format) {
/* 68 */       return this.arg;
/*    */     }
/* 70 */     this.type = TypeInfo.TYPE_JSON;
/* 71 */     return this;
/*    */   }
/*    */ 
/*    */   
/*    */   public boolean isIdentity() {
/* 76 */     return this.arg.isIdentity();
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 81 */     return this.arg.getSQL(paramStringBuilder, paramInt, 0).append(" FORMAT ").append(this.format.name());
/*    */   }
/*    */ 
/*    */   
/*    */   public int getNullable() {
/* 86 */     return this.arg.getNullable();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getTableName() {
/* 91 */     return this.arg.getTableName();
/*    */   }
/*    */ 
/*    */   
/*    */   public String getColumnName(SessionLocal paramSessionLocal, int paramInt) {
/* 96 */     return this.arg.getColumnName(paramSessionLocal, paramInt);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\Format.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */