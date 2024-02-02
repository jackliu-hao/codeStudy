/*      */ package org.codehaus.plexus.util;
/*      */ 
/*      */ import java.io.BufferedReader;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.FileReader;
/*      */ import java.io.FileWriter;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.OutputStreamWriter;
/*      */ import java.io.Reader;
/*      */ import java.io.Writer;
/*      */ import java.net.URL;
/*      */ import java.security.SecureRandom;
/*      */ import java.text.DecimalFormat;
/*      */ import java.util.ArrayList;
/*      */ import java.util.Arrays;
/*      */ import java.util.Iterator;
/*      */ import java.util.List;
/*      */ import java.util.Random;
/*      */ import java.util.Vector;
/*      */ import org.codehaus.plexus.util.io.FileInputStreamFacade;
/*      */ import org.codehaus.plexus.util.io.InputStreamFacade;
/*      */ import org.codehaus.plexus.util.io.URLInputStreamFacade;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class FileUtils
/*      */ {
/*      */   public static final int ONE_KB = 1024;
/*      */   public static final int ONE_MB = 1048576;
/*      */   public static final int ONE_GB = 1073741824;
/*  144 */   public static String FS = System.getProperty("file.separator");
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*  152 */   private static final String[] INVALID_CHARACTERS_FOR_WINDOWS_FILE_NAME = new String[] { ":", "*", "?", "\"", "<", ">", "|" };
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] getDefaultExcludes() {
/*  160 */     return DirectoryScanner.DEFAULTEXCLUDES;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List getDefaultExcludesAsList() {
/*  169 */     return Arrays.asList(getDefaultExcludes());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getDefaultExcludesAsString() {
/*  179 */     return StringUtils.join((Object[])DirectoryScanner.DEFAULTEXCLUDES, ",");
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String byteCountToDisplaySize(int size) {
/*      */     String displaySize;
/*  193 */     if (size / 1073741824 > 0) {
/*      */       
/*  195 */       displaySize = String.valueOf(size / 1073741824) + " GB";
/*      */     }
/*  197 */     else if (size / 1048576 > 0) {
/*      */       
/*  199 */       displaySize = String.valueOf(size / 1048576) + " MB";
/*      */     }
/*  201 */     else if (size / 1024 > 0) {
/*      */       
/*  203 */       displaySize = String.valueOf(size / 1024) + " KB";
/*      */     }
/*      */     else {
/*      */       
/*  207 */       displaySize = String.valueOf(size) + " bytes";
/*      */     } 
/*      */     
/*  210 */     return displaySize;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String dirname(String filename) {
/*  222 */     int i = filename.lastIndexOf(File.separator);
/*  223 */     return (i >= 0) ? filename.substring(0, i) : "";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String filename(String filename) {
/*  234 */     int i = filename.lastIndexOf(File.separator);
/*  235 */     return (i >= 0) ? filename.substring(i + 1) : filename;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String basename(String filename) {
/*  247 */     return basename(filename, extension(filename));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String basename(String filename, String suffix) {
/*  260 */     int i = filename.lastIndexOf(File.separator) + 1;
/*  261 */     int lastDot = (suffix != null && suffix.length() > 0) ? filename.lastIndexOf(suffix) : -1;
/*      */     
/*  263 */     if (lastDot >= 0)
/*      */     {
/*  265 */       return filename.substring(i, lastDot);
/*      */     }
/*  267 */     if (i > 0)
/*      */     {
/*  269 */       return filename.substring(i);
/*      */     }
/*      */ 
/*      */     
/*  273 */     return filename;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String extension(String filename) {
/*  288 */     int lastDot, lastSep = filename.lastIndexOf(File.separatorChar);
/*      */     
/*  290 */     if (lastSep < 0) {
/*      */       
/*  292 */       lastDot = filename.lastIndexOf('.');
/*      */     }
/*      */     else {
/*      */       
/*  296 */       lastDot = filename.substring(lastSep + 1).lastIndexOf('.');
/*  297 */       if (lastDot >= 0)
/*      */       {
/*  299 */         lastDot += lastSep + 1;
/*      */       }
/*      */     } 
/*      */     
/*  303 */     if (lastDot >= 0 && lastDot > lastSep)
/*      */     {
/*  305 */       return filename.substring(lastDot + 1);
/*      */     }
/*      */     
/*  308 */     return "";
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean fileExists(String fileName) {
/*  319 */     File file = new File(fileName);
/*  320 */     return file.exists();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String fileRead(String file) throws IOException {
/*  333 */     return fileRead(file, (String)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String fileRead(String file, String encoding) throws IOException {
/*  345 */     return fileRead(new File(file), encoding);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String fileRead(File file) throws IOException {
/*  358 */     return fileRead(file, (String)null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String fileRead(File file, String encoding) throws IOException {
/*  370 */     StringBuffer buf = new StringBuffer();
/*      */     
/*  372 */     Reader reader = null;
/*      */ 
/*      */     
/*      */     try {
/*  376 */       if (encoding != null) {
/*      */         
/*  378 */         reader = new InputStreamReader(new FileInputStream(file), encoding);
/*      */       }
/*      */       else {
/*      */         
/*  382 */         reader = new InputStreamReader(new FileInputStream(file));
/*      */       } 
/*      */       
/*  385 */       char[] b = new char[512]; int count;
/*  386 */       while ((count = reader.read(b)) > 0)
/*      */       {
/*  388 */         buf.append(b, 0, count);
/*      */       }
/*      */     }
/*      */     finally {
/*      */       
/*  393 */       IOUtil.close(reader);
/*      */     } 
/*      */     
/*  396 */     return buf.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void fileAppend(String fileName, String data) throws IOException {
/*  410 */     fileAppend(fileName, null, data);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void fileAppend(String fileName, String encoding, String data) throws IOException {
/*  424 */     FileOutputStream out = null;
/*      */     
/*      */     try {
/*  427 */       out = new FileOutputStream(fileName, true);
/*  428 */       if (encoding != null) {
/*  429 */         out.write(data.getBytes(encoding));
/*      */       }
/*      */       else {
/*      */         
/*  433 */         out.write(data.getBytes());
/*      */       }
/*      */     
/*      */     } finally {
/*      */       
/*  438 */       IOUtil.close(out);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void fileWrite(String fileName, String data) throws IOException {
/*  453 */     fileWrite(fileName, null, data);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void fileWrite(String fileName, String encoding, String data) throws IOException {
/*  467 */     FileOutputStream out = null;
/*      */     
/*      */     try {
/*  470 */       out = new FileOutputStream(fileName);
/*  471 */       if (encoding != null)
/*      */       {
/*  473 */         out.write(data.getBytes(encoding));
/*      */       }
/*      */       else
/*      */       {
/*  477 */         out.write(data.getBytes());
/*      */       }
/*      */     
/*      */     } finally {
/*      */       
/*  482 */       IOUtil.close(out);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void fileDelete(String fileName) {
/*  493 */     File file = new File(fileName);
/*  494 */     file.delete();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean waitFor(String fileName, int seconds) {
/*  506 */     return waitFor(new File(fileName), seconds);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean waitFor(File file, int seconds) {
/*  518 */     int timeout = 0;
/*  519 */     int tick = 0;
/*  520 */     while (!file.exists()) {
/*      */       
/*  522 */       if (tick++ >= 10) {
/*      */         
/*  524 */         tick = 0;
/*  525 */         if (timeout++ > seconds)
/*      */         {
/*  527 */           return false;
/*      */         }
/*      */       } 
/*      */       
/*      */       try {
/*  532 */         Thread.sleep(100L);
/*      */       }
/*  534 */       catch (InterruptedException ignore) {}
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*  539 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File getFile(String fileName) {
/*  550 */     return new File(fileName);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String[] getFilesFromExtension(String directory, String[] extensions) {
/*  567 */     Vector files = new Vector();
/*      */     
/*  569 */     File currentDir = new File(directory);
/*      */     
/*  571 */     String[] unknownFiles = currentDir.list();
/*      */     
/*  573 */     if (unknownFiles == null)
/*      */     {
/*  575 */       return new String[0];
/*      */     }
/*      */     
/*  578 */     for (int i = 0; i < unknownFiles.length; i++) {
/*      */       
/*  580 */       String currentFileName = directory + System.getProperty("file.separator") + unknownFiles[i];
/*  581 */       File currentFile = new File(currentFileName);
/*      */       
/*  583 */       if (currentFile.isDirectory()) {
/*      */ 
/*      */         
/*  586 */         if (!currentFile.getName().equals("CVS"))
/*      */         {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */           
/*  594 */           String[] fetchFiles = getFilesFromExtension(currentFileName, extensions);
/*  595 */           files = blendFilesToVector(files, fetchFiles);
/*      */         }
/*      */       
/*      */       }
/*      */       else {
/*      */         
/*  601 */         String add = currentFile.getAbsolutePath();
/*  602 */         if (isValidFile(add, extensions))
/*      */         {
/*  604 */           files.addElement(add);
/*      */         }
/*      */       } 
/*      */     } 
/*      */ 
/*      */     
/*  610 */     String[] foundFiles = new String[files.size()];
/*  611 */     files.copyInto((Object[])foundFiles);
/*      */     
/*  613 */     return foundFiles;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static Vector blendFilesToVector(Vector v, String[] files) {
/*  621 */     for (int i = 0; i < files.length; i++)
/*      */     {
/*  623 */       v.addElement(files[i]);
/*      */     }
/*      */     
/*  626 */     return v;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static boolean isValidFile(String file, String[] extensions) {
/*  636 */     String extension = extension(file);
/*  637 */     if (extension == null)
/*      */     {
/*  639 */       extension = "";
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  645 */     for (int i = 0; i < extensions.length; i++) {
/*      */       
/*  647 */       if (extensions[i].equals(extension))
/*      */       {
/*  649 */         return true;
/*      */       }
/*      */     } 
/*      */     
/*  653 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void mkdir(String dir) {
/*  666 */     File file = new File(dir);
/*      */     
/*  668 */     if (Os.isFamily("windows"))
/*      */     {
/*  670 */       if (!isValidWindowsFileName(file))
/*      */       {
/*  672 */         throw new IllegalArgumentException("The file (" + dir + ") cannot contain any of the following characters: \n" + StringUtils.join(INVALID_CHARACTERS_FOR_WINDOWS_FILE_NAME, " "));
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */     
/*  678 */     if (!file.exists())
/*      */     {
/*  680 */       file.mkdirs();
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean contentEquals(File file1, File file2) throws IOException {
/*  695 */     boolean file1Exists = file1.exists();
/*  696 */     if (file1Exists != file2.exists())
/*      */     {
/*  698 */       return false;
/*      */     }
/*      */     
/*  701 */     if (!file1Exists)
/*      */     {
/*      */       
/*  704 */       return true;
/*      */     }
/*      */     
/*  707 */     if (file1.isDirectory() || file2.isDirectory())
/*      */     {
/*      */       
/*  710 */       return false;
/*      */     }
/*      */     
/*  713 */     InputStream input1 = null;
/*  714 */     InputStream input2 = null;
/*      */     
/*      */     try {
/*  717 */       input1 = new FileInputStream(file1);
/*  718 */       input2 = new FileInputStream(file2);
/*  719 */       return IOUtil.contentEquals(input1, input2);
/*      */     
/*      */     }
/*      */     finally {
/*      */       
/*  724 */       IOUtil.close(input1);
/*  725 */       IOUtil.close(input2);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File toFile(URL url) {
/*  738 */     if (url == null || !url.getProtocol().equalsIgnoreCase("file"))
/*      */     {
/*  740 */       return null;
/*      */     }
/*      */     
/*  743 */     String filename = url.getFile().replace('/', File.separatorChar);
/*  744 */     int pos = -1;
/*  745 */     while ((pos = filename.indexOf('%', pos + 1)) >= 0) {
/*      */       
/*  747 */       if (pos + 2 < filename.length()) {
/*      */         
/*  749 */         String hexStr = filename.substring(pos + 1, pos + 3);
/*  750 */         char ch = (char)Integer.parseInt(hexStr, 16);
/*  751 */         filename = filename.substring(0, pos) + ch + filename.substring(pos + 3);
/*      */       } 
/*      */     } 
/*  754 */     return new File(filename);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static URL[] toURLs(File[] files) throws IOException {
/*  767 */     URL[] urls = new URL[files.length];
/*      */     
/*  769 */     for (int i = 0; i < urls.length; i++)
/*      */     {
/*  771 */       urls[i] = files[i].toURL();
/*      */     }
/*      */     
/*  774 */     return urls;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String removeExtension(String filename) {
/*  791 */     String ext = extension(filename);
/*      */     
/*  793 */     if ("".equals(ext))
/*      */     {
/*  795 */       return filename;
/*      */     }
/*      */     
/*  798 */     int index = filename.lastIndexOf(ext) - 1;
/*  799 */     return filename.substring(0, index);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getExtension(String filename) {
/*  816 */     return extension(filename);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String removePath(String filepath) {
/*  832 */     return removePath(filepath, File.separatorChar);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String removePath(String filepath, char fileSeparatorChar) {
/*  849 */     int index = filepath.lastIndexOf(fileSeparatorChar);
/*      */     
/*  851 */     if (-1 == index)
/*      */     {
/*  853 */       return filepath;
/*      */     }
/*      */     
/*  856 */     return filepath.substring(index + 1);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getPath(String filepath) {
/*  872 */     return getPath(filepath, File.separatorChar);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String getPath(String filepath, char fileSeparatorChar) {
/*  889 */     int index = filepath.lastIndexOf(fileSeparatorChar);
/*  890 */     if (-1 == index)
/*      */     {
/*  892 */       return "";
/*      */     }
/*      */     
/*  895 */     return filepath.substring(0, index);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void copyFileToDirectory(String source, String destinationDirectory) throws IOException {
/*  913 */     copyFileToDirectory(new File(source), new File(destinationDirectory));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void copyFileToDirectoryIfModified(String source, String destinationDirectory) throws IOException {
/*  932 */     copyFileToDirectoryIfModified(new File(source), new File(destinationDirectory));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void copyFileToDirectory(File source, File destinationDirectory) throws IOException {
/*  950 */     if (destinationDirectory.exists() && !destinationDirectory.isDirectory())
/*      */     {
/*  952 */       throw new IllegalArgumentException("Destination is not a directory");
/*      */     }
/*      */     
/*  955 */     copyFile(source, new File(destinationDirectory, source.getName()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void copyFileToDirectoryIfModified(File source, File destinationDirectory) throws IOException {
/*  974 */     if (destinationDirectory.exists() && !destinationDirectory.isDirectory())
/*      */     {
/*  976 */       throw new IllegalArgumentException("Destination is not a directory");
/*      */     }
/*      */     
/*  979 */     copyFileIfModified(source, new File(destinationDirectory, source.getName()));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void copyFile(File source, File destination) throws IOException {
/* 1000 */     if (!source.exists()) {
/*      */       
/* 1002 */       String message = "File " + source + " does not exist";
/* 1003 */       throw new IOException(message);
/*      */     } 
/*      */ 
/*      */     
/* 1007 */     if (source.getCanonicalPath().equals(destination.getCanonicalPath())) {
/*      */       return;
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1013 */     copyStreamToFile((InputStreamFacade)new FileInputStreamFacade(source), destination);
/*      */     
/* 1015 */     if (source.length() != destination.length()) {
/*      */       
/* 1017 */       String message = "Failed to copy full contents from " + source + " to " + destination;
/* 1018 */       throw new IOException(message);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean copyFileIfModified(File source, File destination) throws IOException {
/* 1039 */     if (destination.lastModified() < source.lastModified()) {
/*      */       
/* 1041 */       copyFile(source, destination);
/*      */       
/* 1043 */       return true;
/*      */     } 
/*      */     
/* 1046 */     return false;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void copyURLToFile(URL source, File destination) throws IOException {
/* 1067 */     copyStreamToFile((InputStreamFacade)new URLInputStreamFacade(source), destination);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void copyStreamToFile(InputStreamFacade source, File destination) throws IOException {
/* 1090 */     if (destination.getParentFile() != null && !destination.getParentFile().exists())
/*      */     {
/* 1092 */       destination.getParentFile().mkdirs();
/*      */     }
/*      */ 
/*      */     
/* 1096 */     if (destination.exists() && !destination.canWrite()) {
/*      */       
/* 1098 */       String message = "Unable to open file " + destination + " for writing.";
/* 1099 */       throw new IOException(message);
/*      */     } 
/*      */     
/* 1102 */     InputStream input = null;
/* 1103 */     FileOutputStream output = null;
/*      */     
/*      */     try {
/* 1106 */       input = source.getInputStream();
/* 1107 */       output = new FileOutputStream(destination);
/* 1108 */       IOUtil.copy(input, output);
/*      */     }
/*      */     finally {
/*      */       
/* 1112 */       IOUtil.close(input);
/* 1113 */       IOUtil.close(output);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String normalize(String path) {
/* 1137 */     String normalized = path;
/*      */ 
/*      */     
/*      */     while (true) {
/* 1141 */       int index = normalized.indexOf("//");
/* 1142 */       if (index < 0) {
/*      */         break;
/*      */       }
/*      */       
/* 1146 */       normalized = normalized.substring(0, index) + normalized.substring(index + 1);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 1152 */       int index = normalized.indexOf("/./");
/* 1153 */       if (index < 0) {
/*      */         break;
/*      */       }
/*      */       
/* 1157 */       normalized = normalized.substring(0, index) + normalized.substring(index + 2);
/*      */     } 
/*      */ 
/*      */ 
/*      */     
/*      */     while (true) {
/* 1163 */       int index = normalized.indexOf("/../");
/* 1164 */       if (index < 0) {
/*      */         break;
/*      */       }
/*      */       
/* 1168 */       if (index == 0)
/*      */       {
/* 1170 */         return null;
/*      */       }
/* 1172 */       int index2 = normalized.lastIndexOf('/', index - 1);
/* 1173 */       normalized = normalized.substring(0, index2) + normalized.substring(index + 3);
/*      */     } 
/*      */ 
/*      */     
/* 1177 */     return normalized;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String catPath(String lookupPath, String path) {
/* 1197 */     int index = lookupPath.lastIndexOf("/");
/* 1198 */     String lookup = lookupPath.substring(0, index);
/* 1199 */     String pth = path;
/*      */ 
/*      */     
/* 1202 */     while (pth.startsWith("../")) {
/*      */       
/* 1204 */       if (lookup.length() > 0) {
/*      */         
/* 1206 */         index = lookup.lastIndexOf("/");
/* 1207 */         lookup = lookup.substring(0, index);
/*      */       
/*      */       }
/*      */       else {
/*      */         
/* 1212 */         return null;
/*      */       } 
/*      */       
/* 1215 */       index = pth.indexOf("../") + 3;
/* 1216 */       pth = pth.substring(index);
/*      */     } 
/*      */     
/* 1219 */     return lookup + "/" + pth;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File resolveFile(File baseFile, String filename) {
/* 1234 */     String filenm = filename;
/* 1235 */     if ('/' != File.separatorChar)
/*      */     {
/* 1237 */       filenm = filename.replace('/', File.separatorChar);
/*      */     }
/*      */     
/* 1240 */     if ('\\' != File.separatorChar)
/*      */     {
/* 1242 */       filenm = filename.replace('\\', File.separatorChar);
/*      */     }
/*      */ 
/*      */     
/* 1246 */     if (filenm.startsWith(File.separator) || (Os.isFamily("windows") && filenm.indexOf(":") > 0)) {
/*      */       
/* 1248 */       File file1 = new File(filenm);
/*      */ 
/*      */       
/*      */       try {
/* 1252 */         file1 = file1.getCanonicalFile();
/*      */       }
/* 1254 */       catch (IOException ioe) {}
/*      */ 
/*      */ 
/*      */ 
/*      */       
/* 1259 */       return file1;
/*      */     } 
/*      */ 
/*      */     
/* 1263 */     char[] chars = filename.toCharArray();
/* 1264 */     StringBuffer sb = new StringBuffer();
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1269 */     int start = 0;
/* 1270 */     if ('\\' == File.separatorChar) {
/*      */       
/* 1272 */       sb.append(filenm.charAt(0));
/* 1273 */       start++;
/*      */     } 
/*      */     
/* 1276 */     for (int i = start; i < chars.length; i++) {
/*      */       
/* 1278 */       boolean doubleSeparator = (File.separatorChar == chars[i] && File.separatorChar == chars[i - 1]);
/*      */       
/* 1280 */       if (!doubleSeparator)
/*      */       {
/* 1282 */         sb.append(chars[i]);
/*      */       }
/*      */     } 
/*      */     
/* 1286 */     filenm = sb.toString();
/*      */ 
/*      */     
/* 1289 */     File file = (new File(baseFile, filenm)).getAbsoluteFile();
/*      */ 
/*      */     
/*      */     try {
/* 1293 */       file = file.getCanonicalFile();
/*      */     }
/* 1295 */     catch (IOException ioe) {}
/*      */ 
/*      */ 
/*      */ 
/*      */     
/* 1300 */     return file;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void forceDelete(String file) throws IOException {
/* 1312 */     forceDelete(new File(file));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void forceDelete(File file) throws IOException {
/* 1324 */     if (file.isDirectory()) {
/*      */       
/* 1326 */       deleteDirectory(file);
/*      */ 
/*      */     
/*      */     }
/*      */     else {
/*      */ 
/*      */ 
/*      */       
/* 1334 */       boolean filePresent = file.getCanonicalFile().exists();
/* 1335 */       if (!deleteFile(file) && filePresent) {
/*      */         
/* 1337 */         String message = "File " + file + " unable to be deleted.";
/* 1338 */         throw new IOException(message);
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static boolean deleteFile(File file) throws IOException {
/* 1354 */     if (file.isDirectory())
/*      */     {
/* 1356 */       throw new IOException("File " + file + " isn't a file.");
/*      */     }
/*      */     
/* 1359 */     if (!file.delete()) {
/*      */       
/* 1361 */       if (Os.isFamily("windows")) {
/*      */         
/* 1363 */         file = file.getCanonicalFile();
/* 1364 */         System.gc();
/*      */       } 
/*      */ 
/*      */       
/*      */       try {
/* 1369 */         Thread.sleep(10L);
/* 1370 */         return file.delete();
/*      */       }
/* 1372 */       catch (InterruptedException ex) {
/*      */         
/* 1374 */         return file.delete();
/*      */       } 
/*      */     } 
/*      */     
/* 1378 */     return true;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void forceDeleteOnExit(File file) throws IOException {
/* 1391 */     if (!file.exists()) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/* 1396 */     if (file.isDirectory()) {
/*      */       
/* 1398 */       deleteDirectoryOnExit(file);
/*      */     }
/*      */     else {
/*      */       
/* 1402 */       file.deleteOnExit();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void deleteDirectoryOnExit(File directory) throws IOException {
/* 1415 */     if (!directory.exists()) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/* 1420 */     cleanDirectoryOnExit(directory);
/* 1421 */     directory.deleteOnExit();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void cleanDirectoryOnExit(File directory) throws IOException {
/* 1433 */     if (!directory.exists()) {
/*      */       
/* 1435 */       String message = directory + " does not exist";
/* 1436 */       throw new IllegalArgumentException(message);
/*      */     } 
/*      */     
/* 1439 */     if (!directory.isDirectory()) {
/*      */       
/* 1441 */       String message = directory + " is not a directory";
/* 1442 */       throw new IllegalArgumentException(message);
/*      */     } 
/*      */     
/* 1445 */     IOException exception = null;
/*      */     
/* 1447 */     File[] files = directory.listFiles();
/* 1448 */     for (int i = 0; i < files.length; i++) {
/*      */       
/* 1450 */       File file = files[i];
/*      */       
/*      */       try {
/* 1453 */         forceDeleteOnExit(file);
/*      */       }
/* 1455 */       catch (IOException ioe) {
/*      */         
/* 1457 */         exception = ioe;
/*      */       } 
/*      */     } 
/*      */     
/* 1461 */     if (null != exception)
/*      */     {
/* 1463 */       throw exception;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void forceMkdir(File file) throws IOException {
/* 1480 */     if (Os.isFamily("windows"))
/*      */     {
/* 1482 */       if (!isValidWindowsFileName(file))
/*      */       {
/* 1484 */         throw new IllegalArgumentException("The file (" + file.getAbsolutePath() + ") cannot contain any of the following characters: \n" + StringUtils.join(INVALID_CHARACTERS_FOR_WINDOWS_FILE_NAME, " "));
/*      */       }
/*      */     }
/*      */ 
/*      */ 
/*      */     
/* 1490 */     if (file.exists()) {
/*      */       
/* 1492 */       if (file.isFile())
/*      */       {
/* 1494 */         String message = "File " + file + " exists and is " + "not a directory. Unable to create directory.";
/*      */         
/* 1496 */         throw new IOException(message);
/*      */       
/*      */       }
/*      */     
/*      */     }
/* 1501 */     else if (false == file.mkdirs()) {
/*      */       
/* 1503 */       String message = "Unable to create directory " + file;
/* 1504 */       throw new IOException(message);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void deleteDirectory(String directory) throws IOException {
/* 1518 */     deleteDirectory(new File(directory));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void deleteDirectory(File directory) throws IOException {
/* 1530 */     if (!directory.exists()) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/* 1535 */     cleanDirectory(directory);
/* 1536 */     if (!directory.delete()) {
/*      */       
/* 1538 */       String message = "Directory " + directory + " unable to be deleted.";
/* 1539 */       throw new IOException(message);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void cleanDirectory(String directory) throws IOException {
/* 1552 */     cleanDirectory(new File(directory));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void cleanDirectory(File directory) throws IOException {
/* 1564 */     if (!directory.exists()) {
/*      */       
/* 1566 */       String message = directory + " does not exist";
/* 1567 */       throw new IllegalArgumentException(message);
/*      */     } 
/*      */     
/* 1570 */     if (!directory.isDirectory()) {
/*      */       
/* 1572 */       String message = directory + " is not a directory";
/* 1573 */       throw new IllegalArgumentException(message);
/*      */     } 
/*      */     
/* 1576 */     IOException exception = null;
/*      */     
/* 1578 */     File[] files = directory.listFiles();
/*      */     
/* 1580 */     if (files == null) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/* 1585 */     for (int i = 0; i < files.length; i++) {
/*      */       
/* 1587 */       File file = files[i];
/*      */       
/*      */       try {
/* 1590 */         forceDelete(file);
/*      */       }
/* 1592 */       catch (IOException ioe) {
/*      */         
/* 1594 */         exception = ioe;
/*      */       } 
/*      */     } 
/*      */     
/* 1598 */     if (null != exception)
/*      */     {
/* 1600 */       throw exception;
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long sizeOfDirectory(String directory) {
/* 1612 */     return sizeOfDirectory(new File(directory));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static long sizeOfDirectory(File directory) {
/* 1623 */     if (!directory.exists()) {
/*      */       
/* 1625 */       String message = directory + " does not exist";
/* 1626 */       throw new IllegalArgumentException(message);
/*      */     } 
/*      */     
/* 1629 */     if (!directory.isDirectory()) {
/*      */       
/* 1631 */       String message = directory + " is not a directory";
/* 1632 */       throw new IllegalArgumentException(message);
/*      */     } 
/*      */     
/* 1635 */     long size = 0L;
/*      */     
/* 1637 */     File[] files = directory.listFiles();
/* 1638 */     for (int i = 0; i < files.length; i++) {
/*      */       
/* 1640 */       File file = files[i];
/*      */       
/* 1642 */       if (file.isDirectory()) {
/*      */         
/* 1644 */         size += sizeOfDirectory(file);
/*      */       }
/*      */       else {
/*      */         
/* 1648 */         size += file.length();
/*      */       } 
/*      */     } 
/*      */     
/* 1652 */     return size;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List getFiles(File directory, String includes, String excludes) throws IOException {
/* 1669 */     return getFiles(directory, includes, excludes, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List getFiles(File directory, String includes, String excludes, boolean includeBasedir) throws IOException {
/* 1686 */     List fileNames = getFileNames(directory, includes, excludes, includeBasedir);
/*      */     
/* 1688 */     List files = new ArrayList();
/*      */     
/* 1690 */     for (Iterator i = fileNames.iterator(); i.hasNext();)
/*      */     {
/* 1692 */       files.add(new File(i.next()));
/*      */     }
/*      */     
/* 1695 */     return files;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List getFileNames(File directory, String includes, String excludes, boolean includeBasedir) throws IOException {
/* 1712 */     return getFileNames(directory, includes, excludes, includeBasedir, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List getFileNames(File directory, String includes, String excludes, boolean includeBasedir, boolean isCaseSensitive) throws IOException {
/* 1730 */     return getFileAndDirectoryNames(directory, includes, excludes, includeBasedir, isCaseSensitive, true, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List getDirectoryNames(File directory, String includes, String excludes, boolean includeBasedir) throws IOException {
/* 1747 */     return getDirectoryNames(directory, includes, excludes, includeBasedir, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List getDirectoryNames(File directory, String includes, String excludes, boolean includeBasedir, boolean isCaseSensitive) throws IOException {
/* 1765 */     return getFileAndDirectoryNames(directory, includes, excludes, includeBasedir, isCaseSensitive, false, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List getFileAndDirectoryNames(File directory, String includes, String excludes, boolean includeBasedir, boolean isCaseSensitive, boolean getFiles, boolean getDirectories) throws IOException {
/* 1786 */     DirectoryScanner scanner = new DirectoryScanner();
/*      */     
/* 1788 */     scanner.setBasedir(directory);
/*      */     
/* 1790 */     if (includes != null)
/*      */     {
/* 1792 */       scanner.setIncludes(StringUtils.split(includes, ","));
/*      */     }
/*      */     
/* 1795 */     if (excludes != null)
/*      */     {
/* 1797 */       scanner.setExcludes(StringUtils.split(excludes, ","));
/*      */     }
/*      */     
/* 1800 */     scanner.setCaseSensitive(isCaseSensitive);
/*      */     
/* 1802 */     scanner.scan();
/*      */     
/* 1804 */     List list = new ArrayList();
/*      */     
/* 1806 */     if (getFiles) {
/*      */       
/* 1808 */       String[] files = scanner.getIncludedFiles();
/*      */       
/* 1810 */       for (int i = 0; i < files.length; i++) {
/*      */         
/* 1812 */         if (includeBasedir) {
/*      */           
/* 1814 */           list.add(directory + FS + files[i]);
/*      */         }
/*      */         else {
/*      */           
/* 1818 */           list.add(files[i]);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/* 1823 */     if (getDirectories) {
/*      */       
/* 1825 */       String[] directories = scanner.getIncludedDirectories();
/*      */       
/* 1827 */       for (int i = 0; i < directories.length; i++) {
/*      */         
/* 1829 */         if (includeBasedir) {
/*      */           
/* 1831 */           list.add(directory + FS + directories[i]);
/*      */         }
/*      */         else {
/*      */           
/* 1835 */           list.add(directories[i]);
/*      */         } 
/*      */       } 
/*      */     } 
/*      */     
/* 1840 */     return list;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void copyDirectory(File sourceDirectory, File destinationDirectory) throws IOException {
/* 1853 */     copyDirectory(sourceDirectory, destinationDirectory, "**", null);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void copyDirectory(File sourceDirectory, File destinationDirectory, String includes, String excludes) throws IOException {
/* 1870 */     if (!sourceDirectory.exists()) {
/*      */       return;
/*      */     }
/*      */ 
/*      */     
/* 1875 */     List files = getFiles(sourceDirectory, includes, excludes);
/*      */     
/* 1877 */     for (Iterator i = files.iterator(); i.hasNext(); ) {
/*      */       
/* 1879 */       File file = i.next();
/*      */       
/* 1881 */       copyFileToDirectory(file, destinationDirectory);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void copyDirectoryLayout(File sourceDirectory, File destinationDirectory, String[] includes, String[] excludes) throws IOException {
/* 1905 */     if (sourceDirectory == null)
/*      */     {
/* 1907 */       throw new IOException("source directory can't be null.");
/*      */     }
/*      */     
/* 1910 */     if (destinationDirectory == null)
/*      */     {
/* 1912 */       throw new IOException("destination directory can't be null.");
/*      */     }
/*      */     
/* 1915 */     if (sourceDirectory.equals(destinationDirectory))
/*      */     {
/* 1917 */       throw new IOException("source and destination are the same directory.");
/*      */     }
/*      */     
/* 1920 */     if (!sourceDirectory.exists())
/*      */     {
/* 1922 */       throw new IOException("Source directory doesn't exists (" + sourceDirectory.getAbsolutePath() + ").");
/*      */     }
/*      */     
/* 1925 */     DirectoryScanner scanner = new DirectoryScanner();
/*      */     
/* 1927 */     scanner.setBasedir(sourceDirectory);
/*      */     
/* 1929 */     if (includes != null && includes.length >= 1) {
/*      */       
/* 1931 */       scanner.setIncludes(includes);
/*      */     }
/*      */     else {
/*      */       
/* 1935 */       scanner.setIncludes(new String[] { "**" });
/*      */     } 
/*      */     
/* 1938 */     if (excludes != null && excludes.length >= 1)
/*      */     {
/* 1940 */       scanner.setExcludes(excludes);
/*      */     }
/*      */     
/* 1943 */     scanner.addDefaultExcludes();
/* 1944 */     scanner.scan();
/* 1945 */     List includedDirectories = Arrays.asList(scanner.getIncludedDirectories());
/*      */     
/* 1947 */     for (Iterator i = includedDirectories.iterator(); i.hasNext(); ) {
/*      */       
/* 1949 */       String name = i.next();
/*      */       
/* 1951 */       File source = new File(sourceDirectory, name);
/*      */       
/* 1953 */       if (source.equals(sourceDirectory)) {
/*      */         continue;
/*      */       }
/*      */ 
/*      */       
/* 1958 */       File destination = new File(destinationDirectory, name);
/* 1959 */       destination.mkdirs();
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void copyDirectoryStructure(File sourceDirectory, File destinationDirectory) throws IOException {
/* 1979 */     copyDirectoryStructure(sourceDirectory, destinationDirectory, destinationDirectory, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void copyDirectoryStructureIfModified(File sourceDirectory, File destinationDirectory) throws IOException {
/* 1998 */     copyDirectoryStructure(sourceDirectory, destinationDirectory, destinationDirectory, true);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private static void copyDirectoryStructure(File sourceDirectory, File destinationDirectory, File rootDestinationDirectory, boolean onlyModifiedFiles) throws IOException {
/* 2005 */     if (sourceDirectory == null)
/*      */     {
/* 2007 */       throw new IOException("source directory can't be null.");
/*      */     }
/*      */     
/* 2010 */     if (destinationDirectory == null)
/*      */     {
/* 2012 */       throw new IOException("destination directory can't be null.");
/*      */     }
/*      */     
/* 2015 */     if (sourceDirectory.equals(destinationDirectory))
/*      */     {
/* 2017 */       throw new IOException("source and destination are the same directory.");
/*      */     }
/*      */     
/* 2020 */     if (!sourceDirectory.exists())
/*      */     {
/* 2022 */       throw new IOException("Source directory doesn't exists (" + sourceDirectory.getAbsolutePath() + ").");
/*      */     }
/*      */     
/* 2025 */     File[] files = sourceDirectory.listFiles();
/*      */     
/* 2027 */     String sourcePath = sourceDirectory.getAbsolutePath();
/*      */     
/* 2029 */     for (int i = 0; i < files.length; i++) {
/*      */       
/* 2031 */       File file = files[i];
/*      */       
/* 2033 */       if (!file.equals(rootDestinationDirectory)) {
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 2039 */         String dest = file.getAbsolutePath();
/*      */         
/* 2041 */         dest = dest.substring(sourcePath.length() + 1);
/*      */         
/* 2043 */         File destination = new File(destinationDirectory, dest);
/*      */         
/* 2045 */         if (file.isFile()) {
/*      */           
/* 2047 */           destination = destination.getParentFile();
/*      */           
/* 2049 */           if (onlyModifiedFiles)
/*      */           {
/* 2051 */             copyFileToDirectoryIfModified(file, destination);
/*      */           }
/*      */           else
/*      */           {
/* 2055 */             copyFileToDirectory(file, destination);
/*      */           }
/*      */         
/* 2058 */         } else if (file.isDirectory()) {
/*      */           
/* 2060 */           if (!destination.exists() && !destination.mkdirs())
/*      */           {
/* 2062 */             throw new IOException("Could not create destination directory '" + destination.getAbsolutePath() + "'.");
/*      */           }
/*      */ 
/*      */           
/* 2066 */           copyDirectoryStructure(file, destination, rootDestinationDirectory, onlyModifiedFiles);
/*      */         }
/*      */         else {
/*      */           
/* 2070 */           throw new IOException("Unknown file type: " + file.getAbsolutePath());
/*      */         } 
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void rename(File from, File to) throws IOException {
/* 2091 */     if (to.exists() && !to.delete())
/*      */     {
/* 2093 */       throw new IOException("Failed to delete " + to + " while trying to rename " + from);
/*      */     }
/*      */     
/* 2096 */     File parent = to.getParentFile();
/* 2097 */     if (parent != null && !parent.exists() && !parent.mkdirs())
/*      */     {
/* 2099 */       throw new IOException("Failed to create directory " + parent + " while trying to rename " + from);
/*      */     }
/*      */     
/* 2102 */     if (!from.renameTo(to)) {
/*      */       
/* 2104 */       copyFile(from, to);
/* 2105 */       if (!from.delete())
/*      */       {
/* 2107 */         throw new IOException("Failed to delete " + from + " while trying to rename it.");
/*      */       }
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static File createTempFile(String prefix, String suffix, File parentDir) {
/* 2137 */     File result = null;
/* 2138 */     String parent = System.getProperty("java.io.tmpdir");
/* 2139 */     if (parentDir != null)
/*      */     {
/* 2141 */       parent = parentDir.getPath();
/*      */     }
/* 2143 */     DecimalFormat fmt = new DecimalFormat("#####");
/* 2144 */     SecureRandom secureRandom = new SecureRandom();
/* 2145 */     long secureInitializer = secureRandom.nextLong();
/* 2146 */     Random rand = new Random(secureInitializer + Runtime.getRuntime().freeMemory());
/* 2147 */     synchronized (rand) {
/*      */       
/*      */       while (true) {
/*      */         
/* 2151 */         result = new File(parent, prefix + fmt.format(Math.abs(rand.nextInt())) + suffix);
/*      */         
/* 2153 */         if (!result.exists())
/*      */         {
/*      */           
/* 2156 */           return result;
/*      */         }
/*      */       } 
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void copyFile(File from, File to, String encoding, FilterWrapper[] wrappers) throws IOException {
/* 2170 */     copyFile(from, to, encoding, wrappers, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static abstract class FilterWrapper
/*      */   {
/*      */     public abstract Reader getReader(Reader param1Reader);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static void copyFile(File from, File to, String encoding, FilterWrapper[] wrappers, boolean overwrite) throws IOException {
/* 2192 */     if (wrappers != null && wrappers.length > 0) {
/*      */ 
/*      */       
/* 2195 */       Reader fileReader = null;
/* 2196 */       Writer fileWriter = null;
/*      */       
/*      */       try {
/* 2199 */         if (encoding == null || encoding.length() < 1) {
/*      */           
/* 2201 */           fileReader = new BufferedReader(new FileReader(from));
/* 2202 */           fileWriter = new FileWriter(to);
/*      */         }
/*      */         else {
/*      */           
/* 2206 */           FileInputStream instream = new FileInputStream(from);
/*      */           
/* 2208 */           FileOutputStream outstream = new FileOutputStream(to);
/*      */           
/* 2210 */           fileReader = new BufferedReader(new InputStreamReader(instream, encoding));
/*      */           
/* 2212 */           fileWriter = new OutputStreamWriter(outstream, encoding);
/*      */         } 
/*      */         
/* 2215 */         Reader reader = fileReader;
/* 2216 */         for (int i = 0; i < wrappers.length; i++) {
/*      */           
/* 2218 */           FilterWrapper wrapper = wrappers[i];
/* 2219 */           reader = wrapper.getReader(reader);
/*      */         } 
/*      */         
/* 2222 */         IOUtil.copy(reader, fileWriter);
/*      */       }
/*      */       finally {
/*      */         
/* 2226 */         IOUtil.close(fileReader);
/* 2227 */         IOUtil.close(fileWriter);
/*      */       
/*      */       }
/*      */     
/*      */     }
/* 2232 */     else if (to.lastModified() < from.lastModified() || overwrite) {
/*      */       
/* 2234 */       copyFile(from, to);
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static List loadFile(File file) throws IOException {
/* 2249 */     List lines = new ArrayList();
/*      */     
/* 2251 */     if (file.exists()) {
/*      */       
/* 2253 */       BufferedReader reader = new BufferedReader(new FileReader(file));
/*      */       
/* 2255 */       String line = reader.readLine();
/*      */       
/* 2257 */       while (line != null) {
/*      */         
/* 2259 */         line = line.trim();
/*      */         
/* 2261 */         if (!line.startsWith("#") && line.length() != 0)
/*      */         {
/* 2263 */           lines.add(line);
/*      */         }
/* 2265 */         line = reader.readLine();
/*      */       } 
/*      */       
/* 2268 */       reader.close();
/*      */     } 
/*      */     
/* 2271 */     return lines;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static boolean isValidWindowsFileName(File f) {
/* 2286 */     if (Os.isFamily("windows")) {
/*      */       
/* 2288 */       if (StringUtils.indexOfAny(f.getName(), INVALID_CHARACTERS_FOR_WINDOWS_FILE_NAME) != -1)
/*      */       {
/* 2290 */         return false;
/*      */       }
/*      */       
/* 2293 */       if (f.getParentFile() != null)
/*      */       {
/* 2295 */         return isValidWindowsFileName(f.getParentFile());
/*      */       }
/*      */     } 
/*      */     
/* 2299 */     return true;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\FileUtils.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */