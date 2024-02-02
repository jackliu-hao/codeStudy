/*     */ package org.h2.expression.function.table;
/*     */ 
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.function.CSVWriteFunction;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.schema.FunctionAlias;
/*     */ import org.h2.tools.Csv;
/*     */ import org.h2.util.StringUtils;
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
/*     */ public final class CSVReadFunction
/*     */   extends TableFunction
/*     */ {
/*     */   public CSVReadFunction() {
/*  27 */     super(new Expression[4]);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface getValue(SessionLocal paramSessionLocal) {
/*  32 */     paramSessionLocal.getUser().checkAdmin();
/*  33 */     String str1 = getValue(paramSessionLocal, 0);
/*  34 */     String str2 = getValue(paramSessionLocal, 1);
/*  35 */     Csv csv = new Csv();
/*  36 */     String str3 = getValue(paramSessionLocal, 2);
/*  37 */     String str4 = null;
/*  38 */     if (str3 != null && str3.indexOf('=') >= 0) {
/*  39 */       str4 = csv.setOptions(str3);
/*     */     } else {
/*  41 */       str4 = str3;
/*  42 */       String str5 = getValue(paramSessionLocal, 3);
/*  43 */       String str6 = getValue(paramSessionLocal, 4);
/*  44 */       String str7 = getValue(paramSessionLocal, 5);
/*  45 */       String str8 = getValue(paramSessionLocal, 6);
/*  46 */       CSVWriteFunction.setCsvDelimiterEscape(csv, str5, str6, str7);
/*  47 */       csv.setNullString(str8);
/*     */     } 
/*  49 */     char c = csv.getFieldSeparatorRead();
/*  50 */     String[] arrayOfString = StringUtils.arraySplit(str2, c, true);
/*     */     
/*     */     try {
/*  53 */       return FunctionAlias.JavaMethod.resultSetToResult(paramSessionLocal, csv.read(str1, arrayOfString, str4), 2147483647);
/*  54 */     } catch (SQLException sQLException) {
/*  55 */       throw DbException.convert(sQLException);
/*     */     } 
/*     */   }
/*     */   
/*     */   private String getValue(SessionLocal paramSessionLocal, int paramInt) {
/*  60 */     return getValue(paramSessionLocal, this.args, paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public void optimize(SessionLocal paramSessionLocal) {
/*  65 */     super.optimize(paramSessionLocal);
/*  66 */     int i = this.args.length;
/*  67 */     if (i < 1 || i > 7) {
/*  68 */       throw DbException.get(7001, new String[] { getName(), "1..7" });
/*     */     }
/*     */   }
/*     */   
/*     */   public ResultInterface getValueTemplate(SessionLocal paramSessionLocal) {
/*     */     ResultInterface resultInterface;
/*  74 */     paramSessionLocal.getUser().checkAdmin();
/*  75 */     String str1 = getValue(paramSessionLocal, this.args, 0);
/*  76 */     if (str1 == null) {
/*  77 */       throw DbException.get(90012, "fileName");
/*     */     }
/*  79 */     String str2 = getValue(paramSessionLocal, this.args, 1);
/*  80 */     Csv csv = new Csv();
/*  81 */     String str3 = getValue(paramSessionLocal, this.args, 2);
/*  82 */     String str4 = null;
/*  83 */     if (str3 != null && str3.indexOf('=') >= 0) {
/*  84 */       str4 = csv.setOptions(str3);
/*     */     } else {
/*  86 */       str4 = str3;
/*  87 */       String str5 = getValue(paramSessionLocal, this.args, 3);
/*  88 */       String str6 = getValue(paramSessionLocal, this.args, 4);
/*  89 */       String str7 = getValue(paramSessionLocal, this.args, 5);
/*  90 */       CSVWriteFunction.setCsvDelimiterEscape(csv, str5, str6, str7);
/*     */     } 
/*  92 */     char c = csv.getFieldSeparatorRead();
/*  93 */     String[] arrayOfString = StringUtils.arraySplit(str2, c, true);
/*     */     
/*  95 */     try (ResultSet null = csv.read(str1, arrayOfString, str4)) {
/*  96 */       resultInterface = FunctionAlias.JavaMethod.resultSetToResult(paramSessionLocal, resultSet, 0);
/*  97 */     } catch (SQLException sQLException) {
/*  98 */       throw DbException.convert(sQLException);
/*     */     } finally {
/* 100 */       csv.close();
/*     */     } 
/* 102 */     return resultInterface;
/*     */   }
/*     */   
/*     */   private static String getValue(SessionLocal paramSessionLocal, Expression[] paramArrayOfExpression, int paramInt) {
/* 106 */     return (paramInt < paramArrayOfExpression.length) ? paramArrayOfExpression[paramInt].getValue(paramSessionLocal).getString() : null;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 111 */     return "CSVREAD";
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDeterministic() {
/* 116 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\table\CSVReadFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */