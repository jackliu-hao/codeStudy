/*     */ package com.mysql.cj.protocol.x;
/*     */ 
/*     */ import com.mysql.cj.CharsetMapping;
/*     */ import com.mysql.cj.MysqlType;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import com.mysql.cj.protocol.Message;
/*     */ import com.mysql.cj.protocol.ProtocolEntityFactory;
/*     */ import com.mysql.cj.result.Field;
/*     */ import com.mysql.cj.util.LazyString;
/*     */ import com.mysql.cj.x.protobuf.MysqlxResultset;
/*     */ import java.io.UnsupportedEncodingException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FieldFactory
/*     */   implements ProtocolEntityFactory<Field, XMessage>
/*     */ {
/*     */   private static final int XPROTOCOL_COLUMN_BYTES_CONTENT_TYPE_GEOMETRY = 1;
/*     */   private static final int XPROTOCOL_COLUMN_BYTES_CONTENT_TYPE_JSON = 2;
/*     */   private static final int XPROTOCOL_COLUMN_FLAGS_UINT_ZEROFILL = 1;
/*     */   private static final int XPROTOCOL_COLUMN_FLAGS_DOUBLE_UNSIGNED = 1;
/*     */   private static final int XPROTOCOL_COLUMN_FLAGS_FLOAT_UNSIGNED = 1;
/*     */   private static final int XPROTOCOL_COLUMN_FLAGS_DECIMAL_UNSIGNED = 1;
/*     */   private static final int XPROTOCOL_COLUMN_FLAGS_BYTES_RIGHTPAD = 1;
/*     */   private static final int XPROTOCOL_COLUMN_FLAGS_DATETIME_TIMESTAMP = 1;
/*     */   private static final int XPROTOCOL_COLUMN_FLAGS_NOT_NULL = 16;
/*     */   private static final int XPROTOCOL_COLUMN_FLAGS_PRIMARY_KEY = 32;
/*     */   private static final int XPROTOCOL_COLUMN_FLAGS_UNIQUE_KEY = 64;
/*     */   private static final int XPROTOCOL_COLUMN_FLAGS_MULTIPLE_KEY = 128;
/*     */   private static final int XPROTOCOL_COLUMN_FLAGS_AUTO_INCREMENT = 256;
/*     */   String metadataCharacterSet;
/*     */   
/*     */   public FieldFactory(String metadataCharSet) {
/*  70 */     this.metadataCharacterSet = metadataCharSet;
/*     */   }
/*     */ 
/*     */   
/*     */   public Field createFromMessage(XMessage message) {
/*  75 */     return columnMetaDataToField((MysqlxResultset.ColumnMetaData)message.getMessage(), this.metadataCharacterSet);
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
/*     */   private Field columnMetaDataToField(MysqlxResultset.ColumnMetaData col, String characterSet) {
/*     */     try {
/*  89 */       LazyString databaseName = new LazyString(col.getSchema().toString(characterSet));
/*  90 */       LazyString tableName = new LazyString(col.getTable().toString(characterSet));
/*  91 */       LazyString originalTableName = new LazyString(col.getOriginalTable().toString(characterSet));
/*  92 */       LazyString columnName = new LazyString(col.getName().toString(characterSet));
/*  93 */       LazyString originalColumnName = new LazyString(col.getOriginalName().toString(characterSet));
/*     */       
/*  95 */       long length = Integer.toUnsignedLong(col.getLength());
/*  96 */       int decimals = col.getFractionalDigits();
/*  97 */       int collationIndex = 0;
/*  98 */       if (col.hasCollation())
/*     */       {
/* 100 */         collationIndex = (int)col.getCollation();
/*     */       }
/*     */       
/* 103 */       String encoding = CharsetMapping.getStaticJavaEncodingForCollationIndex(Integer.valueOf(collationIndex));
/*     */       
/* 105 */       MysqlType mysqlType = findMysqlType(col.getType(), col.getContentType(), col.getFlags(), collationIndex);
/* 106 */       int mysqlTypeId = xProtocolTypeToMysqlType(col.getType(), col.getContentType());
/*     */ 
/*     */       
/* 109 */       short flags = 0;
/* 110 */       if (col.getType().equals(MysqlxResultset.ColumnMetaData.FieldType.UINT) && 0 < (col.getFlags() & 0x1)) {
/* 111 */         flags = (short)(flags | 0x40);
/* 112 */       } else if (col.getType().equals(MysqlxResultset.ColumnMetaData.FieldType.BYTES) && 0 < (col.getFlags() & 0x1)) {
/* 113 */         mysqlType = MysqlType.CHAR;
/* 114 */       } else if (col.getType().equals(MysqlxResultset.ColumnMetaData.FieldType.DATETIME) && 0 < (col.getFlags() & 0x1)) {
/* 115 */         mysqlType = MysqlType.TIMESTAMP;
/*     */       } 
/* 117 */       if ((col.getFlags() & 0x10) > 0) {
/* 118 */         flags = (short)(flags | 0x1);
/*     */       }
/* 120 */       if ((col.getFlags() & 0x20) > 0) {
/* 121 */         flags = (short)(flags | 0x2);
/*     */       }
/* 123 */       if ((col.getFlags() & 0x40) > 0) {
/* 124 */         flags = (short)(flags | 0x4);
/*     */       }
/* 126 */       if ((col.getFlags() & 0x80) > 0) {
/* 127 */         flags = (short)(flags | 0x8);
/*     */       }
/* 129 */       if ((col.getFlags() & 0x100) > 0) {
/* 130 */         flags = (short)(flags | 0x200);
/*     */       }
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 136 */       switch (mysqlType) {
/*     */         case SINT:
/*     */         case UINT:
/*     */         case FLOAT:
/*     */         case DOUBLE:
/* 141 */           if (decimals == 31) {
/* 142 */             decimals = 0;
/*     */           }
/*     */           break;
/*     */       } 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 150 */       Field f = new Field(databaseName, tableName, originalTableName, columnName, originalColumnName, length, mysqlTypeId, flags, decimals, collationIndex, encoding, mysqlType);
/*     */       
/* 152 */       return f;
/* 153 */     } catch (UnsupportedEncodingException ex) {
/* 154 */       throw new WrongArgumentException("Unable to decode metadata strings", ex);
/*     */     } 
/*     */   }
/*     */   
/*     */   private MysqlType findMysqlType(MysqlxResultset.ColumnMetaData.FieldType type, int contentType, int flags, int collationIndex) {
/* 159 */     switch (type) {
/*     */       case SINT:
/* 161 */         return MysqlType.BIGINT;
/*     */       case UINT:
/* 163 */         return MysqlType.BIGINT_UNSIGNED;
/*     */       case FLOAT:
/* 165 */         return (0 < (flags & 0x1)) ? MysqlType.FLOAT_UNSIGNED : MysqlType.FLOAT;
/*     */       case DOUBLE:
/* 167 */         return (0 < (flags & 0x1)) ? MysqlType.DOUBLE_UNSIGNED : MysqlType.DOUBLE;
/*     */       case DECIMAL:
/* 169 */         return (0 < (flags & 0x1)) ? MysqlType.DECIMAL_UNSIGNED : MysqlType.DECIMAL;
/*     */       case BYTES:
/* 171 */         switch (contentType) {
/*     */           case 1:
/* 173 */             return MysqlType.GEOMETRY;
/*     */           case 2:
/* 175 */             return MysqlType.JSON;
/*     */         } 
/* 177 */         if (collationIndex == 33) {
/* 178 */           return MysqlType.VARBINARY;
/*     */         }
/* 180 */         return MysqlType.VARCHAR;
/*     */       
/*     */       case TIME:
/* 183 */         return MysqlType.TIME;
/*     */       case DATETIME:
/* 185 */         return MysqlType.DATETIME;
/*     */       case SET:
/* 187 */         return MysqlType.SET;
/*     */       case ENUM:
/* 189 */         return MysqlType.ENUM;
/*     */       case BIT:
/* 191 */         return MysqlType.BIT;
/*     */     } 
/*     */     
/* 194 */     throw new WrongArgumentException("TODO: unknown field type: " + type);
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
/*     */   private int xProtocolTypeToMysqlType(MysqlxResultset.ColumnMetaData.FieldType type, int contentType) {
/* 209 */     switch (type) {
/*     */       
/*     */       case SINT:
/* 212 */         return 8;
/*     */       case UINT:
/* 214 */         return 8;
/*     */       case FLOAT:
/* 216 */         return 4;
/*     */       case DOUBLE:
/* 218 */         return 5;
/*     */       case DECIMAL:
/* 220 */         return 246;
/*     */       case BYTES:
/* 222 */         switch (contentType) {
/*     */           case 1:
/* 224 */             return 255;
/*     */           case 2:
/* 226 */             return 245;
/*     */         } 
/* 228 */         return 15;
/*     */       
/*     */       case TIME:
/* 231 */         return 11;
/*     */       
/*     */       case DATETIME:
/* 234 */         return 12;
/*     */       case SET:
/* 236 */         return 248;
/*     */       case ENUM:
/* 238 */         return 247;
/*     */       case BIT:
/* 240 */         return 16;
/*     */     } 
/*     */     
/* 243 */     throw new WrongArgumentException("TODO: unknown field type: " + type);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\x\FieldFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */