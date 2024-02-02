/*     */ package oshi.hardware.platform.linux;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Collections;
/*     */ import java.util.List;
/*     */ import java.util.Map;
/*     */ import org.slf4j.Logger;
/*     */ import org.slf4j.LoggerFactory;
/*     */ import oshi.annotation.concurrent.Immutable;
/*     */ import oshi.hardware.SoundCard;
/*     */ import oshi.hardware.common.AbstractSoundCard;
/*     */ import oshi.util.FileUtil;
/*     */ import oshi.util.platform.linux.ProcPath;
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
/*     */ @Immutable
/*     */ final class LinuxSoundCard
/*     */   extends AbstractSoundCard
/*     */ {
/*  47 */   private static final Logger LOG = LoggerFactory.getLogger(LinuxSoundCard.class);
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String CARD_FOLDER = "card";
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String CARDS_FILE = "cards";
/*     */ 
/*     */ 
/*     */   
/*     */   private static final String ID_FILE = "id";
/*     */ 
/*     */ 
/*     */   
/*     */   LinuxSoundCard(String kernelVersion, String name, String codec) {
/*  64 */     super(kernelVersion, name, codec);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private static List<File> getCardFolders() {
/*  74 */     File cardsDirectory = new File(ProcPath.ASOUND);
/*  75 */     List<File> cardFolders = new ArrayList<>();
/*  76 */     File[] allContents = cardsDirectory.listFiles();
/*  77 */     if (allContents != null) {
/*  78 */       for (File card : allContents) {
/*  79 */         if (card.getName().startsWith("card") && card.isDirectory()) {
/*  80 */           cardFolders.add(card);
/*     */         }
/*     */       } 
/*     */     } else {
/*  84 */       LOG.warn("No Audio Cards Found");
/*     */     } 
/*  86 */     return cardFolders;
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
/*     */   private static String getSoundCardVersion() {
/*  98 */     String driverVersion = FileUtil.getStringFromFile(ProcPath.ASOUND + "version");
/*  99 */     return driverVersion.isEmpty() ? "not available" : driverVersion;
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
/*     */   private static String getCardCodec(File cardDir) {
/* 114 */     String cardCodec = "";
/* 115 */     File[] cardFiles = cardDir.listFiles();
/* 116 */     if (cardFiles != null) {
/* 117 */       for (File file : cardFiles) {
/* 118 */         if (file.getName().startsWith("codec")) {
/* 119 */           if (!file.isDirectory()) {
/* 120 */             cardCodec = (String)FileUtil.getKeyValueMapFromFile(file.getPath(), ":").get("Codec");
/*     */           
/*     */           }
/*     */           else {
/*     */ 
/*     */             
/* 126 */             File[] codecs = file.listFiles();
/* 127 */             if (codecs != null) {
/* 128 */               for (File codec : codecs) {
/* 129 */                 if (!codec.isDirectory() && codec.getName().contains("#")) {
/* 130 */                   cardCodec = codec.getName().substring(0, codec.getName().indexOf('#'));
/*     */                   break;
/*     */                 } 
/*     */               } 
/*     */             }
/*     */           } 
/*     */         }
/*     */       } 
/*     */     }
/* 139 */     return cardCodec;
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
/*     */   private static String getCardName(File file) {
/* 156 */     String cardName = "Not Found..";
/* 157 */     Map<String, String> cardNamePairs = FileUtil.getKeyValueMapFromFile(ProcPath.ASOUND + "/" + "cards", ":");
/* 158 */     String cardId = FileUtil.getStringFromFile(file.getPath() + "/" + "id");
/* 159 */     for (Map.Entry<String, String> entry : cardNamePairs.entrySet()) {
/* 160 */       if (((String)entry.getKey()).contains(cardId)) {
/* 161 */         cardName = entry.getValue();
/* 162 */         return cardName;
/*     */       } 
/*     */     } 
/* 165 */     return cardName;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static List<SoundCard> getSoundCards() {
/* 176 */     List<LinuxSoundCard> soundCards = new ArrayList<>();
/* 177 */     for (File cardFile : getCardFolders()) {
/* 178 */       soundCards.add(new LinuxSoundCard(getSoundCardVersion(), getCardName(cardFile), getCardCodec(cardFile)));
/*     */     }
/* 180 */     return (List)Collections.unmodifiableList(soundCards);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\linux\LinuxSoundCard.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */