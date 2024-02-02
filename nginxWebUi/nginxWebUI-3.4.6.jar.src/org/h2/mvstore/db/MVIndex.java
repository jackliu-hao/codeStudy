/*    */ package org.h2.mvstore.db;
/*    */ 
/*    */ import java.util.List;
/*    */ import org.h2.index.Index;
/*    */ import org.h2.index.IndexType;
/*    */ import org.h2.mvstore.MVMap;
/*    */ import org.h2.result.Row;
/*    */ import org.h2.table.IndexColumn;
/*    */ import org.h2.table.Table;
/*    */ import org.h2.value.VersionedValue;
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
/*    */ public abstract class MVIndex<K, V>
/*    */   extends Index
/*    */ {
/*    */   protected MVIndex(Table paramTable, int paramInt1, String paramString, IndexColumn[] paramArrayOfIndexColumn, int paramInt2, IndexType paramIndexType) {
/* 25 */     super(paramTable, paramInt1, paramString, paramArrayOfIndexColumn, paramInt2, paramIndexType);
/*    */   }
/*    */   
/*    */   public abstract void addRowsToBuffer(List<Row> paramList, String paramString);
/*    */   
/*    */   public abstract void addBufferedRows(List<String> paramList);
/*    */   
/*    */   public abstract MVMap<K, VersionedValue<V>> getMVMap();
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mvstore\db\MVIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */