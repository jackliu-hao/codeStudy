/*     */ package org.h2.engine;
/*     */ 
/*     */ import java.sql.SQLException;
/*     */ import java.util.Comparator;
/*     */ import org.h2.api.DatabaseEventListener;
/*     */ import org.h2.command.Prepared;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.result.SearchRow;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueInteger;
/*     */ import org.h2.value.ValueVarchar;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class MetaRecord
/*     */   implements Comparable<MetaRecord>
/*     */ {
/*     */   static final Comparator<Prepared> CONSTRAINTS_COMPARATOR;
/*     */   private final int id;
/*     */   private final int objectType;
/*     */   private final String sql;
/*     */   
/*     */   static {
/*  29 */     CONSTRAINTS_COMPARATOR = ((paramPrepared1, paramPrepared2) -> {
/*     */         int i = paramPrepared1.getType(); int j = paramPrepared2.getType();
/*  31 */         boolean bool1 = (i == 6 || i == 4) ? true : false;
/*     */         
/*  33 */         boolean bool2 = (j == 6 || j == 4) ? true : false;
/*     */         return (bool1 == bool2) ? (paramPrepared1.getPersistedObjectId() - paramPrepared2.getPersistedObjectId()) : (bool1 ? -1 : 1);
/*     */       });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void populateRowFromDBObject(DbObject paramDbObject, SearchRow paramSearchRow) {
/*  54 */     paramSearchRow.setValue(0, (Value)ValueInteger.get(paramDbObject.getId()));
/*  55 */     paramSearchRow.setValue(1, (Value)ValueInteger.get(0));
/*  56 */     paramSearchRow.setValue(2, (Value)ValueInteger.get(paramDbObject.getType()));
/*  57 */     paramSearchRow.setValue(3, ValueVarchar.get(paramDbObject.getCreateSQLForMeta()));
/*     */   }
/*     */   
/*     */   public MetaRecord(SearchRow paramSearchRow) {
/*  61 */     this.id = paramSearchRow.getValue(0).getInt();
/*  62 */     this.objectType = paramSearchRow.getValue(2).getInt();
/*  63 */     this.sql = paramSearchRow.getValue(3).getString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void prepareAndExecute(Database paramDatabase, SessionLocal paramSessionLocal, DatabaseEventListener paramDatabaseEventListener) {
/*     */     try {
/*  75 */       Prepared prepared = paramSessionLocal.prepare(this.sql);
/*  76 */       prepared.setPersistedObjectId(this.id);
/*  77 */       prepared.update();
/*  78 */     } catch (DbException dbException) {
/*  79 */       throwException(paramDatabase, paramDatabaseEventListener, dbException, this.sql);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   Prepared prepare(Database paramDatabase, SessionLocal paramSessionLocal, DatabaseEventListener paramDatabaseEventListener) {
/*     */     try {
/*  93 */       Prepared prepared = paramSessionLocal.prepare(this.sql);
/*  94 */       prepared.setPersistedObjectId(this.id);
/*  95 */       return prepared;
/*  96 */     } catch (DbException dbException) {
/*  97 */       throwException(paramDatabase, paramDatabaseEventListener, dbException, this.sql);
/*  98 */       return null;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   static void execute(Database paramDatabase, Prepared paramPrepared, DatabaseEventListener paramDatabaseEventListener, String paramString) {
/*     */     try {
/* 112 */       paramPrepared.update();
/* 113 */     } catch (DbException dbException) {
/* 114 */       throwException(paramDatabase, paramDatabaseEventListener, dbException, paramString);
/*     */     } 
/*     */   }
/*     */   
/*     */   private static void throwException(Database paramDatabase, DatabaseEventListener paramDatabaseEventListener, DbException paramDbException, String paramString) {
/* 119 */     paramDbException = paramDbException.addSQL(paramString);
/* 120 */     SQLException sQLException = paramDbException.getSQLException();
/* 121 */     paramDatabase.getTrace(2).error(sQLException, paramString);
/* 122 */     if (paramDatabaseEventListener != null) {
/* 123 */       paramDatabaseEventListener.exceptionThrown(sQLException, paramString);
/*     */     } else {
/*     */       
/* 126 */       throw paramDbException;
/*     */     } 
/*     */   }
/*     */   
/*     */   public int getId() {
/* 131 */     return this.id;
/*     */   }
/*     */   
/*     */   public int getObjectType() {
/* 135 */     return this.objectType;
/*     */   }
/*     */   
/*     */   public String getSQL() {
/* 139 */     return this.sql;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public int compareTo(MetaRecord paramMetaRecord) {
/* 150 */     int i = getCreateOrder();
/* 151 */     int j = paramMetaRecord.getCreateOrder();
/* 152 */     if (i != j) {
/* 153 */       return i - j;
/*     */     }
/* 155 */     return getId() - paramMetaRecord.getId();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private int getCreateOrder() {
/* 165 */     switch (this.objectType) {
/*     */       case 6:
/* 167 */         return 0;
/*     */       case 2:
/* 169 */         return 1;
/*     */       case 10:
/* 171 */         return 2;
/*     */       case 9:
/* 173 */         return 3;
/*     */       case 12:
/* 175 */         return 4;
/*     */       case 3:
/* 177 */         return 5;
/*     */       case 11:
/* 179 */         return 6;
/*     */       case 0:
/* 181 */         return 7;
/*     */       case 1:
/* 183 */         return 8;
/*     */       case 5:
/* 185 */         return 9;
/*     */       case 4:
/* 187 */         return 10;
/*     */       case 15:
/* 189 */         return 11;
/*     */       case 7:
/* 191 */         return 12;
/*     */       case 8:
/* 193 */         return 13;
/*     */       case 14:
/* 195 */         return 14;
/*     */       case 13:
/* 197 */         return 15;
/*     */     } 
/* 199 */     throw DbException.getInternalError("type=" + this.objectType);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 205 */     return "MetaRecord [id=" + this.id + ", objectType=" + this.objectType + ", sql=" + this.sql + ']';
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\engine\MetaRecord.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */