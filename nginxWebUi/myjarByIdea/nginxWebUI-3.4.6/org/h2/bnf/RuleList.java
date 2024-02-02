package org.h2.bnf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import org.h2.util.Utils;

public class RuleList implements Rule {
   final boolean or;
   final ArrayList<Rule> list = Utils.newSmallArrayList();
   private boolean mapSet;

   public RuleList(Rule var1, Rule var2, boolean var3) {
      if (var1 instanceof RuleList && ((RuleList)var1).or == var3) {
         this.list.addAll(((RuleList)var1).list);
      } else {
         this.list.add(var1);
      }

      if (var2 instanceof RuleList && ((RuleList)var2).or == var3) {
         this.list.addAll(((RuleList)var2).list);
      } else {
         this.list.add(var2);
      }

      this.or = var3;
   }

   public void accept(BnfVisitor var1) {
      var1.visitRuleList(this.or, this.list);
   }

   public void setLinks(HashMap<String, RuleHead> var1) {
      if (!this.mapSet) {
         Iterator var2 = this.list.iterator();

         while(var2.hasNext()) {
            Rule var3 = (Rule)var2.next();
            var3.setLinks(var1);
         }

         this.mapSet = true;
      }

   }

   public boolean autoComplete(Sentence var1) {
      var1.stopIfRequired();
      String var2 = var1.getQuery();
      Iterator var3;
      Rule var4;
      if (this.or) {
         var3 = this.list.iterator();

         do {
            if (!var3.hasNext()) {
               return false;
            }

            var4 = (Rule)var3.next();
            var1.setQuery(var2);
         } while(!var4.autoComplete(var1));

         return true;
      } else {
         var3 = this.list.iterator();

         do {
            if (!var3.hasNext()) {
               return true;
            }

            var4 = (Rule)var3.next();
         } while(var4.autoComplete(var1));

         var1.setQuery(var2);
         return false;
      }
   }

   public String toString() {
      StringBuilder var1 = new StringBuilder();
      int var2 = 0;

      for(int var3 = this.list.size(); var2 < var3; ++var2) {
         if (var2 > 0) {
            if (this.or) {
               var1.append(" | ");
            } else {
               var1.append(' ');
            }
         }

         var1.append(((Rule)this.list.get(var2)).toString());
      }

      return var1.toString();
   }
}
