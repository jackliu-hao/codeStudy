/*    */ package com.sun.jna.platform.mac;
/*    */ 
/*    */ import com.sun.jna.Library;
/*    */ import com.sun.jna.Native;
/*    */ import com.sun.jna.Structure;
/*    */ import com.sun.jna.Structure.FieldOrder;
/*    */ import com.sun.jna.platform.FileUtils;
/*    */ import com.sun.jna.ptr.ByteByReference;
/*    */ import com.sun.jna.ptr.PointerByReference;
/*    */ import java.io.File;
/*    */ import java.io.IOException;
/*    */ import java.util.ArrayList;
/*    */ import java.util.List;
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
/*    */ public class MacFileUtils
/*    */   extends FileUtils
/*    */ {
/*    */   public boolean hasTrash() {
/* 41 */     return true;
/*    */   }
/*    */   public static interface FileManager extends Library { public static final int kFSFileOperationDefaultOptions = 0; public static final int kFSFileOperationsOverwrite = 1;
/*    */     public static final int kFSFileOperationsSkipSourcePermissionErrors = 2;
/* 45 */     public static final FileManager INSTANCE = (FileManager)Native.load("CoreServices", FileManager.class); public static final int kFSFileOperationsDoNotMoveAcrossVolumes = 4; public static final int kFSFileOperationsSkipPreflight = 8; public static final int kFSPathDefaultOptions = 0; public static final int kFSPathMakeRefDoNotFollowLeafSymlink = 1;
/*    */     
/*    */     int FSRefMakePath(FSRef param1FSRef, byte[] param1ArrayOfbyte, int param1Int);
/*    */     
/*    */     int FSPathMakeRef(String param1String, int param1Int, ByteByReference param1ByteByReference);
/*    */     
/*    */     int FSPathMakeRefWithOptions(String param1String, int param1Int, FSRef param1FSRef, ByteByReference param1ByteByReference);
/*    */     
/*    */     int FSPathMoveObjectToTrashSync(String param1String, PointerByReference param1PointerByReference, int param1Int);
/*    */     
/*    */     int FSMoveObjectToTrashSync(FSRef param1FSRef1, FSRef param1FSRef2, int param1Int);
/*    */     
/*    */     @FieldOrder({"hidden"})
/* 58 */     public static class FSRef extends Structure { public byte[] hidden = new byte[80]; } } @FieldOrder({"hidden"}) public static class FSRef extends Structure { public byte[] hidden = new byte[80]; }
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
/*    */   public void moveToTrash(File... files) throws IOException {
/* 72 */     List<String> failed = new ArrayList<String>();
/* 73 */     for (File src : files) {
/* 74 */       FileManager.FSRef fsref = new FileManager.FSRef();
/* 75 */       int status = FileManager.INSTANCE.FSPathMakeRefWithOptions(src.getAbsolutePath(), 1, fsref, null);
/*    */ 
/*    */       
/* 78 */       if (status != 0) {
/* 79 */         failed.add(src + " (FSRef: " + status + ")");
/*    */       } else {
/*    */         
/* 82 */         status = FileManager.INSTANCE.FSMoveObjectToTrashSync(fsref, null, 0);
/* 83 */         if (status != 0)
/* 84 */           failed.add(src + " (" + status + ")"); 
/*    */       } 
/*    */     } 
/* 87 */     if (failed.size() > 0)
/* 88 */       throw new IOException("The following files could not be trashed: " + failed); 
/*    */   }
/*    */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\sun\jna\platform\mac\MacFileUtils.class
 * Java compiler version: 6 (50.0)
 * JD-Core Version:       1.1.3
 */