/*     */ package org.apache.commons.codec.language.bm;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.EnumMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.Map;
/*     */ import java.util.Scanner;
/*     */ import java.util.Set;
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
/*     */ public class Lang
/*     */ {
/*     */   private static final class LangRule
/*     */   {
/*     */     private final boolean acceptOnMatch;
/*     */     private final Set<String> languages;
/*     */     private final Pattern pattern;
/*     */     
/*     */     private LangRule(Pattern pattern, Set<String> languages, boolean acceptOnMatch) {
/*  86 */       this.pattern = pattern;
/*  87 */       this.languages = languages;
/*  88 */       this.acceptOnMatch = acceptOnMatch;
/*     */     }
/*     */     
/*     */     public boolean matches(String txt) {
/*  92 */       return this.pattern.matcher(txt).find();
/*     */     }
/*     */   }
/*     */   
/*  96 */   private static final Map<NameType, Lang> Langs = new EnumMap<>(NameType.class);
/*     */   
/*     */   private static final String LANGUAGE_RULES_RN = "org/apache/commons/codec/language/bm/%s_lang.txt";
/*     */   
/*     */   static {
/* 101 */     for (NameType s : NameType.values()) {
/* 102 */       Langs.put(s, loadFromResource(String.format("org/apache/commons/codec/language/bm/%s_lang.txt", new Object[] { s.getName() }), Languages.getInstance(s)));
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private final Languages languages;
/*     */   
/*     */   private final List<LangRule> rules;
/*     */ 
/*     */   
/*     */   public static Lang instance(NameType nameType) {
/* 114 */     return Langs.get(nameType);
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
/*     */   public static Lang loadFromResource(String languageRulesResourceName, Languages languages) {
/* 130 */     List<LangRule> rules = new ArrayList<>();
/* 131 */     try (Scanner scanner = new Scanner(Resources.getInputStream(languageRulesResourceName), "UTF-8")) {
/*     */       
/* 133 */       boolean inExtendedComment = false;
/* 134 */       while (scanner.hasNextLine()) {
/* 135 */         String rawLine = scanner.nextLine();
/* 136 */         String line = rawLine;
/* 137 */         if (inExtendedComment) {
/*     */           
/* 139 */           if (line.endsWith("*/"))
/* 140 */             inExtendedComment = false; 
/*     */           continue;
/*     */         } 
/* 143 */         if (line.startsWith("/*")) {
/* 144 */           inExtendedComment = true;
/*     */           continue;
/*     */         } 
/* 147 */         int cmtI = line.indexOf("//");
/* 148 */         if (cmtI >= 0) {
/* 149 */           line = line.substring(0, cmtI);
/*     */         }
/*     */ 
/*     */         
/* 153 */         line = line.trim();
/*     */         
/* 155 */         if (line.length() == 0) {
/*     */           continue;
/*     */         }
/*     */ 
/*     */         
/* 160 */         String[] parts = line.split("\\s+");
/*     */         
/* 162 */         if (parts.length != 3) {
/* 163 */           throw new IllegalArgumentException("Malformed line '" + rawLine + "' in language resource '" + languageRulesResourceName + "'");
/*     */         }
/*     */ 
/*     */         
/* 167 */         Pattern pattern = Pattern.compile(parts[0]);
/* 168 */         String[] langs = parts[1].split("\\+");
/* 169 */         boolean accept = parts[2].equals("true");
/*     */         
/* 171 */         rules.add(new LangRule(pattern, new HashSet(Arrays.asList((Object[])langs)), accept));
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 176 */     return new Lang(rules, languages);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private Lang(List<LangRule> rules, Languages languages) {
/* 183 */     this.rules = Collections.unmodifiableList(rules);
/* 184 */     this.languages = languages;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String guessLanguage(String text) {
/* 195 */     Languages.LanguageSet ls = guessLanguages(text);
/* 196 */     return ls.isSingleton() ? ls.getAny() : "any";
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Languages.LanguageSet guessLanguages(String input) {
/* 207 */     String text = input.toLowerCase(Locale.ENGLISH);
/*     */     
/* 209 */     Set<String> langs = new HashSet<>(this.languages.getLanguages());
/* 210 */     for (LangRule rule : this.rules) {
/* 211 */       if (rule.matches(text)) {
/* 212 */         if (rule.acceptOnMatch) {
/* 213 */           langs.retainAll(rule.languages); continue;
/*     */         } 
/* 215 */         langs.removeAll(rule.languages);
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 220 */     Languages.LanguageSet ls = Languages.LanguageSet.from(langs);
/* 221 */     return ls.equals(Languages.NO_LANGUAGES) ? Languages.ANY_LANGUAGE : ls;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\language\bm\Lang.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */