/*     */ package com.sun.jna.platform.mac;
/*     */ 
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import com.sun.jna.ptr.PointerByReference;
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
/*     */ public class IOKitUtil
/*     */ {
/*  39 */   private static final IOKit IO = IOKit.INSTANCE;
/*  40 */   private static final SystemB SYS = SystemB.INSTANCE;
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
/*     */   public static int getMasterPort() {
/*  57 */     IntByReference port = new IntByReference();
/*  58 */     IO.IOMasterPort(0, port);
/*  59 */     return port.getValue();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static IOKit.IORegistryEntry getRoot() {
/*  69 */     int masterPort = getMasterPort();
/*  70 */     IOKit.IORegistryEntry root = IO.IORegistryGetRootEntry(masterPort);
/*  71 */     SYS.mach_port_deallocate(SYS.mach_task_self(), masterPort);
/*  72 */     return root;
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
/*     */   public static IOKit.IOService getMatchingService(String serviceName) {
/*  85 */     CoreFoundation.CFMutableDictionaryRef dict = IO.IOServiceMatching(serviceName);
/*  86 */     if (dict != null) {
/*  87 */       return getMatchingService(dict);
/*     */     }
/*  89 */     return null;
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
/*     */   public static IOKit.IOService getMatchingService(CoreFoundation.CFDictionaryRef matchingDictionary) {
/* 103 */     int masterPort = getMasterPort();
/* 104 */     IOKit.IOService service = IO.IOServiceGetMatchingService(masterPort, matchingDictionary);
/* 105 */     SYS.mach_port_deallocate(SYS.mach_task_self(), masterPort);
/* 106 */     return service;
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
/*     */   public static IOKit.IOIterator getMatchingServices(String serviceName) {
/* 119 */     CoreFoundation.CFMutableDictionaryRef dict = IO.IOServiceMatching(serviceName);
/* 120 */     if (dict != null) {
/* 121 */       return getMatchingServices(dict);
/*     */     }
/* 123 */     return null;
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
/*     */   public static IOKit.IOIterator getMatchingServices(CoreFoundation.CFDictionaryRef matchingDictionary) {
/* 137 */     int masterPort = getMasterPort();
/* 138 */     PointerByReference serviceIterator = new PointerByReference();
/* 139 */     int result = IO.IOServiceGetMatchingServices(masterPort, matchingDictionary, serviceIterator);
/* 140 */     SYS.mach_port_deallocate(SYS.mach_task_self(), masterPort);
/* 141 */     if (result == 0 && serviceIterator.getValue() != null) {
/* 142 */       return new IOKit.IOIterator(serviceIterator.getValue());
/*     */     }
/* 144 */     return null;
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
/*     */   public static CoreFoundation.CFMutableDictionaryRef getBSDNameMatchingDict(String bsdName) {
/* 156 */     int masterPort = getMasterPort();
/* 157 */     CoreFoundation.CFMutableDictionaryRef result = IO.IOBSDNameMatching(masterPort, 0, bsdName);
/* 158 */     SYS.mach_port_deallocate(SYS.mach_task_self(), masterPort);
/* 159 */     return result;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\mac\IOKitUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */