package oshi.hardware.platform.linux;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import oshi.annotation.concurrent.Immutable;
import oshi.hardware.SoundCard;
import oshi.hardware.common.AbstractSoundCard;
import oshi.util.FileUtil;
import oshi.util.platform.linux.ProcPath;

@Immutable
final class LinuxSoundCard extends AbstractSoundCard {
   private static final Logger LOG = LoggerFactory.getLogger(LinuxSoundCard.class);
   private static final String CARD_FOLDER = "card";
   private static final String CARDS_FILE = "cards";
   private static final String ID_FILE = "id";

   LinuxSoundCard(String kernelVersion, String name, String codec) {
      super(kernelVersion, name, codec);
   }

   private static List<File> getCardFolders() {
      File cardsDirectory = new File(ProcPath.ASOUND);
      List<File> cardFolders = new ArrayList();
      File[] allContents = cardsDirectory.listFiles();
      if (allContents != null) {
         File[] var3 = allContents;
         int var4 = allContents.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            File card = var3[var5];
            if (card.getName().startsWith("card") && card.isDirectory()) {
               cardFolders.add(card);
            }
         }
      } else {
         LOG.warn("No Audio Cards Found");
      }

      return cardFolders;
   }

   private static String getSoundCardVersion() {
      String driverVersion = FileUtil.getStringFromFile(ProcPath.ASOUND + "version");
      return driverVersion.isEmpty() ? "not available" : driverVersion;
   }

   private static String getCardCodec(File cardDir) {
      String cardCodec = "";
      File[] cardFiles = cardDir.listFiles();
      if (cardFiles != null) {
         File[] var3 = cardFiles;
         int var4 = cardFiles.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            File file = var3[var5];
            if (file.getName().startsWith("codec")) {
               if (!file.isDirectory()) {
                  cardCodec = (String)FileUtil.getKeyValueMapFromFile(file.getPath(), ":").get("Codec");
               } else {
                  File[] codecs = file.listFiles();
                  if (codecs != null) {
                     File[] var8 = codecs;
                     int var9 = codecs.length;

                     for(int var10 = 0; var10 < var9; ++var10) {
                        File codec = var8[var10];
                        if (!codec.isDirectory() && codec.getName().contains("#")) {
                           cardCodec = codec.getName().substring(0, codec.getName().indexOf(35));
                           break;
                        }
                     }
                  }
               }
            }
         }
      }

      return cardCodec;
   }

   private static String getCardName(File file) {
      String cardName = "Not Found..";
      Map<String, String> cardNamePairs = FileUtil.getKeyValueMapFromFile(ProcPath.ASOUND + "/" + "cards", ":");
      String cardId = FileUtil.getStringFromFile(file.getPath() + "/" + "id");
      Iterator var4 = cardNamePairs.entrySet().iterator();

      Map.Entry entry;
      do {
         if (!var4.hasNext()) {
            return cardName;
         }

         entry = (Map.Entry)var4.next();
      } while(!((String)entry.getKey()).contains(cardId));

      cardName = (String)entry.getValue();
      return cardName;
   }

   public static List<SoundCard> getSoundCards() {
      List<LinuxSoundCard> soundCards = new ArrayList();
      Iterator var1 = getCardFolders().iterator();

      while(var1.hasNext()) {
         File cardFile = (File)var1.next();
         soundCards.add(new LinuxSoundCard(getSoundCardVersion(), getCardName(cardFile), getCardCodec(cardFile)));
      }

      return Collections.unmodifiableList(soundCards);
   }
}
