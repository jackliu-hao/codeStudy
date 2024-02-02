/*     */ package com.sun.jna.platform.win32;
/*     */ 
/*     */ import com.sun.jna.Native;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import java.util.ArrayList;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.List;
/*     */ import java.util.Map;
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
/*     */ public class WininetUtil
/*     */ {
/*     */   public static Map<String, String> getCache() {
/*  50 */     List<Wininet.INTERNET_CACHE_ENTRY_INFO> items = new ArrayList<Wininet.INTERNET_CACHE_ENTRY_INFO>();
/*     */     
/*  52 */     WinNT.HANDLE cacheHandle = null;
/*  53 */     Win32Exception we = null;
/*  54 */     int lastError = 0;
/*     */ 
/*     */     
/*  57 */     Map<String, String> cacheItems = new LinkedHashMap<String, String>();
/*     */     
/*     */     try {
/*  60 */       IntByReference size = new IntByReference();
/*     */ 
/*     */ 
/*     */ 
/*     */       
/*  65 */       cacheHandle = Wininet.INSTANCE.FindFirstUrlCacheEntry(null, null, size);
/*  66 */       lastError = Native.getLastError();
/*     */ 
/*     */       
/*  69 */       if (lastError == 259)
/*  70 */         return cacheItems; 
/*  71 */       if (lastError != 0 && lastError != 122) {
/*  72 */         throw new Win32Exception(lastError);
/*     */       }
/*     */       
/*  75 */       Wininet.INTERNET_CACHE_ENTRY_INFO entry = new Wininet.INTERNET_CACHE_ENTRY_INFO(size.getValue());
/*  76 */       cacheHandle = Wininet.INSTANCE.FindFirstUrlCacheEntry(null, entry, size);
/*     */       
/*  78 */       if (cacheHandle == null) {
/*  79 */         throw new Win32Exception(Native.getLastError());
/*     */       }
/*     */       
/*  82 */       items.add(entry);
/*     */       
/*     */       while (true) {
/*  85 */         size = new IntByReference();
/*     */ 
/*     */ 
/*     */ 
/*     */         
/*  90 */         boolean result = Wininet.INSTANCE.FindNextUrlCacheEntry(cacheHandle, null, size);
/*     */         
/*  92 */         if (!result) {
/*  93 */           lastError = Native.getLastError();
/*  94 */           if (lastError == 259)
/*     */             break; 
/*  96 */           if (lastError != 0 && lastError != 122) {
/*  97 */             throw new Win32Exception(lastError);
/*     */           }
/*     */         } 
/*     */         
/* 101 */         entry = new Wininet.INTERNET_CACHE_ENTRY_INFO(size.getValue());
/* 102 */         result = Wininet.INSTANCE.FindNextUrlCacheEntry(cacheHandle, entry, size);
/*     */         
/* 104 */         if (!result) {
/* 105 */           lastError = Native.getLastError();
/* 106 */           if (lastError == 259)
/*     */             break; 
/* 108 */           if (lastError != 0 && lastError != 122) {
/* 109 */             throw new Win32Exception(lastError);
/*     */           }
/*     */         } 
/* 112 */         items.add(entry);
/*     */       } 
/*     */       
/* 115 */       for (Wininet.INTERNET_CACHE_ENTRY_INFO item : items) {
/* 116 */         cacheItems.put(item.lpszSourceUrlName.getWideString(0L), (item.lpszLocalFileName == null) ? "" : item.lpszLocalFileName.getWideString(0L));
/*     */       }
/*     */     }
/* 119 */     catch (Win32Exception e) {
/* 120 */       we = e;
/*     */     } finally {
/* 122 */       if (cacheHandle != null && 
/* 123 */         !Wininet.INSTANCE.FindCloseUrlCache(cacheHandle) && 
/* 124 */         we != null) {
/* 125 */         Win32Exception e = new Win32Exception(Native.getLastError());
/* 126 */         e.addSuppressedReflected((Throwable)we);
/* 127 */         we = e;
/*     */       } 
/*     */     } 
/*     */ 
/*     */     
/* 132 */     if (we != null) {
/* 133 */       throw we;
/*     */     }
/* 135 */     return cacheItems;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\WininetUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */