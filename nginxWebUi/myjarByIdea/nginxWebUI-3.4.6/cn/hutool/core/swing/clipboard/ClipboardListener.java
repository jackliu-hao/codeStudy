package cn.hutool.core.swing.clipboard;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;

public interface ClipboardListener {
   Transferable onChange(Clipboard var1, Transferable var2);
}
