package com.google.zxing.client.j2se;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.validators.PositiveInteger;
import com.google.zxing.BarcodeFormat;
import java.util.List;

final class EncoderConfig {
   static final String DEFAULT_OUTPUT_FILE_BASE = "out";
   @Parameter(
      names = {"--barcode_format"},
      description = "Format to encode, from BarcodeFormat class. Not all formats are supported"
   )
   BarcodeFormat barcodeFormat;
   @Parameter(
      names = {"--image_format"},
      description = "Image output format, such as PNG, JPG, GIF"
   )
   String imageFormat;
   @Parameter(
      names = {"--output"},
      description = "File to write to. Defaults to out.png"
   )
   String outputFileBase;
   @Parameter(
      names = {"--width"},
      description = "Image width",
      validateWith = PositiveInteger.class
   )
   int width;
   @Parameter(
      names = {"--height"},
      description = "Image height",
      validateWith = PositiveInteger.class
   )
   int height;
   @Parameter(
      names = {"--error_correction_level"},
      description = "Error correction level for the encoding"
   )
   String errorCorrectionLevel;
   @Parameter(
      names = {"--help"},
      description = "Prints this help message",
      help = true
   )
   boolean help;
   @Parameter(
      description = "(Text to encode)",
      required = true
   )
   List<String> contents;

   EncoderConfig() {
      this.barcodeFormat = BarcodeFormat.QR_CODE;
      this.imageFormat = "PNG";
      this.outputFileBase = "out";
      this.width = 300;
      this.height = 300;
      this.errorCorrectionLevel = null;
   }
}
