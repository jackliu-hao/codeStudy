package javax.activation;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.OutputStream;

public interface DataContentHandler {
   DataFlavor[] getTransferDataFlavors();

   Object getTransferData(DataFlavor var1, DataSource var2) throws UnsupportedFlavorException, IOException;

   Object getContent(DataSource var1) throws IOException;

   void writeTo(Object var1, String var2, OutputStream var3) throws IOException;
}
