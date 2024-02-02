/*     */ package org.h2.mode;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashSet;
/*     */ import org.h2.constraint.Constraint;
/*     */ import org.h2.engine.RightOwner;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.engine.User;
/*     */ import org.h2.index.Index;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.Row;
/*     */ import org.h2.result.SearchRow;
/*     */ import org.h2.schema.Schema;
/*     */ import org.h2.server.pg.PgServer;
/*     */ import org.h2.table.Column;
/*     */ import org.h2.table.MetaTable;
/*     */ import org.h2.table.Table;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.util.Utils;
/*     */ import org.h2.value.DataType;
/*     */ import org.h2.value.ExtTypeInfo;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueArray;
/*     */ import org.h2.value.ValueBoolean;
/*     */ import org.h2.value.ValueDouble;
/*     */ import org.h2.value.ValueInteger;
/*     */ import org.h2.value.ValueSmallint;
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
/*     */ public final class PgCatalogTable
/*     */   extends MetaTable
/*     */ {
/*     */   private static final int PG_AM = 0;
/*     */   private static final int PG_ATTRDEF = 1;
/*     */   private static final int PG_ATTRIBUTE = 2;
/*     */   private static final int PG_AUTHID = 3;
/*     */   private static final int PG_CLASS = 4;
/*     */   private static final int PG_CONSTRAINT = 5;
/*     */   private static final int PG_DATABASE = 6;
/*     */   private static final int PG_DESCRIPTION = 7;
/*     */   private static final int PG_GROUP = 8;
/*     */   private static final int PG_INDEX = 9;
/*     */   private static final int PG_INHERITS = 10;
/*     */   private static final int PG_NAMESPACE = 11;
/*     */   private static final int PG_PROC = 12;
/*     */   private static final int PG_ROLES = 13;
/*     */   private static final int PG_SETTINGS = 14;
/*     */   private static final int PG_TABLESPACE = 15;
/*     */   private static final int PG_TRIGGER = 16;
/*     */   private static final int PG_TYPE = 17;
/*     */   private static final int PG_USER = 18;
/*     */   public static final int META_TABLE_TYPE_COUNT = 19;
/*  87 */   private static final Object[][] PG_EXTRA_TYPES = new Object[][] {
/*  88 */       { Integer.valueOf(18), "char", Integer.valueOf(1), Integer.valueOf(0)
/*  89 */       }, { Integer.valueOf(19), "name", Integer.valueOf(64), Integer.valueOf(18)
/*  90 */       }, { Integer.valueOf(22), "int2vector", Integer.valueOf(-1), Integer.valueOf(21)
/*  91 */       }, { Integer.valueOf(24), "regproc", Integer.valueOf(4), Integer.valueOf(0)
/*  92 */       }, { Integer.valueOf(1005), "_int2", Integer.valueOf(-1), Integer.valueOf(21)
/*  93 */       }, { Integer.valueOf(1007), "_int4", Integer.valueOf(-1), Integer.valueOf(23)
/*  94 */       }, { Integer.valueOf(1015), "_varchar", Integer.valueOf(-1), Integer.valueOf(1043)
/*  95 */       }, { Integer.valueOf(2205), "regclass", Integer.valueOf(4), Integer.valueOf(0) }
/*     */     };
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
/*     */   public PgCatalogTable(Schema paramSchema, int paramInt1, int paramInt2) {
/* 109 */     super(paramSchema, paramInt1, paramInt2);
/*     */     Column[] arrayOfColumn;
/* 111 */     switch (paramInt2) {
/*     */       case 0:
/* 113 */         setMetaTableName("PG_AM");
/*     */ 
/*     */         
/* 116 */         arrayOfColumn = new Column[] { column("OID", TypeInfo.TYPE_INTEGER), column("AMNAME", TypeInfo.TYPE_VARCHAR) };
/*     */         break;
/*     */       
/*     */       case 1:
/* 120 */         setMetaTableName("PG_ATTRDEF");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 126 */         arrayOfColumn = new Column[] { column("OID", TypeInfo.TYPE_INTEGER), column("ADSRC", TypeInfo.TYPE_INTEGER), column("ADRELID", TypeInfo.TYPE_INTEGER), column("ADNUM", TypeInfo.TYPE_INTEGER), column("ADBIN", TypeInfo.TYPE_VARCHAR) };
/*     */         break;
/*     */       
/*     */       case 2:
/* 130 */         setMetaTableName("PG_ATTRIBUTE");
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
/* 141 */         arrayOfColumn = new Column[] { column("OID", TypeInfo.TYPE_INTEGER), column("ATTRELID", TypeInfo.TYPE_INTEGER), column("ATTNAME", TypeInfo.TYPE_VARCHAR), column("ATTTYPID", TypeInfo.TYPE_INTEGER), column("ATTLEN", TypeInfo.TYPE_INTEGER), column("ATTNUM", TypeInfo.TYPE_INTEGER), column("ATTTYPMOD", TypeInfo.TYPE_INTEGER), column("ATTNOTNULL", TypeInfo.TYPE_BOOLEAN), column("ATTISDROPPED", TypeInfo.TYPE_BOOLEAN), column("ATTHASDEF", TypeInfo.TYPE_BOOLEAN) };
/*     */         break;
/*     */       
/*     */       case 3:
/* 145 */         setMetaTableName("PG_AUTHID");
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
/* 158 */         arrayOfColumn = new Column[] { column("OID", TypeInfo.TYPE_INTEGER), column("ROLNAME", TypeInfo.TYPE_VARCHAR), column("ROLSUPER", TypeInfo.TYPE_BOOLEAN), column("ROLINHERIT", TypeInfo.TYPE_BOOLEAN), column("ROLCREATEROLE", TypeInfo.TYPE_BOOLEAN), column("ROLCREATEDB", TypeInfo.TYPE_BOOLEAN), column("ROLCATUPDATE", TypeInfo.TYPE_BOOLEAN), column("ROLCANLOGIN", TypeInfo.TYPE_BOOLEAN), column("ROLCONNLIMIT", TypeInfo.TYPE_BOOLEAN), column("ROLPASSWORD", TypeInfo.TYPE_BOOLEAN), column("ROLVALIDUNTIL", TypeInfo.TYPE_TIMESTAMP_TZ), column("ROLCONFIG", TypeInfo.getTypeInfo(40, -1L, 0, (ExtTypeInfo)TypeInfo.TYPE_VARCHAR)) };
/*     */         break;
/*     */       
/*     */       case 4:
/* 162 */         setMetaTableName("PG_CLASS");
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
/* 176 */         arrayOfColumn = new Column[] { column("OID", TypeInfo.TYPE_INTEGER), column("RELNAME", TypeInfo.TYPE_VARCHAR), column("RELNAMESPACE", TypeInfo.TYPE_INTEGER), column("RELKIND", TypeInfo.TYPE_CHAR), column("RELAM", TypeInfo.TYPE_INTEGER), column("RELTUPLES", TypeInfo.TYPE_DOUBLE), column("RELTABLESPACE", TypeInfo.TYPE_INTEGER), column("RELPAGES", TypeInfo.TYPE_INTEGER), column("RELHASINDEX", TypeInfo.TYPE_BOOLEAN), column("RELHASRULES", TypeInfo.TYPE_BOOLEAN), column("RELHASOIDS", TypeInfo.TYPE_BOOLEAN), column("RELCHECKS", TypeInfo.TYPE_SMALLINT), column("RELTRIGGERS", TypeInfo.TYPE_INTEGER) };
/*     */         break;
/*     */       
/*     */       case 5:
/* 180 */         setMetaTableName("PG_CONSTRAINT");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 187 */         arrayOfColumn = new Column[] { column("OID", TypeInfo.TYPE_INTEGER), column("CONNAME", TypeInfo.TYPE_VARCHAR), column("CONTYPE", TypeInfo.TYPE_VARCHAR), column("CONRELID", TypeInfo.TYPE_INTEGER), column("CONFRELID", TypeInfo.TYPE_INTEGER), column("CONKEY", TypeInfo.getTypeInfo(40, -1L, 0, (ExtTypeInfo)TypeInfo.TYPE_SMALLINT)) };
/*     */         break;
/*     */       
/*     */       case 6:
/* 191 */         setMetaTableName("PG_DATABASE");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 201 */         arrayOfColumn = new Column[] { column("OID", TypeInfo.TYPE_INTEGER), column("DATNAME", TypeInfo.TYPE_VARCHAR), column("ENCODING", TypeInfo.TYPE_INTEGER), column("DATLASTSYSOID", TypeInfo.TYPE_INTEGER), column("DATALLOWCONN", TypeInfo.TYPE_BOOLEAN), column("DATCONFIG", TypeInfo.getTypeInfo(40, -1L, 0, (ExtTypeInfo)TypeInfo.TYPE_VARCHAR)), column("DATACL", TypeInfo.getTypeInfo(40, -1L, 0, (ExtTypeInfo)TypeInfo.TYPE_VARCHAR)), column("DATDBA", TypeInfo.TYPE_INTEGER), column("DATTABLESPACE", TypeInfo.TYPE_INTEGER) };
/*     */         break;
/*     */       
/*     */       case 7:
/* 205 */         setMetaTableName("PG_DESCRIPTION");
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 210 */         arrayOfColumn = new Column[] { column("OBJOID", TypeInfo.TYPE_INTEGER), column("OBJSUBID", TypeInfo.TYPE_INTEGER), column("CLASSOID", TypeInfo.TYPE_INTEGER), column("DESCRIPTION", TypeInfo.TYPE_VARCHAR) };
/*     */         break;
/*     */       
/*     */       case 8:
/* 214 */         setMetaTableName("PG_GROUP");
/*     */ 
/*     */         
/* 217 */         arrayOfColumn = new Column[] { column("OID", TypeInfo.TYPE_INTEGER), column("GRONAME", TypeInfo.TYPE_VARCHAR) };
/*     */         break;
/*     */       
/*     */       case 9:
/* 221 */         setMetaTableName("PG_INDEX");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 231 */         arrayOfColumn = new Column[] { column("OID", TypeInfo.TYPE_INTEGER), column("INDEXRELID", TypeInfo.TYPE_INTEGER), column("INDRELID", TypeInfo.TYPE_INTEGER), column("INDISCLUSTERED", TypeInfo.TYPE_BOOLEAN), column("INDISUNIQUE", TypeInfo.TYPE_BOOLEAN), column("INDISPRIMARY", TypeInfo.TYPE_BOOLEAN), column("INDEXPRS", TypeInfo.TYPE_VARCHAR), column("INDKEY", TypeInfo.getTypeInfo(40, -1L, 0, (ExtTypeInfo)TypeInfo.TYPE_INTEGER)), column("INDPRED", TypeInfo.TYPE_VARCHAR) };
/*     */         break;
/*     */       
/*     */       case 10:
/* 235 */         setMetaTableName("PG_INHERITS");
/*     */ 
/*     */ 
/*     */         
/* 239 */         arrayOfColumn = new Column[] { column("INHRELID", TypeInfo.TYPE_INTEGER), column("INHPARENT", TypeInfo.TYPE_INTEGER), column("INHSEQNO", TypeInfo.TYPE_INTEGER) };
/*     */         break;
/*     */       
/*     */       case 11:
/* 243 */         setMetaTableName("PG_NAMESPACE");
/*     */ 
/*     */         
/* 246 */         arrayOfColumn = new Column[] { column("ID", TypeInfo.TYPE_INTEGER), column("NSPNAME", TypeInfo.TYPE_VARCHAR) };
/*     */         break;
/*     */       
/*     */       case 12:
/* 250 */         setMetaTableName("PG_PROC");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 256 */         arrayOfColumn = new Column[] { column("OID", TypeInfo.TYPE_INTEGER), column("PRONAME", TypeInfo.TYPE_VARCHAR), column("PRORETTYPE", TypeInfo.TYPE_INTEGER), column("PROARGTYPES", TypeInfo.getTypeInfo(40, -1L, 0, (ExtTypeInfo)TypeInfo.TYPE_INTEGER)), column("PRONAMESPACE", TypeInfo.TYPE_INTEGER) };
/*     */         break;
/*     */       
/*     */       case 13:
/* 260 */         setMetaTableName("PG_ROLES");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 266 */         arrayOfColumn = new Column[] { column("OID", TypeInfo.TYPE_INTEGER), column("ROLNAME", TypeInfo.TYPE_VARCHAR), column("ROLSUPER", TypeInfo.TYPE_CHAR), column("ROLCREATEROLE", TypeInfo.TYPE_CHAR), column("ROLCREATEDB", TypeInfo.TYPE_CHAR) };
/*     */         break;
/*     */       
/*     */       case 14:
/* 270 */         setMetaTableName("PG_SETTINGS");
/*     */ 
/*     */ 
/*     */         
/* 274 */         arrayOfColumn = new Column[] { column("OID", TypeInfo.TYPE_INTEGER), column("NAME", TypeInfo.TYPE_VARCHAR), column("SETTING", TypeInfo.TYPE_VARCHAR) };
/*     */         break;
/*     */       
/*     */       case 15:
/* 278 */         setMetaTableName("PG_TABLESPACE");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 284 */         arrayOfColumn = new Column[] { column("OID", TypeInfo.TYPE_INTEGER), column("SPCNAME", TypeInfo.TYPE_VARCHAR), column("SPCLOCATION", TypeInfo.TYPE_VARCHAR), column("SPCOWNER", TypeInfo.TYPE_INTEGER), column("SPCACL", TypeInfo.getTypeInfo(40, -1L, 0, (ExtTypeInfo)TypeInfo.TYPE_VARCHAR)) };
/*     */         break;
/*     */       
/*     */       case 16:
/* 288 */         setMetaTableName("PG_TRIGGER");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 298 */         arrayOfColumn = new Column[] { column("OID", TypeInfo.TYPE_INTEGER), column("TGCONSTRRELID", TypeInfo.TYPE_INTEGER), column("TGFOID", TypeInfo.TYPE_INTEGER), column("TGARGS", TypeInfo.TYPE_INTEGER), column("TGNARGS", TypeInfo.TYPE_INTEGER), column("TGDEFERRABLE", TypeInfo.TYPE_BOOLEAN), column("TGINITDEFERRED", TypeInfo.TYPE_BOOLEAN), column("TGCONSTRNAME", TypeInfo.TYPE_VARCHAR), column("TGRELID", TypeInfo.TYPE_INTEGER) };
/*     */         break;
/*     */       
/*     */       case 17:
/* 302 */         setMetaTableName("PG_TYPE");
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
/* 315 */         arrayOfColumn = new Column[] { column("OID", TypeInfo.TYPE_INTEGER), column("TYPNAME", TypeInfo.TYPE_VARCHAR), column("TYPNAMESPACE", TypeInfo.TYPE_INTEGER), column("TYPLEN", TypeInfo.TYPE_INTEGER), column("TYPTYPE", TypeInfo.TYPE_VARCHAR), column("TYPDELIM", TypeInfo.TYPE_VARCHAR), column("TYPRELID", TypeInfo.TYPE_INTEGER), column("TYPELEM", TypeInfo.TYPE_INTEGER), column("TYPBASETYPE", TypeInfo.TYPE_INTEGER), column("TYPTYPMOD", TypeInfo.TYPE_INTEGER), column("TYPNOTNULL", TypeInfo.TYPE_BOOLEAN), column("TYPINPUT", TypeInfo.TYPE_VARCHAR) };
/*     */         break;
/*     */       
/*     */       case 18:
/* 319 */         setMetaTableName("PG_USER");
/*     */ 
/*     */ 
/*     */ 
/*     */         
/* 324 */         arrayOfColumn = new Column[] { column("OID", TypeInfo.TYPE_INTEGER), column("USENAME", TypeInfo.TYPE_VARCHAR), column("USECREATEDB", TypeInfo.TYPE_BOOLEAN), column("USESUPER", TypeInfo.TYPE_BOOLEAN) };
/*     */         break;
/*     */       
/*     */       default:
/* 328 */         throw DbException.getInternalError("type=" + paramInt2);
/*     */     } 
/* 330 */     setColumns(arrayOfColumn);
/* 331 */     this.indexColumn = -1;
/* 332 */     this.metaIndex = null; } public ArrayList<Row> generateRows(SessionLocal paramSessionLocal, SearchRow paramSearchRow1, SearchRow paramSearchRow2) { String[] arrayOfString1;
/*     */     int i;
/*     */     String[][] arrayOfString;
/*     */     HashSet<Integer> hashSet;
/*     */     byte b;
/* 337 */     ArrayList<Row> arrayList = Utils.newSmallArrayList();
/* 338 */     String str = this.database.getShortName();
/* 339 */     boolean bool = paramSessionLocal.getUser().isAdmin();
/* 340 */     switch (this.type)
/*     */     { case 0:
/* 342 */         arrayOfString1 = new String[] { "btree", "hash" };
/* 343 */         for (b = 0, null = arrayOfString1.length; b < null; b++) {
/* 344 */           add(paramSessionLocal, arrayList, new Object[] {
/*     */                 
/* 346 */                 ValueInteger.get(b), arrayOfString1[b]
/*     */               });
/*     */         } 
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
/*     */       case 1:
/* 582 */         return arrayList;case 2: for (Schema schema : this.database.getAllSchemas()) { for (Table table : schema.getAllTablesAndViews(paramSessionLocal)) { if (!hideTable(table, paramSessionLocal)) pgAttribute(paramSessionLocal, arrayList, table);  }  }  for (Table table : paramSessionLocal.getLocalTempTables()) { if (!hideTable(table, paramSessionLocal)) pgAttribute(paramSessionLocal, arrayList, table);  } case 3: return arrayList;case 4: for (Schema schema : this.database.getAllSchemas()) { for (Table table : schema.getAllTablesAndViews(paramSessionLocal)) { if (!hideTable(table, paramSessionLocal)) pgClass(paramSessionLocal, arrayList, table);  }  }  for (Table table : paramSessionLocal.getLocalTempTables()) { if (!hideTable(table, paramSessionLocal)) pgClass(paramSessionLocal, arrayList, table);  } case 5: pgConstraint(paramSessionLocal, arrayList);case 6: i = Integer.MAX_VALUE; for (RightOwner rightOwner : this.database.getAllUsersAndRoles()) { if (rightOwner instanceof User && ((User)rightOwner).isAdmin()) { int j = rightOwner.getId(); if (j < i) i = j;  }  }  add(paramSessionLocal, arrayList, new Object[] { ValueInteger.get(100001), str, ValueInteger.get(6), ValueInteger.get(100000), ValueBoolean.TRUE, null, null, ValueInteger.get(i), ValueInteger.get(0) });case 7: add(paramSessionLocal, arrayList, new Object[] { ValueInteger.get(0), ValueInteger.get(0), ValueInteger.get(-1), str });case 8: case 9: case 10: return arrayList;case 11: for (Schema schema : this.database.getAllSchemas()) { add(paramSessionLocal, arrayList, new Object[] { ValueInteger.get(schema.getId()), schema.getName() }); } case 12: return arrayList;case 13: for (RightOwner rightOwner : this.database.getAllUsersAndRoles()) { if (bool || paramSessionLocal.getUser() == rightOwner) { String str1 = (rightOwner instanceof User && ((User)rightOwner).isAdmin()) ? "t" : "f"; add(paramSessionLocal, arrayList, new Object[] { ValueInteger.get(rightOwner.getId()), identifier(rightOwner.getName()), str1, str1, str1 }); }  } case 14: arrayOfString = new String[][] { { "autovacuum", "on" }, { "stats_start_collector", "on" }, { "stats_row_level", "on" } }; for (b = 0, null = arrayOfString.length; b < null; b++) { String[] arrayOfString2 = arrayOfString[b]; add(paramSessionLocal, arrayList, new Object[] { ValueInteger.get(b), arrayOfString2[0], arrayOfString2[1] }); } case 15: add(paramSessionLocal, arrayList, new Object[] { ValueInteger.get(0), "main", "?", ValueInteger.get(0), null });case 16: return arrayList;case 17: hashSet = new HashSet(); for (b = 1, null = 42; b < null; b++) { DataType dataType = DataType.getDataType(b); if (dataType.type != 40) { int j = PgServer.convertType(TypeInfo.getTypeInfo(dataType.type)); if (j != 705 && hashSet.add(Integer.valueOf(j)))
/*     */               add(paramSessionLocal, arrayList, new Object[] { 
/*     */                     ValueInteger.get(j), Value.getTypeName(dataType.type), ValueInteger.get(-1000), ValueInteger.get(-1), "b", ",", ValueInteger.get(0), ValueInteger.get(0), ValueInteger.get(0), ValueInteger.get(-1), 
/*     */                     ValueBoolean.FALSE, null });  }  }  for (Object[] arrayOfObject : PG_EXTRA_TYPES) { add(paramSessionLocal, arrayList, new Object[] { 
/*     */                 ValueInteger.get(((Integer)arrayOfObject[0]).intValue()), arrayOfObject[1], ValueInteger.get(-1000), ValueInteger.get(((Integer)arrayOfObject[2]).intValue()), "b", ",", ValueInteger.get(0), ValueInteger.get(((Integer)arrayOfObject[3]).intValue()), ValueInteger.get(0), ValueInteger.get(-1), 
/* 587 */                 ValueBoolean.FALSE, null }); } case 18: for (RightOwner rightOwner : this.database.getAllUsersAndRoles()) { if (rightOwner instanceof User) { User user = (User)rightOwner; if (bool || paramSessionLocal.getUser() == user) { ValueBoolean valueBoolean = ValueBoolean.get(user.isAdmin()); add(paramSessionLocal, arrayList, new Object[] { ValueInteger.get(user.getId()), identifier(user.getName()), valueBoolean, valueBoolean }); }  }  }  }  throw DbException.getInternalError("type=" + this.type); } private void pgAttribute(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, Table paramTable) { Column[] arrayOfColumn = paramTable.getColumns();
/* 588 */     int i = paramTable.getId();
/* 589 */     for (byte b = 0; b < arrayOfColumn.length; ) {
/* 590 */       Column column = arrayOfColumn[b++];
/* 591 */       addAttribute(paramSessionLocal, paramArrayList, i * 10000 + b, i, paramTable, column, b);
/*     */     } 
/* 593 */     for (Index index : paramTable.getIndexes()) {
/* 594 */       if (index.getCreateSQL() == null) {
/*     */         continue;
/*     */       }
/* 597 */       arrayOfColumn = index.getColumns();
/* 598 */       for (byte b1 = 0; b1 < arrayOfColumn.length; ) {
/* 599 */         Column column = arrayOfColumn[b1++];
/* 600 */         int j = index.getId();
/* 601 */         addAttribute(paramSessionLocal, paramArrayList, 1000000 * j + i * 10000 + b1, j, paramTable, column, b1);
/*     */       } 
/*     */     }  }
/*     */ 
/*     */ 
/*     */   
/*     */   private void pgClass(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, Table paramTable) {
/* 608 */     ArrayList arrayList1 = paramTable.getTriggers();
/* 609 */     addClass(paramSessionLocal, paramArrayList, paramTable.getId(), paramTable.getName(), paramTable.getSchema().getId(), 
/* 610 */         paramTable.isView() ? "v" : "r", false, (arrayList1 != null) ? arrayList1.size() : 0);
/* 611 */     ArrayList arrayList2 = paramTable.getIndexes();
/* 612 */     if (arrayList2 != null) {
/* 613 */       for (Index index : arrayList2) {
/* 614 */         if (index.getCreateSQL() == null) {
/*     */           continue;
/*     */         }
/* 617 */         addClass(paramSessionLocal, paramArrayList, index.getId(), index.getName(), index.getSchema().getId(), "i", true, 0);
/*     */       } 
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   private void pgConstraint(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList) {
/* 624 */     for (Schema schema : this.database.getAllSchemasNoMeta()) {
/* 625 */       for (Constraint constraint : schema.getAllConstraints()) {
/* 626 */         Constraint.Type type = constraint.getConstraintType();
/* 627 */         if (type == Constraint.Type.DOMAIN) {
/*     */           continue;
/*     */         }
/* 630 */         Table table1 = constraint.getTable();
/* 631 */         if (hideTable(table1, paramSessionLocal)) {
/*     */           continue;
/*     */         }
/* 634 */         ArrayList<ValueSmallint> arrayList = new ArrayList();
/* 635 */         for (Column column : constraint.getReferencedColumns(table1)) {
/* 636 */           arrayList.add(ValueSmallint.get((short)(column.getColumnId() + 1)));
/*     */         }
/* 638 */         Table table2 = constraint.getRefTable();
/* 639 */         add(paramSessionLocal, paramArrayList, new Object[] {
/*     */ 
/*     */               
/* 642 */               ValueInteger.get(constraint.getId()), constraint
/*     */               
/* 644 */               .getName(), 
/*     */               
/* 646 */               StringUtils.toLowerEnglish(type.getSqlName().substring(0, 1)), 
/*     */               
/* 648 */               ValueInteger.get(table1.getId()), 
/*     */               
/* 650 */               ValueInteger.get((table2 != null && table2 != table1 && 
/* 651 */                 !hideTable(table2, paramSessionLocal)) ? table1.getId() : 0), 
/*     */               
/* 653 */               ValueArray.get(TypeInfo.TYPE_SMALLINT, arrayList.<Value>toArray(Value.EMPTY_VALUES), null)
/*     */             });
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private void addAttribute(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, int paramInt1, int paramInt2, Table paramTable, Column paramColumn, int paramInt3) {
/* 661 */     long l = paramColumn.getType().getPrecision();
/* 662 */     add(paramSessionLocal, paramArrayList, new Object[] {
/*     */           
/* 664 */           ValueInteger.get(paramInt1), 
/*     */           
/* 666 */           ValueInteger.get(paramInt2), paramColumn
/*     */           
/* 668 */           .getName(), 
/*     */           
/* 670 */           ValueInteger.get(PgServer.convertType(paramColumn.getType())), 
/*     */           
/* 672 */           ValueInteger.get((l > 255L) ? -1 : (int)l), 
/*     */           
/* 674 */           ValueInteger.get(paramInt3), 
/*     */           
/* 676 */           ValueInteger.get(-1), 
/*     */           
/* 678 */           ValueBoolean.get(!paramColumn.isNullable()), ValueBoolean.FALSE, ValueBoolean.FALSE
/*     */         });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void addClass(SessionLocal paramSessionLocal, ArrayList<Row> paramArrayList, int paramInt1, String paramString1, int paramInt2, String paramString2, boolean paramBoolean, int paramInt3) {
/* 687 */     add(paramSessionLocal, paramArrayList, new Object[] {
/*     */           
/* 689 */           ValueInteger.get(paramInt1), paramString1, 
/*     */ 
/*     */ 
/*     */           
/* 693 */           ValueInteger.get(paramInt2), paramString2, 
/*     */ 
/*     */ 
/*     */           
/* 697 */           ValueInteger.get(0), 
/*     */           
/* 699 */           ValueDouble.get(0.0D), 
/*     */           
/* 701 */           ValueInteger.get(0), 
/*     */           
/* 703 */           ValueInteger.get(0), 
/*     */           
/* 705 */           ValueBoolean.get(paramBoolean), ValueBoolean.FALSE, ValueBoolean.FALSE, 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 711 */           ValueSmallint.get((short)0), 
/*     */           
/* 713 */           ValueInteger.get(paramInt3)
/*     */         });
/*     */   }
/*     */   
/*     */   public long getMaxDataModificationId() {
/* 718 */     return this.database.getModificationDataId();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\mode\PgCatalogTable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */