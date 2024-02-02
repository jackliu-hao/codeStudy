/*    */ package org.h2.expression;
/*    */ 
/*    */ import java.util.Map;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.util.ParserUtil;
/*    */ import org.h2.value.ExtTypeInfoRow;
/*    */ import org.h2.value.TypeInfo;
/*    */ import org.h2.value.Value;
/*    */ import org.h2.value.ValueNull;
/*    */ import org.h2.value.ValueRow;
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
/*    */ public final class FieldReference
/*    */   extends Operation1
/*    */ {
/*    */   private final String fieldName;
/*    */   private int ordinal;
/*    */   
/*    */   public FieldReference(Expression paramExpression, String paramString) {
/* 30 */     super(paramExpression);
/* 31 */     this.fieldName = paramString;
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 36 */     return ParserUtil.quoteIdentifier(this.arg.getEnclosedSQL(paramStringBuilder, paramInt).append('.'), this.fieldName, paramInt);
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getValue(SessionLocal paramSessionLocal) {
/* 41 */     Value value = this.arg.getValue(paramSessionLocal);
/* 42 */     if (value != ValueNull.INSTANCE) {
/* 43 */       return ((ValueRow)value).getList()[this.ordinal];
/*    */     }
/* 45 */     return (Value)ValueNull.INSTANCE;
/*    */   }
/*    */ 
/*    */   
/*    */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 50 */     this.arg = this.arg.optimize(paramSessionLocal);
/* 51 */     TypeInfo typeInfo = this.arg.getType();
/* 52 */     if (typeInfo.getValueType() != 41) {
/* 53 */       throw DbException.getInvalidExpressionTypeException("ROW", this.arg);
/*    */     }
/* 55 */     byte b = 0;
/* 56 */     for (Map.Entry entry : ((ExtTypeInfoRow)typeInfo.getExtTypeInfo()).getFields()) {
/* 57 */       if (this.fieldName.equals(entry.getKey())) {
/* 58 */         typeInfo = (TypeInfo)entry.getValue();
/* 59 */         this.type = typeInfo;
/* 60 */         this.ordinal = b;
/* 61 */         if (this.arg.isConstant()) {
/* 62 */           return TypedValueExpression.get(getValue(paramSessionLocal), typeInfo);
/*    */         }
/* 64 */         return this;
/*    */       } 
/* 66 */       b++;
/*    */     } 
/* 68 */     throw DbException.get(42122, this.fieldName);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\FieldReference.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */