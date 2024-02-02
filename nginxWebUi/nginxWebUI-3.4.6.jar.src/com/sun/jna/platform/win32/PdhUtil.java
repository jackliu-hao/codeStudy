/*     */ package com.sun.jna.platform.win32;
/*     */ 
/*     */ import com.sun.jna.Memory;
/*     */ import com.sun.jna.Native;
/*     */ import com.sun.jna.Pointer;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
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
/*     */ public abstract class PdhUtil
/*     */ {
/*  40 */   private static final int CHAR_TO_BYTES = Boolean.getBoolean("w32.ascii") ? 1 : Native.WCHAR_SIZE;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String ENGLISH_COUNTER_KEY = "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Perflib\\009";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String ENGLISH_COUNTER_VALUE = "Counter";
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String PdhLookupPerfNameByIndex(String szMachineName, int dwNameIndex) {
/*  63 */     WinDef.DWORDByReference pcchNameBufferSize = new WinDef.DWORDByReference(new WinDef.DWORD(0L));
/*  64 */     int result = Pdh.INSTANCE.PdhLookupPerfNameByIndex(szMachineName, dwNameIndex, null, pcchNameBufferSize);
/*  65 */     Memory mem = null;
/*     */ 
/*     */     
/*  68 */     if (result != -1073738819) {
/*     */       
/*  70 */       if (result != 0 && result != -2147481646) {
/*  71 */         throw new PdhException(result);
/*     */       }
/*     */       
/*  74 */       if (pcchNameBufferSize.getValue().intValue() < 1) {
/*  75 */         return "";
/*     */       }
/*     */       
/*  78 */       mem = new Memory((pcchNameBufferSize.getValue().intValue() * CHAR_TO_BYTES));
/*  79 */       result = Pdh.INSTANCE.PdhLookupPerfNameByIndex(szMachineName, dwNameIndex, (Pointer)mem, pcchNameBufferSize);
/*     */     } else {
/*     */       int bufferSize;
/*  82 */       for (bufferSize = 32; bufferSize <= 1024; bufferSize *= 2) {
/*  83 */         pcchNameBufferSize = new WinDef.DWORDByReference(new WinDef.DWORD(bufferSize));
/*  84 */         mem = new Memory((bufferSize * CHAR_TO_BYTES));
/*  85 */         result = Pdh.INSTANCE.PdhLookupPerfNameByIndex(szMachineName, dwNameIndex, (Pointer)mem, pcchNameBufferSize);
/*  86 */         if (result != -1073738819 && result != -1073738814) {
/*     */           break;
/*     */         }
/*     */       } 
/*     */     } 
/*  91 */     if (result != 0) {
/*  92 */       throw new PdhException(result);
/*     */     }
/*     */ 
/*     */     
/*  96 */     if (CHAR_TO_BYTES == 1) {
/*  97 */       return mem.getString(0L);
/*     */     }
/*  99 */     return mem.getWideString(0L);
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
/*     */   public static int PdhLookupPerfIndexByEnglishName(String szNameBuffer) {
/* 115 */     String[] counters = Advapi32Util.registryGetStringArray(WinReg.HKEY_LOCAL_MACHINE, "SOFTWARE\\Microsoft\\Windows NT\\CurrentVersion\\Perflib\\009", "Counter");
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 121 */     for (int i = 1; i < counters.length; i += 2) {
/* 122 */       if (counters[i].equals(szNameBuffer)) {
/*     */         try {
/* 124 */           return Integer.parseInt(counters[i - 1]);
/* 125 */         } catch (NumberFormatException e) {
/*     */           
/* 127 */           return 0;
/*     */         } 
/*     */       }
/*     */     } 
/*     */     
/* 132 */     return 0;
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
/*     */   public static PdhEnumObjectItems PdhEnumObjectItems(String szDataSource, String szMachineName, String szObjectName, int dwDetailLevel) {
/* 162 */     List<String> counters = new ArrayList<String>();
/* 163 */     List<String> instances = new ArrayList<String>();
/*     */ 
/*     */     
/* 166 */     WinDef.DWORDByReference pcchCounterListLength = new WinDef.DWORDByReference(new WinDef.DWORD(0L));
/* 167 */     WinDef.DWORDByReference pcchInstanceListLength = new WinDef.DWORDByReference(new WinDef.DWORD(0L));
/* 168 */     int result = Pdh.INSTANCE.PdhEnumObjectItems(szDataSource, szMachineName, szObjectName, null, pcchCounterListLength, null, pcchInstanceListLength, dwDetailLevel, 0);
/*     */     
/* 170 */     if (result != 0 && result != -2147481646) {
/* 171 */       throw new PdhException(result);
/*     */     }
/*     */     
/* 174 */     Memory mszCounterList = null;
/* 175 */     Memory mszInstanceList = null;
/*     */     
/* 177 */     if (pcchCounterListLength.getValue().intValue() > 0) {
/* 178 */       mszCounterList = new Memory((pcchCounterListLength.getValue().intValue() * CHAR_TO_BYTES));
/*     */     }
/*     */     
/* 181 */     if (pcchInstanceListLength.getValue().intValue() > 0) {
/* 182 */       mszInstanceList = new Memory((pcchInstanceListLength.getValue().intValue() * CHAR_TO_BYTES));
/*     */     }
/*     */     
/* 185 */     result = Pdh.INSTANCE.PdhEnumObjectItems(szDataSource, szMachineName, szObjectName, (Pointer)mszCounterList, pcchCounterListLength, (Pointer)mszInstanceList, pcchInstanceListLength, dwDetailLevel, 0);
/*     */ 
/*     */     
/* 188 */     if (result != 0) {
/* 189 */       throw new PdhException(result);
/*     */     }
/*     */ 
/*     */     
/* 193 */     if (mszCounterList != null) {
/* 194 */       int offset = 0;
/* 195 */       while (offset < mszCounterList.size()) {
/* 196 */         String s = null;
/* 197 */         if (CHAR_TO_BYTES == 1) {
/* 198 */           s = mszCounterList.getString(offset);
/*     */         } else {
/* 200 */           s = mszCounterList.getWideString(offset);
/*     */         } 
/*     */         
/* 203 */         if (s.isEmpty()) {
/*     */           break;
/*     */         }
/* 206 */         counters.add(s);
/*     */         
/* 208 */         offset += (s.length() + 1) * CHAR_TO_BYTES;
/*     */       } 
/*     */     } 
/*     */     
/* 212 */     if (mszInstanceList != null) {
/* 213 */       int offset = 0;
/* 214 */       while (offset < mszInstanceList.size()) {
/* 215 */         String s = null;
/* 216 */         if (CHAR_TO_BYTES == 1) {
/* 217 */           s = mszInstanceList.getString(offset);
/*     */         } else {
/* 219 */           s = mszInstanceList.getWideString(offset);
/*     */         } 
/*     */         
/* 222 */         if (s.isEmpty()) {
/*     */           break;
/*     */         }
/* 225 */         instances.add(s);
/*     */         
/* 227 */         offset += (s.length() + 1) * CHAR_TO_BYTES;
/*     */       } 
/*     */     } 
/*     */     
/* 231 */     return new PdhEnumObjectItems(counters, instances);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class PdhEnumObjectItems
/*     */   {
/*     */     private final List<String> counters;
/*     */ 
/*     */     
/*     */     private final List<String> instances;
/*     */ 
/*     */     
/*     */     public PdhEnumObjectItems(List<String> counters, List<String> instances) {
/* 245 */       this.counters = copyAndEmptyListForNullList(counters);
/* 246 */       this.instances = copyAndEmptyListForNullList(instances);
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public List<String> getCounters() {
/* 254 */       return this.counters;
/*     */     }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/*     */     public List<String> getInstances() {
/* 262 */       return this.instances;
/*     */     }
/*     */     
/*     */     private List<String> copyAndEmptyListForNullList(List<String> inputList) {
/* 266 */       if (inputList == null) {
/* 267 */         return new ArrayList<String>();
/*     */       }
/* 269 */       return new ArrayList<String>(inputList);
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public String toString() {
/* 275 */       return "PdhEnumObjectItems{counters=" + this.counters + ", instances=" + this.instances + '}';
/*     */     }
/*     */   }
/*     */   
/*     */   public static final class PdhException extends RuntimeException {
/*     */     private final int errorCode;
/*     */     
/*     */     public PdhException(int errorCode) {
/* 283 */       super(String.format("Pdh call failed with error code 0x%08X", new Object[] { Integer.valueOf(errorCode) }));
/* 284 */       this.errorCode = errorCode;
/*     */     }
/*     */     
/*     */     public int getErrorCode() {
/* 288 */       return this.errorCode;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\win32\PdhUtil.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */