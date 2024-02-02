package cn.hutool.core.swing.clipboard;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.Transferable;

public interface ClipboardListener {
  Transferable onChange(Clipboard paramClipboard, Transferable paramTransferable);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\swing\clipboard\ClipboardListener.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */