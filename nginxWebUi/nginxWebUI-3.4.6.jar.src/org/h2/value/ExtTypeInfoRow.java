/*     */ package org.h2.value;
/*     */ 
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ import java.util.Set;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.ParserUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class ExtTypeInfoRow
/*     */   extends ExtTypeInfo
/*     */ {
/*     */   private final LinkedHashMap<String, TypeInfo> fields;
/*     */   private int hash;
/*     */   
/*     */   public ExtTypeInfoRow(Typed[] paramArrayOfTyped) {
/*  34 */     this(paramArrayOfTyped, paramArrayOfTyped.length);
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
/*     */   public ExtTypeInfoRow(Typed[] paramArrayOfTyped, int paramInt) {
/*  46 */     if (paramInt > 16384) {
/*  47 */       throw DbException.get(54011, "16384");
/*     */     }
/*  49 */     LinkedHashMap<Object, Object> linkedHashMap = new LinkedHashMap<>((int)Math.ceil(paramInt / 0.75D));
/*  50 */     for (byte b = 0; b < paramInt; ) {
/*  51 */       TypeInfo typeInfo = paramArrayOfTyped[b].getType();
/*  52 */       linkedHashMap.put("C" + ++b, typeInfo);
/*     */     } 
/*  54 */     this.fields = (LinkedHashMap)linkedHashMap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ExtTypeInfoRow(LinkedHashMap<String, TypeInfo> paramLinkedHashMap) {
/*  64 */     if (paramLinkedHashMap.size() > 16384) {
/*  65 */       throw DbException.get(54011, "16384");
/*     */     }
/*  67 */     this.fields = paramLinkedHashMap;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Set<Map.Entry<String, TypeInfo>> getFields() {
/*  76 */     return this.fields.entrySet();
/*     */   }
/*     */ 
/*     */   
/*     */   public StringBuilder getSQL(StringBuilder paramStringBuilder, int paramInt) {
/*  81 */     paramStringBuilder.append('(');
/*  82 */     boolean bool = false;
/*  83 */     for (Map.Entry<String, TypeInfo> entry : this.fields.entrySet()) {
/*  84 */       if (bool) {
/*  85 */         paramStringBuilder.append(", ");
/*     */       }
/*  87 */       bool = true;
/*  88 */       ParserUtil.quoteIdentifier(paramStringBuilder, (String)entry.getKey(), paramInt).append(' ');
/*  89 */       ((TypeInfo)entry.getValue()).getSQL(paramStringBuilder, paramInt);
/*     */     } 
/*  91 */     return paramStringBuilder.append(')');
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/*  96 */     int i = this.hash;
/*  97 */     if (i != 0) {
/*  98 */       return i;
/*     */     }
/* 100 */     i = 67378403;
/* 101 */     for (Map.Entry<String, TypeInfo> entry : this.fields.entrySet()) {
/* 102 */       i = (i * 31 + ((String)entry.getKey()).hashCode()) * 37 + ((TypeInfo)entry.getValue()).hashCode();
/*     */     }
/* 104 */     return this.hash = i;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object paramObject) {
/* 109 */     if (this == paramObject) {
/* 110 */       return true;
/*     */     }
/* 112 */     if (paramObject.getClass() != ExtTypeInfoRow.class) {
/* 113 */       return false;
/*     */     }
/* 115 */     LinkedHashMap<String, TypeInfo> linkedHashMap = ((ExtTypeInfoRow)paramObject).fields;
/* 116 */     int i = this.fields.size();
/* 117 */     if (i != linkedHashMap.size()) {
/* 118 */       return false;
/*     */     }
/* 120 */     Iterator<Map.Entry> iterator1 = this.fields.entrySet().iterator();
/* 121 */     for (Iterator<Map.Entry> iterator2 = linkedHashMap.entrySet().iterator(); iterator1.hasNext(); ) {
/* 122 */       Map.Entry entry1 = iterator1.next(), entry2 = iterator2.next();
/* 123 */       if (!((String)entry1.getKey()).equals(entry2.getKey()) || !((TypeInfo)entry1.getValue()).equals(entry2.getValue())) {
/* 124 */         return false;
/*     */       }
/*     */     } 
/* 127 */     return true;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\value\ExtTypeInfoRow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */