/*    */ package org.h2.command.ddl;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.schema.Schema;
/*    */ import org.h2.table.Column;
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
/*    */ 
/*    */ 
/*    */ 
/*    */ public class CreateTableData
/*    */ {
/*    */   public Schema schema;
/*    */   public String tableName;
/*    */   public int id;
/* 37 */   public ArrayList<Column> columns = new ArrayList<>();
/*    */   public boolean temporary;
/*    */   public boolean globalTemporary;
/*    */   public boolean persistIndexes;
/*    */   public boolean persistData;
/*    */   public SessionLocal session;
/*    */   public String tableEngine;
/*    */   public ArrayList<String> tableEngineParams;
/*    */   public boolean isHidden;
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\ddl\CreateTableData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */