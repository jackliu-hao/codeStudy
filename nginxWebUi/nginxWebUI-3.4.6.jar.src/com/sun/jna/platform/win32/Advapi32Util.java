/*      */ package com.sun.jna.platform.win32;
/*      */ 
/*      */ import com.sun.jna.Memory;
/*      */ import com.sun.jna.Native;
/*      */ import com.sun.jna.Pointer;
/*      */ import com.sun.jna.ptr.IntByReference;
/*      */ import com.sun.jna.ptr.LongByReference;
/*      */ import com.sun.jna.ptr.PointerByReference;
/*      */ import com.sun.jna.win32.W32APITypeMapper;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.Closeable;
/*      */ import java.io.File;
/*      */ import java.io.IOException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Map;
/*      */ import java.util.TreeMap;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public abstract class Advapi32Util
/*      */ {
/*      */   public static class Account
/*      */   {
/*      */     public String name;
/*      */     public String domain;
/*      */     public byte[] sid;
/*      */     public String sidString;
/*      */     public int accountType;
/*      */     public String fqn;
/*      */   }
/*      */   
/*      */   public static String getUserName() {
/*  147 */     char[] buffer = new char[128];
/*  148 */     IntByReference len = new IntByReference(buffer.length);
/*  149 */     boolean result = Advapi32.INSTANCE.GetUserNameW(buffer, len);
/*      */     
/*  151 */     if (!result) {
/*  152 */       switch (Kernel32.INSTANCE.GetLastError()) {
/*      */         case 122:
/*  154 */           buffer = new char[len.getValue()];
/*      */           break;
/*      */         
/*      */         default:
/*  158 */           throw new Win32Exception(Native.getLastError());
/*      */       } 
/*      */       
/*  161 */       result = Advapi32.INSTANCE.GetUserNameW(buffer, len);
/*      */     } 
/*      */     
/*  164 */     if (!result) {
/*  165 */       throw new Win32Exception(Native.getLastError());
/*      */     }
/*      */     
/*  168 */     return Native.toString(buffer);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Account getAccountByName(String accountName) {
/*  180 */     return getAccountByName(null, accountName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Account getAccountByName(String systemName, String accountName) {
/*  193 */     IntByReference pSid = new IntByReference(0);
/*  194 */     IntByReference cchDomainName = new IntByReference(0);
/*  195 */     PointerByReference peUse = new PointerByReference();
/*      */     
/*  197 */     if (Advapi32.INSTANCE.LookupAccountName(systemName, accountName, null, pSid, null, cchDomainName, peUse))
/*      */     {
/*  199 */       throw new RuntimeException("LookupAccountNameW was expected to fail with ERROR_INSUFFICIENT_BUFFER");
/*      */     }
/*      */ 
/*      */     
/*  203 */     int rc = Kernel32.INSTANCE.GetLastError();
/*  204 */     if (pSid.getValue() == 0 || rc != 122) {
/*  205 */       throw new Win32Exception(rc);
/*      */     }
/*      */     
/*  208 */     Memory sidMemory = new Memory(pSid.getValue());
/*  209 */     WinNT.PSID result = new WinNT.PSID((Pointer)sidMemory);
/*  210 */     char[] referencedDomainName = new char[cchDomainName.getValue() + 1];
/*      */     
/*  212 */     if (!Advapi32.INSTANCE.LookupAccountName(systemName, accountName, result, pSid, referencedDomainName, cchDomainName, peUse))
/*      */     {
/*  214 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*      */     }
/*      */     
/*  217 */     Account account = new Account();
/*  218 */     account.accountType = peUse.getPointer().getInt(0L);
/*      */     
/*  220 */     String[] accountNamePartsBs = accountName.split("\\\\", 2);
/*  221 */     String[] accountNamePartsAt = accountName.split("@", 2);
/*      */     
/*  223 */     if (accountNamePartsBs.length == 2) {
/*  224 */       account.name = accountNamePartsBs[1];
/*  225 */     } else if (accountNamePartsAt.length == 2) {
/*  226 */       account.name = accountNamePartsAt[0];
/*      */     } else {
/*  228 */       account.name = accountName;
/*      */     } 
/*      */     
/*  231 */     if (cchDomainName.getValue() > 0) {
/*  232 */       account.domain = Native.toString(referencedDomainName);
/*  233 */       account.fqn = account.domain + "\\" + account.name;
/*      */     } else {
/*  235 */       account.fqn = account.name;
/*      */     } 
/*      */     
/*  238 */     account.sid = result.getBytes();
/*  239 */     account.sidString = convertSidToStringSid(new WinNT.PSID(account.sid));
/*  240 */     return account;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Account getAccountBySid(WinNT.PSID sid) {
/*  251 */     return getAccountBySid((String)null, sid);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Account getAccountBySid(String systemName, WinNT.PSID sid) {
/*  264 */     IntByReference cchName = new IntByReference();
/*  265 */     IntByReference cchDomainName = new IntByReference();
/*  266 */     PointerByReference peUse = new PointerByReference();
/*      */     
/*  268 */     if (Advapi32.INSTANCE.LookupAccountSid(systemName, sid, null, cchName, null, cchDomainName, peUse))
/*      */     {
/*  270 */       throw new RuntimeException("LookupAccountSidW was expected to fail with ERROR_INSUFFICIENT_BUFFER");
/*      */     }
/*      */ 
/*      */     
/*  274 */     int rc = Kernel32.INSTANCE.GetLastError();
/*  275 */     if (cchName.getValue() == 0 || rc != 122)
/*      */     {
/*  277 */       throw new Win32Exception(rc);
/*      */     }
/*      */     
/*  280 */     char[] domainName = new char[cchDomainName.getValue()];
/*  281 */     char[] name = new char[cchName.getValue()];
/*      */     
/*  283 */     if (!Advapi32.INSTANCE.LookupAccountSid(systemName, sid, name, cchName, domainName, cchDomainName, peUse))
/*      */     {
/*  285 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*      */     }
/*      */     
/*  288 */     Account account = new Account();
/*  289 */     account.accountType = peUse.getPointer().getInt(0L);
/*  290 */     account.name = Native.toString(name);
/*      */     
/*  292 */     if (cchDomainName.getValue() > 0) {
/*  293 */       account.domain = Native.toString(domainName);
/*  294 */       account.fqn = account.domain + "\\" + account.name;
/*      */     } else {
/*  296 */       account.fqn = account.name;
/*      */     } 
/*      */     
/*  299 */     account.sid = sid.getBytes();
/*  300 */     account.sidString = convertSidToStringSid(sid);
/*  301 */     return account;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String convertSidToStringSid(WinNT.PSID sid) {
/*  313 */     PointerByReference stringSid = new PointerByReference();
/*  314 */     if (!Advapi32.INSTANCE.ConvertSidToStringSid(sid, stringSid)) {
/*  315 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*      */     }
/*      */     
/*  318 */     Pointer ptr = stringSid.getValue();
/*      */     try {
/*  320 */       return ptr.getWideString(0L);
/*      */     } finally {
/*  322 */       Kernel32Util.freeLocalMemory(ptr);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] convertStringSidToSid(String sidString) {
/*  335 */     WinNT.PSIDByReference pSID = new WinNT.PSIDByReference();
/*  336 */     if (!Advapi32.INSTANCE.ConvertStringSidToSid(sidString, pSID)) {
/*  337 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*      */     }
/*      */     
/*  340 */     WinNT.PSID value = pSID.getValue();
/*      */     try {
/*  342 */       return value.getBytes();
/*      */     } finally {
/*  344 */       Kernel32Util.freeLocalMemory(value.getPointer());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isWellKnownSid(String sidString, int wellKnownSidType) {
/*  359 */     WinNT.PSIDByReference pSID = new WinNT.PSIDByReference();
/*  360 */     if (!Advapi32.INSTANCE.ConvertStringSidToSid(sidString, pSID)) {
/*  361 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*      */     }
/*      */     
/*  364 */     WinNT.PSID value = pSID.getValue();
/*      */     try {
/*  366 */       return Advapi32.INSTANCE.IsWellKnownSid(value, wellKnownSidType);
/*      */     } finally {
/*  368 */       Kernel32Util.freeLocalMemory(value.getPointer());
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isWellKnownSid(byte[] sidBytes, int wellKnownSidType) {
/*  383 */     WinNT.PSID pSID = new WinNT.PSID(sidBytes);
/*  384 */     return Advapi32.INSTANCE.IsWellKnownSid(pSID, wellKnownSidType);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int alignOnDWORD(int cbAcl) {
/*  393 */     return cbAcl + 3 & 0xFFFFFFFC;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int getAceSize(int sidLength) {
/*  402 */     return Native.getNativeSize(WinNT.ACCESS_ALLOWED_ACE.class, null) + sidLength - 4;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Account getAccountBySid(String sidString) {
/*  415 */     return getAccountBySid((String)null, sidString);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Account getAccountBySid(String systemName, String sidString) {
/*  428 */     return getAccountBySid(systemName, new WinNT.PSID(convertStringSidToSid(sidString)));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Account[] getTokenGroups(WinNT.HANDLE hToken) {
/*  441 */     IntByReference tokenInformationLength = new IntByReference();
/*  442 */     if (Advapi32.INSTANCE.GetTokenInformation(hToken, 2, null, 0, tokenInformationLength))
/*      */     {
/*      */       
/*  445 */       throw new RuntimeException("Expected GetTokenInformation to fail with ERROR_INSUFFICIENT_BUFFER");
/*      */     }
/*      */     
/*  448 */     int rc = Kernel32.INSTANCE.GetLastError();
/*  449 */     if (rc != 122) {
/*  450 */       throw new Win32Exception(rc);
/*      */     }
/*      */ 
/*      */     
/*  454 */     WinNT.TOKEN_GROUPS groups = new WinNT.TOKEN_GROUPS(tokenInformationLength.getValue());
/*  455 */     if (!Advapi32.INSTANCE.GetTokenInformation(hToken, 2, groups, tokenInformationLength
/*      */         
/*  457 */         .getValue(), tokenInformationLength)) {
/*  458 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*      */     }
/*  460 */     ArrayList<Account> userGroups = new ArrayList<Account>();
/*      */     
/*  462 */     for (WinNT.SID_AND_ATTRIBUTES sidAndAttribute : groups.getGroups()) {
/*      */       Account group;
/*      */       try {
/*  465 */         group = getAccountBySid(sidAndAttribute.Sid);
/*  466 */       } catch (Exception e) {
/*  467 */         group = new Account();
/*  468 */         group.sid = sidAndAttribute.Sid.getBytes();
/*  469 */         group
/*  470 */           .sidString = convertSidToStringSid(sidAndAttribute.Sid);
/*  471 */         group.name = group.sidString;
/*  472 */         group.fqn = group.sidString;
/*  473 */         group.accountType = 2;
/*      */       } 
/*  475 */       userGroups.add(group);
/*      */     } 
/*  477 */     return userGroups.<Account>toArray(new Account[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Account getTokenPrimaryGroup(WinNT.HANDLE hToken) {
/*      */     Account group;
/*  490 */     IntByReference tokenInformationLength = new IntByReference();
/*  491 */     if (Advapi32.INSTANCE.GetTokenInformation(hToken, 5, null, 0, tokenInformationLength))
/*      */     {
/*  493 */       throw new RuntimeException("Expected GetTokenInformation to fail with ERROR_INSUFFICIENT_BUFFER");
/*      */     }
/*  495 */     int rc = Kernel32.INSTANCE.GetLastError();
/*  496 */     if (rc != 122) {
/*  497 */       throw new Win32Exception(rc);
/*      */     }
/*      */     
/*  500 */     WinNT.TOKEN_PRIMARY_GROUP primaryGroup = new WinNT.TOKEN_PRIMARY_GROUP(tokenInformationLength.getValue());
/*  501 */     if (!Advapi32.INSTANCE.GetTokenInformation(hToken, 5, primaryGroup, tokenInformationLength
/*  502 */         .getValue(), tokenInformationLength)) {
/*  503 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*      */     }
/*      */     
/*      */     try {
/*  507 */       group = getAccountBySid(primaryGroup.PrimaryGroup);
/*  508 */     } catch (Exception e) {
/*  509 */       group = new Account();
/*  510 */       group.sid = primaryGroup.PrimaryGroup.getBytes();
/*  511 */       group.sidString = convertSidToStringSid(primaryGroup.PrimaryGroup);
/*  512 */       group.name = group.sidString;
/*  513 */       group.fqn = group.sidString;
/*  514 */       group.accountType = 2;
/*      */     } 
/*  516 */     return group;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Account getTokenAccount(WinNT.HANDLE hToken) {
/*  529 */     IntByReference tokenInformationLength = new IntByReference();
/*  530 */     if (Advapi32.INSTANCE.GetTokenInformation(hToken, 1, null, 0, tokenInformationLength))
/*      */     {
/*      */       
/*  533 */       throw new RuntimeException("Expected GetTokenInformation to fail with ERROR_INSUFFICIENT_BUFFER");
/*      */     }
/*      */     
/*  536 */     int rc = Kernel32.INSTANCE.GetLastError();
/*  537 */     if (rc != 122) {
/*  538 */       throw new Win32Exception(rc);
/*      */     }
/*      */ 
/*      */     
/*  542 */     WinNT.TOKEN_USER user = new WinNT.TOKEN_USER(tokenInformationLength.getValue());
/*  543 */     if (!Advapi32.INSTANCE.GetTokenInformation(hToken, 1, user, tokenInformationLength
/*      */         
/*  545 */         .getValue(), tokenInformationLength)) {
/*  546 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*      */     }
/*  548 */     return getAccountBySid(user.User.Sid);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Account[] getCurrentUserGroups() {
/*  557 */     WinNT.HANDLEByReference phToken = new WinNT.HANDLEByReference();
/*  558 */     Win32Exception err = null;
/*      */     
/*      */     try {
/*  561 */       WinNT.HANDLE threadHandle = Kernel32.INSTANCE.GetCurrentThread();
/*  562 */       if (!Advapi32.INSTANCE.OpenThreadToken(threadHandle, 10, true, phToken)) {
/*      */         
/*  564 */         int rc = Kernel32.INSTANCE.GetLastError();
/*  565 */         if (rc != 1008) {
/*  566 */           throw new Win32Exception(rc);
/*      */         }
/*      */         
/*  569 */         WinNT.HANDLE processHandle = Kernel32.INSTANCE.GetCurrentProcess();
/*  570 */         if (!Advapi32.INSTANCE.OpenProcessToken(processHandle, 10, phToken))
/*      */         {
/*  572 */           throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*      */         }
/*      */       } 
/*      */       
/*  576 */       return getTokenGroups(phToken.getValue());
/*  577 */     } catch (Win32Exception e) {
/*  578 */       err = e;
/*  579 */       throw err;
/*      */     } finally {
/*  581 */       WinNT.HANDLE hToken = phToken.getValue();
/*  582 */       if (!WinBase.INVALID_HANDLE_VALUE.equals(hToken)) {
/*      */         try {
/*  584 */           Kernel32Util.closeHandle(hToken);
/*  585 */         } catch (Win32Exception e) {
/*  586 */           if (err == null) {
/*  587 */             err = e;
/*      */           } else {
/*  589 */             err.addSuppressedReflected((Throwable)e);
/*      */           } 
/*      */         } 
/*      */       }
/*      */       
/*  594 */       if (err != null) {
/*  595 */         throw err;
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean registryKeyExists(WinReg.HKEY root, String key) {
/*  610 */     return registryKeyExists(root, key, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean registryKeyExists(WinReg.HKEY root, String key, int samDesiredExtra) {
/*  626 */     WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
/*  627 */     int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, 0x20019 | samDesiredExtra, phkKey);
/*      */     
/*  629 */     switch (rc) {
/*      */       case 0:
/*  631 */         Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
/*  632 */         return true;
/*      */       case 2:
/*  634 */         return false;
/*      */     } 
/*  636 */     throw new Win32Exception(rc);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean registryValueExists(WinReg.HKEY root, String key, String value) {
/*  652 */     return registryValueExists(root, key, value, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean registryValueExists(WinReg.HKEY root, String key, String value, int samDesiredExtra) {
/*  671 */     WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
/*  672 */     int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, 0x20019 | samDesiredExtra, phkKey);
/*      */     
/*  674 */     switch (rc) {
/*      */       case 0:
/*      */         break;
/*      */       case 2:
/*  678 */         return false;
/*      */       default:
/*  680 */         throw new Win32Exception(rc);
/*      */     }  try {
/*      */       boolean bool;
/*  683 */       IntByReference lpcbData = new IntByReference();
/*  684 */       IntByReference lpType = new IntByReference();
/*  685 */       rc = Advapi32.INSTANCE.RegQueryValueEx(phkKey.getValue(), value, 0, lpType, (Pointer)null, lpcbData);
/*      */       
/*  687 */       switch (rc) {
/*      */         case 0:
/*      */         case 122:
/*      */         case 234:
/*  691 */           bool = true; return bool;
/*      */         case 2:
/*  693 */           bool = false; return bool;
/*      */       } 
/*  695 */       throw new Win32Exception(rc);
/*      */     } finally {
/*      */       
/*  698 */       if (phkKey.getValue() != WinBase.INVALID_HANDLE_VALUE) {
/*  699 */         rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
/*  700 */         if (rc != 0) {
/*  701 */           throw new Win32Exception(rc);
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String registryGetStringValue(WinReg.HKEY root, String key, String value) {
/*  720 */     return registryGetStringValue(root, key, value, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String registryGetStringValue(WinReg.HKEY root, String key, String value, int samDesiredExtra) {
/*  739 */     WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
/*  740 */     int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, 0x20019 | samDesiredExtra, phkKey);
/*      */     
/*  742 */     if (rc != 0) {
/*  743 */       throw new Win32Exception(rc);
/*      */     }
/*      */     try {
/*  746 */       return registryGetStringValue(phkKey.getValue(), value);
/*      */     } finally {
/*  748 */       rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
/*  749 */       if (rc != 0) {
/*  750 */         throw new Win32Exception(rc);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String registryGetStringValue(WinReg.HKEY hKey, String value) {
/*  765 */     IntByReference lpcbData = new IntByReference();
/*  766 */     IntByReference lpType = new IntByReference();
/*  767 */     int rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, (Pointer)null, lpcbData);
/*      */     
/*  769 */     if (rc != 0 && rc != 122)
/*      */     {
/*  771 */       throw new Win32Exception(rc);
/*      */     }
/*  773 */     if (lpType.getValue() != 1 && lpType
/*  774 */       .getValue() != 2) {
/*  775 */       throw new RuntimeException("Unexpected registry type " + lpType
/*  776 */           .getValue() + ", expected REG_SZ or REG_EXPAND_SZ");
/*      */     }
/*      */     
/*  779 */     if (lpcbData.getValue() == 0) {
/*  780 */       return "";
/*      */     }
/*      */     
/*  783 */     Memory mem = new Memory((lpcbData.getValue() + Native.WCHAR_SIZE));
/*  784 */     mem.clear();
/*  785 */     rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, (Pointer)mem, lpcbData);
/*      */     
/*  787 */     if (rc != 0 && rc != 122)
/*      */     {
/*  789 */       throw new Win32Exception(rc);
/*      */     }
/*  791 */     if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
/*  792 */       return mem.getWideString(0L);
/*      */     }
/*  794 */     return mem.getString(0L);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String registryGetExpandableStringValue(WinReg.HKEY root, String key, String value) {
/*  811 */     return registryGetExpandableStringValue(root, key, value, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String registryGetExpandableStringValue(WinReg.HKEY root, String key, String value, int samDesiredExtra) {
/*  830 */     WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
/*  831 */     int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, 0x20019 | samDesiredExtra, phkKey);
/*      */     
/*  833 */     if (rc != 0) {
/*  834 */       throw new Win32Exception(rc);
/*      */     }
/*      */     try {
/*  837 */       return registryGetExpandableStringValue(phkKey.getValue(), value);
/*      */     } finally {
/*  839 */       rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
/*  840 */       if (rc != 0) {
/*  841 */         throw new Win32Exception(rc);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String registryGetExpandableStringValue(WinReg.HKEY hKey, String value) {
/*  856 */     IntByReference lpcbData = new IntByReference();
/*  857 */     IntByReference lpType = new IntByReference();
/*  858 */     int rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, (char[])null, lpcbData);
/*      */     
/*  860 */     if (rc != 0 && rc != 122)
/*      */     {
/*  862 */       throw new Win32Exception(rc);
/*      */     }
/*  864 */     if (lpType.getValue() != 2) {
/*  865 */       throw new RuntimeException("Unexpected registry type " + lpType
/*  866 */           .getValue() + ", expected REG_SZ");
/*      */     }
/*  868 */     if (lpcbData.getValue() == 0) {
/*  869 */       return "";
/*      */     }
/*      */     
/*  872 */     Memory mem = new Memory((lpcbData.getValue() + Native.WCHAR_SIZE));
/*  873 */     mem.clear();
/*  874 */     rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, (Pointer)mem, lpcbData);
/*      */     
/*  876 */     if (rc != 0 && rc != 122)
/*      */     {
/*  878 */       throw new Win32Exception(rc);
/*      */     }
/*  880 */     if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
/*  881 */       return mem.getWideString(0L);
/*      */     }
/*  883 */     return mem.getString(0L);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] registryGetStringArray(WinReg.HKEY root, String key, String value) {
/*  900 */     return registryGetStringArray(root, key, value, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] registryGetStringArray(WinReg.HKEY root, String key, String value, int samDesiredExtra) {
/*  919 */     WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
/*  920 */     int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, 0x20019 | samDesiredExtra, phkKey);
/*      */     
/*  922 */     if (rc != 0) {
/*  923 */       throw new Win32Exception(rc);
/*      */     }
/*      */     try {
/*  926 */       return registryGetStringArray(phkKey.getValue(), value);
/*      */     } finally {
/*  928 */       rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
/*  929 */       if (rc != 0) {
/*  930 */         throw new Win32Exception(rc);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] registryGetStringArray(WinReg.HKEY hKey, String value) {
/*  945 */     IntByReference lpcbData = new IntByReference();
/*  946 */     IntByReference lpType = new IntByReference();
/*  947 */     int rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, (char[])null, lpcbData);
/*      */     
/*  949 */     if (rc != 0 && rc != 122)
/*      */     {
/*  951 */       throw new Win32Exception(rc);
/*      */     }
/*  953 */     if (lpType.getValue() != 7) {
/*  954 */       throw new RuntimeException("Unexpected registry type " + lpType
/*  955 */           .getValue() + ", expected REG_SZ");
/*      */     }
/*      */ 
/*      */     
/*  959 */     Memory data = new Memory((lpcbData.getValue() + 2 * Native.WCHAR_SIZE));
/*  960 */     data.clear();
/*  961 */     rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, (Pointer)data, lpcbData);
/*      */     
/*  963 */     if (rc != 0 && rc != 122)
/*      */     {
/*  965 */       throw new Win32Exception(rc);
/*      */     }
/*  967 */     ArrayList<String> result = new ArrayList<String>();
/*  968 */     int offset = 0;
/*  969 */     while (offset < data.size()) {
/*      */       String s;
/*  971 */       if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
/*  972 */         s = data.getWideString(offset);
/*  973 */         offset += s.length() * Native.WCHAR_SIZE;
/*  974 */         offset += Native.WCHAR_SIZE;
/*      */       } else {
/*  976 */         s = data.getString(offset);
/*  977 */         offset += s.length();
/*  978 */         offset++;
/*      */       } 
/*      */       
/*  981 */       if (s.length() == 0) {
/*      */         break;
/*      */       }
/*      */ 
/*      */ 
/*      */       
/*  987 */       result.add(s);
/*      */     } 
/*      */     
/*  990 */     return result.<String>toArray(new String[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] registryGetBinaryValue(WinReg.HKEY root, String key, String value) {
/* 1006 */     return registryGetBinaryValue(root, key, value, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] registryGetBinaryValue(WinReg.HKEY root, String key, String value, int samDesiredExtra) {
/* 1025 */     WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
/* 1026 */     int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, 0x20019 | samDesiredExtra, phkKey);
/*      */     
/* 1028 */     if (rc != 0) {
/* 1029 */       throw new Win32Exception(rc);
/*      */     }
/*      */     try {
/* 1032 */       return registryGetBinaryValue(phkKey.getValue(), value);
/*      */     } finally {
/* 1034 */       rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
/* 1035 */       if (rc != 0) {
/* 1036 */         throw new Win32Exception(rc);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] registryGetBinaryValue(WinReg.HKEY hKey, String value) {
/* 1051 */     IntByReference lpcbData = new IntByReference();
/* 1052 */     IntByReference lpType = new IntByReference();
/* 1053 */     int rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, (Pointer)null, lpcbData);
/*      */     
/* 1055 */     if (rc != 0 && rc != 122)
/*      */     {
/* 1057 */       throw new Win32Exception(rc);
/*      */     }
/* 1059 */     if (lpType.getValue() != 3) {
/* 1060 */       throw new RuntimeException("Unexpected registry type " + lpType
/* 1061 */           .getValue() + ", expected REG_BINARY");
/*      */     }
/* 1063 */     byte[] data = new byte[lpcbData.getValue()];
/* 1064 */     rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, data, lpcbData);
/*      */     
/* 1066 */     if (rc != 0 && rc != 122)
/*      */     {
/* 1068 */       throw new Win32Exception(rc);
/*      */     }
/* 1070 */     return data;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int registryGetIntValue(WinReg.HKEY root, String key, String value) {
/* 1085 */     return registryGetIntValue(root, key, value, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int registryGetIntValue(WinReg.HKEY root, String key, String value, int samDesiredExtra) {
/* 1103 */     WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
/* 1104 */     int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, 0x20019 | samDesiredExtra, phkKey);
/*      */     
/* 1106 */     if (rc != 0) {
/* 1107 */       throw new Win32Exception(rc);
/*      */     }
/*      */     try {
/* 1110 */       return registryGetIntValue(phkKey.getValue(), value);
/*      */     } finally {
/* 1112 */       rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
/* 1113 */       if (rc != 0) {
/* 1114 */         throw new Win32Exception(rc);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int registryGetIntValue(WinReg.HKEY hKey, String value) {
/* 1129 */     IntByReference lpcbData = new IntByReference();
/* 1130 */     IntByReference lpType = new IntByReference();
/* 1131 */     int rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, (char[])null, lpcbData);
/*      */     
/* 1133 */     if (rc != 0 && rc != 122)
/*      */     {
/* 1135 */       throw new Win32Exception(rc);
/*      */     }
/* 1137 */     if (lpType.getValue() != 4) {
/* 1138 */       throw new RuntimeException("Unexpected registry type " + lpType
/* 1139 */           .getValue() + ", expected REG_DWORD");
/*      */     }
/* 1141 */     IntByReference data = new IntByReference();
/* 1142 */     rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, data, lpcbData);
/*      */     
/* 1144 */     if (rc != 0 && rc != 122)
/*      */     {
/* 1146 */       throw new Win32Exception(rc);
/*      */     }
/* 1148 */     return data.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long registryGetLongValue(WinReg.HKEY root, String key, String value) {
/* 1163 */     return registryGetLongValue(root, key, value, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long registryGetLongValue(WinReg.HKEY root, String key, String value, int samDesiredExtra) {
/* 1181 */     WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
/* 1182 */     int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, key, 0, 0x20019 | samDesiredExtra, phkKey);
/*      */     
/* 1184 */     if (rc != 0) {
/* 1185 */       throw new Win32Exception(rc);
/*      */     }
/*      */     try {
/* 1188 */       return registryGetLongValue(phkKey.getValue(), value);
/*      */     } finally {
/* 1190 */       rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
/* 1191 */       if (rc != 0) {
/* 1192 */         throw new Win32Exception(rc);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long registryGetLongValue(WinReg.HKEY hKey, String value) {
/* 1207 */     IntByReference lpcbData = new IntByReference();
/* 1208 */     IntByReference lpType = new IntByReference();
/* 1209 */     int rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, (char[])null, lpcbData);
/*      */     
/* 1211 */     if (rc != 0 && rc != 122)
/*      */     {
/* 1213 */       throw new Win32Exception(rc);
/*      */     }
/* 1215 */     if (lpType.getValue() != 11) {
/* 1216 */       throw new RuntimeException("Unexpected registry type " + lpType
/* 1217 */           .getValue() + ", expected REG_QWORD");
/*      */     }
/* 1219 */     LongByReference data = new LongByReference();
/* 1220 */     rc = Advapi32.INSTANCE.RegQueryValueEx(hKey, value, 0, lpType, data, lpcbData);
/*      */     
/* 1222 */     if (rc != 0 && rc != 122)
/*      */     {
/* 1224 */       throw new Win32Exception(rc);
/*      */     }
/* 1226 */     return data.getValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Object registryGetValue(WinReg.HKEY hkKey, String subKey, String lpValueName) {
/* 1243 */     Object result = null;
/* 1244 */     IntByReference lpType = new IntByReference();
/* 1245 */     IntByReference lpcbData = new IntByReference();
/*      */     
/* 1247 */     int rc = Advapi32.INSTANCE.RegGetValue(hkKey, subKey, lpValueName, 65535, lpType, (Pointer)null, lpcbData);
/*      */ 
/*      */ 
/*      */     
/* 1251 */     if (lpType.getValue() == 0) {
/* 1252 */       return null;
/*      */     }
/* 1254 */     if (rc != 0 && rc != 122)
/*      */     {
/* 1256 */       throw new Win32Exception(rc);
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1263 */     Memory byteData = new Memory((lpcbData.getValue() + Native.WCHAR_SIZE));
/* 1264 */     byteData.clear();
/*      */     
/* 1266 */     rc = Advapi32.INSTANCE.RegGetValue(hkKey, subKey, lpValueName, 65535, lpType, (Pointer)byteData, lpcbData);
/*      */ 
/*      */     
/* 1269 */     if (rc != 0) {
/* 1270 */       throw new Win32Exception(rc);
/*      */     }
/*      */     
/* 1273 */     if (lpType.getValue() == 4) {
/* 1274 */       result = Integer.valueOf(byteData.getInt(0L));
/* 1275 */     } else if (lpType.getValue() == 11) {
/* 1276 */       result = Long.valueOf(byteData.getLong(0L));
/* 1277 */     } else if (lpType.getValue() == 3) {
/* 1278 */       result = byteData.getByteArray(0L, lpcbData.getValue());
/* 1279 */     } else if (lpType.getValue() == 1 || lpType
/* 1280 */       .getValue() == 2) {
/* 1281 */       if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
/* 1282 */         result = byteData.getWideString(0L);
/*      */       } else {
/* 1284 */         result = byteData.getString(0L);
/*      */       } 
/*      */     } 
/*      */     
/* 1288 */     return result;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean registryCreateKey(WinReg.HKEY hKey, String keyName) {
/* 1301 */     return registryCreateKey(hKey, keyName, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean registryCreateKey(WinReg.HKEY hKey, String keyName, int samDesiredExtra) {
/* 1317 */     WinReg.HKEYByReference phkResult = new WinReg.HKEYByReference();
/* 1318 */     IntByReference lpdwDisposition = new IntByReference();
/* 1319 */     int rc = Advapi32.INSTANCE.RegCreateKeyEx(hKey, keyName, 0, null, 0, 0x20019 | samDesiredExtra, null, phkResult, lpdwDisposition);
/*      */ 
/*      */     
/* 1322 */     if (rc != 0) {
/* 1323 */       throw new Win32Exception(rc);
/*      */     }
/* 1325 */     rc = Advapi32.INSTANCE.RegCloseKey(phkResult.getValue());
/* 1326 */     if (rc != 0) {
/* 1327 */       throw new Win32Exception(rc);
/*      */     }
/* 1329 */     return (1 == lpdwDisposition.getValue());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean registryCreateKey(WinReg.HKEY root, String parentPath, String keyName) {
/* 1345 */     return registryCreateKey(root, parentPath, keyName, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean registryCreateKey(WinReg.HKEY root, String parentPath, String keyName, int samDesiredExtra) {
/* 1364 */     WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
/* 1365 */     int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, parentPath, 0, 0x4 | samDesiredExtra, phkKey);
/*      */     
/* 1367 */     if (rc != 0) {
/* 1368 */       throw new Win32Exception(rc);
/*      */     }
/*      */     try {
/* 1371 */       return registryCreateKey(phkKey.getValue(), keyName);
/*      */     } finally {
/* 1373 */       rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
/* 1374 */       if (rc != 0) {
/* 1375 */         throw new Win32Exception(rc);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void registrySetIntValue(WinReg.HKEY hKey, String name, int value) {
/* 1391 */     byte[] data = new byte[4];
/* 1392 */     data[0] = (byte)(value & 0xFF);
/* 1393 */     data[1] = (byte)(value >> 8 & 0xFF);
/* 1394 */     data[2] = (byte)(value >> 16 & 0xFF);
/* 1395 */     data[3] = (byte)(value >> 24 & 0xFF);
/* 1396 */     int rc = Advapi32.INSTANCE.RegSetValueEx(hKey, name, 0, 4, data, 4);
/*      */     
/* 1398 */     if (rc != 0) {
/* 1399 */       throw new Win32Exception(rc);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void registrySetIntValue(WinReg.HKEY root, String keyPath, String name, int value) {
/* 1417 */     registrySetIntValue(root, keyPath, name, value, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void registrySetIntValue(WinReg.HKEY root, String keyPath, String name, int value, int samDesiredExtra) {
/* 1437 */     WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
/* 1438 */     int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 0x2001F | samDesiredExtra, phkKey);
/*      */     
/* 1440 */     if (rc != 0) {
/* 1441 */       throw new Win32Exception(rc);
/*      */     }
/*      */     try {
/* 1444 */       registrySetIntValue(phkKey.getValue(), name, value);
/*      */     } finally {
/* 1446 */       rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
/* 1447 */       if (rc != 0) {
/* 1448 */         throw new Win32Exception(rc);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void registrySetLongValue(WinReg.HKEY hKey, String name, long value) {
/* 1464 */     byte[] data = new byte[8];
/* 1465 */     data[0] = (byte)(int)(value & 0xFFL);
/* 1466 */     data[1] = (byte)(int)(value >> 8L & 0xFFL);
/* 1467 */     data[2] = (byte)(int)(value >> 16L & 0xFFL);
/* 1468 */     data[3] = (byte)(int)(value >> 24L & 0xFFL);
/* 1469 */     data[4] = (byte)(int)(value >> 32L & 0xFFL);
/* 1470 */     data[5] = (byte)(int)(value >> 40L & 0xFFL);
/* 1471 */     data[6] = (byte)(int)(value >> 48L & 0xFFL);
/* 1472 */     data[7] = (byte)(int)(value >> 56L & 0xFFL);
/* 1473 */     int rc = Advapi32.INSTANCE.RegSetValueEx(hKey, name, 0, 11, data, 8);
/*      */     
/* 1475 */     if (rc != 0) {
/* 1476 */       throw new Win32Exception(rc);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void registrySetLongValue(WinReg.HKEY root, String keyPath, String name, long value) {
/* 1494 */     registrySetLongValue(root, keyPath, name, value, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void registrySetLongValue(WinReg.HKEY root, String keyPath, String name, long value, int samDesiredExtra) {
/* 1514 */     WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
/* 1515 */     int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 0x2001F | samDesiredExtra, phkKey);
/*      */     
/* 1517 */     if (rc != 0) {
/* 1518 */       throw new Win32Exception(rc);
/*      */     }
/*      */     try {
/* 1521 */       registrySetLongValue(phkKey.getValue(), name, value);
/*      */     } finally {
/* 1523 */       rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
/* 1524 */       if (rc != 0) {
/* 1525 */         throw new Win32Exception(rc);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void registrySetStringValue(WinReg.HKEY hKey, String name, String value) {
/*      */     Memory data;
/* 1542 */     if (value == null) {
/* 1543 */       value = "";
/*      */     }
/*      */     
/* 1546 */     if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
/* 1547 */       data = new Memory(((value.length() + 1) * Native.WCHAR_SIZE));
/* 1548 */       data.setWideString(0L, value);
/*      */     } else {
/* 1550 */       data = new Memory((value.length() + 1));
/* 1551 */       data.setString(0L, value);
/*      */     } 
/* 1553 */     int rc = Advapi32.INSTANCE.RegSetValueEx(hKey, name, 0, 1, (Pointer)data, 
/* 1554 */         (int)data.size());
/* 1555 */     if (rc != 0) {
/* 1556 */       throw new Win32Exception(rc);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void registrySetStringValue(WinReg.HKEY root, String keyPath, String name, String value) {
/* 1574 */     registrySetStringValue(root, keyPath, name, value, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void registrySetStringValue(WinReg.HKEY root, String keyPath, String name, String value, int samDesiredExtra) {
/* 1594 */     WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
/* 1595 */     int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 0x2001F | samDesiredExtra, phkKey);
/*      */     
/* 1597 */     if (rc != 0) {
/* 1598 */       throw new Win32Exception(rc);
/*      */     }
/*      */     try {
/* 1601 */       registrySetStringValue(phkKey.getValue(), name, value);
/*      */     } finally {
/* 1603 */       rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
/* 1604 */       if (rc != 0) {
/* 1605 */         throw new Win32Exception(rc);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void registrySetExpandableStringValue(WinReg.HKEY hKey, String name, String value) {
/*      */     Memory data;
/* 1623 */     if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
/* 1624 */       data = new Memory(((value.length() + 1) * Native.WCHAR_SIZE));
/* 1625 */       data.setWideString(0L, value);
/*      */     } else {
/* 1627 */       data = new Memory((value.length() + 1));
/* 1628 */       data.setString(0L, value);
/*      */     } 
/* 1630 */     int rc = Advapi32.INSTANCE.RegSetValueEx(hKey, name, 0, 2, (Pointer)data, 
/* 1631 */         (int)data.size());
/* 1632 */     if (rc != 0) {
/* 1633 */       throw new Win32Exception(rc);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void registrySetExpandableStringValue(WinReg.HKEY root, String keyPath, String name, String value) {
/* 1651 */     registrySetExpandableStringValue(root, keyPath, name, value, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void registrySetExpandableStringValue(WinReg.HKEY root, String keyPath, String name, String value, int samDesiredExtra) {
/* 1671 */     WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
/* 1672 */     int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 0x2001F | samDesiredExtra, phkKey);
/*      */     
/* 1674 */     if (rc != 0) {
/* 1675 */       throw new Win32Exception(rc);
/*      */     }
/*      */     try {
/* 1678 */       registrySetExpandableStringValue(phkKey.getValue(), name, value);
/*      */     } finally {
/* 1680 */       rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
/* 1681 */       if (rc != 0) {
/* 1682 */         throw new Win32Exception(rc);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void registrySetStringArray(WinReg.HKEY hKey, String name, String[] arr) {
/* 1700 */     int charwidth = (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) ? Native.WCHAR_SIZE : 1;
/*      */     
/* 1702 */     int size = 0;
/* 1703 */     for (String s : arr) {
/* 1704 */       size += s.length() * charwidth;
/* 1705 */       size += charwidth;
/*      */     } 
/* 1707 */     size += charwidth;
/*      */     
/* 1709 */     int offset = 0;
/* 1710 */     Memory data = new Memory(size);
/* 1711 */     data.clear();
/* 1712 */     for (String s : arr) {
/* 1713 */       if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
/* 1714 */         data.setWideString(offset, s);
/*      */       } else {
/* 1716 */         data.setString(offset, s);
/*      */       } 
/* 1718 */       offset += s.length() * charwidth;
/* 1719 */       offset += charwidth;
/*      */     } 
/*      */     
/* 1722 */     int rc = Advapi32.INSTANCE.RegSetValueEx(hKey, name, 0, 7, (Pointer)data, size);
/*      */ 
/*      */     
/* 1725 */     if (rc != 0) {
/* 1726 */       throw new Win32Exception(rc);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void registrySetStringArray(WinReg.HKEY root, String keyPath, String name, String[] arr) {
/* 1744 */     registrySetStringArray(root, keyPath, name, arr, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void registrySetStringArray(WinReg.HKEY root, String keyPath, String name, String[] arr, int samDesiredExtra) {
/* 1764 */     WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
/* 1765 */     int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 0x2001F | samDesiredExtra, phkKey);
/*      */     
/* 1767 */     if (rc != 0) {
/* 1768 */       throw new Win32Exception(rc);
/*      */     }
/*      */     try {
/* 1771 */       registrySetStringArray(phkKey.getValue(), name, arr);
/*      */     } finally {
/* 1773 */       rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
/* 1774 */       if (rc != 0) {
/* 1775 */         throw new Win32Exception(rc);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void registrySetBinaryValue(WinReg.HKEY hKey, String name, byte[] data) {
/* 1792 */     int rc = Advapi32.INSTANCE.RegSetValueEx(hKey, name, 0, 3, data, data.length);
/*      */     
/* 1794 */     if (rc != 0) {
/* 1795 */       throw new Win32Exception(rc);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void registrySetBinaryValue(WinReg.HKEY root, String keyPath, String name, byte[] data) {
/* 1813 */     registrySetBinaryValue(root, keyPath, name, data, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void registrySetBinaryValue(WinReg.HKEY root, String keyPath, String name, byte[] data, int samDesiredExtra) {
/* 1833 */     WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
/* 1834 */     int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 0x2001F | samDesiredExtra, phkKey);
/*      */     
/* 1836 */     if (rc != 0) {
/* 1837 */       throw new Win32Exception(rc);
/*      */     }
/*      */     try {
/* 1840 */       registrySetBinaryValue(phkKey.getValue(), name, data);
/*      */     } finally {
/* 1842 */       rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
/* 1843 */       if (rc != 0) {
/* 1844 */         throw new Win32Exception(rc);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void registryDeleteKey(WinReg.HKEY hKey, String keyName) {
/* 1858 */     int rc = Advapi32.INSTANCE.RegDeleteKey(hKey, keyName);
/* 1859 */     if (rc != 0) {
/* 1860 */       throw new Win32Exception(rc);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void registryDeleteKey(WinReg.HKEY root, String keyPath, String keyName) {
/* 1876 */     registryDeleteKey(root, keyPath, keyName, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void registryDeleteKey(WinReg.HKEY root, String keyPath, String keyName, int samDesiredExtra) {
/* 1894 */     WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
/* 1895 */     int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 0x2001F | samDesiredExtra, phkKey);
/*      */     
/* 1897 */     if (rc != 0) {
/* 1898 */       throw new Win32Exception(rc);
/*      */     }
/*      */     try {
/* 1901 */       registryDeleteKey(phkKey.getValue(), keyName);
/*      */     } finally {
/* 1903 */       rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
/* 1904 */       if (rc != 0) {
/* 1905 */         throw new Win32Exception(rc);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void registryDeleteValue(WinReg.HKEY hKey, String valueName) {
/* 1919 */     int rc = Advapi32.INSTANCE.RegDeleteValue(hKey, valueName);
/* 1920 */     if (rc != 0) {
/* 1921 */       throw new Win32Exception(rc);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void registryDeleteValue(WinReg.HKEY root, String keyPath, String valueName) {
/* 1937 */     registryDeleteValue(root, keyPath, valueName, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void registryDeleteValue(WinReg.HKEY root, String keyPath, String valueName, int samDesiredExtra) {
/* 1955 */     WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
/* 1956 */     int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 0x2001F | samDesiredExtra, phkKey);
/*      */     
/* 1958 */     if (rc != 0) {
/* 1959 */       throw new Win32Exception(rc);
/*      */     }
/*      */     try {
/* 1962 */       registryDeleteValue(phkKey.getValue(), valueName);
/*      */     } finally {
/* 1964 */       rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
/* 1965 */       if (rc != 0) {
/* 1966 */         throw new Win32Exception(rc);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] registryGetKeys(WinReg.HKEY hKey) {
/* 1979 */     IntByReference lpcSubKeys = new IntByReference();
/* 1980 */     IntByReference lpcMaxSubKeyLen = new IntByReference();
/*      */     
/* 1982 */     int rc = Advapi32.INSTANCE.RegQueryInfoKey(hKey, null, null, null, lpcSubKeys, lpcMaxSubKeyLen, null, null, null, null, null, null);
/*      */     
/* 1984 */     if (rc != 0) {
/* 1985 */       throw new Win32Exception(rc);
/*      */     }
/* 1987 */     ArrayList<String> keys = new ArrayList<String>(lpcSubKeys.getValue());
/* 1988 */     char[] name = new char[lpcMaxSubKeyLen.getValue() + 1];
/* 1989 */     for (int i = 0; i < lpcSubKeys.getValue(); i++) {
/*      */       
/* 1991 */       IntByReference lpcchValueName = new IntByReference(lpcMaxSubKeyLen.getValue() + 1);
/* 1992 */       rc = Advapi32.INSTANCE.RegEnumKeyEx(hKey, i, name, lpcchValueName, null, null, null, null);
/*      */       
/* 1994 */       if (rc != 0) {
/* 1995 */         throw new Win32Exception(rc);
/*      */       }
/* 1997 */       keys.add(Native.toString(name));
/*      */     } 
/* 1999 */     return keys.<String>toArray(new String[0]);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] registryGetKeys(WinReg.HKEY root, String keyPath) {
/* 2012 */     return registryGetKeys(root, keyPath, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] registryGetKeys(WinReg.HKEY root, String keyPath, int samDesiredExtra) {
/* 2028 */     WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
/* 2029 */     int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 0x20019 | samDesiredExtra, phkKey);
/*      */     
/* 2031 */     if (rc != 0) {
/* 2032 */       throw new Win32Exception(rc);
/*      */     }
/*      */     try {
/* 2035 */       return registryGetKeys(phkKey.getValue());
/*      */     } finally {
/* 2037 */       rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
/* 2038 */       if (rc != 0) {
/* 2039 */         throw new Win32Exception(rc);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static WinReg.HKEYByReference registryGetKey(WinReg.HKEY root, String keyPath, int samDesired) {
/* 2059 */     WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
/* 2060 */     int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, samDesired, phkKey);
/*      */     
/* 2062 */     if (rc != 0) {
/* 2063 */       throw new Win32Exception(rc);
/*      */     }
/*      */     
/* 2066 */     return phkKey;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void registryCloseKey(WinReg.HKEY hKey) {
/* 2076 */     int rc = Advapi32.INSTANCE.RegCloseKey(hKey);
/* 2077 */     if (rc != 0) {
/* 2078 */       throw new Win32Exception(rc);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static TreeMap<String, Object> registryGetValues(WinReg.HKEY hKey) {
/* 2090 */     IntByReference lpcValues = new IntByReference();
/* 2091 */     IntByReference lpcMaxValueNameLen = new IntByReference();
/* 2092 */     IntByReference lpcMaxValueLen = new IntByReference();
/* 2093 */     int rc = Advapi32.INSTANCE.RegQueryInfoKey(hKey, null, null, null, null, null, null, lpcValues, lpcMaxValueNameLen, lpcMaxValueLen, null, null);
/*      */ 
/*      */     
/* 2096 */     if (rc != 0) {
/* 2097 */       throw new Win32Exception(rc);
/*      */     }
/* 2099 */     TreeMap<String, Object> keyValues = new TreeMap<String, Object>();
/* 2100 */     char[] name = new char[lpcMaxValueNameLen.getValue() + 1];
/*      */ 
/*      */ 
/*      */     
/* 2104 */     Memory byteData = new Memory((lpcMaxValueLen.getValue() + 2 * Native.WCHAR_SIZE));
/* 2105 */     for (int i = 0; i < lpcValues.getValue(); i++) {
/* 2106 */       byteData.clear();
/*      */       
/* 2108 */       IntByReference lpcchValueName = new IntByReference(lpcMaxValueNameLen.getValue() + 1);
/*      */       
/* 2110 */       IntByReference lpcbData = new IntByReference(lpcMaxValueLen.getValue());
/* 2111 */       IntByReference lpType = new IntByReference();
/* 2112 */       rc = Advapi32.INSTANCE.RegEnumValue(hKey, i, name, lpcchValueName, (IntByReference)null, lpType, (Pointer)byteData, lpcbData);
/*      */       
/* 2114 */       if (rc != 0) {
/* 2115 */         throw new Win32Exception(rc);
/*      */       }
/*      */       
/* 2118 */       String nameString = Native.toString(name);
/*      */       
/* 2120 */       if (lpcbData.getValue() == 0) {
/* 2121 */         switch (lpType.getValue()) {
/*      */           case 3:
/* 2123 */             keyValues.put(nameString, new byte[0]);
/*      */             break;
/*      */           
/*      */           case 1:
/*      */           case 2:
/* 2128 */             keyValues.put(nameString, new char[0]);
/*      */             break;
/*      */           
/*      */           case 7:
/* 2132 */             keyValues.put(nameString, new String[0]);
/*      */             break;
/*      */           
/*      */           case 0:
/* 2136 */             keyValues.put(nameString, null);
/*      */             break;
/*      */           
/*      */           default:
/* 2140 */             throw new RuntimeException("Unsupported empty type: " + lpType
/* 2141 */                 .getValue());
/*      */         } 
/*      */       } else {
/*      */         ArrayList<String> result;
/*      */         int offset;
/* 2146 */         switch (lpType.getValue()) {
/*      */           case 11:
/* 2148 */             keyValues.put(nameString, Long.valueOf(byteData.getLong(0L)));
/*      */             break;
/*      */           
/*      */           case 4:
/* 2152 */             keyValues.put(nameString, Integer.valueOf(byteData.getInt(0L)));
/*      */             break;
/*      */           
/*      */           case 1:
/*      */           case 2:
/* 2157 */             if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
/* 2158 */               keyValues.put(nameString, byteData.getWideString(0L)); break;
/*      */             } 
/* 2160 */             keyValues.put(nameString, byteData.getString(0L));
/*      */             break;
/*      */ 
/*      */           
/*      */           case 3:
/* 2165 */             keyValues.put(nameString, byteData
/* 2166 */                 .getByteArray(0L, lpcbData.getValue()));
/*      */             break;
/*      */           
/*      */           case 7:
/* 2170 */             result = new ArrayList<String>();
/* 2171 */             offset = 0;
/* 2172 */             while (offset < byteData.size()) {
/*      */               String s;
/* 2174 */               if (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) {
/* 2175 */                 s = byteData.getWideString(offset);
/* 2176 */                 offset += s.length() * Native.WCHAR_SIZE;
/* 2177 */                 offset += Native.WCHAR_SIZE;
/*      */               } else {
/* 2179 */                 s = byteData.getString(offset);
/* 2180 */                 offset += s.length();
/* 2181 */                 offset++;
/*      */               } 
/*      */               
/* 2184 */               if (s.length() == 0) {
/*      */                 break;
/*      */               }
/*      */ 
/*      */ 
/*      */               
/* 2190 */               result.add(s);
/*      */             } 
/*      */             
/* 2193 */             keyValues.put(nameString, result.toArray(new String[0]));
/*      */             break;
/*      */           
/*      */           default:
/* 2197 */             throw new RuntimeException("Unsupported type: " + lpType
/* 2198 */                 .getValue());
/*      */         } 
/*      */       } 
/* 2201 */     }  return keyValues;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static TreeMap<String, Object> registryGetValues(WinReg.HKEY root, String keyPath) {
/* 2215 */     return registryGetValues(root, keyPath, 0);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static TreeMap<String, Object> registryGetValues(WinReg.HKEY root, String keyPath, int samDesiredExtra) {
/* 2232 */     WinReg.HKEYByReference phkKey = new WinReg.HKEYByReference();
/* 2233 */     int rc = Advapi32.INSTANCE.RegOpenKeyEx(root, keyPath, 0, 0x20019 | samDesiredExtra, phkKey);
/*      */     
/* 2235 */     if (rc != 0) {
/* 2236 */       throw new Win32Exception(rc);
/*      */     }
/*      */     try {
/* 2239 */       return registryGetValues(phkKey.getValue());
/*      */     } finally {
/* 2241 */       rc = Advapi32.INSTANCE.RegCloseKey(phkKey.getValue());
/* 2242 */       if (rc != 0) {
/* 2243 */         throw new Win32Exception(rc);
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static InfoKey registryQueryInfoKey(WinReg.HKEY hKey, int lpcbSecurityDescriptor) {
/* 2260 */     InfoKey infoKey = new InfoKey(hKey, lpcbSecurityDescriptor);
/* 2261 */     int rc = Advapi32.INSTANCE.RegQueryInfoKey(hKey, infoKey.lpClass, infoKey.lpcClass, null, infoKey.lpcSubKeys, infoKey.lpcMaxSubKeyLen, infoKey.lpcMaxClassLen, infoKey.lpcValues, infoKey.lpcMaxValueNameLen, infoKey.lpcMaxValueLen, infoKey.lpcbSecurityDescriptor, infoKey.lpftLastWriteTime);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2268 */     if (rc != 0) {
/* 2269 */       throw new Win32Exception(rc);
/*      */     }
/*      */     
/* 2272 */     return infoKey;
/*      */   }
/*      */   
/*      */   public static class InfoKey {
/*      */     public WinReg.HKEY hKey;
/* 2277 */     public char[] lpClass = new char[260];
/* 2278 */     public IntByReference lpcClass = new IntByReference(260);
/* 2279 */     public IntByReference lpcSubKeys = new IntByReference();
/* 2280 */     public IntByReference lpcMaxSubKeyLen = new IntByReference();
/* 2281 */     public IntByReference lpcMaxClassLen = new IntByReference();
/* 2282 */     public IntByReference lpcValues = new IntByReference();
/* 2283 */     public IntByReference lpcMaxValueNameLen = new IntByReference();
/* 2284 */     public IntByReference lpcMaxValueLen = new IntByReference();
/* 2285 */     public IntByReference lpcbSecurityDescriptor = new IntByReference();
/* 2286 */     public WinBase.FILETIME lpftLastWriteTime = new WinBase.FILETIME();
/*      */ 
/*      */     
/*      */     public InfoKey() {}
/*      */     
/*      */     public InfoKey(WinReg.HKEY hKey, int securityDescriptor) {
/* 2292 */       this.hKey = hKey;
/* 2293 */       this.lpcbSecurityDescriptor = new IntByReference(securityDescriptor);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static EnumKey registryRegEnumKey(WinReg.HKEY hKey, int dwIndex) {
/* 2307 */     EnumKey enumKey = new EnumKey(hKey, dwIndex);
/* 2308 */     int rc = Advapi32.INSTANCE.RegEnumKeyEx(hKey, enumKey.dwIndex, enumKey.lpName, enumKey.lpcName, null, enumKey.lpClass, enumKey.lpcbClass, enumKey.lpftLastWriteTime);
/*      */ 
/*      */ 
/*      */     
/* 2312 */     if (rc != 0) {
/* 2313 */       throw new Win32Exception(rc);
/*      */     }
/*      */     
/* 2316 */     return enumKey;
/*      */   }
/*      */   
/*      */   public static class EnumKey {
/*      */     public WinReg.HKEY hKey;
/* 2321 */     public int dwIndex = 0;
/* 2322 */     public char[] lpName = new char[255];
/* 2323 */     public IntByReference lpcName = new IntByReference(255);
/*      */     
/* 2325 */     public char[] lpClass = new char[255];
/* 2326 */     public IntByReference lpcbClass = new IntByReference(255);
/*      */     
/* 2328 */     public WinBase.FILETIME lpftLastWriteTime = new WinBase.FILETIME();
/*      */ 
/*      */     
/*      */     public EnumKey() {}
/*      */     
/*      */     public EnumKey(WinReg.HKEY hKey, int dwIndex) {
/* 2334 */       this.hKey = hKey;
/* 2335 */       this.dwIndex = dwIndex;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getEnvironmentBlock(Map<String, String> environment) {
/* 2350 */     StringBuilder out = new StringBuilder(environment.size() * 32);
/* 2351 */     for (Map.Entry<String, String> entry : environment.entrySet()) {
/* 2352 */       String key = entry.getKey(), value = entry.getValue();
/* 2353 */       if (value != null) {
/* 2354 */         out.append(key).append("=").append(value).append(false);
/*      */       }
/*      */     } 
/* 2357 */     return out.append(false).toString();
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public enum EventLogType
/*      */   {
/* 2364 */     Error, Warning, Informational, AuditSuccess, AuditFailure;
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static class EventLogRecord
/*      */   {
/*      */     private WinNT.EVENTLOGRECORD _record;
/*      */ 
/*      */     
/*      */     private String _source;
/*      */     
/*      */     private byte[] _data;
/*      */     
/*      */     private String[] _strings;
/*      */ 
/*      */     
/*      */     public WinNT.EVENTLOGRECORD getRecord() {
/* 2382 */       return this._record;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int getInstanceId() {
/* 2393 */       return this._record.EventID.intValue();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     @Deprecated
/*      */     public int getEventId() {
/* 2404 */       return this._record.EventID.intValue();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String getSource() {
/* 2413 */       return this._source;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int getStatusCode() {
/* 2424 */       return this._record.EventID.intValue() & 0xFFFF;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int getRecordNumber() {
/* 2435 */       return this._record.RecordNumber.intValue();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public int getLength() {
/* 2444 */       return this._record.Length.intValue();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public String[] getStrings() {
/* 2453 */       return this._strings;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Advapi32Util.EventLogType getType() {
/* 2462 */       switch (this._record.EventType.intValue()) {
/*      */         case 0:
/*      */         case 4:
/* 2465 */           return Advapi32Util.EventLogType.Informational;
/*      */         case 16:
/* 2467 */           return Advapi32Util.EventLogType.AuditFailure;
/*      */         case 8:
/* 2469 */           return Advapi32Util.EventLogType.AuditSuccess;
/*      */         case 1:
/* 2471 */           return Advapi32Util.EventLogType.Error;
/*      */         case 2:
/* 2473 */           return Advapi32Util.EventLogType.Warning;
/*      */       } 
/* 2475 */       throw new RuntimeException("Invalid type: " + this._record.EventType
/* 2476 */           .intValue());
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public byte[] getData() {
/* 2486 */       return this._data;
/*      */     }
/*      */     
/*      */     public EventLogRecord(Pointer pevlr) {
/* 2490 */       this._record = new WinNT.EVENTLOGRECORD(pevlr);
/* 2491 */       this._source = pevlr.getWideString(this._record.size());
/*      */       
/* 2493 */       if (this._record.DataLength.intValue() > 0) {
/* 2494 */         this._data = pevlr.getByteArray(this._record.DataOffset.intValue(), this._record.DataLength
/* 2495 */             .intValue());
/*      */       }
/*      */       
/* 2498 */       if (this._record.NumStrings.intValue() > 0) {
/* 2499 */         ArrayList<String> strings = new ArrayList<String>();
/* 2500 */         int count = this._record.NumStrings.intValue();
/* 2501 */         long offset = this._record.StringOffset.intValue();
/* 2502 */         while (count > 0) {
/* 2503 */           String s = pevlr.getWideString(offset);
/* 2504 */           strings.add(s);
/* 2505 */           offset += (s.length() * Native.WCHAR_SIZE);
/* 2506 */           offset += Native.WCHAR_SIZE;
/* 2507 */           count--;
/*      */         } 
/* 2509 */         this._strings = strings.<String>toArray(new String[0]);
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */   
/*      */   public static class EventLogIterator
/*      */     implements Iterable<EventLogRecord>, Iterator<EventLogRecord>
/*      */   {
/*      */     private WinNT.HANDLE _h;
/*      */     
/* 2521 */     private Memory _buffer = new Memory(65536L);
/*      */     
/*      */     private boolean _done = false;
/* 2524 */     private int _dwRead = 0;
/*      */     
/* 2526 */     private Pointer _pevlr = null;
/*      */     private int _flags;
/*      */     
/*      */     public EventLogIterator(String sourceName) {
/* 2530 */       this(null, sourceName, 4);
/*      */     }
/*      */     
/*      */     public EventLogIterator(String serverName, String sourceName, int flags) {
/* 2534 */       this._flags = flags;
/* 2535 */       this._h = Advapi32.INSTANCE.OpenEventLog(serverName, sourceName);
/* 2536 */       if (this._h == null) {
/* 2537 */         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*      */       }
/*      */     }
/*      */ 
/*      */     
/*      */     private boolean read() {
/* 2543 */       if (this._done || this._dwRead > 0) {
/* 2544 */         return false;
/*      */       }
/*      */       
/* 2547 */       IntByReference pnBytesRead = new IntByReference();
/* 2548 */       IntByReference pnMinNumberOfBytesNeeded = new IntByReference();
/*      */ 
/*      */       
/* 2551 */       if (!Advapi32.INSTANCE.ReadEventLog(this._h, 0x1 | this._flags, 0, (Pointer)this._buffer, 
/* 2552 */           (int)this._buffer.size(), pnBytesRead, pnMinNumberOfBytesNeeded)) {
/*      */ 
/*      */         
/* 2555 */         int rc = Kernel32.INSTANCE.GetLastError();
/*      */ 
/*      */         
/* 2558 */         if (rc == 122) {
/* 2559 */           this._buffer = new Memory(pnMinNumberOfBytesNeeded.getValue());
/*      */           
/* 2561 */           if (!Advapi32.INSTANCE.ReadEventLog(this._h, 0x1 | this._flags, 0, (Pointer)this._buffer, 
/*      */               
/* 2563 */               (int)this._buffer.size(), pnBytesRead, pnMinNumberOfBytesNeeded))
/*      */           {
/* 2565 */             throw new Win32Exception(Kernel32.INSTANCE
/* 2566 */                 .GetLastError());
/*      */           }
/*      */         } else {
/*      */           
/* 2570 */           close();
/* 2571 */           if (rc != 38) {
/* 2572 */             throw new Win32Exception(rc);
/*      */           }
/* 2574 */           return false;
/*      */         } 
/*      */       } 
/*      */       
/* 2578 */       this._dwRead = pnBytesRead.getValue();
/* 2579 */       this._pevlr = (Pointer)this._buffer;
/* 2580 */       return true;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void close() {
/* 2588 */       this._done = true;
/* 2589 */       if (this._h != null) {
/* 2590 */         if (!Advapi32.INSTANCE.CloseEventLog(this._h)) {
/* 2591 */           throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*      */         }
/* 2593 */         this._h = null;
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Iterator<Advapi32Util.EventLogRecord> iterator() {
/* 2601 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public boolean hasNext() {
/* 2608 */       read();
/* 2609 */       return !this._done;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Advapi32Util.EventLogRecord next() {
/* 2616 */       read();
/* 2617 */       Advapi32Util.EventLogRecord record = new Advapi32Util.EventLogRecord(this._pevlr);
/* 2618 */       this._dwRead -= record.getLength();
/* 2619 */       this._pevlr = this._pevlr.share(record.getLength());
/* 2620 */       return record;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void remove() {}
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static WinNT.ACE_HEADER[] getFileSecurity(String fileName, boolean compact) {
/*      */     boolean repeat;
/*      */     Memory memory;
/* 2637 */     int infoType = 4;
/* 2638 */     int nLength = 1024;
/*      */ 
/*      */ 
/*      */     
/*      */     do {
/* 2643 */       repeat = false;
/* 2644 */       memory = new Memory(nLength);
/* 2645 */       IntByReference lpnSize = new IntByReference();
/* 2646 */       boolean succeded = Advapi32.INSTANCE.GetFileSecurity(fileName, infoType, (Pointer)memory, nLength, lpnSize);
/*      */ 
/*      */       
/* 2649 */       if (!succeded) {
/* 2650 */         int lastError = Kernel32.INSTANCE.GetLastError();
/* 2651 */         memory.clear();
/* 2652 */         if (122 != lastError) {
/* 2653 */           throw new Win32Exception(lastError);
/*      */         }
/*      */       } 
/* 2656 */       int lengthNeeded = lpnSize.getValue();
/* 2657 */       if (nLength >= lengthNeeded)
/* 2658 */         continue;  repeat = true;
/* 2659 */       nLength = lengthNeeded;
/* 2660 */       memory.clear();
/*      */     }
/* 2662 */     while (repeat);
/*      */     
/* 2664 */     WinNT.SECURITY_DESCRIPTOR_RELATIVE sdr = new WinNT.SECURITY_DESCRIPTOR_RELATIVE((Pointer)memory);
/*      */     
/* 2666 */     WinNT.ACL dacl = sdr.getDiscretionaryACL();
/* 2667 */     WinNT.ACE_HEADER[] aceStructures = dacl.getACEs();
/*      */     
/* 2669 */     if (compact) {
/* 2670 */       List<WinNT.ACE_HEADER> result = new ArrayList<WinNT.ACE_HEADER>();
/* 2671 */       Map<String, WinNT.ACCESS_ACEStructure> aceMap = new HashMap<String, WinNT.ACCESS_ACEStructure>();
/* 2672 */       for (WinNT.ACE_HEADER aceStructure : aceStructures) {
/* 2673 */         if (aceStructure instanceof WinNT.ACCESS_ACEStructure) {
/* 2674 */           WinNT.ACCESS_ACEStructure accessACEStructure = (WinNT.ACCESS_ACEStructure)aceStructure;
/* 2675 */           boolean inherted = ((aceStructure.AceFlags & 0x1F) != 0);
/*      */           
/* 2677 */           String key = accessACEStructure.getSidString() + "/" + inherted + "/" + aceStructure.getClass().getName();
/* 2678 */           WinNT.ACCESS_ACEStructure aceStructure2 = aceMap.get(key);
/* 2679 */           if (aceStructure2 != null) {
/* 2680 */             int accessMask = aceStructure2.Mask;
/* 2681 */             accessMask |= accessACEStructure.Mask;
/* 2682 */             aceStructure2.Mask = accessMask;
/*      */           } else {
/* 2684 */             aceMap.put(key, accessACEStructure);
/* 2685 */             result.add(aceStructure2);
/*      */           } 
/*      */         } else {
/* 2688 */           result.add(aceStructure);
/*      */         } 
/*      */       } 
/* 2691 */       return result.<WinNT.ACE_HEADER>toArray(new WinNT.ACE_HEADER[0]);
/*      */     } 
/*      */     
/* 2694 */     return aceStructures;
/*      */   }
/*      */   
/*      */   public enum AccessCheckPermission {
/* 2698 */     READ(-2147483648),
/* 2699 */     WRITE(1073741824),
/* 2700 */     EXECUTE(536870912);
/*      */     
/*      */     final int code;
/*      */     
/*      */     AccessCheckPermission(int code) {
/* 2705 */       this.code = code;
/*      */     }
/*      */     
/*      */     public int getCode() {
/* 2709 */       return this.code;
/*      */     }
/*      */   }
/*      */ 
/*      */   
/*      */   private static Memory getSecurityDescriptorForFile(String absoluteFilePath) {
/* 2715 */     int infoType = 7;
/*      */ 
/*      */     
/* 2718 */     IntByReference lpnSize = new IntByReference();
/* 2719 */     boolean succeeded = Advapi32.INSTANCE.GetFileSecurity(absoluteFilePath, 7, null, 0, lpnSize);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2725 */     if (!succeeded) {
/* 2726 */       int lastError = Kernel32.INSTANCE.GetLastError();
/* 2727 */       if (122 != lastError) {
/* 2728 */         throw new Win32Exception(lastError);
/*      */       }
/*      */     } 
/*      */     
/* 2732 */     int nLength = lpnSize.getValue();
/* 2733 */     Memory securityDescriptorMemoryPointer = new Memory(nLength);
/* 2734 */     succeeded = Advapi32.INSTANCE.GetFileSecurity(absoluteFilePath, 7, (Pointer)securityDescriptorMemoryPointer, nLength, lpnSize);
/*      */ 
/*      */     
/* 2737 */     if (!succeeded) {
/* 2738 */       securityDescriptorMemoryPointer.clear();
/* 2739 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*      */     } 
/*      */     
/* 2742 */     return securityDescriptorMemoryPointer;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static Memory getSecurityDescriptorForObject(String absoluteObjectPath, int objectType, boolean getSACL) {
/* 2760 */     int infoType = 0x7 | (getSACL ? 8 : 0);
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2765 */     PointerByReference ppSecurityDescriptor = new PointerByReference();
/*      */     
/* 2767 */     int lastError = Advapi32.INSTANCE.GetNamedSecurityInfo(absoluteObjectPath, objectType, infoType, null, null, null, null, ppSecurityDescriptor);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2777 */     if (lastError != 0) {
/* 2778 */       throw new Win32Exception(lastError);
/*      */     }
/*      */     
/* 2781 */     int nLength = Advapi32.INSTANCE.GetSecurityDescriptorLength(ppSecurityDescriptor.getValue());
/* 2782 */     Memory memory = new Memory(nLength);
/* 2783 */     Pointer secValue = ppSecurityDescriptor.getValue();
/*      */     try {
/* 2785 */       byte[] data = secValue.getByteArray(0L, nLength);
/* 2786 */       memory.write(0L, data, 0, nLength);
/* 2787 */       return memory;
/*      */     } finally {
/* 2789 */       Kernel32Util.freeLocalMemory(secValue);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void setSecurityDescriptorForObject(String absoluteObjectPath, int objectType, WinNT.SECURITY_DESCRIPTOR_RELATIVE securityDescriptor, boolean setOwner, boolean setGroup, boolean setDACL, boolean setSACL, boolean setDACLProtectedStatus, boolean setSACLProtectedStatus) {
/* 2833 */     WinNT.PSID psidOwner = securityDescriptor.getOwner();
/* 2834 */     WinNT.PSID psidGroup = securityDescriptor.getGroup();
/* 2835 */     WinNT.ACL dacl = securityDescriptor.getDiscretionaryACL();
/* 2836 */     WinNT.ACL sacl = securityDescriptor.getSystemACL();
/*      */     
/* 2838 */     int infoType = 0;
/*      */     
/* 2840 */     if (setOwner) {
/* 2841 */       if (psidOwner == null)
/* 2842 */         throw new IllegalArgumentException("SECURITY_DESCRIPTOR_RELATIVE does not contain owner"); 
/* 2843 */       if (!Advapi32.INSTANCE.IsValidSid(psidOwner))
/* 2844 */         throw new IllegalArgumentException("Owner PSID is invalid"); 
/* 2845 */       infoType |= 0x1;
/*      */     } 
/*      */     
/* 2848 */     if (setGroup) {
/* 2849 */       if (psidGroup == null)
/* 2850 */         throw new IllegalArgumentException("SECURITY_DESCRIPTOR_RELATIVE does not contain group"); 
/* 2851 */       if (!Advapi32.INSTANCE.IsValidSid(psidGroup))
/* 2852 */         throw new IllegalArgumentException("Group PSID is invalid"); 
/* 2853 */       infoType |= 0x2;
/*      */     } 
/*      */     
/* 2856 */     if (setDACL) {
/* 2857 */       if (dacl == null)
/* 2858 */         throw new IllegalArgumentException("SECURITY_DESCRIPTOR_RELATIVE does not contain DACL"); 
/* 2859 */       if (!Advapi32.INSTANCE.IsValidAcl(dacl.getPointer()))
/* 2860 */         throw new IllegalArgumentException("DACL is invalid"); 
/* 2861 */       infoType |= 0x4;
/*      */     } 
/*      */     
/* 2864 */     if (setSACL) {
/* 2865 */       if (sacl == null)
/* 2866 */         throw new IllegalArgumentException("SECURITY_DESCRIPTOR_RELATIVE does not contain SACL"); 
/* 2867 */       if (!Advapi32.INSTANCE.IsValidAcl(sacl.getPointer()))
/* 2868 */         throw new IllegalArgumentException("SACL is invalid"); 
/* 2869 */       infoType |= 0x8;
/*      */     } 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 2877 */     if (setDACLProtectedStatus) {
/* 2878 */       if ((securityDescriptor.Control & 0x1000) != 0) {
/* 2879 */         infoType |= Integer.MIN_VALUE;
/*      */       }
/* 2881 */       else if ((securityDescriptor.Control & 0x1000) == 0) {
/* 2882 */         infoType |= 0x20000000;
/*      */       } 
/*      */     }
/*      */     
/* 2886 */     if (setSACLProtectedStatus) {
/* 2887 */       if ((securityDescriptor.Control & 0x2000) != 0) {
/* 2888 */         infoType |= 0x40000000;
/* 2889 */       } else if ((securityDescriptor.Control & 0x2000) == 0) {
/* 2890 */         infoType |= 0x10000000;
/*      */       } 
/*      */     }
/*      */     
/* 2894 */     int lastError = Advapi32.INSTANCE.SetNamedSecurityInfo(absoluteObjectPath, objectType, infoType, setOwner ? psidOwner
/*      */ 
/*      */ 
/*      */         
/* 2898 */         .getPointer() : null, setGroup ? psidGroup
/* 2899 */         .getPointer() : null, setDACL ? dacl
/* 2900 */         .getPointer() : null, setSACL ? sacl
/* 2901 */         .getPointer() : null);
/*      */     
/* 2903 */     if (lastError != 0) {
/* 2904 */       throw new Win32Exception(lastError);
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean accessCheck(File file, AccessCheckPermission permissionToCheck) {
/* 2915 */     Memory securityDescriptorMemoryPointer = getSecurityDescriptorForFile(file.getAbsolutePath().replace('/', '\\'));
/*      */     
/* 2917 */     WinNT.HANDLEByReference openedAccessToken = new WinNT.HANDLEByReference();
/* 2918 */     WinNT.HANDLEByReference duplicatedToken = new WinNT.HANDLEByReference();
/* 2919 */     Win32Exception err = null;
/*      */     try {
/* 2921 */       int desireAccess = 131086;
/* 2922 */       WinNT.HANDLE hProcess = Kernel32.INSTANCE.GetCurrentProcess();
/* 2923 */       if (!Advapi32.INSTANCE.OpenProcessToken(hProcess, desireAccess, openedAccessToken)) {
/* 2924 */         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*      */       }
/*      */       
/* 2927 */       if (!Advapi32.INSTANCE.DuplicateToken(openedAccessToken.getValue(), 2, duplicatedToken)) {
/* 2928 */         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*      */       }
/*      */       
/* 2931 */       WinNT.GENERIC_MAPPING mapping = new WinNT.GENERIC_MAPPING();
/* 2932 */       mapping.genericRead = new WinDef.DWORD(1179785L);
/* 2933 */       mapping.genericWrite = new WinDef.DWORD(1179926L);
/* 2934 */       mapping.genericExecute = new WinDef.DWORD(1179808L);
/* 2935 */       mapping.genericAll = new WinDef.DWORD(2032127L);
/*      */       
/* 2937 */       WinDef.DWORDByReference rights = new WinDef.DWORDByReference(new WinDef.DWORD(permissionToCheck.getCode()));
/* 2938 */       Advapi32.INSTANCE.MapGenericMask(rights, mapping);
/*      */       
/* 2940 */       WinNT.PRIVILEGE_SET privileges = new WinNT.PRIVILEGE_SET(1);
/* 2941 */       privileges.PrivilegeCount = new WinDef.DWORD(0L);
/* 2942 */       WinDef.DWORDByReference privilegeLength = new WinDef.DWORDByReference(new WinDef.DWORD(privileges.size()));
/*      */       
/* 2944 */       WinDef.DWORDByReference grantedAccess = new WinDef.DWORDByReference();
/* 2945 */       WinDef.BOOLByReference result = new WinDef.BOOLByReference();
/* 2946 */       if (!Advapi32.INSTANCE.AccessCheck((Pointer)securityDescriptorMemoryPointer, duplicatedToken
/* 2947 */           .getValue(), rights
/* 2948 */           .getValue(), mapping, privileges, privilegeLength, grantedAccess, result))
/*      */       {
/*      */         
/* 2951 */         throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*      */       }
/*      */       
/* 2954 */       return result.getValue().booleanValue();
/* 2955 */     } catch (Win32Exception e) {
/* 2956 */       err = e;
/* 2957 */       throw err;
/*      */     } finally {
/*      */       try {
/* 2960 */         Kernel32Util.closeHandleRefs(new WinNT.HANDLEByReference[] { openedAccessToken, duplicatedToken });
/* 2961 */       } catch (Win32Exception e) {
/* 2962 */         if (err == null) {
/* 2963 */           err = e;
/*      */         } else {
/* 2965 */           err.addSuppressedReflected((Throwable)e);
/*      */         } 
/*      */       } 
/*      */       
/* 2969 */       if (securityDescriptorMemoryPointer != null) {
/* 2970 */         securityDescriptorMemoryPointer.clear();
/*      */       }
/*      */       
/* 2973 */       if (err != null) {
/* 2974 */         throw err;
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static WinNT.SECURITY_DESCRIPTOR_RELATIVE getFileSecurityDescriptor(File file, boolean getSACL) {
/* 2991 */     Memory securityDesc = getSecurityDescriptorForObject(file.getAbsolutePath().replaceAll("/", "\\"), 1, getSACL);
/* 2992 */     WinNT.SECURITY_DESCRIPTOR_RELATIVE sdr = new WinNT.SECURITY_DESCRIPTOR_RELATIVE((Pointer)securityDesc);
/* 2993 */     return sdr;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void setFileSecurityDescriptor(File file, WinNT.SECURITY_DESCRIPTOR_RELATIVE securityDescriptor, boolean setOwner, boolean setGroup, boolean setDACL, boolean setSACL, boolean setDACLProtectedStatus, boolean setSACLProtectedStatus) {
/* 3025 */     setSecurityDescriptorForObject(file.getAbsolutePath().replaceAll("/", "\\"), 1, securityDescriptor, setOwner, setGroup, setDACL, setSACL, setDACLProtectedStatus, setSACLProtectedStatus);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void encryptFile(File file) {
/* 3035 */     String lpFileName = file.getAbsolutePath();
/* 3036 */     if (!Advapi32.INSTANCE.EncryptFile(lpFileName)) {
/* 3037 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void decryptFile(File file) {
/* 3048 */     String lpFileName = file.getAbsolutePath();
/* 3049 */     if (!Advapi32.INSTANCE.DecryptFile(lpFileName, new WinDef.DWORD(0L))) {
/* 3050 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static int fileEncryptionStatus(File file) {
/* 3062 */     WinDef.DWORDByReference status = new WinDef.DWORDByReference();
/* 3063 */     String lpFileName = file.getAbsolutePath();
/* 3064 */     if (!Advapi32.INSTANCE.FileEncryptionStatus(lpFileName, status)) {
/* 3065 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*      */     }
/* 3067 */     return status.getValue().intValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void disableEncryption(File directory, boolean disable) {
/* 3080 */     String dirPath = directory.getAbsolutePath();
/* 3081 */     if (!Advapi32.INSTANCE.EncryptionDisable(dirPath, disable)) {
/* 3082 */       throw new Win32Exception(Native.getLastError());
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void backupEncryptedFile(File src, File destDir) {
/* 3100 */     if (!destDir.isDirectory()) {
/* 3101 */       throw new IllegalArgumentException("destDir must be a directory.");
/*      */     }
/*      */     
/* 3104 */     WinDef.ULONG readFlag = new WinDef.ULONG(0L);
/* 3105 */     WinDef.ULONG writeFlag = new WinDef.ULONG(1L);
/*      */     
/* 3107 */     if (src.isDirectory()) {
/* 3108 */       writeFlag.setValue(3L);
/*      */     }
/*      */ 
/*      */     
/* 3112 */     String srcFileName = src.getAbsolutePath();
/* 3113 */     PointerByReference pvContext = new PointerByReference();
/* 3114 */     if (Advapi32.INSTANCE.OpenEncryptedFileRaw(srcFileName, readFlag, pvContext) != 0)
/*      */     {
/* 3116 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*      */     }
/*      */ 
/*      */     
/* 3120 */     final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
/* 3121 */     WinBase.FE_EXPORT_FUNC pfExportCallback = new WinBase.FE_EXPORT_FUNC()
/*      */       {
/*      */         public WinDef.DWORD callback(Pointer pbData, Pointer pvCallbackContext, WinDef.ULONG ulLength)
/*      */         {
/* 3125 */           byte[] arr = pbData.getByteArray(0L, ulLength.intValue());
/*      */           try {
/* 3127 */             outputStream.write(arr);
/* 3128 */           } catch (IOException e) {
/* 3129 */             throw new RuntimeException(e);
/*      */           } 
/* 3131 */           return new WinDef.DWORD(0L);
/*      */         }
/*      */       };
/*      */     
/* 3135 */     if (Advapi32.INSTANCE.ReadEncryptedFileRaw(pfExportCallback, null, pvContext
/* 3136 */         .getValue()) != 0) {
/* 3137 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*      */     }
/*      */ 
/*      */     
/*      */     try {
/* 3142 */       outputStream.close();
/* 3143 */     } catch (IOException e) {
/* 3144 */       throw new RuntimeException(e);
/*      */     } 
/* 3146 */     Advapi32.INSTANCE.CloseEncryptedFileRaw(pvContext.getValue());
/*      */ 
/*      */ 
/*      */     
/* 3150 */     String destFileName = destDir.getAbsolutePath() + File.separator + src.getName();
/* 3151 */     pvContext = new PointerByReference();
/* 3152 */     if (Advapi32.INSTANCE.OpenEncryptedFileRaw(destFileName, writeFlag, pvContext) != 0)
/*      */     {
/* 3154 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*      */     }
/*      */ 
/*      */     
/* 3158 */     final IntByReference elementsReadWrapper = new IntByReference(0);
/* 3159 */     WinBase.FE_IMPORT_FUNC pfImportCallback = new WinBase.FE_IMPORT_FUNC()
/*      */       {
/*      */         public WinDef.DWORD callback(Pointer pbData, Pointer pvCallbackContext, WinDef.ULONGByReference ulLength)
/*      */         {
/* 3163 */           int elementsRead = elementsReadWrapper.getValue();
/* 3164 */           int remainingElements = outputStream.size() - elementsRead;
/* 3165 */           int length = Math.min(remainingElements, ulLength.getValue().intValue());
/* 3166 */           pbData.write(0L, outputStream.toByteArray(), elementsRead, length);
/*      */           
/* 3168 */           elementsReadWrapper.setValue(elementsRead + length);
/* 3169 */           ulLength.setValue(new WinDef.ULONG(length));
/* 3170 */           return new WinDef.DWORD(0L);
/*      */         }
/*      */       };
/*      */     
/* 3174 */     if (Advapi32.INSTANCE.WriteEncryptedFileRaw(pfImportCallback, null, pvContext
/* 3175 */         .getValue()) != 0) {
/* 3176 */       throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*      */     }
/*      */ 
/*      */     
/* 3180 */     Advapi32.INSTANCE.CloseEncryptedFileRaw(pvContext.getValue());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static class Privilege
/*      */     implements Closeable
/*      */   {
/*      */     private boolean currentlyImpersonating = false;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private boolean privilegesEnabled = false;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private final WinNT.LUID[] pLuids;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Privilege(String... privileges) throws IllegalArgumentException, Win32Exception {
/* 3208 */       this.pLuids = new WinNT.LUID[privileges.length];
/* 3209 */       int i = 0;
/* 3210 */       for (String p : privileges) {
/* 3211 */         this.pLuids[i] = new WinNT.LUID();
/* 3212 */         if (!Advapi32.INSTANCE.LookupPrivilegeValue(null, p, this.pLuids[i])) {
/* 3213 */           throw new IllegalArgumentException("Failed to find privilege \"" + privileges[i] + "\" - " + Kernel32.INSTANCE.GetLastError());
/*      */         }
/* 3215 */         i++;
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void close() {
/* 3225 */       disable();
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public Privilege enable() throws Win32Exception {
/* 3237 */       if (this.privilegesEnabled) {
/* 3238 */         return this;
/*      */       }
/*      */       
/* 3241 */       WinNT.HANDLEByReference phThreadToken = new WinNT.HANDLEByReference();
/*      */       
/*      */       try {
/* 3244 */         phThreadToken.setValue(getThreadToken());
/* 3245 */         WinNT.TOKEN_PRIVILEGES tp = new WinNT.TOKEN_PRIVILEGES(this.pLuids.length);
/* 3246 */         for (int i = 0; i < this.pLuids.length; i++) {
/* 3247 */           tp.Privileges[i] = new WinNT.LUID_AND_ATTRIBUTES(this.pLuids[i], new WinDef.DWORD(2L));
/*      */         }
/* 3249 */         if (!Advapi32.INSTANCE.AdjustTokenPrivileges(phThreadToken.getValue(), false, tp, 0, null, null)) {
/* 3250 */           throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*      */         }
/* 3252 */         this.privilegesEnabled = true;
/*      */       }
/* 3254 */       catch (Win32Exception ex) {
/*      */         
/* 3256 */         if (this.currentlyImpersonating) {
/* 3257 */           Advapi32.INSTANCE.SetThreadToken(null, null);
/* 3258 */           this.currentlyImpersonating = false;
/*      */         
/*      */         }
/* 3261 */         else if (this.privilegesEnabled) {
/* 3262 */           WinNT.TOKEN_PRIVILEGES tp = new WinNT.TOKEN_PRIVILEGES(this.pLuids.length);
/* 3263 */           for (int i = 0; i < this.pLuids.length; i++) {
/* 3264 */             tp.Privileges[i] = new WinNT.LUID_AND_ATTRIBUTES(this.pLuids[i], new WinDef.DWORD(0L));
/*      */           }
/* 3266 */           Advapi32.INSTANCE.AdjustTokenPrivileges(phThreadToken.getValue(), false, tp, 0, null, null);
/* 3267 */           this.privilegesEnabled = false;
/*      */         } 
/*      */         
/* 3270 */         throw ex;
/*      */       }
/*      */       finally {
/*      */         
/* 3274 */         if (phThreadToken.getValue() != WinBase.INVALID_HANDLE_VALUE && phThreadToken
/* 3275 */           .getValue() != null) {
/* 3276 */           Kernel32.INSTANCE.CloseHandle(phThreadToken.getValue());
/* 3277 */           phThreadToken.setValue(null);
/*      */         } 
/*      */       } 
/* 3280 */       return this;
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     public void disable() throws Win32Exception {
/* 3289 */       WinNT.HANDLEByReference phThreadToken = new WinNT.HANDLEByReference();
/*      */       
/*      */       try {
/* 3292 */         phThreadToken.setValue(getThreadToken());
/* 3293 */         if (this.currentlyImpersonating) {
/* 3294 */           Advapi32.INSTANCE.SetThreadToken(null, null);
/*      */ 
/*      */         
/*      */         }
/* 3298 */         else if (this.privilegesEnabled) {
/* 3299 */           WinNT.TOKEN_PRIVILEGES tp = new WinNT.TOKEN_PRIVILEGES(this.pLuids.length);
/* 3300 */           for (int i = 0; i < this.pLuids.length; i++) {
/* 3301 */             tp.Privileges[i] = new WinNT.LUID_AND_ATTRIBUTES(this.pLuids[i], new WinDef.DWORD(0L));
/*      */           }
/* 3303 */           Advapi32.INSTANCE.AdjustTokenPrivileges(phThreadToken.getValue(), false, tp, 0, null, null);
/* 3304 */           this.privilegesEnabled = false;
/*      */         }
/*      */       
/*      */       }
/*      */       finally {
/*      */         
/* 3310 */         if (phThreadToken.getValue() != WinBase.INVALID_HANDLE_VALUE && phThreadToken
/* 3311 */           .getValue() != null) {
/* 3312 */           Kernel32.INSTANCE.CloseHandle(phThreadToken.getValue());
/* 3313 */           phThreadToken.setValue(null);
/*      */         } 
/*      */       } 
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     private WinNT.HANDLE getThreadToken() throws Win32Exception {
/* 3326 */       WinNT.HANDLEByReference phThreadToken = new WinNT.HANDLEByReference();
/* 3327 */       WinNT.HANDLEByReference phProcessToken = new WinNT.HANDLEByReference();
/*      */ 
/*      */       
/*      */       try {
/* 3331 */         if (!Advapi32.INSTANCE.OpenThreadToken(Kernel32.INSTANCE.GetCurrentThread(), 32, false, phThreadToken))
/*      */         {
/*      */ 
/*      */ 
/*      */           
/* 3336 */           int lastError = Kernel32.INSTANCE.GetLastError();
/* 3337 */           if (1008 != lastError) {
/* 3338 */             throw new Win32Exception(lastError);
/*      */           }
/*      */ 
/*      */           
/* 3342 */           if (!Advapi32.INSTANCE.OpenProcessToken(Kernel32.INSTANCE.GetCurrentProcess(), 2, phProcessToken)) {
/* 3343 */             throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*      */           }
/*      */ 
/*      */           
/* 3347 */           if (!Advapi32.INSTANCE.DuplicateTokenEx(phProcessToken.getValue(), 36, null, 2, 2, phThreadToken))
/*      */           {
/*      */ 
/*      */ 
/*      */ 
/*      */             
/* 3353 */             throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*      */           }
/*      */ 
/*      */           
/* 3357 */           if (!Advapi32.INSTANCE.SetThreadToken(null, phThreadToken.getValue())) {
/* 3358 */             throw new Win32Exception(Kernel32.INSTANCE.GetLastError());
/*      */           }
/* 3360 */           this.currentlyImpersonating = true;
/*      */         }
/*      */       
/* 3363 */       } catch (Win32Exception ex) {
/*      */         
/* 3365 */         if (phThreadToken.getValue() != WinBase.INVALID_HANDLE_VALUE && phThreadToken
/* 3366 */           .getValue() != null) {
/* 3367 */           Kernel32.INSTANCE.CloseHandle(phThreadToken.getValue());
/* 3368 */           phThreadToken.setValue(null);
/*      */         } 
/* 3370 */         throw ex;
/*      */       
/*      */       }
/*      */       finally {
/*      */         
/* 3375 */         if (phProcessToken.getValue() != WinBase.INVALID_HANDLE_VALUE && phProcessToken
/* 3376 */           .getValue() != null) {
/* 3377 */           Kernel32.INSTANCE.CloseHandle(phProcessToken.getValue());
/* 3378 */           phProcessToken.setValue(null);
/*      */         } 
/*      */       } 
/*      */       
/* 3382 */       return phThreadToken.getValue();
/*      */     }
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\Advapi32Util.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */