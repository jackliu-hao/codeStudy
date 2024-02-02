/*    */ package org.h2.bnf.context;
/*    */ 
/*    */ import java.sql.DatabaseMetaData;
/*    */ import java.sql.ResultSet;
/*    */ import java.sql.SQLException;
/*    */ import java.util.ArrayList;
/*    */ import org.h2.util.Utils;
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
/*    */ public class DbProcedure
/*    */ {
/*    */   private final DbSchema schema;
/*    */   private final String name;
/*    */   private final String quotedName;
/*    */   private final boolean returnsResult;
/*    */   private DbColumn[] parameters;
/*    */   
/*    */   public DbProcedure(DbSchema paramDbSchema, ResultSet paramResultSet) throws SQLException {
/* 28 */     this.schema = paramDbSchema;
/* 29 */     this.name = paramResultSet.getString("PROCEDURE_NAME");
/* 30 */     this.returnsResult = (paramResultSet.getShort("PROCEDURE_TYPE") == 2);
/*    */     
/* 32 */     this.quotedName = paramDbSchema.getContents().quoteIdentifier(this.name);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DbSchema getSchema() {
/* 39 */     return this.schema;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DbColumn[] getParameters() {
/* 46 */     return this.parameters;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getName() {
/* 53 */     return this.name;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public String getQuotedName() {
/* 60 */     return this.quotedName;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isReturnsResult() {
/* 67 */     return this.returnsResult;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   void readParameters(DatabaseMetaData paramDatabaseMetaData) throws SQLException {
/* 77 */     ResultSet resultSet = paramDatabaseMetaData.getProcedureColumns(null, this.schema.name, this.name, null);
/* 78 */     ArrayList<DbColumn> arrayList = Utils.newSmallArrayList();
/* 79 */     while (resultSet.next()) {
/* 80 */       DbColumn dbColumn = DbColumn.getProcedureColumn(this.schema.getContents(), resultSet);
/* 81 */       if (dbColumn.getPosition() > 0)
/*    */       {
/* 83 */         arrayList.add(dbColumn);
/*    */       }
/*    */     } 
/* 86 */     resultSet.close();
/* 87 */     this.parameters = new DbColumn[arrayList.size()];
/*    */     
/* 89 */     for (byte b = 0; b < this.parameters.length; b++) {
/* 90 */       DbColumn dbColumn = arrayList.get(b);
/* 91 */       if (dbColumn.getPosition() > 0 && dbColumn
/* 92 */         .getPosition() <= this.parameters.length)
/* 93 */         this.parameters[dbColumn.getPosition() - 1] = dbColumn; 
/*    */     } 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\bnf\context\DbProcedure.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */