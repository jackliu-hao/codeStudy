package oshi.hardware;

import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import oshi.annotation.concurrent.Immutable;
import oshi.annotation.concurrent.ThreadSafe;
import oshi.util.FileUtil;
import oshi.util.Memoizer;
import oshi.util.ParseUtil;
import oshi.util.Util;

@ThreadSafe
public interface CentralProcessor {
   ProcessorIdentifier getProcessorIdentifier();

   long getMaxFreq();

   long[] getCurrentFreq();

   List<LogicalProcessor> getLogicalProcessors();

   double getSystemCpuLoadBetweenTicks(long[] var1);

   long[] getSystemCpuLoadTicks();

   double[] getSystemLoadAverage(int var1);

   double[] getProcessorCpuLoadBetweenTicks(long[][] var1);

   long[][] getProcessorCpuLoadTicks();

   int getLogicalProcessorCount();

   int getPhysicalProcessorCount();

   int getPhysicalPackageCount();

   long getContextSwitches();

   long getInterrupts();

   @Immutable
   public static final class ProcessorIdentifier {
      private static final String OSHI_ARCHITECTURE_PROPERTIES = "oshi.architecture.properties";
      private final String cpuVendor;
      private final String cpuName;
      private final String cpuFamily;
      private final String cpuModel;
      private final String cpuStepping;
      private final String processorID;
      private final String cpuIdentifier;
      private final boolean cpu64bit;
      private final long cpuVendorFreq;
      private final Supplier<String> microArchictecture;

      public ProcessorIdentifier(String cpuVendor, String cpuName, String cpuFamily, String cpuModel, String cpuStepping, String processorID, boolean cpu64bit) {
         this(cpuVendor, cpuName, cpuFamily, cpuModel, cpuStepping, processorID, cpu64bit, -1L);
      }

      public ProcessorIdentifier(String cpuVendor, String cpuName, String cpuFamily, String cpuModel, String cpuStepping, String processorID, boolean cpu64bit, long vendorFreq) {
         this.microArchictecture = Memoizer.memoize(this::queryMicroarchitecture);
         this.cpuVendor = cpuVendor;
         this.cpuName = cpuName;
         this.cpuFamily = cpuFamily;
         this.cpuModel = cpuModel;
         this.cpuStepping = cpuStepping;
         this.processorID = processorID;
         this.cpu64bit = cpu64bit;
         StringBuilder sb = new StringBuilder();
         if (cpuVendor.contentEquals("GenuineIntel")) {
            sb.append(cpu64bit ? "Intel64" : "x86");
         } else {
            sb.append(cpuVendor);
         }

         sb.append(" Family ").append(cpuFamily);
         sb.append(" Model ").append(cpuModel);
         sb.append(" Stepping ").append(cpuStepping);
         this.cpuIdentifier = sb.toString();
         if (vendorFreq >= 0L) {
            this.cpuVendorFreq = vendorFreq;
         } else {
            Pattern pattern = Pattern.compile("@ (.*)$");
            Matcher matcher = pattern.matcher(cpuName);
            if (matcher.find()) {
               String unit = matcher.group(1);
               this.cpuVendorFreq = ParseUtil.parseHertz(unit);
            } else {
               this.cpuVendorFreq = -1L;
            }
         }

      }

      public String getVendor() {
         return this.cpuVendor;
      }

      public String getName() {
         return this.cpuName;
      }

      public String getFamily() {
         return this.cpuFamily;
      }

      public String getModel() {
         return this.cpuModel;
      }

      public String getStepping() {
         return this.cpuStepping;
      }

      public String getProcessorID() {
         return this.processorID;
      }

      public String getIdentifier() {
         return this.cpuIdentifier;
      }

      public boolean isCpu64bit() {
         return this.cpu64bit;
      }

      public long getVendorFreq() {
         return this.cpuVendorFreq;
      }

      public String getMicroarchitecture() {
         return (String)this.microArchictecture.get();
      }

      private String queryMicroarchitecture() {
         String arch = null;
         Properties archProps = FileUtil.readPropertiesFromFilename("oshi.architecture.properties");
         StringBuilder sb = new StringBuilder();
         if (this.cpuVendor.toUpperCase().contains("AMD")) {
            sb.append("amd.");
         } else if (this.getVendor().toUpperCase().contains("ARM")) {
            sb.append("arm.");
         } else if (this.getVendor().toUpperCase().contains("IBM")) {
            int powerIdx = this.cpuName.indexOf("_POWER");
            if (powerIdx > 0) {
               arch = this.cpuName.substring(powerIdx + 1);
            }
         }

         if (Util.isBlank(arch) && !sb.toString().equals("arm.")) {
            sb.append(this.cpuFamily);
            arch = archProps.getProperty(sb.toString());
         }

         if (Util.isBlank(arch)) {
            sb.append('.').append(this.cpuModel);
            arch = archProps.getProperty(sb.toString());
         }

         if (Util.isBlank(arch)) {
            sb.append('.').append(this.cpuStepping);
            arch = archProps.getProperty(sb.toString());
         }

         return Util.isBlank(arch) ? "unknown" : arch;
      }

      public String toString() {
         return this.getIdentifier();
      }
   }

   @Immutable
   public static class LogicalProcessor {
      private final int processorNumber;
      private final int physicalProcessorNumber;
      private final int physicalPackageNumber;
      private final int numaNode;
      private final int processorGroup;

      public LogicalProcessor(int processorNumber, int physicalProcessorNumber, int physicalPackageNumber) {
         this(processorNumber, physicalProcessorNumber, physicalPackageNumber, 0, 0);
      }

      public LogicalProcessor(int processorNumber, int physicalProcessorNumber, int physicalPackageNumber, int numaNode) {
         this(processorNumber, physicalProcessorNumber, physicalPackageNumber, numaNode, 0);
      }

      public LogicalProcessor(int processorNumber, int physicalProcessorNumber, int physicalPackageNumber, int numaNode, int processorGroup) {
         this.processorNumber = processorNumber;
         this.physicalProcessorNumber = physicalProcessorNumber;
         this.physicalPackageNumber = physicalPackageNumber;
         this.numaNode = numaNode;
         this.processorGroup = processorGroup;
      }

      public int getProcessorNumber() {
         return this.processorNumber;
      }

      public int getPhysicalProcessorNumber() {
         return this.physicalProcessorNumber;
      }

      public int getPhysicalPackageNumber() {
         return this.physicalPackageNumber;
      }

      public int getNumaNode() {
         return this.numaNode;
      }

      public int getProcessorGroup() {
         return this.processorGroup;
      }
   }

   public static enum TickType {
      USER(0),
      NICE(1),
      SYSTEM(2),
      IDLE(3),
      IOWAIT(4),
      IRQ(5),
      SOFTIRQ(6),
      STEAL(7);

      private int index;

      private TickType(int value) {
         this.index = value;
      }

      public int getIndex() {
         return this.index;
      }
   }
}
