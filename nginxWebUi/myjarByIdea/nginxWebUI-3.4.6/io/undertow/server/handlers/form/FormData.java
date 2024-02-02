package io.undertow.server.handlers.form;

import io.undertow.UndertowMessages;
import io.undertow.util.HeaderMap;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public final class FormData implements Iterable<String> {
   private final Map<String, Deque<FormValue>> values = new LinkedHashMap();
   private final int maxValues;
   private int valueCount = 0;

   public FormData(int maxValues) {
      this.maxValues = maxValues;
   }

   public Iterator<String> iterator() {
      return this.values.keySet().iterator();
   }

   public FormValue getFirst(String name) {
      Deque<FormValue> deque = (Deque)this.values.get(name);
      return deque == null ? null : (FormValue)deque.peekFirst();
   }

   public FormValue getLast(String name) {
      Deque<FormValue> deque = (Deque)this.values.get(name);
      return deque == null ? null : (FormValue)deque.peekLast();
   }

   public Deque<FormValue> get(String name) {
      return (Deque)this.values.get(name);
   }

   public void add(String name, byte[] value, String fileName, HeaderMap headers) {
      Deque<FormValue> values = (Deque)this.values.get(name);
      if (values == null) {
         this.values.put(name, values = new ArrayDeque(1));
      }

      ((Deque)values).add(new FormValueImpl(value, fileName, headers));
      if (++this.valueCount > this.maxValues) {
         throw new RuntimeException(UndertowMessages.MESSAGES.tooManyParameters(this.maxValues));
      }
   }

   public void add(String name, String value) {
      this.add(name, (String)value, (String)null, (HeaderMap)null);
   }

   public void add(String name, String value, HeaderMap headers) {
      this.add(name, (String)value, (String)null, headers);
   }

   public void add(String name, String value, String charset, HeaderMap headers) {
      Deque<FormValue> values = (Deque)this.values.get(name);
      if (values == null) {
         this.values.put(name, values = new ArrayDeque(1));
      }

      ((Deque)values).add(new FormValueImpl(value, charset, headers));
      if (++this.valueCount > this.maxValues) {
         throw new RuntimeException(UndertowMessages.MESSAGES.tooManyParameters(this.maxValues));
      }
   }

   public void add(String name, Path value, String fileName, HeaderMap headers) {
      Deque<FormValue> values = (Deque)this.values.get(name);
      if (values == null) {
         this.values.put(name, values = new ArrayDeque(1));
      }

      ((Deque)values).add(new FormValueImpl(value, fileName, headers));
      if (((Deque)values).size() > this.maxValues) {
         throw new RuntimeException(UndertowMessages.MESSAGES.tooManyParameters(this.maxValues));
      } else if (++this.valueCount > this.maxValues) {
         throw new RuntimeException(UndertowMessages.MESSAGES.tooManyParameters(this.maxValues));
      }
   }

   public void put(String name, String value, HeaderMap headers) {
      Deque<FormValue> values = new ArrayDeque(1);
      Deque<FormValue> old = (Deque)this.values.put(name, values);
      if (old != null) {
         this.valueCount -= old.size();
      }

      values.add(new FormValueImpl(value, headers));
      if (++this.valueCount > this.maxValues) {
         throw new RuntimeException(UndertowMessages.MESSAGES.tooManyParameters(this.maxValues));
      }
   }

   public Deque<FormValue> remove(String name) {
      Deque<FormValue> old = (Deque)this.values.remove(name);
      if (old != null) {
         this.valueCount -= old.size();
      }

      return old;
   }

   public boolean contains(String name) {
      Deque<FormValue> value = (Deque)this.values.get(name);
      return value != null && !value.isEmpty();
   }

   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         FormData strings = (FormData)o;
         if (this.values != null) {
            if (!this.values.equals(strings.values)) {
               return false;
            }
         } else if (strings.values != null) {
            return false;
         }

         return true;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.values != null ? this.values.hashCode() : 0;
   }

   public String toString() {
      return "FormData{values=" + this.values + '}';
   }

   static class FormValueImpl implements FormValue {
      private final String value;
      private final String fileName;
      private final HeaderMap headers;
      private final FileItem fileItem;
      private final String charset;

      FormValueImpl(String value, HeaderMap headers) {
         this.value = value;
         this.headers = headers;
         this.fileName = null;
         this.fileItem = null;
         this.charset = null;
      }

      FormValueImpl(String value, String charset, HeaderMap headers) {
         this.value = value;
         this.charset = charset;
         this.headers = headers;
         this.fileName = null;
         this.fileItem = null;
      }

      FormValueImpl(Path file, String fileName, HeaderMap headers) {
         this.fileItem = new FileItem(file);
         this.headers = headers;
         this.fileName = fileName;
         this.value = null;
         this.charset = null;
      }

      FormValueImpl(byte[] data, String fileName, HeaderMap headers) {
         this.fileItem = new FileItem(data);
         this.fileName = fileName;
         this.headers = headers;
         this.value = null;
         this.charset = null;
      }

      public String getValue() {
         if (this.value == null) {
            throw UndertowMessages.MESSAGES.formValueIsAFile();
         } else {
            return this.value;
         }
      }

      public String getCharset() {
         return this.charset;
      }

      public boolean isFile() {
         return this.fileItem != null && !this.fileItem.isInMemory();
      }

      public Path getPath() {
         if (this.fileItem == null) {
            throw UndertowMessages.MESSAGES.formValueIsAString();
         } else if (this.fileItem.isInMemory()) {
            throw UndertowMessages.MESSAGES.formValueIsInMemoryFile();
         } else {
            return this.fileItem.getFile();
         }
      }

      public File getFile() {
         return this.getPath().toFile();
      }

      public FileItem getFileItem() {
         if (this.fileItem == null) {
            throw UndertowMessages.MESSAGES.formValueIsAString();
         } else {
            return this.fileItem;
         }
      }

      public boolean isFileItem() {
         return this.fileItem != null;
      }

      public HeaderMap getHeaders() {
         return this.headers;
      }

      public String getFileName() {
         return this.fileName;
      }
   }

   public static class FileItem {
      private final Path file;
      private final byte[] content;

      public FileItem(Path file) {
         this.file = file;
         this.content = null;
      }

      public FileItem(byte[] content) {
         this.file = null;
         this.content = content;
      }

      public boolean isInMemory() {
         return this.file == null;
      }

      public Path getFile() {
         return this.file;
      }

      public long getFileSize() throws IOException {
         return this.isInMemory() ? (long)this.content.length : Files.size(this.file);
      }

      public InputStream getInputStream() throws IOException {
         return (InputStream)(this.file != null ? new BufferedInputStream(Files.newInputStream(this.file)) : new ByteArrayInputStream(this.content));
      }

      public void delete() throws IOException {
         if (this.file != null) {
            try {
               Files.delete(this.file);
            } catch (NoSuchFileException var2) {
            }
         }

      }

      public void write(Path target) throws IOException {
         if (this.file != null) {
            try {
               Files.move(this.file, target, StandardCopyOption.REPLACE_EXISTING);
               return;
            } catch (IOException var15) {
            }
         }

         InputStream is = this.getInputStream();
         Throwable var3 = null;

         try {
            Files.copy(is, target, new CopyOption[]{StandardCopyOption.REPLACE_EXISTING});
         } catch (Throwable var13) {
            var3 = var13;
            throw var13;
         } finally {
            if (is != null) {
               if (var3 != null) {
                  try {
                     is.close();
                  } catch (Throwable var12) {
                     var3.addSuppressed(var12);
                  }
               } else {
                  is.close();
               }
            }

         }

      }
   }

   public interface FormValue {
      String getValue();

      String getCharset();

      /** @deprecated */
      @Deprecated
      boolean isFile();

      /** @deprecated */
      @Deprecated
      Path getPath();

      /** @deprecated */
      @Deprecated
      File getFile();

      FileItem getFileItem();

      boolean isFileItem();

      String getFileName();

      HeaderMap getHeaders();
   }
}
