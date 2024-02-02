package org.h2.bnf.context;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.h2.bnf.Bnf;
import org.h2.bnf.BnfVisitor;
import org.h2.bnf.Rule;
import org.h2.bnf.RuleElement;
import org.h2.bnf.RuleHead;
import org.h2.bnf.RuleList;
import org.h2.bnf.Sentence;
import org.h2.message.DbException;
import org.h2.util.ParserUtil;
import org.h2.util.StringUtils;

public class DbContextRule implements Rule {
   public static final int COLUMN = 0;
   public static final int TABLE = 1;
   public static final int TABLE_ALIAS = 2;
   public static final int NEW_TABLE_ALIAS = 3;
   public static final int COLUMN_ALIAS = 4;
   public static final int SCHEMA = 5;
   public static final int PROCEDURE = 6;
   private final DbContents contents;
   private final int type;
   private String columnType;

   public DbContextRule(DbContents var1, int var2) {
      this.contents = var1;
      this.type = var2;
   }

   public void setColumnType(String var1) {
      this.columnType = var1;
   }

   public void setLinks(HashMap<String, RuleHead> var1) {
   }

   public void accept(BnfVisitor var1) {
   }

   public boolean autoComplete(Sentence var1) {
      String var2 = var1.getQuery();
      String var3 = var2;
      String var4 = var1.getQueryUpper();
      String var6;
      DbSchema[] var8;
      int var9;
      int var10;
      DbSchema var11;
      String var12;
      String var13;
      String var14;
      switch (this.type) {
         case 0:
            HashSet var25 = var1.getTables();
            var6 = null;
            DbTableOrView var28 = var1.getLastMatchedTable();
            if (var28 != null && var28.getColumns() != null) {
               DbColumn[] var30 = var28.getColumns();
               var9 = var30.length;

               for(var10 = 0; var10 < var9; ++var10) {
                  DbColumn var33 = var30[var10];
                  var12 = var4;
                  var13 = StringUtils.toUpperEnglish(var33.getName());
                  if (var33.getQuotedName().length() > var13.length()) {
                     var13 = var33.getQuotedName();
                     var12 = var2;
                  }

                  if (var12.startsWith(var13) && this.testColumnType(var33)) {
                     var14 = var3.substring(var13.length());
                     if (var6 != null && var14.length() >= var6.length()) {
                        if ((var3.length() == 0 || var13.startsWith(var12)) && var3.length() < var13.length()) {
                           var1.add(var33.getName(), var33.getName().substring(var3.length()), 0);
                        }
                     } else {
                        var6 = var14;
                     }
                  }
               }
            }

            var8 = this.contents.getSchemas();
            var9 = var8.length;

            for(var10 = 0; var10 < var9; ++var10) {
               var11 = var8[var10];
               DbTableOrView[] var35 = var11.getTables();
               int var36 = var35.length;

               for(int var37 = 0; var37 < var36; ++var37) {
                  DbTableOrView var15 = var35[var37];
                  if ((var15 == var28 || var25 == null || var25.contains(var15)) && var15 != null && var15.getColumns() != null) {
                     DbColumn[] var16 = var15.getColumns();
                     int var17 = var16.length;

                     for(int var18 = 0; var18 < var17; ++var18) {
                        DbColumn var19 = var16[var18];
                        String var20 = StringUtils.toUpperEnglish(var19.getName());
                        if (this.testColumnType(var19)) {
                           if (var4.startsWith(var20)) {
                              String var21 = var3.substring(var20.length());
                              if (var6 == null || var21.length() < var6.length()) {
                                 var6 = var21;
                              }
                           } else if ((var3.length() == 0 || var20.startsWith(var4)) && var3.length() < var20.length()) {
                              var1.add(var19.getName(), var19.getName().substring(var3.length()), 0);
                           }
                        }
                     }
                  }
               }
            }

            if (var6 != null) {
               var3 = var6;
            }
            break;
         case 1:
            DbSchema var23 = var1.getLastMatchedSchema();
            if (var23 == null) {
               var23 = this.contents.getDefaultSchema();
            }

            DbTableOrView[] var26 = var23.getTables();
            String var27 = null;
            DbTableOrView var29 = null;
            DbTableOrView[] var31 = var26;
            var10 = var26.length;

            for(int var32 = 0; var32 < var10; ++var32) {
               DbTableOrView var34 = var31[var32];
               var13 = var4;
               var14 = StringUtils.toUpperEnglish(var34.getName());
               if (var34.getQuotedName().length() > var14.length()) {
                  var14 = var34.getQuotedName();
                  var13 = var2;
               }

               if (var13.startsWith(var14)) {
                  if (var27 == null || var14.length() > var27.length()) {
                     var27 = var14;
                     var29 = var34;
                  }
               } else if ((var3.length() == 0 || var14.startsWith(var13)) && var3.length() < var14.length()) {
                  var1.add(var34.getQuotedName(), var34.getQuotedName().substring(var3.length()), 0);
               }
            }

            if (var27 != null) {
               var1.setLastMatchedTable(var29);
               var1.addTable(var29);
               var3 = var3.substring(var27.length());
            }
            break;
         case 2:
            var3 = autoCompleteTableAlias(var1, false);
            break;
         case 3:
            var3 = autoCompleteTableAlias(var1, true);
            break;
         case 4:
            int var22 = 0;
            if (var2.indexOf(32) >= 0) {
               while(var22 < var4.length()) {
                  char var24 = var4.charAt(var22);
                  if (var24 != '_' && !Character.isLetterOrDigit(var24)) {
                     break;
                  }

                  ++var22;
               }

               if (var22 != 0) {
                  var6 = var4.substring(0, var22);
                  if (!ParserUtil.isKeyword(var6, false)) {
                     var3 = var2.substring(var6.length());
                  }
               }
            }
            break;
         case 5:
            DbSchema[] var5 = this.contents.getSchemas();
            var6 = null;
            DbSchema var7 = null;
            var8 = var5;
            var9 = var5.length;

            for(var10 = 0; var10 < var9; ++var10) {
               var11 = var8[var10];
               var12 = StringUtils.toUpperEnglish(var11.name);
               if (var4.startsWith(var12)) {
                  if (var6 == null || var12.length() > var6.length()) {
                     var6 = var12;
                     var7 = var11;
                  }
               } else if ((var3.length() == 0 || var12.startsWith(var4)) && var3.length() < var12.length()) {
                  var1.add(var12, var12.substring(var3.length()), this.type);
                  var1.add(var11.quotedName + ".", var11.quotedName.substring(var3.length()) + ".", 0);
               }
            }

            if (var6 != null) {
               var1.setLastMatchedSchema(var7);
               var3 = var3.substring(var6.length());
            }
            break;
         case 6:
            this.autoCompleteProcedure(var1);
            break;
         default:
            throw DbException.getInternalError("type=" + this.type);
      }

      if (var3.equals(var2)) {
         return false;
      } else {
         while(Bnf.startWithSpace(var3)) {
            var3 = var3.substring(1);
         }

         var1.setQuery(var3);
         return true;
      }
   }

   private boolean testColumnType(DbColumn var1) {
      if (this.columnType == null) {
         return true;
      } else {
         String var2 = var1.getDataType();
         if (!this.columnType.contains("CHAR") && !this.columnType.contains("CLOB")) {
            if (!this.columnType.contains("BINARY") && !this.columnType.contains("BLOB")) {
               return var2.contains(this.columnType);
            } else {
               return var2.contains("BINARY") || var2.contains("BLOB");
            }
         } else {
            return var2.contains("CHAR") || var2.contains("CLOB");
         }
      }
   }

   private void autoCompleteProcedure(Sentence var1) {
      DbSchema var2 = var1.getLastMatchedSchema();
      if (var2 == null) {
         var2 = this.contents.getDefaultSchema();
      }

      String var3 = var1.getQueryUpper();
      String var4 = var3;
      int var5 = var3.indexOf(40);
      if (var5 != -1) {
         var4 = StringUtils.trimSubstring(var3, 0, var5);
      }

      RuleElement var6 = new RuleElement("(", "Function");
      RuleElement var7 = new RuleElement(")", "Function");
      RuleElement var8 = new RuleElement(",", "Function");
      DbProcedure[] var9 = var2.getProcedures();
      int var10 = var9.length;

      for(int var11 = 0; var11 < var10; ++var11) {
         DbProcedure var12 = var9[var11];
         String var13 = var12.getName();
         if (var13.startsWith(var4)) {
            RuleElement var14 = new RuleElement(var13, "Function");
            RuleList var15 = new RuleList(var14, var6, false);
            if (var3.contains("(")) {
               DbColumn[] var16 = var12.getParameters();
               int var17 = var16.length;

               for(int var18 = 0; var18 < var17; ++var18) {
                  DbColumn var19 = var16[var18];
                  if (var19.getPosition() > 1) {
                     var15 = new RuleList(var15, var8, false);
                  }

                  DbContextRule var20 = new DbContextRule(this.contents, 0);
                  String var21 = var19.getDataType();
                  if (var21.contains("(")) {
                     var21 = var21.substring(0, var21.indexOf(40));
                  }

                  var20.setColumnType(var21);
                  var15 = new RuleList(var15, var20, false);
               }

               var15 = new RuleList(var15, var7, false);
            }

            var15.autoComplete(var1);
         }
      }

   }

   private static String autoCompleteTableAlias(Sentence var0, boolean var1) {
      String var2 = var0.getQuery();
      String var3 = var0.getQueryUpper();

      int var4;
      for(var4 = 0; var4 < var3.length(); ++var4) {
         char var5 = var3.charAt(var4);
         if (var5 != '_' && !Character.isLetterOrDigit(var5)) {
            break;
         }
      }

      if (var4 == 0) {
         return var2;
      } else {
         String var12 = var3.substring(0, var4);
         if (!"SET".equals(var12) && !ParserUtil.isKeyword(var12, false)) {
            if (var1) {
               var0.addAlias(var12, var0.getLastTable());
            }

            HashMap var6 = var0.getAliases();
            if ((var6 == null || !var6.containsKey(var12)) && var0.getLastTable() != null) {
               HashSet var7 = var0.getTables();
               if (var7 != null) {
                  String var8 = null;
                  Iterator var9 = var7.iterator();

                  while(true) {
                     while(var9.hasNext()) {
                        DbTableOrView var10 = (DbTableOrView)var9.next();
                        String var11 = StringUtils.toUpperEnglish(var10.getName());
                        if (var12.startsWith(var11) && (var8 == null || var11.length() > var8.length())) {
                           var0.setLastMatchedTable(var10);
                           var8 = var11;
                        } else if (var2.length() == 0 || var11.startsWith(var12)) {
                           var0.add(var11 + ".", var11.substring(var2.length()) + ".", 0);
                        }
                     }

                     if (var8 != null) {
                        var2 = var2.substring(var8.length());
                        if (var2.length() == 0) {
                           var0.add(var12 + ".", ".", 0);
                        }

                        return var2;
                     }
                     break;
                  }
               }

               return var2;
            } else if (var1 && var2.length() == var12.length()) {
               return var2;
            } else {
               var2 = var2.substring(var12.length());
               if (var2.length() == 0) {
                  var0.add(var12 + ".", ".", 0);
               }

               return var2;
            }
         } else {
            return var2;
         }
      }
   }
}
