/*    */ package com.mysql.cj.xdevapi;
/*    */ 
/*    */ import java.util.Arrays;
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
/*    */ public class TableFilterParams
/*    */   extends AbstractFilterParams
/*    */ {
/*    */   public TableFilterParams(String schemaName, String collectionName) {
/* 48 */     this(schemaName, collectionName, true);
/*    */   }
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
/*    */   public TableFilterParams(String schemaName, String collectionName, boolean supportsOffset) {
/* 62 */     super(schemaName, collectionName, supportsOffset, true);
/*    */   }
/*    */ 
/*    */   
/*    */   public void setFields(String... projection) {
/* 67 */     this.projection = projection;
/* 68 */     this.fields = (new ExprParser(Arrays.<CharSequence>stream((CharSequence[])projection).collect(Collectors.joining(", ")), true)).parseTableSelectProjection();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\TableFilterParams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */