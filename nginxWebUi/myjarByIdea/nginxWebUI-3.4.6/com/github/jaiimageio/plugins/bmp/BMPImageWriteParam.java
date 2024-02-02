package com.github.jaiimageio.plugins.bmp;

import com.github.jaiimageio.impl.plugins.bmp.BMPConstants;
import java.util.Locale;
import javax.imageio.ImageWriteParam;

public class BMPImageWriteParam extends ImageWriteParam {
   /** @deprecated */
   public static final int VERSION_2 = 0;
   /** @deprecated */
   public static final int VERSION_3 = 1;
   /** @deprecated */
   public static final int VERSION_4 = 2;
   /** @deprecated */
   public static final int VERSION_5 = 3;
   private boolean topDown;

   public BMPImageWriteParam(Locale locale) {
      super(locale);
      this.topDown = false;
      this.compressionTypes = BMPConstants.compressionTypeNames;
      this.canWriteCompressed = true;
      this.compressionMode = 3;
      this.compressionType = this.compressionTypes[0];
   }

   public BMPImageWriteParam() {
      this((Locale)null);
   }

   /** @deprecated */
   public int getVersion() {
      return 1;
   }

   public void setTopDown(boolean topDown) {
      this.topDown = topDown;
   }

   public boolean isTopDown() {
      return this.topDown;
   }

   public void setCompressionType(String compressionType) {
      super.setCompressionType(compressionType);
      if (!compressionType.equals("BI_RGB") && !compressionType.equals("BI_BITFIELDS") && this.topDown) {
         this.topDown = false;
      }

   }
}
