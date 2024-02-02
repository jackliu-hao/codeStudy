package cn.hutool.core.io.unit;

import cn.hutool.core.util.StrUtil;

public enum DataUnit {
   BYTES("B", DataSize.ofBytes(1L)),
   KILOBYTES("KB", DataSize.ofKilobytes(1L)),
   MEGABYTES("MB", DataSize.ofMegabytes(1L)),
   GIGABYTES("GB", DataSize.ofGigabytes(1L)),
   TERABYTES("TB", DataSize.ofTerabytes(1L));

   public static final String[] UNIT_NAMES = new String[]{"B", "KB", "MB", "GB", "TB", "PB", "EB"};
   private final String suffix;
   private final DataSize size;

   private DataUnit(String suffix, DataSize size) {
      this.suffix = suffix;
      this.size = size;
   }

   DataSize size() {
      return this.size;
   }

   public static DataUnit fromSuffix(String suffix) {
      DataUnit[] var1 = values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         DataUnit candidate = var1[var3];
         if (StrUtil.startWithIgnoreCase(candidate.suffix, suffix)) {
            return candidate;
         }
      }

      throw new IllegalArgumentException("Unknown data unit suffix '" + suffix + "'");
   }
}
