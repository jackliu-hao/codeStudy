package oshi.hardware.platform.unix.freebsd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.SoundCard;
import oshi.hardware.common.AbstractSoundCard;
import oshi.util.ExecutingCommand;
import oshi.util.ParseUtil;

@Immutable
final class FreeBsdSoundCard extends AbstractSoundCard {
   private static final String LSHAL = "lshal";

   FreeBsdSoundCard(String kernelVersion, String name, String codec) {
      super(kernelVersion, name, codec);
   }

   public static List<SoundCard> getSoundCards() {
      Map<String, String> vendorMap = new HashMap();
      Map<String, String> productMap = new HashMap();
      vendorMap.clear();
      productMap.clear();
      List<String> sounds = new ArrayList();
      String key = "";
      Iterator var4 = ExecutingCommand.runNative("lshal").iterator();

      while(true) {
         while(var4.hasNext()) {
            String line = (String)var4.next();
            line = line.trim();
            if (line.startsWith("udi =")) {
               key = ParseUtil.getSingleQuoteStringValue(line);
            } else if (!key.isEmpty() && !line.isEmpty()) {
               if (line.contains("freebsd.driver =") && "pcm".equals(ParseUtil.getSingleQuoteStringValue(line))) {
                  sounds.add(key);
               } else if (line.contains("info.product")) {
                  productMap.put(key, ParseUtil.getStringBetween(line, '\''));
               } else if (line.contains("info.vendor")) {
                  vendorMap.put(key, ParseUtil.getStringBetween(line, '\''));
               }
            }
         }

         List<SoundCard> soundCards = new ArrayList();
         Iterator var8 = sounds.iterator();

         while(var8.hasNext()) {
            String _key = (String)var8.next();
            soundCards.add(new FreeBsdSoundCard((String)productMap.get(_key), (String)vendorMap.get(_key) + " " + (String)productMap.get(_key), (String)productMap.get(_key)));
         }

         return Collections.unmodifiableList(soundCards);
      }
   }
}
