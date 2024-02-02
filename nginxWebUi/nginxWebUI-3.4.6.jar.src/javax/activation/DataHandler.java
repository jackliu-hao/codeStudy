/*     */ package javax.activation;
/*     */ 
/*     */ import java.awt.datatransfer.DataFlavor;
/*     */ import java.awt.datatransfer.Transferable;
/*     */ import java.awt.datatransfer.UnsupportedFlavorException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.PipedInputStream;
/*     */ import java.io.PipedOutputStream;
/*     */ import java.net.URL;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class DataHandler
/*     */   implements Transferable
/*     */ {
/*  80 */   private DataSource dataSource = null;
/*  81 */   private DataSource objDataSource = null;
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*  86 */   private Object object = null;
/*  87 */   private String objectMimeType = null;
/*     */ 
/*     */   
/*  90 */   private CommandMap currentCommandMap = null;
/*     */ 
/*     */   
/*  93 */   private static final DataFlavor[] emptyFlavors = new DataFlavor[0];
/*  94 */   private DataFlavor[] transferFlavors = emptyFlavors;
/*     */ 
/*     */   
/*  97 */   private DataContentHandler dataContentHandler = null;
/*  98 */   private DataContentHandler factoryDCH = null;
/*     */ 
/*     */   
/* 101 */   private static DataContentHandlerFactory factory = null;
/* 102 */   private DataContentHandlerFactory oldFactory = null;
/*     */   
/* 104 */   private String shortType = null;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DataHandler(DataSource ds) {
/* 115 */     this.dataSource = ds;
/* 116 */     this.oldFactory = factory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DataHandler(Object obj, String mimeType) {
/* 129 */     this.object = obj;
/* 130 */     this.objectMimeType = mimeType;
/* 131 */     this.oldFactory = factory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DataHandler(URL url) {
/* 142 */     this.dataSource = new URLDataSource(url);
/* 143 */     this.oldFactory = factory;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private synchronized CommandMap getCommandMap() {
/* 150 */     if (this.currentCommandMap != null) {
/* 151 */       return this.currentCommandMap;
/*     */     }
/* 153 */     return CommandMap.getDefaultCommandMap();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public DataSource getDataSource() {
/* 171 */     if (this.dataSource == null) {
/*     */       
/* 173 */       if (this.objDataSource == null)
/* 174 */         this.objDataSource = new DataHandlerDataSource(this); 
/* 175 */       return this.objDataSource;
/*     */     } 
/* 177 */     return this.dataSource;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getName() {
/* 189 */     if (this.dataSource != null) {
/* 190 */       return this.dataSource.getName();
/*     */     }
/* 192 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getContentType() {
/* 203 */     if (this.dataSource != null) {
/* 204 */       return this.dataSource.getContentType();
/*     */     }
/* 206 */     return this.objectMimeType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public InputStream getInputStream() throws IOException {
/* 234 */     InputStream ins = null;
/*     */     
/* 236 */     if (this.dataSource != null) {
/* 237 */       ins = this.dataSource.getInputStream();
/*     */     } else {
/* 239 */       DataContentHandler dch = getDataContentHandler();
/*     */       
/* 241 */       if (dch == null) {
/* 242 */         throw new UnsupportedDataTypeException("no DCH for MIME type " + getBaseType());
/*     */       }
/*     */       
/* 245 */       if (dch instanceof ObjectDataContentHandler && (
/* 246 */         (ObjectDataContentHandler)dch).getDCH() == null) {
/* 247 */         throw new UnsupportedDataTypeException("no object DCH for MIME type " + getBaseType());
/*     */       }
/*     */ 
/*     */       
/* 251 */       final DataContentHandler fdch = dch;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */       
/* 259 */       final PipedOutputStream pos = new PipedOutputStream();
/* 260 */       PipedInputStream pin = new PipedInputStream(pos);
/* 261 */       (new Thread(new Runnable() { private final DataContentHandler val$fdch; private final PipedOutputStream val$pos; private final DataHandler this$0;
/*     */             
/*     */             public void run() {
/*     */               
/* 265 */               try { fdch.writeTo(DataHandler.this.object, DataHandler.this.objectMimeType, pos); }
/* 266 */               catch (IOException e)
/*     */               
/*     */               { 
/*     */                 try {
/* 270 */                   pos.close();
/* 271 */                 } catch (IOException ie) {} } finally { try { pos.close(); } catch (IOException ie) {} }
/*     */             
/*     */             } }
/*     */           "DataHandler.getInputStream")).start();
/*     */       
/* 276 */       ins = pin;
/*     */     } 
/*     */     
/* 279 */     return ins;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void writeTo(OutputStream os) throws IOException {
/* 299 */     if (this.dataSource != null) {
/* 300 */       InputStream is = null;
/* 301 */       byte[] data = new byte[8192];
/*     */ 
/*     */       
/* 304 */       is = this.dataSource.getInputStream();
/*     */       try {
/*     */         int bytes_read;
/* 307 */         while ((bytes_read = is.read(data)) > 0) {
/* 308 */           os.write(data, 0, bytes_read);
/*     */         }
/*     */       } finally {
/* 311 */         is.close();
/* 312 */         is = null;
/*     */       } 
/*     */     } else {
/* 315 */       DataContentHandler dch = getDataContentHandler();
/* 316 */       dch.writeTo(this.object, this.objectMimeType, os);
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public OutputStream getOutputStream() throws IOException {
/* 333 */     if (this.dataSource != null) {
/* 334 */       return this.dataSource.getOutputStream();
/*     */     }
/* 336 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized DataFlavor[] getTransferDataFlavors() {
/* 364 */     if (factory != this.oldFactory) {
/* 365 */       this.transferFlavors = emptyFlavors;
/*     */     }
/*     */     
/* 368 */     if (this.transferFlavors == emptyFlavors)
/* 369 */       this.transferFlavors = getDataContentHandler().getTransferDataFlavors(); 
/* 370 */     return this.transferFlavors;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean isDataFlavorSupported(DataFlavor flavor) {
/* 386 */     DataFlavor[] lFlavors = getTransferDataFlavors();
/*     */     
/* 388 */     for (int i = 0; i < lFlavors.length; i++) {
/* 389 */       if (lFlavors[i].equals(flavor))
/* 390 */         return true; 
/*     */     } 
/* 392 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
/* 430 */     return getDataContentHandler().getTransferData(flavor, this.dataSource);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public synchronized void setCommandMap(CommandMap commandMap) {
/* 446 */     if (commandMap != this.currentCommandMap || commandMap == null) {
/*     */       
/* 448 */       this.transferFlavors = emptyFlavors;
/* 449 */       this.dataContentHandler = null;
/*     */       
/* 451 */       this.currentCommandMap = commandMap;
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CommandInfo[] getPreferredCommands() {
/* 469 */     if (this.dataSource != null) {
/* 470 */       return getCommandMap().getPreferredCommands(getBaseType(), this.dataSource);
/*     */     }
/*     */     
/* 473 */     return getCommandMap().getPreferredCommands(getBaseType());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CommandInfo[] getAllCommands() {
/* 489 */     if (this.dataSource != null) {
/* 490 */       return getCommandMap().getAllCommands(getBaseType(), this.dataSource);
/*     */     }
/* 492 */     return getCommandMap().getAllCommands(getBaseType());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public CommandInfo getCommand(String cmdName) {
/* 508 */     if (this.dataSource != null) {
/* 509 */       return getCommandMap().getCommand(getBaseType(), cmdName, this.dataSource);
/*     */     }
/*     */     
/* 512 */     return getCommandMap().getCommand(getBaseType(), cmdName);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getContent() throws IOException {
/* 533 */     if (this.object != null) {
/* 534 */       return this.object;
/*     */     }
/* 536 */     return getDataContentHandler().getContent(getDataSource());
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object getBean(CommandInfo cmdinfo) {
/* 552 */     Object bean = null;
/*     */ 
/*     */ 
/*     */     
/* 556 */     try { ClassLoader cld = null;
/*     */       
/* 558 */       cld = SecuritySupport.getContextClassLoader();
/* 559 */       if (cld == null)
/* 560 */         cld = getClass().getClassLoader(); 
/* 561 */       bean = cmdinfo.getCommandObject(this, cld); }
/* 562 */     catch (IOException e) {  }
/* 563 */     catch (ClassNotFoundException e) {}
/*     */     
/* 565 */     return bean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private synchronized DataContentHandler getDataContentHandler() {
/* 588 */     if (factory != this.oldFactory) {
/* 589 */       this.oldFactory = factory;
/* 590 */       this.factoryDCH = null;
/* 591 */       this.dataContentHandler = null;
/* 592 */       this.transferFlavors = emptyFlavors;
/*     */     } 
/*     */     
/* 595 */     if (this.dataContentHandler != null) {
/* 596 */       return this.dataContentHandler;
/*     */     }
/* 598 */     String simpleMT = getBaseType();
/*     */     
/* 600 */     if (this.factoryDCH == null && factory != null) {
/* 601 */       this.factoryDCH = factory.createDataContentHandler(simpleMT);
/*     */     }
/* 603 */     if (this.factoryDCH != null) {
/* 604 */       this.dataContentHandler = this.factoryDCH;
/*     */     }
/* 606 */     if (this.dataContentHandler == null) {
/* 607 */       if (this.dataSource != null) {
/* 608 */         this.dataContentHandler = getCommandMap().createDataContentHandler(simpleMT, this.dataSource);
/*     */       } else {
/*     */         
/* 611 */         this.dataContentHandler = getCommandMap().createDataContentHandler(simpleMT);
/*     */       } 
/*     */     }
/*     */ 
/*     */ 
/*     */     
/* 617 */     if (this.dataSource != null) {
/* 618 */       this.dataContentHandler = new DataSourceDataContentHandler(this.dataContentHandler, this.dataSource);
/*     */     }
/*     */     else {
/*     */       
/* 622 */       this.dataContentHandler = new ObjectDataContentHandler(this.dataContentHandler, this.object, this.objectMimeType);
/*     */     } 
/*     */ 
/*     */     
/* 626 */     return this.dataContentHandler;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private synchronized String getBaseType() {
/* 634 */     if (this.shortType == null) {
/* 635 */       String ct = getContentType();
/*     */       try {
/* 637 */         MimeType mt = new MimeType(ct);
/* 638 */         this.shortType = mt.getBaseType();
/* 639 */       } catch (MimeTypeParseException e) {
/* 640 */         this.shortType = ct;
/*     */       } 
/*     */     } 
/* 643 */     return this.shortType;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static synchronized void setDataContentHandlerFactory(DataContentHandlerFactory newFactory) {
/* 661 */     if (factory != null) {
/* 662 */       throw new Error("DataContentHandlerFactory already defined");
/*     */     }
/* 664 */     SecurityManager security = System.getSecurityManager();
/* 665 */     if (security != null)
/*     */       
/*     */       try {
/* 668 */         security.checkSetFactory();
/* 669 */       } catch (SecurityException ex) {
/*     */ 
/*     */ 
/*     */         
/* 673 */         if (DataHandler.class.getClassLoader() != newFactory.getClass().getClassLoader())
/*     */         {
/* 675 */           throw ex;
/*     */         }
/*     */       }  
/* 678 */     factory = newFactory;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\javax\activation\DataHandler.class
 * Java compiler version: 4 (48.0)
 * JD-Core Version:       1.1.3
 */