package cn.hutool.core.lang.generator;

import cn.hutool.core.lang.Snowflake;

public class SnowflakeGenerator implements Generator<Long> {
   private final Snowflake snowflake;

   public SnowflakeGenerator() {
      this(0L, 0L);
   }

   public SnowflakeGenerator(long workerId, long dataCenterId) {
      this.snowflake = new Snowflake(workerId, dataCenterId);
   }

   public Long next() {
      return this.snowflake.nextId();
   }
}
