/*     */ package cn.hutool.db.sql;
/*     */ 
/*     */ import cn.hutool.core.map.MapUtil;
/*     */ import cn.hutool.core.text.StrBuilder;
/*     */ import cn.hutool.core.util.ArrayUtil;
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.util.LinkedList;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class NamedSql
/*     */ {
/*  26 */   private static final char[] NAME_START_CHARS = new char[] { ':', '@', '?' };
/*     */ 
/*     */ 
/*     */   
/*     */   private String sql;
/*     */ 
/*     */   
/*     */   private final List<Object> params;
/*     */ 
/*     */ 
/*     */   
/*     */   public NamedSql(String namedSql, Map<String, Object> paramMap) {
/*  38 */     this.params = new LinkedList();
/*  39 */     parse(namedSql, paramMap);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getSql() {
/*  48 */     return this.sql;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object[] getParams() {
/*  57 */     return this.params.toArray(new Object[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public List<Object> getParamList() {
/*  66 */     return this.params;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void parse(String namedSql, Map<String, Object> paramMap) {
/*  76 */     if (MapUtil.isEmpty(paramMap)) {
/*  77 */       this.sql = namedSql;
/*     */       
/*     */       return;
/*     */     } 
/*  81 */     int len = namedSql.length();
/*     */     
/*  83 */     StrBuilder name = StrUtil.strBuilder();
/*  84 */     StrBuilder sqlBuilder = StrUtil.strBuilder();
/*     */     
/*  86 */     Character nameStartChar = null;
/*  87 */     for (int i = 0; i < len; i++) {
/*  88 */       char c = namedSql.charAt(i);
/*  89 */       if (ArrayUtil.contains(NAME_START_CHARS, c)) {
/*     */         
/*  91 */         replaceVar(nameStartChar, name, sqlBuilder, paramMap);
/*  92 */         nameStartChar = Character.valueOf(c);
/*  93 */       } else if (null != nameStartChar) {
/*     */         
/*  95 */         if (isGenerateChar(c)) {
/*     */           
/*  97 */           name.append(c);
/*     */         } else {
/*     */           
/* 100 */           replaceVar(nameStartChar, name, sqlBuilder, paramMap);
/* 101 */           nameStartChar = null;
/* 102 */           sqlBuilder.append(c);
/*     */         } 
/*     */       } else {
/*     */         
/* 106 */         sqlBuilder.append(c);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 111 */     if (false == name.isEmpty()) {
/* 112 */       replaceVar(nameStartChar, name, sqlBuilder, paramMap);
/*     */     }
/*     */     
/* 115 */     this.sql = sqlBuilder.toString();
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
/*     */   private void replaceVar(Character nameStartChar, StrBuilder name, StrBuilder sqlBuilder, Map<String, Object> paramMap) {
/* 127 */     if (name.isEmpty()) {
/* 128 */       if (null != nameStartChar)
/*     */       {
/* 130 */         sqlBuilder.append(nameStartChar);
/*     */       }
/*     */ 
/*     */       
/*     */       return;
/*     */     } 
/*     */     
/* 137 */     String nameStr = name.toString();
/* 138 */     if (paramMap.containsKey(nameStr)) {
/*     */       
/* 140 */       Object paramValue = paramMap.get(nameStr);
/* 141 */       if (ArrayUtil.isArray(paramValue) && StrUtil.contains((CharSequence)sqlBuilder, "in")) {
/*     */         
/* 143 */         int length = ArrayUtil.length(paramValue);
/* 144 */         for (int i = 0; i < length; i++) {
/* 145 */           if (0 != i) {
/* 146 */             sqlBuilder.append(',');
/*     */           }
/* 148 */           sqlBuilder.append('?');
/* 149 */           this.params.add(ArrayUtil.get(paramValue, i));
/*     */         } 
/*     */       } else {
/* 152 */         sqlBuilder.append('?');
/* 153 */         this.params.add(paramValue);
/*     */       } 
/*     */     } else {
/*     */       
/* 157 */       sqlBuilder.append(nameStartChar).append((CharSequence)name);
/*     */     } 
/*     */ 
/*     */     
/* 161 */     name.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static boolean isGenerateChar(char c) {
/* 171 */     return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_' || (c >= '0' && c <= '9'));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\sql\NamedSql.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */