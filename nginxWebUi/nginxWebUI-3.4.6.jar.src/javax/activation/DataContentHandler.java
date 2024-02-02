package javax.activation;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.OutputStream;

public interface DataContentHandler {
  DataFlavor[] getTransferDataFlavors();
  
  Object getTransferData(DataFlavor paramDataFlavor, DataSource paramDataSource) throws UnsupportedFlavorException, IOException;
  
  Object getContent(DataSource paramDataSource) throws IOException;
  
  void writeTo(Object paramObject, String paramString, OutputStream paramOutputStream) throws IOException;
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\activation\DataContentHandler.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */