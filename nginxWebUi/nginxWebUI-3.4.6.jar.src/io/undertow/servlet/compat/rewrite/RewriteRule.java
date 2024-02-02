/*     */ package io.undertow.servlet.compat.rewrite;
/*     */ 
/*     */ import java.util.ArrayList;
/*     */ import java.util.Map;
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
/*     */ public class RewriteRule
/*     */ {
/*  31 */   protected RewriteCond[] conditions = new RewriteCond[0];
/*     */   
/*  33 */   protected ThreadLocal<Pattern> pattern = new ThreadLocal<>();
/*  34 */   protected Substitution substitution = null;
/*     */   
/*  36 */   protected String patternString = null;
/*  37 */   protected String substitutionString = null;
/*     */ 
/*     */   
/*     */   public void parse(Map<String, RewriteMap> maps) {
/*  41 */     if (!"-".equals(this.substitutionString)) {
/*  42 */       this.substitution = new Substitution();
/*  43 */       this.substitution.setSub(this.substitutionString);
/*  44 */       this.substitution.parse(maps);
/*     */     } 
/*     */     
/*  47 */     int flags = 0;
/*  48 */     if (isNocase()) {
/*  49 */       flags |= 0x2;
/*     */     }
/*  51 */     Pattern.compile(this.patternString, flags);
/*     */     int i;
/*  53 */     for (i = 0; i < this.conditions.length; i++) {
/*  54 */       this.conditions[i].parse(maps);
/*     */     }
/*     */     
/*  57 */     if (isEnv()) {
/*  58 */       for (i = 0; i < this.envValue.size(); i++) {
/*  59 */         Substitution newEnvSubstitution = new Substitution();
/*  60 */         newEnvSubstitution.setSub(this.envValue.get(i));
/*  61 */         newEnvSubstitution.parse(maps);
/*  62 */         this.envSubstitution.add(newEnvSubstitution);
/*  63 */         this.envResult.add(new ThreadLocal<>());
/*     */       } 
/*     */     }
/*  66 */     if (isCookie()) {
/*  67 */       this.cookieSubstitution = new Substitution();
/*  68 */       this.cookieSubstitution.setSub(this.cookieValue);
/*  69 */       this.cookieSubstitution.parse(maps);
/*     */     } 
/*     */   }
/*     */   
/*     */   public void addCondition(RewriteCond condition) {
/*  74 */     RewriteCond[] conditions = new RewriteCond[this.conditions.length + 1];
/*  75 */     for (int i = 0; i < this.conditions.length; i++) {
/*  76 */       conditions[i] = this.conditions[i];
/*     */     }
/*  78 */     conditions[this.conditions.length] = condition;
/*  79 */     this.conditions = conditions;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CharSequence evaluate(CharSequence url, Resolver resolver) {
/*  88 */     Pattern pattern = this.pattern.get();
/*  89 */     if (pattern == null) {
/*     */       
/*  91 */       int flags = 0;
/*  92 */       if (isNocase()) {
/*  93 */         flags |= 0x2;
/*     */       }
/*  95 */       pattern = Pattern.compile(this.patternString, flags);
/*  96 */       this.pattern.set(pattern);
/*     */     } 
/*  98 */     Matcher matcher = pattern.matcher(url);
/*  99 */     if (!matcher.matches())
/*     */     {
/* 101 */       return null;
/*     */     }
/*     */     
/* 104 */     boolean done = false;
/* 105 */     boolean rewrite = true;
/* 106 */     Matcher lastMatcher = null;
/* 107 */     int pos = 0;
/* 108 */     while (!done) {
/* 109 */       if (pos < this.conditions.length) {
/* 110 */         rewrite = this.conditions[pos].evaluate(matcher, lastMatcher, resolver);
/* 111 */         if (rewrite) {
/* 112 */           Matcher lastMatcher2 = this.conditions[pos].getMatcher();
/* 113 */           if (lastMatcher2 != null) {
/* 114 */             lastMatcher = lastMatcher2;
/*     */           }
/* 116 */           while (pos < this.conditions.length && this.conditions[pos].isOrnext()) {
/* 117 */             pos++;
/*     */           }
/* 119 */         } else if (!this.conditions[pos].isOrnext()) {
/* 120 */           done = true;
/*     */         } 
/* 122 */         pos++; continue;
/*     */       } 
/* 124 */       done = true;
/*     */     } 
/*     */ 
/*     */     
/* 128 */     if (rewrite) {
/* 129 */       if (isEnv()) {
/* 130 */         for (int i = 0; i < this.envSubstitution.size(); i++) {
/* 131 */           ((ThreadLocal<String>)this.envResult.get(i)).set(((Substitution)this.envSubstitution.get(i)).evaluate(matcher, lastMatcher, resolver));
/*     */         }
/*     */       }
/* 134 */       if (isCookie()) {
/* 135 */         this.cookieResult.set(this.cookieSubstitution.evaluate(matcher, lastMatcher, resolver));
/*     */       }
/* 137 */       if (this.substitution != null) {
/* 138 */         return this.substitution.evaluate(matcher, lastMatcher, resolver);
/*     */       }
/* 140 */       return url;
/*     */     } 
/*     */     
/* 143 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 153 */     return "RewriteRule " + this.patternString + " " + this.substitutionString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean chain = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean cookie = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 176 */   protected String cookieName = null;
/* 177 */   protected String cookieValue = null;
/* 178 */   protected String cookieDomain = null;
/* 179 */   protected int cookieLifetime = -1;
/* 180 */   protected String cookiePath = null;
/*     */   protected boolean cookieSecure = false;
/*     */   protected boolean cookieHttpOnly = false;
/* 183 */   protected Substitution cookieSubstitution = null;
/* 184 */   protected ThreadLocal<String> cookieResult = new ThreadLocal<>();
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean env = false;
/*     */ 
/*     */ 
/*     */   
/* 192 */   protected ArrayList<String> envName = new ArrayList<>();
/* 193 */   protected ArrayList<String> envValue = new ArrayList<>();
/* 194 */   protected ArrayList<Substitution> envSubstitution = new ArrayList<>();
/* 195 */   protected ArrayList<ThreadLocal<String>> envResult = new ArrayList<>();
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
/*     */   protected boolean forbidden = false;
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
/*     */   protected boolean gone = false;
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
/*     */   protected boolean host = false;
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
/*     */   protected boolean last = false;
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
/*     */   protected boolean next = false;
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
/*     */   protected boolean nocase = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean noescape = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean nosubreq = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean qsappend = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean redirect = false;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 311 */   protected int redirectCode = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 320 */   protected int skip = 0;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   protected boolean type = false;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/* 330 */   protected String typeValue = null;
/*     */   
/*     */   public boolean isChain() {
/* 333 */     return this.chain;
/*     */   }
/*     */   
/*     */   public void setChain(boolean chain) {
/* 337 */     this.chain = chain;
/*     */   }
/*     */   
/*     */   public RewriteCond[] getConditions() {
/* 341 */     return this.conditions;
/*     */   }
/*     */   
/*     */   public void setConditions(RewriteCond[] conditions) {
/* 345 */     this.conditions = conditions;
/*     */   }
/*     */   
/*     */   public boolean isCookie() {
/* 349 */     return this.cookie;
/*     */   }
/*     */   
/*     */   public void setCookie(boolean cookie) {
/* 353 */     this.cookie = cookie;
/*     */   }
/*     */   
/*     */   public String getCookieName() {
/* 357 */     return this.cookieName;
/*     */   }
/*     */   
/*     */   public void setCookieName(String cookieName) {
/* 361 */     this.cookieName = cookieName;
/*     */   }
/*     */   
/*     */   public String getCookieValue() {
/* 365 */     return this.cookieValue;
/*     */   }
/*     */   
/*     */   public void setCookieValue(String cookieValue) {
/* 369 */     this.cookieValue = cookieValue;
/*     */   }
/*     */   
/*     */   public String getCookieResult() {
/* 373 */     return this.cookieResult.get();
/*     */   }
/*     */   
/*     */   public boolean isEnv() {
/* 377 */     return this.env;
/*     */   }
/*     */   
/*     */   public int getEnvSize() {
/* 381 */     return this.envName.size();
/*     */   }
/*     */   
/*     */   public void setEnv(boolean env) {
/* 385 */     this.env = env;
/*     */   }
/*     */   
/*     */   public String getEnvName(int i) {
/* 389 */     return this.envName.get(i);
/*     */   }
/*     */   
/*     */   public void addEnvName(String envName) {
/* 393 */     this.envName.add(envName);
/*     */   }
/*     */   
/*     */   public String getEnvValue(int i) {
/* 397 */     return this.envValue.get(i);
/*     */   }
/*     */   
/*     */   public void addEnvValue(String envValue) {
/* 401 */     this.envValue.add(envValue);
/*     */   }
/*     */   
/*     */   public String getEnvResult(int i) {
/* 405 */     return ((ThreadLocal<String>)this.envResult.get(i)).get();
/*     */   }
/*     */   
/*     */   public boolean isForbidden() {
/* 409 */     return this.forbidden;
/*     */   }
/*     */   
/*     */   public void setForbidden(boolean forbidden) {
/* 413 */     this.forbidden = forbidden;
/*     */   }
/*     */   
/*     */   public boolean isGone() {
/* 417 */     return this.gone;
/*     */   }
/*     */   
/*     */   public void setGone(boolean gone) {
/* 421 */     this.gone = gone;
/*     */   }
/*     */   
/*     */   public boolean isLast() {
/* 425 */     return this.last;
/*     */   }
/*     */   
/*     */   public void setLast(boolean last) {
/* 429 */     this.last = last;
/*     */   }
/*     */   
/*     */   public boolean isNext() {
/* 433 */     return this.next;
/*     */   }
/*     */   
/*     */   public void setNext(boolean next) {
/* 437 */     this.next = next;
/*     */   }
/*     */   
/*     */   public boolean isNocase() {
/* 441 */     return this.nocase;
/*     */   }
/*     */   
/*     */   public void setNocase(boolean nocase) {
/* 445 */     this.nocase = nocase;
/*     */   }
/*     */   
/*     */   public boolean isNoescape() {
/* 449 */     return this.noescape;
/*     */   }
/*     */   
/*     */   public void setNoescape(boolean noescape) {
/* 453 */     this.noescape = noescape;
/*     */   }
/*     */   
/*     */   public boolean isNosubreq() {
/* 457 */     return this.nosubreq;
/*     */   }
/*     */   
/*     */   public void setNosubreq(boolean nosubreq) {
/* 461 */     this.nosubreq = nosubreq;
/*     */   }
/*     */   
/*     */   public boolean isQsappend() {
/* 465 */     return this.qsappend;
/*     */   }
/*     */   
/*     */   public void setQsappend(boolean qsappend) {
/* 469 */     this.qsappend = qsappend;
/*     */   }
/*     */   
/*     */   public boolean isRedirect() {
/* 473 */     return this.redirect;
/*     */   }
/*     */   
/*     */   public void setRedirect(boolean redirect) {
/* 477 */     this.redirect = redirect;
/*     */   }
/*     */   
/*     */   public int getRedirectCode() {
/* 481 */     return this.redirectCode;
/*     */   }
/*     */   
/*     */   public void setRedirectCode(int redirectCode) {
/* 485 */     this.redirectCode = redirectCode;
/*     */   }
/*     */   
/*     */   public int getSkip() {
/* 489 */     return this.skip;
/*     */   }
/*     */   
/*     */   public void setSkip(int skip) {
/* 493 */     this.skip = skip;
/*     */   }
/*     */   
/*     */   public Substitution getSubstitution() {
/* 497 */     return this.substitution;
/*     */   }
/*     */   
/*     */   public void setSubstitution(Substitution substitution) {
/* 501 */     this.substitution = substitution;
/*     */   }
/*     */   
/*     */   public boolean isType() {
/* 505 */     return this.type;
/*     */   }
/*     */   
/*     */   public void setType(boolean type) {
/* 509 */     this.type = type;
/*     */   }
/*     */   
/*     */   public String getTypeValue() {
/* 513 */     return this.typeValue;
/*     */   }
/*     */   
/*     */   public void setTypeValue(String typeValue) {
/* 517 */     this.typeValue = typeValue;
/*     */   }
/*     */   
/*     */   public String getPatternString() {
/* 521 */     return this.patternString;
/*     */   }
/*     */   
/*     */   public void setPatternString(String patternString) {
/* 525 */     this.patternString = patternString;
/*     */   }
/*     */   
/*     */   public String getSubstitutionString() {
/* 529 */     return this.substitutionString;
/*     */   }
/*     */   
/*     */   public void setSubstitutionString(String substitutionString) {
/* 533 */     this.substitutionString = substitutionString;
/*     */   }
/*     */   
/*     */   public boolean isHost() {
/* 537 */     return this.host;
/*     */   }
/*     */   
/*     */   public void setHost(boolean host) {
/* 541 */     this.host = host;
/*     */   }
/*     */   
/*     */   public String getCookieDomain() {
/* 545 */     return this.cookieDomain;
/*     */   }
/*     */   
/*     */   public void setCookieDomain(String cookieDomain) {
/* 549 */     this.cookieDomain = cookieDomain;
/*     */   }
/*     */   
/*     */   public int getCookieLifetime() {
/* 553 */     return this.cookieLifetime;
/*     */   }
/*     */   
/*     */   public void setCookieLifetime(int cookieLifetime) {
/* 557 */     this.cookieLifetime = cookieLifetime;
/*     */   }
/*     */   
/*     */   public String getCookiePath() {
/* 561 */     return this.cookiePath;
/*     */   }
/*     */   
/*     */   public void setCookiePath(String cookiePath) {
/* 565 */     this.cookiePath = cookiePath;
/*     */   }
/*     */   
/*     */   public boolean isCookieSecure() {
/* 569 */     return this.cookieSecure;
/*     */   }
/*     */   
/*     */   public void setCookieSecure(boolean cookieSecure) {
/* 573 */     this.cookieSecure = cookieSecure;
/*     */   }
/*     */   
/*     */   public boolean isCookieHttpOnly() {
/* 577 */     return this.cookieHttpOnly;
/*     */   }
/*     */   
/*     */   public void setCookieHttpOnly(boolean cookieHttpOnly) {
/* 581 */     this.cookieHttpOnly = cookieHttpOnly;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\servlet\compat\rewrite\RewriteRule.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */