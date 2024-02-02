/*     */ package org.h2.expression.function;
/*     */ 
/*     */ import org.h2.constraint.Constraint;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.Role;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.engine.Setting;
/*     */ import org.h2.engine.User;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.index.Index;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.schema.Constant;
/*     */ import org.h2.schema.Domain;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.schema.Sequence;
/*     */ import org.h2.schema.TriggerObject;
/*     */ import org.h2.schema.UserDefinedFunction;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.table.TableSynonym;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueInteger;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.ValueVarchar;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class DBObjectFunction
/*     */   extends FunctionN
/*     */ {
/*     */   public static final int DB_OBJECT_ID = 0;
/*     */   public static final int DB_OBJECT_SQL = 1;
/*  36 */   private static final String[] NAMES = new String[] { "DB_OBJECT_ID", "DB_OBJECT_SQL" };
/*     */ 
/*     */   
/*     */   private final int function;
/*     */ 
/*     */   
/*     */   public DBObjectFunction(Expression paramExpression1, Expression paramExpression2, Expression paramExpression3, int paramInt) {
/*  43 */     super((paramExpression3 == null) ? new Expression[2] : new Expression[3]);
/*  44 */     this.function = paramInt;
/*     */   }
/*     */   public Value getValue(SessionLocal paramSessionLocal, Value paramValue1, Value paramValue2, Value paramValue3) {
/*     */     User user;
/*     */     String str2;
/*  49 */     paramSessionLocal.getUser().checkAdmin();
/*  50 */     String str1 = paramValue1.getString();
/*     */     
/*  52 */     if (paramValue3 != null) {
/*  53 */       Constant constant; Constraint constraint; Domain domain; Index index; UserDefinedFunction userDefinedFunction; Sequence sequence; TableSynonym tableSynonym; Table table; TriggerObject triggerObject; Schema schema = paramSessionLocal.getDatabase().findSchema(paramValue2.getString());
/*  54 */       if (schema == null) {
/*  55 */         return (Value)ValueNull.INSTANCE;
/*     */       }
/*  57 */       String str = paramValue3.getString();
/*  58 */       switch (str1) {
/*     */         case "CONSTANT":
/*  60 */           constant = schema.findConstant(str);
/*     */           break;
/*     */         case "CONSTRAINT":
/*  63 */           constraint = schema.findConstraint(paramSessionLocal, str);
/*     */           break;
/*     */         case "DOMAIN":
/*  66 */           domain = schema.findDomain(str);
/*     */           break;
/*     */         case "INDEX":
/*  69 */           index = schema.findIndex(paramSessionLocal, str);
/*     */           break;
/*     */         case "ROUTINE":
/*  72 */           userDefinedFunction = schema.findFunctionOrAggregate(str);
/*     */           break;
/*     */         case "SEQUENCE":
/*  75 */           sequence = schema.findSequence(str);
/*     */           break;
/*     */         case "SYNONYM":
/*  78 */           tableSynonym = schema.getSynonym(str);
/*     */           break;
/*     */         case "TABLE":
/*  81 */           table = schema.findTableOrView(paramSessionLocal, str);
/*     */           break;
/*     */         case "TRIGGER":
/*  84 */           triggerObject = schema.findTrigger(str);
/*     */           break;
/*     */         default:
/*  87 */           return (Value)ValueNull.INSTANCE;
/*     */       } 
/*     */     } else {
/*  90 */       Role role; Setting setting; Schema schema; String str = paramValue2.getString();
/*  91 */       Database database = paramSessionLocal.getDatabase();
/*  92 */       switch (str1) {
/*     */         case "ROLE":
/*  94 */           role = database.findRole(str);
/*     */           break;
/*     */         case "SETTING":
/*  97 */           setting = database.findSetting(str);
/*     */           break;
/*     */         case "SCHEMA":
/* 100 */           schema = database.findSchema(str);
/*     */           break;
/*     */         case "USER":
/* 103 */           user = database.findUser(str);
/*     */           break;
/*     */         default:
/* 106 */           return (Value)ValueNull.INSTANCE;
/*     */       } 
/*     */     } 
/* 109 */     if (user == null) {
/* 110 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/* 112 */     switch (this.function) {
/*     */       case 0:
/* 114 */         return (Value)ValueInteger.get(user.getId());
/*     */       case 1:
/* 116 */         str2 = user.getCreateSQLForMeta();
/* 117 */         return (str2 != null) ? ValueVarchar.get(str2, (CastDataProvider)paramSessionLocal) : (Value)ValueNull.INSTANCE;
/*     */     } 
/* 119 */     throw DbException.getInternalError("function=" + this.function);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/* 125 */     optimizeArguments(paramSessionLocal, false);
/* 126 */     this.type = (this.function == 0) ? TypeInfo.TYPE_INTEGER : TypeInfo.TYPE_VARCHAR;
/* 127 */     return (Expression)this;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 132 */     switch (paramExpressionVisitor.getType()) {
/*     */       case 2:
/* 134 */         return false;
/*     */     } 
/* 136 */     return super.isEverything(paramExpressionVisitor);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 141 */     return NAMES[this.function];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\DBObjectFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */