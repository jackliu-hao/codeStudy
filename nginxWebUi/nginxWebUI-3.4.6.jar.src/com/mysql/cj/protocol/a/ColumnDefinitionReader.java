/*     */ package com.mysql.cj.protocol.a;
/*     */ 
/*     */ import com.mysql.cj.MysqlType;
/*     */ import com.mysql.cj.protocol.ColumnDefinition;
/*     */ import com.mysql.cj.protocol.ProtocolEntity;
/*     */ import com.mysql.cj.protocol.ProtocolEntityFactory;
/*     */ import com.mysql.cj.protocol.ProtocolEntityReader;
/*     */ import com.mysql.cj.result.Field;
/*     */ import com.mysql.cj.util.LazyString;
/*     */ import java.io.IOException;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class ColumnDefinitionReader
/*     */   implements ProtocolEntityReader<ColumnDefinition, NativePacketPayload>
/*     */ {
/*     */   private NativeProtocol protocol;
/*     */   
/*     */   public ColumnDefinitionReader(NativeProtocol prot) {
/*  45 */     this.protocol = prot;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public ColumnDefinition read(ProtocolEntityFactory<ColumnDefinition, NativePacketPayload> sf) {
/*  51 */     ColumnDefinitionFactory cdf = (ColumnDefinitionFactory)sf;
/*     */     
/*  53 */     long columnCount = cdf.getColumnCount();
/*  54 */     ColumnDefinition cdef = cdf.getColumnDefinitionFromCache();
/*     */     
/*  56 */     if (cdef != null && !cdf.mergeColumnDefinitions()) {
/*  57 */       for (int j = 0; j < columnCount; j++) {
/*  58 */         this.protocol.skipPacket();
/*     */       }
/*  60 */       return cdef;
/*     */     } 
/*     */ 
/*     */     
/*  64 */     Field[] fields = null;
/*  65 */     boolean checkEOF = !this.protocol.getServerSession().isEOFDeprecated();
/*     */ 
/*     */ 
/*     */     
/*  69 */     fields = new Field[(int)columnCount];
/*     */     
/*  71 */     for (int i = 0; i < columnCount; i++) {
/*  72 */       NativePacketPayload fieldPacket = this.protocol.readMessage((NativePacketPayload)null);
/*     */       
/*  74 */       if (checkEOF && fieldPacket.isEOFPacket()) {
/*     */         break;
/*     */       }
/*  77 */       fields[i] = unpackField(fieldPacket, this.protocol.getServerSession().getCharsetSettings().getMetadataEncoding());
/*     */     } 
/*     */     
/*  80 */     return cdf.createFromFields(fields);
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
/*     */   protected Field unpackField(NativePacketPayload packet, String characterSetMetadata) {
/*  96 */     int length = (int)packet.readInteger(NativeConstants.IntegerDataType.INT_LENENC);
/*  97 */     packet.setPosition(packet.getPosition() + length);
/*     */     
/*  99 */     length = (int)packet.readInteger(NativeConstants.IntegerDataType.INT_LENENC);
/* 100 */     int offset = packet.getPosition();
/* 101 */     LazyString databaseName = new LazyString(packet.getByteBuffer(), offset, length, characterSetMetadata);
/* 102 */     packet.setPosition(packet.getPosition() + length);
/*     */     
/* 104 */     length = (int)packet.readInteger(NativeConstants.IntegerDataType.INT_LENENC);
/* 105 */     offset = packet.getPosition();
/* 106 */     LazyString tableName = new LazyString(packet.getByteBuffer(), offset, length, characterSetMetadata);
/* 107 */     packet.setPosition(packet.getPosition() + length);
/*     */     
/* 109 */     length = (int)packet.readInteger(NativeConstants.IntegerDataType.INT_LENENC);
/* 110 */     offset = packet.getPosition();
/* 111 */     LazyString originalTableName = new LazyString(packet.getByteBuffer(), offset, length, characterSetMetadata);
/* 112 */     packet.setPosition(packet.getPosition() + length);
/*     */     
/* 114 */     length = (int)packet.readInteger(NativeConstants.IntegerDataType.INT_LENENC);
/* 115 */     offset = packet.getPosition();
/* 116 */     LazyString columnName = new LazyString(packet.getByteBuffer(), offset, length, characterSetMetadata);
/* 117 */     packet.setPosition(packet.getPosition() + length);
/*     */     
/* 119 */     length = (int)packet.readInteger(NativeConstants.IntegerDataType.INT_LENENC);
/* 120 */     offset = packet.getPosition();
/* 121 */     LazyString originalColumnName = new LazyString(packet.getByteBuffer(), offset, length, characterSetMetadata);
/* 122 */     packet.setPosition(packet.getPosition() + length);
/*     */     
/* 124 */     packet.readInteger(NativeConstants.IntegerDataType.INT1);
/*     */     
/* 126 */     short collationIndex = (short)(int)packet.readInteger(NativeConstants.IntegerDataType.INT2);
/* 127 */     long colLength = packet.readInteger(NativeConstants.IntegerDataType.INT4);
/* 128 */     int colType = (int)packet.readInteger(NativeConstants.IntegerDataType.INT1);
/* 129 */     short colFlag = (short)(int)packet.readInteger(this.protocol.getServerSession().hasLongColumnInfo() ? NativeConstants.IntegerDataType.INT2 : NativeConstants.IntegerDataType.INT1);
/* 130 */     int colDecimals = (int)packet.readInteger(NativeConstants.IntegerDataType.INT1);
/*     */     
/* 132 */     String encoding = this.protocol.getServerSession().getCharsetSettings().getJavaEncodingForCollationIndex(collationIndex);
/*     */     
/* 134 */     MysqlType mysqlType = NativeProtocol.findMysqlType(this.protocol.getPropertySet(), colType, colFlag, colLength, tableName, originalTableName, collationIndex, encoding);
/*     */ 
/*     */ 
/*     */     
/* 138 */     switch (mysqlType) {
/*     */       case TINYINT:
/*     */       case TINYINT_UNSIGNED:
/*     */       case SMALLINT:
/*     */       case SMALLINT_UNSIGNED:
/*     */       case MEDIUMINT:
/*     */       case MEDIUMINT_UNSIGNED:
/*     */       case INT:
/*     */       case INT_UNSIGNED:
/*     */       case BIGINT:
/*     */       case BIGINT_UNSIGNED:
/*     */       case BOOLEAN:
/* 150 */         colLength = mysqlType.getPrecision().intValue();
/*     */         break;
/*     */       
/*     */       case DECIMAL:
/* 154 */         colLength--;
/* 155 */         if (colDecimals > 0) {
/* 156 */           colLength--;
/*     */         }
/*     */         break;
/*     */       case DECIMAL_UNSIGNED:
/* 160 */         if (colDecimals > 0) {
/* 161 */           colLength--;
/*     */         }
/*     */         break;
/*     */ 
/*     */ 
/*     */       
/*     */       case FLOAT:
/*     */       case FLOAT_UNSIGNED:
/*     */       case DOUBLE:
/*     */       case DOUBLE_UNSIGNED:
/* 171 */         if (colDecimals == 31) {
/* 172 */           colDecimals = 0;
/*     */         }
/*     */         break;
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 180 */     return new Field(databaseName, tableName, originalTableName, columnName, originalColumnName, colLength, colType, colFlag, colDecimals, collationIndex, encoding, mysqlType);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\a\ColumnDefinitionReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */