package io.undertow.servlet.compat.rewrite;

import java.util.ArrayList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RewriteRule {
   protected RewriteCond[] conditions = new RewriteCond[0];
   protected ThreadLocal<Pattern> pattern = new ThreadLocal();
   protected Substitution substitution = null;
   protected String patternString = null;
   protected String substitutionString = null;
   protected boolean chain = false;
   protected boolean cookie = false;
   protected String cookieName = null;
   protected String cookieValue = null;
   protected String cookieDomain = null;
   protected int cookieLifetime = -1;
   protected String cookiePath = null;
   protected boolean cookieSecure = false;
   protected boolean cookieHttpOnly = false;
   protected Substitution cookieSubstitution = null;
   protected ThreadLocal<String> cookieResult = new ThreadLocal();
   protected boolean env = false;
   protected ArrayList<String> envName = new ArrayList();
   protected ArrayList<String> envValue = new ArrayList();
   protected ArrayList<Substitution> envSubstitution = new ArrayList();
   protected ArrayList<ThreadLocal<String>> envResult = new ArrayList();
   protected boolean forbidden = false;
   protected boolean gone = false;
   protected boolean host = false;
   protected boolean last = false;
   protected boolean next = false;
   protected boolean nocase = false;
   protected boolean noescape = false;
   protected boolean nosubreq = false;
   protected boolean qsappend = false;
   protected boolean redirect = false;
   protected int redirectCode = 0;
   protected int skip = 0;
   protected boolean type = false;
   protected String typeValue = null;

   public void parse(Map<String, RewriteMap> maps) {
      if (!"-".equals(this.substitutionString)) {
         this.substitution = new Substitution();
         this.substitution.setSub(this.substitutionString);
         this.substitution.parse(maps);
      }

      int flags = 0;
      if (this.isNocase()) {
         flags |= 2;
      }

      Pattern.compile(this.patternString, flags);

      int i;
      for(i = 0; i < this.conditions.length; ++i) {
         this.conditions[i].parse(maps);
      }

      if (this.isEnv()) {
         for(i = 0; i < this.envValue.size(); ++i) {
            Substitution newEnvSubstitution = new Substitution();
            newEnvSubstitution.setSub((String)this.envValue.get(i));
            newEnvSubstitution.parse(maps);
            this.envSubstitution.add(newEnvSubstitution);
            this.envResult.add(new ThreadLocal());
         }
      }

      if (this.isCookie()) {
         this.cookieSubstitution = new Substitution();
         this.cookieSubstitution.setSub(this.cookieValue);
         this.cookieSubstitution.parse(maps);
      }

   }

   public void addCondition(RewriteCond condition) {
      RewriteCond[] conditions = new RewriteCond[this.conditions.length + 1];

      for(int i = 0; i < this.conditions.length; ++i) {
         conditions[i] = this.conditions[i];
      }

      conditions[this.conditions.length] = condition;
      this.conditions = conditions;
   }

   public CharSequence evaluate(CharSequence url, Resolver resolver) {
      Pattern pattern = (Pattern)this.pattern.get();
      if (pattern == null) {
         int flags = 0;
         if (this.isNocase()) {
            flags |= 2;
         }

         pattern = Pattern.compile(this.patternString, flags);
         this.pattern.set(pattern);
      }

      Matcher matcher = pattern.matcher(url);
      if (!matcher.matches()) {
         return null;
      } else {
         boolean done = false;
         boolean rewrite = true;
         Matcher lastMatcher = null;
         int pos = 0;

         while(true) {
            while(!done) {
               if (pos < this.conditions.length) {
                  rewrite = this.conditions[pos].evaluate(matcher, lastMatcher, resolver);
                  if (rewrite) {
                     Matcher lastMatcher2 = this.conditions[pos].getMatcher();
                     if (lastMatcher2 != null) {
                        lastMatcher = lastMatcher2;
                     }

                     while(pos < this.conditions.length && this.conditions[pos].isOrnext()) {
                        ++pos;
                     }
                  } else if (!this.conditions[pos].isOrnext()) {
                     done = true;
                  }

                  ++pos;
               } else {
                  done = true;
               }
            }

            if (!rewrite) {
               return null;
            }

            if (this.isEnv()) {
               for(int i = 0; i < this.envSubstitution.size(); ++i) {
                  ((ThreadLocal)this.envResult.get(i)).set(((Substitution)this.envSubstitution.get(i)).evaluate(matcher, lastMatcher, resolver));
               }
            }

            if (this.isCookie()) {
               this.cookieResult.set(this.cookieSubstitution.evaluate(matcher, lastMatcher, resolver));
            }

            if (this.substitution != null) {
               return this.substitution.evaluate(matcher, lastMatcher, resolver);
            }

            return url;
         }
      }
   }

   public String toString() {
      return "RewriteRule " + this.patternString + " " + this.substitutionString;
   }

   public boolean isChain() {
      return this.chain;
   }

   public void setChain(boolean chain) {
      this.chain = chain;
   }

   public RewriteCond[] getConditions() {
      return this.conditions;
   }

   public void setConditions(RewriteCond[] conditions) {
      this.conditions = conditions;
   }

   public boolean isCookie() {
      return this.cookie;
   }

   public void setCookie(boolean cookie) {
      this.cookie = cookie;
   }

   public String getCookieName() {
      return this.cookieName;
   }

   public void setCookieName(String cookieName) {
      this.cookieName = cookieName;
   }

   public String getCookieValue() {
      return this.cookieValue;
   }

   public void setCookieValue(String cookieValue) {
      this.cookieValue = cookieValue;
   }

   public String getCookieResult() {
      return (String)this.cookieResult.get();
   }

   public boolean isEnv() {
      return this.env;
   }

   public int getEnvSize() {
      return this.envName.size();
   }

   public void setEnv(boolean env) {
      this.env = env;
   }

   public String getEnvName(int i) {
      return (String)this.envName.get(i);
   }

   public void addEnvName(String envName) {
      this.envName.add(envName);
   }

   public String getEnvValue(int i) {
      return (String)this.envValue.get(i);
   }

   public void addEnvValue(String envValue) {
      this.envValue.add(envValue);
   }

   public String getEnvResult(int i) {
      return (String)((ThreadLocal)this.envResult.get(i)).get();
   }

   public boolean isForbidden() {
      return this.forbidden;
   }

   public void setForbidden(boolean forbidden) {
      this.forbidden = forbidden;
   }

   public boolean isGone() {
      return this.gone;
   }

   public void setGone(boolean gone) {
      this.gone = gone;
   }

   public boolean isLast() {
      return this.last;
   }

   public void setLast(boolean last) {
      this.last = last;
   }

   public boolean isNext() {
      return this.next;
   }

   public void setNext(boolean next) {
      this.next = next;
   }

   public boolean isNocase() {
      return this.nocase;
   }

   public void setNocase(boolean nocase) {
      this.nocase = nocase;
   }

   public boolean isNoescape() {
      return this.noescape;
   }

   public void setNoescape(boolean noescape) {
      this.noescape = noescape;
   }

   public boolean isNosubreq() {
      return this.nosubreq;
   }

   public void setNosubreq(boolean nosubreq) {
      this.nosubreq = nosubreq;
   }

   public boolean isQsappend() {
      return this.qsappend;
   }

   public void setQsappend(boolean qsappend) {
      this.qsappend = qsappend;
   }

   public boolean isRedirect() {
      return this.redirect;
   }

   public void setRedirect(boolean redirect) {
      this.redirect = redirect;
   }

   public int getRedirectCode() {
      return this.redirectCode;
   }

   public void setRedirectCode(int redirectCode) {
      this.redirectCode = redirectCode;
   }

   public int getSkip() {
      return this.skip;
   }

   public void setSkip(int skip) {
      this.skip = skip;
   }

   public Substitution getSubstitution() {
      return this.substitution;
   }

   public void setSubstitution(Substitution substitution) {
      this.substitution = substitution;
   }

   public boolean isType() {
      return this.type;
   }

   public void setType(boolean type) {
      this.type = type;
   }

   public String getTypeValue() {
      return this.typeValue;
   }

   public void setTypeValue(String typeValue) {
      this.typeValue = typeValue;
   }

   public String getPatternString() {
      return this.patternString;
   }

   public void setPatternString(String patternString) {
      this.patternString = patternString;
   }

   public String getSubstitutionString() {
      return this.substitutionString;
   }

   public void setSubstitutionString(String substitutionString) {
      this.substitutionString = substitutionString;
   }

   public boolean isHost() {
      return this.host;
   }

   public void setHost(boolean host) {
      this.host = host;
   }

   public String getCookieDomain() {
      return this.cookieDomain;
   }

   public void setCookieDomain(String cookieDomain) {
      this.cookieDomain = cookieDomain;
   }

   public int getCookieLifetime() {
      return this.cookieLifetime;
   }

   public void setCookieLifetime(int cookieLifetime) {
      this.cookieLifetime = cookieLifetime;
   }

   public String getCookiePath() {
      return this.cookiePath;
   }

   public void setCookiePath(String cookiePath) {
      this.cookiePath = cookiePath;
   }

   public boolean isCookieSecure() {
      return this.cookieSecure;
   }

   public void setCookieSecure(boolean cookieSecure) {
      this.cookieSecure = cookieSecure;
   }

   public boolean isCookieHttpOnly() {
      return this.cookieHttpOnly;
   }

   public void setCookieHttpOnly(boolean cookieHttpOnly) {
      this.cookieHttpOnly = cookieHttpOnly;
   }
}
