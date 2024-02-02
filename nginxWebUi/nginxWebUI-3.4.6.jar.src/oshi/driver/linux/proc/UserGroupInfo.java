/*     */ package oshi.driver.linux.proc;
/*     */ 
/*     */ import java.util.HashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import java.util.concurrent.TimeUnit;
/*     */ import java.util.function.Supplier;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.util.ExecutingCommand;
/*     */ import oshi.util.Memoizer;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ @ThreadSafe
/*     */ public final class UserGroupInfo
/*     */ {
/*  46 */   private static final Supplier<Map<String, String>> usersIdMap = Memoizer.memoize(UserGroupInfo::getUserMap, TimeUnit.MINUTES
/*  47 */       .toNanos(1L));
/*  48 */   private static final Supplier<Map<String, String>> groupsIdMap = Memoizer.memoize(UserGroupInfo::getGroupMap, TimeUnit.MINUTES
/*  49 */       .toNanos(1L));
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
/*     */   public static String getUser(String userId) {
/*  63 */     return (String)((Map)usersIdMap.get()).getOrDefault(userId, "unknown");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getGroupName(String groupId) {
/*  74 */     return (String)((Map)groupsIdMap.get()).getOrDefault(groupId, "unknown");
/*     */   }
/*     */   
/*     */   private static Map<String, String> getUserMap() {
/*  78 */     HashMap<String, String> userMap = new HashMap<>();
/*  79 */     List<String> passwd = ExecutingCommand.runNative("getent passwd");
/*     */     
/*  81 */     for (String entry : passwd) {
/*  82 */       String[] split = entry.split(":");
/*  83 */       if (split.length > 2) {
/*  84 */         String userName = split[0];
/*  85 */         String uid = split[2];
/*     */ 
/*     */         
/*  88 */         userMap.putIfAbsent(uid, userName);
/*     */       } 
/*     */     } 
/*  91 */     return userMap;
/*     */   }
/*     */   
/*     */   private static Map<String, String> getGroupMap() {
/*  95 */     Map<String, String> groupMap = new HashMap<>();
/*  96 */     List<String> group = ExecutingCommand.runNative("getent group");
/*     */     
/*  98 */     for (String entry : group) {
/*  99 */       String[] split = entry.split(":");
/* 100 */       if (split.length > 2) {
/* 101 */         String groupName = split[0];
/* 102 */         String gid = split[2];
/* 103 */         groupMap.putIfAbsent(gid, groupName);
/*     */       } 
/*     */     } 
/* 106 */     return groupMap;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\driver\linux\proc\UserGroupInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */