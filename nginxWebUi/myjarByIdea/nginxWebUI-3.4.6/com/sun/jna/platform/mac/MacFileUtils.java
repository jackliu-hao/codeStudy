package com.sun.jna.platform.mac;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.platform.FileUtils;
import com.sun.jna.ptr.ByteByReference;
import com.sun.jna.ptr.PointerByReference;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MacFileUtils extends FileUtils {
   public boolean hasTrash() {
      return true;
   }

   public void moveToTrash(File... files) throws IOException {
      List<String> failed = new ArrayList();
      File[] var3 = files;
      int var4 = files.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         File src = var3[var5];
         FileManager.FSRef fsref = new FileManager.FSRef();
         int status = MacFileUtils.FileManager.INSTANCE.FSPathMakeRefWithOptions(src.getAbsolutePath(), 1, fsref, (ByteByReference)null);
         if (status != 0) {
            failed.add(src + " (FSRef: " + status + ")");
         } else {
            status = MacFileUtils.FileManager.INSTANCE.FSMoveObjectToTrashSync(fsref, (FileManager.FSRef)null, 0);
            if (status != 0) {
               failed.add(src + " (" + status + ")");
            }
         }
      }

      if (failed.size() > 0) {
         throw new IOException("The following files could not be trashed: " + failed);
      }
   }

   public interface FileManager extends Library {
      FileManager INSTANCE = (FileManager)Native.load("CoreServices", FileManager.class);
      int kFSFileOperationDefaultOptions = 0;
      int kFSFileOperationsOverwrite = 1;
      int kFSFileOperationsSkipSourcePermissionErrors = 2;
      int kFSFileOperationsDoNotMoveAcrossVolumes = 4;
      int kFSFileOperationsSkipPreflight = 8;
      int kFSPathDefaultOptions = 0;
      int kFSPathMakeRefDoNotFollowLeafSymlink = 1;

      int FSRefMakePath(FSRef var1, byte[] var2, int var3);

      int FSPathMakeRef(String var1, int var2, ByteByReference var3);

      int FSPathMakeRefWithOptions(String var1, int var2, FSRef var3, ByteByReference var4);

      int FSPathMoveObjectToTrashSync(String var1, PointerByReference var2, int var3);

      int FSMoveObjectToTrashSync(FSRef var1, FSRef var2, int var3);

      @Structure.FieldOrder({"hidden"})
      public static class FSRef extends Structure {
         public byte[] hidden = new byte[80];
      }
   }
}
