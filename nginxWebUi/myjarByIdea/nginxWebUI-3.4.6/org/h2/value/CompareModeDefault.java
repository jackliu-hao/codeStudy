package org.h2.value;

import java.text.CollationKey;
import java.text.Collator;
import org.h2.engine.SysProperties;
import org.h2.message.DbException;
import org.h2.util.SmallLRUCache;

public class CompareModeDefault extends CompareMode {
   private final Collator collator;
   private final SmallLRUCache<String, CollationKey> collationKeys;
   private volatile CompareModeDefault caseInsensitive;

   protected CompareModeDefault(String var1, int var2) {
      super(var1, var2);
      this.collator = CompareMode.getCollator(var1);
      if (this.collator == null) {
         throw DbException.getInternalError(var1);
      } else {
         this.collator.setStrength(var2);
         int var3 = SysProperties.COLLATOR_CACHE_SIZE;
         if (var3 != 0) {
            this.collationKeys = SmallLRUCache.newInstance(var3);
         } else {
            this.collationKeys = null;
         }

      }
   }

   public int compareString(String var1, String var2, boolean var3) {
      if (var3 && this.getStrength() > 1) {
         CompareModeDefault var7 = this.caseInsensitive;
         if (var7 == null) {
            this.caseInsensitive = var7 = new CompareModeDefault(this.getName(), 1);
         }

         return var7.compareString(var1, var2, false);
      } else {
         int var4;
         if (this.collationKeys != null) {
            CollationKey var5 = this.getKey(var1);
            CollationKey var6 = this.getKey(var2);
            var4 = var5.compareTo(var6);
         } else {
            var4 = this.collator.compare(var1, var2);
         }

         return var4;
      }
   }

   public boolean equalsChars(String var1, int var2, String var3, int var4, boolean var5) {
      return this.compareString(var1.substring(var2, var2 + 1), var3.substring(var4, var4 + 1), var5) == 0;
   }

   private CollationKey getKey(String var1) {
      synchronized(this.collationKeys) {
         CollationKey var3 = (CollationKey)this.collationKeys.get(var1);
         if (var3 == null) {
            var3 = this.collator.getCollationKey(var1);
            this.collationKeys.put(var1, var3);
         }

         return var3;
      }
   }
}
