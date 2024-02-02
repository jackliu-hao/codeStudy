/*      */ package cn.hutool.core.io;
/*      */ 
/*      */ import cn.hutool.core.collection.LineIter;
/*      */ import cn.hutool.core.convert.Convert;
/*      */ import cn.hutool.core.exceptions.UtilException;
/*      */ import cn.hutool.core.io.copy.ReaderWriterCopier;
/*      */ import cn.hutool.core.io.copy.StreamCopier;
/*      */ import cn.hutool.core.lang.Assert;
/*      */ import cn.hutool.core.util.CharsetUtil;
/*      */ import cn.hutool.core.util.HexUtil;
/*      */ import cn.hutool.core.util.StrUtil;
/*      */ import java.io.BufferedInputStream;
/*      */ import java.io.BufferedOutputStream;
/*      */ import java.io.BufferedReader;
/*      */ import java.io.BufferedWriter;
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.Closeable;
/*      */ import java.io.File;
/*      */ import java.io.FileInputStream;
/*      */ import java.io.FileNotFoundException;
/*      */ import java.io.FileOutputStream;
/*      */ import java.io.Flushable;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.InputStreamReader;
/*      */ import java.io.ObjectOutputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.OutputStreamWriter;
/*      */ import java.io.PushbackInputStream;
/*      */ import java.io.PushbackReader;
/*      */ import java.io.Reader;
/*      */ import java.io.Serializable;
/*      */ import java.io.UnsupportedEncodingException;
/*      */ import java.io.Writer;
/*      */ import java.nio.CharBuffer;
/*      */ import java.nio.channels.FileChannel;
/*      */ import java.nio.charset.Charset;
/*      */ import java.util.Objects;
/*      */ import java.util.zip.CRC32;
/*      */ import java.util.zip.CheckedInputStream;
/*      */ import java.util.zip.Checksum;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ public class IoUtil
/*      */   extends NioUtil
/*      */ {
/*      */   public static long copy(Reader reader, Writer writer) throws IORuntimeException {
/*   65 */     return copy(reader, writer, 8192);
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
/*      */   public static long copy(Reader reader, Writer writer, int bufferSize) throws IORuntimeException {
/*   78 */     return copy(reader, writer, bufferSize, (StreamProgress)null);
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
/*      */   public static long copy(Reader reader, Writer writer, int bufferSize, StreamProgress streamProgress) throws IORuntimeException {
/*   92 */     return copy(reader, writer, bufferSize, -1L, streamProgress);
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
/*      */   public static long copy(Reader reader, Writer writer, int bufferSize, long count, StreamProgress streamProgress) throws IORuntimeException {
/*  107 */     return (new ReaderWriterCopier(bufferSize, count, streamProgress)).copy(reader, writer);
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
/*      */   public static long copy(InputStream in, OutputStream out) throws IORuntimeException {
/*  119 */     return copy(in, out, 8192);
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
/*      */   public static long copy(InputStream in, OutputStream out, int bufferSize) throws IORuntimeException {
/*  132 */     return copy(in, out, bufferSize, (StreamProgress)null);
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
/*      */   public static long copy(InputStream in, OutputStream out, int bufferSize, StreamProgress streamProgress) throws IORuntimeException {
/*  146 */     return copy(in, out, bufferSize, -1L, streamProgress);
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
/*      */   public static long copy(InputStream in, OutputStream out, int bufferSize, long count, StreamProgress streamProgress) throws IORuntimeException {
/*  162 */     return (new StreamCopier(bufferSize, count, streamProgress)).copy(in, out);
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
/*      */   public static long copy(FileInputStream in, FileOutputStream out) throws IORuntimeException {
/*  174 */     Assert.notNull(in, "FileInputStream is null!", new Object[0]);
/*  175 */     Assert.notNull(out, "FileOutputStream is null!", new Object[0]);
/*      */     
/*  177 */     FileChannel inChannel = null;
/*  178 */     FileChannel outChannel = null;
/*      */     try {
/*  180 */       inChannel = in.getChannel();
/*  181 */       outChannel = out.getChannel();
/*  182 */       return copy(inChannel, outChannel);
/*      */     } finally {
/*  184 */       close(outChannel);
/*  185 */       close(inChannel);
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
/*      */   public static BufferedReader getUtf8Reader(InputStream in) {
/*  201 */     return getReader(in, CharsetUtil.CHARSET_UTF_8);
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
/*      */   @Deprecated
/*      */   public static BufferedReader getReader(InputStream in, String charsetName) {
/*  214 */     return getReader(in, Charset.forName(charsetName));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedReader getReader(BOMInputStream in) {
/*  225 */     return getReader(in, in.getCharset());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BomReader getBomReader(InputStream in) {
/*  236 */     return new BomReader(in);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedReader getReader(InputStream in, Charset charset) {
/*      */     InputStreamReader reader;
/*  247 */     if (null == in) {
/*  248 */       return null;
/*      */     }
/*      */ 
/*      */     
/*  252 */     if (null == charset) {
/*  253 */       reader = new InputStreamReader(in);
/*      */     } else {
/*  255 */       reader = new InputStreamReader(in, charset);
/*      */     } 
/*      */     
/*  258 */     return new BufferedReader(reader);
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
/*      */   public static BufferedReader getReader(Reader reader) {
/*  270 */     if (null == reader) {
/*  271 */       return null;
/*      */     }
/*      */     
/*  274 */     return (reader instanceof BufferedReader) ? (BufferedReader)reader : new BufferedReader(reader);
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
/*      */   public static PushbackReader getPushBackReader(Reader reader, int pushBackSize) {
/*  287 */     return (reader instanceof PushbackReader) ? (PushbackReader)reader : new PushbackReader(reader, pushBackSize);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static OutputStreamWriter getUtf8Writer(OutputStream out) {
/*  298 */     return getWriter(out, CharsetUtil.CHARSET_UTF_8);
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
/*      */   @Deprecated
/*      */   public static OutputStreamWriter getWriter(OutputStream out, String charsetName) {
/*  311 */     return getWriter(out, Charset.forName(charsetName));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static OutputStreamWriter getWriter(OutputStream out, Charset charset) {
/*  322 */     if (null == out) {
/*  323 */       return null;
/*      */     }
/*      */     
/*  326 */     if (null == charset) {
/*  327 */       return new OutputStreamWriter(out);
/*      */     }
/*  329 */     return new OutputStreamWriter(out, charset);
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
/*      */   public static String readUtf8(InputStream in) throws IORuntimeException {
/*  345 */     return read(in, CharsetUtil.CHARSET_UTF_8);
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
/*      */   @Deprecated
/*      */   public static String read(InputStream in, String charsetName) throws IORuntimeException {
/*  359 */     FastByteArrayOutputStream out = read(in);
/*  360 */     return StrUtil.isBlank(charsetName) ? out.toString() : out.toString(charsetName);
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
/*      */   public static String read(InputStream in, Charset charset) throws IORuntimeException {
/*  372 */     return StrUtil.str(readBytes(in), charset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static FastByteArrayOutputStream read(InputStream in) throws IORuntimeException {
/*  383 */     return read(in, true);
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
/*      */   public static FastByteArrayOutputStream read(InputStream in, boolean isClose) throws IORuntimeException {
/*      */     FastByteArrayOutputStream out;
/*  397 */     if (in instanceof FileInputStream) {
/*      */       
/*      */       try {
/*  400 */         out = new FastByteArrayOutputStream(in.available());
/*  401 */       } catch (IOException e) {
/*  402 */         throw new IORuntimeException(e);
/*      */       } 
/*      */     } else {
/*  405 */       out = new FastByteArrayOutputStream();
/*      */     } 
/*      */     try {
/*  408 */       copy(in, out);
/*      */     } finally {
/*  410 */       if (isClose) {
/*  411 */         close(in);
/*      */       }
/*      */     } 
/*  414 */     return out;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String read(Reader reader) throws IORuntimeException {
/*  425 */     return read(reader, true);
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
/*      */   public static String read(Reader reader, boolean isClose) throws IORuntimeException {
/*  437 */     StringBuilder builder = StrUtil.builder();
/*  438 */     CharBuffer buffer = CharBuffer.allocate(8192);
/*      */     try {
/*  440 */       while (-1 != reader.read(buffer)) {
/*  441 */         builder.append(buffer.flip());
/*      */       }
/*  443 */     } catch (IOException e) {
/*  444 */       throw new IORuntimeException(e);
/*      */     } finally {
/*  446 */       if (isClose) {
/*  447 */         close(reader);
/*      */       }
/*      */     } 
/*  450 */     return builder.toString();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static byte[] readBytes(InputStream in) throws IORuntimeException {
/*  461 */     return readBytes(in, true);
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
/*      */   public static byte[] readBytes(InputStream in, boolean isClose) throws IORuntimeException {
/*  474 */     if (in instanceof FileInputStream) {
/*      */       byte[] result;
/*      */       
/*      */       try {
/*  478 */         int available = in.available();
/*  479 */         result = new byte[available];
/*  480 */         int readLength = in.read(result);
/*  481 */         if (readLength != available) {
/*  482 */           throw new IOException(StrUtil.format("File length is [{}] but read [{}]!", new Object[] { Integer.valueOf(available), Integer.valueOf(readLength) }));
/*      */         }
/*  484 */       } catch (IOException e) {
/*  485 */         throw new IORuntimeException(e);
/*      */       } finally {
/*  487 */         if (isClose) {
/*  488 */           close(in);
/*      */         }
/*      */       } 
/*  491 */       return result;
/*      */     } 
/*      */ 
/*      */     
/*  495 */     return read(in, isClose).toByteArray();
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
/*      */   public static byte[] readBytes(InputStream in, int length) throws IORuntimeException {
/*  507 */     if (null == in) {
/*  508 */       return null;
/*      */     }
/*  510 */     if (length <= 0) {
/*  511 */       return new byte[0];
/*      */     }
/*      */     
/*  514 */     FastByteArrayOutputStream out = new FastByteArrayOutputStream(length);
/*  515 */     copy(in, out, 8192, length, (StreamProgress)null);
/*  516 */     return out.toByteArray();
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
/*      */   public static String readHex(InputStream in, int length, boolean toLowerCase) throws IORuntimeException {
/*  529 */     return HexUtil.encodeHexStr(readBytes(in, length), toLowerCase);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String readHex28Upper(InputStream in) throws IORuntimeException {
/*  540 */     return readHex(in, 28, false);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String readHex28Lower(InputStream in) throws IORuntimeException {
/*  551 */     return readHex(in, 28, true);
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
/*      */   public static <T> T readObj(InputStream in) throws IORuntimeException, UtilException {
/*  568 */     return readObj(in, (Class<T>)null);
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
/*      */   public static <T> T readObj(InputStream in, Class<T> clazz) throws IORuntimeException, UtilException {
/*      */     try {
/*  587 */       return readObj((in instanceof ValidateObjectInputStream) ? (ValidateObjectInputStream)in : new ValidateObjectInputStream(in, new Class[0]), clazz);
/*      */     
/*      */     }
/*  590 */     catch (IOException e) {
/*  591 */       throw new IORuntimeException(e);
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
/*      */   public static <T> T readObj(ValidateObjectInputStream in, Class<T> clazz) throws IORuntimeException, UtilException {
/*  612 */     if (in == null) {
/*  613 */       throw new IllegalArgumentException("The InputStream must not be null");
/*      */     }
/*  615 */     if (null != clazz) {
/*  616 */       in.accept(new Class[] { clazz });
/*      */     }
/*      */     
/*      */     try {
/*  620 */       return (T)in.readObject();
/*  621 */     } catch (IOException e) {
/*  622 */       throw new IORuntimeException(e);
/*  623 */     } catch (ClassNotFoundException e) {
/*  624 */       throw new UtilException(e);
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
/*      */   public static <T extends java.util.Collection<String>> T readUtf8Lines(InputStream in, T collection) throws IORuntimeException {
/*  638 */     return readLines(in, CharsetUtil.CHARSET_UTF_8, collection);
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
/*      */   @Deprecated
/*      */   public static <T extends java.util.Collection<String>> T readLines(InputStream in, String charsetName, T collection) throws IORuntimeException {
/*  654 */     return readLines(in, CharsetUtil.charset(charsetName), collection);
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
/*      */   public static <T extends java.util.Collection<String>> T readLines(InputStream in, Charset charset, T collection) throws IORuntimeException {
/*  668 */     return readLines(getReader(in, charset), collection);
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
/*      */   public static <T extends java.util.Collection<String>> T readLines(Reader reader, T collection) throws IORuntimeException {
/*  681 */     readLines(reader, collection::add);
/*  682 */     return collection;
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
/*      */   public static void readUtf8Lines(InputStream in, LineHandler lineHandler) throws IORuntimeException {
/*  694 */     readLines(in, CharsetUtil.CHARSET_UTF_8, lineHandler);
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
/*      */   public static void readLines(InputStream in, Charset charset, LineHandler lineHandler) throws IORuntimeException {
/*  707 */     readLines(getReader(in, charset), lineHandler);
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
/*      */   public static void readLines(Reader reader, LineHandler lineHandler) throws IORuntimeException {
/*  720 */     Assert.notNull(reader);
/*  721 */     Assert.notNull(lineHandler);
/*      */     
/*  723 */     for (String line : lineIter(reader)) {
/*  724 */       lineHandler.handle(line);
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
/*      */   @Deprecated
/*      */   public static ByteArrayInputStream toStream(String content, String charsetName) {
/*  740 */     return toStream(content, CharsetUtil.charset(charsetName));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ByteArrayInputStream toStream(String content, Charset charset) {
/*  751 */     if (content == null) {
/*  752 */       return null;
/*      */     }
/*  754 */     return toStream(StrUtil.bytes(content, charset));
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ByteArrayInputStream toUtf8Stream(String content) {
/*  765 */     return toStream(content, CharsetUtil.CHARSET_UTF_8);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static FileInputStream toStream(File file) {
/*      */     try {
/*  776 */       return new FileInputStream(file);
/*  777 */     } catch (FileNotFoundException e) {
/*  778 */       throw new IORuntimeException(e);
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
/*      */   public static ByteArrayInputStream toStream(byte[] content) {
/*  790 */     if (content == null) {
/*  791 */       return null;
/*      */     }
/*  793 */     return new ByteArrayInputStream(content);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static ByteArrayInputStream toStream(ByteArrayOutputStream out) {
/*  804 */     if (out == null) {
/*  805 */       return null;
/*      */     }
/*  807 */     return new ByteArrayInputStream(out.toByteArray());
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedInputStream toBuffered(InputStream in) {
/*  818 */     Assert.notNull(in, "InputStream must be not null!", new Object[0]);
/*  819 */     return (in instanceof BufferedInputStream) ? (BufferedInputStream)in : new BufferedInputStream(in);
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
/*      */   public static BufferedInputStream toBuffered(InputStream in, int bufferSize) {
/*  831 */     Assert.notNull(in, "InputStream must be not null!", new Object[0]);
/*  832 */     return (in instanceof BufferedInputStream) ? (BufferedInputStream)in : new BufferedInputStream(in, bufferSize);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedOutputStream toBuffered(OutputStream out) {
/*  843 */     Assert.notNull(out, "OutputStream must be not null!", new Object[0]);
/*  844 */     return (out instanceof BufferedOutputStream) ? (BufferedOutputStream)out : new BufferedOutputStream(out);
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
/*      */   public static BufferedOutputStream toBuffered(OutputStream out, int bufferSize) {
/*  856 */     Assert.notNull(out, "OutputStream must be not null!", new Object[0]);
/*  857 */     return (out instanceof BufferedOutputStream) ? (BufferedOutputStream)out : new BufferedOutputStream(out, bufferSize);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedReader toBuffered(Reader reader) {
/*  868 */     Assert.notNull(reader, "Reader must be not null!", new Object[0]);
/*  869 */     return (reader instanceof BufferedReader) ? (BufferedReader)reader : new BufferedReader(reader);
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
/*      */   public static BufferedReader toBuffered(Reader reader, int bufferSize) {
/*  881 */     Assert.notNull(reader, "Reader must be not null!", new Object[0]);
/*  882 */     return (reader instanceof BufferedReader) ? (BufferedReader)reader : new BufferedReader(reader, bufferSize);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static BufferedWriter toBuffered(Writer writer) {
/*  893 */     Assert.notNull(writer, "Writer must be not null!", new Object[0]);
/*  894 */     return (writer instanceof BufferedWriter) ? (BufferedWriter)writer : new BufferedWriter(writer);
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
/*      */   public static BufferedWriter toBuffered(Writer writer, int bufferSize) {
/*  906 */     Assert.notNull(writer, "Writer must be not null!", new Object[0]);
/*  907 */     return (writer instanceof BufferedWriter) ? (BufferedWriter)writer : new BufferedWriter(writer, bufferSize);
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
/*      */   public static InputStream toMarkSupportStream(InputStream in) {
/*  919 */     if (null == in) {
/*  920 */       return null;
/*      */     }
/*  922 */     if (false == in.markSupported()) {
/*  923 */       return new BufferedInputStream(in);
/*      */     }
/*  925 */     return in;
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
/*      */   public static PushbackInputStream toPushbackStream(InputStream in, int pushBackSize) {
/*  938 */     return (in instanceof PushbackInputStream) ? (PushbackInputStream)in : new PushbackInputStream(in, pushBackSize);
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
/*      */   public static InputStream toAvailableStream(InputStream in) {
/*  958 */     if (in instanceof FileInputStream)
/*      */     {
/*  960 */       return in;
/*      */     }
/*      */     
/*  963 */     PushbackInputStream pushbackInputStream = toPushbackStream(in, 1);
/*      */     try {
/*  965 */       int available = pushbackInputStream.available();
/*  966 */       if (available <= 0) {
/*      */         
/*  968 */         int b = pushbackInputStream.read();
/*  969 */         pushbackInputStream.unread(b);
/*      */       } 
/*  971 */     } catch (IOException e) {
/*  972 */       throw new IORuntimeException(e);
/*      */     } 
/*      */     
/*  975 */     return pushbackInputStream;
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
/*      */   public static void write(OutputStream out, boolean isCloseOut, byte[] content) throws IORuntimeException {
/*      */     try {
/*  988 */       out.write(content);
/*  989 */     } catch (IOException e) {
/*  990 */       throw new IORuntimeException(e);
/*      */     } finally {
/*  992 */       if (isCloseOut) {
/*  993 */         close(out);
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
/*      */   public static void writeUtf8(OutputStream out, boolean isCloseOut, Object... contents) throws IORuntimeException {
/* 1008 */     write(out, CharsetUtil.CHARSET_UTF_8, isCloseOut, contents);
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
/*      */   @Deprecated
/*      */   public static void write(OutputStream out, String charsetName, boolean isCloseOut, Object... contents) throws IORuntimeException {
/* 1023 */     write(out, CharsetUtil.charset(charsetName), isCloseOut, contents);
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
/*      */   public static void write(OutputStream out, Charset charset, boolean isCloseOut, Object... contents) throws IORuntimeException {
/* 1037 */     OutputStreamWriter osw = null;
/*      */     try {
/* 1039 */       osw = getWriter(out, charset);
/* 1040 */       for (Object content : contents) {
/* 1041 */         if (content != null) {
/* 1042 */           osw.write(Convert.toStr(content, ""));
/*      */         }
/*      */       } 
/* 1045 */       osw.flush();
/* 1046 */     } catch (IOException e) {
/* 1047 */       throw new IORuntimeException(e);
/*      */     } finally {
/* 1049 */       if (isCloseOut) {
/* 1050 */         close(osw);
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
/*      */   public static void writeObj(OutputStream out, boolean isCloseOut, Serializable obj) throws IORuntimeException {
/* 1065 */     writeObjects(out, isCloseOut, new Serializable[] { obj });
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
/*      */   public static void writeObjects(OutputStream out, boolean isCloseOut, Serializable... contents) throws IORuntimeException {
/* 1077 */     ObjectOutputStream osw = null;
/*      */     try {
/* 1079 */       osw = (out instanceof ObjectOutputStream) ? (ObjectOutputStream)out : new ObjectOutputStream(out);
/* 1080 */       for (Serializable content : contents) {
/* 1081 */         if (content != null) {
/* 1082 */           osw.writeObject(content);
/*      */         }
/*      */       } 
/* 1085 */       osw.flush();
/* 1086 */     } catch (IOException e) {
/* 1087 */       throw new IORuntimeException(e);
/*      */     } finally {
/* 1089 */       if (isCloseOut) {
/* 1090 */         close(osw);
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
/*      */   public static void flush(Flushable flushable) {
/* 1102 */     if (null != flushable) {
/*      */       try {
/* 1104 */         flushable.flush();
/* 1105 */       } catch (Exception exception) {}
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
/*      */   public static void close(Closeable closeable) {
/* 1118 */     if (null != closeable) {
/*      */       try {
/* 1120 */         closeable.close();
/* 1121 */       } catch (Exception exception) {}
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
/*      */   public static void closeIfPosible(Object obj) {
/* 1135 */     if (obj instanceof AutoCloseable) {
/* 1136 */       close((AutoCloseable)obj);
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
/*      */   public static boolean contentEquals(InputStream input1, InputStream input2) throws IORuntimeException {
/* 1151 */     if (false == input1 instanceof BufferedInputStream) {
/* 1152 */       input1 = new BufferedInputStream(input1);
/*      */     }
/* 1154 */     if (false == input2 instanceof BufferedInputStream) {
/* 1155 */       input2 = new BufferedInputStream(input2);
/*      */     }
/*      */     
/*      */     try {
/* 1159 */       int ch = input1.read();
/* 1160 */       while (-1 != ch) {
/* 1161 */         int i = input2.read();
/* 1162 */         if (ch != i) {
/* 1163 */           return false;
/*      */         }
/* 1165 */         ch = input1.read();
/*      */       } 
/*      */       
/* 1168 */       int ch2 = input2.read();
/* 1169 */       return (ch2 == -1);
/* 1170 */     } catch (IOException e) {
/* 1171 */       throw new IORuntimeException(e);
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
/*      */   public static boolean contentEquals(Reader input1, Reader input2) throws IORuntimeException {
/* 1186 */     input1 = getReader(input1);
/* 1187 */     input2 = getReader(input2);
/*      */     
/*      */     try {
/* 1190 */       int ch = input1.read();
/* 1191 */       while (-1 != ch) {
/* 1192 */         int i = input2.read();
/* 1193 */         if (ch != i) {
/* 1194 */           return false;
/*      */         }
/* 1196 */         ch = input1.read();
/*      */       } 
/*      */       
/* 1199 */       int ch2 = input2.read();
/* 1200 */       return (ch2 == -1);
/* 1201 */     } catch (IOException e) {
/* 1202 */       throw new IORuntimeException(e);
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
/*      */   public static boolean contentEqualsIgnoreEOL(Reader input1, Reader input2) throws IORuntimeException {
/* 1217 */     BufferedReader br1 = getReader(input1);
/* 1218 */     BufferedReader br2 = getReader(input2);
/*      */     
/*      */     try {
/* 1221 */       String line1 = br1.readLine();
/* 1222 */       String line2 = br2.readLine();
/* 1223 */       while (line1 != null && line1.equals(line2)) {
/* 1224 */         line1 = br1.readLine();
/* 1225 */         line2 = br2.readLine();
/*      */       } 
/* 1227 */       return Objects.equals(line1, line2);
/* 1228 */     } catch (IOException e) {
/* 1229 */       throw new IORuntimeException(e);
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
/*      */   public static long checksumCRC32(InputStream in) throws IORuntimeException {
/* 1242 */     return checksum(in, new CRC32()).getValue();
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
/*      */   public static Checksum checksum(InputStream in, Checksum checksum) throws IORuntimeException {
/* 1255 */     Assert.notNull(in, "InputStream is null !", new Object[0]);
/* 1256 */     if (null == checksum) {
/* 1257 */       checksum = new CRC32();
/*      */     }
/*      */     try {
/* 1260 */       in = new CheckedInputStream(in, checksum);
/* 1261 */       copy(in, new NullOutputStream());
/*      */     } finally {
/* 1263 */       close(in);
/*      */     } 
/* 1265 */     return checksum;
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
/*      */   public static long checksumValue(InputStream in, Checksum checksum) {
/* 1278 */     return checksum(in, checksum).getValue();
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
/*      */   public static LineIter lineIter(Reader reader) {
/* 1301 */     return new LineIter(reader);
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
/*      */   public static LineIter lineIter(InputStream in, Charset charset) {
/* 1325 */     return new LineIter(in, charset);
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   public static String toStr(ByteArrayOutputStream out, Charset charset) {
/*      */     try {
/* 1337 */       return out.toString(charset.name());
/* 1338 */     } catch (UnsupportedEncodingException e) {
/* 1339 */       throw new IORuntimeException(e);
/*      */     } 
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\cn\hutool\core\io\IoUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */