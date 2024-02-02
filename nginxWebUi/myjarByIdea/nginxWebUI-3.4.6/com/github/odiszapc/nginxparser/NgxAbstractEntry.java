package com.github.odiszapc.nginxparser;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public abstract class NgxAbstractEntry implements NgxEntry {
   private Collection<NgxToken> tokens = new ArrayList();

   public NgxAbstractEntry(String... rawValues) {
      String[] arr$ = rawValues;
      int len$ = rawValues.length;

      for(int i$ = 0; i$ < len$; ++i$) {
         String val = arr$[i$];
         this.tokens.add(new NgxToken(val));
      }

   }

   public Collection<NgxToken> getTokens() {
      return this.tokens;
   }

   public void addValue(NgxToken token) {
      this.tokens.add(token);
   }

   public void addValue(String value) {
      this.addValue(new NgxToken(value));
   }

   public String toString() {
      StringBuilder builder = new StringBuilder();
      Iterator i$ = this.tokens.iterator();

      while(i$.hasNext()) {
         NgxToken value = (NgxToken)i$.next();
         builder.append(value).append(" ");
      }

      String s = builder.toString();
      return s.substring(0, s.length() - 1);
   }

   public String getName() {
      return this.getTokens().isEmpty() ? null : ((NgxToken)this.getTokens().iterator().next()).toString();
   }

   public List<String> getValues() {
      ArrayList<String> values = new ArrayList();
      if (this.getTokens().size() < 2) {
         return values;
      } else {
         Iterator<NgxToken> it = this.getTokens().iterator();
         it.next();

         while(it.hasNext()) {
            values.add(((NgxToken)it.next()).toString());
         }

         return values;
      }
   }

   public String getValue() {
      Iterator<String> iterator = this.getValues().iterator();
      StringBuilder builder = new StringBuilder();

      while(iterator.hasNext()) {
         builder.append((String)iterator.next());
         if (iterator.hasNext()) {
            builder.append(' ');
         }
      }

      return builder.toString();
   }
}
