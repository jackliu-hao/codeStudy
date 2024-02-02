/*     */ package org.h2.bnf;
/*     */ 
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.SQLException;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.StringTokenizer;
/*     */ import org.h2.bnf.context.DbContextRule;
/*     */ import org.h2.command.dml.Help;
/*     */ import org.h2.tools.Csv;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.util.Utils;
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
/*     */ public class Bnf
/*     */ {
/*  33 */   private final HashMap<String, RuleHead> ruleMap = new HashMap<>();
/*     */   
/*     */   private String syntax;
/*     */   
/*     */   private String currentToken;
/*     */   
/*     */   private String[] tokens;
/*     */   
/*     */   private char firstChar;
/*     */   
/*     */   private int index;
/*     */   
/*     */   private Rule lastRepeat;
/*     */   
/*     */   private ArrayList<RuleHead> statements;
/*     */   
/*     */   private String currentTopic;
/*     */   
/*     */   public static Bnf getInstance(Reader paramReader) throws SQLException, IOException {
/*  52 */     Bnf bnf = new Bnf();
/*  53 */     if (paramReader == null) {
/*  54 */       byte[] arrayOfByte = Utils.getResource("/org/h2/res/help.csv");
/*  55 */       paramReader = new InputStreamReader(new ByteArrayInputStream(arrayOfByte));
/*     */     } 
/*  57 */     bnf.parse(paramReader);
/*  58 */     return bnf;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void addAlias(String paramString1, String paramString2) {
/*  68 */     RuleHead ruleHead = this.ruleMap.get(paramString2);
/*  69 */     this.ruleMap.put(paramString1, ruleHead);
/*     */   }
/*     */   
/*     */   private void addFixedRule(String paramString, int paramInt) {
/*  73 */     RuleFixed ruleFixed = new RuleFixed(paramInt);
/*  74 */     addRule(paramString, "Fixed", ruleFixed);
/*     */   }
/*     */   
/*     */   private RuleHead addRule(String paramString1, String paramString2, Rule paramRule) {
/*  78 */     RuleHead ruleHead = new RuleHead(paramString2, paramString1, paramRule);
/*  79 */     String str = StringUtils.toLowerEnglish(paramString1.trim().replace(' ', '_'));
/*  80 */     if (this.ruleMap.putIfAbsent(str, ruleHead) != null) {
/*  81 */       throw new AssertionError("already exists: " + paramString1);
/*     */     }
/*  83 */     return ruleHead;
/*     */   }
/*     */   
/*     */   private void parse(Reader paramReader) throws SQLException, IOException {
/*  87 */     Rule rule = null;
/*  88 */     this.statements = new ArrayList<>();
/*  89 */     Csv csv = new Csv();
/*  90 */     csv.setLineCommentCharacter('#');
/*  91 */     ResultSet resultSet = csv.read(paramReader, null);
/*  92 */     while (resultSet.next()) {
/*  93 */       String str1 = resultSet.getString("SECTION").trim();
/*  94 */       if (str1.startsWith("System")) {
/*     */         continue;
/*     */       }
/*  97 */       String str2 = resultSet.getString("TOPIC");
/*  98 */       this.syntax = Help.stripAnnotationsFromSyntax(resultSet.getString("SYNTAX"));
/*  99 */       this.currentTopic = str1;
/* 100 */       this.tokens = tokenize();
/* 101 */       this.index = 0;
/* 102 */       Rule rule1 = parseRule();
/* 103 */       if (str1.startsWith("Command")) {
/* 104 */         rule1 = new RuleList(rule1, new RuleElement(";\n\n", this.currentTopic), false);
/*     */       }
/* 106 */       RuleHead ruleHead = addRule(str2, str1, rule1);
/* 107 */       if (str1.startsWith("Function")) {
/* 108 */         if (rule == null) {
/* 109 */           rule = rule1; continue;
/*     */         } 
/* 111 */         rule = new RuleList(rule1, rule, true); continue;
/*     */       } 
/* 113 */       if (str1.startsWith("Commands")) {
/* 114 */         this.statements.add(ruleHead);
/*     */       }
/*     */     } 
/* 117 */     addRule("@func@", "Function", rule);
/* 118 */     addFixedRule("@ymd@", 0);
/* 119 */     addFixedRule("@hms@", 1);
/* 120 */     addFixedRule("@nanos@", 2);
/* 121 */     addFixedRule("anything_except_single_quote", 3);
/* 122 */     addFixedRule("single_character", 3);
/* 123 */     addFixedRule("anything_except_double_quote", 4);
/* 124 */     addFixedRule("anything_until_end_of_line", 5);
/* 125 */     addFixedRule("anything_until_comment_start_or_end", 6);
/* 126 */     addFixedRule("anything_except_two_dollar_signs", 8);
/* 127 */     addFixedRule("anything", 7);
/* 128 */     addFixedRule("@hex_start@", 10);
/* 129 */     addFixedRule("@concat@", 11);
/* 130 */     addFixedRule("@az_@", 12);
/* 131 */     addFixedRule("@af@", 13);
/* 132 */     addFixedRule("@digit@", 14);
/* 133 */     addFixedRule("@open_bracket@", 15);
/* 134 */     addFixedRule("@close_bracket@", 16);
/* 135 */     addFixedRule("json_text", 17);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void visit(BnfVisitor paramBnfVisitor, String paramString) {
/* 145 */     this.syntax = paramString;
/* 146 */     this.tokens = tokenize();
/* 147 */     this.index = 0;
/* 148 */     Rule rule = parseRule();
/* 149 */     rule.setLinks(this.ruleMap);
/* 150 */     rule.accept(paramBnfVisitor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static boolean startWithSpace(String paramString) {
/* 160 */     return (paramString.length() > 0 && Character.isWhitespace(paramString.charAt(0)));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getRuleMapKey(String paramString) {
/* 170 */     StringBuilder stringBuilder = new StringBuilder();
/* 171 */     for (char c : paramString.toCharArray()) {
/* 172 */       if (Character.isUpperCase(c)) {
/* 173 */         stringBuilder.append('_').append(Character.toLowerCase(c));
/*     */       } else {
/* 175 */         stringBuilder.append(c);
/*     */       } 
/*     */     } 
/* 178 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RuleHead getRuleHead(String paramString) {
/* 188 */     return this.ruleMap.get(paramString);
/*     */   }
/*     */   
/*     */   private Rule parseRule() {
/* 192 */     read();
/* 193 */     return parseOr();
/*     */   }
/*     */   
/*     */   private Rule parseOr() {
/* 197 */     Rule rule = parseList();
/* 198 */     if (this.firstChar == '|') {
/* 199 */       read();
/* 200 */       rule = new RuleList(rule, parseOr(), true);
/*     */     } 
/* 202 */     this.lastRepeat = rule;
/* 203 */     return rule;
/*     */   }
/*     */   
/*     */   private Rule parseList() {
/* 207 */     Rule rule = parseToken();
/* 208 */     if (this.firstChar != '|' && this.firstChar != ']' && this.firstChar != '}' && this.firstChar != '\000')
/*     */     {
/* 210 */       rule = new RuleList(rule, parseList(), false);
/*     */     }
/* 212 */     this.lastRepeat = rule;
/* 213 */     return rule;
/*     */   }
/*     */   private RuleExtension parseExtension(boolean paramBoolean) {
/*     */     Rule rule;
/* 217 */     read();
/*     */     
/* 219 */     if (this.firstChar == '[') {
/* 220 */       read();
/* 221 */       rule = parseOr();
/* 222 */       rule = new RuleOptional(rule);
/* 223 */       if (this.firstChar != ']') {
/* 224 */         throw new AssertionError("expected ], got " + this.currentToken + " syntax:" + this.syntax);
/*     */       }
/* 226 */     } else if (this.firstChar == '{') {
/* 227 */       read();
/* 228 */       rule = parseOr();
/* 229 */       if (this.firstChar != '}') {
/* 230 */         throw new AssertionError("expected }, got " + this.currentToken + " syntax:" + this.syntax);
/*     */       }
/*     */     } else {
/* 233 */       rule = parseOr();
/*     */     } 
/* 235 */     return new RuleExtension(rule, paramBoolean);
/*     */   }
/*     */   
/*     */   private Rule parseToken() {
/*     */     Rule rule;
/* 240 */     if ((this.firstChar >= 'A' && this.firstChar <= 'Z') || (this.firstChar >= 'a' && this.firstChar <= 'z')) {
/*     */ 
/*     */       
/* 243 */       rule = new RuleElement(this.currentToken, this.currentTopic);
/* 244 */     } else if (this.firstChar == '[') {
/* 245 */       read();
/* 246 */       rule = parseOr();
/* 247 */       rule = new RuleOptional(rule);
/* 248 */       if (this.firstChar != ']') {
/* 249 */         throw new AssertionError("expected ], got " + this.currentToken + " syntax:" + this.syntax);
/*     */       }
/* 251 */     } else if (this.firstChar == '{') {
/* 252 */       read();
/* 253 */       rule = parseOr();
/* 254 */       if (this.firstChar != '}') {
/* 255 */         throw new AssertionError("expected }, got " + this.currentToken + " syntax:" + this.syntax);
/*     */       }
/* 257 */     } else if (this.firstChar == '@') {
/* 258 */       if ("@commaDots@".equals(this.currentToken)) {
/* 259 */         RuleList ruleList = new RuleList(new RuleElement(",", this.currentTopic), this.lastRepeat, false);
/* 260 */         rule = new RuleRepeat(ruleList, true);
/* 261 */       } else if ("@dots@".equals(this.currentToken)) {
/* 262 */         rule = new RuleRepeat(this.lastRepeat, false);
/* 263 */       } else if ("@c@".equals(this.currentToken)) {
/* 264 */         rule = parseExtension(true);
/* 265 */       } else if ("@h2@".equals(this.currentToken)) {
/* 266 */         rule = parseExtension(false);
/*     */       } else {
/* 268 */         rule = new RuleElement(this.currentToken, this.currentTopic);
/*     */       } 
/*     */     } else {
/* 271 */       rule = new RuleElement(this.currentToken, this.currentTopic);
/*     */     } 
/* 273 */     this.lastRepeat = rule;
/* 274 */     read();
/* 275 */     return rule;
/*     */   }
/*     */   
/*     */   private void read() {
/* 279 */     if (this.index < this.tokens.length) {
/* 280 */       this.currentToken = this.tokens[this.index++];
/* 281 */       this.firstChar = this.currentToken.charAt(0);
/*     */     } else {
/* 283 */       this.currentToken = "";
/* 284 */       this.firstChar = Character.MIN_VALUE;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 290 */     StringBuilder stringBuilder = new StringBuilder(); int i;
/* 291 */     for (i = 0; i < this.index; i++) {
/* 292 */       stringBuilder.append(this.tokens[i]).append(' ');
/*     */     }
/* 294 */     stringBuilder.append("[*]");
/* 295 */     for (i = this.index; i < this.tokens.length; i++) {
/* 296 */       stringBuilder.append(' ').append(this.tokens[i]);
/*     */     }
/* 298 */     return stringBuilder.toString();
/*     */   }
/*     */   
/*     */   private String[] tokenize() {
/* 302 */     ArrayList<String> arrayList = new ArrayList();
/* 303 */     this.syntax = StringUtils.replaceAll(this.syntax, "yyyy-MM-dd", "@ymd@");
/* 304 */     this.syntax = StringUtils.replaceAll(this.syntax, "hh:mm:ss", "@hms@");
/* 305 */     this.syntax = StringUtils.replaceAll(this.syntax, "hh:mm", "@hms@");
/* 306 */     this.syntax = StringUtils.replaceAll(this.syntax, "mm:ss", "@hms@");
/* 307 */     this.syntax = StringUtils.replaceAll(this.syntax, "nnnnnnnnn", "@nanos@");
/* 308 */     this.syntax = StringUtils.replaceAll(this.syntax, "function", "@func@");
/* 309 */     this.syntax = StringUtils.replaceAll(this.syntax, "0x", "@hexStart@");
/* 310 */     this.syntax = StringUtils.replaceAll(this.syntax, ",...", "@commaDots@");
/* 311 */     this.syntax = StringUtils.replaceAll(this.syntax, "...", "@dots@");
/* 312 */     this.syntax = StringUtils.replaceAll(this.syntax, "||", "@concat@");
/* 313 */     this.syntax = StringUtils.replaceAll(this.syntax, "a-z|_", "@az_@");
/* 314 */     this.syntax = StringUtils.replaceAll(this.syntax, "A-Z|_", "@az_@");
/* 315 */     this.syntax = StringUtils.replaceAll(this.syntax, "A-F", "@af@");
/* 316 */     this.syntax = StringUtils.replaceAll(this.syntax, "0-9", "@digit@");
/* 317 */     this.syntax = StringUtils.replaceAll(this.syntax, "'['", "@openBracket@");
/* 318 */     this.syntax = StringUtils.replaceAll(this.syntax, "']'", "@closeBracket@");
/* 319 */     StringTokenizer stringTokenizer = getTokenizer(this.syntax);
/* 320 */     while (stringTokenizer.hasMoreTokens()) {
/* 321 */       String str = stringTokenizer.nextToken();
/*     */       
/* 323 */       str = StringUtils.cache(str);
/* 324 */       if (str.length() == 1 && 
/* 325 */         " \r\n".indexOf(str.charAt(0)) >= 0) {
/*     */         continue;
/*     */       }
/*     */       
/* 329 */       arrayList.add(str);
/*     */     } 
/* 331 */     return arrayList.<String>toArray(new String[0]);
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
/*     */   public HashMap<String, String> getNextTokenList(String paramString) {
/* 347 */     Sentence sentence = new Sentence();
/* 348 */     sentence.setQuery(paramString);
/*     */     try {
/* 350 */       for (RuleHead ruleHead : this.statements) {
/* 351 */         if (!ruleHead.getSection().startsWith("Commands")) {
/*     */           continue;
/*     */         }
/* 354 */         sentence.start();
/* 355 */         if (ruleHead.getRule().autoComplete(sentence)) {
/*     */           break;
/*     */         }
/*     */       } 
/* 359 */     } catch (IllegalStateException illegalStateException) {}
/*     */ 
/*     */     
/* 362 */     return sentence.getNext();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void linkStatements() {
/* 370 */     for (RuleHead ruleHead : this.ruleMap.values()) {
/* 371 */       ruleHead.getRule().setLinks(this.ruleMap);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void updateTopic(String paramString, DbContextRule paramDbContextRule) {
/* 383 */     paramString = StringUtils.toLowerEnglish(paramString);
/* 384 */     RuleHead ruleHead = this.ruleMap.get(paramString);
/* 385 */     if (ruleHead == null) {
/* 386 */       ruleHead = new RuleHead("db", paramString, (Rule)paramDbContextRule);
/* 387 */       this.ruleMap.put(paramString, ruleHead);
/* 388 */       this.statements.add(ruleHead);
/*     */     } else {
/* 390 */       ruleHead.setRule((Rule)paramDbContextRule);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public ArrayList<RuleHead> getStatements() {
/* 400 */     return this.statements;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static StringTokenizer getTokenizer(String paramString) {
/* 410 */     return new StringTokenizer(paramString, " [](){}|.,\r\n<>:-+*/=\"!'$", true);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\bnf\Bnf.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */