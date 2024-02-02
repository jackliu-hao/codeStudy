package io.undertow.servlet.handlers;

import javax.servlet.http.MappingMatch;

public class ServletPathMatch {
   private final String matched;
   private final String remaining;
   private final boolean requiredWelcomeFileMatch;
   private final ServletChain servletChain;
   private final String rewriteLocation;
   private final Type type;

   public ServletPathMatch(ServletChain target, String uri, boolean requiredWelcomeFileMatch) {
      this.servletChain = target;
      this.requiredWelcomeFileMatch = requiredWelcomeFileMatch;
      this.type = ServletPathMatch.Type.NORMAL;
      this.rewriteLocation = null;
      if (target.getServletPath() == null) {
         this.matched = uri;
         this.remaining = null;
      } else {
         this.matched = target.getServletPath();
         if (uri.length() == this.matched.length()) {
            this.remaining = null;
         } else {
            this.remaining = uri.substring(this.matched.length());
         }
      }

   }

   public ServletPathMatch(ServletChain target, String matched, String remaining, Type type, String rewriteLocation) {
      this.servletChain = target;
      this.matched = matched;
      this.remaining = remaining;
      this.requiredWelcomeFileMatch = false;
      this.type = type;
      this.rewriteLocation = rewriteLocation;
   }

   public String getMatched() {
      return this.matched;
   }

   public String getRemaining() {
      return this.remaining;
   }

   public boolean isRequiredWelcomeFileMatch() {
      return this.requiredWelcomeFileMatch;
   }

   public ServletChain getServletChain() {
      return this.servletChain;
   }

   public String getRewriteLocation() {
      return this.rewriteLocation;
   }

   public Type getType() {
      return this.type;
   }

   public String getMatchString() {
      return this.servletChain.getPattern();
   }

   public MappingMatch getMappingMatch() {
      return this.servletChain.getMappingMatch();
   }

   public static enum Type {
      NORMAL,
      REDIRECT,
      REWRITE;
   }
}
