/*     */ package org.h2.expression;
/*     */ 
/*     */ import java.util.Objects;
/*     */ import org.h2.value.DataType;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueNull;
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
/*     */ public class TypedValueExpression
/*     */   extends ValueExpression
/*     */ {
/*  23 */   public static final TypedValueExpression UNKNOWN = new TypedValueExpression((Value)ValueNull.INSTANCE, TypeInfo.TYPE_BOOLEAN);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final TypeInfo type;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ValueExpression get(Value paramValue, TypeInfo paramTypeInfo) {
/*  36 */     return getImpl(paramValue, paramTypeInfo, true);
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
/*     */   public static ValueExpression getTypedIfNull(Value paramValue, TypeInfo paramTypeInfo) {
/*  50 */     return getImpl(paramValue, paramTypeInfo, false);
/*     */   }
/*     */   
/*     */   private static ValueExpression getImpl(Value paramValue, TypeInfo paramTypeInfo, boolean paramBoolean) {
/*  54 */     if (paramValue == ValueNull.INSTANCE) {
/*  55 */       switch (paramTypeInfo.getValueType()) {
/*     */         case 0:
/*  57 */           return ValueExpression.NULL;
/*     */         case 8:
/*  59 */           return UNKNOWN;
/*     */       } 
/*  61 */       return new TypedValueExpression(paramValue, paramTypeInfo);
/*     */     } 
/*  63 */     if (paramBoolean) {
/*  64 */       DataType dataType = DataType.getDataType(paramTypeInfo.getValueType());
/*  65 */       TypeInfo typeInfo = paramValue.getType();
/*  66 */       if ((dataType.supportsPrecision && paramTypeInfo.getPrecision() != typeInfo.getPrecision()) || (dataType.supportsScale && paramTypeInfo
/*  67 */         .getScale() != typeInfo.getScale()) || 
/*  68 */         !Objects.equals(paramTypeInfo.getExtTypeInfo(), typeInfo.getExtTypeInfo())) {
/*  69 */         return new TypedValueExpression(paramValue, paramTypeInfo);
/*     */       }
/*     */     } 
/*  72 */     return ValueExpression.get(paramValue);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private TypedValueExpression(Value paramValue, TypeInfo paramTypeInfo) {
/*  78 */     super(paramValue);
/*  79 */     this.type = paramTypeInfo;
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/*  84 */     return this.type;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  89 */     if (this == UNKNOWN) {
/*  90 */       paramStringBuilder.append("UNKNOWN");
/*     */     } else {
/*  92 */       this.value.getSQL(paramStringBuilder.append("CAST("), paramInt | 0x4).append(" AS ");
/*  93 */       this.type.getSQL(paramStringBuilder, paramInt).append(')');
/*     */     } 
/*  95 */     return paramStringBuilder;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isNullConstant() {
/* 100 */     return (this.value == ValueNull.INSTANCE);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\TypedValueExpression.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */