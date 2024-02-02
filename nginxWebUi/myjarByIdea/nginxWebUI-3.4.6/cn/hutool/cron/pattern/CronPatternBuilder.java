package cn.hutool.cron.pattern;

import cn.hutool.core.builder.Builder;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.text.StrJoiner;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.StrUtil;

public class CronPatternBuilder implements Builder<String> {
   private static final long serialVersionUID = 1L;
   final String[] parts = new String[7];

   public static CronPatternBuilder of() {
      return new CronPatternBuilder();
   }

   public CronPatternBuilder setValues(Part part, int... values) {
      int[] var3 = values;
      int var4 = values.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         int value = var3[var5];
         part.checkValue(value);
      }

      return this.set(part, ArrayUtil.join((Object)values, ","));
   }

   public CronPatternBuilder setRange(Part part, int begin, int end) {
      Assert.notNull(part);
      part.checkValue(begin);
      part.checkValue(end);
      return this.set(part, StrUtil.format("{}-{}", new Object[]{begin, end}));
   }

   public CronPatternBuilder set(Part part, String value) {
      this.parts[part.ordinal()] = value;
      return this;
   }

   public String build() {
      for(int i = Part.MINUTE.ordinal(); i < Part.YEAR.ordinal(); ++i) {
         if (StrUtil.isBlank(this.parts[i])) {
            this.parts[i] = "*";
         }
      }

      return StrJoiner.of((CharSequence)" ").setNullMode(StrJoiner.NullMode.IGNORE).append((Object[])this.parts).toString();
   }
}
