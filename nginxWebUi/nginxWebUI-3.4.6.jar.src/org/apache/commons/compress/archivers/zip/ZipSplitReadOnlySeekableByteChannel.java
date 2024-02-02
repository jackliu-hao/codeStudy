/*     */ package org.apache.commons.compress.archivers.zip;
/*     */ 
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.Serializable;
/*     */ import java.nio.ByteBuffer;
/*     */ import java.nio.channels.SeekableByteChannel;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.OpenOption;
/*     */ import java.nio.file.StandardOpenOption;
/*     */ import java.util.ArrayList;
/*     */ import java.util.Arrays;
/*     */ import java.util.Comparator;
/*     */ import java.util.List;
/*     */ import java.util.Objects;
/*     */ import java.util.regex.Pattern;
/*     */ import org.apache.commons.compress.utils.FileNameUtils;
/*     */ import org.apache.commons.compress.utils.MultiReadOnlySeekableByteChannel;
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
/*     */ public class ZipSplitReadOnlySeekableByteChannel
/*     */   extends MultiReadOnlySeekableByteChannel
/*     */ {
/*     */   private static final int ZIP_SPLIT_SIGNATURE_LENGTH = 4;
/*  50 */   private final ByteBuffer zipSplitSignatureByteBuffer = ByteBuffer.allocate(4);
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
/*     */   public ZipSplitReadOnlySeekableByteChannel(List<SeekableByteChannel> channels) throws IOException {
/*  66 */     super(channels);
/*     */ 
/*     */     
/*  69 */     assertSplitSignature(channels);
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private void assertSplitSignature(List<SeekableByteChannel> channels) throws IOException {
/*  92 */     SeekableByteChannel channel = channels.get(0);
/*     */     
/*  94 */     channel.position(0L);
/*     */     
/*  96 */     this.zipSplitSignatureByteBuffer.rewind();
/*  97 */     channel.read(this.zipSplitSignatureByteBuffer);
/*  98 */     ZipLong signature = new ZipLong(this.zipSplitSignatureByteBuffer.array());
/*  99 */     if (!signature.equals(ZipLong.DD_SIG)) {
/* 100 */       channel.position(0L);
/* 101 */       throw new IOException("The first zip split segment does not begin with split zip file signature");
/*     */     } 
/*     */     
/* 104 */     channel.position(0L);
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
/*     */   public static SeekableByteChannel forOrderedSeekableByteChannels(SeekableByteChannel... channels) throws IOException {
/* 117 */     if (((SeekableByteChannel[])Objects.requireNonNull((T)channels, "channels must not be null")).length == 1) {
/* 118 */       return channels[0];
/*     */     }
/* 120 */     return (SeekableByteChannel)new ZipSplitReadOnlySeekableByteChannel(Arrays.asList(channels));
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
/*     */ 
/*     */   
/*     */   public static SeekableByteChannel forOrderedSeekableByteChannels(SeekableByteChannel lastSegmentChannel, Iterable<SeekableByteChannel> channels) throws IOException {
/* 136 */     Objects.requireNonNull(channels, "channels");
/* 137 */     Objects.requireNonNull(lastSegmentChannel, "lastSegmentChannel");
/*     */     
/* 139 */     List<SeekableByteChannel> channelsList = new ArrayList<>();
/* 140 */     for (SeekableByteChannel channel : channels) {
/* 141 */       channelsList.add(channel);
/*     */     }
/* 143 */     channelsList.add(lastSegmentChannel);
/*     */     
/* 145 */     return forOrderedSeekableByteChannels(channelsList.<SeekableByteChannel>toArray(new SeekableByteChannel[0]));
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
/*     */   public static SeekableByteChannel buildFromLastSplitSegment(File lastSegmentFile) throws IOException {
/* 158 */     String extension = FileNameUtils.getExtension(lastSegmentFile.getCanonicalPath());
/* 159 */     if (!extension.equalsIgnoreCase("zip")) {
/* 160 */       throw new IllegalArgumentException("The extension of last zip split segment should be .zip");
/*     */     }
/*     */     
/* 163 */     File parent = lastSegmentFile.getParentFile();
/* 164 */     String fileBaseName = FileNameUtils.getBaseName(lastSegmentFile.getCanonicalPath());
/* 165 */     ArrayList<File> splitZipSegments = new ArrayList<>();
/*     */ 
/*     */     
/* 168 */     Pattern pattern = Pattern.compile(Pattern.quote(fileBaseName) + ".[zZ][0-9]+");
/* 169 */     File[] children = parent.listFiles();
/* 170 */     if (children != null) {
/* 171 */       for (File file : children) {
/* 172 */         if (pattern.matcher(file.getName()).matches())
/*     */         {
/*     */ 
/*     */           
/* 176 */           splitZipSegments.add(file);
/*     */         }
/*     */       } 
/*     */     }
/* 180 */     splitZipSegments.sort(new ZipSplitSegmentComparator());
/* 181 */     return forFiles(lastSegmentFile, splitZipSegments);
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
/*     */   
/*     */   public static SeekableByteChannel forFiles(File... files) throws IOException {
/* 196 */     List<SeekableByteChannel> channels = new ArrayList<>();
/* 197 */     for (File f : (File[])Objects.<File[]>requireNonNull(files, "files must not be null")) {
/* 198 */       channels.add(Files.newByteChannel(f.toPath(), new OpenOption[] { StandardOpenOption.READ }));
/*     */     } 
/* 200 */     if (channels.size() == 1) {
/* 201 */       return channels.get(0);
/*     */     }
/* 203 */     return (SeekableByteChannel)new ZipSplitReadOnlySeekableByteChannel(channels);
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
/*     */   
/*     */   public static SeekableByteChannel forFiles(File lastSegmentFile, Iterable<File> files) throws IOException {
/* 218 */     Objects.requireNonNull(files, "files");
/* 219 */     Objects.requireNonNull(lastSegmentFile, "lastSegmentFile");
/*     */     
/* 221 */     List<File> filesList = new ArrayList<>();
/* 222 */     for (File f : files) {
/* 223 */       filesList.add(f);
/*     */     }
/* 225 */     filesList.add(lastSegmentFile);
/*     */     
/* 227 */     return forFiles(filesList.<File>toArray(new File[0]));
/*     */   }
/*     */   
/*     */   private static class ZipSplitSegmentComparator implements Comparator<File>, Serializable {
/*     */     private static final long serialVersionUID = 20200123L;
/*     */     
/*     */     public int compare(File file1, File file2) {
/* 234 */       String extension1 = FileNameUtils.getExtension(file1.getPath());
/* 235 */       String extension2 = FileNameUtils.getExtension(file2.getPath());
/*     */       
/* 237 */       if (!extension1.startsWith("z")) {
/* 238 */         return -1;
/*     */       }
/*     */       
/* 241 */       if (!extension2.startsWith("z")) {
/* 242 */         return 1;
/*     */       }
/*     */       
/* 245 */       Integer splitSegmentNumber1 = Integer.valueOf(Integer.parseInt(extension1.substring(1)));
/* 246 */       Integer splitSegmentNumber2 = Integer.valueOf(Integer.parseInt(extension2.substring(1)));
/*     */       
/* 248 */       return splitSegmentNumber1.compareTo(splitSegmentNumber2);
/*     */     }
/*     */     
/*     */     private ZipSplitSegmentComparator() {}
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\archivers\zip\ZipSplitReadOnlySeekableByteChannel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */