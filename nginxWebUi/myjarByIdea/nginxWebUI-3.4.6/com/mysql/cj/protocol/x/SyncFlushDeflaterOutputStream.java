package com.mysql.cj.protocol.x;

import java.io.OutputStream;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

public class SyncFlushDeflaterOutputStream extends DeflaterOutputStream {
   public SyncFlushDeflaterOutputStream(OutputStream out) {
      super(out, new Deflater(), true);
   }
}
