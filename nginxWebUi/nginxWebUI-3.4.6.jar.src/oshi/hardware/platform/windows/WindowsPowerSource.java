/*     */ package oshi.hardware.platform.windows;
/*     */ 
/*     */ import com.sun.jna.Memory;
/*     */ import com.sun.jna.Platform;
/*     */ import com.sun.jna.Pointer;
/*     */ import com.sun.jna.platform.win32.Guid;
/*     */ import com.sun.jna.platform.win32.Kernel32;
/*     */ import com.sun.jna.platform.win32.SetupApi;
/*     */ import com.sun.jna.platform.win32.WinBase;
/*     */ import com.sun.jna.platform.win32.WinNT;
/*     */ import com.sun.jna.ptr.IntByReference;
/*     */ import com.sun.jna.win32.W32APITypeMapper;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.time.LocalDate;
/*     */ import java.util.Arrays;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import oshi.annotation.concurrent.ThreadSafe;
/*     */ import oshi.hardware.PowerSource;
/*     */ import oshi.hardware.common.AbstractPowerSource;
/*     */ import oshi.jna.platform.windows.PowrProf;
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
/*     */ @ThreadSafe
/*     */ public final class WindowsPowerSource
/*     */   extends AbstractPowerSource
/*     */ {
/*  62 */   private static final Guid.GUID GUID_DEVCLASS_BATTERY = Guid.GUID.fromString("{72631E54-78A4-11D0-BCF7-00AA00B7B32A}");
/*  63 */   private static final int CHAR_WIDTH = (W32APITypeMapper.DEFAULT == W32APITypeMapper.UNICODE) ? 2 : 1;
/*  64 */   private static final boolean X64 = Platform.is64Bit();
/*     */   
/*     */   private static final int BATTERY_SYSTEM_BATTERY = -2147483648;
/*     */   
/*     */   private static final int BATTERY_IS_SHORT_TERM = 536870912;
/*     */   
/*     */   private static final int BATTERY_POWER_ON_LINE = 1;
/*     */   
/*     */   private static final int BATTERY_DISCHARGING = 2;
/*     */   
/*     */   private static final int BATTERY_CHARGING = 4;
/*     */   
/*     */   private static final int BATTERY_CAPACITY_RELATIVE = 1073741824;
/*     */   
/*     */   private static final int IOCTL_BATTERY_QUERY_TAG = 2703424;
/*     */   private static final int IOCTL_BATTERY_QUERY_STATUS = 2703436;
/*     */   private static final int IOCTL_BATTERY_QUERY_INFORMATION = 2703428;
/*     */   
/*     */   public WindowsPowerSource(String psName, String psDeviceName, double psRemainingCapacityPercent, double psTimeRemainingEstimated, double psTimeRemainingInstant, double psPowerUsageRate, double psVoltage, double psAmperage, boolean psPowerOnLine, boolean psCharging, boolean psDischarging, PowerSource.CapacityUnits psCapacityUnits, int psCurrentCapacity, int psMaxCapacity, int psDesignCapacity, int psCycleCount, String psChemistry, LocalDate psManufactureDate, String psManufacturer, String psSerialNumber, double psTemperature) {
/*  83 */     super(psName, psDeviceName, psRemainingCapacityPercent, psTimeRemainingEstimated, psTimeRemainingInstant, psPowerUsageRate, psVoltage, psAmperage, psPowerOnLine, psCharging, psDischarging, psCapacityUnits, psCurrentCapacity, psMaxCapacity, psDesignCapacity, psCycleCount, psChemistry, psManufactureDate, psManufacturer, psSerialNumber, psTemperature);
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
/*     */   public static List<PowerSource> getPowerSources() {
/*  95 */     return Collections.unmodifiableList((List)Arrays.asList((Object[])new WindowsPowerSource[] { getPowerSource("System Battery") }));
/*     */   }
/*     */   
/*     */   private static WindowsPowerSource getPowerSource(String name) {
/*  99 */     String psName = name;
/* 100 */     String psDeviceName = "unknown";
/* 101 */     double psRemainingCapacityPercent = 1.0D;
/* 102 */     double psTimeRemainingEstimated = -1.0D;
/* 103 */     double psTimeRemainingInstant = 0.0D;
/* 104 */     int psPowerUsageRate = 0;
/* 105 */     double psVoltage = -1.0D;
/* 106 */     double psAmperage = 0.0D;
/* 107 */     boolean psPowerOnLine = false;
/* 108 */     boolean psCharging = false;
/* 109 */     boolean psDischarging = false;
/* 110 */     PowerSource.CapacityUnits psCapacityUnits = PowerSource.CapacityUnits.RELATIVE;
/* 111 */     int psCurrentCapacity = 0;
/* 112 */     int psMaxCapacity = 1;
/* 113 */     int psDesignCapacity = 1;
/* 114 */     int psCycleCount = -1;
/* 115 */     String psChemistry = "unknown";
/* 116 */     LocalDate psManufactureDate = null;
/* 117 */     String psManufacturer = "unknown";
/* 118 */     String psSerialNumber = "unknown";
/* 119 */     double psTemperature = 0.0D;
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
/* 130 */     int size = (new PowrProf.SystemBatteryState()).size();
/* 131 */     Memory mem = new Memory(size);
/* 132 */     if (0 == PowrProf.INSTANCE.CallNtPowerInformation(5, null, 0, (Pointer)mem, size)) {
/*     */       
/* 134 */       PowrProf.SystemBatteryState batteryState = new PowrProf.SystemBatteryState((Pointer)mem);
/* 135 */       if (batteryState.batteryPresent > 0) {
/* 136 */         if (batteryState.acOnLine == 0 && batteryState.charging == 0 && batteryState.discharging > 0) {
/* 137 */           psTimeRemainingEstimated = batteryState.estimatedTime;
/* 138 */         } else if (batteryState.charging > 0) {
/* 139 */           psTimeRemainingEstimated = -2.0D;
/*     */         } 
/* 141 */         psMaxCapacity = batteryState.maxCapacity;
/* 142 */         psCurrentCapacity = batteryState.remainingCapacity;
/* 143 */         psRemainingCapacityPercent = Math.min(1.0D, psCurrentCapacity / psMaxCapacity);
/* 144 */         psPowerUsageRate = batteryState.rate;
/*     */       } 
/*     */     } 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 152 */     WinNT.HANDLE hdev = SetupApi.INSTANCE.SetupDiGetClassDevs(GUID_DEVCLASS_BATTERY, null, null, 18);
/*     */     
/* 154 */     if (WinBase.INVALID_HANDLE_VALUE != hdev) {
/* 155 */       boolean batteryFound = false;
/*     */       
/* 157 */       for (int idev = 0; !batteryFound && idev < 100; idev++) {
/* 158 */         SetupApi.SP_DEVICE_INTERFACE_DATA did = new SetupApi.SP_DEVICE_INTERFACE_DATA();
/* 159 */         did.cbSize = did.size();
/*     */         
/* 161 */         if (SetupApi.INSTANCE.SetupDiEnumDeviceInterfaces(hdev, null, GUID_DEVCLASS_BATTERY, idev, did)) {
/* 162 */           IntByReference requiredSize = new IntByReference(0);
/* 163 */           SetupApi.INSTANCE.SetupDiGetDeviceInterfaceDetail(hdev, did, null, 0, requiredSize, null);
/* 164 */           if (122 == Kernel32.INSTANCE.GetLastError()) {
/*     */             
/* 166 */             Memory pdidd = new Memory(requiredSize.getValue());
/*     */ 
/*     */ 
/*     */             
/* 170 */             pdidd.setInt(0L, 4 + (X64 ? 4 : CHAR_WIDTH));
/*     */             
/* 172 */             if (SetupApi.INSTANCE.SetupDiGetDeviceInterfaceDetail(hdev, did, (Pointer)pdidd, (int)pdidd.size(), requiredSize, null)) {
/*     */ 
/*     */ 
/*     */               
/* 176 */               String devicePath = (CHAR_WIDTH > 1) ? pdidd.getWideString(4L) : pdidd.getString(4L);
/* 177 */               WinNT.HANDLE hBattery = Kernel32.INSTANCE.CreateFile(devicePath, -1073741824, 3, null, 3, 128, null);
/*     */ 
/*     */ 
/*     */               
/* 181 */               if (!WinBase.INVALID_HANDLE_VALUE.equals(hBattery)) {
/*     */                 
/* 183 */                 PowrProf.BATTERY_QUERY_INFORMATION bqi = new PowrProf.BATTERY_QUERY_INFORMATION();
/* 184 */                 IntByReference dwWait = new IntByReference(0);
/* 185 */                 IntByReference dwTag = new IntByReference();
/* 186 */                 IntByReference dwOut = new IntByReference();
/*     */                 
/* 188 */                 if (Kernel32.INSTANCE.DeviceIoControl(hBattery, 2703424, dwWait
/* 189 */                     .getPointer(), 4, dwTag.getPointer(), 4, dwOut, null)) {
/*     */                   
/* 191 */                   bqi.BatteryTag = dwTag.getValue();
/* 192 */                   if (bqi.BatteryTag > 0) {
/*     */                     
/* 194 */                     bqi
/* 195 */                       .InformationLevel = PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatteryInformation.ordinal();
/* 196 */                     bqi.write();
/* 197 */                     PowrProf.BATTERY_INFORMATION bi = new PowrProf.BATTERY_INFORMATION();
/* 198 */                     if (Kernel32.INSTANCE.DeviceIoControl(hBattery, 2703428, bqi
/* 199 */                         .getPointer(), bqi.size(), bi.getPointer(), bi.size(), dwOut, null)) {
/*     */ 
/*     */                       
/* 202 */                       bi.read();
/* 203 */                       if (0 != (bi.Capabilities & Integer.MIN_VALUE) && 0 == (bi.Capabilities & 0x20000000)) {
/*     */ 
/*     */                         
/* 206 */                         if (0 == (bi.Capabilities & 0x40000000)) {
/* 207 */                           psCapacityUnits = PowerSource.CapacityUnits.MWH;
/*     */                         }
/* 209 */                         psChemistry = new String(bi.Chemistry, StandardCharsets.US_ASCII);
/* 210 */                         psDesignCapacity = bi.DesignedCapacity;
/* 211 */                         psMaxCapacity = bi.FullChargedCapacity;
/* 212 */                         psCycleCount = bi.CycleCount;
/*     */ 
/*     */                         
/* 215 */                         PowrProf.BATTERY_WAIT_STATUS bws = new PowrProf.BATTERY_WAIT_STATUS();
/* 216 */                         bws.BatteryTag = bqi.BatteryTag;
/* 217 */                         bws.write();
/* 218 */                         PowrProf.BATTERY_STATUS bs = new PowrProf.BATTERY_STATUS();
/* 219 */                         if (Kernel32.INSTANCE.DeviceIoControl(hBattery, 2703436, bws
/* 220 */                             .getPointer(), bws.size(), bs
/* 221 */                             .getPointer(), bs.size(), dwOut, null)) {
/* 222 */                           bs.read();
/* 223 */                           if (0 != (bs.PowerState & 0x1)) {
/* 224 */                             psPowerOnLine = true;
/*     */                           }
/* 226 */                           if (0 != (bs.PowerState & 0x2)) {
/* 227 */                             psDischarging = true;
/*     */                           }
/* 229 */                           if (0 != (bs.PowerState & 0x4)) {
/* 230 */                             psCharging = true;
/*     */                           }
/* 232 */                           psCurrentCapacity = bs.Capacity;
/* 233 */                           psVoltage = (bs.Voltage > 0) ? (bs.Voltage / 1000.0D) : bs.Voltage;
/* 234 */                           psPowerUsageRate = bs.Rate;
/* 235 */                           if (psVoltage > 0.0D) {
/* 236 */                             psAmperage = psPowerUsageRate / psVoltage;
/*     */                           }
/*     */                         } 
/*     */                       } 
/*     */                       
/* 241 */                       psDeviceName = batteryQueryString(hBattery, dwTag.getValue(), PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatteryDeviceName
/*     */                           
/* 243 */                           .ordinal());
/* 244 */                       psManufacturer = batteryQueryString(hBattery, dwTag.getValue(), PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatteryManufactureName
/*     */                           
/* 246 */                           .ordinal());
/* 247 */                       psSerialNumber = batteryQueryString(hBattery, dwTag.getValue(), PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatterySerialNumber
/*     */                           
/* 249 */                           .ordinal());
/*     */                       
/* 251 */                       bqi
/* 252 */                         .InformationLevel = PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatteryManufactureDate.ordinal();
/* 253 */                       bqi.write();
/* 254 */                       PowrProf.BATTERY_MANUFACTURE_DATE bmd = new PowrProf.BATTERY_MANUFACTURE_DATE();
/* 255 */                       if (Kernel32.INSTANCE.DeviceIoControl(hBattery, 2703428, bqi
/* 256 */                           .getPointer(), bqi.size(), bmd
/* 257 */                           .getPointer(), bmd.size(), dwOut, null)) {
/* 258 */                         bmd.read();
/*     */                         
/* 260 */                         if (bmd.Year > 1900 && bmd.Month > 0 && bmd.Day > 0) {
/* 261 */                           psManufactureDate = LocalDate.of(bmd.Year, bmd.Month, bmd.Day);
/*     */                         }
/*     */                       } 
/*     */                       
/* 265 */                       bqi
/* 266 */                         .InformationLevel = PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatteryTemperature.ordinal();
/* 267 */                       bqi.write();
/* 268 */                       IntByReference tempK = new IntByReference();
/* 269 */                       if (Kernel32.INSTANCE.DeviceIoControl(hBattery, 2703428, bqi
/* 270 */                           .getPointer(), bqi.size(), tempK
/* 271 */                           .getPointer(), 4, dwOut, null)) {
/* 272 */                         psTemperature = tempK.getValue() / 10.0D - 273.15D;
/*     */                       }
/*     */ 
/*     */                       
/* 276 */                       bqi
/* 277 */                         .InformationLevel = PowrProf.BATTERY_QUERY_INFORMATION_LEVEL.BatteryEstimatedTime.ordinal();
/* 278 */                       if (psPowerUsageRate != 0) {
/* 279 */                         bqi.AtRate = psPowerUsageRate;
/*     */                       }
/* 281 */                       bqi.write();
/* 282 */                       IntByReference tr = new IntByReference();
/* 283 */                       if (Kernel32.INSTANCE.DeviceIoControl(hBattery, 2703428, bqi
/* 284 */                           .getPointer(), bqi.size(), tr
/* 285 */                           .getPointer(), 4, dwOut, null)) {
/* 286 */                         psTimeRemainingInstant = tr.getValue();
/*     */                       }
/*     */                       
/* 289 */                       if (psTimeRemainingInstant < 0.0D && psPowerUsageRate != 0) {
/* 290 */                         psTimeRemainingInstant = (psMaxCapacity - psCurrentCapacity) * 3600.0D / psPowerUsageRate;
/*     */                         
/* 292 */                         if (psTimeRemainingInstant < 0.0D) {
/* 293 */                           psTimeRemainingInstant *= -1.0D;
/*     */                         }
/*     */                       } 
/*     */                       
/* 297 */                       batteryFound = true;
/*     */                     } 
/*     */                   } 
/*     */                 } 
/* 301 */                 Kernel32.INSTANCE.CloseHandle(hBattery);
/*     */               } 
/*     */             } 
/*     */           } 
/* 305 */         } else if (259 == Kernel32.INSTANCE.GetLastError()) {
/*     */           break;
/*     */         } 
/*     */       } 
/* 309 */       SetupApi.INSTANCE.SetupDiDestroyDeviceInfoList(hdev);
/*     */     } 
/*     */     
/* 312 */     return new WindowsPowerSource(psName, psDeviceName, psRemainingCapacityPercent, psTimeRemainingEstimated, psTimeRemainingInstant, psPowerUsageRate, psVoltage, psAmperage, psPowerOnLine, psCharging, psDischarging, psCapacityUnits, psCurrentCapacity, psMaxCapacity, psDesignCapacity, psCycleCount, psChemistry, psManufactureDate, psManufacturer, psSerialNumber, psTemperature);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static String batteryQueryString(WinNT.HANDLE hBattery, int tag, int infoLevel) {
/*     */     Memory nameBuf;
/* 319 */     PowrProf.BATTERY_QUERY_INFORMATION bqi = new PowrProf.BATTERY_QUERY_INFORMATION();
/* 320 */     bqi.BatteryTag = tag;
/* 321 */     bqi.InformationLevel = infoLevel;
/* 322 */     bqi.write();
/* 323 */     IntByReference dwOut = new IntByReference();
/* 324 */     boolean ret = false;
/* 325 */     long bufSize = 0L;
/*     */ 
/*     */     
/*     */     do {
/* 329 */       bufSize += 256L;
/* 330 */       nameBuf = new Memory(bufSize);
/* 331 */       ret = Kernel32.INSTANCE.DeviceIoControl(hBattery, 2703428, bqi.getPointer(), bqi
/* 332 */           .size(), (Pointer)nameBuf, (int)nameBuf.size(), dwOut, null);
/* 333 */     } while (!ret && bufSize < 4096L);
/* 334 */     return (CHAR_WIDTH > 1) ? nameBuf.getWideString(0L) : nameBuf.getString(0L);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\windows\WindowsPowerSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */