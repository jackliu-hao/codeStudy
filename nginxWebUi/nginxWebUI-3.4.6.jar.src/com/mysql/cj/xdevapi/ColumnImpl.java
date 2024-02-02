/*     */ package com.mysql.cj.xdevapi;
/*     */ 
/*     */ import com.mysql.cj.CharsetMapping;
/*     */ import com.mysql.cj.MysqlType;
/*     */ import com.mysql.cj.result.Field;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ColumnImpl
/*     */   implements Column
/*     */ {
/*     */   private Field field;
/*     */   
/*     */   public ColumnImpl(Field f) {
/*  46 */     this.field = f;
/*     */   }
/*     */   
/*     */   public String getSchemaName() {
/*  50 */     return this.field.getDatabaseName();
/*     */   }
/*     */   
/*     */   public String getTableName() {
/*  54 */     return this.field.getOriginalTableName();
/*     */   }
/*     */   
/*     */   public String getTableLabel() {
/*  58 */     return this.field.getTableName();
/*     */   }
/*     */   
/*     */   public String getColumnName() {
/*  62 */     return this.field.getOriginalName();
/*     */   }
/*     */   
/*     */   public String getColumnLabel() {
/*  66 */     return this.field.getName();
/*     */   }
/*     */   public Type getType() {
/*     */     int len;
/*  70 */     switch (this.field.getMysqlType()) {
/*     */       case BIT:
/*  72 */         return Type.BIT;
/*     */       case BIGINT:
/*  74 */         len = (int)this.field.getLength();
/*  75 */         if (len < 5)
/*  76 */           return Type.TINYINT; 
/*  77 */         if (len < 7)
/*  78 */           return Type.SMALLINT; 
/*  79 */         if (len < 10)
/*  80 */           return Type.MEDIUMINT; 
/*  81 */         if (len < 12)
/*  82 */           return Type.INT; 
/*  83 */         if (len < 21) {
/*  84 */           return Type.BIGINT;
/*     */         }
/*  86 */         throw new IllegalArgumentException("Unknown field length `" + this.field.getLength() + "` for signed int");
/*     */       case BIGINT_UNSIGNED:
/*  88 */         len = (int)this.field.getLength();
/*  89 */         if (len < 4)
/*  90 */           return Type.TINYINT; 
/*  91 */         if (len < 6)
/*  92 */           return Type.SMALLINT; 
/*  93 */         if (len < 9)
/*  94 */           return Type.MEDIUMINT; 
/*  95 */         if (len < 11)
/*  96 */           return Type.INT; 
/*  97 */         if (len < 21) {
/*  98 */           return Type.BIGINT;
/*     */         }
/* 100 */         throw new IllegalArgumentException("Unknown field length `" + this.field.getLength() + "` for unsigned int");
/*     */       case FLOAT:
/*     */       case FLOAT_UNSIGNED:
/* 103 */         return Type.FLOAT;
/*     */       case DECIMAL:
/*     */       case DECIMAL_UNSIGNED:
/* 106 */         return Type.DECIMAL;
/*     */       case DOUBLE:
/*     */       case DOUBLE_UNSIGNED:
/* 109 */         return Type.DOUBLE;
/*     */       case CHAR:
/*     */       case VARCHAR:
/* 112 */         return Type.STRING;
/*     */       case JSON:
/* 114 */         return Type.JSON;
/*     */       case VARBINARY:
/* 116 */         return Type.BYTES;
/*     */       case TIME:
/* 118 */         return Type.TIME;
/*     */       case DATETIME:
/* 120 */         len = (int)this.field.getLength();
/* 121 */         if (len == 10)
/* 122 */           return Type.DATE; 
/* 123 */         if (len > 18 && len < 27) {
/* 124 */           return Type.DATETIME;
/*     */         }
/* 126 */         throw new IllegalArgumentException("Unknown field length `" + this.field.getLength() + "` for datetime");
/*     */       case TIMESTAMP:
/* 128 */         return Type.TIMESTAMP;
/*     */       case SET:
/* 130 */         return Type.SET;
/*     */       case ENUM:
/* 132 */         return Type.ENUM;
/*     */       case GEOMETRY:
/* 134 */         return Type.GEOMETRY;
/*     */     } 
/*     */ 
/*     */     
/* 138 */     throw new IllegalArgumentException("Unknown type in metadata: " + this.field.getMysqlType());
/*     */   }
/*     */   
/*     */   public long getLength() {
/* 142 */     return this.field.getLength();
/*     */   }
/*     */   
/*     */   public int getFractionalDigits() {
/* 146 */     return this.field.getDecimals();
/*     */   }
/*     */   
/*     */   public boolean isNumberSigned() {
/* 150 */     return MysqlType.isSigned(this.field.getMysqlType());
/*     */   }
/*     */   
/*     */   public String getCollationName() {
/* 154 */     return CharsetMapping.getStaticCollationNameForCollationIndex(Integer.valueOf(this.field.getCollationIndex()));
/*     */   }
/*     */   
/*     */   public String getCharacterSetName() {
/* 158 */     return CharsetMapping.getStaticMysqlCharsetNameForCollationIndex(Integer.valueOf(this.field.getCollationIndex()));
/*     */   }
/*     */   
/*     */   public boolean isPadded() {
/* 162 */     return (this.field.isZeroFill() || this.field.getMysqlType() == MysqlType.CHAR);
/*     */   }
/*     */   
/*     */   public boolean isNullable() {
/* 166 */     return !this.field.isNotNull();
/*     */   }
/*     */   
/*     */   public boolean isAutoIncrement() {
/* 170 */     return this.field.isAutoIncrement();
/*     */   }
/*     */   
/*     */   public boolean isPrimaryKey() {
/* 174 */     return this.field.isPrimaryKey();
/*     */   }
/*     */   
/*     */   public boolean isUniqueKey() {
/* 178 */     return this.field.isUniqueKey();
/*     */   }
/*     */   
/*     */   public boolean isPartKey() {
/* 182 */     return this.field.isMultipleKey();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\ColumnImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */