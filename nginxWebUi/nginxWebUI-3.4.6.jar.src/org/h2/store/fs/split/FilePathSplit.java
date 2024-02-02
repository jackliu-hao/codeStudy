/*     */ package org.h2.store.fs.split;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.io.SequenceInputStream;
/*     */ import java.nio.channels.FileChannel;
/*     */ import java.util.ArrayList;
/*     */ import java.util.List;
/*     */ import org.h2.engine.SysProperties;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.store.fs.FilePath;
/*     */ import org.h2.store.fs.FilePathWrapper;
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
/*     */ public class FilePathSplit
/*     */   extends FilePathWrapper
/*     */ {
/*     */   private static final String PART_SUFFIX = ".part";
/*     */   
/*     */   protected String getPrefix() {
/*  31 */     return getScheme() + ":" + parse(this.name)[0] + ":";
/*     */   }
/*     */ 
/*     */   
/*     */   public FilePath unwrap(String paramString) {
/*  36 */     return FilePath.get(parse(paramString)[1]);
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean setReadOnly() {
/*  41 */     boolean bool = false;
/*  42 */     byte b = 0; while (true) {
/*  43 */       FilePath filePath = getBase(b);
/*  44 */       if (filePath.exists()) {
/*  45 */         bool = filePath.setReadOnly();
/*     */         b++;
/*     */       } 
/*     */       break;
/*     */     } 
/*  50 */     return bool;
/*     */   }
/*     */ 
/*     */   
/*     */   public void delete() {
/*  55 */     byte b = 0; while (true) {
/*  56 */       FilePath filePath = getBase(b);
/*  57 */       if (filePath.exists()) {
/*  58 */         filePath.delete();
/*     */         b++;
/*     */       } 
/*     */       break;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long lastModified() {
/*  67 */     long l = 0L;
/*  68 */     byte b = 0; while (true) {
/*  69 */       FilePath filePath = getBase(b);
/*  70 */       if (filePath.exists()) {
/*  71 */         long l1 = filePath.lastModified();
/*  72 */         l = Math.max(l, l1);
/*     */         b++;
/*     */       } 
/*     */       break;
/*     */     } 
/*  77 */     return l;
/*     */   }
/*     */ 
/*     */   
/*     */   public long size() {
/*  82 */     long l = 0L;
/*  83 */     byte b = 0; while (true) {
/*  84 */       FilePath filePath = getBase(b);
/*  85 */       if (filePath.exists()) {
/*  86 */         l += filePath.size();
/*     */         b++;
/*     */       } 
/*     */       break;
/*     */     } 
/*  91 */     return l;
/*     */   }
/*     */ 
/*     */   
/*     */   public ArrayList<FilePath> newDirectoryStream() {
/*  96 */     List list = getBase().newDirectoryStream();
/*  97 */     ArrayList<FilePathWrapper> arrayList = new ArrayList();
/*  98 */     for (FilePath filePath : list) {
/*  99 */       if (!filePath.getName().endsWith(".part")) {
/* 100 */         arrayList.add(wrap(filePath));
/*     */       }
/*     */     } 
/* 103 */     return (ArrayList)arrayList;
/*     */   }
/*     */ 
/*     */   
/*     */   public InputStream newInputStream() throws IOException {
/* 108 */     InputStream inputStream = getBase().newInputStream();
/* 109 */     byte b = 1; while (true) {
/* 110 */       FilePath filePath = getBase(b);
/* 111 */       if (filePath.exists()) {
/* 112 */         InputStream inputStream1 = filePath.newInputStream();
/* 113 */         inputStream = new SequenceInputStream(inputStream, inputStream1);
/*     */         b++;
/*     */       } 
/*     */       break;
/*     */     } 
/* 118 */     return inputStream;
/*     */   }
/*     */ 
/*     */   
/*     */   public FileChannel open(String paramString) throws IOException {
/* 123 */     ArrayList<FileChannel> arrayList = new ArrayList();
/* 124 */     arrayList.add(getBase().open(paramString));
/* 125 */     byte b = 1; while (true) {
/* 126 */       FilePath filePath = getBase(b);
/* 127 */       if (filePath.exists()) {
/* 128 */         arrayList.add(filePath.open(paramString));
/*     */         b++;
/*     */       } 
/*     */       break;
/*     */     } 
/* 133 */     FileChannel[] arrayOfFileChannel = arrayList.<FileChannel>toArray(new FileChannel[0]);
/* 134 */     long l1 = arrayOfFileChannel[0].size();
/* 135 */     long l2 = l1;
/* 136 */     if (arrayOfFileChannel.length == 1) {
/* 137 */       long l = getDefaultMaxLength();
/* 138 */       if (l1 < l) {
/* 139 */         l1 = l;
/*     */       }
/*     */     } else {
/* 142 */       if (l1 == 0L) {
/* 143 */         closeAndThrow(0, arrayOfFileChannel, arrayOfFileChannel[0], l1);
/*     */       }
/* 145 */       for (byte b1 = 1; b1 < arrayOfFileChannel.length - 1; b1++) {
/* 146 */         FileChannel fileChannel1 = arrayOfFileChannel[b1];
/* 147 */         long l3 = fileChannel1.size();
/* 148 */         l2 += l3;
/* 149 */         if (l3 != l1) {
/* 150 */           closeAndThrow(b1, arrayOfFileChannel, fileChannel1, l1);
/*     */         }
/*     */       } 
/* 153 */       FileChannel fileChannel = arrayOfFileChannel[arrayOfFileChannel.length - 1];
/* 154 */       long l = fileChannel.size();
/* 155 */       l2 += l;
/* 156 */       if (l > l1) {
/* 157 */         closeAndThrow(arrayOfFileChannel.length - 1, arrayOfFileChannel, fileChannel, l1);
/*     */       }
/*     */     } 
/* 160 */     return (FileChannel)new FileSplit(this, paramString, arrayOfFileChannel, l2, l1);
/*     */   }
/*     */   
/*     */   private long getDefaultMaxLength() {
/* 164 */     return 1L << Integer.decode(parse(this.name)[0]).intValue();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private void closeAndThrow(int paramInt, FileChannel[] paramArrayOfFileChannel, FileChannel paramFileChannel, long paramLong) throws IOException {
/* 170 */     String str = "Expected file length: " + paramLong + " got: " + paramFileChannel.size() + " for " + getName(paramInt);
/* 171 */     for (FileChannel fileChannel : paramArrayOfFileChannel) {
/* 172 */       fileChannel.close();
/*     */     }
/* 174 */     throw new IOException(str);
/*     */   }
/*     */ 
/*     */   
/*     */   public OutputStream newOutputStream(boolean paramBoolean) throws IOException {
/* 179 */     return newFileChannelOutputStream(open("rw"), paramBoolean);
/*     */   }
/*     */ 
/*     */   
/*     */   public void moveTo(FilePath paramFilePath, boolean paramBoolean) {
/* 184 */     FilePathSplit filePathSplit = (FilePathSplit)paramFilePath;
/* 185 */     for (byte b = 0;; b++) {
/* 186 */       FilePath filePath = getBase(b);
/* 187 */       if (filePath.exists()) {
/* 188 */         filePath.moveTo(filePathSplit.getBase(b), paramBoolean);
/* 189 */       } else if (filePathSplit.getBase(b).exists()) {
/* 190 */         filePathSplit.getBase(b).delete();
/*     */       } else {
/*     */         break;
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private String[] parse(String paramString) {
/*     */     String str;
/* 204 */     if (!paramString.startsWith(getScheme())) {
/* 205 */       throw DbException.getInternalError(paramString + " doesn't start with " + getScheme());
/*     */     }
/* 207 */     paramString = paramString.substring(getScheme().length() + 1);
/*     */     
/* 209 */     if (paramString.length() > 0 && Character.isDigit(paramString.charAt(0))) {
/* 210 */       int i = paramString.indexOf(':');
/* 211 */       str = paramString.substring(0, i);
/*     */       try {
/* 213 */         paramString = paramString.substring(i + 1);
/* 214 */       } catch (NumberFormatException numberFormatException) {}
/*     */     }
/*     */     else {
/*     */       
/* 218 */       str = Long.toString(SysProperties.SPLIT_FILE_SIZE_SHIFT);
/*     */     } 
/* 220 */     return new String[] { str, paramString };
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   FilePath getBase(int paramInt) {
/* 230 */     return FilePath.get(getName(paramInt));
/*     */   }
/*     */   
/*     */   private String getName(int paramInt) {
/* 234 */     return (paramInt > 0) ? ((getBase()).name + "." + paramInt + ".part") : (getBase()).name;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getScheme() {
/* 239 */     return "split";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\store\fs\split\FilePathSplit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */