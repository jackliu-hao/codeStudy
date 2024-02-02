/*     */ package org.h2.expression.aggregate;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import org.h2.api.Aggregate;
/*     */ import org.h2.command.query.Select;
/*     */ import org.h2.engine.DbObject;
/*     */ import org.h2.engine.Session;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.jdbc.JdbcConnection;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.schema.UserAggregate;
/*     */ import org.h2.util.ParserUtil;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueBoolean;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.ValueRow;
/*     */ import org.h2.value.ValueToObjectConverter;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class JavaAggregate
/*     */   extends AbstractAggregate
/*     */ {
/*     */   private final UserAggregate userAggregate;
/*     */   private int[] argTypes;
/*     */   private int dataType;
/*     */   private JdbcConnection userConnection;
/*     */   
/*     */   public JavaAggregate(UserAggregate paramUserAggregate, Expression[] paramArrayOfExpression, Select paramSelect, boolean paramBoolean) {
/*  37 */     super(paramSelect, paramArrayOfExpression, paramBoolean);
/*  38 */     this.userAggregate = paramUserAggregate;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCost() {
/*  43 */     int i = 5;
/*  44 */     for (Expression expression : this.args) {
/*  45 */       i += expression.getCost();
/*     */     }
/*  47 */     if (this.filterCondition != null) {
/*  48 */       i += this.filterCondition.getCost();
/*     */     }
/*  50 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  55 */     ParserUtil.quoteIdentifier(paramStringBuilder, this.userAggregate.getName(), paramInt).append('(');
/*  56 */     writeExpressions(paramStringBuilder, this.args, paramInt).append(')');
/*  57 */     return appendTailConditions(paramStringBuilder, paramInt, false);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/*  62 */     if (!super.isEverything(paramExpressionVisitor)) {
/*  63 */       return false;
/*     */     }
/*  65 */     switch (paramExpressionVisitor.getType()) {
/*     */ 
/*     */ 
/*     */       
/*     */       case 1:
/*     */       case 2:
/*  71 */         return false;
/*     */       case 7:
/*  73 */         paramExpressionVisitor.addDependency((DbObject)this.userAggregate);
/*     */         break;
/*     */     } 
/*     */     
/*  77 */     for (Expression expression : this.args) {
/*  78 */       if (expression != null && !expression.isEverything(paramExpressionVisitor)) {
/*  79 */         return false;
/*     */       }
/*     */     } 
/*  82 */     return (this.filterCondition == null || this.filterCondition.isEverything(paramExpressionVisitor));
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/*  87 */     super.optimize(paramSessionLocal);
/*  88 */     this.userConnection = paramSessionLocal.createConnection(false);
/*  89 */     int i = this.args.length;
/*  90 */     this.argTypes = new int[i];
/*  91 */     for (byte b = 0; b < i; b++) {
/*  92 */       int j = this.args[b].getType().getValueType();
/*  93 */       this.argTypes[b] = j;
/*     */     } 
/*     */     try {
/*  96 */       Aggregate aggregate = getInstance();
/*  97 */       this.dataType = aggregate.getInternalType(this.argTypes);
/*  98 */       this.type = TypeInfo.getTypeInfo(this.dataType);
/*  99 */     } catch (SQLException sQLException) {
/* 100 */       throw DbException.convert(sQLException);
/*     */     } 
/* 102 */     return (Expression)this;
/*     */   }
/*     */   
/*     */   private Aggregate getInstance() {
/* 106 */     Aggregate aggregate = this.userAggregate.getInstance();
/*     */     try {
/* 108 */       aggregate.init((Connection)this.userConnection);
/* 109 */     } catch (SQLException sQLException) {
/* 110 */       throw DbException.convert(sQLException);
/*     */     } 
/* 112 */     return aggregate;
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getAggregatedValue(SessionLocal paramSessionLocal, Object paramObject) {
/*     */     try {
/*     */       Aggregate aggregate;
/* 119 */       if (this.distinct) {
/* 120 */         aggregate = getInstance();
/* 121 */         AggregateDataCollecting aggregateDataCollecting = (AggregateDataCollecting)paramObject;
/* 122 */         if (aggregateDataCollecting != null) {
/* 123 */           for (Value value : aggregateDataCollecting.values) {
/* 124 */             if (this.args.length == 1) {
/* 125 */               aggregate.add(ValueToObjectConverter.valueToDefaultObject(value, this.userConnection, false)); continue;
/*     */             } 
/* 127 */             Value[] arrayOfValue = ((ValueRow)value).getList();
/* 128 */             Object[] arrayOfObject = new Object[this.args.length]; byte b; int i;
/* 129 */             for (b = 0, i = this.args.length; b < i; b++) {
/* 130 */               arrayOfObject[b] = ValueToObjectConverter.valueToDefaultObject(arrayOfValue[b], this.userConnection, false);
/*     */             }
/*     */             
/* 133 */             aggregate.add(arrayOfObject);
/*     */           } 
/*     */         }
/*     */       } else {
/*     */         
/* 138 */         aggregate = (Aggregate)paramObject;
/* 139 */         if (aggregate == null) {
/* 140 */           aggregate = getInstance();
/*     */         }
/*     */       } 
/* 143 */       Object object = aggregate.getResult();
/* 144 */       if (object == null) {
/* 145 */         return (Value)ValueNull.INSTANCE;
/*     */       }
/* 147 */       return ValueToObjectConverter.objectToValue((Session)paramSessionLocal, object, this.dataType);
/* 148 */     } catch (SQLException sQLException) {
/* 149 */       throw DbException.convert(sQLException);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void updateAggregate(SessionLocal paramSessionLocal, Object paramObject) {
/* 155 */     updateData(paramSessionLocal, paramObject, (Value[])null);
/*     */   }
/*     */   
/*     */   private void updateData(SessionLocal paramSessionLocal, Object paramObject, Value[] paramArrayOfValue) {
/*     */     try {
/* 160 */       if (this.distinct) {
/* 161 */         AggregateDataCollecting aggregateDataCollecting = (AggregateDataCollecting)paramObject;
/* 162 */         Value[] arrayOfValue = new Value[this.args.length];
/* 163 */         Value value = null; byte b; int i;
/* 164 */         for (b = 0, i = this.args.length; b < i; b++) {
/* 165 */           value = (paramArrayOfValue == null) ? this.args[b].getValue(paramSessionLocal) : paramArrayOfValue[b];
/* 166 */           arrayOfValue[b] = value;
/*     */         } 
/* 168 */         aggregateDataCollecting.add(paramSessionLocal, (this.args.length == 1) ? value : (Value)ValueRow.get(arrayOfValue));
/*     */       } else {
/* 170 */         Aggregate aggregate = (Aggregate)paramObject;
/* 171 */         Object[] arrayOfObject = new Object[this.args.length];
/* 172 */         Object object = null; byte b; int i;
/* 173 */         for (b = 0, i = this.args.length; b < i; b++) {
/* 174 */           Value value = (paramArrayOfValue == null) ? this.args[b].getValue(paramSessionLocal) : paramArrayOfValue[b];
/* 175 */           object = ValueToObjectConverter.valueToDefaultObject(value, this.userConnection, false);
/* 176 */           arrayOfObject[b] = object;
/*     */         } 
/* 178 */         aggregate.add((this.args.length == 1) ? object : arrayOfObject);
/*     */       } 
/* 180 */     } catch (SQLException sQLException) {
/* 181 */       throw DbException.convert(sQLException);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   protected void updateGroupAggregates(SessionLocal paramSessionLocal, int paramInt) {
/* 187 */     super.updateGroupAggregates(paramSessionLocal, paramInt);
/* 188 */     for (Expression expression : this.args) {
/* 189 */       expression.updateAggregate(paramSessionLocal, paramInt);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected int getNumExpressions() {
/* 195 */     int i = this.args.length;
/* 196 */     if (this.filterCondition != null) {
/* 197 */       i++;
/*     */     }
/* 199 */     return i;
/*     */   }
/*     */ 
/*     */   
/*     */   protected void rememberExpressions(SessionLocal paramSessionLocal, Value[] paramArrayOfValue) {
/* 204 */     int i = this.args.length;
/* 205 */     for (byte b = 0; b < i; b++) {
/* 206 */       paramArrayOfValue[b] = this.args[b].getValue(paramSessionLocal);
/*     */     }
/* 208 */     if (this.filterCondition != null) {
/* 209 */       paramArrayOfValue[i] = (Value)ValueBoolean.get(this.filterCondition.getBooleanValue(paramSessionLocal));
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected void updateFromExpressions(SessionLocal paramSessionLocal, Object paramObject, Value[] paramArrayOfValue) {
/* 215 */     if (this.filterCondition == null || paramArrayOfValue[getNumExpressions() - 1].isTrue()) {
/* 216 */       updateData(paramSessionLocal, paramObject, paramArrayOfValue);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   protected Object createAggregateData() {
/* 222 */     return this.distinct ? new AggregateDataCollecting(true, false, AggregateDataCollecting.NullCollectionMode.IGNORED) : getInstance();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\aggregate\JavaAggregate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */