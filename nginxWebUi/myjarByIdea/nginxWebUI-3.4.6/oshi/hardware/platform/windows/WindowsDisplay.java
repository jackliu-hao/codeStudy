package oshi.hardware.platform.windows;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.Guid;
import com.sun.jna.platform.win32.SetupApi;
import com.sun.jna.platform.win32.WinBase;
import com.sun.jna.platform.win32.WinNT;
import com.sun.jna.platform.win32.WinReg;
import com.sun.jna.ptr.IntByReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Display;
import oshi.hardware.common.AbstractDisplay;

@Immutable
final class WindowsDisplay extends AbstractDisplay {
   private static final Logger LOG = LoggerFactory.getLogger(WindowsDisplay.class);
   private static final SetupApi SU;
   private static final Advapi32 ADV;
   private static final Guid.GUID GUID_DEVINTERFACE_MONITOR;

   WindowsDisplay(byte[] edid) {
      super(edid);
      LOG.debug("Initialized WindowsDisplay");
   }

   public static List<Display> getDisplays() {
      List<Display> displays = new ArrayList();
      WinNT.HANDLE hDevInfo = SU.SetupDiGetClassDevs(GUID_DEVINTERFACE_MONITOR, (Pointer)null, (Pointer)null, 18);
      if (!hDevInfo.equals(WinBase.INVALID_HANDLE_VALUE)) {
         SetupApi.SP_DEVICE_INTERFACE_DATA deviceInterfaceData = new SetupApi.SP_DEVICE_INTERFACE_DATA();
         deviceInterfaceData.cbSize = deviceInterfaceData.size();
         SetupApi.SP_DEVINFO_DATA info = new SetupApi.SP_DEVINFO_DATA();

         for(int memberIndex = 0; SU.SetupDiEnumDeviceInfo(hDevInfo, memberIndex, info); ++memberIndex) {
            WinReg.HKEY key = SU.SetupDiOpenDevRegKey(hDevInfo, info, 1, 0, 1, 1);
            byte[] edid = new byte[1];
            IntByReference pType = new IntByReference();
            IntByReference lpcbData = new IntByReference();
            if (ADV.RegQueryValueEx(key, "EDID", 0, pType, (byte[])edid, lpcbData) == 234) {
               edid = new byte[lpcbData.getValue()];
               if (ADV.RegQueryValueEx(key, "EDID", 0, pType, (byte[])edid, lpcbData) == 0) {
                  Display display = new WindowsDisplay(edid);
                  displays.add(display);
               }
            }

            Advapi32.INSTANCE.RegCloseKey(key);
         }

         SU.SetupDiDestroyDeviceInfoList(hDevInfo);
      }

      return Collections.unmodifiableList(displays);
   }

   static {
      SU = SetupApi.INSTANCE;
      ADV = Advapi32.INSTANCE;
      GUID_DEVINTERFACE_MONITOR = new Guid.GUID("E6F07B5F-EE97-4a90-B076-33F57BF4EAA7");
   }
}
