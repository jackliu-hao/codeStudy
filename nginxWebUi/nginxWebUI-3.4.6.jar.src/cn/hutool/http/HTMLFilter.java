/*     */ package cn.hutool.http;
/*     */ 
/*     */ import cn.hutool.core.lang.Console;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.ConcurrentHashMap;
/*     */ import java.util.concurrent.ConcurrentMap;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
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
/*     */ public final class HTMLFilter
/*     */ {
/*     */   private static final int REGEX_FLAGS_SI = 34;
/*  39 */   private static final Pattern P_COMMENTS = Pattern.compile("<!--(.*?)-->", 32);
/*  40 */   private static final Pattern P_COMMENT = Pattern.compile("^!--(.*)--$", 34);
/*  41 */   private static final Pattern P_TAGS = Pattern.compile("<(.*?)>", 32);
/*  42 */   private static final Pattern P_END_TAG = Pattern.compile("^/([a-z0-9]+)", 34);
/*  43 */   private static final Pattern P_START_TAG = Pattern.compile("^([a-z0-9]+)(.*?)(/?)$", 34);
/*  44 */   private static final Pattern P_QUOTED_ATTRIBUTES = Pattern.compile("([a-z0-9]+)=([\"'])(.*?)\\2", 34);
/*  45 */   private static final Pattern P_UNQUOTED_ATTRIBUTES = Pattern.compile("([a-z0-9]+)(=)([^\"\\s']+)", 34);
/*  46 */   private static final Pattern P_PROTOCOL = Pattern.compile("^([^:]+):", 34);
/*  47 */   private static final Pattern P_ENTITY = Pattern.compile("&#(\\d+);?");
/*  48 */   private static final Pattern P_ENTITY_UNICODE = Pattern.compile("&#x([0-9a-f]+);?");
/*  49 */   private static final Pattern P_ENCODE = Pattern.compile("%([0-9a-f]{2});?");
/*  50 */   private static final Pattern P_VALID_ENTITIES = Pattern.compile("&([^&;]*)(?=(;|&|$))");
/*  51 */   private static final Pattern P_VALID_QUOTES = Pattern.compile("(>|^)([^<]+?)(<|$)", 32);
/*  52 */   private static final Pattern P_END_ARROW = Pattern.compile("^>");
/*  53 */   private static final Pattern P_BODY_TO_END = Pattern.compile("<([^>]*?)(?=<|$)");
/*  54 */   private static final Pattern P_XML_CONTENT = Pattern.compile("(^|>)([^<]*?)(?=>)");
/*  55 */   private static final Pattern P_STRAY_LEFT_ARROW = Pattern.compile("<([^>]*?)(?=<|$)");
/*  56 */   private static final Pattern P_STRAY_RIGHT_ARROW = Pattern.compile("(^|>)([^<]*?)(?=>)");
/*  57 */   private static final Pattern P_AMP = Pattern.compile("&");
/*  58 */   private static final Pattern P_QUOTE = Pattern.compile("\"");
/*  59 */   private static final Pattern P_LEFT_ARROW = Pattern.compile("<");
/*  60 */   private static final Pattern P_RIGHT_ARROW = Pattern.compile(">");
/*  61 */   private static final Pattern P_BOTH_ARROWS = Pattern.compile("<>");
/*     */ 
/*     */   
/*  64 */   private static final ConcurrentMap<String, Pattern> P_REMOVE_PAIR_BLANKS = new ConcurrentHashMap<>();
/*  65 */   private static final ConcurrentMap<String, Pattern> P_REMOVE_SELF_BLANKS = new ConcurrentHashMap<>();
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private final Map<String, List<String>> vAllowed;
/*     */ 
/*     */ 
/*     */   
/*  74 */   private final Map<String, Integer> vTagCounts = new HashMap<>();
/*     */ 
/*     */ 
/*     */   
/*     */   private final String[] vSelfClosingTags;
/*     */ 
/*     */ 
/*     */   
/*     */   private final String[] vNeedClosingTags;
/*     */ 
/*     */ 
/*     */   
/*     */   private final String[] vDisallowed;
/*     */ 
/*     */ 
/*     */   
/*     */   private final String[] vProtocolAtts;
/*     */ 
/*     */ 
/*     */   
/*     */   private final String[] vAllowedProtocols;
/*     */ 
/*     */ 
/*     */   
/*     */   private final String[] vRemoveBlanks;
/*     */ 
/*     */ 
/*     */   
/*     */   private final String[] vAllowedEntities;
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean stripComment;
/*     */ 
/*     */ 
/*     */   
/*     */   private final boolean encodeQuotes;
/*     */ 
/*     */   
/*     */   private boolean vDebug = false;
/*     */ 
/*     */   
/*     */   private final boolean alwaysMakeTags;
/*     */ 
/*     */ 
/*     */   
/*     */   public HTMLFilter() {
/* 121 */     this.vAllowed = new HashMap<>();
/*     */     
/* 123 */     ArrayList<String> a_atts = new ArrayList<>();
/* 124 */     a_atts.add("href");
/* 125 */     a_atts.add("target");
/* 126 */     this.vAllowed.put("a", a_atts);
/*     */     
/* 128 */     ArrayList<String> img_atts = new ArrayList<>();
/* 129 */     img_atts.add("src");
/* 130 */     img_atts.add("width");
/* 131 */     img_atts.add("height");
/* 132 */     img_atts.add("alt");
/* 133 */     this.vAllowed.put("img", img_atts);
/*     */     
/* 135 */     ArrayList<String> no_atts = new ArrayList<>();
/* 136 */     this.vAllowed.put("b", no_atts);
/* 137 */     this.vAllowed.put("strong", no_atts);
/* 138 */     this.vAllowed.put("i", no_atts);
/* 139 */     this.vAllowed.put("em", no_atts);
/*     */     
/* 141 */     this.vSelfClosingTags = new String[] { "img" };
/* 142 */     this.vNeedClosingTags = new String[] { "a", "b", "strong", "i", "em" };
/* 143 */     this.vDisallowed = new String[0];
/* 144 */     this.vAllowedProtocols = new String[] { "http", "mailto", "https" };
/* 145 */     this.vProtocolAtts = new String[] { "src", "href" };
/* 146 */     this.vRemoveBlanks = new String[] { "a", "b", "strong", "i", "em" };
/* 147 */     this.vAllowedEntities = new String[] { "amp", "gt", "lt", "quot" };
/* 148 */     this.stripComment = true;
/* 149 */     this.encodeQuotes = true;
/* 150 */     this.alwaysMakeTags = true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public HTMLFilter(boolean debug) {
/* 159 */     this();
/* 160 */     this.vDebug = debug;
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
/*     */   public HTMLFilter(Map<String, Object> conf) {
/* 172 */     assert conf.containsKey("vAllowed") : "configuration requires vAllowed";
/* 173 */     assert conf.containsKey("vSelfClosingTags") : "configuration requires vSelfClosingTags";
/* 174 */     assert conf.containsKey("vNeedClosingTags") : "configuration requires vNeedClosingTags";
/* 175 */     assert conf.containsKey("vDisallowed") : "configuration requires vDisallowed";
/* 176 */     assert conf.containsKey("vAllowedProtocols") : "configuration requires vAllowedProtocols";
/* 177 */     assert conf.containsKey("vProtocolAtts") : "configuration requires vProtocolAtts";
/* 178 */     assert conf.containsKey("vRemoveBlanks") : "configuration requires vRemoveBlanks";
/* 179 */     assert conf.containsKey("vAllowedEntities") : "configuration requires vAllowedEntities";
/*     */     
/* 181 */     this.vAllowed = Collections.unmodifiableMap((HashMap)conf.get("vAllowed"));
/* 182 */     this.vSelfClosingTags = (String[])conf.get("vSelfClosingTags");
/* 183 */     this.vNeedClosingTags = (String[])conf.get("vNeedClosingTags");
/* 184 */     this.vDisallowed = (String[])conf.get("vDisallowed");
/* 185 */     this.vAllowedProtocols = (String[])conf.get("vAllowedProtocols");
/* 186 */     this.vProtocolAtts = (String[])conf.get("vProtocolAtts");
/* 187 */     this.vRemoveBlanks = (String[])conf.get("vRemoveBlanks");
/* 188 */     this.vAllowedEntities = (String[])conf.get("vAllowedEntities");
/* 189 */     this.stripComment = conf.containsKey("stripComment") ? ((Boolean)conf.get("stripComment")).booleanValue() : true;
/* 190 */     this.encodeQuotes = conf.containsKey("encodeQuotes") ? ((Boolean)conf.get("encodeQuotes")).booleanValue() : true;
/* 191 */     this.alwaysMakeTags = conf.containsKey("alwaysMakeTags") ? ((Boolean)conf.get("alwaysMakeTags")).booleanValue() : true;
/*     */   }
/*     */   
/*     */   private void reset() {
/* 195 */     this.vTagCounts.clear();
/*     */   }
/*     */   
/*     */   private void debug(String msg) {
/* 199 */     if (this.vDebug) {
/* 200 */       Console.log(msg);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static String chr(int decimal) {
/* 207 */     return String.valueOf((char)decimal);
/*     */   }
/*     */   
/*     */   public static String htmlSpecialChars(String s) {
/* 211 */     String result = s;
/* 212 */     result = regexReplace(P_AMP, "&amp;", result);
/* 213 */     result = regexReplace(P_QUOTE, "&quot;", result);
/* 214 */     result = regexReplace(P_LEFT_ARROW, "&lt;", result);
/* 215 */     result = regexReplace(P_RIGHT_ARROW, "&gt;", result);
/* 216 */     return result;
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
/*     */   public String filter(String input) {
/* 228 */     reset();
/* 229 */     String s = input;
/*     */     
/* 231 */     debug("************************************************");
/* 232 */     debug("              INPUT: " + input);
/*     */     
/* 234 */     s = escapeComments(s);
/* 235 */     debug("     escapeComments: " + s);
/*     */     
/* 237 */     s = balanceHTML(s);
/* 238 */     debug("        balanceHTML: " + s);
/*     */     
/* 240 */     s = checkTags(s);
/* 241 */     debug("          checkTags: " + s);
/*     */     
/* 243 */     s = processRemoveBlanks(s);
/* 244 */     debug("processRemoveBlanks: " + s);
/*     */     
/* 246 */     s = validateEntities(s);
/* 247 */     debug("    validateEntites: " + s);
/*     */     
/* 249 */     debug("************************************************\n\n");
/* 250 */     return s;
/*     */   }
/*     */   
/*     */   public boolean isAlwaysMakeTags() {
/* 254 */     return this.alwaysMakeTags;
/*     */   }
/*     */   
/*     */   public boolean isStripComments() {
/* 258 */     return this.stripComment;
/*     */   }
/*     */   
/*     */   private String escapeComments(String s) {
/* 262 */     Matcher m = P_COMMENTS.matcher(s);
/* 263 */     StringBuffer buf = new StringBuffer();
/* 264 */     if (m.find()) {
/* 265 */       String match = m.group(1);
/* 266 */       m.appendReplacement(buf, Matcher.quoteReplacement("<!--" + htmlSpecialChars(match) + "-->"));
/*     */     } 
/* 268 */     m.appendTail(buf);
/*     */     
/* 270 */     return buf.toString();
/*     */   }
/*     */   
/*     */   private String balanceHTML(String s) {
/* 274 */     if (this.alwaysMakeTags) {
/*     */ 
/*     */ 
/*     */       
/* 278 */       s = regexReplace(P_END_ARROW, "", s);
/* 279 */       s = regexReplace(P_BODY_TO_END, "<$1>", s);
/* 280 */       s = regexReplace(P_XML_CONTENT, "$1<$2", s);
/*     */     
/*     */     }
/*     */     else {
/*     */ 
/*     */       
/* 286 */       s = regexReplace(P_STRAY_LEFT_ARROW, "&lt;$1", s);
/* 287 */       s = regexReplace(P_STRAY_RIGHT_ARROW, "$1$2&gt;<", s);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 294 */       s = regexReplace(P_BOTH_ARROWS, "", s);
/*     */     } 
/*     */     
/* 297 */     return s;
/*     */   }
/*     */   
/*     */   private String checkTags(String s) {
/* 301 */     Matcher m = P_TAGS.matcher(s);
/*     */     
/* 303 */     StringBuffer buf = new StringBuffer();
/* 304 */     while (m.find()) {
/* 305 */       String replaceStr = m.group(1);
/* 306 */       replaceStr = processTag(replaceStr);
/* 307 */       m.appendReplacement(buf, Matcher.quoteReplacement(replaceStr));
/*     */     } 
/* 309 */     m.appendTail(buf);
/*     */ 
/*     */ 
/*     */     
/* 313 */     StringBuilder sBuilder = new StringBuilder(buf.toString());
/* 314 */     for (String key : this.vTagCounts.keySet()) {
/* 315 */       for (int ii = 0; ii < ((Integer)this.vTagCounts.get(key)).intValue(); ii++) {
/* 316 */         sBuilder.append("</").append(key).append(">");
/*     */       }
/*     */     } 
/* 319 */     s = sBuilder.toString();
/*     */     
/* 321 */     return s;
/*     */   }
/*     */   
/*     */   private String processRemoveBlanks(String s) {
/* 325 */     String result = s;
/* 326 */     for (String tag : this.vRemoveBlanks) {
/* 327 */       if (!P_REMOVE_PAIR_BLANKS.containsKey(tag)) {
/* 328 */         P_REMOVE_PAIR_BLANKS.putIfAbsent(tag, Pattern.compile("<" + tag + "(\\s[^>]*)?></" + tag + ">"));
/*     */       }
/* 330 */       result = regexReplace(P_REMOVE_PAIR_BLANKS.get(tag), "", result);
/* 331 */       if (!P_REMOVE_SELF_BLANKS.containsKey(tag)) {
/* 332 */         P_REMOVE_SELF_BLANKS.putIfAbsent(tag, Pattern.compile("<" + tag + "(\\s[^>]*)?/>"));
/*     */       }
/* 334 */       result = regexReplace(P_REMOVE_SELF_BLANKS.get(tag), "", result);
/*     */     } 
/*     */     
/* 337 */     return result;
/*     */   }
/*     */   
/*     */   private static String regexReplace(Pattern regex_pattern, String replacement, String s) {
/* 341 */     Matcher m = regex_pattern.matcher(s);
/* 342 */     return m.replaceAll(replacement);
/*     */   }
/*     */ 
/*     */   
/*     */   private String processTag(String s) {
/* 347 */     Matcher m = P_END_TAG.matcher(s);
/* 348 */     if (m.find()) {
/* 349 */       String name = m.group(1).toLowerCase();
/* 350 */       if (allowed(name) && false == 
/* 351 */         inArray(name, this.vSelfClosingTags) && 
/* 352 */         this.vTagCounts.containsKey(name)) {
/* 353 */         this.vTagCounts.put(name, Integer.valueOf(((Integer)this.vTagCounts.get(name)).intValue() - 1));
/* 354 */         return "</" + name + ">";
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 361 */     m = P_START_TAG.matcher(s);
/* 362 */     if (m.find()) {
/* 363 */       String name = m.group(1).toLowerCase();
/* 364 */       String body = m.group(2);
/* 365 */       String ending = m.group(3);
/*     */ 
/*     */       
/* 368 */       if (allowed(name)) {
/* 369 */         StringBuilder params = new StringBuilder();
/*     */         
/* 371 */         Matcher m2 = P_QUOTED_ATTRIBUTES.matcher(body);
/* 372 */         Matcher m3 = P_UNQUOTED_ATTRIBUTES.matcher(body);
/* 373 */         List<String> paramNames = new ArrayList<>();
/* 374 */         List<String> paramValues = new ArrayList<>();
/* 375 */         while (m2.find()) {
/* 376 */           paramNames.add(m2.group(1));
/* 377 */           paramValues.add(m2.group(3));
/*     */         } 
/* 379 */         while (m3.find()) {
/* 380 */           paramNames.add(m3.group(1));
/* 381 */           paramValues.add(m3.group(3));
/*     */         } 
/*     */ 
/*     */         
/* 385 */         for (int ii = 0; ii < paramNames.size(); ii++) {
/* 386 */           String paramName = ((String)paramNames.get(ii)).toLowerCase();
/* 387 */           String paramValue = paramValues.get(ii);
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */           
/* 393 */           if (allowedAttribute(name, paramName)) {
/* 394 */             if (inArray(paramName, this.vProtocolAtts)) {
/* 395 */               paramValue = processParamProtocol(paramValue);
/*     */             }
/* 397 */             params.append(' ').append(paramName).append("=\"").append(paramValue).append("\"");
/*     */           } 
/*     */         } 
/*     */         
/* 401 */         if (inArray(name, this.vSelfClosingTags)) {
/* 402 */           ending = " /";
/*     */         }
/*     */         
/* 405 */         if (inArray(name, this.vNeedClosingTags)) {
/* 406 */           ending = "";
/*     */         }
/*     */         
/* 409 */         if (ending == null || ending.length() < 1) {
/* 410 */           if (this.vTagCounts.containsKey(name)) {
/* 411 */             this.vTagCounts.put(name, Integer.valueOf(((Integer)this.vTagCounts.get(name)).intValue() + 1));
/*     */           } else {
/* 413 */             this.vTagCounts.put(name, Integer.valueOf(1));
/*     */           } 
/*     */         } else {
/* 416 */           ending = " /";
/*     */         } 
/* 418 */         return "<" + name + params + ending + ">";
/*     */       } 
/* 420 */       return "";
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 425 */     m = P_COMMENT.matcher(s);
/* 426 */     if (!this.stripComment && m.find()) {
/* 427 */       return "<" + m.group() + ">";
/*     */     }
/*     */     
/* 430 */     return "";
/*     */   }
/*     */   
/*     */   private String processParamProtocol(String s) {
/* 434 */     s = decodeEntities(s);
/* 435 */     Matcher m = P_PROTOCOL.matcher(s);
/* 436 */     if (m.find()) {
/* 437 */       String protocol = m.group(1);
/* 438 */       if (!inArray(protocol, this.vAllowedProtocols)) {
/*     */         
/* 440 */         s = "#" + s.substring(protocol.length() + 1);
/* 441 */         if (s.startsWith("#//")) {
/* 442 */           s = "#" + s.substring(3);
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 447 */     return s;
/*     */   }
/*     */   
/*     */   private String decodeEntities(String s) {
/* 451 */     StringBuffer buf = new StringBuffer();
/*     */     
/* 453 */     Matcher m = P_ENTITY.matcher(s);
/* 454 */     while (m.find()) {
/* 455 */       String match = m.group(1);
/* 456 */       int decimal = Integer.decode(match).intValue();
/* 457 */       m.appendReplacement(buf, Matcher.quoteReplacement(chr(decimal)));
/*     */     } 
/* 459 */     m.appendTail(buf);
/* 460 */     s = buf.toString();
/*     */     
/* 462 */     buf = new StringBuffer();
/* 463 */     m = P_ENTITY_UNICODE.matcher(s);
/* 464 */     while (m.find()) {
/* 465 */       String match = m.group(1);
/* 466 */       int decimal = Integer.parseInt(match, 16);
/* 467 */       m.appendReplacement(buf, Matcher.quoteReplacement(chr(decimal)));
/*     */     } 
/* 469 */     m.appendTail(buf);
/* 470 */     s = buf.toString();
/*     */     
/* 472 */     buf = new StringBuffer();
/* 473 */     m = P_ENCODE.matcher(s);
/* 474 */     while (m.find()) {
/* 475 */       String match = m.group(1);
/* 476 */       int decimal = Integer.parseInt(match, 16);
/* 477 */       m.appendReplacement(buf, Matcher.quoteReplacement(chr(decimal)));
/*     */     } 
/* 479 */     m.appendTail(buf);
/* 480 */     s = buf.toString();
/*     */     
/* 482 */     s = validateEntities(s);
/* 483 */     return s;
/*     */   }
/*     */   
/*     */   private String validateEntities(String s) {
/* 487 */     StringBuffer buf = new StringBuffer();
/*     */ 
/*     */     
/* 490 */     Matcher m = P_VALID_ENTITIES.matcher(s);
/* 491 */     while (m.find()) {
/* 492 */       String one = m.group(1);
/* 493 */       String two = m.group(2);
/* 494 */       m.appendReplacement(buf, Matcher.quoteReplacement(checkEntity(one, two)));
/*     */     } 
/* 496 */     m.appendTail(buf);
/*     */     
/* 498 */     return encodeQuotes(buf.toString());
/*     */   }
/*     */   
/*     */   private String encodeQuotes(String s) {
/* 502 */     if (this.encodeQuotes) {
/* 503 */       StringBuffer buf = new StringBuffer();
/* 504 */       Matcher m = P_VALID_QUOTES.matcher(s);
/* 505 */       while (m.find()) {
/* 506 */         String one = m.group(1);
/* 507 */         String two = m.group(2);
/* 508 */         String three = m.group(3);
/* 509 */         m.appendReplacement(buf, Matcher.quoteReplacement(one + regexReplace(P_QUOTE, "&quot;", two) + three));
/*     */       } 
/* 511 */       m.appendTail(buf);
/* 512 */       return buf.toString();
/*     */     } 
/* 514 */     return s;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private String checkEntity(String preamble, String term) {
/* 520 */     return (";".equals(term) && isValidEntity(preamble)) ? ('&' + preamble) : ("&amp;" + preamble);
/*     */   }
/*     */   
/*     */   private boolean isValidEntity(String entity) {
/* 524 */     return inArray(entity, this.vAllowedEntities);
/*     */   }
/*     */   
/*     */   private static boolean inArray(String s, String[] array) {
/* 528 */     for (String item : array) {
/* 529 */       if (item != null && item.equals(s)) {
/* 530 */         return true;
/*     */       }
/*     */     } 
/* 533 */     return false;
/*     */   }
/*     */   
/*     */   private boolean allowed(String name) {
/* 537 */     return ((this.vAllowed.isEmpty() || this.vAllowed.containsKey(name)) && !inArray(name, this.vDisallowed));
/*     */   }
/*     */   
/*     */   private boolean allowedAttribute(String name, String paramName) {
/* 541 */     return (allowed(name) && (this.vAllowed.isEmpty() || ((List)this.vAllowed.get(name)).contains(paramName)));
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\http\HTMLFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */