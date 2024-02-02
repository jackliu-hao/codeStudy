/*    */ package org.h2.result;
/*    */ 
/*    */ import java.io.IOException;
/*    */ import org.h2.value.Transfer;
/*    */ import org.h2.value.TypeInfo;
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
/*    */ public class ResultColumn
/*    */ {
/*    */   final String alias;
/*    */   final String schemaName;
/*    */   final String tableName;
/*    */   final String columnName;
/*    */   final TypeInfo columnType;
/*    */   final boolean identity;
/*    */   final int nullable;
/*    */   
/*    */   ResultColumn(Transfer paramTransfer) throws IOException {
/* 60 */     this.alias = paramTransfer.readString();
/* 61 */     this.schemaName = paramTransfer.readString();
/* 62 */     this.tableName = paramTransfer.readString();
/* 63 */     this.columnName = paramTransfer.readString();
/* 64 */     this.columnType = paramTransfer.readTypeInfo();
/* 65 */     if (paramTransfer.getVersion() < 20) {
/* 66 */       paramTransfer.readInt();
/*    */     }
/* 68 */     this.identity = paramTransfer.readBoolean();
/* 69 */     this.nullable = paramTransfer.readInt();
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
/*    */   public static void writeColumn(Transfer paramTransfer, ResultInterface paramResultInterface, int paramInt) throws IOException {
/* 82 */     paramTransfer.writeString(paramResultInterface.getAlias(paramInt));
/* 83 */     paramTransfer.writeString(paramResultInterface.getSchemaName(paramInt));
/* 84 */     paramTransfer.writeString(paramResultInterface.getTableName(paramInt));
/* 85 */     paramTransfer.writeString(paramResultInterface.getColumnName(paramInt));
/* 86 */     TypeInfo typeInfo = paramResultInterface.getColumnType(paramInt);
/* 87 */     paramTransfer.writeTypeInfo(typeInfo);
/* 88 */     if (paramTransfer.getVersion() < 20) {
/* 89 */       paramTransfer.writeInt(typeInfo.getDisplaySize());
/*    */     }
/* 91 */     paramTransfer.writeBoolean(paramResultInterface.isIdentity(paramInt));
/* 92 */     paramTransfer.writeInt(paramResultInterface.getNullable(paramInt));
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\result\ResultColumn.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */