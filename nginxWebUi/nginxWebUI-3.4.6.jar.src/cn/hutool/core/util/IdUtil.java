/*     */ package cn.hutool.core.util;
/*     */ 
/*     */ import cn.hutool.core.exceptions.UtilException;
/*     */ import cn.hutool.core.lang.Assert;
/*     */ import cn.hutool.core.lang.ObjectId;
/*     */ import cn.hutool.core.lang.Singleton;
/*     */ import cn.hutool.core.lang.Snowflake;
/*     */ import cn.hutool.core.lang.UUID;
/*     */ import cn.hutool.core.lang.id.NanoId;
/*     */ import cn.hutool.core.net.NetUtil;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class IdUtil
/*     */ {
/*     */   public static String randomUUID() {
/*  35 */     return UUID.randomUUID().toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String simpleUUID() {
/*  44 */     return UUID.randomUUID().toString(true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String fastUUID() {
/*  54 */     return UUID.fastUUID().toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String fastSimpleUUID() {
/*  64 */     return UUID.fastUUID().toString(true);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String objectId() {
/*  83 */     return ObjectId.next();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public static Snowflake createSnowflake(long workerId, long datacenterId) {
/* 114 */     return new Snowflake(workerId, datacenterId);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Snowflake getSnowflake(long workerId, long datacenterId) {
/* 141 */     return (Snowflake)Singleton.get(Snowflake.class, new Object[] { Long.valueOf(workerId), Long.valueOf(datacenterId) });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Snowflake getSnowflake(long workerId) {
/* 167 */     return (Snowflake)Singleton.get(Snowflake.class, new Object[] { Long.valueOf(workerId) });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Snowflake getSnowflake() {
/* 192 */     return (Snowflake)Singleton.get(Snowflake.class, new Object[0]);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long getDataCenterId(long maxDatacenterId) {
/* 207 */     Assert.isTrue((maxDatacenterId > 0L), "maxDatacenterId must be > 0", new Object[0]);
/* 208 */     if (maxDatacenterId == Long.MAX_VALUE) {
/* 209 */       maxDatacenterId--;
/*     */     }
/* 211 */     long id = 1L;
/* 212 */     byte[] mac = null;
/*     */     try {
/* 214 */       mac = NetUtil.getLocalHardwareAddress();
/* 215 */     } catch (UtilException utilException) {}
/*     */ 
/*     */     
/* 218 */     if (null != mac) {
/* 219 */       id = (0xFFL & mac[mac.length - 2] | 0xFF00L & mac[mac.length - 1] << 8L) >> 6L;
/*     */       
/* 221 */       id %= maxDatacenterId + 1L;
/*     */     } 
/*     */     
/* 224 */     return id;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long getWorkerId(long datacenterId, long maxWorkerId) {
/* 241 */     StringBuilder mpid = new StringBuilder();
/* 242 */     mpid.append(datacenterId);
/*     */     try {
/* 244 */       mpid.append(RuntimeUtil.getPid());
/* 245 */     } catch (UtilException utilException) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 251 */     return (mpid.toString().hashCode() & 0xFFFF) % (maxWorkerId + 1L);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String nanoId() {
/* 263 */     return NanoId.randomNanoId();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String nanoId(int size) {
/* 274 */     return NanoId.randomNanoId(size);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static long getSnowflakeNextId() {
/* 285 */     return getSnowflake().nextId();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getSnowflakeNextIdStr() {
/* 296 */     return getSnowflake().nextIdStr();
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\cor\\util\IdUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */