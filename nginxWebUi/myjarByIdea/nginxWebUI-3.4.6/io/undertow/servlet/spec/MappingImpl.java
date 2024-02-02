package io.undertow.servlet.spec;

import javax.servlet.http.HttpServletMapping;
import javax.servlet.http.MappingMatch;

public class MappingImpl implements HttpServletMapping {
   private final String matchValue;
   private final String pattern;
   private final MappingMatch matchType;
   private final String servletName;

   public MappingImpl(String matchValue, String pattern, MappingMatch matchType, String servletName) {
      this.matchValue = matchValue;
      this.pattern = pattern;
      this.matchType = matchType;
      this.servletName = servletName;
   }

   public String getMatchValue() {
      return this.matchValue;
   }

   public String getPattern() {
      return this.pattern;
   }

   public String getServletName() {
      return this.servletName;
   }

   public MappingMatch getMappingMatch() {
      return this.matchType;
   }
}
