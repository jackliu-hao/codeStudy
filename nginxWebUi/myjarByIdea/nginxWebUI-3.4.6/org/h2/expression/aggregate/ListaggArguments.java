package org.h2.expression.aggregate;

public final class ListaggArguments {
   private String separator;
   private boolean onOverflowTruncate;
   private String filter;
   private boolean withoutCount;

   public void setSeparator(String var1) {
      this.separator = var1 != null ? var1 : "";
   }

   public String getSeparator() {
      return this.separator;
   }

   public String getEffectiveSeparator() {
      return this.separator != null ? this.separator : ",";
   }

   public void setOnOverflowTruncate(boolean var1) {
      this.onOverflowTruncate = var1;
   }

   public boolean getOnOverflowTruncate() {
      return this.onOverflowTruncate;
   }

   public void setFilter(String var1) {
      this.filter = var1 != null ? var1 : "";
   }

   public String getFilter() {
      return this.filter;
   }

   public String getEffectiveFilter() {
      return this.filter != null ? this.filter : "...";
   }

   public void setWithoutCount(boolean var1) {
      this.withoutCount = var1;
   }

   public boolean isWithoutCount() {
      return this.withoutCount;
   }
}
