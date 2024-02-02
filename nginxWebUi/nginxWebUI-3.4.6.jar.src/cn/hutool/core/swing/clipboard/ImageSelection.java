/*    */ package cn.hutool.core.swing.clipboard;
/*    */ 
/*    */ import java.awt.Image;
/*    */ import java.awt.datatransfer.DataFlavor;
/*    */ import java.awt.datatransfer.Transferable;
/*    */ import java.awt.datatransfer.UnsupportedFlavorException;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ImageSelection
/*    */   implements Transferable, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   private final Image image;
/*    */   
/*    */   public ImageSelection(Image image) {
/* 27 */     this.image = image;
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public DataFlavor[] getTransferDataFlavors() {
/* 37 */     return new DataFlavor[] { DataFlavor.imageFlavor };
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public boolean isDataFlavorSupported(DataFlavor flavor) {
/* 48 */     return DataFlavor.imageFlavor.equals(flavor);
/*    */   }
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */   
/*    */   public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
/* 59 */     if (false == DataFlavor.imageFlavor.equals(flavor)) {
/* 60 */       throw new UnsupportedFlavorException(flavor);
/*    */     }
/* 62 */     return this.image;
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\swing\clipboard\ImageSelection.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */