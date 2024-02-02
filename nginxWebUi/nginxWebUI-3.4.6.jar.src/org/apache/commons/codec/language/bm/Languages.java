/*     */ package org.apache.commons.codec.language.bm;
/*     */ 
/*     */ import java.util.Collections;
/*     */ import java.util.EnumMap;
/*     */ import java.util.HashSet;
/*     */ import java.util.Map;
/*     */ import java.util.NoSuchElementException;
/*     */ import java.util.Scanner;
/*     */ import java.util.Set;
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
/*     */ public class Languages
/*     */ {
/*     */   public static final String ANY = "any";
/*     */   
/*     */   public static abstract class LanguageSet
/*     */   {
/*     */     public static LanguageSet from(Set<String> langs) {
/*  69 */       return langs.isEmpty() ? Languages.NO_LANGUAGES : new Languages.SomeLanguages(langs);
/*     */     }
/*     */ 
/*     */     
/*     */     public abstract boolean contains(String param1String);
/*     */     
/*     */     public abstract String getAny();
/*     */     
/*     */     public abstract boolean isEmpty();
/*     */     
/*     */     public abstract boolean isSingleton();
/*     */     
/*     */     public abstract LanguageSet restrictTo(LanguageSet param1LanguageSet);
/*     */     
/*     */     abstract LanguageSet merge(LanguageSet param1LanguageSet);
/*     */   }
/*     */   
/*     */   public static final class SomeLanguages
/*     */     extends LanguageSet
/*     */   {
/*     */     private final Set<String> languages;
/*     */     
/*     */     private SomeLanguages(Set<String> languages) {
/*  92 */       this.languages = Collections.unmodifiableSet(languages);
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean contains(String language) {
/*  97 */       return this.languages.contains(language);
/*     */     }
/*     */ 
/*     */     
/*     */     public String getAny() {
/* 102 */       return this.languages.iterator().next();
/*     */     }
/*     */     
/*     */     public Set<String> getLanguages() {
/* 106 */       return this.languages;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isEmpty() {
/* 111 */       return this.languages.isEmpty();
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isSingleton() {
/* 116 */       return (this.languages.size() == 1);
/*     */     }
/*     */ 
/*     */     
/*     */     public Languages.LanguageSet restrictTo(Languages.LanguageSet other) {
/* 121 */       if (other == Languages.NO_LANGUAGES)
/* 122 */         return other; 
/* 123 */       if (other == Languages.ANY_LANGUAGE) {
/* 124 */         return this;
/*     */       }
/* 126 */       SomeLanguages sl = (SomeLanguages)other;
/* 127 */       Set<String> ls = new HashSet<>(Math.min(this.languages.size(), sl.languages.size()));
/* 128 */       for (String lang : this.languages) {
/* 129 */         if (sl.languages.contains(lang)) {
/* 130 */           ls.add(lang);
/*     */         }
/*     */       } 
/* 133 */       return from(ls);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public Languages.LanguageSet merge(Languages.LanguageSet other) {
/* 139 */       if (other == Languages.NO_LANGUAGES)
/* 140 */         return this; 
/* 141 */       if (other == Languages.ANY_LANGUAGE) {
/* 142 */         return other;
/*     */       }
/* 144 */       SomeLanguages sl = (SomeLanguages)other;
/* 145 */       Set<String> ls = new HashSet<>(this.languages);
/* 146 */       for (String lang : sl.languages) {
/* 147 */         ls.add(lang);
/*     */       }
/* 149 */       return from(ls);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public String toString() {
/* 155 */       return "Languages(" + this.languages.toString() + ")";
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 162 */   private static final Map<NameType, Languages> LANGUAGES = new EnumMap<>(NameType.class); private final Set<String> languages;
/*     */   
/*     */   static {
/* 165 */     for (NameType s : NameType.values()) {
/* 166 */       LANGUAGES.put(s, getInstance(langResourceName(s)));
/*     */     }
/*     */   }
/*     */   
/*     */   public static Languages getInstance(NameType nameType) {
/* 171 */     return LANGUAGES.get(nameType);
/*     */   }
/*     */ 
/*     */   
/*     */   public static Languages getInstance(String languagesResourceName) {
/* 176 */     Set<String> ls = new HashSet<>();
/* 177 */     try (Scanner lsScanner = new Scanner(Resources.getInputStream(languagesResourceName), "UTF-8")) {
/*     */       
/* 179 */       boolean inExtendedComment = false;
/* 180 */       while (lsScanner.hasNextLine()) {
/* 181 */         String line = lsScanner.nextLine().trim();
/* 182 */         if (inExtendedComment) {
/* 183 */           if (line.endsWith("*/"))
/* 184 */             inExtendedComment = false; 
/*     */           continue;
/*     */         } 
/* 187 */         if (line.startsWith("/*")) {
/* 188 */           inExtendedComment = true; continue;
/* 189 */         }  if (line.length() > 0) {
/* 190 */           ls.add(line);
/*     */         }
/*     */       } 
/*     */       
/* 194 */       return new Languages(Collections.unmodifiableSet(ls));
/*     */     } 
/*     */   }
/*     */   
/*     */   private static String langResourceName(NameType nameType) {
/* 199 */     return String.format("org/apache/commons/codec/language/bm/%s_languages.txt", new Object[] { nameType.getName() });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 207 */   public static final LanguageSet NO_LANGUAGES = new LanguageSet()
/*     */     {
/*     */       public boolean contains(String language) {
/* 210 */         return false;
/*     */       }
/*     */ 
/*     */       
/*     */       public String getAny() {
/* 215 */         throw new NoSuchElementException("Can't fetch any language from the empty language set.");
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean isEmpty() {
/* 220 */         return true;
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean isSingleton() {
/* 225 */         return false;
/*     */       }
/*     */ 
/*     */       
/*     */       public Languages.LanguageSet restrictTo(Languages.LanguageSet other) {
/* 230 */         return this;
/*     */       }
/*     */ 
/*     */       
/*     */       public Languages.LanguageSet merge(Languages.LanguageSet other) {
/* 235 */         return other;
/*     */       }
/*     */ 
/*     */       
/*     */       public String toString() {
/* 240 */         return "NO_LANGUAGES";
/*     */       }
/*     */     };
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 247 */   public static final LanguageSet ANY_LANGUAGE = new LanguageSet()
/*     */     {
/*     */       public boolean contains(String language) {
/* 250 */         return true;
/*     */       }
/*     */ 
/*     */       
/*     */       public String getAny() {
/* 255 */         throw new NoSuchElementException("Can't fetch any language from the any language set.");
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean isEmpty() {
/* 260 */         return false;
/*     */       }
/*     */ 
/*     */       
/*     */       public boolean isSingleton() {
/* 265 */         return false;
/*     */       }
/*     */ 
/*     */       
/*     */       public Languages.LanguageSet restrictTo(Languages.LanguageSet other) {
/* 270 */         return other;
/*     */       }
/*     */ 
/*     */       
/*     */       public Languages.LanguageSet merge(Languages.LanguageSet other) {
/* 275 */         return other;
/*     */       }
/*     */ 
/*     */       
/*     */       public String toString() {
/* 280 */         return "ANY_LANGUAGE";
/*     */       }
/*     */     };
/*     */   
/*     */   private Languages(Set<String> languages) {
/* 285 */     this.languages = languages;
/*     */   }
/*     */   
/*     */   public Set<String> getLanguages() {
/* 289 */     return this.languages;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\codec\language\bm\Languages.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */