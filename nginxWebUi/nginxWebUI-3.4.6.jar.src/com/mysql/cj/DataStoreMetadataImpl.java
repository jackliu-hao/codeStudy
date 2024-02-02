/*    */ package com.mysql.cj;
/*    */ 
/*    */ import com.mysql.cj.protocol.Message;
/*    */ import com.mysql.cj.result.LongValueFactory;
/*    */ import com.mysql.cj.result.Row;
/*    */ import com.mysql.cj.result.ValueFactory;
/*    */ import com.mysql.cj.xdevapi.ExprUnparser;
/*    */ import java.util.List;
/*    */ import java.util.function.Function;
/*    */ import java.util.stream.Collectors;
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
/*    */ 
/*    */ 
/*    */ public class DataStoreMetadataImpl
/*    */   implements DataStoreMetadata
/*    */ {
/*    */   private Session session;
/*    */   
/*    */   public DataStoreMetadataImpl(Session sess) {
/* 44 */     this.session = sess;
/*    */   }
/*    */   
/*    */   public boolean schemaExists(String schemaName) {
/* 48 */     StringBuilder stmt = new StringBuilder("select count(*) from information_schema.schemata where schema_name = '");
/*    */     
/* 50 */     stmt.append(schemaName.replaceAll("'", "\\'"));
/* 51 */     stmt.append("'");
/*    */     
/* 53 */     Function<Row, Long> rowToLong = r -> (Long)r.getValue(0, (ValueFactory)new LongValueFactory(this.session.getPropertySet()));
/* 54 */     List<Long> counters = this.session.<Message, Long, List<Long>>query(this.session.<Message>getMessageBuilder().buildSqlStatement(stmt.toString()), null, rowToLong, Collectors.toList());
/* 55 */     return (1L == ((Long)counters.get(0)).longValue());
/*    */   }
/*    */   
/*    */   public boolean tableExists(String schemaName, String tableName) {
/* 59 */     StringBuilder stmt = new StringBuilder("select count(*) from information_schema.tables where table_schema = '");
/*    */     
/* 61 */     stmt.append(schemaName.replaceAll("'", "\\'"));
/* 62 */     stmt.append("' and table_name = '");
/* 63 */     stmt.append(tableName.replaceAll("'", "\\'"));
/* 64 */     stmt.append("'");
/*    */     
/* 66 */     Function<Row, Long> rowToLong = r -> (Long)r.getValue(0, (ValueFactory)new LongValueFactory(this.session.getPropertySet()));
/* 67 */     List<Long> counters = this.session.<Message, Long, List<Long>>query(this.session.<Message>getMessageBuilder().buildSqlStatement(stmt.toString()), null, rowToLong, Collectors.toList());
/* 68 */     return (1L == ((Long)counters.get(0)).longValue());
/*    */   }
/*    */ 
/*    */   
/*    */   public long getTableRowCount(String schemaName, String tableName) {
/* 73 */     StringBuilder stmt = new StringBuilder("select count(*) from ");
/* 74 */     stmt.append(ExprUnparser.quoteIdentifier(schemaName));
/* 75 */     stmt.append(".");
/* 76 */     stmt.append(ExprUnparser.quoteIdentifier(tableName));
/*    */     
/* 78 */     Function<Row, Long> rowToLong = r -> (Long)r.getValue(0, (ValueFactory)new LongValueFactory(this.session.getPropertySet()));
/* 79 */     List<Long> counters = this.session.<Message, Long, List<Long>>query(this.session.<Message>getMessageBuilder().buildSqlStatement(stmt.toString()), null, rowToLong, Collectors.toList());
/* 80 */     return ((Long)counters.get(0)).longValue();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\DataStoreMetadataImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */