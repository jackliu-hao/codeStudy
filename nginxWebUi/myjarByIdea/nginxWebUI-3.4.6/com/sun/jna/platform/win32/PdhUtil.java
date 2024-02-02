package com.sun.jna.platform.win32;

import com.sun.jna.Memory;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import java.util.ArrayList;
import java.util.List;

public abstract class PdhUtil {
   private static final int CHAR_TO_BYTES;
   private static final String ENGLISH_COUNTER_KEY = "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Perflib\\009";
   private static final String ENGLISH_COUNTER_VALUE = "Counter";

   public static String PdhLookupPerfNameByIndex(String szMachineName, int dwNameIndex) {
      WinDef.DWORDByReference pcchNameBufferSize = new WinDef.DWORDByReference(new WinDef.DWORD(0L));
      int result = Pdh.INSTANCE.PdhLookupPerfNameByIndex(szMachineName, dwNameIndex, (Pointer)null, pcchNameBufferSize);
      Memory mem = null;
      if (result != -1073738819) {
         if (result != 0 && result != -2147481646) {
            throw new PdhException(result);
         }

         if (pcchNameBufferSize.getValue().intValue() < 1) {
            return "";
         }

         mem = new Memory((long)(pcchNameBufferSize.getValue().intValue() * CHAR_TO_BYTES));
         result = Pdh.INSTANCE.PdhLookupPerfNameByIndex(szMachineName, dwNameIndex, mem, pcchNameBufferSize);
      } else {
         for(int bufferSize = 32; bufferSize <= 1024; bufferSize *= 2) {
            pcchNameBufferSize = new WinDef.DWORDByReference(new WinDef.DWORD((long)bufferSize));
            mem = new Memory((long)(bufferSize * CHAR_TO_BYTES));
            result = Pdh.INSTANCE.PdhLookupPerfNameByIndex(szMachineName, dwNameIndex, mem, pcchNameBufferSize);
            if (result != -1073738819 && result != -1073738814) {
               break;
            }
         }
      }

      if (result != 0) {
         throw new PdhException(result);
      } else {
         return CHAR_TO_BYTES == 1 ? mem.getString(0L) : mem.getWideString(0L);
      }
   }

   public static int PdhLookupPerfIndexByEnglishName(String szNameBuffer) {
      String[] counters = Advapi32Util.registryGetStringArray(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Perflib\\009", "Counter");

      for(int i = 1; i < counters.length; i += 2) {
         if (counters[i].equals(szNameBuffer)) {
            try {
               return Integer.parseInt(counters[i - 1]);
            } catch (NumberFormatException var4) {
               return 0;
            }
         }
      }

      return 0;
   }

   public static PdhEnumObjectItems PdhEnumObjectItems(String szDataSource, String szMachineName, String szObjectName, int dwDetailLevel) {
      List<String> counters = new ArrayList();
      List<String> instances = new ArrayList();
      WinDef.DWORDByReference pcchCounterListLength = new WinDef.DWORDByReference(new WinDef.DWORD(0L));
      WinDef.DWORDByReference pcchInstanceListLength = new WinDef.DWORDByReference(new WinDef.DWORD(0L));
      int result = Pdh.INSTANCE.PdhEnumObjectItems(szDataSource, szMachineName, szObjectName, (Pointer)null, pcchCounterListLength, (Pointer)null, pcchInstanceListLength, dwDetailLevel, 0);
      if (result != 0 && result != -2147481646) {
         throw new PdhException(result);
      } else {
         Memory mszCounterList = null;
         Memory mszInstanceList = null;
         if (pcchCounterListLength.getValue().intValue() > 0) {
            mszCounterList = new Memory((long)(pcchCounterListLength.getValue().intValue() * CHAR_TO_BYTES));
         }

         if (pcchInstanceListLength.getValue().intValue() > 0) {
            mszInstanceList = new Memory((long)(pcchInstanceListLength.getValue().intValue() * CHAR_TO_BYTES));
         }

         result = Pdh.INSTANCE.PdhEnumObjectItems(szDataSource, szMachineName, szObjectName, mszCounterList, pcchCounterListLength, mszInstanceList, pcchInstanceListLength, dwDetailLevel, 0);
         if (result != 0) {
            throw new PdhException(result);
         } else {
            int offset;
            String s;
            if (mszCounterList != null) {
               for(offset = 0; (long)offset < mszCounterList.size(); offset += (s.length() + 1) * CHAR_TO_BYTES) {
                  s = null;
                  if (CHAR_TO_BYTES == 1) {
                     s = mszCounterList.getString((long)offset);
                  } else {
                     s = mszCounterList.getWideString((long)offset);
                  }

                  if (s.isEmpty()) {
                     break;
                  }

                  counters.add(s);
               }
            }

            if (mszInstanceList != null) {
               for(offset = 0; (long)offset < mszInstanceList.size(); offset += (s.length() + 1) * CHAR_TO_BYTES) {
                  s = null;
                  if (CHAR_TO_BYTES == 1) {
                     s = mszInstanceList.getString((long)offset);
                  } else {
                     s = mszInstanceList.getWideString((long)offset);
                  }

                  if (s.isEmpty()) {
                     break;
                  }

                  instances.add(s);
               }
            }

            return new PdhEnumObjectItems(counters, instances);
         }
      }
   }

   static {
      CHAR_TO_BYTES = Boolean.getBoolean("w32.ascii") ? 1 : Native.WCHAR_SIZE;
   }

   public static final class PdhException extends RuntimeException {
      private final int errorCode;

      public PdhException(int errorCode) {
         super(String.format("Pdh call failed with error code 0x%08X", errorCode));
         this.errorCode = errorCode;
      }

      public int getErrorCode() {
         return this.errorCode;
      }
   }

   public static class PdhEnumObjectItems {
      private final List<String> counters;
      private final List<String> instances;

      public PdhEnumObjectItems(List<String> counters, List<String> instances) {
         this.counters = this.copyAndEmptyListForNullList(counters);
         this.instances = this.copyAndEmptyListForNullList(instances);
      }

      public List<String> getCounters() {
         return this.counters;
      }

      public List<String> getInstances() {
         return this.instances;
      }

      private List<String> copyAndEmptyListForNullList(List<String> inputList) {
         return inputList == null ? new ArrayList() : new ArrayList(inputList);
      }

      public String toString() {
         return "PdhEnumObjectItems{counters=" + this.counters + ", instances=" + this.instances + '}';
      }
   }
}
