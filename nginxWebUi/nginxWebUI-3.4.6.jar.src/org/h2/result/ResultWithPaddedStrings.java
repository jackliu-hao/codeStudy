/*     */ package org.h2.result;
/*     */ 
/*     */ import java.util.Arrays;
/*     */ import org.h2.engine.Session;
/*     */ import org.h2.util.MathUtils;
/*     */ import org.h2.value.TypeInfo;
/*     */ import org.h2.value.Value;
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
/*     */ public class ResultWithPaddedStrings
/*     */   implements ResultInterface
/*     */ {
/*     */   private final ResultInterface source;
/*     */   
/*     */   public static ResultInterface get(ResultInterface paramResultInterface) {
/*  31 */     int i = paramResultInterface.getVisibleColumnCount();
/*  32 */     for (byte b = 0; b < i; b++) {
/*  33 */       if (paramResultInterface.getColumnType(b).getValueType() == 1) {
/*  34 */         return new ResultWithPaddedStrings(paramResultInterface);
/*     */       }
/*     */     } 
/*  37 */     return paramResultInterface;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private ResultWithPaddedStrings(ResultInterface paramResultInterface) {
/*  47 */     this.source = paramResultInterface;
/*     */   }
/*     */ 
/*     */   
/*     */   public void reset() {
/*  52 */     this.source.reset();
/*     */   }
/*     */ 
/*     */   
/*     */   public Value[] currentRow() {
/*  57 */     int i = this.source.getVisibleColumnCount();
/*  58 */     Value[] arrayOfValue = Arrays.<Value>copyOf(this.source.currentRow(), i);
/*  59 */     for (byte b = 0; b < i; b++) {
/*  60 */       TypeInfo typeInfo = this.source.getColumnType(b);
/*  61 */       if (typeInfo.getValueType() == 1) {
/*  62 */         long l = typeInfo.getPrecision();
/*  63 */         if (l == 2147483647L)
/*     */         {
/*  65 */           l = 1L;
/*     */         }
/*  67 */         String str = arrayOfValue[b].getString();
/*  68 */         if (str != null && str.length() < l)
/*     */         {
/*     */ 
/*     */ 
/*     */ 
/*     */           
/*  74 */           arrayOfValue[b] = ValueVarchar.get(rightPadWithSpaces(str, MathUtils.convertLongToInt(l)));
/*     */         }
/*     */       } 
/*     */     } 
/*  78 */     return arrayOfValue;
/*     */   }
/*     */   
/*     */   private static String rightPadWithSpaces(String paramString, int paramInt) {
/*  82 */     int i = paramString.length();
/*  83 */     if (paramInt <= i) {
/*  84 */       return paramString;
/*     */     }
/*  86 */     char[] arrayOfChar = new char[paramInt];
/*  87 */     paramString.getChars(0, i, arrayOfChar, 0);
/*  88 */     Arrays.fill(arrayOfChar, i, paramInt, ' ');
/*  89 */     return new String(arrayOfChar);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean next() {
/*  94 */     return this.source.next();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRowId() {
/*  99 */     return this.source.getRowId();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isAfterLast() {
/* 104 */     return this.source.isAfterLast();
/*     */   }
/*     */ 
/*     */   
/*     */   public int getVisibleColumnCount() {
/* 109 */     return this.source.getVisibleColumnCount();
/*     */   }
/*     */ 
/*     */   
/*     */   public long getRowCount() {
/* 114 */     return this.source.getRowCount();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean hasNext() {
/* 119 */     return this.source.hasNext();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needToClose() {
/* 124 */     return this.source.needToClose();
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 129 */     this.source.close();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getAlias(int paramInt) {
/* 134 */     return this.source.getAlias(paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getSchemaName(int paramInt) {
/* 139 */     return this.source.getSchemaName(paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getTableName(int paramInt) {
/* 144 */     return this.source.getTableName(paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public String getColumnName(int paramInt) {
/* 149 */     return this.source.getColumnName(paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public TypeInfo getColumnType(int paramInt) {
/* 154 */     return this.source.getColumnType(paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isIdentity(int paramInt) {
/* 159 */     return this.source.isIdentity(paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getNullable(int paramInt) {
/* 164 */     return this.source.getNullable(paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public void setFetchSize(int paramInt) {
/* 169 */     this.source.setFetchSize(paramInt);
/*     */   }
/*     */ 
/*     */   
/*     */   public int getFetchSize() {
/* 174 */     return this.source.getFetchSize();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isLazy() {
/* 179 */     return this.source.isLazy();
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isClosed() {
/* 184 */     return this.source.isClosed();
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface createShallowCopy(Session paramSession) {
/* 189 */     ResultInterface resultInterface = this.source.createShallowCopy(paramSession);
/* 190 */     return (resultInterface != null) ? new ResultWithPaddedStrings(resultInterface) : null;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\result\ResultWithPaddedStrings.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */