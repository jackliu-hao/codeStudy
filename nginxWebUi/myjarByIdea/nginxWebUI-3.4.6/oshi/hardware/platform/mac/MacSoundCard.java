package oshi.hardware.platform.mac;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.SoundCard;
import oshi.hardware.common.AbstractSoundCard;
import oshi.util.FileUtil;
import oshi.util.ParseUtil;

@Immutable
final class MacSoundCard extends AbstractSoundCard {
   private static final String APPLE = "Apple Inc.";

   MacSoundCard(String kernelVersion, String name, String codec) {
      super(kernelVersion, name, codec);
   }

   public static List<SoundCard> getSoundCards() {
      List<MacSoundCard> soundCards = new ArrayList();
      String manufacturer = "Apple Inc.";
      String kernelVersion = "AppleHDAController";
      String codec = "AppleHDACodec";
      boolean version = false;
      String versionMarker = "<key>com.apple.driver.AppleHDAController</key>";
      Iterator var6 = FileUtil.readFile("/System/Library/Extensions/AppleHDA.kext/Contents/Info.plist").iterator();

      while(var6.hasNext()) {
         String checkLine = (String)var6.next();
         if (checkLine.contains(versionMarker)) {
            version = true;
         } else if (version) {
            kernelVersion = "AppleHDAController " + ParseUtil.getTextBetweenStrings(checkLine, "<string>", "</string>");
            version = false;
         }
      }

      soundCards.add(new MacSoundCard(kernelVersion, manufacturer, codec));
      return Collections.unmodifiableList(soundCards);
   }
}
