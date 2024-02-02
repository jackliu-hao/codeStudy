/*    */ package com.mysql.cj.xdevapi;
/*    */ 
/*    */ import com.mysql.cj.conf.PropertySet;
/*    */ import com.mysql.cj.protocol.ColumnDefinition;
/*    */ import com.mysql.cj.protocol.ProtocolEntity;
/*    */ import com.mysql.cj.result.Field;
/*    */ import com.mysql.cj.result.RowList;
/*    */ import java.util.Arrays;
/*    */ import java.util.List;
/*    */ import java.util.TimeZone;
/*    */ import java.util.function.Function;
/*    */ import java.util.function.Supplier;
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
/*    */ public class RowResultImpl
/*    */   extends AbstractDataResult<Row>
/*    */   implements RowResult
/*    */ {
/*    */   private ColumnDefinition metadata;
/*    */   
/*    */   public RowResultImpl(ColumnDefinition metadata, TimeZone defaultTimeZone, RowList rows, Supplier<ProtocolEntity> completer, PropertySet pset) {
/* 65 */     super(rows, completer, new RowFactory(metadata, defaultTimeZone, pset));
/* 66 */     this.metadata = metadata;
/*    */   }
/*    */   
/*    */   public int getColumnCount() {
/* 70 */     return (this.metadata.getFields()).length;
/*    */   }
/*    */   
/*    */   public List<Column> getColumns() {
/* 74 */     return (List<Column>)Arrays.<Field>stream(this.metadata.getFields()).map(ColumnImpl::new).collect(Collectors.toList());
/*    */   }
/*    */   
/*    */   public List<String> getColumnNames() {
/* 78 */     return (List<String>)Arrays.<Field>stream(this.metadata.getFields()).map(Field::getColumnLabel).collect(Collectors.toList());
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\RowResultImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */