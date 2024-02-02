/*    */ package org.h2.mode;
/*    */ 
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.expression.Operation1;
/*    */ import org.h2.expression.ValueExpression;
/*    */ import org.h2.index.Index;
/*    */ import org.h2.message.DbException;
/*    */ import org.h2.schema.Schema;
/*    */ import org.h2.table.Table;
/*    */ import org.h2.value.TypeInfo;
/*    */ import org.h2.value.Value;
/*    */ import org.h2.value.ValueInteger;
/*    */ import org.h2.value.ValueNull;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public final class Regclass
/*    */   extends Operation1
/*    */ {
/*    */   public Regclass(Expression paramExpression) {
/* 28 */     super(paramExpression);
/*    */   }
/*    */ 
/*    */   
/*    */   public Value getValue(SessionLocal paramSessionLocal) {
/* 33 */     Value value = this.arg.getValue(paramSessionLocal);
/* 34 */     if (value == ValueNull.INSTANCE) {
/* 35 */       return (Value)ValueNull.INSTANCE;
/*    */     }
/* 37 */     int i = value.getValueType();
/* 38 */     if (i >= 9 && i <= 11) {
/* 39 */       return (Value)value.convertToInt(null);
/*    */     }
/* 41 */     if (i == 12) {
/* 42 */       return (Value)ValueInteger.get((int)value.getLong());
/*    */     }
/* 44 */     String str = value.getString();
/* 45 */     for (Schema schema : paramSessionLocal.getDatabase().getAllSchemas()) {
/* 46 */       Table table = schema.findTableOrView(paramSessionLocal, str);
/* 47 */       if (table != null && !table.isHidden()) {
/* 48 */         return (Value)ValueInteger.get(table.getId());
/*    */       }
/* 50 */       Index index = schema.findIndex(paramSessionLocal, str);
/* 51 */       if (index != null && index.getCreateSQL() != null) {
/* 52 */         return (Value)ValueInteger.get(index.getId());
/*    */       }
/*    */     } 
/* 55 */     throw DbException.get(42102, str);
/*    */   }
/*    */ 
/*    */   
/*    */   public TypeInfo getType() {
/* 60 */     return TypeInfo.TYPE_INTEGER;
/*    */   }
/*    */ 
/*    */   
/*    */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 65 */     this.arg = this.arg.optimize(paramSessionLocal);
/* 66 */     if (this.arg.isConstant()) {
/* 67 */       return (Expression)ValueExpression.get(getValue(paramSessionLocal));
/*    */     }
/* 69 */     return (Expression)this;
/*    */   }
/*    */ 
/*    */   
/*    */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 74 */     return this.arg.getSQL(paramStringBuilder, paramInt, 0).append("::REGCLASS");
/*    */   }
/*    */ 
/*    */   
/*    */   public int getCost() {
/* 79 */     return this.arg.getCost() + 100;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mode\Regclass.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */