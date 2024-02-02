/*     */ package org.apache.commons.codec.language.bm;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.Comparator;
/*     */ import java.util.EnumMap;
/*     */ import java.util.HashMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.Scanner;
/*     */ import java.util.Set;
/*     */ import java.util.regex.Matcher;
/*     */ import java.util.regex.Pattern;
/*     */ import org.apache.commons.codec.Resources;
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
/*     */ public class Rule
/*     */ {
/*     */   public static final class Phoneme
/*     */     implements PhonemeExpr
/*     */   {
/*  84 */     public static final Comparator<Phoneme> COMPARATOR = new Comparator<Phoneme>()
/*     */       {
/*     */         public int compare(Rule.Phoneme o1, Rule.Phoneme o2) {
/*  87 */           for (int i = 0; i < o1.phonemeText.length(); i++) {
/*  88 */             if (i >= o2.phonemeText.length()) {
/*  89 */               return 1;
/*     */             }
/*  91 */             int c = o1.phonemeText.charAt(i) - o2.phonemeText.charAt(i);
/*  92 */             if (c != 0) {
/*  93 */               return c;
/*     */             }
/*     */           } 
/*     */           
/*  97 */           if (o1.phonemeText.length() < o2.phonemeText.length()) {
/*  98 */             return -1;
/*     */           }
/*     */           
/* 101 */           return 0;
/*     */         }
/*     */       };
/*     */     
/*     */     private final StringBuilder phonemeText;
/*     */     private final Languages.LanguageSet languages;
/*     */     
/*     */     public Phoneme(CharSequence phonemeText, Languages.LanguageSet languages) {
/* 109 */       this.phonemeText = new StringBuilder(phonemeText);
/* 110 */       this.languages = languages;
/*     */     }
/*     */     
/*     */     public Phoneme(Phoneme phonemeLeft, Phoneme phonemeRight) {
/* 114 */       this(phonemeLeft.phonemeText, phonemeLeft.languages);
/* 115 */       this.phonemeText.append(phonemeRight.phonemeText);
/*     */     }
/*     */     
/*     */     public Phoneme(Phoneme phonemeLeft, Phoneme phonemeRight, Languages.LanguageSet languages) {
/* 119 */       this(phonemeLeft.phonemeText, languages);
/* 120 */       this.phonemeText.append(phonemeRight.phonemeText);
/*     */     }
/*     */     
/*     */     public Phoneme append(CharSequence str) {
/* 124 */       this.phonemeText.append(str);
/* 125 */       return this;
/*     */     }
/*     */     
/*     */     public Languages.LanguageSet getLanguages() {
/* 129 */       return this.languages;
/*     */     }
/*     */ 
/*     */     
/*     */     public Iterable<Phoneme> getPhonemes() {
/* 134 */       return Collections.singleton(this);
/*     */     }
/*     */     
/*     */     public CharSequence getPhonemeText() {
/* 138 */       return this.phonemeText;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     public Phoneme join(Phoneme right) {
/* 150 */       return new Phoneme(this.phonemeText.toString() + right.phonemeText.toString(), this.languages
/* 151 */           .restrictTo(right.languages));
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Phoneme mergeWithLanguage(Languages.LanguageSet lang) {
/* 162 */       return new Phoneme(this.phonemeText.toString(), this.languages.merge(lang));
/*     */     }
/*     */ 
/*     */     
/*     */     public String toString() {
/* 167 */       return this.phonemeText.toString() + "[" + this.languages + "]";
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public static final class PhonemeList
/*     */     implements PhonemeExpr
/*     */   {
/*     */     private final List<Rule.Phoneme> phonemes;
/*     */ 
/*     */     
/*     */     public PhonemeList(List<Rule.Phoneme> phonemes) {
/* 179 */       this.phonemes = phonemes;
/*     */     }
/*     */ 
/*     */     
/*     */     public List<Rule.Phoneme> getPhonemes() {
/* 184 */       return this.phonemes;
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
/* 195 */   public static final RPattern ALL_STRINGS_RMATCHER = new RPattern()
/*     */     {
/*     */       public boolean isMatch(CharSequence input) {
/* 198 */         return true;
/*     */       }
/*     */     };
/*     */ 
/*     */   
/*     */   public static final String ALL = "ALL";
/*     */   
/*     */   private static final String DOUBLE_QUOTE = "\"";
/*     */   
/*     */   private static final String HASH_INCLUDE = "#include";
/* 208 */   private static final Map<NameType, Map<RuleType, Map<String, Map<String, List<Rule>>>>> RULES = new EnumMap<>(NameType.class); private final RPattern lContext;
/*     */   private final String pattern;
/*     */   
/*     */   static {
/* 212 */     for (NameType s : NameType.values()) {
/* 213 */       Map<RuleType, Map<String, Map<String, List<Rule>>>> rts = new EnumMap<>(RuleType.class);
/*     */ 
/*     */       
/* 216 */       for (RuleType rt : RuleType.values()) {
/* 217 */         Map<String, Map<String, List<Rule>>> rs = new HashMap<>();
/*     */         
/* 219 */         Languages ls = Languages.getInstance(s);
/* 220 */         for (String l : ls.getLanguages()) {
/* 221 */           try (Scanner scanner = createScanner(s, rt, l)) {
/* 222 */             rs.put(l, parseRules(scanner, createResourceName(s, rt, l)));
/* 223 */           } catch (IllegalStateException e) {
/* 224 */             throw new IllegalStateException("Problem processing " + createResourceName(s, rt, l), e);
/*     */           } 
/*     */         } 
/* 227 */         if (!rt.equals(RuleType.RULES)) {
/* 228 */           try (Scanner scanner = createScanner(s, rt, "common")) {
/* 229 */             rs.put("common", parseRules(scanner, createResourceName(s, rt, "common")));
/*     */           } 
/*     */         }
/*     */         
/* 233 */         rts.put(rt, Collections.unmodifiableMap(rs));
/*     */       } 
/*     */       
/* 236 */       RULES.put(s, Collections.unmodifiableMap(rts));
/*     */     } 
/*     */   }
/*     */   private final PhonemeExpr phoneme; private final RPattern rContext;
/*     */   private static boolean contains(CharSequence chars, char input) {
/* 241 */     for (int i = 0; i < chars.length(); i++) {
/* 242 */       if (chars.charAt(i) == input) {
/* 243 */         return true;
/*     */       }
/*     */     } 
/* 246 */     return false;
/*     */   }
/*     */   
/*     */   private static String createResourceName(NameType nameType, RuleType rt, String lang) {
/* 250 */     return String.format("org/apache/commons/codec/language/bm/%s_%s_%s.txt", new Object[] { nameType
/* 251 */           .getName(), rt.getName(), lang });
/*     */   }
/*     */   
/*     */   private static Scanner createScanner(NameType nameType, RuleType rt, String lang) {
/* 255 */     String resName = createResourceName(nameType, rt, lang);
/* 256 */     return new Scanner(Resources.getInputStream(resName), "UTF-8");
/*     */   }
/*     */   
/*     */   private static Scanner createScanner(String lang) {
/* 260 */     String resName = String.format("org/apache/commons/codec/language/bm/%s.txt", new Object[] { lang });
/* 261 */     return new Scanner(Resources.getInputStream(resName), "UTF-8");
/*     */   }
/*     */   
/*     */   private static boolean endsWith(CharSequence input, CharSequence suffix) {
/* 265 */     if (suffix.length() > input.length()) {
/* 266 */       return false;
/*     */     }
/* 268 */     for (int i = input.length() - 1, j = suffix.length() - 1; j >= 0; i--, j--) {
/* 269 */       if (input.charAt(i) != suffix.charAt(j)) {
/* 270 */         return false;
/*     */       }
/*     */     } 
/* 273 */     return true;
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
/*     */   public static List<Rule> getInstance(NameType nameType, RuleType rt, Languages.LanguageSet langs) {
/* 289 */     Map<String, List<Rule>> ruleMap = getInstanceMap(nameType, rt, langs);
/* 290 */     List<Rule> allRules = new ArrayList<>();
/* 291 */     for (List<Rule> rules : ruleMap.values()) {
/* 292 */       allRules.addAll(rules);
/*     */     }
/* 294 */     return allRules;
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
/*     */   public static List<Rule> getInstance(NameType nameType, RuleType rt, String lang) {
/* 309 */     return getInstance(nameType, rt, Languages.LanguageSet.from(new HashSet<>(Arrays.asList(new String[] { lang }))));
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
/*     */   
/*     */   public static Map<String, List<Rule>> getInstanceMap(NameType nameType, RuleType rt, Languages.LanguageSet langs) {
/* 326 */     return langs.isSingleton() ? getInstanceMap(nameType, rt, langs.getAny()) : 
/* 327 */       getInstanceMap(nameType, rt, "any");
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
/*     */   
/*     */   public static Map<String, List<Rule>> getInstanceMap(NameType nameType, RuleType rt, String lang) {
/* 344 */     Map<String, List<Rule>> rules = (Map<String, List<Rule>>)((Map)((Map)RULES.get(nameType)).get(rt)).get(lang);
/*     */     
/* 346 */     if (rules == null) {
/* 347 */       throw new IllegalArgumentException(String.format("No rules found for %s, %s, %s.", new Object[] { nameType
/* 348 */               .getName(), rt.getName(), lang }));
/*     */     }
/*     */     
/* 351 */     return rules;
/*     */   }
/*     */   
/*     */   private static Phoneme parsePhoneme(String ph) {
/* 355 */     int open = ph.indexOf("[");
/* 356 */     if (open >= 0) {
/* 357 */       if (!ph.endsWith("]")) {
/* 358 */         throw new IllegalArgumentException("Phoneme expression contains a '[' but does not end in ']'");
/*     */       }
/* 360 */       String before = ph.substring(0, open);
/* 361 */       String in = ph.substring(open + 1, ph.length() - 1);
/* 362 */       Set<String> langs = new HashSet<>(Arrays.asList(in.split("[+]")));
/*     */       
/* 364 */       return new Phoneme(before, Languages.LanguageSet.from(langs));
/*     */     } 
/* 366 */     return new Phoneme(ph, Languages.ANY_LANGUAGE);
/*     */   }
/*     */   
/*     */   private static PhonemeExpr parsePhonemeExpr(String ph) {
/* 370 */     if (ph.startsWith("(")) {
/* 371 */       if (!ph.endsWith(")")) {
/* 372 */         throw new IllegalArgumentException("Phoneme starts with '(' so must end with ')'");
/*     */       }
/*     */       
/* 375 */       List<Phoneme> phs = new ArrayList<>();
/* 376 */       String body = ph.substring(1, ph.length() - 1);
/* 377 */       for (String part : body.split("[|]")) {
/* 378 */         phs.add(parsePhoneme(part));
/*     */       }
/* 380 */       if (body.startsWith("|") || body.endsWith("|")) {
/* 381 */         phs.add(new Phoneme("", Languages.ANY_LANGUAGE));
/*     */       }
/*     */       
/* 384 */       return new PhonemeList(phs);
/*     */     } 
/* 386 */     return parsePhoneme(ph);
/*     */   }
/*     */   
/*     */   private static Map<String, List<Rule>> parseRules(Scanner scanner, final String location) {
/* 390 */     Map<String, List<Rule>> lines = new HashMap<>();
/* 391 */     int currentLine = 0;
/*     */     
/* 393 */     boolean inMultilineComment = false;
/* 394 */     while (scanner.hasNextLine()) {
/* 395 */       currentLine++;
/* 396 */       String rawLine = scanner.nextLine();
/* 397 */       String line = rawLine;
/*     */       
/* 399 */       if (inMultilineComment) {
/* 400 */         if (line.endsWith("*/"))
/* 401 */           inMultilineComment = false; 
/*     */         continue;
/*     */       } 
/* 404 */       if (line.startsWith("/*")) {
/* 405 */         inMultilineComment = true;
/*     */         continue;
/*     */       } 
/* 408 */       int cmtI = line.indexOf("//");
/* 409 */       if (cmtI >= 0) {
/* 410 */         line = line.substring(0, cmtI);
/*     */       }
/*     */ 
/*     */       
/* 414 */       line = line.trim();
/*     */       
/* 416 */       if (line.length() == 0) {
/*     */         continue;
/*     */       }
/*     */       
/* 420 */       if (line.startsWith("#include")) {
/*     */         
/* 422 */         String incl = line.substring("#include".length()).trim();
/* 423 */         if (incl.contains(" ")) {
/* 424 */           throw new IllegalArgumentException("Malformed import statement '" + rawLine + "' in " + location);
/*     */         }
/*     */         
/* 427 */         try (Scanner hashIncludeScanner = createScanner(incl)) {
/* 428 */           lines.putAll(parseRules(hashIncludeScanner, location + "->" + incl));
/*     */         } 
/*     */         continue;
/*     */       } 
/* 432 */       String[] parts = line.split("\\s+");
/* 433 */       if (parts.length != 4) {
/* 434 */         throw new IllegalArgumentException("Malformed rule statement split into " + parts.length + " parts: " + rawLine + " in " + location);
/*     */       }
/*     */       
/*     */       try {
/* 438 */         final String pat = stripQuotes(parts[0]);
/* 439 */         final String lCon = stripQuotes(parts[1]);
/* 440 */         final String rCon = stripQuotes(parts[2]);
/* 441 */         PhonemeExpr ph = parsePhonemeExpr(stripQuotes(parts[3]));
/* 442 */         final int cLine = currentLine;
/* 443 */         Rule r = new Rule(pat, lCon, rCon, ph) {
/* 444 */             private final int myLine = cLine;
/* 445 */             private final String loc = location;
/*     */ 
/*     */             
/*     */             public String toString() {
/* 449 */               StringBuilder sb = new StringBuilder();
/* 450 */               sb.append("Rule");
/* 451 */               sb.append("{line=").append(this.myLine);
/* 452 */               sb.append(", loc='").append(this.loc).append('\'');
/* 453 */               sb.append(", pat='").append(pat).append('\'');
/* 454 */               sb.append(", lcon='").append(lCon).append('\'');
/* 455 */               sb.append(", rcon='").append(rCon).append('\'');
/* 456 */               sb.append('}');
/* 457 */               return sb.toString();
/*     */             }
/*     */           };
/* 460 */         String patternKey = r.pattern.substring(0, 1);
/* 461 */         List<Rule> rules = lines.get(patternKey);
/* 462 */         if (rules == null) {
/* 463 */           rules = new ArrayList<>();
/* 464 */           lines.put(patternKey, rules);
/*     */         } 
/* 466 */         rules.add(r);
/* 467 */       } catch (IllegalArgumentException e) {
/* 468 */         throw new IllegalStateException("Problem parsing line '" + currentLine + "' in " + location, e);
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 476 */     return lines;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static RPattern pattern(final String regex) {
/* 487 */     boolean startsWith = regex.startsWith("^");
/* 488 */     boolean endsWith = regex.endsWith("$");
/* 489 */     final String content = regex.substring(startsWith ? 1 : 0, endsWith ? (regex.length() - 1) : regex.length());
/* 490 */     boolean boxes = content.contains("[");
/*     */     
/* 492 */     if (!boxes) {
/* 493 */       if (startsWith && endsWith) {
/*     */         
/* 495 */         if (content.length() == 0)
/*     */         {
/* 497 */           return new RPattern()
/*     */             {
/*     */               public boolean isMatch(CharSequence input) {
/* 500 */                 return (input.length() == 0);
/*     */               }
/*     */             };
/*     */         }
/* 504 */         return new RPattern()
/*     */           {
/*     */             public boolean isMatch(CharSequence input) {
/* 507 */               return input.equals(content); }
/*     */           };
/*     */       } 
/* 510 */       if ((startsWith || endsWith) && content.length() == 0)
/*     */       {
/* 512 */         return ALL_STRINGS_RMATCHER; } 
/* 513 */       if (startsWith)
/*     */       {
/* 515 */         return new RPattern()
/*     */           {
/*     */             public boolean isMatch(CharSequence input) {
/* 518 */               return Rule.startsWith(input, content);
/*     */             }
/*     */           }; } 
/* 521 */       if (endsWith)
/*     */       {
/* 523 */         return new RPattern()
/*     */           {
/*     */             public boolean isMatch(CharSequence input) {
/* 526 */               return Rule.endsWith(input, content);
/*     */             }
/*     */           };
/*     */       }
/*     */     } else {
/* 531 */       boolean startsWithBox = content.startsWith("[");
/* 532 */       boolean endsWithBox = content.endsWith("]");
/*     */       
/* 534 */       if (startsWithBox && endsWithBox) {
/* 535 */         String boxContent = content.substring(1, content.length() - 1);
/* 536 */         if (!boxContent.contains("[")) {
/*     */           
/* 538 */           boolean negate = boxContent.startsWith("^");
/* 539 */           if (negate) {
/* 540 */             boxContent = boxContent.substring(1);
/*     */           }
/* 542 */           final String bContent = boxContent;
/* 543 */           final boolean shouldMatch = !negate;
/*     */           
/* 545 */           if (startsWith && endsWith)
/*     */           {
/* 547 */             return new RPattern()
/*     */               {
/*     */                 public boolean isMatch(CharSequence input) {
/* 550 */                   return (input.length() == 1 && Rule.contains(bContent, input.charAt(0)) == shouldMatch);
/*     */                 }
/*     */               }; } 
/* 553 */           if (startsWith)
/*     */           {
/* 555 */             return new RPattern()
/*     */               {
/*     */                 public boolean isMatch(CharSequence input) {
/* 558 */                   return (input.length() > 0 && Rule.contains(bContent, input.charAt(0)) == shouldMatch);
/*     */                 }
/*     */               }; } 
/* 561 */           if (endsWith)
/*     */           {
/* 563 */             return new RPattern()
/*     */               {
/*     */                 public boolean isMatch(CharSequence input) {
/* 566 */                   return (input.length() > 0 && Rule
/* 567 */                     .contains(bContent, input.charAt(input.length() - 1)) == shouldMatch);
/*     */                 }
/*     */               };
/*     */           }
/*     */         } 
/*     */       } 
/*     */     } 
/*     */     
/* 575 */     return new RPattern() {
/* 576 */         Pattern pattern = Pattern.compile(regex);
/*     */ 
/*     */         
/*     */         public boolean isMatch(CharSequence input) {
/* 580 */           Matcher matcher = this.pattern.matcher(input);
/* 581 */           return matcher.find();
/*     */         }
/*     */       };
/*     */   }
/*     */   
/*     */   private static boolean startsWith(CharSequence input, CharSequence prefix) {
/* 587 */     if (prefix.length() > input.length()) {
/* 588 */       return false;
/*     */     }
/* 590 */     for (int i = 0; i < prefix.length(); i++) {
/* 591 */       if (input.charAt(i) != prefix.charAt(i)) {
/* 592 */         return false;
/*     */       }
/*     */     } 
/* 595 */     return true;
/*     */   }
/*     */   
/*     */   private static String stripQuotes(String str) {
/* 599 */     if (str.startsWith("\"")) {
/* 600 */       str = str.substring(1);
/*     */     }
/*     */     
/* 603 */     if (str.endsWith("\"")) {
/* 604 */       str = str.substring(0, str.length() - 1);
/*     */     }
/*     */     
/* 607 */     return str;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Rule(String pattern, String lContext, String rContext, PhonemeExpr phoneme) {
/* 631 */     this.pattern = pattern;
/* 632 */     this.lContext = pattern(lContext + "$");
/* 633 */     this.rContext = pattern("^" + rContext);
/* 634 */     this.phoneme = phoneme;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RPattern getLContext() {
/* 643 */     return this.lContext;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getPattern() {
/* 652 */     return this.pattern;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public PhonemeExpr getPhoneme() {
/* 661 */     return this.phoneme;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public RPattern getRContext() {
/* 670 */     return this.rContext;
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
/*     */   public boolean patternAndContextMatches(CharSequence input, int i) {
/* 685 */     if (i < 0) {
/* 686 */       throw new IndexOutOfBoundsException("Can not match pattern at negative indexes");
/*     */     }
/*     */     
/* 689 */     int patternLength = this.pattern.length();
/* 690 */     int ipl = i + patternLength;
/*     */     
/* 692 */     if (ipl > input.length())
/*     */     {
/* 694 */       return false;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 699 */     if (!input.subSequence(i, ipl).equals(this.pattern))
/* 700 */       return false; 
/* 701 */     if (!this.rContext.isMatch(input.subSequence(ipl, input.length()))) {
/* 702 */       return false;
/*     */     }
/* 704 */     return this.lContext.isMatch(input.subSequence(0, i));
/*     */   }
/*     */   
/*     */   public static interface RPattern {
/*     */     boolean isMatch(CharSequence param1CharSequence);
/*     */   }
/*     */   
/*     */   public static interface PhonemeExpr {
/*     */     Iterable<Rule.Phoneme> getPhonemes();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\language\bm\Rule.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */