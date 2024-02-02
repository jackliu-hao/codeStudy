package com.google.zxing.datamatrix.encoder;

interface Encoder {
   int getEncodingMode();

   void encode(EncoderContext var1);
}
