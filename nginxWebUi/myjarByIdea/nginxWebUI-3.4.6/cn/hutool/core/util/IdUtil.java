package cn.hutool.core.util;

import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.lang.ObjectId;
import cn.hutool.core.lang.Singleton;
import cn.hutool.core.lang.Snowflake;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.lang.id.NanoId;
import cn.hutool.core.net.NetUtil;

public class IdUtil {
   public static String randomUUID() {
      return UUID.randomUUID().toString();
   }

   public static String simpleUUID() {
      return UUID.randomUUID().toString(true);
   }

   public static String fastUUID() {
      return UUID.fastUUID().toString();
   }

   public static String fastSimpleUUID() {
      return UUID.fastUUID().toString(true);
   }

   public static String objectId() {
      return ObjectId.next();
   }

   /** @deprecated */
   @Deprecated
   public static Snowflake createSnowflake(long workerId, long datacenterId) {
      return new Snowflake(workerId, datacenterId);
   }

   public static Snowflake getSnowflake(long workerId, long datacenterId) {
      return (Snowflake)Singleton.get(Snowflake.class, workerId, datacenterId);
   }

   public static Snowflake getSnowflake(long workerId) {
      return (Snowflake)Singleton.get(Snowflake.class, workerId);
   }

   public static Snowflake getSnowflake() {
      return (Snowflake)Singleton.get(Snowflake.class);
   }

   public static long getDataCenterId(long maxDatacenterId) {
      Assert.isTrue(maxDatacenterId > 0L, "maxDatacenterId must be > 0");
      if (maxDatacenterId == Long.MAX_VALUE) {
         --maxDatacenterId;
      }

      long id = 1L;
      byte[] mac = null;

      try {
         mac = NetUtil.getLocalHardwareAddress();
      } catch (UtilException var6) {
      }

      if (null != mac) {
         id = (255L & (long)mac[mac.length - 2] | 65280L & (long)mac[mac.length - 1] << 8) >> 6;
         id %= maxDatacenterId + 1L;
      }

      return id;
   }

   public static long getWorkerId(long datacenterId, long maxWorkerId) {
      StringBuilder mpid = new StringBuilder();
      mpid.append(datacenterId);

      try {
         mpid.append(RuntimeUtil.getPid());
      } catch (UtilException var6) {
      }

      return (long)(mpid.toString().hashCode() & '\uffff') % (maxWorkerId + 1L);
   }

   public static String nanoId() {
      return NanoId.randomNanoId();
   }

   public static String nanoId(int size) {
      return NanoId.randomNanoId(size);
   }

   public static long getSnowflakeNextId() {
      return getSnowflake().nextId();
   }

   public static String getSnowflakeNextIdStr() {
      return getSnowflake().nextIdStr();
   }
}
