package ch.qos.logback.core.rolling;

import ch.qos.logback.core.rolling.helper.CompressionMode;
import ch.qos.logback.core.rolling.helper.Compressor;
import ch.qos.logback.core.rolling.helper.FileFilterUtil;
import ch.qos.logback.core.rolling.helper.FileNamePattern;
import ch.qos.logback.core.rolling.helper.IntegerTokenConverter;
import ch.qos.logback.core.rolling.helper.RenameUtil;
import java.io.File;
import java.util.Date;

public class FixedWindowRollingPolicy extends RollingPolicyBase {
   static final String FNP_NOT_SET = "The \"FileNamePattern\" property must be set before using FixedWindowRollingPolicy. ";
   static final String PRUDENT_MODE_UNSUPPORTED = "See also http://logback.qos.ch/codes.html#tbr_fnp_prudent_unsupported";
   static final String SEE_PARENT_FN_NOT_SET = "Please refer to http://logback.qos.ch/codes.html#fwrp_parentFileName_not_set";
   int maxIndex = 7;
   int minIndex = 1;
   RenameUtil util = new RenameUtil();
   Compressor compressor;
   public static final String ZIP_ENTRY_DATE_PATTERN = "yyyy-MM-dd_HHmm";
   private static int MAX_WINDOW_SIZE = 20;

   public void start() {
      this.util.setContext(this.context);
      if (this.fileNamePatternStr != null) {
         this.fileNamePattern = new FileNamePattern(this.fileNamePatternStr, this.context);
         this.determineCompressionMode();
         if (this.isParentPrudent()) {
            this.addError("Prudent mode is not supported with FixedWindowRollingPolicy.");
            this.addError("See also http://logback.qos.ch/codes.html#tbr_fnp_prudent_unsupported");
            throw new IllegalStateException("Prudent mode is not supported.");
         } else if (this.getParentsRawFileProperty() == null) {
            this.addError("The File name property must be set before using this rolling policy.");
            this.addError("Please refer to http://logback.qos.ch/codes.html#fwrp_parentFileName_not_set");
            throw new IllegalStateException("The \"File\" option must be set.");
         } else {
            if (this.maxIndex < this.minIndex) {
               this.addWarn("MaxIndex (" + this.maxIndex + ") cannot be smaller than MinIndex (" + this.minIndex + ").");
               this.addWarn("Setting maxIndex to equal minIndex.");
               this.maxIndex = this.minIndex;
            }

            int maxWindowSize = this.getMaxWindowSize();
            if (this.maxIndex - this.minIndex > maxWindowSize) {
               this.addWarn("Large window sizes are not allowed.");
               this.maxIndex = this.minIndex + maxWindowSize;
               this.addWarn("MaxIndex reduced to " + this.maxIndex);
            }

            IntegerTokenConverter itc = this.fileNamePattern.getIntegerTokenConverter();
            if (itc == null) {
               throw new IllegalStateException("FileNamePattern [" + this.fileNamePattern.getPattern() + "] does not contain a valid IntegerToken");
            } else {
               if (this.compressionMode == CompressionMode.ZIP) {
                  String zipEntryFileNamePatternStr = this.transformFileNamePatternFromInt2Date(this.fileNamePatternStr);
                  this.zipEntryFileNamePattern = new FileNamePattern(zipEntryFileNamePatternStr, this.context);
               }

               this.compressor = new Compressor(this.compressionMode);
               this.compressor.setContext(this.context);
               super.start();
            }
         }
      } else {
         this.addError("The \"FileNamePattern\" property must be set before using FixedWindowRollingPolicy. ");
         this.addError("See also http://logback.qos.ch/codes.html#tbr_fnp_not_set");
         throw new IllegalStateException("The \"FileNamePattern\" property must be set before using FixedWindowRollingPolicy. See also http://logback.qos.ch/codes.html#tbr_fnp_not_set");
      }
   }

   protected int getMaxWindowSize() {
      return MAX_WINDOW_SIZE;
   }

   private String transformFileNamePatternFromInt2Date(String fileNamePatternStr) {
      String slashified = FileFilterUtil.slashify(fileNamePatternStr);
      String stemOfFileNamePattern = FileFilterUtil.afterLastSlash(slashified);
      return stemOfFileNamePattern.replace("%i", "%d{yyyy-MM-dd_HHmm}");
   }

   public void rollover() throws RolloverFailure {
      if (this.maxIndex >= 0) {
         File file = new File(this.fileNamePattern.convertInt(this.maxIndex));
         if (file.exists()) {
            file.delete();
         }

         for(int i = this.maxIndex - 1; i >= this.minIndex; --i) {
            String toRenameStr = this.fileNamePattern.convertInt(i);
            File toRename = new File(toRenameStr);
            if (toRename.exists()) {
               this.util.rename(toRenameStr, this.fileNamePattern.convertInt(i + 1));
            } else {
               this.addInfo("Skipping roll-over for inexistent file " + toRenameStr);
            }
         }

         switch (this.compressionMode) {
            case NONE:
               this.util.rename(this.getActiveFileName(), this.fileNamePattern.convertInt(this.minIndex));
               break;
            case GZ:
               this.compressor.compress(this.getActiveFileName(), this.fileNamePattern.convertInt(this.minIndex), (String)null);
               break;
            case ZIP:
               this.compressor.compress(this.getActiveFileName(), this.fileNamePattern.convertInt(this.minIndex), this.zipEntryFileNamePattern.convert(new Date()));
         }
      }

   }

   public String getActiveFileName() {
      return this.getParentsRawFileProperty();
   }

   public int getMaxIndex() {
      return this.maxIndex;
   }

   public int getMinIndex() {
      return this.minIndex;
   }

   public void setMaxIndex(int maxIndex) {
      this.maxIndex = maxIndex;
   }

   public void setMinIndex(int minIndex) {
      this.minIndex = minIndex;
   }
}
