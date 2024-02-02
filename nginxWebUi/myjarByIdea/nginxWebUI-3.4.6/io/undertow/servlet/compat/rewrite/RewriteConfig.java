package io.undertow.servlet.compat.rewrite;

import java.util.Map;

public class RewriteConfig {
   private final RewriteRule[] rules;
   private final Map<String, RewriteMap> maps;

   public RewriteConfig(RewriteRule[] rules, Map<String, RewriteMap> maps) {
      this.rules = rules;
      this.maps = maps;
   }

   public RewriteRule[] getRules() {
      return this.rules;
   }

   public Map<String, RewriteMap> getMaps() {
      return this.maps;
   }

   public String toString() {
      StringBuffer buffer = new StringBuffer();

      for(int i = 0; i < this.rules.length; ++i) {
         for(int j = 0; j < this.rules[i].getConditions().length; ++j) {
            buffer.append(this.rules[i].getConditions()[j].toString()).append("\r\n");
         }

         buffer.append(this.rules[i].toString()).append("\r\n").append("\r\n");
      }

      return buffer.toString();
   }
}
