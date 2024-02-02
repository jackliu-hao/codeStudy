package cn.hutool.core.io;

import cn.hutool.core.collection.LineIter;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.io.copy.ReaderWriterCopier;
import cn.hutool.core.io.copy.StreamCopier;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PushbackInputStream;
import java.io.PushbackReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;
import java.util.zip.Checksum;

public class IoUtil extends NioUtil {
   public static long copy(Reader reader, Writer writer) throws IORuntimeException {
      return copy((Reader)reader, (Writer)writer, 8192);
   }

   public static long copy(Reader reader, Writer writer, int bufferSize) throws IORuntimeException {
      return copy((Reader)reader, (Writer)writer, bufferSize, (StreamProgress)null);
   }

   public static long copy(Reader reader, Writer writer, int bufferSize, StreamProgress streamProgress) throws IORuntimeException {
      return copy(reader, writer, bufferSize, -1L, streamProgress);
   }

   public static long copy(Reader reader, Writer writer, int bufferSize, long count, StreamProgress streamProgress) throws IORuntimeException {
      return (new ReaderWriterCopier(bufferSize, count, streamProgress)).copy(reader, writer);
   }

   public static long copy(InputStream in, OutputStream out) throws IORuntimeException {
      return copy((InputStream)in, (OutputStream)out, 8192);
   }

   public static long copy(InputStream in, OutputStream out, int bufferSize) throws IORuntimeException {
      return copy((InputStream)in, (OutputStream)out, bufferSize, (StreamProgress)null);
   }

   public static long copy(InputStream in, OutputStream out, int bufferSize, StreamProgress streamProgress) throws IORuntimeException {
      return copy(in, out, bufferSize, -1L, streamProgress);
   }

   public static long copy(InputStream in, OutputStream out, int bufferSize, long count, StreamProgress streamProgress) throws IORuntimeException {
      return (new StreamCopier(bufferSize, count, streamProgress)).copy(in, out);
   }

   public static long copy(FileInputStream in, FileOutputStream out) throws IORuntimeException {
      Assert.notNull(in, "FileInputStream is null!");
      Assert.notNull(out, "FileOutputStream is null!");
      FileChannel inChannel = null;
      FileChannel outChannel = null;

      long var4;
      try {
         inChannel = in.getChannel();
         outChannel = out.getChannel();
         var4 = copy((FileChannel)inChannel, (FileChannel)outChannel);
      } finally {
         close(outChannel);
         close(inChannel);
      }

      return var4;
   }

   public static BufferedReader getUtf8Reader(InputStream in) {
      return getReader(in, CharsetUtil.CHARSET_UTF_8);
   }

   /** @deprecated */
   @Deprecated
   public static BufferedReader getReader(InputStream in, String charsetName) {
      return getReader(in, Charset.forName(charsetName));
   }

   public static BufferedReader getReader(BOMInputStream in) {
      return getReader(in, (String)in.getCharset());
   }

   public static BomReader getBomReader(InputStream in) {
      return new BomReader(in);
   }

   public static BufferedReader getReader(InputStream in, Charset charset) {
      if (null == in) {
         return null;
      } else {
         InputStreamReader reader;
         if (null == charset) {
            reader = new InputStreamReader(in);
         } else {
            reader = new InputStreamReader(in, charset);
         }

         return new BufferedReader(reader);
      }
   }

   public static BufferedReader getReader(Reader reader) {
      if (null == reader) {
         return null;
      } else {
         return reader instanceof BufferedReader ? (BufferedReader)reader : new BufferedReader(reader);
      }
   }

   public static PushbackReader getPushBackReader(Reader reader, int pushBackSize) {
      return reader instanceof PushbackReader ? (PushbackReader)reader : new PushbackReader(reader, pushBackSize);
   }

   public static OutputStreamWriter getUtf8Writer(OutputStream out) {
      return getWriter(out, CharsetUtil.CHARSET_UTF_8);
   }

   /** @deprecated */
   @Deprecated
   public static OutputStreamWriter getWriter(OutputStream out, String charsetName) {
      return getWriter(out, Charset.forName(charsetName));
   }

   public static OutputStreamWriter getWriter(OutputStream out, Charset charset) {
      if (null == out) {
         return null;
      } else {
         return null == charset ? new OutputStreamWriter(out) : new OutputStreamWriter(out, charset);
      }
   }

   public static String readUtf8(InputStream in) throws IORuntimeException {
      return read(in, CharsetUtil.CHARSET_UTF_8);
   }

   /** @deprecated */
   @Deprecated
   public static String read(InputStream in, String charsetName) throws IORuntimeException {
      FastByteArrayOutputStream out = read(in);
      return StrUtil.isBlank(charsetName) ? out.toString() : out.toString(charsetName);
   }

   public static String read(InputStream in, Charset charset) throws IORuntimeException {
      return StrUtil.str(readBytes(in), charset);
   }

   public static FastByteArrayOutputStream read(InputStream in) throws IORuntimeException {
      return read(in, true);
   }

   public static FastByteArrayOutputStream read(InputStream in, boolean isClose) throws IORuntimeException {
      FastByteArrayOutputStream out;
      if (in instanceof FileInputStream) {
         try {
            out = new FastByteArrayOutputStream(in.available());
         } catch (IOException var7) {
            throw new IORuntimeException(var7);
         }
      } else {
         out = new FastByteArrayOutputStream();
      }

      try {
         copy((InputStream)in, (OutputStream)out);
      } finally {
         if (isClose) {
            close(in);
         }

      }

      return out;
   }

   public static String read(Reader reader) throws IORuntimeException {
      return read(reader, true);
   }

   public static String read(Reader reader, boolean isClose) throws IORuntimeException {
      StringBuilder builder = StrUtil.builder();
      CharBuffer buffer = CharBuffer.allocate(8192);

      try {
         while(-1 != reader.read(buffer)) {
            builder.append(buffer.flip());
         }
      } catch (IOException var8) {
         throw new IORuntimeException(var8);
      } finally {
         if (isClose) {
            close(reader);
         }

      }

      return builder.toString();
   }

   public static byte[] readBytes(InputStream in) throws IORuntimeException {
      return readBytes(in, true);
   }

   public static byte[] readBytes(InputStream in, boolean isClose) throws IORuntimeException {
      if (!(in instanceof FileInputStream)) {
         return read(in, isClose).toByteArray();
      } else {
         byte[] result;
         try {
            int available = in.available();
            result = new byte[available];
            int readLength = in.read(result);
            if (readLength != available) {
               throw new IOException(StrUtil.format("File length is [{}] but read [{}]!", new Object[]{available, readLength}));
            }
         } catch (IOException var8) {
            throw new IORuntimeException(var8);
         } finally {
            if (isClose) {
               close(in);
            }

         }

         return result;
      }
   }

   public static byte[] readBytes(InputStream in, int length) throws IORuntimeException {
      if (null == in) {
         return null;
      } else if (length <= 0) {
         return new byte[0];
      } else {
         FastByteArrayOutputStream out = new FastByteArrayOutputStream(length);
         copy((InputStream)in, (OutputStream)out, 8192, (long)length, (StreamProgress)null);
         return out.toByteArray();
      }
   }

   public static String readHex(InputStream in, int length, boolean toLowerCase) throws IORuntimeException {
      return HexUtil.encodeHexStr(readBytes(in, length), toLowerCase);
   }

   public static String readHex28Upper(InputStream in) throws IORuntimeException {
      return readHex(in, 28, false);
   }

   public static String readHex28Lower(InputStream in) throws IORuntimeException {
      return readHex(in, 28, true);
   }

   public static <T> T readObj(InputStream in) throws IORuntimeException, UtilException {
      return readObj((InputStream)in, (Class)null);
   }

   public static <T> T readObj(InputStream in, Class<T> clazz) throws IORuntimeException, UtilException {
      try {
         return readObj(in instanceof ValidateObjectInputStream ? (ValidateObjectInputStream)in : new ValidateObjectInputStream(in, new Class[0]), clazz);
      } catch (IOException var3) {
         throw new IORuntimeException(var3);
      }
   }

   public static <T> T readObj(ValidateObjectInputStream in, Class<T> clazz) throws IORuntimeException, UtilException {
      if (in == null) {
         throw new IllegalArgumentException("The InputStream must not be null");
      } else {
         if (null != clazz) {
            in.accept(clazz);
         }

         try {
            return in.readObject();
         } catch (IOException var3) {
            throw new IORuntimeException(var3);
         } catch (ClassNotFoundException var4) {
            throw new UtilException(var4);
         }
      }
   }

   public static <T extends Collection<String>> T readUtf8Lines(InputStream in, T collection) throws IORuntimeException {
      return readLines(in, CharsetUtil.CHARSET_UTF_8, collection);
   }

   /** @deprecated */
   @Deprecated
   public static <T extends Collection<String>> T readLines(InputStream in, String charsetName, T collection) throws IORuntimeException {
      return readLines(in, CharsetUtil.charset(charsetName), collection);
   }

   public static <T extends Collection<String>> T readLines(InputStream in, Charset charset, T collection) throws IORuntimeException {
      return readLines(getReader(in, charset), (Collection)collection);
   }

   public static <T extends Collection<String>> T readLines(Reader reader, T collection) throws IORuntimeException {
      readLines(reader, collection::add);
      return collection;
   }

   public static void readUtf8Lines(InputStream in, LineHandler lineHandler) throws IORuntimeException {
      readLines(in, CharsetUtil.CHARSET_UTF_8, lineHandler);
   }

   public static void readLines(InputStream in, Charset charset, LineHandler lineHandler) throws IORuntimeException {
      readLines(getReader(in, charset), (LineHandler)lineHandler);
   }

   public static void readLines(Reader reader, LineHandler lineHandler) throws IORuntimeException {
      Assert.notNull(reader);
      Assert.notNull(lineHandler);
      Iterator var2 = lineIter(reader).iterator();

      while(var2.hasNext()) {
         String line = (String)var2.next();
         lineHandler.handle(line);
      }

   }

   /** @deprecated */
   @Deprecated
   public static ByteArrayInputStream toStream(String content, String charsetName) {
      return toStream(content, CharsetUtil.charset(charsetName));
   }

   public static ByteArrayInputStream toStream(String content, Charset charset) {
      return content == null ? null : toStream(StrUtil.bytes(content, charset));
   }

   public static ByteArrayInputStream toUtf8Stream(String content) {
      return toStream(content, CharsetUtil.CHARSET_UTF_8);
   }

   public static FileInputStream toStream(File file) {
      try {
         return new FileInputStream(file);
      } catch (FileNotFoundException var2) {
         throw new IORuntimeException(var2);
      }
   }

   public static ByteArrayInputStream toStream(byte[] content) {
      return content == null ? null : new ByteArrayInputStream(content);
   }

   public static ByteArrayInputStream toStream(ByteArrayOutputStream out) {
      return out == null ? null : new ByteArrayInputStream(out.toByteArray());
   }

   public static BufferedInputStream toBuffered(InputStream in) {
      Assert.notNull(in, "InputStream must be not null!");
      return in instanceof BufferedInputStream ? (BufferedInputStream)in : new BufferedInputStream(in);
   }

   public static BufferedInputStream toBuffered(InputStream in, int bufferSize) {
      Assert.notNull(in, "InputStream must be not null!");
      return in instanceof BufferedInputStream ? (BufferedInputStream)in : new BufferedInputStream(in, bufferSize);
   }

   public static BufferedOutputStream toBuffered(OutputStream out) {
      Assert.notNull(out, "OutputStream must be not null!");
      return out instanceof BufferedOutputStream ? (BufferedOutputStream)out : new BufferedOutputStream(out);
   }

   public static BufferedOutputStream toBuffered(OutputStream out, int bufferSize) {
      Assert.notNull(out, "OutputStream must be not null!");
      return out instanceof BufferedOutputStream ? (BufferedOutputStream)out : new BufferedOutputStream(out, bufferSize);
   }

   public static BufferedReader toBuffered(Reader reader) {
      Assert.notNull(reader, "Reader must be not null!");
      return reader instanceof BufferedReader ? (BufferedReader)reader : new BufferedReader(reader);
   }

   public static BufferedReader toBuffered(Reader reader, int bufferSize) {
      Assert.notNull(reader, "Reader must be not null!");
      return reader instanceof BufferedReader ? (BufferedReader)reader : new BufferedReader(reader, bufferSize);
   }

   public static BufferedWriter toBuffered(Writer writer) {
      Assert.notNull(writer, "Writer must be not null!");
      return writer instanceof BufferedWriter ? (BufferedWriter)writer : new BufferedWriter(writer);
   }

   public static BufferedWriter toBuffered(Writer writer, int bufferSize) {
      Assert.notNull(writer, "Writer must be not null!");
      return writer instanceof BufferedWriter ? (BufferedWriter)writer : new BufferedWriter(writer, bufferSize);
   }

   public static InputStream toMarkSupportStream(InputStream in) {
      if (null == in) {
         return null;
      } else {
         return (InputStream)(!in.markSupported() ? new BufferedInputStream(in) : in);
      }
   }

   public static PushbackInputStream toPushbackStream(InputStream in, int pushBackSize) {
      return in instanceof PushbackInputStream ? (PushbackInputStream)in : new PushbackInputStream(in, pushBackSize);
   }

   public static InputStream toAvailableStream(InputStream in) {
      if (in instanceof FileInputStream) {
         return in;
      } else {
         PushbackInputStream pushbackInputStream = toPushbackStream(in, 1);

         try {
            int available = pushbackInputStream.available();
            if (available <= 0) {
               int b = pushbackInputStream.read();
               pushbackInputStream.unread(b);
            }

            return pushbackInputStream;
         } catch (IOException var4) {
            throw new IORuntimeException(var4);
         }
      }
   }

   public static void write(OutputStream out, boolean isCloseOut, byte[] content) throws IORuntimeException {
      try {
         out.write(content);
      } catch (IOException var7) {
         throw new IORuntimeException(var7);
      } finally {
         if (isCloseOut) {
            close(out);
         }

      }

   }

   public static void writeUtf8(OutputStream out, boolean isCloseOut, Object... contents) throws IORuntimeException {
      write(out, CharsetUtil.CHARSET_UTF_8, isCloseOut, contents);
   }

   /** @deprecated */
   @Deprecated
   public static void write(OutputStream out, String charsetName, boolean isCloseOut, Object... contents) throws IORuntimeException {
      write(out, CharsetUtil.charset(charsetName), isCloseOut, contents);
   }

   public static void write(OutputStream out, Charset charset, boolean isCloseOut, Object... contents) throws IORuntimeException {
      OutputStreamWriter osw = null;

      try {
         osw = getWriter(out, charset);
         Object[] var5 = contents;
         int var6 = contents.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            Object content = var5[var7];
            if (content != null) {
               osw.write(Convert.toStr(content, ""));
            }
         }

         osw.flush();
      } catch (IOException var12) {
         throw new IORuntimeException(var12);
      } finally {
         if (isCloseOut) {
            close(osw);
         }

      }

   }

   public static void writeObj(OutputStream out, boolean isCloseOut, Serializable obj) throws IORuntimeException {
      writeObjects(out, isCloseOut, obj);
   }

   public static void writeObjects(OutputStream out, boolean isCloseOut, Serializable... contents) throws IORuntimeException {
      ObjectOutputStream osw = null;

      try {
         osw = out instanceof ObjectOutputStream ? (ObjectOutputStream)out : new ObjectOutputStream(out);
         Serializable[] var4 = contents;
         int var5 = contents.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Object content = var4[var6];
            if (content != null) {
               osw.writeObject(content);
            }
         }

         osw.flush();
      } catch (IOException var11) {
         throw new IORuntimeException(var11);
      } finally {
         if (isCloseOut) {
            close(osw);
         }

      }
   }

   public static void flush(Flushable flushable) {
      if (null != flushable) {
         try {
            flushable.flush();
         } catch (Exception var2) {
         }
      }

   }

   public static void close(Closeable closeable) {
      if (null != closeable) {
         try {
            closeable.close();
         } catch (Exception var2) {
         }
      }

   }

   public static void closeIfPosible(Object obj) {
      if (obj instanceof AutoCloseable) {
         close((AutoCloseable)obj);
      }

   }

   public static boolean contentEquals(InputStream input1, InputStream input2) throws IORuntimeException {
      if (!(input1 instanceof BufferedInputStream)) {
         input1 = new BufferedInputStream((InputStream)input1);
      }

      if (!(input2 instanceof BufferedInputStream)) {
         input2 = new BufferedInputStream((InputStream)input2);
      }

      try {
         int ch2;
         for(int ch = ((InputStream)input1).read(); -1 != ch; ch = ((InputStream)input1).read()) {
            ch2 = ((InputStream)input2).read();
            if (ch != ch2) {
               return false;
            }
         }

         ch2 = ((InputStream)input2).read();
         return ch2 == -1;
      } catch (IOException var4) {
         throw new IORuntimeException(var4);
      }
   }

   public static boolean contentEquals(Reader input1, Reader input2) throws IORuntimeException {
      Reader input1 = getReader(input1);
      Reader input2 = getReader(input2);

      try {
         int ch2;
         for(int ch = input1.read(); -1 != ch; ch = input1.read()) {
            ch2 = input2.read();
            if (ch != ch2) {
               return false;
            }
         }

         ch2 = input2.read();
         return ch2 == -1;
      } catch (IOException var4) {
         throw new IORuntimeException(var4);
      }
   }

   public static boolean contentEqualsIgnoreEOL(Reader input1, Reader input2) throws IORuntimeException {
      BufferedReader br1 = getReader(input1);
      BufferedReader br2 = getReader(input2);

      try {
         String line1 = br1.readLine();

         String line2;
         for(line2 = br2.readLine(); line1 != null && line1.equals(line2); line2 = br2.readLine()) {
            line1 = br1.readLine();
         }

         return Objects.equals(line1, line2);
      } catch (IOException var6) {
         throw new IORuntimeException(var6);
      }
   }

   public static long checksumCRC32(InputStream in) throws IORuntimeException {
      return checksum(in, new CRC32()).getValue();
   }

   public static Checksum checksum(InputStream in, Checksum checksum) throws IORuntimeException {
      Assert.notNull(in, "InputStream is null !");
      if (null == checksum) {
         checksum = new CRC32();
      }

      try {
         in = new CheckedInputStream((InputStream)in, (Checksum)checksum);
         copy((InputStream)in, (OutputStream)(new NullOutputStream()));
      } finally {
         close((Closeable)in);
      }

      return (Checksum)checksum;
   }

   public static long checksumValue(InputStream in, Checksum checksum) {
      return checksum(in, checksum).getValue();
   }

   public static LineIter lineIter(Reader reader) {
      return new LineIter(reader);
   }

   public static LineIter lineIter(InputStream in, Charset charset) {
      return new LineIter(in, charset);
   }

   public static String toStr(ByteArrayOutputStream out, Charset charset) {
      try {
         return out.toString(charset.name());
      } catch (UnsupportedEncodingException var3) {
         throw new IORuntimeException(var3);
      }
   }
}
