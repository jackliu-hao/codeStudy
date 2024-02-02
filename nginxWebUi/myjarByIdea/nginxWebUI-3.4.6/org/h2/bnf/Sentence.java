package org.h2.bnf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Objects;
import org.h2.bnf.context.DbSchema;
import org.h2.bnf.context.DbTableOrView;
import org.h2.util.StringUtils;

public class Sentence {
   public static final int CONTEXT = 0;
   public static final int KEYWORD = 1;
   public static final int FUNCTION = 2;
   private static final int MAX_PROCESSING_TIME = 100;
   private final HashMap<String, String> next = new HashMap();
   private String query;
   private String queryUpper;
   private long stopAtNs;
   private DbSchema lastMatchedSchema;
   private DbTableOrView lastMatchedTable;
   private DbTableOrView lastTable;
   private HashSet<DbTableOrView> tables;
   private HashMap<String, DbTableOrView> aliases;

   public void start() {
      this.stopAtNs = System.nanoTime() + 100000000L;
   }

   public void stopIfRequired() {
      if (System.nanoTime() - this.stopAtNs > 0L) {
         throw new IllegalStateException();
      }
   }

   public void add(String var1, String var2, int var3) {
      this.next.put(var3 + "#" + var1, var2);
   }

   public void addAlias(String var1, DbTableOrView var2) {
      if (this.aliases == null) {
         this.aliases = new HashMap();
      }

      this.aliases.put(var1, var2);
   }

   public void addTable(DbTableOrView var1) {
      this.lastTable = var1;
      if (this.tables == null) {
         this.tables = new HashSet();
      }

      this.tables.add(var1);
   }

   public HashSet<DbTableOrView> getTables() {
      return this.tables;
   }

   public HashMap<String, DbTableOrView> getAliases() {
      return this.aliases;
   }

   public DbTableOrView getLastTable() {
      return this.lastTable;
   }

   public DbSchema getLastMatchedSchema() {
      return this.lastMatchedSchema;
   }

   public void setLastMatchedSchema(DbSchema var1) {
      this.lastMatchedSchema = var1;
   }

   public void setLastMatchedTable(DbTableOrView var1) {
      this.lastMatchedTable = var1;
   }

   public DbTableOrView getLastMatchedTable() {
      return this.lastMatchedTable;
   }

   public void setQuery(String var1) {
      if (!Objects.equals(this.query, var1)) {
         this.query = var1;
         this.queryUpper = StringUtils.toUpperEnglish(var1);
      }

   }

   public String getQuery() {
      return this.query;
   }

   public String getQueryUpper() {
      return this.queryUpper;
   }

   public HashMap<String, String> getNext() {
      return this.next;
   }
}
