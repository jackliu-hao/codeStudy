/*     */ package org.h2.bnf;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ 
/*     */ 
/*     */ 
/*     */ public class RuleFixed
/*     */   implements Rule
/*     */ {
/*     */   public static final int YMD = 0;
/*     */   public static final int HMS = 1;
/*     */   public static final int NANOS = 2;
/*     */   public static final int ANY_EXCEPT_SINGLE_QUOTE = 3;
/*     */   public static final int ANY_EXCEPT_DOUBLE_QUOTE = 4;
/*     */   public static final int ANY_UNTIL_EOL = 5;
/*     */   public static final int ANY_UNTIL_END = 6;
/*     */   public static final int ANY_WORD = 7;
/*     */   public static final int ANY_EXCEPT_2_DOLLAR = 8;
/*     */   public static final int HEX_START = 10;
/*     */   public static final int CONCAT = 11;
/*     */   public static final int AZ_UNDERSCORE = 12;
/*     */   public static final int AF = 13;
/*     */   public static final int DIGIT = 14;
/*     */   public static final int OPEN_BRACKET = 15;
/*     */   public static final int CLOSE_BRACKET = 16;
/*     */   public static final int JSON_TEXT = 17;
/*     */   private final int type;
/*     */   
/*     */   RuleFixed(int paramInt) {
/*  30 */     this.type = paramInt;
/*     */   }
/*     */ 
/*     */   
/*     */   public void accept(BnfVisitor paramBnfVisitor) {
/*  35 */     paramBnfVisitor.visitRuleFixed(this.type);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLinks(HashMap<String, RuleHead> paramHashMap) {}
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean autoComplete(Sentence paramSentence) {
/*  45 */     paramSentence.stopIfRequired();
/*  46 */     String str1 = paramSentence.getQuery();
/*  47 */     String str2 = str1;
/*  48 */     boolean bool = false;
/*  49 */     switch (this.type) {
/*     */       case 0:
/*  51 */         while (str2.length() > 0 && "0123456789-".indexOf(str2.charAt(0)) >= 0) {
/*  52 */           str2 = str2.substring(1);
/*     */         }
/*  54 */         if (str2.length() == 0) {
/*  55 */           paramSentence.add("2006-01-01", "1", 1);
/*     */         }
/*     */         
/*  58 */         bool = true;
/*     */         break;
/*     */       case 1:
/*  61 */         while (str2.length() > 0 && "0123456789:".indexOf(str2.charAt(0)) >= 0) {
/*  62 */           str2 = str2.substring(1);
/*     */         }
/*  64 */         if (str2.length() == 0) {
/*  65 */           paramSentence.add("12:00:00", "1", 1);
/*     */         }
/*     */         break;
/*     */       case 2:
/*  69 */         while (str2.length() > 0 && Character.isDigit(str2.charAt(0))) {
/*  70 */           str2 = str2.substring(1);
/*     */         }
/*  72 */         if (str2.length() == 0) {
/*  73 */           paramSentence.add("nanoseconds", "0", 1);
/*     */         }
/*  75 */         bool = true;
/*     */         break;
/*     */       case 3:
/*     */         while (true) {
/*  79 */           if (str2.length() > 0 && str2.charAt(0) != '\'') {
/*  80 */             str2 = str2.substring(1); continue;
/*     */           } 
/*  82 */           if (str2.startsWith("''")) {
/*  83 */             str2 = str2.substring(2);
/*     */             continue;
/*     */           } 
/*     */           break;
/*     */         } 
/*  88 */         if (str2.length() == 0) {
/*  89 */           paramSentence.add("anything", "Hello World", 1);
/*  90 */           paramSentence.add("'", "'", 1);
/*     */         } 
/*     */         break;
/*     */       case 8:
/*  94 */         while (str2.length() > 0 && !str2.startsWith("$$")) {
/*  95 */           str2 = str2.substring(1);
/*     */         }
/*  97 */         if (str2.length() == 0) {
/*  98 */           paramSentence.add("anything", "Hello World", 1);
/*  99 */           paramSentence.add("$$", "$$", 1);
/*     */         } 
/*     */         break;
/*     */       case 4:
/*     */         while (true) {
/* 104 */           if (str2.length() > 0 && str2.charAt(0) != '"') {
/* 105 */             str2 = str2.substring(1); continue;
/*     */           } 
/* 107 */           if (str2.startsWith("\"\"")) {
/* 108 */             str2 = str2.substring(2);
/*     */             continue;
/*     */           } 
/*     */           break;
/*     */         } 
/* 113 */         if (str2.length() == 0) {
/* 114 */           paramSentence.add("anything", "identifier", 1);
/* 115 */           paramSentence.add("\"", "\"", 1);
/*     */         } 
/*     */         break;
/*     */       case 7:
/*     */       case 17:
/* 120 */         while (str2.length() > 0 && !Bnf.startWithSpace(str2)) {
/* 121 */           str2 = str2.substring(1);
/*     */         }
/* 123 */         if (str2.length() == 0) {
/* 124 */           paramSentence.add("anything", "anything", 1);
/*     */         }
/*     */         break;
/*     */       case 10:
/* 128 */         if (str2.startsWith("0X") || str2.startsWith("0x")) {
/* 129 */           str2 = str2.substring(2); break;
/* 130 */         }  if ("0".equals(str2)) {
/* 131 */           paramSentence.add("0x", "x", 1); break;
/* 132 */         }  if (str2.length() == 0) {
/* 133 */           paramSentence.add("0x", "0x", 1);
/*     */         }
/*     */         break;
/*     */       case 11:
/* 137 */         if (str2.equals("|")) {
/* 138 */           paramSentence.add("||", "|", 1);
/* 139 */         } else if (str2.startsWith("||")) {
/* 140 */           str2 = str2.substring(2);
/* 141 */         } else if (str2.length() == 0) {
/* 142 */           paramSentence.add("||", "||", 1);
/*     */         } 
/* 144 */         bool = true;
/*     */         break;
/*     */       case 12:
/* 147 */         if (str2.length() > 0 && (
/* 148 */           Character.isLetter(str2.charAt(0)) || str2.charAt(0) == '_')) {
/* 149 */           str2 = str2.substring(1);
/*     */         }
/* 151 */         if (str2.length() == 0) {
/* 152 */           paramSentence.add("character", "A", 1);
/*     */         }
/*     */         break;
/*     */       case 13:
/* 156 */         if (str2.length() > 0) {
/* 157 */           char c = Character.toUpperCase(str2.charAt(0));
/* 158 */           if (c >= 'A' && c <= 'F') {
/* 159 */             str2 = str2.substring(1);
/*     */           }
/*     */         } 
/* 162 */         if (str2.length() == 0) {
/* 163 */           paramSentence.add("hex character", "0A", 1);
/*     */         }
/*     */         break;
/*     */       case 14:
/* 167 */         if (str2.length() > 0 && Character.isDigit(str2.charAt(0))) {
/* 168 */           str2 = str2.substring(1);
/*     */         }
/* 170 */         if (str2.length() == 0) {
/* 171 */           paramSentence.add("digit", "1", 1);
/*     */         }
/*     */         break;
/*     */       case 15:
/* 175 */         if (str2.length() == 0) {
/* 176 */           paramSentence.add("[", "[", 1);
/* 177 */         } else if (str2.charAt(0) == '[') {
/* 178 */           str2 = str2.substring(1);
/*     */         } 
/* 180 */         bool = true;
/*     */         break;
/*     */       case 16:
/* 183 */         if (str2.length() == 0) {
/* 184 */           paramSentence.add("]", "]", 1);
/* 185 */         } else if (str2.charAt(0) == ']') {
/* 186 */           str2 = str2.substring(1);
/*     */         } 
/* 188 */         bool = true;
/*     */         break;
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*     */       default:
/* 195 */         throw new AssertionError("type=" + this.type);
/*     */     } 
/* 197 */     if (!str2.equals(str1)) {
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 202 */       if (bool) {
/* 203 */         while (Bnf.startWithSpace(str2)) {
/* 204 */           str2 = str2.substring(1);
/*     */         }
/*     */       }
/* 207 */       paramSentence.setQuery(str2);
/* 208 */       return true;
/*     */     } 
/* 210 */     return false;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 215 */     return "#" + this.type;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\bnf\RuleFixed.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */