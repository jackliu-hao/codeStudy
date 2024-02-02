package javax.activation;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.OutputStream;

class ObjectDataContentHandler implements DataContentHandler {
   private DataFlavor[] transferFlavors = null;
   private Object obj;
   private String mimeType;
   private DataContentHandler dch = null;

   public ObjectDataContentHandler(DataContentHandler dch, Object obj, String mimeType) {
      this.obj = obj;
      this.mimeType = mimeType;
      this.dch = dch;
   }

   public DataContentHandler getDCH() {
      return this.dch;
   }

   public DataFlavor[] getTransferDataFlavors() {
      if (this.transferFlavors == null) {
         if (this.dch != null) {
            this.transferFlavors = this.dch.getTransferDataFlavors();
         } else {
            this.transferFlavors = new DataFlavor[1];
            this.transferFlavors[0] = new ActivationDataFlavor(this.obj.getClass(), this.mimeType, this.mimeType);
         }
      }

      return this.transferFlavors;
   }

   public Object getTransferData(DataFlavor df, DataSource ds) throws UnsupportedFlavorException, IOException {
      if (this.dch != null) {
         return this.dch.getTransferData(df, ds);
      } else if (df.equals(this.transferFlavors[0])) {
         return this.obj;
      } else {
         throw new UnsupportedFlavorException(df);
      }
   }

   public Object getContent(DataSource ds) {
      return this.obj;
   }

   public void writeTo(Object obj, String mimeType, OutputStream os) throws IOException {
      if (this.dch != null) {
         this.dch.writeTo(obj, mimeType, os);
      } else {
         throw new UnsupportedDataTypeException("no object DCH for MIME type " + this.mimeType);
      }
   }
}
