/*     */ package org.h2.command.dml;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.sql.ResultSet;
/*     */ import org.h2.command.Prepared;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.expression.ExpressionColumn;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.LocalResult;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.tools.Csv;
/*     */ import org.h2.util.Utils;
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
/*     */ 
/*     */ 
/*     */ public class Help
/*     */   extends Prepared
/*     */ {
/*     */   private final String[] conditions;
/*     */   private final Expression[] expressions;
/*     */   
/*     */   public Help(SessionLocal paramSessionLocal, String[] paramArrayOfString) {
/*  39 */     super(paramSessionLocal);
/*  40 */     this.conditions = paramArrayOfString;
/*  41 */     Database database = paramSessionLocal.getDatabase();
/*  42 */     this.expressions = new Expression[] { (Expression)new ExpressionColumn(database, new Column("SECTION", TypeInfo.TYPE_VARCHAR)), (Expression)new ExpressionColumn(database, new Column("TOPIC", TypeInfo.TYPE_VARCHAR)), (Expression)new ExpressionColumn(database, new Column("SYNTAX", TypeInfo.TYPE_VARCHAR)), (Expression)new ExpressionColumn(database, new Column("TEXT", TypeInfo.TYPE_VARCHAR)) };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultInterface queryMeta() {
/*  52 */     LocalResult localResult = new LocalResult(this.session, this.expressions, 4, 4);
/*  53 */     localResult.done();
/*  54 */     return (ResultInterface)localResult;
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface query(long paramLong) {
/*  59 */     LocalResult localResult = new LocalResult(this.session, this.expressions, 4, 4);
/*     */     try {
/*  61 */       ResultSet resultSet = getTable();
/*  62 */       label20: while (resultSet.next()) {
/*  63 */         String str = resultSet.getString(2).trim();
/*  64 */         for (String str1 : this.conditions) {
/*  65 */           if (!str.contains(str1)) {
/*     */             continue label20;
/*     */           }
/*     */         } 
/*  69 */         localResult.addRow(new Value[] {
/*     */               
/*  71 */               ValueVarchar.get(resultSet.getString(1).trim(), (CastDataProvider)this.session), 
/*     */               
/*  73 */               ValueVarchar.get(str, (CastDataProvider)this.session), 
/*     */               
/*  75 */               ValueVarchar.get(stripAnnotationsFromSyntax(resultSet.getString(3)), (CastDataProvider)this.session), 
/*     */               
/*  77 */               ValueVarchar.get(processHelpText(resultSet.getString(4)), (CastDataProvider)this.session) });
/*     */       } 
/*  79 */     } catch (Exception exception) {
/*  80 */       throw DbException.convert(exception);
/*     */     } 
/*  82 */     localResult.done();
/*  83 */     return (ResultInterface)localResult;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String stripAnnotationsFromSyntax(String paramString) {
/*  94 */     return paramString.replaceAll("@c@ ", "").replaceAll("@h2@ ", "")
/*  95 */       .replaceAll("@c@", "").replaceAll("@h2@", "").trim();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String processHelpText(String paramString) {
/* 104 */     int i = paramString.length();
/* 105 */     byte b = 0;
/* 106 */     for (; b < i; b++) {
/* 107 */       char c = paramString.charAt(b);
/* 108 */       if (c == '.') {
/* 109 */         b++;
/*     */         break;
/*     */       } 
/* 112 */       if (c == '"') {
/*     */         do {
/* 114 */           b++;
/* 115 */         } while (b < i && paramString.charAt(b) != '"');
/*     */       }
/*     */     } 
/* 118 */     paramString = paramString.substring(0, b);
/* 119 */     return paramString.trim();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ResultSet getTable() throws IOException {
/* 130 */     InputStreamReader inputStreamReader = new InputStreamReader(new ByteArrayInputStream(Utils.getResource("/org/h2/res/help.csv")));
/* 131 */     Csv csv = new Csv();
/* 132 */     csv.setLineCommentCharacter('#');
/* 133 */     return csv.read(inputStreamReader, null);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isQuery() {
/* 138 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isTransactional() {
/* 143 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isReadOnly() {
/* 148 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getType() {
/* 153 */     return 57;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isCacheable() {
/* 158 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\dml\Help.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */