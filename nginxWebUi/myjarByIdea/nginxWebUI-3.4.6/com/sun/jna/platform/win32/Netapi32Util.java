package com.sun.jna.platform.win32;

import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import java.util.ArrayList;

public abstract class Netapi32Util {
   public static String getDCName() {
      return getDCName((String)null, (String)null);
   }

   public static String getDCName(String serverName, String domainName) {
      PointerByReference bufptr = new PointerByReference();

      String var4;
      try {
         int rc = Netapi32.INSTANCE.NetGetDCName(domainName, serverName, bufptr);
         if (0 != rc) {
            throw new Win32Exception(rc);
         }

         var4 = bufptr.getValue().getWideString(0L);
      } finally {
         if (0 != Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue())) {
            throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
         }

      }

      return var4;
   }

   public static int getJoinStatus() {
      return getJoinStatus((String)null);
   }

   public static int getJoinStatus(String computerName) {
      PointerByReference lpNameBuffer = new PointerByReference();
      IntByReference bufferType = new IntByReference();
      boolean var9 = false;

      int var4;
      try {
         var9 = true;
         int rc = Netapi32.INSTANCE.NetGetJoinInformation(computerName, lpNameBuffer, bufferType);
         if (0 != rc) {
            throw new Win32Exception(rc);
         }

         var4 = bufferType.getValue();
         var9 = false;
      } finally {
         if (var9) {
            if (lpNameBuffer.getPointer() != null) {
               int rc = Netapi32.INSTANCE.NetApiBufferFree(lpNameBuffer.getValue());
               if (0 != rc) {
                  throw new Win32Exception(rc);
               }
            }

         }
      }

      if (lpNameBuffer.getPointer() != null) {
         int rc = Netapi32.INSTANCE.NetApiBufferFree(lpNameBuffer.getValue());
         if (0 != rc) {
            throw new Win32Exception(rc);
         }
      }

      return var4;
   }

   public static String getDomainName(String computerName) {
      PointerByReference lpNameBuffer = new PointerByReference();
      IntByReference bufferType = new IntByReference();
      boolean var9 = false;

      String var4;
      try {
         var9 = true;
         int rc = Netapi32.INSTANCE.NetGetJoinInformation(computerName, lpNameBuffer, bufferType);
         if (0 != rc) {
            throw new Win32Exception(rc);
         }

         var4 = lpNameBuffer.getValue().getWideString(0L);
         var9 = false;
      } finally {
         if (var9) {
            if (lpNameBuffer.getPointer() != null) {
               int rc = Netapi32.INSTANCE.NetApiBufferFree(lpNameBuffer.getValue());
               if (0 != rc) {
                  throw new Win32Exception(rc);
               }
            }

         }
      }

      if (lpNameBuffer.getPointer() != null) {
         int rc = Netapi32.INSTANCE.NetApiBufferFree(lpNameBuffer.getValue());
         if (0 != rc) {
            throw new Win32Exception(rc);
         }
      }

      return var4;
   }

   public static LocalGroup[] getLocalGroups() {
      return getLocalGroups((String)null);
   }

   public static LocalGroup[] getLocalGroups(String serverName) {
      PointerByReference bufptr = new PointerByReference();
      IntByReference entriesRead = new IntByReference();
      IntByReference totalEntries = new IntByReference();
      boolean var16 = false;

      LocalGroup[] var18;
      try {
         var16 = true;
         int rc = Netapi32.INSTANCE.NetLocalGroupEnum(serverName, 1, bufptr, -1, entriesRead, totalEntries, (IntByReference)null);
         if (0 != rc || bufptr.getValue() == Pointer.NULL) {
            throw new Win32Exception(rc);
         }

         ArrayList<LocalGroup> result = new ArrayList();
         if (entriesRead.getValue() > 0) {
            LMAccess.LOCALGROUP_INFO_1 group = new LMAccess.LOCALGROUP_INFO_1(bufptr.getValue());
            LMAccess.LOCALGROUP_INFO_1[] groups = (LMAccess.LOCALGROUP_INFO_1[])((LMAccess.LOCALGROUP_INFO_1[])group.toArray(entriesRead.getValue()));
            LMAccess.LOCALGROUP_INFO_1[] var8 = groups;
            int var9 = groups.length;

            for(int var10 = 0; var10 < var9; ++var10) {
               LMAccess.LOCALGROUP_INFO_1 lgpi = var8[var10];
               LocalGroup lgp = new LocalGroup();
               lgp.name = lgpi.lgrui1_name;
               lgp.comment = lgpi.lgrui1_comment;
               result.add(lgp);
            }
         }

         var18 = (LocalGroup[])result.toArray(new LocalGroup[0]);
         var16 = false;
      } finally {
         if (var16) {
            if (bufptr.getValue() != Pointer.NULL) {
               int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
               if (0 != rc) {
                  throw new Win32Exception(rc);
               }
            }

         }
      }

      if (bufptr.getValue() != Pointer.NULL) {
         int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
         if (0 != rc) {
            throw new Win32Exception(rc);
         }
      }

      return var18;
   }

   public static Group[] getGlobalGroups() {
      return getGlobalGroups((String)null);
   }

   public static Group[] getGlobalGroups(String serverName) {
      PointerByReference bufptr = new PointerByReference();
      IntByReference entriesRead = new IntByReference();
      IntByReference totalEntries = new IntByReference();
      boolean var16 = false;

      Group[] var18;
      try {
         var16 = true;
         int rc = Netapi32.INSTANCE.NetGroupEnum(serverName, 1, bufptr, -1, entriesRead, totalEntries, (IntByReference)null);
         if (0 != rc || bufptr.getValue() == Pointer.NULL) {
            throw new Win32Exception(rc);
         }

         ArrayList<LocalGroup> result = new ArrayList();
         if (entriesRead.getValue() > 0) {
            LMAccess.GROUP_INFO_1 group = new LMAccess.GROUP_INFO_1(bufptr.getValue());
            LMAccess.GROUP_INFO_1[] groups = (LMAccess.GROUP_INFO_1[])((LMAccess.GROUP_INFO_1[])group.toArray(entriesRead.getValue()));
            LMAccess.GROUP_INFO_1[] var8 = groups;
            int var9 = groups.length;

            for(int var10 = 0; var10 < var9; ++var10) {
               LMAccess.GROUP_INFO_1 lgpi = var8[var10];
               LocalGroup lgp = new LocalGroup();
               lgp.name = lgpi.grpi1_name;
               lgp.comment = lgpi.grpi1_comment;
               result.add(lgp);
            }
         }

         var18 = (Group[])result.toArray(new LocalGroup[0]);
         var16 = false;
      } finally {
         if (var16) {
            if (bufptr.getValue() != Pointer.NULL) {
               int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
               if (0 != rc) {
                  throw new Win32Exception(rc);
               }
            }

         }
      }

      if (bufptr.getValue() != Pointer.NULL) {
         int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
         if (0 != rc) {
            throw new Win32Exception(rc);
         }
      }

      return var18;
   }

   public static User[] getUsers() {
      return getUsers((String)null);
   }

   public static User[] getUsers(String serverName) {
      PointerByReference bufptr = new PointerByReference();
      IntByReference entriesRead = new IntByReference();
      IntByReference totalEntries = new IntByReference();
      boolean var16 = false;

      User[] var18;
      try {
         var16 = true;
         int rc = Netapi32.INSTANCE.NetUserEnum(serverName, 1, 0, bufptr, -1, entriesRead, totalEntries, (IntByReference)null);
         if (0 != rc || bufptr.getValue() == Pointer.NULL) {
            throw new Win32Exception(rc);
         }

         ArrayList<User> result = new ArrayList();
         if (entriesRead.getValue() > 0) {
            LMAccess.USER_INFO_1 user = new LMAccess.USER_INFO_1(bufptr.getValue());
            LMAccess.USER_INFO_1[] users = (LMAccess.USER_INFO_1[])((LMAccess.USER_INFO_1[])user.toArray(entriesRead.getValue()));
            LMAccess.USER_INFO_1[] var8 = users;
            int var9 = users.length;

            for(int var10 = 0; var10 < var9; ++var10) {
               LMAccess.USER_INFO_1 lu = var8[var10];
               User auser = new User();
               if (lu.usri1_name != null) {
                  auser.name = lu.usri1_name;
               }

               result.add(auser);
            }
         }

         var18 = (User[])result.toArray(new User[0]);
         var16 = false;
      } finally {
         if (var16) {
            if (bufptr.getValue() != Pointer.NULL) {
               int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
               if (0 != rc) {
                  throw new Win32Exception(rc);
               }
            }

         }
      }

      if (bufptr.getValue() != Pointer.NULL) {
         int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
         if (0 != rc) {
            throw new Win32Exception(rc);
         }
      }

      return var18;
   }

   public static Group[] getCurrentUserLocalGroups() {
      return getUserLocalGroups(Secur32Util.getUserNameEx(2));
   }

   public static Group[] getUserLocalGroups(String userName) {
      return getUserLocalGroups(userName, (String)null);
   }

   public static Group[] getUserLocalGroups(String userName, String serverName) {
      PointerByReference bufptr = new PointerByReference();
      IntByReference entriesread = new IntByReference();
      IntByReference totalentries = new IntByReference();
      boolean var17 = false;

      Group[] var19;
      try {
         var17 = true;
         int rc = Netapi32.INSTANCE.NetUserGetLocalGroups(serverName, userName, 0, 0, bufptr, -1, entriesread, totalentries);
         if (rc != 0) {
            throw new Win32Exception(rc);
         }

         ArrayList<Group> result = new ArrayList();
         if (entriesread.getValue() > 0) {
            LMAccess.LOCALGROUP_USERS_INFO_0 lgroup = new LMAccess.LOCALGROUP_USERS_INFO_0(bufptr.getValue());
            LMAccess.LOCALGROUP_USERS_INFO_0[] lgroups = (LMAccess.LOCALGROUP_USERS_INFO_0[])((LMAccess.LOCALGROUP_USERS_INFO_0[])lgroup.toArray(entriesread.getValue()));
            LMAccess.LOCALGROUP_USERS_INFO_0[] var9 = lgroups;
            int var10 = lgroups.length;

            for(int var11 = 0; var11 < var10; ++var11) {
               LMAccess.LOCALGROUP_USERS_INFO_0 lgpi = var9[var11];
               LocalGroup lgp = new LocalGroup();
               if (lgpi.lgrui0_name != null) {
                  lgp.name = lgpi.lgrui0_name;
               }

               result.add(lgp);
            }
         }

         var19 = (Group[])result.toArray(new Group[0]);
         var17 = false;
      } finally {
         if (var17) {
            if (bufptr.getValue() != Pointer.NULL) {
               int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
               if (0 != rc) {
                  throw new Win32Exception(rc);
               }
            }

         }
      }

      if (bufptr.getValue() != Pointer.NULL) {
         int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
         if (0 != rc) {
            throw new Win32Exception(rc);
         }
      }

      return var19;
   }

   public static Group[] getUserGroups(String userName) {
      return getUserGroups(userName, (String)null);
   }

   public static Group[] getUserGroups(String userName, String serverName) {
      PointerByReference bufptr = new PointerByReference();
      IntByReference entriesread = new IntByReference();
      IntByReference totalentries = new IntByReference();
      boolean var17 = false;

      Group[] var19;
      try {
         var17 = true;
         int rc = Netapi32.INSTANCE.NetUserGetGroups(serverName, userName, 0, bufptr, -1, entriesread, totalentries);
         if (rc != 0) {
            throw new Win32Exception(rc);
         }

         ArrayList<Group> result = new ArrayList();
         if (entriesread.getValue() > 0) {
            LMAccess.GROUP_USERS_INFO_0 lgroup = new LMAccess.GROUP_USERS_INFO_0(bufptr.getValue());
            LMAccess.GROUP_USERS_INFO_0[] lgroups = (LMAccess.GROUP_USERS_INFO_0[])((LMAccess.GROUP_USERS_INFO_0[])lgroup.toArray(entriesread.getValue()));
            LMAccess.GROUP_USERS_INFO_0[] var9 = lgroups;
            int var10 = lgroups.length;

            for(int var11 = 0; var11 < var10; ++var11) {
               LMAccess.GROUP_USERS_INFO_0 lgpi = var9[var11];
               Group lgp = new Group();
               if (lgpi.grui0_name != null) {
                  lgp.name = lgpi.grui0_name;
               }

               result.add(lgp);
            }
         }

         var19 = (Group[])result.toArray(new Group[0]);
         var17 = false;
      } finally {
         if (var17) {
            if (bufptr.getValue() != Pointer.NULL) {
               int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
               if (0 != rc) {
                  throw new Win32Exception(rc);
               }
            }

         }
      }

      if (bufptr.getValue() != Pointer.NULL) {
         int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
         if (0 != rc) {
            throw new Win32Exception(rc);
         }
      }

      return var19;
   }

   public static DomainController getDC() {
      DsGetDC.PDOMAIN_CONTROLLER_INFO pdci = new DsGetDC.PDOMAIN_CONTROLLER_INFO();
      int rc = Netapi32.INSTANCE.DsGetDcName((String)null, (String)null, (Guid.GUID)null, (String)null, 0, pdci);
      if (0 != rc) {
         throw new Win32Exception(rc);
      } else {
         DomainController dc = new DomainController();
         dc.address = pdci.dci.DomainControllerAddress;
         dc.addressType = pdci.dci.DomainControllerAddressType;
         dc.clientSiteName = pdci.dci.ClientSiteName;
         dc.dnsForestName = pdci.dci.DnsForestName;
         dc.domainGuid = pdci.dci.DomainGuid;
         dc.domainName = pdci.dci.DomainName;
         dc.flags = pdci.dci.Flags;
         dc.name = pdci.dci.DomainControllerName;
         rc = Netapi32.INSTANCE.NetApiBufferFree(pdci.dci.getPointer());
         if (0 != rc) {
            throw new Win32Exception(rc);
         } else {
            return dc;
         }
      }
   }

   public static DomainTrust[] getDomainTrusts() {
      return getDomainTrusts((String)null);
   }

   public static DomainTrust[] getDomainTrusts(String serverName) {
      IntByReference domainTrustCount = new IntByReference();
      PointerByReference domainsPointerRef = new PointerByReference();
      int rc = Netapi32.INSTANCE.DsEnumerateDomainTrusts(serverName, 63, domainsPointerRef, domainTrustCount);
      if (0 != rc) {
         throw new Win32Exception(rc);
      } else {
         DomainTrust[] var15;
         try {
            ArrayList<DomainTrust> trusts = new ArrayList(domainTrustCount.getValue());
            if (domainTrustCount.getValue() > 0) {
               DsGetDC.DS_DOMAIN_TRUSTS domainTrustRefs = new DsGetDC.DS_DOMAIN_TRUSTS(domainsPointerRef.getValue());
               DsGetDC.DS_DOMAIN_TRUSTS[] domainTrusts = (DsGetDC.DS_DOMAIN_TRUSTS[])((DsGetDC.DS_DOMAIN_TRUSTS[])domainTrustRefs.toArray(new DsGetDC.DS_DOMAIN_TRUSTS[domainTrustCount.getValue()]));
               DsGetDC.DS_DOMAIN_TRUSTS[] var7 = domainTrusts;
               int var8 = domainTrusts.length;

               for(int var9 = 0; var9 < var8; ++var9) {
                  DsGetDC.DS_DOMAIN_TRUSTS domainTrust = var7[var9];
                  DomainTrust t = new DomainTrust();
                  if (domainTrust.DnsDomainName != null) {
                     t.DnsDomainName = domainTrust.DnsDomainName;
                  }

                  if (domainTrust.NetbiosDomainName != null) {
                     t.NetbiosDomainName = domainTrust.NetbiosDomainName;
                  }

                  t.DomainSid = domainTrust.DomainSid;
                  if (domainTrust.DomainSid != null) {
                     t.DomainSidString = Advapi32Util.convertSidToStringSid(domainTrust.DomainSid);
                  }

                  t.DomainGuid = domainTrust.DomainGuid;
                  if (domainTrust.DomainGuid != null) {
                     t.DomainGuidString = Ole32Util.getStringFromGUID(domainTrust.DomainGuid);
                  }

                  t.flags = domainTrust.Flags;
                  trusts.add(t);
               }
            }

            var15 = (DomainTrust[])trusts.toArray(new DomainTrust[0]);
         } finally {
            rc = Netapi32.INSTANCE.NetApiBufferFree(domainsPointerRef.getValue());
            if (0 != rc) {
               throw new Win32Exception(rc);
            }

         }

         return var15;
      }
   }

   public static UserInfo getUserInfo(String accountName) {
      return getUserInfo(accountName, getDCName());
   }

   public static UserInfo getUserInfo(String accountName, String domainName) {
      PointerByReference bufptr = new PointerByReference();

      UserInfo var6;
      try {
         int rc = Netapi32.INSTANCE.NetUserGetInfo(domainName, accountName, 23, bufptr);
         if (rc != 0) {
            throw new Win32Exception(rc);
         }

         LMAccess.USER_INFO_23 info_23 = new LMAccess.USER_INFO_23(bufptr.getValue());
         UserInfo userInfo = new UserInfo();
         userInfo.comment = info_23.usri23_comment;
         userInfo.flags = info_23.usri23_flags;
         userInfo.fullName = info_23.usri23_full_name;
         userInfo.name = info_23.usri23_name;
         if (info_23.usri23_user_sid != null) {
            userInfo.sidString = Advapi32Util.convertSidToStringSid(info_23.usri23_user_sid);
         }

         userInfo.sid = info_23.usri23_user_sid;
         var6 = userInfo;
      } finally {
         if (bufptr.getValue() != Pointer.NULL) {
            Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
         }

      }

      return var6;
   }

   public static class DomainTrust {
      public String NetbiosDomainName;
      public String DnsDomainName;
      public WinNT.PSID DomainSid;
      public String DomainSidString;
      public Guid.GUID DomainGuid;
      public String DomainGuidString;
      private int flags;

      public boolean isInForest() {
         return (this.flags & 1) != 0;
      }

      public boolean isOutbound() {
         return (this.flags & 2) != 0;
      }

      public boolean isRoot() {
         return (this.flags & 4) != 0;
      }

      public boolean isPrimary() {
         return (this.flags & 8) != 0;
      }

      public boolean isNativeMode() {
         return (this.flags & 16) != 0;
      }

      public boolean isInbound() {
         return (this.flags & 32) != 0;
      }
   }

   public static class DomainController {
      public String name;
      public String address;
      public int addressType;
      public Guid.GUID domainGuid;
      public String domainName;
      public String dnsForestName;
      public int flags;
      public String clientSiteName;
   }

   public static class LocalGroup extends Group {
      public String comment;
   }

   public static class UserInfo extends User {
      public String fullName;
      public String sidString;
      public WinNT.PSID sid;
      public int flags;
   }

   public static class User {
      public String name;
      public String comment;
   }

   public static class Group {
      public String name;
   }
}
