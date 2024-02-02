package cn.hutool.core.swing.clipboard;

import java.awt.Image;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.Serializable;

public class ImageSelection implements Transferable, Serializable {
   private static final long serialVersionUID = 1L;
   private final Image image;

   public ImageSelection(Image image) {
      this.image = image;
   }

   public DataFlavor[] getTransferDataFlavors() {
      return new DataFlavor[]{DataFlavor.imageFlavor};
   }

   public boolean isDataFlavorSupported(DataFlavor flavor) {
      return DataFlavor.imageFlavor.equals(flavor);
   }

   public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
      if (!DataFlavor.imageFlavor.equals(flavor)) {
         throw new UnsupportedFlavorException(flavor);
      } else {
         return this.image;
      }
   }
}
