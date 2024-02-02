/*     */ package org.h2.bnf.context;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import org.h2.bnf.Bnf;
/*     */ import org.h2.bnf.BnfVisitor;
/*     */ import org.h2.bnf.Rule;
/*     */ import org.h2.bnf.RuleElement;
/*     */ import org.h2.bnf.RuleHead;
/*     */ import org.h2.bnf.RuleList;
/*     */ import org.h2.bnf.Sentence;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.util.ParserUtil;
/*     */ import org.h2.util.StringUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DbContextRule
/*     */   implements Rule
/*     */ {
/*     */   public static final int COLUMN = 0;
/*     */   public static final int TABLE = 1;
/*     */   public static final int TABLE_ALIAS = 2;
/*     */   public static final int NEW_TABLE_ALIAS = 3;
/*     */   public static final int COLUMN_ALIAS = 4;
/*     */   public static final int SCHEMA = 5;
/*     */   public static final int PROCEDURE = 6;
/*     */   private final DbContents contents;
/*     */   private final int type;
/*     */   private String columnType;
/*     */   
/*     */   public DbContextRule(DbContents paramDbContents, int paramInt) {
/*  49 */     this.contents = paramDbContents;
/*  50 */     this.type = paramInt;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setColumnType(String paramString) {
/*  57 */     this.columnType = paramString;
/*     */   }
/*     */   
/*     */   public void setLinks(HashMap<String, RuleHead> paramHashMap) {}
/*     */   
/*     */   public boolean autoComplete(Sentence paramSentence) {
/*     */     DbSchema arrayOfDbSchema[], dbSchema1;
/*     */     byte b;
/*     */     HashSet hashSet;
/*     */     String str5;
/*     */     DbTableOrView[] arrayOfDbTableOrView;
/*     */     String str4;
/*     */     DbSchema dbSchema2;
/*     */     String str6;
/*     */     DbTableOrView dbTableOrView1, dbTableOrView2;
/*  72 */     String str1 = paramSentence.getQuery(), str2 = str1;
/*  73 */     String str3 = paramSentence.getQueryUpper();
/*  74 */     switch (this.type) {
/*     */       case 5:
/*  76 */         arrayOfDbSchema = this.contents.getSchemas();
/*  77 */         str5 = null;
/*  78 */         dbSchema2 = null;
/*  79 */         for (DbSchema dbSchema : arrayOfDbSchema) {
/*  80 */           String str = StringUtils.toUpperEnglish(dbSchema.name);
/*  81 */           if (str3.startsWith(str)) {
/*  82 */             if (str5 == null || str.length() > str5.length()) {
/*  83 */               str5 = str;
/*  84 */               dbSchema2 = dbSchema;
/*     */             } 
/*  86 */           } else if ((str2.length() == 0 || str.startsWith(str3)) && 
/*  87 */             str2.length() < str.length()) {
/*  88 */             paramSentence.add(str, str.substring(str2.length()), this.type);
/*  89 */             paramSentence.add(dbSchema.quotedName + ".", dbSchema.quotedName
/*  90 */                 .substring(str2.length()) + ".", 0);
/*     */           } 
/*     */         } 
/*     */ 
/*     */         
/*  95 */         if (str5 != null) {
/*  96 */           paramSentence.setLastMatchedSchema(dbSchema2);
/*  97 */           str2 = str2.substring(str5.length());
/*     */         } 
/*     */         break;
/*     */       
/*     */       case 1:
/* 102 */         dbSchema1 = paramSentence.getLastMatchedSchema();
/* 103 */         if (dbSchema1 == null) {
/* 104 */           dbSchema1 = this.contents.getDefaultSchema();
/*     */         }
/* 106 */         arrayOfDbTableOrView = dbSchema1.getTables();
/* 107 */         dbSchema2 = null;
/* 108 */         dbTableOrView2 = null;
/* 109 */         for (DbTableOrView dbTableOrView : arrayOfDbTableOrView) {
/* 110 */           String str7 = str3;
/* 111 */           String str8 = StringUtils.toUpperEnglish(dbTableOrView.getName());
/* 112 */           if (dbTableOrView.getQuotedName().length() > str8.length()) {
/* 113 */             str8 = dbTableOrView.getQuotedName();
/* 114 */             str7 = str1;
/*     */           } 
/* 116 */           if (str7.startsWith(str8)) {
/* 117 */             if (dbSchema2 == null || str8.length() > dbSchema2.length()) {
/* 118 */               str6 = str8;
/* 119 */               dbTableOrView2 = dbTableOrView;
/*     */             } 
/* 121 */           } else if ((str2.length() == 0 || str8.startsWith(str7)) && 
/* 122 */             str2.length() < str8.length()) {
/* 123 */             paramSentence.add(dbTableOrView.getQuotedName(), dbTableOrView
/* 124 */                 .getQuotedName().substring(str2.length()), 0);
/*     */           } 
/*     */         } 
/*     */ 
/*     */         
/* 129 */         if (str6 != null) {
/* 130 */           paramSentence.setLastMatchedTable(dbTableOrView2);
/* 131 */           paramSentence.addTable(dbTableOrView2);
/* 132 */           str2 = str2.substring(str6.length());
/*     */         } 
/*     */         break;
/*     */       
/*     */       case 3:
/* 137 */         str2 = autoCompleteTableAlias(paramSentence, true);
/*     */         break;
/*     */       case 2:
/* 140 */         str2 = autoCompleteTableAlias(paramSentence, false);
/*     */         break;
/*     */       case 4:
/* 143 */         b = 0;
/* 144 */         if (str1.indexOf(' ') < 0) {
/*     */           break;
/*     */         }
/* 147 */         for (; b < str3.length(); b++) {
/* 148 */           char c = str3.charAt(b);
/* 149 */           if (c != '_' && !Character.isLetterOrDigit(c)) {
/*     */             break;
/*     */           }
/*     */         } 
/* 153 */         if (b == 0) {
/*     */           break;
/*     */         }
/* 156 */         str4 = str3.substring(0, b);
/* 157 */         if (ParserUtil.isKeyword(str4, false)) {
/*     */           break;
/*     */         }
/* 160 */         str2 = str2.substring(str4.length());
/*     */         break;
/*     */       
/*     */       case 0:
/* 164 */         hashSet = paramSentence.getTables();
/* 165 */         str4 = null;
/* 166 */         dbTableOrView1 = paramSentence.getLastMatchedTable();
/* 167 */         if (dbTableOrView1 != null && dbTableOrView1.getColumns() != null) {
/* 168 */           for (DbColumn dbColumn : dbTableOrView1.getColumns()) {
/* 169 */             String str7 = str3;
/* 170 */             String str8 = StringUtils.toUpperEnglish(dbColumn.getName());
/* 171 */             if (dbColumn.getQuotedName().length() > str8.length()) {
/* 172 */               str8 = dbColumn.getQuotedName();
/* 173 */               str7 = str1;
/*     */             } 
/* 175 */             if (str7.startsWith(str8) && testColumnType(dbColumn)) {
/* 176 */               String str = str2.substring(str8.length());
/* 177 */               if (str4 == null || str.length() < str4.length()) {
/* 178 */                 str4 = str;
/* 179 */               } else if ((str2.length() == 0 || str8.startsWith(str7)) && 
/* 180 */                 str2.length() < str8.length()) {
/* 181 */                 paramSentence.add(dbColumn.getName(), dbColumn
/* 182 */                     .getName().substring(str2.length()), 0);
/*     */               } 
/*     */             } 
/*     */           } 
/*     */         }
/*     */ 
/*     */         
/* 189 */         for (DbSchema dbSchema : this.contents.getSchemas()) {
/* 190 */           for (DbTableOrView dbTableOrView : dbSchema.getTables()) {
/* 191 */             if (dbTableOrView == dbTableOrView1 || hashSet == null || hashSet.contains(dbTableOrView))
/*     */             {
/*     */               
/* 194 */               if (dbTableOrView != null && dbTableOrView.getColumns() != null)
/*     */               {
/*     */                 
/* 197 */                 for (DbColumn dbColumn : dbTableOrView.getColumns()) {
/* 198 */                   String str = StringUtils.toUpperEnglish(dbColumn
/* 199 */                       .getName());
/* 200 */                   if (testColumnType(dbColumn)) {
/* 201 */                     if (str3.startsWith(str)) {
/* 202 */                       String str7 = str2.substring(str.length());
/* 203 */                       if (str4 == null || str7.length() < str4.length()) {
/* 204 */                         str4 = str7;
/*     */                       }
/* 206 */                     } else if ((str2.length() == 0 || str.startsWith(str3)) && 
/* 207 */                       str2.length() < str.length()) {
/* 208 */                       paramSentence.add(dbColumn.getName(), dbColumn
/* 209 */                           .getName().substring(str2.length()), 0);
/*     */                     } 
/*     */                   }
/*     */                 } 
/*     */               }
/*     */             }
/*     */           } 
/*     */         } 
/* 217 */         if (str4 != null) {
/* 218 */           str2 = str4;
/*     */         }
/*     */         break;
/*     */       
/*     */       case 6:
/* 223 */         autoCompleteProcedure(paramSentence);
/*     */         break;
/*     */       default:
/* 226 */         throw DbException.getInternalError("type=" + this.type);
/*     */     } 
/* 228 */     if (!str2.equals(str1)) {
/* 229 */       while (Bnf.startWithSpace(str2)) {
/* 230 */         str2 = str2.substring(1);
/*     */       }
/* 232 */       paramSentence.setQuery(str2);
/* 233 */       return true;
/*     */     } 
/* 235 */     return false;
/*     */   }
/*     */   public void accept(BnfVisitor paramBnfVisitor) {}
/*     */   private boolean testColumnType(DbColumn paramDbColumn) {
/* 239 */     if (this.columnType == null) {
/* 240 */       return true;
/*     */     }
/* 242 */     String str = paramDbColumn.getDataType();
/* 243 */     if (this.columnType.contains("CHAR") || this.columnType.contains("CLOB")) {
/* 244 */       return (str.contains("CHAR") || str.contains("CLOB"));
/*     */     }
/* 246 */     if (this.columnType.contains("BINARY") || this.columnType.contains("BLOB")) {
/* 247 */       return (str.contains("BINARY") || str.contains("BLOB"));
/*     */     }
/* 249 */     return str.contains(this.columnType);
/*     */   }
/*     */   
/*     */   private void autoCompleteProcedure(Sentence paramSentence) {
/* 253 */     DbSchema dbSchema = paramSentence.getLastMatchedSchema();
/* 254 */     if (dbSchema == null) {
/* 255 */       dbSchema = this.contents.getDefaultSchema();
/*     */     }
/* 257 */     String str1 = paramSentence.getQueryUpper();
/* 258 */     String str2 = str1;
/* 259 */     int i = str1.indexOf('(');
/* 260 */     if (i != -1) {
/* 261 */       str2 = StringUtils.trimSubstring(str1, 0, i);
/*     */     }
/*     */ 
/*     */     
/* 265 */     RuleElement ruleElement1 = new RuleElement("(", "Function");
/* 266 */     RuleElement ruleElement2 = new RuleElement(")", "Function");
/* 267 */     RuleElement ruleElement3 = new RuleElement(",", "Function");
/*     */ 
/*     */     
/* 270 */     for (DbProcedure dbProcedure : dbSchema.getProcedures()) {
/* 271 */       String str = dbProcedure.getName();
/* 272 */       if (str.startsWith(str2)) {
/*     */         
/* 274 */         RuleElement ruleElement = new RuleElement(str, "Function");
/*     */         
/* 276 */         RuleList ruleList = new RuleList((Rule)ruleElement, (Rule)ruleElement1, false);
/*     */         
/* 278 */         if (str1.contains("(")) {
/* 279 */           for (DbColumn dbColumn : dbProcedure.getParameters()) {
/* 280 */             if (dbColumn.getPosition() > 1) {
/* 281 */               ruleList = new RuleList((Rule)ruleList, (Rule)ruleElement3, false);
/*     */             }
/* 283 */             DbContextRule dbContextRule = new DbContextRule(this.contents, 0);
/*     */             
/* 285 */             String str3 = dbColumn.getDataType();
/*     */             
/* 287 */             if (str3.contains("(")) {
/* 288 */               str3 = str3.substring(0, str3
/* 289 */                   .indexOf('('));
/*     */             }
/* 291 */             dbContextRule.setColumnType(str3);
/* 292 */             ruleList = new RuleList((Rule)ruleList, dbContextRule, false);
/*     */           } 
/* 294 */           ruleList = new RuleList((Rule)ruleList, (Rule)ruleElement2, false);
/*     */         } 
/* 296 */         ruleList.autoComplete(paramSentence);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   private static String autoCompleteTableAlias(Sentence paramSentence, boolean paramBoolean) {
/* 303 */     String str1 = paramSentence.getQuery();
/* 304 */     String str2 = paramSentence.getQueryUpper();
/* 305 */     byte b = 0;
/* 306 */     for (; b < str2.length(); b++) {
/* 307 */       char c = str2.charAt(b);
/* 308 */       if (c != '_' && !Character.isLetterOrDigit(c)) {
/*     */         break;
/*     */       }
/*     */     } 
/* 312 */     if (b == 0) {
/* 313 */       return str1;
/*     */     }
/* 315 */     String str3 = str2.substring(0, b);
/* 316 */     if ("SET".equals(str3) || ParserUtil.isKeyword(str3, false)) {
/* 317 */       return str1;
/*     */     }
/* 319 */     if (paramBoolean) {
/* 320 */       paramSentence.addAlias(str3, paramSentence.getLastTable());
/*     */     }
/* 322 */     HashMap hashMap = paramSentence.getAliases();
/* 323 */     if ((hashMap != null && hashMap.containsKey(str3)) || paramSentence
/* 324 */       .getLastTable() == null) {
/* 325 */       if (paramBoolean && str1.length() == str3.length()) {
/* 326 */         return str1;
/*     */       }
/* 328 */       str1 = str1.substring(str3.length());
/* 329 */       if (str1.length() == 0) {
/* 330 */         paramSentence.add(str3 + ".", ".", 0);
/*     */       }
/* 332 */       return str1;
/*     */     } 
/* 334 */     HashSet hashSet = paramSentence.getTables();
/* 335 */     if (hashSet != null) {
/* 336 */       String str = null;
/* 337 */       for (DbTableOrView dbTableOrView : hashSet) {
/*     */         
/* 339 */         String str4 = StringUtils.toUpperEnglish(dbTableOrView.getName());
/* 340 */         if (str3.startsWith(str4) && (str == null || str4
/* 341 */           .length() > str.length())) {
/* 342 */           paramSentence.setLastMatchedTable(dbTableOrView);
/* 343 */           str = str4; continue;
/* 344 */         }  if (str1.length() == 0 || str4.startsWith(str3)) {
/* 345 */           paramSentence.add(str4 + ".", str4
/* 346 */               .substring(str1.length()) + ".", 0);
/*     */         }
/*     */       } 
/*     */       
/* 350 */       if (str != null) {
/* 351 */         str1 = str1.substring(str.length());
/* 352 */         if (str1.length() == 0) {
/* 353 */           paramSentence.add(str3 + ".", ".", 0);
/*     */         }
/* 355 */         return str1;
/*     */       } 
/*     */     } 
/* 358 */     return str1;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\bnf\context\DbContextRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */