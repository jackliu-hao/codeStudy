/*     */ package org.h2.jdbc.meta;
/*     */ 
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.result.SimpleResult;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueArray;
/*     */ import org.h2.value.ValueInteger;
/*     */ import org.h2.value.ValueNull;
/*     */ import org.h2.value.ValueVarchar;
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
/*     */ public final class DatabaseMetaServer
/*     */ {
/*     */   public static ResultInterface process(SessionLocal paramSessionLocal, int paramInt, Value[] paramArrayOfValue) {
/*  68 */     DatabaseMeta databaseMeta = paramSessionLocal.getDatabaseMeta();
/*  69 */     switch (paramInt) {
/*     */       case 0:
/*  71 */         return result(databaseMeta.defaultNullOrdering().ordinal());
/*     */       case 1:
/*  73 */         return result(paramSessionLocal, databaseMeta.getDatabaseProductVersion());
/*     */       case 2:
/*  75 */         return result(paramSessionLocal, databaseMeta.getSQLKeywords());
/*     */       case 3:
/*  77 */         return result(paramSessionLocal, databaseMeta.getNumericFunctions());
/*     */       case 4:
/*  79 */         return result(paramSessionLocal, databaseMeta.getStringFunctions());
/*     */       case 5:
/*  81 */         return result(paramSessionLocal, databaseMeta.getSystemFunctions());
/*     */       case 6:
/*  83 */         return result(paramSessionLocal, databaseMeta.getTimeDateFunctions());
/*     */       case 7:
/*  85 */         return result(paramSessionLocal, databaseMeta.getSearchStringEscape());
/*     */       case 8:
/*  87 */         return databaseMeta.getProcedures(paramArrayOfValue[0].getString(), paramArrayOfValue[1].getString(), paramArrayOfValue[2].getString());
/*     */       case 9:
/*  89 */         return databaseMeta.getProcedureColumns(paramArrayOfValue[0].getString(), paramArrayOfValue[1].getString(), paramArrayOfValue[2].getString(), paramArrayOfValue[3]
/*  90 */             .getString());
/*     */       case 10:
/*  92 */         return databaseMeta.getTables(paramArrayOfValue[0].getString(), paramArrayOfValue[1].getString(), paramArrayOfValue[2].getString(), 
/*  93 */             toStringArray(paramArrayOfValue[3]));
/*     */       case 11:
/*  95 */         return databaseMeta.getSchemas();
/*     */       case 12:
/*  97 */         return databaseMeta.getCatalogs();
/*     */       case 13:
/*  99 */         return databaseMeta.getTableTypes();
/*     */       case 14:
/* 101 */         return databaseMeta.getColumns(paramArrayOfValue[0].getString(), paramArrayOfValue[1].getString(), paramArrayOfValue[2].getString(), paramArrayOfValue[3].getString());
/*     */       case 15:
/* 103 */         return databaseMeta.getColumnPrivileges(paramArrayOfValue[0].getString(), paramArrayOfValue[1].getString(), paramArrayOfValue[2].getString(), paramArrayOfValue[3]
/* 104 */             .getString());
/*     */       case 16:
/* 106 */         return databaseMeta.getTablePrivileges(paramArrayOfValue[0].getString(), paramArrayOfValue[1].getString(), paramArrayOfValue[2].getString());
/*     */       case 17:
/* 108 */         return databaseMeta.getBestRowIdentifier(paramArrayOfValue[0].getString(), paramArrayOfValue[1].getString(), paramArrayOfValue[2].getString(), paramArrayOfValue[3]
/* 109 */             .getInt(), paramArrayOfValue[4].getBoolean());
/*     */       case 18:
/* 111 */         return databaseMeta.getVersionColumns(paramArrayOfValue[0].getString(), paramArrayOfValue[1].getString(), paramArrayOfValue[2].getString());
/*     */       case 19:
/* 113 */         return databaseMeta.getPrimaryKeys(paramArrayOfValue[0].getString(), paramArrayOfValue[1].getString(), paramArrayOfValue[2].getString());
/*     */       case 20:
/* 115 */         return databaseMeta.getImportedKeys(paramArrayOfValue[0].getString(), paramArrayOfValue[1].getString(), paramArrayOfValue[2].getString());
/*     */       case 21:
/* 117 */         return databaseMeta.getExportedKeys(paramArrayOfValue[0].getString(), paramArrayOfValue[1].getString(), paramArrayOfValue[2].getString());
/*     */       case 22:
/* 119 */         return databaseMeta.getCrossReference(paramArrayOfValue[0].getString(), paramArrayOfValue[1].getString(), paramArrayOfValue[2].getString(), paramArrayOfValue[3]
/* 120 */             .getString(), paramArrayOfValue[4].getString(), paramArrayOfValue[5].getString());
/*     */       case 23:
/* 122 */         return databaseMeta.getTypeInfo();
/*     */       case 24:
/* 124 */         return databaseMeta.getIndexInfo(paramArrayOfValue[0].getString(), paramArrayOfValue[1].getString(), paramArrayOfValue[2].getString(), paramArrayOfValue[3]
/* 125 */             .getBoolean(), paramArrayOfValue[4].getBoolean());
/*     */       case 25:
/* 127 */         return databaseMeta.getUDTs(paramArrayOfValue[0].getString(), paramArrayOfValue[1].getString(), paramArrayOfValue[2].getString(), toIntArray(paramArrayOfValue[3]));
/*     */       case 26:
/* 129 */         return databaseMeta.getSuperTypes(paramArrayOfValue[0].getString(), paramArrayOfValue[1].getString(), paramArrayOfValue[2].getString());
/*     */       case 27:
/* 131 */         return databaseMeta.getSuperTables(paramArrayOfValue[0].getString(), paramArrayOfValue[1].getString(), paramArrayOfValue[2].getString());
/*     */       case 28:
/* 133 */         return databaseMeta.getAttributes(paramArrayOfValue[0].getString(), paramArrayOfValue[1].getString(), paramArrayOfValue[2].getString(), paramArrayOfValue[3]
/* 134 */             .getString());
/*     */       case 29:
/* 136 */         return result(databaseMeta.getDatabaseMajorVersion());
/*     */       case 30:
/* 138 */         return result(databaseMeta.getDatabaseMinorVersion());
/*     */       case 31:
/* 140 */         return databaseMeta.getSchemas(paramArrayOfValue[0].getString(), paramArrayOfValue[1].getString());
/*     */       case 32:
/* 142 */         return databaseMeta.getFunctions(paramArrayOfValue[0].getString(), paramArrayOfValue[1].getString(), paramArrayOfValue[2].getString());
/*     */       case 33:
/* 144 */         return databaseMeta.getFunctionColumns(paramArrayOfValue[0].getString(), paramArrayOfValue[1].getString(), paramArrayOfValue[2].getString(), paramArrayOfValue[3]
/* 145 */             .getString());
/*     */       case 34:
/* 147 */         return databaseMeta.getPseudoColumns(paramArrayOfValue[0].getString(), paramArrayOfValue[1].getString(), paramArrayOfValue[2].getString(), paramArrayOfValue[3]
/* 148 */             .getString());
/*     */     } 
/* 150 */     throw DbException.getUnsupportedException("META " + paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   private static String[] toStringArray(Value paramValue) {
/* 155 */     if (paramValue == ValueNull.INSTANCE) {
/* 156 */       return null;
/*     */     }
/* 158 */     Value[] arrayOfValue = ((ValueArray)paramValue).getList();
/* 159 */     int i = arrayOfValue.length;
/* 160 */     String[] arrayOfString = new String[i];
/* 161 */     for (byte b = 0; b < i; b++) {
/* 162 */       arrayOfString[b] = arrayOfValue[b].getString();
/*     */     }
/* 164 */     return arrayOfString;
/*     */   }
/*     */   
/*     */   private static int[] toIntArray(Value paramValue) {
/* 168 */     if (paramValue == ValueNull.INSTANCE) {
/* 169 */       return null;
/*     */     }
/* 171 */     Value[] arrayOfValue = ((ValueArray)paramValue).getList();
/* 172 */     int i = arrayOfValue.length;
/* 173 */     int[] arrayOfInt = new int[i];
/* 174 */     for (byte b = 0; b < i; b++) {
/* 175 */       arrayOfInt[b] = arrayOfValue[b].getInt();
/*     */     }
/* 177 */     return arrayOfInt;
/*     */   }
/*     */   
/*     */   private static ResultInterface result(int paramInt) {
/* 181 */     return result((Value)ValueInteger.get(paramInt));
/*     */   }
/*     */   
/*     */   private static ResultInterface result(SessionLocal paramSessionLocal, String paramString) {
/* 185 */     return result(ValueVarchar.get(paramString, (CastDataProvider)paramSessionLocal));
/*     */   }
/*     */   
/*     */   private static ResultInterface result(Value paramValue) {
/* 189 */     SimpleResult simpleResult = new SimpleResult();
/* 190 */     simpleResult.addColumn("RESULT", paramValue.getType());
/* 191 */     simpleResult.addRow(new Value[] { paramValue });
/* 192 */     return (ResultInterface)simpleResult;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\jdbc\meta\DatabaseMetaServer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */