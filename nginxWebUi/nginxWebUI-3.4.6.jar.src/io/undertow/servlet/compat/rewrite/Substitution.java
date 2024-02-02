/*     */ package io.undertow.servlet.compat.rewrite;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Map;
/*     */ import java.util.regex.Matcher;
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
/*     */ public class Substitution
/*     */ {
/*     */   public abstract class SubstitutionElement
/*     */   {
/*     */     public abstract String evaluate(Matcher param1Matcher1, Matcher param1Matcher2, Resolver param1Resolver);
/*     */   }
/*     */   
/*     */   public class StaticElement
/*     */     extends SubstitutionElement
/*     */   {
/*     */     public String value;
/*     */     
/*     */     public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
/*  39 */       return this.value;
/*     */     }
/*     */   }
/*     */   
/*     */   public class RewriteRuleBackReferenceElement
/*     */     extends SubstitutionElement {
/*     */     public int n;
/*     */     
/*     */     public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
/*  48 */       return rule.group(this.n);
/*     */     }
/*     */   }
/*     */   
/*     */   public class RewriteCondBackReferenceElement extends SubstitutionElement {
/*     */     public int n;
/*     */     
/*     */     public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
/*  56 */       return cond.group(this.n);
/*     */     }
/*     */   }
/*     */   
/*     */   public class ServerVariableElement extends SubstitutionElement {
/*     */     public String key;
/*     */     
/*     */     public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
/*  64 */       return resolver.resolve(this.key);
/*     */     }
/*     */   }
/*     */   
/*     */   public class ServerVariableEnvElement extends SubstitutionElement {
/*     */     public String key;
/*     */     
/*     */     public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
/*  72 */       return resolver.resolveEnv(this.key);
/*     */     }
/*     */   }
/*     */   
/*     */   public class ServerVariableSslElement extends SubstitutionElement {
/*     */     public String key;
/*     */     
/*     */     public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
/*  80 */       return resolver.resolveSsl(this.key);
/*     */     }
/*     */   }
/*     */   
/*     */   public class ServerVariableHttpElement extends SubstitutionElement {
/*     */     public String key;
/*     */     
/*     */     public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
/*  88 */       return resolver.resolveHttp(this.key);
/*     */     } }
/*     */   public class MapElement extends SubstitutionElement { public RewriteMap map;
/*     */     
/*     */     public MapElement() {
/*  93 */       this.map = null;
/*     */       
/*  95 */       this.defaultValue = null;
/*     */     } public String key; public String defaultValue;
/*     */     public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
/*  98 */       String result = this.map.lookup(this.key);
/*  99 */       if (result == null) {
/* 100 */         result = this.defaultValue;
/*     */       }
/* 102 */       return result;
/*     */     } }
/*     */ 
/*     */   
/* 106 */   protected SubstitutionElement[] elements = null;
/*     */   
/* 108 */   protected String sub = null;
/*     */   
/*     */   public String getSub() {
/* 111 */     return this.sub;
/*     */   }
/*     */   
/*     */   public void setSub(String sub) {
/* 115 */     this.sub = sub;
/*     */   }
/*     */ 
/*     */   
/*     */   public void parse(Map<String, RewriteMap> maps) {
/* 120 */     ArrayList<SubstitutionElement> elements = new ArrayList<>();
/* 121 */     int pos = 0;
/* 122 */     int percentPos = 0;
/* 123 */     int dollarPos = 0;
/*     */     
/* 125 */     while (pos < this.sub.length()) {
/* 126 */       percentPos = this.sub.indexOf('%', pos);
/* 127 */       dollarPos = this.sub.indexOf('$', pos);
/* 128 */       if (percentPos == -1 && dollarPos == -1) {
/*     */         
/* 130 */         StaticElement staticElement = new StaticElement();
/* 131 */         staticElement.value = this.sub.substring(pos, this.sub.length());
/* 132 */         pos = this.sub.length();
/* 133 */         elements.add(staticElement); continue;
/* 134 */       }  if (percentPos == -1 || (dollarPos != -1 && dollarPos < percentPos)) {
/*     */         
/* 136 */         if (dollarPos + 1 == this.sub.length()) {
/* 137 */           throw new IllegalArgumentException(this.sub);
/*     */         }
/* 139 */         if (pos < dollarPos) {
/*     */           
/* 141 */           StaticElement staticElement = new StaticElement();
/* 142 */           staticElement.value = this.sub.substring(pos, dollarPos);
/* 143 */           pos = dollarPos;
/* 144 */           elements.add(staticElement);
/*     */         } 
/* 146 */         if (Character.isDigit(this.sub.charAt(dollarPos + 1))) {
/*     */           
/* 148 */           RewriteRuleBackReferenceElement rewriteRuleBackReferenceElement = new RewriteRuleBackReferenceElement();
/* 149 */           rewriteRuleBackReferenceElement.n = Character.digit(this.sub.charAt(dollarPos + 1), 10);
/* 150 */           pos = dollarPos + 2;
/* 151 */           elements.add(rewriteRuleBackReferenceElement);
/*     */           continue;
/*     */         } 
/* 154 */         MapElement mapElement = new MapElement();
/* 155 */         int i = this.sub.indexOf('{', dollarPos);
/* 156 */         int j = this.sub.indexOf(':', dollarPos);
/* 157 */         int def = this.sub.indexOf('|', dollarPos);
/* 158 */         int k = this.sub.indexOf('}', dollarPos);
/* 159 */         if (-1 >= i || i >= j || j >= k) {
/* 160 */           throw new IllegalArgumentException(this.sub);
/*     */         }
/* 162 */         mapElement.map = maps.get(this.sub.substring(i + 1, j));
/* 163 */         if (mapElement.map == null) {
/* 164 */           throw new IllegalArgumentException(this.sub + ": No map: " + this.sub.substring(i + 1, j));
/*     */         }
/* 166 */         if (def > -1) {
/* 167 */           if (j >= def || def >= k) {
/* 168 */             throw new IllegalArgumentException(this.sub);
/*     */           }
/* 170 */           mapElement.key = this.sub.substring(j + 1, def);
/* 171 */           mapElement.defaultValue = this.sub.substring(def + 1, k);
/*     */         } else {
/* 173 */           mapElement.key = this.sub.substring(j + 1, k);
/*     */         } 
/* 175 */         pos = k + 1;
/* 176 */         elements.add(mapElement);
/*     */         
/*     */         continue;
/*     */       } 
/* 180 */       if (percentPos + 1 == this.sub.length()) {
/* 181 */         throw new IllegalArgumentException(this.sub);
/*     */       }
/* 183 */       if (pos < percentPos) {
/*     */         
/* 185 */         StaticElement staticElement = new StaticElement();
/* 186 */         staticElement.value = this.sub.substring(pos, percentPos);
/* 187 */         pos = percentPos;
/* 188 */         elements.add(staticElement);
/*     */       } 
/* 190 */       if (Character.isDigit(this.sub.charAt(percentPos + 1))) {
/*     */         
/* 192 */         RewriteCondBackReferenceElement rewriteCondBackReferenceElement = new RewriteCondBackReferenceElement();
/* 193 */         rewriteCondBackReferenceElement.n = Character.digit(this.sub.charAt(percentPos + 1), 10);
/* 194 */         pos = percentPos + 2;
/* 195 */         elements.add(rewriteCondBackReferenceElement);
/*     */         continue;
/*     */       } 
/* 198 */       SubstitutionElement newElement = null;
/* 199 */       int open = this.sub.indexOf('{', percentPos);
/* 200 */       int colon = this.sub.indexOf(':', percentPos);
/* 201 */       int close = this.sub.indexOf('}', percentPos);
/* 202 */       if (-1 >= open || open >= close) {
/* 203 */         throw new IllegalArgumentException(this.sub);
/*     */       }
/* 205 */       if (colon > -1) {
/* 206 */         if (open >= colon || colon >= close) {
/* 207 */           throw new IllegalArgumentException(this.sub);
/*     */         }
/* 209 */         String type = this.sub.substring(open + 1, colon);
/* 210 */         if (type.equals("ENV")) {
/* 211 */           newElement = new ServerVariableEnvElement();
/* 212 */           ((ServerVariableEnvElement)newElement).key = this.sub.substring(colon + 1, close);
/* 213 */         } else if (type.equals("SSL")) {
/* 214 */           newElement = new ServerVariableSslElement();
/* 215 */           ((ServerVariableSslElement)newElement).key = this.sub.substring(colon + 1, close);
/* 216 */         } else if (type.equals("HTTP")) {
/* 217 */           newElement = new ServerVariableHttpElement();
/* 218 */           ((ServerVariableHttpElement)newElement).key = this.sub.substring(colon + 1, close);
/*     */         } else {
/* 220 */           throw new IllegalArgumentException(this.sub + ": Bad type: " + type);
/*     */         } 
/*     */       } else {
/* 223 */         newElement = new ServerVariableElement();
/* 224 */         ((ServerVariableElement)newElement).key = this.sub.substring(open + 1, close);
/*     */       } 
/* 226 */       pos = close + 1;
/* 227 */       elements.add(newElement);
/*     */     } 
/*     */ 
/*     */ 
/*     */     
/* 232 */     this.elements = elements.<SubstitutionElement>toArray(new SubstitutionElement[0]);
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
/*     */   public String evaluate(Matcher rule, Matcher cond, Resolver resolver) {
/* 244 */     StringBuffer buf = new StringBuffer();
/* 245 */     for (int i = 0; i < this.elements.length; i++) {
/* 246 */       buf.append(this.elements[i].evaluate(rule, cond, resolver));
/*     */     }
/* 248 */     return buf.toString();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\compat\rewrite\Substitution.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */