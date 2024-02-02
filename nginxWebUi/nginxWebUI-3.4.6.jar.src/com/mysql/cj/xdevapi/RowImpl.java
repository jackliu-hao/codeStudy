/*     */ package com.mysql.cj.xdevapi;
/*     */ 
/*     */ import com.mysql.cj.conf.PropertySet;
/*     */ import com.mysql.cj.exceptions.DataReadException;
/*     */ import com.mysql.cj.protocol.ColumnDefinition;
/*     */ import com.mysql.cj.result.BigDecimalValueFactory;
/*     */ import com.mysql.cj.result.BooleanValueFactory;
/*     */ import com.mysql.cj.result.ByteValueFactory;
/*     */ import com.mysql.cj.result.DoubleValueFactory;
/*     */ import com.mysql.cj.result.IntegerValueFactory;
/*     */ import com.mysql.cj.result.LongValueFactory;
/*     */ import com.mysql.cj.result.Row;
/*     */ import com.mysql.cj.result.SqlDateValueFactory;
/*     */ import com.mysql.cj.result.SqlTimeValueFactory;
/*     */ import com.mysql.cj.result.SqlTimestampValueFactory;
/*     */ import com.mysql.cj.result.StringValueFactory;
/*     */ import com.mysql.cj.result.ValueFactory;
/*     */ import java.math.BigDecimal;
/*     */ import java.sql.Date;
/*     */ import java.sql.Time;
/*     */ import java.sql.Timestamp;
/*     */ import java.util.TimeZone;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RowImpl
/*     */   implements Row
/*     */ {
/*     */   private Row row;
/*     */   private ColumnDefinition metadata;
/*     */   private TimeZone defaultTimeZone;
/*     */   private PropertySet pset;
/*     */   
/*     */   public RowImpl(Row row, ColumnDefinition metadata, TimeZone defaultTimeZone, PropertySet pset) {
/*  78 */     this.row = row;
/*  79 */     this.metadata = metadata;
/*  80 */     this.defaultTimeZone = defaultTimeZone;
/*  81 */     this.pset = pset;
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
/*     */   private int fieldNameToIndex(String fieldName) {
/*  94 */     int idx = this.metadata.findColumn(fieldName, true, 0);
/*  95 */     if (idx == -1) {
/*  96 */       throw new DataReadException("Invalid column");
/*     */     }
/*  98 */     return idx;
/*     */   }
/*     */   
/*     */   public BigDecimal getBigDecimal(String fieldName) {
/* 102 */     return getBigDecimal(fieldNameToIndex(fieldName));
/*     */   }
/*     */   
/*     */   public BigDecimal getBigDecimal(int pos) {
/* 106 */     return (BigDecimal)this.row.getValue(pos, (ValueFactory)new BigDecimalValueFactory(this.pset));
/*     */   }
/*     */   
/*     */   public boolean getBoolean(String fieldName) {
/* 110 */     return getBoolean(fieldNameToIndex(fieldName));
/*     */   }
/*     */   
/*     */   public boolean getBoolean(int pos) {
/* 114 */     Boolean res = (Boolean)this.row.getValue(pos, (ValueFactory)new BooleanValueFactory(this.pset));
/* 115 */     return (res == null) ? false : res.booleanValue();
/*     */   }
/*     */   
/*     */   public byte getByte(String fieldName) {
/* 119 */     return getByte(fieldNameToIndex(fieldName));
/*     */   }
/*     */   
/*     */   public byte getByte(int pos) {
/* 123 */     Byte res = (Byte)this.row.getValue(pos, (ValueFactory)new ByteValueFactory(this.pset));
/* 124 */     return (res == null) ? 0 : res.byteValue();
/*     */   }
/*     */   
/*     */   public Date getDate(String fieldName) {
/* 128 */     return getDate(fieldNameToIndex(fieldName));
/*     */   }
/*     */   
/*     */   public Date getDate(int pos) {
/* 132 */     return (Date)this.row.getValue(pos, (ValueFactory)new SqlDateValueFactory(this.pset, null, this.defaultTimeZone));
/*     */   }
/*     */   
/*     */   public DbDoc getDbDoc(String fieldName) {
/* 136 */     return getDbDoc(fieldNameToIndex(fieldName));
/*     */   }
/*     */   
/*     */   public DbDoc getDbDoc(int pos) {
/* 140 */     return (DbDoc)this.row.getValue(pos, (ValueFactory)new DbDocValueFactory(this.pset));
/*     */   }
/*     */   
/*     */   public double getDouble(String fieldName) {
/* 144 */     return getDouble(fieldNameToIndex(fieldName));
/*     */   }
/*     */   
/*     */   public double getDouble(int pos) {
/* 148 */     Double res = (Double)this.row.getValue(pos, (ValueFactory)new DoubleValueFactory(this.pset));
/* 149 */     return (res == null) ? 0.0D : res.doubleValue();
/*     */   }
/*     */   
/*     */   public int getInt(String fieldName) {
/* 153 */     return getInt(fieldNameToIndex(fieldName));
/*     */   }
/*     */   
/*     */   public int getInt(int pos) {
/* 157 */     Integer res = (Integer)this.row.getValue(pos, (ValueFactory)new IntegerValueFactory(this.pset));
/* 158 */     return (res == null) ? 0 : res.intValue();
/*     */   }
/*     */   
/*     */   public long getLong(String fieldName) {
/* 162 */     return getLong(fieldNameToIndex(fieldName));
/*     */   }
/*     */   
/*     */   public long getLong(int pos) {
/* 166 */     Long res = (Long)this.row.getValue(pos, (ValueFactory)new LongValueFactory(this.pset));
/* 167 */     return (res == null) ? 0L : res.longValue();
/*     */   }
/*     */   
/*     */   public String getString(String fieldName) {
/* 171 */     return getString(fieldNameToIndex(fieldName));
/*     */   }
/*     */ 
/*     */   
/*     */   public String getString(int pos) {
/* 176 */     return (String)this.row.getValue(pos, (ValueFactory)new StringValueFactory(this.pset));
/*     */   }
/*     */   
/*     */   public Time getTime(String fieldName) {
/* 180 */     return getTime(fieldNameToIndex(fieldName));
/*     */   }
/*     */   
/*     */   public Time getTime(int pos) {
/* 184 */     return (Time)this.row.getValue(pos, (ValueFactory)new SqlTimeValueFactory(this.pset, null, this.defaultTimeZone));
/*     */   }
/*     */   
/*     */   public Timestamp getTimestamp(String fieldName) {
/* 188 */     return getTimestamp(fieldNameToIndex(fieldName));
/*     */   }
/*     */   
/*     */   public Timestamp getTimestamp(int pos) {
/* 192 */     return (Timestamp)this.row.getValue(pos, (ValueFactory)new SqlTimestampValueFactory(this.pset, null, this.defaultTimeZone, this.defaultTimeZone));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\xdevapi\RowImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */