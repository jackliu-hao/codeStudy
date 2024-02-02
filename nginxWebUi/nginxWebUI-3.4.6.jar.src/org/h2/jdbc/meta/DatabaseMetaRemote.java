/*     */ package org.h2.jdbc.meta;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import org.h2.engine.CastDataProvider;
/*     */ import org.h2.engine.SessionRemote;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.mode.DefaultNullOrdering;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.result.ResultRemote;
/*     */ import org.h2.value.Transfer;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueArray;
/*     */ import org.h2.value.ValueBoolean;
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
/*     */ public class DatabaseMetaRemote
/*     */   extends DatabaseMeta
/*     */ {
/*     */   static final int DEFAULT_NULL_ORDERING = 0;
/*     */   static final int GET_DATABASE_PRODUCT_VERSION = 1;
/*     */   static final int GET_SQL_KEYWORDS = 2;
/*     */   static final int GET_NUMERIC_FUNCTIONS = 3;
/*     */   static final int GET_STRING_FUNCTIONS = 4;
/*     */   static final int GET_SYSTEM_FUNCTIONS = 5;
/*     */   static final int GET_TIME_DATE_FUNCTIONS = 6;
/*     */   static final int GET_SEARCH_STRING_ESCAPE = 7;
/*     */   static final int GET_PROCEDURES_3 = 8;
/*     */   static final int GET_PROCEDURE_COLUMNS_4 = 9;
/*     */   static final int GET_TABLES_4 = 10;
/*     */   static final int GET_SCHEMAS = 11;
/*     */   static final int GET_CATALOGS = 12;
/*     */   static final int GET_TABLE_TYPES = 13;
/*     */   static final int GET_COLUMNS_4 = 14;
/*     */   static final int GET_COLUMN_PRIVILEGES_4 = 15;
/*     */   static final int GET_TABLE_PRIVILEGES_3 = 16;
/*     */   static final int GET_BEST_ROW_IDENTIFIER_5 = 17;
/*     */   static final int GET_VERSION_COLUMNS_3 = 18;
/*     */   static final int GET_PRIMARY_KEYS_3 = 19;
/*     */   static final int GET_IMPORTED_KEYS_3 = 20;
/*     */   static final int GET_EXPORTED_KEYS_3 = 21;
/*     */   static final int GET_CROSS_REFERENCE_6 = 22;
/*     */   static final int GET_TYPE_INFO = 23;
/*     */   static final int GET_INDEX_INFO_5 = 24;
/*     */   static final int GET_UDTS_4 = 25;
/*     */   static final int GET_SUPER_TYPES_3 = 26;
/*     */   static final int GET_SUPER_TABLES_3 = 27;
/*     */   static final int GET_ATTRIBUTES_4 = 28;
/*     */   static final int GET_DATABASE_MAJOR_VERSION = 29;
/*     */   static final int GET_DATABASE_MINOR_VERSION = 30;
/*     */   static final int GET_SCHEMAS_2 = 31;
/*     */   static final int GET_FUNCTIONS_3 = 32;
/*     */   static final int GET_FUNCTION_COLUMNS_4 = 33;
/*     */   static final int GET_PSEUDO_COLUMNS_4 = 34;
/*     */   private final SessionRemote session;
/*     */   private final ArrayList<Transfer> transferList;
/*     */   
/*     */   public DatabaseMetaRemote(SessionRemote paramSessionRemote, ArrayList<Transfer> paramArrayList) {
/* 106 */     this.session = paramSessionRemote;
/* 107 */     this.transferList = paramArrayList;
/*     */   }
/*     */ 
/*     */   
/*     */   public DefaultNullOrdering defaultNullOrdering() {
/* 112 */     ResultInterface resultInterface = executeQuery(0, new Value[0]);
/* 113 */     resultInterface.next();
/* 114 */     return DefaultNullOrdering.valueOf(resultInterface.currentRow()[0].getInt());
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDatabaseProductVersion() {
/* 119 */     ResultInterface resultInterface = executeQuery(1, new Value[0]);
/* 120 */     resultInterface.next();
/* 121 */     return resultInterface.currentRow()[0].getString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSQLKeywords() {
/* 126 */     ResultInterface resultInterface = executeQuery(2, new Value[0]);
/* 127 */     resultInterface.next();
/* 128 */     return resultInterface.currentRow()[0].getString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getNumericFunctions() {
/* 133 */     ResultInterface resultInterface = executeQuery(3, new Value[0]);
/* 134 */     resultInterface.next();
/* 135 */     return resultInterface.currentRow()[0].getString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getStringFunctions() {
/* 140 */     ResultInterface resultInterface = executeQuery(4, new Value[0]);
/* 141 */     resultInterface.next();
/* 142 */     return resultInterface.currentRow()[0].getString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSystemFunctions() {
/* 147 */     ResultInterface resultInterface = executeQuery(5, new Value[0]);
/* 148 */     resultInterface.next();
/* 149 */     return resultInterface.currentRow()[0].getString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getTimeDateFunctions() {
/* 154 */     ResultInterface resultInterface = executeQuery(6, new Value[0]);
/* 155 */     resultInterface.next();
/* 156 */     return resultInterface.currentRow()[0].getString();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSearchStringEscape() {
/* 161 */     ResultInterface resultInterface = executeQuery(7, new Value[0]);
/* 162 */     resultInterface.next();
/* 163 */     return resultInterface.currentRow()[0].getString();
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface getProcedures(String paramString1, String paramString2, String paramString3) {
/* 168 */     return executeQuery(8, new Value[] { getString(paramString1), getString(paramString2), 
/* 169 */           getString(paramString3) });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultInterface getProcedureColumns(String paramString1, String paramString2, String paramString3, String paramString4) {
/* 175 */     return executeQuery(9, new Value[] { getString(paramString1), getString(paramString2), 
/* 176 */           getString(paramString3), getString(paramString4) });
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface getTables(String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) {
/* 181 */     return executeQuery(10, new Value[] { getString(paramString1), getString(paramString2), getString(paramString3), 
/* 182 */           getStringArray(paramArrayOfString) });
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface getSchemas() {
/* 187 */     return executeQuery(11, new Value[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface getCatalogs() {
/* 192 */     return executeQuery(12, new Value[0]);
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface getTableTypes() {
/* 197 */     return executeQuery(13, new Value[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultInterface getColumns(String paramString1, String paramString2, String paramString3, String paramString4) {
/* 203 */     return executeQuery(14, new Value[] { getString(paramString1), getString(paramString2), getString(paramString3), 
/* 204 */           getString(paramString4) });
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface getColumnPrivileges(String paramString1, String paramString2, String paramString3, String paramString4) {
/* 209 */     return executeQuery(15, new Value[] { getString(paramString1), getString(paramString2), getString(paramString3), 
/* 210 */           getString(paramString4) });
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface getTablePrivileges(String paramString1, String paramString2, String paramString3) {
/* 215 */     return executeQuery(16, new Value[] { getString(paramString1), getString(paramString2), 
/* 216 */           getString(paramString3) });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultInterface getBestRowIdentifier(String paramString1, String paramString2, String paramString3, int paramInt, boolean paramBoolean) {
/* 222 */     return executeQuery(17, new Value[] { getString(paramString1), getString(paramString2), getString(paramString3), 
/* 223 */           (Value)ValueInteger.get(paramInt), (Value)ValueBoolean.get(paramBoolean) });
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface getVersionColumns(String paramString1, String paramString2, String paramString3) {
/* 228 */     return executeQuery(18, new Value[] { getString(paramString1), getString(paramString2), getString(paramString3) });
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface getPrimaryKeys(String paramString1, String paramString2, String paramString3) {
/* 233 */     return executeQuery(19, new Value[] { getString(paramString1), getString(paramString2), getString(paramString3) });
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface getImportedKeys(String paramString1, String paramString2, String paramString3) {
/* 238 */     return executeQuery(20, new Value[] { getString(paramString1), getString(paramString2), getString(paramString3) });
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface getExportedKeys(String paramString1, String paramString2, String paramString3) {
/* 243 */     return executeQuery(21, new Value[] { getString(paramString1), getString(paramString2), getString(paramString3) });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultInterface getCrossReference(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5, String paramString6) {
/* 249 */     return executeQuery(22, new Value[] { getString(paramString1), getString(paramString2), 
/* 250 */           getString(paramString3), getString(paramString4), getString(paramString5), getString(paramString6) });
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface getTypeInfo() {
/* 255 */     return executeQuery(23, new Value[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultInterface getIndexInfo(String paramString1, String paramString2, String paramString3, boolean paramBoolean1, boolean paramBoolean2) {
/* 261 */     return executeQuery(24, new Value[] { getString(paramString1), getString(paramString2), 
/* 262 */           getString(paramString3), (Value)ValueBoolean.get(paramBoolean1), (Value)ValueBoolean.get(paramBoolean2) });
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface getUDTs(String paramString1, String paramString2, String paramString3, int[] paramArrayOfint) {
/* 267 */     return executeQuery(25, new Value[] { getString(paramString1), getString(paramString2), getString(paramString3), 
/* 268 */           getIntArray(paramArrayOfint) });
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface getSuperTypes(String paramString1, String paramString2, String paramString3) {
/* 273 */     return executeQuery(26, new Value[] { getString(paramString1), getString(paramString2), 
/* 274 */           getString(paramString3) });
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface getSuperTables(String paramString1, String paramString2, String paramString3) {
/* 279 */     return executeQuery(27, new Value[] { getString(paramString1), getString(paramString2), 
/* 280 */           getString(paramString3) });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultInterface getAttributes(String paramString1, String paramString2, String paramString3, String paramString4) {
/* 286 */     return executeQuery(28, new Value[] { getString(paramString1), getString(paramString2), getString(paramString3), 
/* 287 */           getString(paramString4) });
/*     */   }
/*     */ 
/*     */   
/*     */   public int getDatabaseMajorVersion() {
/* 292 */     ResultInterface resultInterface = executeQuery(29, new Value[0]);
/* 293 */     resultInterface.next();
/* 294 */     return resultInterface.currentRow()[0].getInt();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getDatabaseMinorVersion() {
/* 299 */     ResultInterface resultInterface = executeQuery(30, new Value[0]);
/* 300 */     resultInterface.next();
/* 301 */     return resultInterface.currentRow()[0].getInt();
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface getSchemas(String paramString1, String paramString2) {
/* 306 */     return executeQuery(31, new Value[] { getString(paramString1), getString(paramString2) });
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface getFunctions(String paramString1, String paramString2, String paramString3) {
/* 311 */     return executeQuery(32, new Value[] { getString(paramString1), getString(paramString2), 
/* 312 */           getString(paramString3) });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultInterface getFunctionColumns(String paramString1, String paramString2, String paramString3, String paramString4) {
/* 318 */     return executeQuery(33, new Value[] { getString(paramString1), getString(paramString2), 
/* 319 */           getString(paramString3), getString(paramString4) });
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ResultInterface getPseudoColumns(String paramString1, String paramString2, String paramString3, String paramString4) {
/* 325 */     return executeQuery(34, new Value[] { getString(paramString1), getString(paramString2), 
/* 326 */           getString(paramString3), getString(paramString4) });
/*     */   }
/*     */   
/*     */   private ResultInterface executeQuery(int paramInt, Value... paramVarArgs) {
/* 330 */     if (this.session.isClosed()) {
/* 331 */       throw DbException.get(90121);
/*     */     }
/* 333 */     synchronized (this.session) {
/* 334 */       int i = this.session.getNextId();
/* 335 */       for (byte b1 = 0, b2 = 0; b1 < this.transferList.size(); b1++) {
/* 336 */         Transfer transfer = this.transferList.get(b1);
/*     */         try {
/* 338 */           this.session.traceOperation("GET_META", i);
/* 339 */           int j = paramVarArgs.length;
/* 340 */           transfer.writeInt(19).writeInt(paramInt).writeInt(j); int k;
/* 341 */           for (k = 0; k < j; k++) {
/* 342 */             transfer.writeValue(paramVarArgs[k]);
/*     */           }
/* 344 */           this.session.done(transfer);
/* 345 */           k = transfer.readInt();
/* 346 */           return (ResultInterface)new ResultRemote(this.session, transfer, i, k, 2147483647);
/* 347 */         } catch (IOException iOException) {
/* 348 */           this.session.removeServer(iOException, b1--, ++b2);
/*     */         } 
/*     */       } 
/* 351 */       return null;
/*     */     } 
/*     */   }
/*     */   
/*     */   private Value getIntArray(int[] paramArrayOfint) {
/* 356 */     if (paramArrayOfint == null) {
/* 357 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/* 359 */     int i = paramArrayOfint.length;
/* 360 */     Value[] arrayOfValue = new Value[i];
/* 361 */     for (byte b = 0; b < i; b++) {
/* 362 */       arrayOfValue[b] = (Value)ValueInteger.get(paramArrayOfint[b]);
/*     */     }
/* 364 */     return (Value)ValueArray.get(TypeInfo.TYPE_INTEGER, arrayOfValue, (CastDataProvider)this.session);
/*     */   }
/*     */   
/*     */   private Value getStringArray(String[] paramArrayOfString) {
/* 368 */     if (paramArrayOfString == null) {
/* 369 */       return (Value)ValueNull.INSTANCE;
/*     */     }
/* 371 */     int i = paramArrayOfString.length;
/* 372 */     Value[] arrayOfValue = new Value[i];
/* 373 */     for (byte b = 0; b < i; b++) {
/* 374 */       arrayOfValue[b] = getString(paramArrayOfString[b]);
/*     */     }
/* 376 */     return (Value)ValueArray.get(TypeInfo.TYPE_VARCHAR, arrayOfValue, (CastDataProvider)this.session);
/*     */   }
/*     */   
/*     */   private Value getString(String paramString) {
/* 380 */     return (paramString != null) ? ValueVarchar.get(paramString, (CastDataProvider)this.session) : (Value)ValueNull.INSTANCE;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\jdbc\meta\DatabaseMetaRemote.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */