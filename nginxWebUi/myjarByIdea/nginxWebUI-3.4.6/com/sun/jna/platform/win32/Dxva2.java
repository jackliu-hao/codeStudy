package com.sun.jna.platform.win32;

import com.sun.jna.DefaultTypeMapper;
import com.sun.jna.Native;
import com.sun.jna.platform.EnumConverter;
import com.sun.jna.win32.StdCallLibrary;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public interface Dxva2 extends StdCallLibrary, PhysicalMonitorEnumerationAPI, HighLevelMonitorConfigurationAPI, LowLevelMonitorConfigurationAPI {
   Map<String, Object> DXVA_OPTIONS = Collections.unmodifiableMap(new HashMap<String, Object>() {
      private static final long serialVersionUID = -1987971664975780480L;

      {
         this.put("type-mapper", new DefaultTypeMapper() {
            {
               this.addTypeConverter(HighLevelMonitorConfigurationAPI.MC_POSITION_TYPE.class, new EnumConverter(HighLevelMonitorConfigurationAPI.MC_POSITION_TYPE.class));
               this.addTypeConverter(HighLevelMonitorConfigurationAPI.MC_SIZE_TYPE.class, new EnumConverter(HighLevelMonitorConfigurationAPI.MC_SIZE_TYPE.class));
               this.addTypeConverter(HighLevelMonitorConfigurationAPI.MC_GAIN_TYPE.class, new EnumConverter(HighLevelMonitorConfigurationAPI.MC_GAIN_TYPE.class));
               this.addTypeConverter(HighLevelMonitorConfigurationAPI.MC_DRIVE_TYPE.class, new EnumConverter(HighLevelMonitorConfigurationAPI.MC_DRIVE_TYPE.class));
            }
         });
      }
   });
   Dxva2 INSTANCE = (Dxva2)Native.load("Dxva2", Dxva2.class, DXVA_OPTIONS);

   WinDef.BOOL GetMonitorCapabilities(WinNT.HANDLE var1, WinDef.DWORDByReference var2, WinDef.DWORDByReference var3);

   WinDef.BOOL SaveCurrentMonitorSettings(WinNT.HANDLE var1);

   WinDef.BOOL GetMonitorTechnologyType(WinNT.HANDLE var1, HighLevelMonitorConfigurationAPI.MC_DISPLAY_TECHNOLOGY_TYPE.ByReference var2);

   WinDef.BOOL GetMonitorBrightness(WinNT.HANDLE var1, WinDef.DWORDByReference var2, WinDef.DWORDByReference var3, WinDef.DWORDByReference var4);

   WinDef.BOOL GetMonitorContrast(WinNT.HANDLE var1, WinDef.DWORDByReference var2, WinDef.DWORDByReference var3, WinDef.DWORDByReference var4);

   WinDef.BOOL GetMonitorColorTemperature(WinNT.HANDLE var1, HighLevelMonitorConfigurationAPI.MC_COLOR_TEMPERATURE.ByReference var2);

   WinDef.BOOL GetMonitorRedGreenOrBlueDrive(WinNT.HANDLE var1, HighLevelMonitorConfigurationAPI.MC_DRIVE_TYPE var2, WinDef.DWORDByReference var3, WinDef.DWORDByReference var4, WinDef.DWORDByReference var5);

   WinDef.BOOL GetMonitorRedGreenOrBlueGain(WinNT.HANDLE var1, HighLevelMonitorConfigurationAPI.MC_GAIN_TYPE var2, WinDef.DWORDByReference var3, WinDef.DWORDByReference var4, WinDef.DWORDByReference var5);

   WinDef.BOOL SetMonitorBrightness(WinNT.HANDLE var1, int var2);

   WinDef.BOOL SetMonitorContrast(WinNT.HANDLE var1, int var2);

   WinDef.BOOL SetMonitorColorTemperature(WinNT.HANDLE var1, HighLevelMonitorConfigurationAPI.MC_COLOR_TEMPERATURE var2);

   WinDef.BOOL SetMonitorRedGreenOrBlueDrive(WinNT.HANDLE var1, HighLevelMonitorConfigurationAPI.MC_DRIVE_TYPE var2, int var3);

   WinDef.BOOL SetMonitorRedGreenOrBlueGain(WinNT.HANDLE var1, HighLevelMonitorConfigurationAPI.MC_GAIN_TYPE var2, int var3);

   WinDef.BOOL DegaussMonitor(WinNT.HANDLE var1);

   WinDef.BOOL GetMonitorDisplayAreaSize(WinNT.HANDLE var1, HighLevelMonitorConfigurationAPI.MC_SIZE_TYPE var2, WinDef.DWORDByReference var3, WinDef.DWORDByReference var4, WinDef.DWORDByReference var5);

   WinDef.BOOL GetMonitorDisplayAreaPosition(WinNT.HANDLE var1, HighLevelMonitorConfigurationAPI.MC_POSITION_TYPE var2, WinDef.DWORDByReference var3, WinDef.DWORDByReference var4, WinDef.DWORDByReference var5);

   WinDef.BOOL SetMonitorDisplayAreaSize(WinNT.HANDLE var1, HighLevelMonitorConfigurationAPI.MC_SIZE_TYPE var2, int var3);

   WinDef.BOOL SetMonitorDisplayAreaPosition(WinNT.HANDLE var1, HighLevelMonitorConfigurationAPI.MC_POSITION_TYPE var2, int var3);

   WinDef.BOOL RestoreMonitorFactoryColorDefaults(WinNT.HANDLE var1);

   WinDef.BOOL RestoreMonitorFactoryDefaults(WinNT.HANDLE var1);

   WinDef.BOOL GetVCPFeatureAndVCPFeatureReply(WinNT.HANDLE var1, WinDef.BYTE var2, LowLevelMonitorConfigurationAPI.MC_VCP_CODE_TYPE.ByReference var3, WinDef.DWORDByReference var4, WinDef.DWORDByReference var5);

   WinDef.BOOL SetVCPFeature(WinNT.HANDLE var1, WinDef.BYTE var2, WinDef.DWORD var3);

   WinDef.BOOL SaveCurrentSettings(WinNT.HANDLE var1);

   WinDef.BOOL GetCapabilitiesStringLength(WinNT.HANDLE var1, WinDef.DWORDByReference var2);

   WinDef.BOOL CapabilitiesRequestAndCapabilitiesReply(WinNT.HANDLE var1, WTypes.LPSTR var2, WinDef.DWORD var3);

   WinDef.BOOL GetTimingReport(WinNT.HANDLE var1, LowLevelMonitorConfigurationAPI.MC_TIMING_REPORT var2);

   WinDef.BOOL GetNumberOfPhysicalMonitorsFromHMONITOR(WinUser.HMONITOR var1, WinDef.DWORDByReference var2);

   WinDef.BOOL GetPhysicalMonitorsFromHMONITOR(WinUser.HMONITOR var1, int var2, PhysicalMonitorEnumerationAPI.PHYSICAL_MONITOR[] var3);

   WinDef.BOOL DestroyPhysicalMonitor(WinNT.HANDLE var1);

   WinDef.BOOL DestroyPhysicalMonitors(int var1, PhysicalMonitorEnumerationAPI.PHYSICAL_MONITOR[] var2);
}
