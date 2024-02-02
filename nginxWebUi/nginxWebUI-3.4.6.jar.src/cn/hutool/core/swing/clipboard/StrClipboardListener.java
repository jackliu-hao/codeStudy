/*    */ package cn.hutool.core.swing.clipboard;
/*    */ 
/*    */ import java.awt.datatransfer.Clipboard;
/*    */ import java.awt.datatransfer.DataFlavor;
/*    */ import java.awt.datatransfer.Transferable;
/*    */ import java.io.Serializable;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public abstract class StrClipboardListener
/*    */   implements ClipboardListener, Serializable
/*    */ {
/*    */   private static final long serialVersionUID = 1L;
/*    */   
/*    */   public Transferable onChange(Clipboard clipboard, Transferable contents) {
/* 19 */     if (contents.isDataFlavorSupported(DataFlavor.stringFlavor)) {
/* 20 */       return onChange(clipboard, ClipboardUtil.getStr(contents));
/*    */     }
/* 22 */     return null;
/*    */   }
/*    */   
/*    */   public abstract Transferable onChange(Clipboard paramClipboard, String paramString);
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\swing\clipboard\StrClipboardListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */