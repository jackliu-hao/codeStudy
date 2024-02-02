/*     */ package cn.hutool.core.swing.clipboard;
/*     */ 
/*     */ import cn.hutool.core.exceptions.UtilException;
/*     */ import java.awt.Image;
/*     */ import java.awt.Toolkit;
/*     */ import java.awt.datatransfer.Clipboard;
/*     */ import java.awt.datatransfer.ClipboardOwner;
/*     */ import java.awt.datatransfer.DataFlavor;
/*     */ import java.awt.datatransfer.StringSelection;
/*     */ import java.awt.datatransfer.Transferable;
/*     */ import java.awt.datatransfer.UnsupportedFlavorException;
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
/*     */ public class ClipboardUtil
/*     */ {
/*     */   public static Clipboard getClipboard() {
/*  29 */     return Toolkit.getDefaultToolkit().getSystemClipboard();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void set(Transferable contents) {
/*  38 */     set(contents, null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void set(Transferable contents, ClipboardOwner owner) {
/*  48 */     getClipboard().setContents(contents, owner);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Object get(DataFlavor flavor) {
/*  58 */     return get(getClipboard().getContents(null), flavor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Object get(Transferable content, DataFlavor flavor) {
/*  69 */     if (null != content && content.isDataFlavorSupported(flavor)) {
/*     */       try {
/*  71 */         return content.getTransferData(flavor);
/*  72 */       } catch (UnsupportedFlavorException|java.io.IOException e) {
/*  73 */         throw new UtilException(e);
/*     */       } 
/*     */     }
/*  76 */     return null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setStr(String text) {
/*  85 */     set(new StringSelection(text));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getStr() {
/*  94 */     return (String)get(DataFlavor.stringFlavor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String getStr(Transferable content) {
/* 105 */     return (String)get(content, DataFlavor.stringFlavor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void setImage(Image image) {
/* 114 */     set(new ImageSelection(image), null);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Image getImage() {
/* 123 */     return (Image)get(DataFlavor.imageFlavor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static Image getImage(Transferable content) {
/* 134 */     return (Image)get(content, DataFlavor.imageFlavor);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void listen(ClipboardListener listener) {
/* 145 */     listen(listener, true);
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
/*     */   public static void listen(ClipboardListener listener, boolean sync) {
/* 157 */     listen(10, 100L, listener, sync);
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
/*     */   public static void listen(int tryCount, long delay, ClipboardListener listener, boolean sync) {
/* 171 */     ClipboardMonitor.INSTANCE
/* 172 */       .setTryCount(tryCount)
/* 173 */       .setDelay(delay)
/* 174 */       .addListener(listener)
/* 175 */       .listen(sync);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\swing\clipboard\ClipboardUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */