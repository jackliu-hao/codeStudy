package javax.activation;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.net.URL;

public class DataHandler implements Transferable {
   private DataSource dataSource = null;
   private DataSource objDataSource = null;
   private Object object = null;
   private String objectMimeType = null;
   private CommandMap currentCommandMap = null;
   private static final DataFlavor[] emptyFlavors = new DataFlavor[0];
   private DataFlavor[] transferFlavors;
   private DataContentHandler dataContentHandler;
   private DataContentHandler factoryDCH;
   private static DataContentHandlerFactory factory = null;
   private DataContentHandlerFactory oldFactory;
   private String shortType;

   public DataHandler(DataSource ds) {
      this.transferFlavors = emptyFlavors;
      this.dataContentHandler = null;
      this.factoryDCH = null;
      this.oldFactory = null;
      this.shortType = null;
      this.dataSource = ds;
      this.oldFactory = factory;
   }

   public DataHandler(Object obj, String mimeType) {
      this.transferFlavors = emptyFlavors;
      this.dataContentHandler = null;
      this.factoryDCH = null;
      this.oldFactory = null;
      this.shortType = null;
      this.object = obj;
      this.objectMimeType = mimeType;
      this.oldFactory = factory;
   }

   public DataHandler(URL url) {
      this.transferFlavors = emptyFlavors;
      this.dataContentHandler = null;
      this.factoryDCH = null;
      this.oldFactory = null;
      this.shortType = null;
      this.dataSource = new URLDataSource(url);
      this.oldFactory = factory;
   }

   private synchronized CommandMap getCommandMap() {
      return this.currentCommandMap != null ? this.currentCommandMap : CommandMap.getDefaultCommandMap();
   }

   public DataSource getDataSource() {
      if (this.dataSource == null) {
         if (this.objDataSource == null) {
            this.objDataSource = new DataHandlerDataSource(this);
         }

         return this.objDataSource;
      } else {
         return this.dataSource;
      }
   }

   public String getName() {
      return this.dataSource != null ? this.dataSource.getName() : null;
   }

   public String getContentType() {
      return this.dataSource != null ? this.dataSource.getContentType() : this.objectMimeType;
   }

   public InputStream getInputStream() throws IOException {
      InputStream ins = null;
      if (this.dataSource != null) {
         ins = this.dataSource.getInputStream();
      } else {
         final DataContentHandler dch = this.getDataContentHandler();
         if (dch == null) {
            throw new UnsupportedDataTypeException("no DCH for MIME type " + this.getBaseType());
         }

         if (dch instanceof ObjectDataContentHandler && ((ObjectDataContentHandler)dch).getDCH() == null) {
            throw new UnsupportedDataTypeException("no object DCH for MIME type " + this.getBaseType());
         }

         final PipedOutputStream pos = new PipedOutputStream();
         PipedInputStream pin = new PipedInputStream(pos);
         (new Thread(new Runnable() {
            public void run() {
               try {
                  dch.writeTo(DataHandler.this.object, DataHandler.this.objectMimeType, pos);
               } catch (IOException var10) {
               } finally {
                  try {
                     pos.close();
                  } catch (IOException var9) {
                  }

               }

            }
         }, "DataHandler.getInputStream")).start();
         ins = pin;
      }

      return (InputStream)ins;
   }

   public void writeTo(OutputStream os) throws IOException {
      if (this.dataSource != null) {
         InputStream is = null;
         byte[] data = new byte[8192];
         is = this.dataSource.getInputStream();

         int bytes_read;
         try {
            while((bytes_read = is.read(data)) > 0) {
               os.write(data, 0, bytes_read);
            }
         } finally {
            is.close();
            is = null;
         }
      } else {
         DataContentHandler dch = this.getDataContentHandler();
         dch.writeTo(this.object, this.objectMimeType, os);
      }

   }

   public OutputStream getOutputStream() throws IOException {
      return this.dataSource != null ? this.dataSource.getOutputStream() : null;
   }

   public synchronized DataFlavor[] getTransferDataFlavors() {
      if (factory != this.oldFactory) {
         this.transferFlavors = emptyFlavors;
      }

      if (this.transferFlavors == emptyFlavors) {
         this.transferFlavors = this.getDataContentHandler().getTransferDataFlavors();
      }

      return this.transferFlavors;
   }

   public boolean isDataFlavorSupported(DataFlavor flavor) {
      DataFlavor[] lFlavors = this.getTransferDataFlavors();

      for(int i = 0; i < lFlavors.length; ++i) {
         if (lFlavors[i].equals(flavor)) {
            return true;
         }
      }

      return false;
   }

   public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
      return this.getDataContentHandler().getTransferData(flavor, this.dataSource);
   }

   public synchronized void setCommandMap(CommandMap commandMap) {
      if (commandMap != this.currentCommandMap || commandMap == null) {
         this.transferFlavors = emptyFlavors;
         this.dataContentHandler = null;
         this.currentCommandMap = commandMap;
      }

   }

   public CommandInfo[] getPreferredCommands() {
      return this.dataSource != null ? this.getCommandMap().getPreferredCommands(this.getBaseType(), this.dataSource) : this.getCommandMap().getPreferredCommands(this.getBaseType());
   }

   public CommandInfo[] getAllCommands() {
      return this.dataSource != null ? this.getCommandMap().getAllCommands(this.getBaseType(), this.dataSource) : this.getCommandMap().getAllCommands(this.getBaseType());
   }

   public CommandInfo getCommand(String cmdName) {
      return this.dataSource != null ? this.getCommandMap().getCommand(this.getBaseType(), cmdName, this.dataSource) : this.getCommandMap().getCommand(this.getBaseType(), cmdName);
   }

   public Object getContent() throws IOException {
      return this.object != null ? this.object : this.getDataContentHandler().getContent(this.getDataSource());
   }

   public Object getBean(CommandInfo cmdinfo) {
      Object bean = null;

      try {
         ClassLoader cld = null;
         cld = SecuritySupport.getContextClassLoader();
         if (cld == null) {
            cld = this.getClass().getClassLoader();
         }

         bean = cmdinfo.getCommandObject(this, cld);
      } catch (IOException var4) {
      } catch (ClassNotFoundException var5) {
      }

      return bean;
   }

   private synchronized DataContentHandler getDataContentHandler() {
      if (factory != this.oldFactory) {
         this.oldFactory = factory;
         this.factoryDCH = null;
         this.dataContentHandler = null;
         this.transferFlavors = emptyFlavors;
      }

      if (this.dataContentHandler != null) {
         return this.dataContentHandler;
      } else {
         String simpleMT = this.getBaseType();
         if (this.factoryDCH == null && factory != null) {
            this.factoryDCH = factory.createDataContentHandler(simpleMT);
         }

         if (this.factoryDCH != null) {
            this.dataContentHandler = this.factoryDCH;
         }

         if (this.dataContentHandler == null) {
            if (this.dataSource != null) {
               this.dataContentHandler = this.getCommandMap().createDataContentHandler(simpleMT, this.dataSource);
            } else {
               this.dataContentHandler = this.getCommandMap().createDataContentHandler(simpleMT);
            }
         }

         if (this.dataSource != null) {
            this.dataContentHandler = new DataSourceDataContentHandler(this.dataContentHandler, this.dataSource);
         } else {
            this.dataContentHandler = new ObjectDataContentHandler(this.dataContentHandler, this.object, this.objectMimeType);
         }

         return this.dataContentHandler;
      }
   }

   private synchronized String getBaseType() {
      if (this.shortType == null) {
         String ct = this.getContentType();

         try {
            MimeType mt = new MimeType(ct);
            this.shortType = mt.getBaseType();
         } catch (MimeTypeParseException var3) {
            this.shortType = ct;
         }
      }

      return this.shortType;
   }

   public static synchronized void setDataContentHandlerFactory(DataContentHandlerFactory newFactory) {
      if (factory != null) {
         throw new Error("DataContentHandlerFactory already defined");
      } else {
         SecurityManager security = System.getSecurityManager();
         if (security != null) {
            try {
               security.checkSetFactory();
            } catch (SecurityException var3) {
               if (DataHandler.class.getClassLoader() != newFactory.getClass().getClassLoader()) {
                  throw var3;
               }
            }
         }

         factory = newFactory;
      }
   }
}
