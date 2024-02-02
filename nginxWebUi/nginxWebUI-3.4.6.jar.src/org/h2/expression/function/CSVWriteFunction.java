/*     */ package org.h2.expression.function;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.SQLException;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionVisitor;
/*     */ import org.h2.jdbc.JdbcConnection;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.tools.Csv;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueInteger;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class CSVWriteFunction
/*     */   extends FunctionN
/*     */ {
/*     */   public CSVWriteFunction() {
/*  27 */     super(new Expression[4]);
/*     */   }
/*     */ 
/*     */   
/*     */   public Value getValue(SessionLocal paramSessionLocal) {
/*  32 */     paramSessionLocal.getUser().checkAdmin();
/*  33 */     JdbcConnection jdbcConnection = paramSessionLocal.createConnection(false);
/*  34 */     Csv csv = new Csv();
/*  35 */     String str1 = getValue(paramSessionLocal, 2);
/*  36 */     String str2 = null;
/*  37 */     if (str1 != null && str1.indexOf('=') >= 0) {
/*  38 */       str2 = csv.setOptions(str1);
/*     */     } else {
/*  40 */       str2 = str1;
/*  41 */       String str3 = getValue(paramSessionLocal, 3);
/*  42 */       String str4 = getValue(paramSessionLocal, 4);
/*  43 */       String str5 = getValue(paramSessionLocal, 5);
/*  44 */       String str6 = getValue(paramSessionLocal, 6);
/*  45 */       String str7 = getValue(paramSessionLocal, 7);
/*  46 */       setCsvDelimiterEscape(csv, str3, str4, str5);
/*  47 */       csv.setNullString(str6);
/*  48 */       if (str7 != null) {
/*  49 */         csv.setLineSeparator(str7);
/*     */       }
/*     */     } 
/*     */     try {
/*  53 */       return (Value)ValueInteger.get(csv.write((Connection)jdbcConnection, this.args[0].getValue(paramSessionLocal).getString(), this.args[1]
/*  54 */             .getValue(paramSessionLocal).getString(), str2));
/*  55 */     } catch (SQLException sQLException) {
/*  56 */       throw DbException.convert(sQLException);
/*     */     } 
/*     */   }
/*     */   
/*     */   private String getValue(SessionLocal paramSessionLocal, int paramInt) {
/*  61 */     return (paramInt < this.args.length) ? this.args[paramInt].getValue(paramSessionLocal).getString() : null;
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
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setCsvDelimiterEscape(Csv paramCsv, String paramString1, String paramString2, String paramString3) {
/*  78 */     if (paramString1 != null) {
/*  79 */       paramCsv.setFieldSeparatorWrite(paramString1);
/*  80 */       if (!paramString1.isEmpty()) {
/*  81 */         char c = paramString1.charAt(0);
/*  82 */         paramCsv.setFieldSeparatorRead(c);
/*     */       } 
/*     */     } 
/*  85 */     if (paramString2 != null) {
/*  86 */       boolean bool = paramString2.isEmpty() ? false : paramString2.charAt(0);
/*  87 */       paramCsv.setFieldDelimiter(bool);
/*     */     } 
/*  89 */     if (paramString3 != null) {
/*  90 */       boolean bool = paramString3.isEmpty() ? false : paramString3.charAt(0);
/*  91 */       paramCsv.setEscapeCharacter(bool);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Expression optimize(SessionLocal paramSessionLocal) {
/*  97 */     optimizeArguments(paramSessionLocal, false);
/*  98 */     int i = this.args.length;
/*  99 */     if (i < 2 || i > 8) {
/* 100 */       throw DbException.get(7001, new String[] { getName(), "2..8" });
/*     */     }
/* 102 */     this.type = TypeInfo.TYPE_INTEGER;
/* 103 */     return (Expression)this;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 108 */     return "CSVWRITE";
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isEverything(ExpressionVisitor paramExpressionVisitor) {
/* 113 */     if (!super.isEverything(paramExpressionVisitor)) {
/* 114 */       return false;
/*     */     }
/* 116 */     switch (paramExpressionVisitor.getType()) {
/*     */       case 2:
/*     */       case 5:
/*     */       case 8:
/* 120 */         return false;
/*     */     } 
/* 122 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\CSVWriteFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */