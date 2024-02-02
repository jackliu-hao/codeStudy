package cn.hutool.core.swing.clipboard;

import cn.hutool.core.exceptions.UtilException;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class ClipboardUtil {
   public static Clipboard getClipboard() {
      return Toolkit.getDefaultToolkit().getSystemClipboard();
   }

   public static void set(Transferable contents) {
      set(contents, (ClipboardOwner)null);
   }

   public static void set(Transferable contents, ClipboardOwner owner) {
      getClipboard().setContents(contents, owner);
   }

   public static Object get(DataFlavor flavor) {
      return get(getClipboard().getContents((Object)null), flavor);
   }

   public static Object get(Transferable content, DataFlavor flavor) {
      if (null != content && content.isDataFlavorSupported(flavor)) {
         try {
            return content.getTransferData(flavor);
         } catch (IOException | UnsupportedFlavorException var3) {
            throw new UtilException(var3);
         }
      } else {
         return null;
      }
   }

   public static void setStr(String text) {
      set(new StringSelection(text));
   }

   public static String getStr() {
      return (String)get(DataFlavor.stringFlavor);
   }

   public static String getStr(Transferable content) {
      return (String)get(content, DataFlavor.stringFlavor);
   }

   public static void setImage(Image image) {
      set(new ImageSelection(image), (ClipboardOwner)null);
   }

   public static Image getImage() {
      return (Image)get(DataFlavor.imageFlavor);
   }

   public static Image getImage(Transferable content) {
      return (Image)get(content, DataFlavor.imageFlavor);
   }

   public static void listen(ClipboardListener listener) {
      listen(listener, true);
   }

   public static void listen(ClipboardListener listener, boolean sync) {
      listen(10, 100L, listener, sync);
   }

   public static void listen(int tryCount, long delay, ClipboardListener listener, boolean sync) {
      ClipboardMonitor.INSTANCE.setTryCount(tryCount).setDelay(delay).addListener(listener).listen(sync);
   }
}
