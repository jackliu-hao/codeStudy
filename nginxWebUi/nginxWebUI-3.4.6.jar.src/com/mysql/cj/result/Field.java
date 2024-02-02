/*     */ package com.mysql.cj.result;
/*     */ 
/*     */ import com.mysql.cj.MysqlType;
/*     */ import com.mysql.cj.protocol.ProtocolEntity;
/*     */ import com.mysql.cj.util.LazyString;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Field
/*     */   implements ProtocolEntity
/*     */ {
/*  41 */   private int collationIndex = 0;
/*     */   
/*  43 */   private String encoding = "US-ASCII";
/*     */   
/*     */   private int colDecimals;
/*     */   
/*     */   private short colFlag;
/*     */   
/*  49 */   private LazyString databaseName = null;
/*  50 */   private LazyString tableName = null;
/*  51 */   private LazyString originalTableName = null;
/*  52 */   private LazyString columnName = null;
/*  53 */   private LazyString originalColumnName = null;
/*     */   
/*  55 */   private String fullName = null;
/*     */   
/*     */   private long length;
/*     */   
/*  59 */   private int mysqlTypeId = -1;
/*     */   
/*  61 */   private MysqlType mysqlType = MysqlType.UNKNOWN;
/*     */ 
/*     */   
/*     */   public Field(LazyString databaseName, LazyString tableName, LazyString originalTableName, LazyString columnName, LazyString originalColumnName, long length, int mysqlTypeId, short colFlag, int colDecimals, int collationIndex, String encoding, MysqlType mysqlType) {
/*  65 */     this.databaseName = databaseName;
/*  66 */     this.tableName = tableName;
/*  67 */     this.originalTableName = originalTableName;
/*  68 */     this.columnName = columnName;
/*  69 */     this.originalColumnName = originalColumnName;
/*  70 */     this.length = length;
/*  71 */     this.colFlag = colFlag;
/*  72 */     this.colDecimals = colDecimals;
/*  73 */     this.mysqlTypeId = mysqlTypeId;
/*  74 */     this.collationIndex = collationIndex;
/*     */ 
/*     */     
/*  77 */     this.encoding = "UnicodeBig".equals(encoding) ? "UTF-16" : encoding;
/*     */ 
/*     */     
/*  80 */     if (mysqlType == MysqlType.JSON) {
/*  81 */       this.encoding = "UTF-8";
/*     */     }
/*     */     
/*  84 */     this.mysqlType = mysqlType;
/*     */     
/*  86 */     adjustFlagsByMysqlType();
/*     */   }
/*     */ 
/*     */   
/*     */   private void adjustFlagsByMysqlType() {
/*  91 */     switch (this.mysqlType) {
/*     */       case BIT:
/*  93 */         if (this.length > 1L) {
/*  94 */           this.colFlag = (short)(this.colFlag | 0x80);
/*  95 */           this.colFlag = (short)(this.colFlag | 0x10);
/*     */         } 
/*     */         break;
/*     */       
/*     */       case BINARY:
/*     */       case VARBINARY:
/* 101 */         this.colFlag = (short)(this.colFlag | 0x80);
/* 102 */         this.colFlag = (short)(this.colFlag | 0x10);
/*     */         break;
/*     */       
/*     */       case DECIMAL_UNSIGNED:
/*     */       case TINYINT_UNSIGNED:
/*     */       case SMALLINT_UNSIGNED:
/*     */       case INT_UNSIGNED:
/*     */       case FLOAT_UNSIGNED:
/*     */       case DOUBLE_UNSIGNED:
/*     */       case BIGINT_UNSIGNED:
/*     */       case MEDIUMINT_UNSIGNED:
/* 113 */         this.colFlag = (short)(this.colFlag | 0x20);
/*     */         break;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Field(String tableName, String columnName, int collationIndex, String encoding, MysqlType mysqlType, int length) {
/* 141 */     this.databaseName = new LazyString(null);
/* 142 */     this.tableName = new LazyString(tableName);
/* 143 */     this.originalTableName = new LazyString(null);
/* 144 */     this.columnName = new LazyString(columnName);
/* 145 */     this.originalColumnName = new LazyString(null);
/* 146 */     this.length = length;
/* 147 */     this.mysqlType = mysqlType;
/* 148 */     this.colFlag = 0;
/* 149 */     this.colDecimals = 0;
/*     */     
/* 151 */     adjustFlagsByMysqlType();
/*     */     
/* 153 */     switch (mysqlType) {
/*     */ 
/*     */       
/*     */       case CHAR:
/*     */       case VARCHAR:
/*     */       case TINYTEXT:
/*     */       case TEXT:
/*     */       case MEDIUMTEXT:
/*     */       case LONGTEXT:
/*     */       case JSON:
/* 163 */         this.collationIndex = collationIndex;
/*     */ 
/*     */         
/* 166 */         this.encoding = "UnicodeBig".equals(encoding) ? "UTF-16" : encoding;
/*     */         break;
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
/*     */   public String getEncoding() {
/* 180 */     return this.encoding;
/*     */   }
/*     */   
/*     */   public String getColumnLabel() {
/* 184 */     return getName();
/*     */   }
/*     */   
/*     */   public String getDatabaseName() {
/* 188 */     return this.databaseName.toString();
/*     */   }
/*     */   
/*     */   public int getDecimals() {
/* 192 */     return this.colDecimals;
/*     */   }
/*     */   
/*     */   public String getFullName() {
/* 196 */     if (this.fullName == null) {
/* 197 */       StringBuilder fullNameBuf = new StringBuilder(this.tableName.length() + 1 + this.columnName.length());
/* 198 */       fullNameBuf.append(this.tableName.toString());
/* 199 */       fullNameBuf.append('.');
/* 200 */       fullNameBuf.append(this.columnName.toString());
/* 201 */       this.fullName = fullNameBuf.toString();
/*     */     } 
/*     */     
/* 204 */     return this.fullName;
/*     */   }
/*     */   
/*     */   public long getLength() {
/* 208 */     return this.length;
/*     */   }
/*     */   
/*     */   public int getMysqlTypeId() {
/* 212 */     return this.mysqlTypeId;
/*     */   }
/*     */   
/*     */   public void setMysqlTypeId(int id) {
/* 216 */     this.mysqlTypeId = id;
/*     */   }
/*     */   
/*     */   public String getName() {
/* 220 */     return this.columnName.toString();
/*     */   }
/*     */   
/*     */   public String getOriginalName() {
/* 224 */     return this.originalColumnName.toString();
/*     */   }
/*     */   
/*     */   public String getOriginalTableName() {
/* 228 */     return this.originalTableName.toString();
/*     */   }
/*     */   
/*     */   public int getJavaType() {
/* 232 */     return this.mysqlType.getJdbcType();
/*     */   }
/*     */   
/*     */   public String getTableName() {
/* 236 */     return this.tableName.toString();
/*     */   }
/*     */   
/*     */   public boolean isAutoIncrement() {
/* 240 */     return ((this.colFlag & 0x200) > 0);
/*     */   }
/*     */   
/*     */   public boolean isBinary() {
/* 244 */     return ((this.colFlag & 0x80) > 0);
/*     */   }
/*     */   
/*     */   public void setBinary() {
/* 248 */     this.colFlag = (short)(this.colFlag | 0x80);
/*     */   }
/*     */   
/*     */   public boolean isBlob() {
/* 252 */     return ((this.colFlag & 0x10) > 0);
/*     */   }
/*     */   
/*     */   public void setBlob() {
/* 256 */     this.colFlag = (short)(this.colFlag | 0x10);
/*     */   }
/*     */   
/*     */   public boolean isMultipleKey() {
/* 260 */     return ((this.colFlag & 0x8) > 0);
/*     */   }
/*     */   
/*     */   public boolean isNotNull() {
/* 264 */     return ((this.colFlag & 0x1) > 0);
/*     */   }
/*     */   
/*     */   public boolean isPrimaryKey() {
/* 268 */     return ((this.colFlag & 0x2) > 0);
/*     */   }
/*     */   
/*     */   public boolean isFromFunction() {
/* 272 */     return (this.originalTableName.length() == 0);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isReadOnly() {
/* 282 */     return (this.originalColumnName.length() == 0 && this.originalTableName.length() == 0);
/*     */   }
/*     */   
/*     */   public boolean isUniqueKey() {
/* 286 */     return ((this.colFlag & 0x4) > 0);
/*     */   }
/*     */   
/*     */   public boolean isUnsigned() {
/* 290 */     return ((this.colFlag & 0x20) > 0);
/*     */   }
/*     */   
/*     */   public boolean isZeroFill() {
/* 294 */     return ((this.colFlag & 0x40) > 0);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/*     */     try {
/* 300 */       StringBuilder asString = new StringBuilder();
/* 301 */       asString.append(super.toString());
/* 302 */       asString.append("[");
/* 303 */       asString.append("dbName=");
/* 304 */       asString.append(getDatabaseName());
/* 305 */       asString.append(",tableName=");
/* 306 */       asString.append(getTableName());
/* 307 */       asString.append(",originalTableName=");
/* 308 */       asString.append(getOriginalTableName());
/* 309 */       asString.append(",columnName=");
/* 310 */       asString.append(getName());
/* 311 */       asString.append(",originalColumnName=");
/* 312 */       asString.append(getOriginalName());
/* 313 */       asString.append(",mysqlType=");
/* 314 */       asString.append(getMysqlTypeId());
/* 315 */       asString.append("(");
/* 316 */       MysqlType ft = getMysqlType();
/* 317 */       if (ft.equals(MysqlType.UNKNOWN)) {
/* 318 */         asString.append(" Unknown MySQL Type # ");
/* 319 */         asString.append(getMysqlTypeId());
/*     */       } else {
/* 321 */         asString.append("FIELD_TYPE_");
/* 322 */         asString.append(ft.getName());
/*     */       } 
/* 324 */       asString.append(")");
/* 325 */       asString.append(",sqlType=");
/* 326 */       asString.append(ft.getJdbcType());
/* 327 */       asString.append(",flags=");
/*     */       
/* 329 */       if (isAutoIncrement()) {
/* 330 */         asString.append(" AUTO_INCREMENT");
/*     */       }
/*     */       
/* 333 */       if (isPrimaryKey()) {
/* 334 */         asString.append(" PRIMARY_KEY");
/*     */       }
/*     */       
/* 337 */       if (isUniqueKey()) {
/* 338 */         asString.append(" UNIQUE_KEY");
/*     */       }
/*     */       
/* 341 */       if (isBinary()) {
/* 342 */         asString.append(" BINARY");
/*     */       }
/*     */       
/* 345 */       if (isBlob()) {
/* 346 */         asString.append(" BLOB");
/*     */       }
/*     */       
/* 349 */       if (isMultipleKey()) {
/* 350 */         asString.append(" MULTI_KEY");
/*     */       }
/*     */       
/* 353 */       if (isUnsigned()) {
/* 354 */         asString.append(" UNSIGNED");
/*     */       }
/*     */       
/* 357 */       if (isZeroFill()) {
/* 358 */         asString.append(" ZEROFILL");
/*     */       }
/*     */       
/* 361 */       asString.append(", charsetIndex=");
/* 362 */       asString.append(this.collationIndex);
/* 363 */       asString.append(", charsetName=");
/* 364 */       asString.append(this.encoding);
/*     */       
/* 366 */       asString.append("]");
/*     */       
/* 368 */       return asString.toString();
/* 369 */     } catch (Throwable t) {
/* 370 */       return super.toString() + "[<unable to generate contents>]";
/*     */     } 
/*     */   }
/*     */   
/*     */   public boolean isSingleBit() {
/* 375 */     return (this.length <= 1L);
/*     */   }
/*     */   
/*     */   public boolean getValueNeedsQuoting() {
/* 379 */     switch (this.mysqlType) {
/*     */       case BIT:
/*     */       case DECIMAL_UNSIGNED:
/*     */       case TINYINT_UNSIGNED:
/*     */       case SMALLINT_UNSIGNED:
/*     */       case INT_UNSIGNED:
/*     */       case FLOAT_UNSIGNED:
/*     */       case DOUBLE_UNSIGNED:
/*     */       case BIGINT_UNSIGNED:
/*     */       case MEDIUMINT_UNSIGNED:
/*     */       case BIGINT:
/*     */       case DECIMAL:
/*     */       case DOUBLE:
/*     */       case INT:
/*     */       case MEDIUMINT:
/*     */       case FLOAT:
/*     */       case SMALLINT:
/*     */       case TINYINT:
/* 397 */         return false;
/*     */     } 
/* 399 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCollationIndex() {
/* 404 */     return this.collationIndex;
/*     */   }
/*     */   
/*     */   public MysqlType getMysqlType() {
/* 408 */     return this.mysqlType;
/*     */   }
/*     */   
/*     */   public void setMysqlType(MysqlType mysqlType) {
/* 412 */     this.mysqlType = mysqlType;
/*     */   }
/*     */   
/*     */   public short getFlags() {
/* 416 */     return this.colFlag;
/*     */   }
/*     */   
/*     */   public void setFlags(short colFlag) {
/* 420 */     this.colFlag = colFlag;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\result\Field.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */