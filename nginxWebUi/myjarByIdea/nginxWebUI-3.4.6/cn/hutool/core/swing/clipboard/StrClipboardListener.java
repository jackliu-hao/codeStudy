package cn.hutool.core.swing.clipboard;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.io.Serializable;

public abstract class StrClipboardListener implements ClipboardListener, Serializable {
   private static final long serialVersionUID = 1L;

   public Transferable onChange(Clipboard clipboard, Transferable contents) {
      return contents.isDataFlavorSupported(DataFlavor.stringFlavor) ? this.onChange(clipboard, ClipboardUtil.getStr(contents)) : null;
   }

   public abstract Transferable onChange(Clipboard var1, String var2);
}
