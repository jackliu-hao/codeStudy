/*    */ package com.mysql.cj.xdevapi;
/*    */ 
/*    */ import com.mysql.cj.x.protobuf.MysqlxCrud;
/*    */ import java.util.Arrays;
/*    */ import java.util.Collections;
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
/*    */ public class DocFilterParams
/*    */   extends AbstractFilterParams
/*    */ {
/*    */   public DocFilterParams(String schemaName, String collectionName) {
/* 52 */     this(schemaName, collectionName, true);
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
/*    */   public DocFilterParams(String schemaName, String collectionName, boolean supportsOffset) {
/* 66 */     super(schemaName, collectionName, supportsOffset, false);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public void setFields(Expression docProjection) {
/* 76 */     this.fields = Collections.singletonList(MysqlxCrud.Projection.newBuilder().setSource((new ExprParser(docProjection.getExpressionString(), false)).parse()).build());
/*    */   }
/*    */ 
/*    */   
/*    */   public void setFields(String... projection) {
/* 81 */     this.fields = (new ExprParser(Arrays.<CharSequence>stream((CharSequence[])projection).collect(Collectors.joining(", ")), false)).parseDocumentProjection();
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\DocFilterParams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */