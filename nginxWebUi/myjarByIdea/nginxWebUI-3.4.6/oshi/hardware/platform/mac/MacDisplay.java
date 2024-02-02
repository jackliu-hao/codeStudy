package oshi.hardware.platform.mac;

import com.sun.jna.Pointer;
import com.sun.jna.platform.mac.CoreFoundation;
import com.sun.jna.platform.mac.IOKit;
import com.sun.jna.platform.mac.IOKitUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.Display;
import oshi.hardware.common.AbstractDisplay;

@Immutable
final class MacDisplay extends AbstractDisplay {
   private static final Logger LOG = LoggerFactory.getLogger(MacDisplay.class);

   MacDisplay(byte[] edid) {
      super(edid);
      LOG.debug("Initialized MacDisplay");
   }

   public static List<Display> getDisplays() {
      List<Display> displays = new ArrayList();
      IOKit.IOIterator serviceIterator = IOKitUtil.getMatchingServices("IODisplayConnect");
      if (serviceIterator != null) {
         CoreFoundation.CFStringRef cfEdid = CoreFoundation.CFStringRef.createCFString("IODisplayEDID");

         for(IOKit.IORegistryEntry sdService = serviceIterator.next(); sdService != null; sdService = serviceIterator.next()) {
            IOKit.IORegistryEntry properties = sdService.getChildEntry("IOService");
            if (properties != null) {
               CoreFoundation.CFTypeRef edidRaw = properties.createCFProperty(cfEdid);
               if (edidRaw != null) {
                  CoreFoundation.CFDataRef edid = new CoreFoundation.CFDataRef(edidRaw.getPointer());
                  int length = edid.getLength();
                  Pointer p = edid.getBytePtr();
                  displays.add(new MacDisplay(p.getByteArray(0L, length)));
                  edid.release();
               }

               properties.release();
            }

            sdService.release();
         }

         serviceIterator.release();
         cfEdid.release();
      }

      return Collections.unmodifiableList(displays);
   }
}
