package oshi.driver.linux.proc;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.ExecutingCommand;
import oshi.util.Memoizer;

@ThreadSafe
public final class UserGroupInfo {
   private static final Supplier<Map<String, String>> usersIdMap;
   private static final Supplier<Map<String, String>> groupsIdMap;

   private UserGroupInfo() {
   }

   public static String getUser(String userId) {
      return (String)((Map)usersIdMap.get()).getOrDefault(userId, "unknown");
   }

   public static String getGroupName(String groupId) {
      return (String)((Map)groupsIdMap.get()).getOrDefault(groupId, "unknown");
   }

   private static Map<String, String> getUserMap() {
      HashMap<String, String> userMap = new HashMap();
      List<String> passwd = ExecutingCommand.runNative("getent passwd");
      Iterator var2 = passwd.iterator();

      while(var2.hasNext()) {
         String entry = (String)var2.next();
         String[] split = entry.split(":");
         if (split.length > 2) {
            String userName = split[0];
            String uid = split[2];
            userMap.putIfAbsent(uid, userName);
         }
      }

      return userMap;
   }

   private static Map<String, String> getGroupMap() {
      Map<String, String> groupMap = new HashMap();
      List<String> group = ExecutingCommand.runNative("getent group");
      Iterator var2 = group.iterator();

      while(var2.hasNext()) {
         String entry = (String)var2.next();
         String[] split = entry.split(":");
         if (split.length > 2) {
            String groupName = split[0];
            String gid = split[2];
            groupMap.putIfAbsent(gid, groupName);
         }
      }

      return groupMap;
   }

   static {
      usersIdMap = Memoizer.memoize(UserGroupInfo::getUserMap, TimeUnit.MINUTES.toNanos(1L));
      groupsIdMap = Memoizer.memoize(UserGroupInfo::getGroupMap, TimeUnit.MINUTES.toNanos(1L));
   }
}
