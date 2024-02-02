/*     */ package com.mysql.cj.xdevapi;
/*     */ 
/*     */ import com.mysql.cj.x.protobuf.MysqlxCrud;
/*     */ import com.mysql.cj.x.protobuf.MysqlxExpr;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.stream.Collectors;
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
/*     */ public class InsertParams
/*     */ {
/*     */   private List<MysqlxCrud.Column> projection;
/*  47 */   private List<MysqlxCrud.Insert.TypedRow> rows = new LinkedList<>();
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setProjection(String[] projection) {
/*  56 */     this.projection = (List<MysqlxCrud.Column>)Arrays.<String>stream(projection).map(p -> (new ExprParser(p, true)).parseTableInsertField()).collect(Collectors.toList());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getProjection() {
/*  65 */     return this.projection;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addRow(List<Object> row) {
/*  75 */     this.rows.add(MysqlxCrud.Insert.TypedRow.newBuilder().addAllField((Iterable)row.stream().map(f -> ExprUtil.argObjectToExpr(f, true)).collect(Collectors.toList())).build());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getRows() {
/*  84 */     return this.rows;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFieldsAndValues(Map<String, Object> fieldsAndValues) {
/*  94 */     this.projection = new ArrayList<>();
/*  95 */     MysqlxCrud.Insert.TypedRow.Builder rowBuilder = MysqlxCrud.Insert.TypedRow.newBuilder();
/*  96 */     fieldsAndValues.entrySet().stream().forEach(e -> {
/*     */           this.projection.add((new ExprParser((String)e.getKey(), true)).parseTableInsertField());
/*     */           rowBuilder.addField(ExprUtil.argObjectToExpr(e.getValue(), true));
/*     */         });
/* 100 */     this.rows.add(rowBuilder.build());
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\InsertParams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */