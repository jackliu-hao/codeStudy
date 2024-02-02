package oshi.hardware.platform.windows;

import com.sun.jna.platform.win32.Advapi32Util;
import com.sun.jna.platform.win32.Win32Exception;
import com.sun.jna.platform.win32.WinReg;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.SoundCard;
import oshi.hardware.common.AbstractSoundCard;

@Immutable
final class WindowsSoundCard extends AbstractSoundCard {
   private static final String REGISTRY_SOUNDCARDS = "SYSTEM\\CurrentControlSet\\Control\\Class\\{4d36e96c-e325-11ce-bfc1-08002be10318}\\";

   WindowsSoundCard(String kernelVersion, String name, String codec) {
      super(kernelVersion, name, codec);
   }

   public static List<SoundCard> getSoundCards() {
      List<WindowsSoundCard> soundCards = new ArrayList();
      String[] keys = Advapi32Util.registryGetKeys(WinReg.HKEY_LOCAL_MACHINE, "SYSTEM\\CurrentControlSet\\Control\\Class\\{4d36e96c-e325-11ce-bfc1-08002be10318}\\");
      String[] var2 = keys;
      int var3 = keys.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String key = var2[var4];
         String fullKey = "SYSTEM\\CurrentControlSet\\Control\\Class\\{4d36e96c-e325-11ce-bfc1-08002be10318}\\" + key;

         try {
            if (Advapi32Util.registryValueExists(WinReg.HKEY_LOCAL_MACHINE, fullKey, "Driver")) {
               soundCards.add(new WindowsSoundCard(Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, fullKey, "Driver") + " " + Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, fullKey, "DriverVersion"), Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, fullKey, "ProviderName") + " " + Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, fullKey, "DriverDesc"), Advapi32Util.registryGetStringValue(WinReg.HKEY_LOCAL_MACHINE, fullKey, "DriverDesc")));
            }
         } catch (Win32Exception var8) {
            if (var8.getErrorCode() != 5) {
               throw var8;
            }
         }
      }

      return Collections.unmodifiableList(soundCards);
   }
}
