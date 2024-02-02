/*     */ package org.h2.expression.function.table;
/*     */ 
/*     */ import java.sql.Connection;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.jdbc.JdbcConnection;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.result.SimpleResult;
/*     */ import org.h2.util.JdbcUtils;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueVarchar;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class LinkSchemaFunction
/*     */   extends TableFunction
/*     */ {
/*     */   public LinkSchemaFunction() {
/*  30 */     super(new org.h2.expression.Expression[6]);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface getValue(SessionLocal paramSessionLocal) {
/*  35 */     paramSessionLocal.getUser().checkAdmin();
/*  36 */     String str1 = getValue(paramSessionLocal, 0);
/*  37 */     String str2 = getValue(paramSessionLocal, 1);
/*  38 */     String str3 = getValue(paramSessionLocal, 2);
/*  39 */     String str4 = getValue(paramSessionLocal, 3);
/*  40 */     String str5 = getValue(paramSessionLocal, 4);
/*  41 */     String str6 = getValue(paramSessionLocal, 5);
/*  42 */     if (str1 == null || str2 == null || str3 == null || str4 == null || str5 == null || str6 == null)
/*     */     {
/*  44 */       return getValueTemplate(paramSessionLocal);
/*     */     }
/*  46 */     JdbcConnection jdbcConnection = paramSessionLocal.createConnection(false);
/*  47 */     Connection connection = null;
/*  48 */     Statement statement = null;
/*  49 */     ResultSet resultSet = null;
/*  50 */     SimpleResult simpleResult = new SimpleResult();
/*  51 */     simpleResult.addColumn("TABLE_NAME", TypeInfo.TYPE_VARCHAR);
/*     */     try {
/*  53 */       connection = JdbcUtils.getConnection(str2, str3, str4, str5);
/*  54 */       statement = jdbcConnection.createStatement();
/*  55 */       statement.execute(StringUtils.quoteIdentifier(new StringBuilder("CREATE SCHEMA IF NOT EXISTS "), str1)
/*  56 */           .toString());
/*     */       
/*  58 */       if (str3.startsWith("jdbc:postgresql:")) {
/*  59 */         resultSet = connection.getMetaData().getTables(null, str6, null, new String[] { "TABLE", "LINKED TABLE", "VIEW", "EXTERNAL" });
/*     */       } else {
/*     */         
/*  62 */         resultSet = connection.getMetaData().getTables(null, str6, null, null);
/*     */       } 
/*  64 */       while (resultSet.next()) {
/*  65 */         String str = resultSet.getString("TABLE_NAME");
/*  66 */         StringBuilder stringBuilder = new StringBuilder();
/*  67 */         stringBuilder.append("DROP TABLE IF EXISTS ");
/*  68 */         StringUtils.quoteIdentifier(stringBuilder, str1).append('.');
/*  69 */         StringUtils.quoteIdentifier(stringBuilder, str);
/*  70 */         statement.execute(stringBuilder.toString());
/*  71 */         stringBuilder.setLength(0);
/*  72 */         stringBuilder.append("CREATE LINKED TABLE ");
/*  73 */         StringUtils.quoteIdentifier(stringBuilder, str1).append('.');
/*  74 */         StringUtils.quoteIdentifier(stringBuilder, str).append('(');
/*  75 */         StringUtils.quoteStringSQL(stringBuilder, str2).append(", ");
/*  76 */         StringUtils.quoteStringSQL(stringBuilder, str3).append(", ");
/*  77 */         StringUtils.quoteStringSQL(stringBuilder, str4).append(", ");
/*  78 */         StringUtils.quoteStringSQL(stringBuilder, str5).append(", ");
/*  79 */         StringUtils.quoteStringSQL(stringBuilder, str6).append(", ");
/*  80 */         StringUtils.quoteStringSQL(stringBuilder, str).append(')');
/*  81 */         statement.execute(stringBuilder.toString());
/*  82 */         simpleResult.addRow(new Value[] { ValueVarchar.get(str, (CastDataProvider)paramSessionLocal) });
/*     */       } 
/*  84 */     } catch (SQLException sQLException) {
/*  85 */       simpleResult.close();
/*  86 */       throw DbException.convert(sQLException);
/*     */     } finally {
/*  88 */       JdbcUtils.closeSilently(resultSet);
/*  89 */       JdbcUtils.closeSilently(connection);
/*  90 */       JdbcUtils.closeSilently(statement);
/*     */     } 
/*  92 */     return (ResultInterface)simpleResult;
/*     */   }
/*     */   
/*     */   private String getValue(SessionLocal paramSessionLocal, int paramInt) {
/*  96 */     return this.args[paramInt].getValue(paramSessionLocal).getString();
/*     */   }
/*     */ 
/*     */   
/*     */   public void optimize(SessionLocal paramSessionLocal) {
/* 101 */     super.optimize(paramSessionLocal);
/* 102 */     int i = this.args.length;
/* 103 */     if (i != 6) {
/* 104 */       throw DbException.get(7001, new String[] { getName(), "6" });
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface getValueTemplate(SessionLocal paramSessionLocal) {
/* 110 */     SimpleResult simpleResult = new SimpleResult();
/* 111 */     simpleResult.addColumn("TABLE_NAME", TypeInfo.TYPE_VARCHAR);
/* 112 */     return (ResultInterface)simpleResult;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/* 117 */     return "LINK_SCHEMA";
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isDeterministic() {
/* 122 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\function\table\LinkSchemaFunction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */