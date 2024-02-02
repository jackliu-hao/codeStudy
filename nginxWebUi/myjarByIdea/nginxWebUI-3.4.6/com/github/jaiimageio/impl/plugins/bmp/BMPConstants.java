package com.github.jaiimageio.impl.plugins.bmp;

public interface BMPConstants {
   String VERSION_2 = "BMP v. 2.x";
   String VERSION_3 = "BMP v. 3.x";
   String VERSION_3_NT = "BMP v. 3.x NT";
   String VERSION_4 = "BMP v. 4.x";
   String VERSION_5 = "BMP v. 5.x";
   int LCS_CALIBRATED_RGB = 0;
   int LCS_sRGB = 1;
   int LCS_WINDOWS_COLOR_SPACE = 2;
   int PROFILE_LINKED = 3;
   int PROFILE_EMBEDDED = 4;
   int BI_RGB = 0;
   int BI_RLE8 = 1;
   int BI_RLE4 = 2;
   int BI_BITFIELDS = 3;
   int BI_JPEG = 4;
   int BI_PNG = 5;
   String[] compressionTypeNames = new String[]{"BI_RGB", "BI_RLE8", "BI_RLE4", "BI_BITFIELDS", "BI_JPEG", "BI_PNG"};
}
