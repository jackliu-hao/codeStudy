package org.h2.bnf;

import java.util.HashMap;
import org.h2.util.StringUtils;

public class RuleElement implements Rule {
   private final boolean keyword;
   private final String name;
   private Rule link;
   private final int type;

   public RuleElement(String var1, String var2) {
      this.name = var1;
      this.keyword = var1.length() == 1 || var1.equals(StringUtils.toUpperEnglish(var1));
      var2 = StringUtils.toLowerEnglish(var2);
      this.type = var2.startsWith("function") ? 2 : 1;
   }

   public void accept(BnfVisitor var1) {
      var1.visitRuleElement(this.keyword, this.name, this.link);
   }

   public void setLinks(HashMap<String, RuleHead> var1) {
      if (this.link != null) {
         this.link.setLinks(var1);
      }

      if (!this.keyword) {
         String var2 = Bnf.getRuleMapKey(this.name);

         for(int var3 = 0; var3 < var2.length(); ++var3) {
            String var4 = var2.substring(var3);
            RuleHead var5 = (RuleHead)var1.get(var4);
            if (var5 != null) {
               this.link = var5.getRule();
               return;
            }
         }

         throw new AssertionError("Unknown " + this.name + "/" + var2);
      }
   }

   public boolean autoComplete(Sentence var1) {
      var1.stopIfRequired();
      if (!this.keyword) {
         return this.link.autoComplete(var1);
      } else {
         String var2 = var1.getQuery();
         String var3 = var2.trim();
         String var4 = var1.getQueryUpper().trim();
         if (var4.startsWith(this.name)) {
            for(var2 = var2.substring(this.name.length()); !"_".equals(this.name) && Bnf.startWithSpace(var2); var2 = var2.substring(1)) {
            }

            var1.setQuery(var2);
            return true;
         } else {
            if ((var3.length() == 0 || this.name.startsWith(var4)) && var3.length() < this.name.length()) {
               var1.add(this.name, this.name.substring(var3.length()), this.type);
            }

            return false;
         }
      }
   }

   public String toString() {
      return this.name;
   }
}
