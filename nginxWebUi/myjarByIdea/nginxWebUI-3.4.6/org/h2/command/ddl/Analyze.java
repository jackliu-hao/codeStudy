package org.h2.command.ddl;

import java.util.Arrays;
import java.util.Iterator;
import org.h2.engine.Database;
import org.h2.engine.SessionLocal;
import org.h2.index.Cursor;
import org.h2.result.Row;
import org.h2.result.SearchRow;
import org.h2.schema.Schema;
import org.h2.table.Column;
import org.h2.table.Table;
import org.h2.table.TableType;
import org.h2.value.DataType;
import org.h2.value.Value;

public class Analyze extends DefineCommand {
   private int sampleRows;
   private Table table;

   public Analyze(SessionLocal var1) {
      super(var1);
      this.sampleRows = var1.getDatabase().getSettings().analyzeSample;
   }

   public void setTable(Table var1) {
      this.table = var1;
   }

   public long update() {
      this.session.getUser().checkAdmin();
      Database var1 = this.session.getDatabase();
      if (this.table != null) {
         analyzeTable(this.session, this.table, this.sampleRows, true);
      } else {
         Iterator var2 = var1.getAllSchemasNoMeta().iterator();

         while(var2.hasNext()) {
            Schema var3 = (Schema)var2.next();
            Iterator var4 = var3.getAllTablesAndViews((SessionLocal)null).iterator();

            while(var4.hasNext()) {
               Table var5 = (Table)var4.next();
               analyzeTable(this.session, var5, this.sampleRows, true);
            }
         }
      }

      return 0L;
   }

   public static void analyzeTable(SessionLocal var0, Table var1, int var2, boolean var3) {
      if (var1.getTableType() == TableType.TABLE && !var1.isHidden() && var0 != null && (var3 || !var0.getDatabase().isSysTableLocked() && !var1.hasSelectTrigger()) && (!var1.isTemporary() || var1.isGlobalTemporary() || var0.findLocalTempTable(var1.getName()) != null) && (!var1.isLockedExclusively() || var1.isLockedExclusivelyBy(var0)) && var0.getUser().hasTableRight(var1, 1) && var0.getCancel() == 0L) {
         var1.lock(var0, 0);
         Column[] var4 = var1.getColumns();
         int var5 = var4.length;
         if (var5 != 0) {
            Cursor var6 = var1.getScanIndex(var0).find(var0, (SearchRow)null, (SearchRow)null);
            if (var6.next()) {
               SelectivityData[] var7 = new SelectivityData[var5];

               for(int var8 = 0; var8 < var5; ++var8) {
                  Column var9 = var4[var8];
                  if (!DataType.isLargeObject(var9.getType().getValueType())) {
                     var7[var8] = new SelectivityData();
                  }
               }

               long var14 = 0L;

               do {
                  Row var10 = var6.get();

                  for(int var11 = 0; var11 < var5; ++var11) {
                     SelectivityData var12 = var7[var11];
                     if (var12 != null) {
                        var12.add(var10.getValue(var11));
                     }
                  }

                  ++var14;
               } while((var2 <= 0 || var14 < (long)var2) && var6.next());

               for(int var15 = 0; var15 < var5; ++var15) {
                  SelectivityData var16 = var7[var15];
                  if (var16 != null) {
                     var4[var15].setSelectivity(var16.getSelectivity(var14));
                  }
               }
            } else {
               for(int var13 = 0; var13 < var5; ++var13) {
                  var4[var13].setSelectivity(0);
               }
            }

            var0.getDatabase().updateMeta(var0, var1);
         }
      }
   }

   public void setTop(int var1) {
      this.sampleRows = var1;
   }

   public int getType() {
      return 21;
   }

   private static final class SelectivityData {
      private long distinctCount;
      private int size;
      private int[] elements = new int[8];
      private boolean zeroElement;
      private int maxSize = 7;

      SelectivityData() {
      }

      void add(Value var1) {
         int var2 = this.currentSize();
         if (var2 >= 10000) {
            this.size = 0;
            Arrays.fill(this.elements, 0);
            this.zeroElement = false;
            this.distinctCount += (long)var2;
         }

         int var3 = var1.hashCode();
         if (var3 == 0) {
            this.zeroElement = true;
         } else {
            if (this.size >= this.maxSize) {
               this.rehash();
            }

            this.add(var3);
         }

      }

      int getSelectivity(long var1) {
         int var3;
         if (var1 == 0L) {
            var3 = 0;
         } else {
            var3 = (int)(100L * (this.distinctCount + (long)this.currentSize()) / var1);
            if (var3 <= 0) {
               var3 = 1;
            }
         }

         return var3;
      }

      private int currentSize() {
         int var1 = this.size;
         if (this.zeroElement) {
            ++var1;
         }

         return var1;
      }

      private void add(int var1) {
         int var2 = this.elements.length;
         int var3 = var2 - 1;
         int var4 = var1 & var3;
         int var5 = 1;

         do {
            int var6 = this.elements[var4];
            if (var6 == 0) {
               ++this.size;
               this.elements[var4] = var1;
               return;
            }

            if (var6 == var1) {
               return;
            }

            var4 = var4 + var5++ & var3;
         } while(var5 <= var2);

      }

      private void rehash() {
         this.size = 0;
         int[] var1 = this.elements;
         int var2 = var1.length << 1;
         this.elements = new int[var2];
         this.maxSize = (int)((long)var2 * 90L / 100L);
         int[] var3 = var1;
         int var4 = var1.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            int var6 = var3[var5];
            if (var6 != 0) {
               this.add(var6);
            }
         }

      }
   }
}
