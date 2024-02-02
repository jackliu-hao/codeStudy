/*     */ package org.codehaus.plexus.util;
/*     */ 
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.ByteArrayOutputStream;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.Reader;
/*     */ import java.io.StringReader;
/*     */ import java.io.StringWriter;
/*     */ import java.io.Writer;
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
/*     */ public final class IOUtil
/*     */ {
/*     */   private static final int DEFAULT_BUFFER_SIZE = 4096;
/*     */   
/*     */   public static void copy(InputStream input, OutputStream output) throws IOException {
/* 174 */     copy(input, output, 4096);
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
/*     */   public static void copy(InputStream input, OutputStream output, int bufferSize) throws IOException {
/* 186 */     byte[] buffer = new byte[bufferSize];
/* 187 */     int n = 0;
/* 188 */     while (-1 != (n = input.read(buffer)))
/*     */     {
/* 190 */       output.write(buffer, 0, n);
/*     */     }
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void copy(Reader input, Writer output) throws IOException {
/* 200 */     copy(input, output, 4096);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void copy(Reader input, Writer output, int bufferSize) throws IOException {
/* 210 */     char[] buffer = new char[bufferSize];
/* 211 */     int n = 0;
/* 212 */     while (-1 != (n = input.read(buffer)))
/*     */     {
/* 214 */       output.write(buffer, 0, n);
/*     */     }
/* 216 */     output.flush();
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
/*     */   public static void copy(InputStream input, Writer output) throws IOException {
/* 236 */     copy(input, output, 4096);
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
/*     */   public static void copy(InputStream input, Writer output, int bufferSize) throws IOException {
/* 248 */     InputStreamReader in = new InputStreamReader(input);
/* 249 */     copy(in, output, bufferSize);
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
/*     */   public static void copy(InputStream input, Writer output, String encoding) throws IOException {
/* 262 */     InputStreamReader in = new InputStreamReader(input, encoding);
/* 263 */     copy(in, output);
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
/*     */   public static void copy(InputStream input, Writer output, String encoding, int bufferSize) throws IOException {
/* 280 */     InputStreamReader in = new InputStreamReader(input, encoding);
/* 281 */     copy(in, output, bufferSize);
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
/*     */   public static String toString(InputStream input) throws IOException {
/* 295 */     return toString(input, 4096);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toString(InputStream input, int bufferSize) throws IOException {
/* 306 */     StringWriter sw = new StringWriter();
/* 307 */     copy(input, sw, bufferSize);
/* 308 */     return sw.toString();
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
/*     */   public static String toString(InputStream input, String encoding) throws IOException {
/* 320 */     return toString(input, encoding, 4096);
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
/*     */   public static String toString(InputStream input, String encoding, int bufferSize) throws IOException {
/* 335 */     StringWriter sw = new StringWriter();
/* 336 */     copy(input, sw, encoding, bufferSize);
/* 337 */     return sw.toString();
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
/*     */   public static byte[] toByteArray(InputStream input) throws IOException {
/* 349 */     return toByteArray(input, 4096);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] toByteArray(InputStream input, int bufferSize) throws IOException {
/* 359 */     ByteArrayOutputStream output = new ByteArrayOutputStream();
/* 360 */     copy(input, output, bufferSize);
/* 361 */     return output.toByteArray();
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
/*     */   public static void copy(Reader input, OutputStream output) throws IOException {
/* 379 */     copy(input, output, 4096);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void copy(Reader input, OutputStream output, int bufferSize) throws IOException {
/* 390 */     OutputStreamWriter out = new OutputStreamWriter(output);
/* 391 */     copy(input, out, bufferSize);
/*     */ 
/*     */     
/* 394 */     out.flush();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toString(Reader input) throws IOException {
/* 405 */     return toString(input, 4096);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toString(Reader input, int bufferSize) throws IOException {
/* 415 */     StringWriter sw = new StringWriter();
/* 416 */     copy(input, sw, bufferSize);
/* 417 */     return sw.toString();
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
/*     */   public static byte[] toByteArray(Reader input) throws IOException {
/* 429 */     return toByteArray(input, 4096);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] toByteArray(Reader input, int bufferSize) throws IOException {
/* 439 */     ByteArrayOutputStream output = new ByteArrayOutputStream();
/* 440 */     copy(input, output, bufferSize);
/* 441 */     return output.toByteArray();
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
/*     */   public static void copy(String input, OutputStream output) throws IOException {
/* 461 */     copy(input, output, 4096);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static void copy(String input, OutputStream output, int bufferSize) throws IOException {
/* 472 */     StringReader in = new StringReader(input);
/* 473 */     OutputStreamWriter out = new OutputStreamWriter(output);
/* 474 */     copy(in, out, bufferSize);
/*     */ 
/*     */     
/* 477 */     out.flush();
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
/*     */   public static void copy(String input, Writer output) throws IOException {
/* 491 */     output.write(input);
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
/*     */   public static void bufferedCopy(InputStream input, OutputStream output) throws IOException {
/* 508 */     BufferedInputStream in = new BufferedInputStream(input);
/* 509 */     BufferedOutputStream out = new BufferedOutputStream(output);
/* 510 */     copy(in, out);
/* 511 */     out.flush();
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
/*     */   public static byte[] toByteArray(String input) throws IOException {
/* 523 */     return toByteArray(input, 4096);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static byte[] toByteArray(String input, int bufferSize) throws IOException {
/* 533 */     ByteArrayOutputStream output = new ByteArrayOutputStream();
/* 534 */     copy(input, output, bufferSize);
/* 535 */     return output.toByteArray();
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
/*     */   public static void copy(byte[] input, Writer output) throws IOException {
/* 557 */     copy(input, output, 4096);
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
/*     */   public static void copy(byte[] input, Writer output, int bufferSize) throws IOException {
/* 569 */     ByteArrayInputStream in = new ByteArrayInputStream(input);
/* 570 */     copy(in, output, bufferSize);
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
/*     */   public static void copy(byte[] input, Writer output, String encoding) throws IOException {
/* 583 */     ByteArrayInputStream in = new ByteArrayInputStream(input);
/* 584 */     copy(in, output, encoding);
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
/*     */   public static void copy(byte[] input, Writer output, String encoding, int bufferSize) throws IOException {
/* 601 */     ByteArrayInputStream in = new ByteArrayInputStream(input);
/* 602 */     copy(in, output, encoding, bufferSize);
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
/*     */   public static String toString(byte[] input) throws IOException {
/* 616 */     return toString(input, 4096);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static String toString(byte[] input, int bufferSize) throws IOException {
/* 627 */     StringWriter sw = new StringWriter();
/* 628 */     copy(input, sw, bufferSize);
/* 629 */     return sw.toString();
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
/*     */   public static String toString(byte[] input, String encoding) throws IOException {
/* 641 */     return toString(input, encoding, 4096);
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
/*     */   public static String toString(byte[] input, String encoding, int bufferSize) throws IOException {
/* 656 */     StringWriter sw = new StringWriter();
/* 657 */     copy(input, sw, encoding, bufferSize);
/* 658 */     return sw.toString();
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
/*     */   public static void copy(byte[] input, OutputStream output) throws IOException {
/* 671 */     copy(input, output, 4096);
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
/*     */   public static void copy(byte[] input, OutputStream output, int bufferSize) throws IOException {
/* 683 */     output.write(input);
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
/*     */   public static boolean contentEquals(InputStream input1, InputStream input2) throws IOException {
/* 697 */     InputStream bufferedInput1 = new BufferedInputStream(input1);
/* 698 */     InputStream bufferedInput2 = new BufferedInputStream(input2);
/*     */     
/* 700 */     int ch = bufferedInput1.read();
/* 701 */     while (-1 != ch) {
/*     */       
/* 703 */       int i = bufferedInput2.read();
/* 704 */       if (ch != i)
/*     */       {
/* 706 */         return false;
/*     */       }
/* 708 */       ch = bufferedInput1.read();
/*     */     } 
/*     */     
/* 711 */     int ch2 = bufferedInput2.read();
/* 712 */     if (-1 != ch2)
/*     */     {
/* 714 */       return false;
/*     */     }
/*     */ 
/*     */     
/* 718 */     return true;
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
/*     */   public static void close(InputStream inputStream) {
/* 733 */     if (inputStream == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 740 */       inputStream.close();
/*     */     }
/* 742 */     catch (IOException ex) {}
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
/*     */   public static void close(OutputStream outputStream) {
/* 755 */     if (outputStream == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 762 */       outputStream.close();
/*     */     }
/* 764 */     catch (IOException ex) {}
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
/*     */   public static void close(Reader reader) {
/* 777 */     if (reader == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 784 */       reader.close();
/*     */     }
/* 786 */     catch (IOException ex) {}
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
/*     */   public static void close(Writer writer) {
/* 799 */     if (writer == null) {
/*     */       return;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     try {
/* 806 */       writer.close();
/*     */     }
/* 808 */     catch (IOException ex) {}
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\codehaus\plexu\\util\IOUtil.class
 * Java compiler version: 3 (47.0)
 * JD-Core Version:       1.1.3
 */