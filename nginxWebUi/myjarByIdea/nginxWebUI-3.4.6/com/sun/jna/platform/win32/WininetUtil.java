package com.sun.jna.platform.win32;

import com.sun.jna.Native;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class WininetUtil {
   public static Map<String, String> getCache() {
      List<Wininet.INTERNET_CACHE_ENTRY_INFO> items = new ArrayList();
      WinNT.HANDLE cacheHandle = null;
      Win32Exception we = null;
      int lastError = false;
      LinkedHashMap cacheItems = new LinkedHashMap();
      boolean var13 = false;

      LinkedHashMap var18;
      label239: {
         label234: {
            Win32Exception e;
            label233: {
               try {
                  var13 = true;
                  IntByReference size = new IntByReference();
                  cacheHandle = Wininet.INSTANCE.FindFirstUrlCacheEntry((String)null, (Wininet.INTERNET_CACHE_ENTRY_INFO)null, size);
                  int lastError = Native.getLastError();
                  if (lastError == 259) {
                     var18 = cacheItems;
                     var13 = false;
                     break label239;
                  }

                  if (lastError != 0 && lastError != 122) {
                     throw new Win32Exception(lastError);
                  }

                  Wininet.INTERNET_CACHE_ENTRY_INFO entry = new Wininet.INTERNET_CACHE_ENTRY_INFO(size.getValue());
                  cacheHandle = Wininet.INSTANCE.FindFirstUrlCacheEntry((String)null, entry, size);
                  if (cacheHandle == null) {
                     throw new Win32Exception(Native.getLastError());
                  }

                  items.add(entry);

                  while(true) {
                     size = new IntByReference();
                     boolean result = Wininet.INSTANCE.FindNextUrlCacheEntry(cacheHandle, (Wininet.INTERNET_CACHE_ENTRY_INFO)null, size);
                     if (!result) {
                        lastError = Native.getLastError();
                        if (lastError == 259) {
                           break;
                        }

                        if (lastError != 0 && lastError != 122) {
                           throw new Win32Exception(lastError);
                        }
                     }

                     entry = new Wininet.INTERNET_CACHE_ENTRY_INFO(size.getValue());
                     result = Wininet.INSTANCE.FindNextUrlCacheEntry(cacheHandle, entry, size);
                     if (!result) {
                        lastError = Native.getLastError();
                        if (lastError == 259) {
                           break;
                        }

                        if (lastError != 0 && lastError != 122) {
                           throw new Win32Exception(lastError);
                        }
                     }

                     items.add(entry);
                  }

                  Iterator var19 = items.iterator();

                  while(var19.hasNext()) {
                     Wininet.INTERNET_CACHE_ENTRY_INFO item = (Wininet.INTERNET_CACHE_ENTRY_INFO)var19.next();
                     cacheItems.put(item.lpszSourceUrlName.getWideString(0L), item.lpszLocalFileName == null ? "" : item.lpszLocalFileName.getWideString(0L));
                  }

                  var13 = false;
                  break label233;
               } catch (Win32Exception var14) {
                  we = var14;
                  var13 = false;
               } finally {
                  if (var13) {
                     if (cacheHandle != null && !Wininet.INSTANCE.FindCloseUrlCache(cacheHandle) && we != null) {
                        Win32Exception e = new Win32Exception(Native.getLastError());
                        e.addSuppressedReflected(we);
                     }

                  }
               }

               if (cacheHandle != null && !Wininet.INSTANCE.FindCloseUrlCache(cacheHandle) && we != null) {
                  e = new Win32Exception(Native.getLastError());
                  e.addSuppressedReflected(we);
                  we = e;
               }
               break label234;
            }

            if (cacheHandle != null && !Wininet.INSTANCE.FindCloseUrlCache(cacheHandle) && we != null) {
               e = new Win32Exception(Native.getLastError());
               e.addSuppressedReflected(we);
               we = e;
            }
         }

         if (we != null) {
            throw we;
         }

         return cacheItems;
      }

      if (cacheHandle != null && !Wininet.INSTANCE.FindCloseUrlCache(cacheHandle) && we != null) {
         Win32Exception e = new Win32Exception(Native.getLastError());
         e.addSuppressedReflected(we);
      }

      return var18;
   }
}
