/*     */ package cn.hutool.db.sql;
/*     */ 
/*     */ import cn.hutool.core.util.StrUtil;
/*     */ import java.util.HashSet;
/*     */ import java.util.LinkedList;
/*     */ import java.util.Set;
/*     */ import java.util.StringTokenizer;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class SqlFormatter
/*     */ {
/*  16 */   private static final Set<String> BEGIN_CLAUSES = new HashSet<>();
/*  17 */   private static final Set<String> END_CLAUSES = new HashSet<>();
/*  18 */   private static final Set<String> LOGICAL = new HashSet<>();
/*  19 */   private static final Set<String> QUANTIFIERS = new HashSet<>();
/*  20 */   private static final Set<String> DML = new HashSet<>();
/*  21 */   private static final Set<String> MISC = new HashSet<>();
/*     */   
/*     */   static {
/*  24 */     BEGIN_CLAUSES.add("left");
/*  25 */     BEGIN_CLAUSES.add("right");
/*  26 */     BEGIN_CLAUSES.add("inner");
/*  27 */     BEGIN_CLAUSES.add("outer");
/*  28 */     BEGIN_CLAUSES.add("group");
/*  29 */     BEGIN_CLAUSES.add("order");
/*     */     
/*  31 */     END_CLAUSES.add("where");
/*  32 */     END_CLAUSES.add("set");
/*  33 */     END_CLAUSES.add("having");
/*  34 */     END_CLAUSES.add("join");
/*  35 */     END_CLAUSES.add("from");
/*  36 */     END_CLAUSES.add("by");
/*  37 */     END_CLAUSES.add("into");
/*  38 */     END_CLAUSES.add("union");
/*     */     
/*  40 */     LOGICAL.add("and");
/*  41 */     LOGICAL.add("or");
/*  42 */     LOGICAL.add("when");
/*  43 */     LOGICAL.add("else");
/*  44 */     LOGICAL.add("end");
/*     */     
/*  46 */     QUANTIFIERS.add("in");
/*  47 */     QUANTIFIERS.add("all");
/*  48 */     QUANTIFIERS.add("exists");
/*  49 */     QUANTIFIERS.add("some");
/*  50 */     QUANTIFIERS.add("any");
/*     */     
/*  52 */     DML.add("insert");
/*  53 */     DML.add("update");
/*  54 */     DML.add("delete");
/*     */     
/*  56 */     MISC.add("select");
/*  57 */     MISC.add("on");
/*     */   }
/*     */   
/*     */   private static final String indentString = "    ";
/*     */   private static final String initial = "\n    ";
/*     */   
/*     */   public static String format(String source) {
/*  64 */     return (new FormatProcess(source)).perform().trim();
/*     */   }
/*     */ 
/*     */   
/*     */   private static class FormatProcess
/*     */   {
/*     */     boolean beginLine = true;
/*     */     
/*     */     boolean afterBeginBeforeEnd = false;
/*     */     boolean afterByOrSetOrFromOrSelect = false;
/*     */     boolean afterOn = false;
/*     */     boolean afterBetween = false;
/*     */     boolean afterInsert = false;
/*  77 */     int inFunction = 0;
/*  78 */     int parensSinceSelect = 0;
/*  79 */     private final LinkedList<Integer> parenCounts = new LinkedList<>();
/*  80 */     private final LinkedList<Boolean> afterByOrFromOrSelects = new LinkedList<>();
/*     */     
/*  82 */     int indent = 1;
/*     */     
/*  84 */     StringBuffer result = new StringBuffer();
/*     */     StringTokenizer tokens;
/*     */     String lastToken;
/*     */     String token;
/*     */     String lcToken;
/*     */     
/*     */     public FormatProcess(String sql) {
/*  91 */       this.tokens = new StringTokenizer(sql, "()+*/-=<>'`\"[], \n\r\f\t", true);
/*     */     }
/*     */     
/*     */     public String perform() {
/*  95 */       this.result.append("\n    ");
/*     */       
/*  97 */       while (this.tokens.hasMoreTokens()) {
/*  98 */         this.token = this.tokens.nextToken();
/*  99 */         this.lcToken = this.token.toLowerCase();
/*     */         
/* 101 */         if ("'".equals(this.token)) {
/*     */           String t;
/*     */           do {
/* 104 */             t = this.tokens.nextToken();
/* 105 */             this.token += t;
/* 106 */           } while (!"'".equals(t) && this.tokens.hasMoreTokens());
/* 107 */         } else if ("\"".equals(this.token)) {
/*     */           String t;
/*     */           do {
/* 110 */             t = this.tokens.nextToken();
/* 111 */             this.token += t;
/* 112 */           } while (!"\"".equals(t));
/*     */         } 
/*     */         
/* 115 */         if (this.afterByOrSetOrFromOrSelect && ",".equals(this.token)) {
/* 116 */           commaAfterByOrFromOrSelect();
/* 117 */         } else if (this.afterOn && ",".equals(this.token)) {
/* 118 */           commaAfterOn();
/* 119 */         } else if ("(".equals(this.token)) {
/* 120 */           openParen();
/* 121 */         } else if (")".equals(this.token)) {
/* 122 */           closeParen();
/* 123 */         } else if (SqlFormatter.BEGIN_CLAUSES.contains(this.lcToken)) {
/* 124 */           beginNewClause();
/* 125 */         } else if (SqlFormatter.END_CLAUSES.contains(this.lcToken)) {
/* 126 */           endNewClause();
/* 127 */         } else if ("select".equals(this.lcToken)) {
/* 128 */           select();
/* 129 */         } else if (SqlFormatter.DML.contains(this.lcToken)) {
/* 130 */           updateOrInsertOrDelete();
/* 131 */         } else if ("values".equals(this.lcToken)) {
/* 132 */           values();
/* 133 */         } else if ("on".equals(this.lcToken)) {
/* 134 */           on();
/* 135 */         } else if (this.afterBetween && "and".equals(this.lcToken)) {
/* 136 */           misc();
/* 137 */           this.afterBetween = false;
/* 138 */         } else if (SqlFormatter.LOGICAL.contains(this.lcToken)) {
/* 139 */           logical();
/* 140 */         } else if (isWhitespace(this.token)) {
/* 141 */           white();
/*     */         } else {
/* 143 */           misc();
/*     */         } 
/*     */         
/* 146 */         if (false == isWhitespace(this.token)) {
/* 147 */           this.lastToken = this.lcToken;
/*     */         }
/*     */       } 
/*     */       
/* 151 */       return this.result.toString();
/*     */     }
/*     */     
/*     */     private void commaAfterOn() {
/* 155 */       out();
/* 156 */       this.indent--;
/* 157 */       newline();
/* 158 */       this.afterOn = false;
/* 159 */       this.afterByOrSetOrFromOrSelect = true;
/*     */     }
/*     */     
/*     */     private void commaAfterByOrFromOrSelect() {
/* 163 */       out();
/* 164 */       newline();
/*     */     }
/*     */     
/*     */     private void logical() {
/* 168 */       if ("end".equals(this.lcToken)) {
/* 169 */         this.indent--;
/*     */       }
/* 171 */       newline();
/* 172 */       out();
/* 173 */       this.beginLine = false;
/*     */     }
/*     */     
/*     */     private void on() {
/* 177 */       this.indent++;
/* 178 */       this.afterOn = true;
/* 179 */       newline();
/* 180 */       out();
/* 181 */       this.beginLine = false;
/*     */     }
/*     */     
/*     */     private void misc() {
/* 185 */       out();
/* 186 */       if ("between".equals(this.lcToken)) {
/* 187 */         this.afterBetween = true;
/*     */       }
/* 189 */       if (this.afterInsert) {
/* 190 */         newline();
/* 191 */         this.afterInsert = false;
/*     */       } else {
/* 193 */         this.beginLine = false;
/* 194 */         if ("case".equals(this.lcToken)) {
/* 195 */           this.indent++;
/*     */         }
/*     */       } 
/*     */     }
/*     */     
/*     */     private void white() {
/* 201 */       if (!this.beginLine) {
/* 202 */         this.result.append(" ");
/*     */       }
/*     */     }
/*     */     
/*     */     private void updateOrInsertOrDelete() {
/* 207 */       out();
/* 208 */       this.indent++;
/* 209 */       this.beginLine = false;
/* 210 */       if ("update".equals(this.lcToken)) {
/* 211 */         newline();
/*     */       }
/* 213 */       if ("insert".equals(this.lcToken)) {
/* 214 */         this.afterInsert = true;
/*     */       }
/*     */     }
/*     */     
/*     */     private void select() {
/* 219 */       out();
/* 220 */       this.indent++;
/* 221 */       newline();
/* 222 */       this.parenCounts.addLast(Integer.valueOf(this.parensSinceSelect));
/* 223 */       this.afterByOrFromOrSelects.addLast(Boolean.valueOf(this.afterByOrSetOrFromOrSelect));
/* 224 */       this.parensSinceSelect = 0;
/* 225 */       this.afterByOrSetOrFromOrSelect = true;
/*     */     }
/*     */     
/*     */     private void out() {
/* 229 */       this.result.append(this.token);
/*     */     }
/*     */     
/*     */     private void endNewClause() {
/* 233 */       if (!this.afterBeginBeforeEnd) {
/* 234 */         this.indent--;
/* 235 */         if (this.afterOn) {
/* 236 */           this.indent--;
/* 237 */           this.afterOn = false;
/*     */         } 
/* 239 */         newline();
/*     */       } 
/* 241 */       out();
/* 242 */       if (!"union".equals(this.lcToken)) {
/* 243 */         this.indent++;
/*     */       }
/* 245 */       newline();
/* 246 */       this.afterBeginBeforeEnd = false;
/* 247 */       this.afterByOrSetOrFromOrSelect = ("by".equals(this.lcToken) || "set".equals(this.lcToken) || "from".equals(this.lcToken));
/*     */     }
/*     */     
/*     */     private void beginNewClause() {
/* 251 */       if (!this.afterBeginBeforeEnd) {
/* 252 */         if (this.afterOn) {
/* 253 */           this.indent--;
/* 254 */           this.afterOn = false;
/*     */         } 
/* 256 */         this.indent--;
/* 257 */         newline();
/*     */       } 
/* 259 */       out();
/* 260 */       this.beginLine = false;
/* 261 */       this.afterBeginBeforeEnd = true;
/*     */     }
/*     */     
/*     */     private void values() {
/* 265 */       this.indent--;
/* 266 */       newline();
/* 267 */       out();
/* 268 */       this.indent++;
/* 269 */       newline();
/*     */     }
/*     */ 
/*     */     
/*     */     private void closeParen() {
/* 274 */       this.parensSinceSelect--;
/* 275 */       if (this.parensSinceSelect < 0) {
/* 276 */         this.indent--;
/* 277 */         this.parensSinceSelect = ((Integer)this.parenCounts.removeLast()).intValue();
/* 278 */         this.afterByOrSetOrFromOrSelect = ((Boolean)this.afterByOrFromOrSelects.removeLast()).booleanValue();
/*     */       } 
/* 280 */       if (this.inFunction > 0) {
/* 281 */         this.inFunction--;
/*     */       }
/* 283 */       else if (!this.afterByOrSetOrFromOrSelect) {
/* 284 */         this.indent--;
/* 285 */         newline();
/*     */       } 
/*     */       
/* 288 */       out();
/* 289 */       this.beginLine = false;
/*     */     }
/*     */     
/*     */     private void openParen() {
/* 293 */       if (isFunctionName(this.lastToken) || this.inFunction > 0) {
/* 294 */         this.inFunction++;
/*     */       }
/* 296 */       this.beginLine = false;
/* 297 */       if (this.inFunction > 0) {
/* 298 */         out();
/*     */       } else {
/* 300 */         out();
/* 301 */         if (!this.afterByOrSetOrFromOrSelect) {
/* 302 */           this.indent++;
/* 303 */           newline();
/* 304 */           this.beginLine = true;
/*     */         } 
/*     */       } 
/* 307 */       this.parensSinceSelect++;
/*     */     }
/*     */     
/*     */     private static boolean isFunctionName(String tok) {
/* 311 */       if (StrUtil.isEmpty(tok)) {
/* 312 */         return true;
/*     */       }
/* 314 */       char begin = tok.charAt(0);
/* 315 */       boolean isIdentifier = (Character.isJavaIdentifierStart(begin) || '"' == begin);
/* 316 */       return (isIdentifier && !SqlFormatter.LOGICAL.contains(tok) && !SqlFormatter.END_CLAUSES.contains(tok) && !SqlFormatter.QUANTIFIERS.contains(tok) && !SqlFormatter.DML.contains(tok) && !SqlFormatter.MISC.contains(tok));
/*     */     }
/*     */     
/*     */     private static boolean isWhitespace(String token) {
/* 320 */       return " \n\r\f\t".contains(token);
/*     */     }
/*     */     
/*     */     private void newline() {
/* 324 */       this.result.append("\n");
/* 325 */       for (int i = 0; i < this.indent; i++) {
/* 326 */         this.result.append("    ");
/*     */       }
/* 328 */       this.beginLine = true;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\db\sql\SqlFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */