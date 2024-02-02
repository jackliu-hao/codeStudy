/*     */ package io.undertow.servlet.compat.rewrite;
/*     */ 
/*     */ import io.undertow.servlet.UndertowServletLogger;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.util.ArrayList;
/*     */ import java.util.HashMap;
/*     */ import java.util.Map;
/*     */ import java.util.StringTokenizer;
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
/*     */ public class RewriteConfigFactory
/*     */ {
/*     */   public static RewriteConfig build(InputStream inputStream) {
/*  41 */     BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
/*     */     
/*     */     try {
/*  44 */       return parse(reader);
/*     */     } finally {
/*     */       try {
/*  47 */         reader.close();
/*  48 */       } catch (IOException iOException) {}
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static RewriteConfig parse(BufferedReader reader) {
/*  55 */     ArrayList<RewriteRule> rules = new ArrayList<>();
/*  56 */     ArrayList<RewriteCond> conditions = new ArrayList<>();
/*  57 */     Map<String, RewriteMap> maps = new HashMap<>();
/*     */     while (true) {
/*     */       try {
/*  60 */         String line = reader.readLine();
/*  61 */         if (line == null) {
/*     */           break;
/*     */         }
/*  64 */         Object result = parse(line);
/*  65 */         if (result instanceof RewriteRule) {
/*  66 */           RewriteRule rule = (RewriteRule)result;
/*  67 */           if (UndertowServletLogger.ROOT_LOGGER.isDebugEnabled())
/*  68 */             UndertowServletLogger.ROOT_LOGGER.debug("Add rule with pattern " + rule.getPatternString() + " and substitution " + rule
/*  69 */                 .getSubstitutionString()); 
/*     */           int j;
/*  71 */           for (j = conditions.size() - 1; j > 0; j--) {
/*  72 */             if (((RewriteCond)conditions.get(j - 1)).isOrnext()) {
/*  73 */               ((RewriteCond)conditions.get(j)).setOrnext(true);
/*     */             }
/*     */           } 
/*  76 */           for (j = 0; j < conditions.size(); j++) {
/*  77 */             if (UndertowServletLogger.ROOT_LOGGER.isDebugEnabled()) {
/*  78 */               RewriteCond cond = conditions.get(j);
/*  79 */               UndertowServletLogger.ROOT_LOGGER.debug("Add condition " + cond.getCondPattern() + " test " + cond
/*  80 */                   .getTestString() + " to rule with pattern " + rule
/*  81 */                   .getPatternString() + " and substitution " + rule
/*  82 */                   .getSubstitutionString() + (cond.isOrnext() ? " [OR]" : "") + (
/*  83 */                   cond.isNocase() ? " [NC]" : ""));
/*     */             } 
/*  85 */             rule.addCondition(conditions.get(j));
/*     */           } 
/*  87 */           conditions.clear();
/*  88 */           rules.add(rule); continue;
/*  89 */         }  if (result instanceof RewriteCond) {
/*  90 */           conditions.add((RewriteCond)result); continue;
/*  91 */         }  if (result instanceof Object[]) {
/*  92 */           String mapName = (String)((Object[])result)[0];
/*  93 */           RewriteMap map = (RewriteMap)((Object[])result)[1];
/*  94 */           maps.put(mapName, map);
/*     */         
/*     */         }
/*     */       
/*     */       }
/*  99 */       catch (IOException e) {
/* 100 */         UndertowServletLogger.ROOT_LOGGER.errorReadingRewriteConfiguration(e);
/*     */       } 
/*     */     } 
/* 103 */     RewriteRule[] rulesArray = rules.<RewriteRule>toArray(new RewriteRule[0]);
/*     */ 
/*     */     
/* 106 */     for (int i = 0; i < rulesArray.length; i++) {
/* 107 */       rulesArray[i].parse(maps);
/*     */     }
/* 109 */     return new RewriteConfig(rulesArray, maps);
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
/*     */   private static Object parse(String line) {
/* 122 */     StringTokenizer tokenizer = new StringTokenizer(line);
/* 123 */     if (tokenizer.hasMoreTokens()) {
/* 124 */       String token = tokenizer.nextToken();
/* 125 */       if (token.equals("RewriteCond")) {
/*     */         
/* 127 */         RewriteCond condition = new RewriteCond();
/* 128 */         if (tokenizer.countTokens() < 2) {
/* 129 */           throw UndertowServletLogger.ROOT_LOGGER.invalidRewriteConfiguration(line);
/*     */         }
/* 131 */         condition.setTestString(tokenizer.nextToken());
/* 132 */         condition.setCondPattern(tokenizer.nextToken());
/* 133 */         if (tokenizer.hasMoreTokens()) {
/* 134 */           String flags = tokenizer.nextToken();
/* 135 */           if (flags.startsWith("[") && flags.endsWith("]")) {
/* 136 */             flags = flags.substring(1, flags.length() - 1);
/*     */           }
/* 138 */           StringTokenizer flagsTokenizer = new StringTokenizer(flags, ",");
/* 139 */           while (flagsTokenizer.hasMoreElements()) {
/* 140 */             parseCondFlag(line, condition, flagsTokenizer.nextToken());
/*     */           }
/*     */         } 
/* 143 */         return condition;
/* 144 */       }  if (token.equals("RewriteRule")) {
/*     */         
/* 146 */         RewriteRule rule = new RewriteRule();
/* 147 */         if (tokenizer.countTokens() < 2) {
/* 148 */           throw UndertowServletLogger.ROOT_LOGGER.invalidRewriteConfiguration(line);
/*     */         }
/* 150 */         rule.setPatternString(tokenizer.nextToken());
/* 151 */         rule.setSubstitutionString(tokenizer.nextToken());
/* 152 */         if (tokenizer.hasMoreTokens()) {
/* 153 */           String flags = tokenizer.nextToken();
/* 154 */           if (flags.startsWith("[") && flags.endsWith("]")) {
/* 155 */             flags = flags.substring(1, flags.length() - 1);
/*     */           }
/* 157 */           StringTokenizer flagsTokenizer = new StringTokenizer(flags, ",");
/* 158 */           while (flagsTokenizer.hasMoreElements()) {
/* 159 */             parseRuleFlag(line, rule, flagsTokenizer.nextToken());
/*     */           }
/*     */         } 
/* 162 */         return rule;
/* 163 */       }  if (token.equals("RewriteMap")) {
/*     */         
/* 165 */         if (tokenizer.countTokens() < 2) {
/* 166 */           throw UndertowServletLogger.ROOT_LOGGER.invalidRewriteConfiguration(line);
/*     */         }
/* 168 */         String name = tokenizer.nextToken();
/* 169 */         String rewriteMapClassName = tokenizer.nextToken();
/* 170 */         RewriteMap map = null;
/*     */         try {
/* 172 */           map = (RewriteMap)Class.forName(rewriteMapClassName).newInstance();
/* 173 */         } catch (Exception e) {
/* 174 */           throw UndertowServletLogger.ROOT_LOGGER.invalidRewriteMap(rewriteMapClassName);
/*     */         } 
/* 176 */         if (tokenizer.hasMoreTokens()) {
/* 177 */           map.setParameters(tokenizer.nextToken());
/*     */         }
/* 179 */         Object[] result = new Object[2];
/* 180 */         result[0] = name;
/* 181 */         result[1] = map;
/* 182 */         return result;
/* 183 */       }  if (!token.startsWith("#"))
/*     */       {
/*     */         
/* 186 */         throw UndertowServletLogger.ROOT_LOGGER.invalidRewriteConfiguration(line);
/*     */       }
/*     */     } 
/* 189 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected static void parseCondFlag(String line, RewriteCond condition, String flag) {
/* 200 */     if (flag.equals("NC") || flag.equals("nocase")) {
/* 201 */       condition.setNocase(true);
/* 202 */     } else if (flag.equals("OR") || flag.equals("ornext")) {
/* 203 */       condition.setOrnext(true);
/*     */     } else {
/* 205 */       throw UndertowServletLogger.ROOT_LOGGER.invalidRewriteFlags(line, flag);
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
/*     */   protected static void parseRuleFlag(String line, RewriteRule rule, String flag) {
/* 217 */     if (flag.equals("chain") || flag.equals("C")) {
/* 218 */       rule.setChain(true);
/* 219 */     } else if (flag.startsWith("cookie=") || flag.startsWith("CO=")) {
/* 220 */       rule.setCookie(true);
/* 221 */       if (flag.startsWith("cookie")) {
/* 222 */         flag = flag.substring("cookie=".length());
/* 223 */       } else if (flag.startsWith("CO=")) {
/* 224 */         flag = flag.substring("CO=".length());
/*     */       } 
/* 226 */       StringTokenizer tokenizer = new StringTokenizer(flag, ":");
/* 227 */       if (tokenizer.countTokens() < 2) {
/* 228 */         throw UndertowServletLogger.ROOT_LOGGER.invalidRewriteFlags(line);
/*     */       }
/* 230 */       rule.setCookieName(tokenizer.nextToken());
/* 231 */       rule.setCookieValue(tokenizer.nextToken());
/* 232 */       if (tokenizer.hasMoreTokens()) {
/* 233 */         rule.setCookieDomain(tokenizer.nextToken());
/*     */       }
/* 235 */       if (tokenizer.hasMoreTokens()) {
/*     */         try {
/* 237 */           rule.setCookieLifetime(Integer.parseInt(tokenizer.nextToken()));
/* 238 */         } catch (NumberFormatException e) {
/* 239 */           throw UndertowServletLogger.ROOT_LOGGER.invalidRewriteFlags(line);
/*     */         } 
/*     */       }
/* 242 */       if (tokenizer.hasMoreTokens()) {
/* 243 */         rule.setCookiePath(tokenizer.nextToken());
/*     */       }
/* 245 */       if (tokenizer.hasMoreTokens()) {
/* 246 */         rule.setCookieSecure(Boolean.parseBoolean(tokenizer.nextToken()));
/*     */       }
/* 248 */       if (tokenizer.hasMoreTokens()) {
/* 249 */         rule.setCookieHttpOnly(Boolean.parseBoolean(tokenizer.nextToken()));
/*     */       }
/* 251 */     } else if (flag.startsWith("env=") || flag.startsWith("E=")) {
/* 252 */       rule.setEnv(true);
/* 253 */       if (flag.startsWith("env=")) {
/* 254 */         flag = flag.substring("env=".length());
/* 255 */       } else if (flag.startsWith("E=")) {
/* 256 */         flag = flag.substring("E=".length());
/*     */       } 
/* 258 */       int pos = flag.indexOf(':');
/* 259 */       if (pos == -1 || pos + 1 == flag.length()) {
/* 260 */         throw UndertowServletLogger.ROOT_LOGGER.invalidRewriteFlags(line);
/*     */       }
/* 262 */       rule.addEnvName(flag.substring(0, pos));
/* 263 */       rule.addEnvValue(flag.substring(pos + 1));
/* 264 */     } else if (flag.startsWith("forbidden") || flag.startsWith("F")) {
/* 265 */       rule.setForbidden(true);
/* 266 */     } else if (flag.startsWith("gone") || flag.startsWith("G")) {
/* 267 */       rule.setGone(true);
/* 268 */     } else if (flag.startsWith("host") || flag.startsWith("H")) {
/* 269 */       rule.setHost(true);
/* 270 */     } else if (flag.startsWith("last") || flag.startsWith("L")) {
/* 271 */       rule.setLast(true);
/* 272 */     } else if (flag.startsWith("next") || flag.startsWith("N")) {
/* 273 */       rule.setNext(true);
/* 274 */     } else if (flag.startsWith("nocase") || flag.startsWith("NC")) {
/* 275 */       rule.setNocase(true);
/* 276 */     } else if (flag.startsWith("noescape") || flag.startsWith("NE")) {
/* 277 */       rule.setNoescape(true);
/*     */ 
/*     */     
/*     */     }
/* 281 */     else if (flag.startsWith("qsappend") || flag.startsWith("QSA")) {
/* 282 */       rule.setQsappend(true);
/* 283 */     } else if (flag.startsWith("redirect") || flag.startsWith("R")) {
/* 284 */       if (flag.startsWith("redirect=")) {
/* 285 */         flag = flag.substring("redirect=".length());
/* 286 */         rule.setRedirect(true);
/* 287 */         rule.setRedirectCode(Integer.parseInt(flag));
/* 288 */       } else if (flag.startsWith("R=")) {
/* 289 */         flag = flag.substring("R=".length());
/* 290 */         rule.setRedirect(true);
/* 291 */         rule.setRedirectCode(Integer.parseInt(flag));
/*     */       } else {
/* 293 */         rule.setRedirect(true);
/* 294 */         rule.setRedirectCode(302);
/*     */       } 
/* 296 */     } else if (flag.startsWith("skip") || flag.startsWith("S")) {
/* 297 */       if (flag.startsWith("skip=")) {
/* 298 */         flag = flag.substring("skip=".length());
/* 299 */       } else if (flag.startsWith("S=")) {
/* 300 */         flag = flag.substring("S=".length());
/*     */       } 
/* 302 */       rule.setSkip(Integer.parseInt(flag));
/* 303 */     } else if (flag.startsWith("type") || flag.startsWith("T")) {
/* 304 */       if (flag.startsWith("type=")) {
/* 305 */         flag = flag.substring("type=".length());
/* 306 */       } else if (flag.startsWith("T=")) {
/* 307 */         flag = flag.substring("T=".length());
/*     */       } 
/* 309 */       rule.setType(true);
/* 310 */       rule.setTypeValue(flag);
/*     */     } else {
/* 312 */       throw UndertowServletLogger.ROOT_LOGGER.invalidRewriteFlags(line, flag);
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\compat\rewrite\RewriteConfigFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */