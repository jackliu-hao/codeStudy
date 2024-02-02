/*     */ package com.sun.jna.platform.win32;
/*     */ 
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.Structure;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import com.sun.jna.ptr.PointerByReference;
/*     */ import java.util.ArrayList;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Netapi32Util
/*     */ {
/*     */   public static class Group
/*     */   {
/*     */     public String name;
/*     */   }
/*     */   
/*     */   public static class User
/*     */   {
/*     */     public String name;
/*     */     public String comment;
/*     */   }
/*     */   
/*     */   public static class UserInfo
/*     */     extends User
/*     */   {
/*     */     public String fullName;
/*     */     public String sidString;
/*     */     public WinNT.PSID sid;
/*     */     public int flags;
/*     */   }
/*     */   
/*     */   public static class LocalGroup
/*     */     extends Group
/*     */   {
/*     */     public String comment;
/*     */   }
/*     */   
/*     */   public static String getDCName() {
/* 105 */     return getDCName(null, null);
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
/*     */   public static String getDCName(String serverName, String domainName) {
/* 119 */     PointerByReference bufptr = new PointerByReference();
/*     */     try {
/* 121 */       int rc = Netapi32.INSTANCE.NetGetDCName(domainName, serverName, bufptr);
/* 122 */       if (0 != rc) {
/* 123 */         throw new Win32Exception(rc);
/*     */       }
/* 125 */       return bufptr.getValue().getWideString(0L);
/*     */     } finally {
/* 127 */       if (0 != Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue())) {
/* 128 */         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*     */       }
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getJoinStatus() {
/* 138 */     return getJoinStatus(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getJoinStatus(String computerName) {
/* 147 */     PointerByReference lpNameBuffer = new PointerByReference();
/* 148 */     IntByReference bufferType = new IntByReference();
/*     */     
/*     */     try {
/* 151 */       int rc = Netapi32.INSTANCE.NetGetJoinInformation(computerName, lpNameBuffer, bufferType);
/* 152 */       if (0 != rc) {
/* 153 */         throw new Win32Exception(rc);
/*     */       }
/* 155 */       return bufferType.getValue();
/*     */     } finally {
/* 157 */       if (lpNameBuffer.getPointer() != null) {
/* 158 */         int rc = Netapi32.INSTANCE.NetApiBufferFree(lpNameBuffer.getValue());
/* 159 */         if (0 != rc) {
/* 160 */           throw new Win32Exception(rc);
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getDomainName(String computerName) {
/* 172 */     PointerByReference lpNameBuffer = new PointerByReference();
/* 173 */     IntByReference bufferType = new IntByReference();
/*     */     
/*     */     try {
/* 176 */       int rc = Netapi32.INSTANCE.NetGetJoinInformation(computerName, lpNameBuffer, bufferType);
/* 177 */       if (0 != rc) {
/* 178 */         throw new Win32Exception(rc);
/*     */       }
/*     */       
/* 181 */       return lpNameBuffer.getValue().getWideString(0L);
/*     */     } finally {
/* 183 */       if (lpNameBuffer.getPointer() != null) {
/* 184 */         int rc = Netapi32.INSTANCE.NetApiBufferFree(lpNameBuffer.getValue());
/* 185 */         if (0 != rc) {
/* 186 */           throw new Win32Exception(rc);
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LocalGroup[] getLocalGroups() {
/* 197 */     return getLocalGroups(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static LocalGroup[] getLocalGroups(String serverName) {
/* 206 */     PointerByReference bufptr = new PointerByReference();
/* 207 */     IntByReference entriesRead = new IntByReference();
/* 208 */     IntByReference totalEntries = new IntByReference();
/*     */     try {
/* 210 */       int rc = Netapi32.INSTANCE.NetLocalGroupEnum(serverName, 1, bufptr, -1, entriesRead, totalEntries, null);
/* 211 */       if (0 != rc || bufptr.getValue() == Pointer.NULL) {
/* 212 */         throw new Win32Exception(rc);
/*     */       }
/*     */       
/* 215 */       ArrayList<LocalGroup> result = new ArrayList<LocalGroup>();
/*     */       
/* 217 */       if (entriesRead.getValue() > 0) {
/* 218 */         LMAccess.LOCALGROUP_INFO_1 group = new LMAccess.LOCALGROUP_INFO_1(bufptr.getValue());
/* 219 */         LMAccess.LOCALGROUP_INFO_1[] groups = (LMAccess.LOCALGROUP_INFO_1[])group.toArray(entriesRead.getValue());
/* 220 */         for (LMAccess.LOCALGROUP_INFO_1 lgpi : groups) {
/* 221 */           LocalGroup lgp = new LocalGroup();
/* 222 */           lgp.name = lgpi.lgrui1_name;
/* 223 */           lgp.comment = lgpi.lgrui1_comment;
/* 224 */           result.add(lgp);
/*     */         } 
/*     */       } 
/*     */       
/* 228 */       return result.<LocalGroup>toArray(new LocalGroup[0]);
/*     */     } finally {
/* 230 */       if (bufptr.getValue() != Pointer.NULL) {
/* 231 */         int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
/* 232 */         if (0 != rc) {
/* 233 */           throw new Win32Exception(rc);
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Group[] getGlobalGroups() {
/* 244 */     return getGlobalGroups(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Group[] getGlobalGroups(String serverName) {
/* 253 */     PointerByReference bufptr = new PointerByReference();
/* 254 */     IntByReference entriesRead = new IntByReference();
/* 255 */     IntByReference totalEntries = new IntByReference();
/*     */     try {
/* 257 */       int rc = Netapi32.INSTANCE.NetGroupEnum(serverName, 1, bufptr, -1, entriesRead, totalEntries, null);
/*     */ 
/*     */       
/* 260 */       if (0 != rc || bufptr.getValue() == Pointer.NULL) {
/* 261 */         throw new Win32Exception(rc);
/*     */       }
/*     */       
/* 264 */       ArrayList<LocalGroup> result = new ArrayList<LocalGroup>();
/*     */       
/* 266 */       if (entriesRead.getValue() > 0) {
/* 267 */         LMAccess.GROUP_INFO_1 group = new LMAccess.GROUP_INFO_1(bufptr.getValue());
/* 268 */         LMAccess.GROUP_INFO_1[] groups = (LMAccess.GROUP_INFO_1[])group.toArray(entriesRead.getValue());
/* 269 */         for (LMAccess.GROUP_INFO_1 lgpi : groups) {
/* 270 */           LocalGroup lgp = new LocalGroup();
/* 271 */           lgp.name = lgpi.grpi1_name;
/* 272 */           lgp.comment = lgpi.grpi1_comment;
/* 273 */           result.add(lgp);
/*     */         } 
/*     */       } 
/*     */       
/* 277 */       return result.<Group>toArray((Group[])new LocalGroup[0]);
/*     */     } finally {
/* 279 */       if (bufptr.getValue() != Pointer.NULL) {
/* 280 */         int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
/* 281 */         if (0 != rc) {
/* 282 */           throw new Win32Exception(rc);
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static User[] getUsers() {
/* 293 */     return getUsers(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static User[] getUsers(String serverName) {
/* 302 */     PointerByReference bufptr = new PointerByReference();
/* 303 */     IntByReference entriesRead = new IntByReference();
/* 304 */     IntByReference totalEntries = new IntByReference();
/*     */     try {
/* 306 */       int rc = Netapi32.INSTANCE.NetUserEnum(serverName, 1, 0, bufptr, -1, entriesRead, totalEntries, null);
/*     */ 
/*     */ 
/*     */       
/* 310 */       if (0 != rc || bufptr.getValue() == Pointer.NULL) {
/* 311 */         throw new Win32Exception(rc);
/*     */       }
/*     */       
/* 314 */       ArrayList<User> result = new ArrayList<User>();
/*     */       
/* 316 */       if (entriesRead.getValue() > 0) {
/* 317 */         LMAccess.USER_INFO_1 user = new LMAccess.USER_INFO_1(bufptr.getValue());
/* 318 */         LMAccess.USER_INFO_1[] users = (LMAccess.USER_INFO_1[])user.toArray(entriesRead.getValue());
/* 319 */         for (LMAccess.USER_INFO_1 lu : users) {
/* 320 */           User auser = new User();
/* 321 */           if (lu.usri1_name != null) {
/* 322 */             auser.name = lu.usri1_name;
/*     */           }
/* 324 */           result.add(auser);
/*     */         } 
/*     */       } 
/*     */       
/* 328 */       return result.<User>toArray(new User[0]);
/*     */     } finally {
/* 330 */       if (bufptr.getValue() != Pointer.NULL) {
/* 331 */         int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
/* 332 */         if (0 != rc) {
/* 333 */           throw new Win32Exception(rc);
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Group[] getCurrentUserLocalGroups() {
/* 344 */     return getUserLocalGroups(Secur32Util.getUserNameEx(2));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Group[] getUserLocalGroups(String userName) {
/* 353 */     return getUserLocalGroups(userName, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Group[] getUserLocalGroups(String userName, String serverName) {
/* 363 */     PointerByReference bufptr = new PointerByReference();
/* 364 */     IntByReference entriesread = new IntByReference();
/* 365 */     IntByReference totalentries = new IntByReference();
/*     */     try {
/* 367 */       int rc = Netapi32.INSTANCE.NetUserGetLocalGroups(serverName, userName, 0, 0, bufptr, -1, entriesread, totalentries);
/*     */ 
/*     */       
/* 370 */       if (rc != 0) {
/* 371 */         throw new Win32Exception(rc);
/*     */       }
/* 373 */       ArrayList<Group> result = new ArrayList<Group>();
/* 374 */       if (entriesread.getValue() > 0) {
/* 375 */         LMAccess.LOCALGROUP_USERS_INFO_0 lgroup = new LMAccess.LOCALGROUP_USERS_INFO_0(bufptr.getValue());
/* 376 */         LMAccess.LOCALGROUP_USERS_INFO_0[] lgroups = (LMAccess.LOCALGROUP_USERS_INFO_0[])lgroup.toArray(entriesread.getValue());
/* 377 */         for (LMAccess.LOCALGROUP_USERS_INFO_0 lgpi : lgroups) {
/* 378 */           LocalGroup lgp = new LocalGroup();
/* 379 */           if (lgpi.lgrui0_name != null) {
/* 380 */             lgp.name = lgpi.lgrui0_name;
/*     */           }
/* 382 */           result.add(lgp);
/*     */         } 
/*     */       } 
/* 385 */       return result.<Group>toArray(new Group[0]);
/*     */     } finally {
/* 387 */       if (bufptr.getValue() != Pointer.NULL) {
/* 388 */         int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
/* 389 */         if (0 != rc) {
/* 390 */           throw new Win32Exception(rc);
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Group[] getUserGroups(String userName) {
/* 402 */     return getUserGroups(userName, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Group[] getUserGroups(String userName, String serverName) {
/* 412 */     PointerByReference bufptr = new PointerByReference();
/* 413 */     IntByReference entriesread = new IntByReference();
/* 414 */     IntByReference totalentries = new IntByReference();
/*     */     try {
/* 416 */       int rc = Netapi32.INSTANCE.NetUserGetGroups(serverName, userName, 0, bufptr, -1, entriesread, totalentries);
/*     */ 
/*     */       
/* 419 */       if (rc != 0) {
/* 420 */         throw new Win32Exception(rc);
/*     */       }
/*     */       
/* 423 */       ArrayList<Group> result = new ArrayList<Group>();
/*     */       
/* 425 */       if (entriesread.getValue() > 0) {
/* 426 */         LMAccess.GROUP_USERS_INFO_0 lgroup = new LMAccess.GROUP_USERS_INFO_0(bufptr.getValue());
/* 427 */         LMAccess.GROUP_USERS_INFO_0[] lgroups = (LMAccess.GROUP_USERS_INFO_0[])lgroup.toArray(entriesread.getValue());
/* 428 */         for (LMAccess.GROUP_USERS_INFO_0 lgpi : lgroups) {
/* 429 */           Group lgp = new Group();
/* 430 */           if (lgpi.grui0_name != null) {
/* 431 */             lgp.name = lgpi.grui0_name;
/*     */           }
/* 433 */           result.add(lgp);
/*     */         } 
/*     */       } 
/*     */       
/* 437 */       return result.<Group>toArray(new Group[0]);
/*     */     } finally {
/* 439 */       if (bufptr.getValue() != Pointer.NULL) {
/* 440 */         int rc = Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue());
/* 441 */         if (0 != rc) {
/* 442 */           throw new Win32Exception(rc);
/*     */         }
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class DomainController
/*     */   {
/*     */     public String name;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String address;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public int addressType;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Guid.GUID domainGuid;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String domainName;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String dnsForestName;
/*     */ 
/*     */ 
/*     */     
/*     */     public int flags;
/*     */ 
/*     */ 
/*     */     
/*     */     public String clientSiteName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DomainController getDC() {
/* 494 */     DsGetDC.PDOMAIN_CONTROLLER_INFO pdci = new DsGetDC.PDOMAIN_CONTROLLER_INFO();
/* 495 */     int rc = Netapi32.INSTANCE.DsGetDcName(null, null, null, null, 0, pdci);
/* 496 */     if (0 != rc) {
/* 497 */       throw new Win32Exception(rc);
/*     */     }
/* 499 */     DomainController dc = new DomainController();
/* 500 */     dc.address = pdci.dci.DomainControllerAddress;
/* 501 */     dc.addressType = pdci.dci.DomainControllerAddressType;
/* 502 */     dc.clientSiteName = pdci.dci.ClientSiteName;
/* 503 */     dc.dnsForestName = pdci.dci.DnsForestName;
/* 504 */     dc.domainGuid = pdci.dci.DomainGuid;
/* 505 */     dc.domainName = pdci.dci.DomainName;
/* 506 */     dc.flags = pdci.dci.Flags;
/* 507 */     dc.name = pdci.dci.DomainControllerName;
/* 508 */     rc = Netapi32.INSTANCE.NetApiBufferFree(pdci.dci.getPointer());
/* 509 */     if (0 != rc) {
/* 510 */       throw new Win32Exception(rc);
/*     */     }
/* 512 */     return dc;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static class DomainTrust
/*     */   {
/*     */     public String NetbiosDomainName;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String DnsDomainName;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public WinNT.PSID DomainSid;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String DomainSidString;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public Guid.GUID DomainGuid;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public String DomainGuidString;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     private int flags;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean isInForest() {
/* 559 */       return ((this.flags & 0x1) != 0);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean isOutbound() {
/* 570 */       return ((this.flags & 0x2) != 0);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean isRoot() {
/* 581 */       return ((this.flags & 0x4) != 0);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean isPrimary() {
/* 591 */       return ((this.flags & 0x8) != 0);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean isNativeMode() {
/* 600 */       return ((this.flags & 0x10) != 0);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public boolean isInbound() {
/* 611 */       return ((this.flags & 0x20) != 0);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DomainTrust[] getDomainTrusts() {
/* 621 */     return getDomainTrusts(null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static DomainTrust[] getDomainTrusts(String serverName) {
/* 632 */     IntByReference domainTrustCount = new IntByReference();
/* 633 */     PointerByReference domainsPointerRef = new PointerByReference();
/* 634 */     int rc = Netapi32.INSTANCE.DsEnumerateDomainTrusts(serverName, 63, domainsPointerRef, domainTrustCount);
/*     */     
/* 636 */     if (0 != rc) {
/* 637 */       throw new Win32Exception(rc);
/*     */     }
/*     */     try {
/* 640 */       ArrayList<DomainTrust> trusts = new ArrayList<DomainTrust>(domainTrustCount.getValue());
/*     */       
/* 642 */       if (domainTrustCount.getValue() > 0) {
/* 643 */         DsGetDC.DS_DOMAIN_TRUSTS domainTrustRefs = new DsGetDC.DS_DOMAIN_TRUSTS(domainsPointerRef.getValue());
/* 644 */         DsGetDC.DS_DOMAIN_TRUSTS[] domainTrusts = (DsGetDC.DS_DOMAIN_TRUSTS[])domainTrustRefs.toArray((Structure[])new DsGetDC.DS_DOMAIN_TRUSTS[domainTrustCount.getValue()]);
/* 645 */         for (DsGetDC.DS_DOMAIN_TRUSTS domainTrust : domainTrusts) {
/* 646 */           DomainTrust t = new DomainTrust();
/* 647 */           if (domainTrust.DnsDomainName != null) {
/* 648 */             t.DnsDomainName = domainTrust.DnsDomainName;
/*     */           }
/* 650 */           if (domainTrust.NetbiosDomainName != null) {
/* 651 */             t.NetbiosDomainName = domainTrust.NetbiosDomainName;
/*     */           }
/* 653 */           t.DomainSid = domainTrust.DomainSid;
/* 654 */           if (domainTrust.DomainSid != null) {
/* 655 */             t.DomainSidString = Advapi32Util.convertSidToStringSid(domainTrust.DomainSid);
/*     */           }
/* 657 */           t.DomainGuid = domainTrust.DomainGuid;
/* 658 */           if (domainTrust.DomainGuid != null) {
/* 659 */             t.DomainGuidString = Ole32Util.getStringFromGUID(domainTrust.DomainGuid);
/*     */           }
/* 661 */           t.flags = domainTrust.Flags;
/* 662 */           trusts.add(t);
/*     */         } 
/*     */       } 
/*     */       
/* 666 */       return trusts.<DomainTrust>toArray(new DomainTrust[0]);
/*     */     } finally {
/* 668 */       rc = Netapi32.INSTANCE.NetApiBufferFree(domainsPointerRef.getValue());
/* 669 */       if (0 != rc) {
/* 670 */         throw new Win32Exception(rc);
/*     */       }
/*     */     } 
/*     */   }
/*     */   
/*     */   public static UserInfo getUserInfo(String accountName) {
/* 676 */     return getUserInfo(accountName, getDCName());
/*     */   }
/*     */   
/*     */   public static UserInfo getUserInfo(String accountName, String domainName) {
/* 680 */     PointerByReference bufptr = new PointerByReference();
/*     */     try {
/* 682 */       int rc = Netapi32.INSTANCE.NetUserGetInfo(domainName, accountName, 23, bufptr);
/* 683 */       if (rc == 0) {
/* 684 */         LMAccess.USER_INFO_23 info_23 = new LMAccess.USER_INFO_23(bufptr.getValue());
/* 685 */         UserInfo userInfo = new UserInfo();
/* 686 */         userInfo.comment = info_23.usri23_comment;
/* 687 */         userInfo.flags = info_23.usri23_flags;
/* 688 */         userInfo.fullName = info_23.usri23_full_name;
/* 689 */         userInfo.name = info_23.usri23_name;
/* 690 */         if (info_23.usri23_user_sid != null) {
/* 691 */           userInfo.sidString = Advapi32Util.convertSidToStringSid(info_23.usri23_user_sid);
/*     */         }
/* 693 */         userInfo.sid = info_23.usri23_user_sid;
/* 694 */         return userInfo;
/*     */       } 
/* 696 */       throw new Win32Exception(rc);
/*     */     } finally {
/*     */       
/* 699 */       if (bufptr.getValue() != Pointer.NULL)
/* 700 */         Netapi32.INSTANCE.NetApiBufferFree(bufptr.getValue()); 
/*     */     } 
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\Netapi32Util.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */