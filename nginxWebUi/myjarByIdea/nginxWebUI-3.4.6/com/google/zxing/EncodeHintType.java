package com.google.zxing;

public enum EncodeHintType {
   ERROR_CORRECTION,
   CHARACTER_SET,
   DATA_MATRIX_SHAPE,
   /** @deprecated */
   @Deprecated
   MIN_SIZE,
   /** @deprecated */
   @Deprecated
   MAX_SIZE,
   MARGIN,
   PDF417_COMPACT,
   PDF417_COMPACTION,
   PDF417_DIMENSIONS,
   AZTEC_LAYERS,
   QR_VERSION;
}
