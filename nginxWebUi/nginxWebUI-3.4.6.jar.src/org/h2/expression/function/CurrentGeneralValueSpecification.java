/*     */ package org.h2.expression.function;
/*     */ 
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.expression.Operation0;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.ParserUtil;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.ValueVarchar;
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
/*     */ public final class CurrentGeneralValueSpecification
/*     */   extends Operation0
/*     */   implements NamedExpression
/*     */ {
/*     */   public static final int CURRENT_CATALOG = 0;
/*     */   public static final int CURRENT_PATH = 1;
/*     */   public static final int CURRENT_ROLE = 2;
/*     */   public static final int CURRENT_SCHEMA = 3;
/*     */   public static final int CURRENT_USER = 4;
/*     */   public static final int SESSION_USER = 5;
/*     */   public static final int SYSTEM_USER = 6;
/*  61 */   private static final String[] NAMES = new String[] { "CURRENT_CATALOG", "CURRENT_PATH", "CURRENT_ROLE", "CURRENT_SCHEMA", "CURRENT_USER", "SESSION_USER", "SYSTEM_USER" };
/*     */   
/*     */   private final int specification;
/*     */ 
/*     */   
/*     */   public CurrentGeneralValueSpecification(int paramInt) {
/*  67 */     this.specification = paramInt;
/*     */   }
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/*     */     String str;
/*     */     String[] arrayOfString;
/*     */     Database database;
/*  73 */     switch (this.specification) {
/*     */       case 0:
/*  75 */         str = paramSessionLocal.getDatabase().getShortName();
/*     */         break;
/*     */       case 1:
/*  78 */         arrayOfString = paramSessionLocal.getSchemaSearchPath();
/*  79 */         if (arrayOfString != null) {
/*  80 */           StringBuilder stringBuilder = new StringBuilder();
/*  81 */           for (byte b = 0; b < arrayOfString.length; b++) {
/*  82 */             if (b > 0) {
/*  83 */               stringBuilder.append(',');
/*     */             }
/*  85 */             ParserUtil.quoteIdentifier(stringBuilder, arrayOfString[b], 0);
/*     */           } 
/*  87 */           str = stringBuilder.toString(); break;
/*     */         } 
/*  89 */         str = "";
/*     */         break;
/*     */ 
/*     */       
/*     */       case 2:
/*  94 */         database = paramSessionLocal.getDatabase();
/*  95 */         str = database.getPublicRole().getName();
/*  96 */         if ((database.getSettings()).databaseToLower) {
/*  97 */           str = StringUtils.toLowerEnglish(str);
/*     */         }
/*     */         break;
/*     */       
/*     */       case 3:
/* 102 */         str = paramSessionLocal.getCurrentSchemaName();
/*     */         break;
/*     */       case 4:
/*     */       case 5:
/*     */       case 6:
/* 107 */         str = paramSessionLocal.getUser().getName();
/* 108 */         if ((paramSessionLocal.getDatabase().getSettings()).databaseToLower) {
/* 109 */           str = StringUtils.toLowerEnglish(str);
/*     */         }
/*     */         break;
/*     */       default:
/* 113 */         throw DbException.getInternalError("specification=" + this.specification);
/*     */     } 
/* 115 */     return (str != null) ? ValueVarchar.get(str, (CastDataProvider)paramSessionLocal) : (Value)ValueNull.INSTANCE;
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getUnenclosedSQL(StringBuilder paramStringBuilder, int paramInt) {
/* 120 */     return paramStringBuilder.append(getName());
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 125 */     switch (paramExpressionVisitor.getType()) {
/*     */       case 2:
/* 127 */         return false;
/*     */     } 
/* 129 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getType() {
/* 134 */     return TypeInfo.TYPE_VARCHAR;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCost() {
/* 139 */     return 1;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 144 */     return NAMES[this.specification];
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\CurrentGeneralValueSpecification.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */