/*    */ package org.h2.expression.aggregate;
/*    */ 
/*    */ import java.util.ArrayList;
/*    */ import org.h2.engine.SessionLocal;
/*    */ import org.h2.expression.Expression;
/*    */ import org.h2.expression.ExpressionColumn;
/*    */ import org.h2.index.Index;
/*    */ import org.h2.table.Column;
/*    */ import org.h2.table.TableFilter;
/*    */ import org.h2.util.geometry.GeometryUtils;
/*    */ import org.h2.value.Value;
/*    */ import org.h2.value.ValueGeometry;
/*    */ import org.h2.value.ValueNull;
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
/*    */ final class AggregateDataEnvelope
/*    */   extends AggregateData
/*    */ {
/*    */   private double[] envelope;
/*    */   
/*    */   static Index getGeometryColumnIndex(Expression paramExpression) {
/* 38 */     if (paramExpression instanceof ExpressionColumn) {
/* 39 */       ExpressionColumn expressionColumn = (ExpressionColumn)paramExpression;
/* 40 */       Column column = expressionColumn.getColumn();
/* 41 */       if (column.getType().getValueType() == 37) {
/* 42 */         TableFilter tableFilter = expressionColumn.getTableFilter();
/* 43 */         if (tableFilter != null) {
/* 44 */           ArrayList<Index> arrayList = tableFilter.getTable().getIndexes();
/* 45 */           if (arrayList != null) {
/* 46 */             byte b; int i; for (b = 1, i = arrayList.size(); b < i; b++) {
/* 47 */               Index index = arrayList.get(b);
/* 48 */               if (index instanceof org.h2.mvstore.db.MVSpatialIndex && index.isFirstColumn(column)) {
/* 49 */                 return index;
/*    */               }
/*    */             } 
/*    */           } 
/*    */         } 
/*    */       } 
/*    */     } 
/* 56 */     return null;
/*    */   }
/*    */ 
/*    */   
/*    */   void add(SessionLocal paramSessionLocal, Value paramValue) {
/* 61 */     if (paramValue == ValueNull.INSTANCE) {
/*    */       return;
/*    */     }
/* 64 */     this.envelope = GeometryUtils.union(this.envelope, paramValue.convertToGeometry(null).getEnvelopeNoCopy());
/*    */   }
/*    */ 
/*    */   
/*    */   Value getValue(SessionLocal paramSessionLocal) {
/* 69 */     return ValueGeometry.fromEnvelope(this.envelope);
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\expression\aggregate\AggregateDataEnvelope.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */