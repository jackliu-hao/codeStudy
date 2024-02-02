package com.google.zxing.datamatrix.encoder;

interface Encoder {
  int getEncodingMode();
  
  void encode(EncoderContext paramEncoderContext);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\zxing\datamatrix\encoder\Encoder.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */