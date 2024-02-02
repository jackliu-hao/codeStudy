/*     */ package oshi.hardware.platform.linux;
/*     */ 
/*     */ import java.time.format.DateTimeFormatter;
/*     */ import java.time.format.DateTimeParseException;
/*     */ import java.util.List;
/*     */ import java.util.Locale;
/*     */ import java.util.function.Supplier;
/*     */ import oshi.annotation.concurrent.Immutable;
/*     */ import oshi.driver.linux.Dmidecode;
/*     */ import oshi.driver.linux.Sysfs;
/*     */ import oshi.hardware.common.AbstractFirmware;
/*     */ import oshi.util.ExecutingCommand;
/*     */ import oshi.util.Memoizer;
/*     */ import oshi.util.ParseUtil;
/*     */ import oshi.util.tuples.Pair;
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
/*     */ @Immutable
/*     */ final class LinuxFirmware
/*     */   extends AbstractFirmware
/*     */ {
/*  49 */   private static final DateTimeFormatter VCGEN_FORMATTER = DateTimeFormatter.ofPattern("MMM d uuuu HH:mm:ss", Locale.ENGLISH);
/*     */ 
/*     */   
/*  52 */   private final Supplier<String> manufacturer = Memoizer.memoize(this::queryManufacturer);
/*     */   
/*  54 */   private final Supplier<String> description = Memoizer.memoize(this::queryDescription);
/*     */   
/*  56 */   private final Supplier<String> version = Memoizer.memoize(this::queryVersion);
/*     */   
/*  58 */   private final Supplier<String> releaseDate = Memoizer.memoize(this::queryReleaseDate);
/*     */   
/*  60 */   private final Supplier<String> name = Memoizer.memoize(this::queryName);
/*     */   
/*  62 */   private final Supplier<VcGenCmdStrings> vcGenCmd = Memoizer.memoize(LinuxFirmware::queryVcGenCmd);
/*     */   
/*  64 */   private final Supplier<Pair<String, String>> biosNameRev = Memoizer.memoize(Dmidecode::queryBiosNameRev);
/*     */ 
/*     */   
/*     */   public String getManufacturer() {
/*  68 */     return this.manufacturer.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDescription() {
/*  73 */     return this.description.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getVersion() {
/*  78 */     return this.version.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getReleaseDate() {
/*  83 */     return this.releaseDate.get();
/*     */   }
/*     */ 
/*     */   
/*     */   public String getName() {
/*  88 */     return this.name.get();
/*     */   }
/*     */   
/*     */   private String queryManufacturer() {
/*  92 */     String result = null;
/*  93 */     if ((result = Sysfs.queryBiosVendor()) == null && (result = (this.vcGenCmd.get()).manufacturer) == null) {
/*  94 */       return "unknown";
/*     */     }
/*  96 */     return result;
/*     */   }
/*     */   
/*     */   private String queryDescription() {
/* 100 */     String result = null;
/* 101 */     if ((result = Sysfs.queryBiosDescription()) == null && (result = (this.vcGenCmd.get()).description) == null) {
/* 102 */       return "unknown";
/*     */     }
/* 104 */     return result;
/*     */   }
/*     */   
/*     */   private String queryVersion() {
/* 108 */     String result = null;
/* 109 */     if ((result = Sysfs.queryBiosVersion((String)((Pair)this.biosNameRev.get()).getB())) == null && (
/* 110 */       result = (this.vcGenCmd.get()).version) == null) {
/* 111 */       return "unknown";
/*     */     }
/* 113 */     return result;
/*     */   }
/*     */   
/*     */   private String queryReleaseDate() {
/* 117 */     String result = null;
/* 118 */     if ((result = Sysfs.queryBiosReleaseDate()) == null && (result = (this.vcGenCmd.get()).releaseDate) == null) {
/* 119 */       return "unknown";
/*     */     }
/* 121 */     return result;
/*     */   }
/*     */   
/*     */   private String queryName() {
/* 125 */     String result = null;
/* 126 */     if ((result = (String)((Pair)this.biosNameRev.get()).getA()) == null && (result = (this.vcGenCmd.get()).name) == null) {
/* 127 */       return "unknown";
/*     */     }
/* 129 */     return result;
/*     */   }
/*     */   
/*     */   private static VcGenCmdStrings queryVcGenCmd() {
/* 133 */     String vcReleaseDate = null;
/* 134 */     String vcManufacturer = null;
/* 135 */     String vcVersion = null;
/*     */     
/* 137 */     List<String> vcgencmd = ExecutingCommand.runNative("vcgencmd version");
/* 138 */     if (vcgencmd.size() >= 3) {
/*     */       
/*     */       try {
/* 141 */         vcReleaseDate = DateTimeFormatter.ISO_LOCAL_DATE.format(VCGEN_FORMATTER.parse(vcgencmd.get(0)));
/* 142 */       } catch (DateTimeParseException e) {
/* 143 */         vcReleaseDate = "unknown";
/*     */       } 
/*     */       
/* 146 */       String[] copyright = ParseUtil.whitespaces.split(vcgencmd.get(1));
/* 147 */       vcManufacturer = copyright[copyright.length - 1];
/*     */       
/* 149 */       vcVersion = ((String)vcgencmd.get(2)).replace("version ", "");
/* 150 */       return new VcGenCmdStrings(vcReleaseDate, vcManufacturer, vcVersion, "RPi", "Bootloader");
/*     */     } 
/* 152 */     return new VcGenCmdStrings(null, null, null, null, null);
/*     */   }
/*     */   
/*     */   private static final class VcGenCmdStrings
/*     */   {
/*     */     private final String releaseDate;
/*     */     private final String manufacturer;
/*     */     private final String version;
/*     */     private final String name;
/*     */     private final String description;
/*     */     
/*     */     private VcGenCmdStrings(String releaseDate, String manufacturer, String version, String name, String description) {
/* 164 */       this.releaseDate = releaseDate;
/* 165 */       this.manufacturer = manufacturer;
/* 166 */       this.version = version;
/* 167 */       this.name = name;
/* 168 */       this.description = description;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\platform\linux\LinuxFirmware.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */