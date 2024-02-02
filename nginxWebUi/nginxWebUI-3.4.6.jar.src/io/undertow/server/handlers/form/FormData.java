/*     */ package io.undertow.server.handlers.form;
/*     */ 
/*     */ import io.undertow.UndertowMessages;
/*     */ import io.undertow.util.HeaderMap;
/*     */ import java.io.BufferedInputStream;
/*     */ import java.io.ByteArrayInputStream;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.nio.file.CopyOption;
/*     */ import java.nio.file.Files;
/*     */ import java.nio.file.NoSuchFileException;
/*     */ import java.nio.file.Path;
/*     */ import java.nio.file.StandardCopyOption;
/*     */ import java.util.ArrayDeque;
/*     */ import java.util.Deque;
/*     */ import java.util.Iterator;
/*     */ import java.util.LinkedHashMap;
/*     */ import java.util.Map;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public final class FormData
/*     */   implements Iterable<String>
/*     */ {
/*  46 */   private final Map<String, Deque<FormValue>> values = new LinkedHashMap<>();
/*     */   
/*     */   private final int maxValues;
/*  49 */   private int valueCount = 0;
/*     */   
/*     */   public FormData(int maxValues) {
/*  52 */     this.maxValues = maxValues;
/*     */   }
/*     */ 
/*     */   
/*     */   public Iterator<String> iterator() {
/*  57 */     return this.values.keySet().iterator();
/*     */   }
/*     */   
/*     */   public FormValue getFirst(String name) {
/*  61 */     Deque<FormValue> deque = this.values.get(name);
/*  62 */     return (deque == null) ? null : deque.peekFirst();
/*     */   }
/*     */   
/*     */   public FormValue getLast(String name) {
/*  66 */     Deque<FormValue> deque = this.values.get(name);
/*  67 */     return (deque == null) ? null : deque.peekLast();
/*     */   }
/*     */   
/*     */   public Deque<FormValue> get(String name) {
/*  71 */     return this.values.get(name);
/*     */   }
/*     */   
/*     */   public void add(String name, byte[] value, String fileName, HeaderMap headers) {
/*  75 */     Deque<FormValue> values = this.values.get(name);
/*  76 */     if (values == null) {
/*  77 */       this.values.put(name, values = new ArrayDeque<>(1));
/*     */     }
/*  79 */     values.add(new FormValueImpl(value, fileName, headers));
/*  80 */     if (++this.valueCount > this.maxValues) {
/*  81 */       throw new RuntimeException(UndertowMessages.MESSAGES.tooManyParameters(this.maxValues));
/*     */     }
/*     */   }
/*     */   
/*     */   public void add(String name, String value) {
/*  86 */     add(name, value, (String)null, (HeaderMap)null);
/*     */   }
/*     */   
/*     */   public void add(String name, String value, HeaderMap headers) {
/*  90 */     add(name, value, (String)null, headers);
/*     */   }
/*     */   
/*     */   public void add(String name, String value, String charset, HeaderMap headers) {
/*  94 */     Deque<FormValue> values = this.values.get(name);
/*  95 */     if (values == null) {
/*  96 */       this.values.put(name, values = new ArrayDeque<>(1));
/*     */     }
/*  98 */     values.add(new FormValueImpl(value, charset, headers));
/*  99 */     if (++this.valueCount > this.maxValues) {
/* 100 */       throw new RuntimeException(UndertowMessages.MESSAGES.tooManyParameters(this.maxValues));
/*     */     }
/*     */   }
/*     */   
/*     */   public void add(String name, Path value, String fileName, HeaderMap headers) {
/* 105 */     Deque<FormValue> values = this.values.get(name);
/* 106 */     if (values == null) {
/* 107 */       this.values.put(name, values = new ArrayDeque<>(1));
/*     */     }
/* 109 */     values.add(new FormValueImpl(value, fileName, headers));
/* 110 */     if (values.size() > this.maxValues) {
/* 111 */       throw new RuntimeException(UndertowMessages.MESSAGES.tooManyParameters(this.maxValues));
/*     */     }
/* 113 */     if (++this.valueCount > this.maxValues) {
/* 114 */       throw new RuntimeException(UndertowMessages.MESSAGES.tooManyParameters(this.maxValues));
/*     */     }
/*     */   }
/*     */   
/*     */   public void put(String name, String value, HeaderMap headers) {
/* 119 */     Deque<FormValue> values = new ArrayDeque<>(1);
/* 120 */     Deque<FormValue> old = this.values.put(name, values);
/* 121 */     if (old != null) {
/* 122 */       this.valueCount -= old.size();
/*     */     }
/* 124 */     values.add(new FormValueImpl(value, headers));
/*     */     
/* 126 */     if (++this.valueCount > this.maxValues) {
/* 127 */       throw new RuntimeException(UndertowMessages.MESSAGES.tooManyParameters(this.maxValues));
/*     */     }
/*     */   }
/*     */   
/*     */   public Deque<FormValue> remove(String name) {
/* 132 */     Deque<FormValue> old = this.values.remove(name);
/* 133 */     if (old != null) {
/* 134 */       this.valueCount -= old.size();
/*     */     }
/* 136 */     return old;
/*     */   }
/*     */   
/*     */   public boolean contains(String name) {
/* 140 */     Deque<FormValue> value = this.values.get(name);
/* 141 */     return (value != null && !value.isEmpty());
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean equals(Object o) {
/* 146 */     if (this == o) return true; 
/* 147 */     if (o == null || getClass() != o.getClass()) return false;
/*     */     
/* 149 */     FormData strings = (FormData)o;
/*     */     
/* 151 */     if ((this.values != null) ? !this.values.equals(strings.values) : (strings.values != null)) return false;
/*     */     
/* 153 */     return true;
/*     */   }
/*     */ 
/*     */   
/*     */   public int hashCode() {
/* 158 */     return (this.values != null) ? this.values.hashCode() : 0;
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 163 */     return "FormData{values=" + this.values + '}';
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static interface FormValue
/*     */   {
/*     */     String getValue();
/*     */ 
/*     */ 
/*     */     
/*     */     String getCharset();
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     boolean isFile();
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     Path getPath();
/*     */ 
/*     */ 
/*     */     
/*     */     @Deprecated
/*     */     File getFile();
/*     */ 
/*     */ 
/*     */     
/*     */     FormData.FileItem getFileItem();
/*     */ 
/*     */ 
/*     */     
/*     */     boolean isFileItem();
/*     */ 
/*     */ 
/*     */     
/*     */     String getFileName();
/*     */ 
/*     */ 
/*     */     
/*     */     HeaderMap getHeaders();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public static class FileItem
/*     */   {
/*     */     private final Path file;
/*     */ 
/*     */     
/*     */     private final byte[] content;
/*     */ 
/*     */ 
/*     */     
/*     */     public FileItem(Path file) {
/* 221 */       this.file = file;
/* 222 */       this.content = null;
/*     */     }
/*     */     
/*     */     public FileItem(byte[] content) {
/* 226 */       this.file = null;
/* 227 */       this.content = content;
/*     */     }
/*     */     
/*     */     public boolean isInMemory() {
/* 231 */       return (this.file == null);
/*     */     }
/*     */     
/*     */     public Path getFile() {
/* 235 */       return this.file;
/*     */     }
/*     */     
/*     */     public long getFileSize() throws IOException {
/* 239 */       if (isInMemory()) {
/* 240 */         return this.content.length;
/*     */       }
/* 242 */       return Files.size(this.file);
/*     */     }
/*     */ 
/*     */     
/*     */     public InputStream getInputStream() throws IOException {
/* 247 */       if (this.file != null) {
/* 248 */         return new BufferedInputStream(Files.newInputStream(this.file, new java.nio.file.OpenOption[0]));
/*     */       }
/* 250 */       return new ByteArrayInputStream(this.content);
/*     */     }
/*     */ 
/*     */     
/*     */     public void delete() throws IOException {
/* 255 */       if (this.file != null) {
/*     */         try {
/* 257 */           Files.delete(this.file);
/* 258 */         } catch (NoSuchFileException noSuchFileException) {}
/*     */       }
/*     */     }
/*     */ 
/*     */     
/*     */     public void write(Path target) throws IOException {
/* 264 */       if (this.file != null) {
/*     */         try {
/* 266 */           Files.move(this.file, target, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
/*     */           return;
/* 268 */         } catch (IOException iOException) {}
/*     */       }
/*     */ 
/*     */ 
/*     */       
/* 273 */       try (InputStream is = getInputStream()) {
/* 274 */         Files.copy(is, target, new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   static class FormValueImpl
/*     */     implements FormValue
/*     */   {
/*     */     private final String value;
/*     */     private final String fileName;
/*     */     private final HeaderMap headers;
/*     */     private final FormData.FileItem fileItem;
/*     */     private final String charset;
/*     */     
/*     */     FormValueImpl(String value, HeaderMap headers) {
/* 289 */       this.value = value;
/* 290 */       this.headers = headers;
/* 291 */       this.fileName = null;
/* 292 */       this.fileItem = null;
/* 293 */       this.charset = null;
/*     */     }
/*     */     
/*     */     FormValueImpl(String value, String charset, HeaderMap headers) {
/* 297 */       this.value = value;
/* 298 */       this.charset = charset;
/* 299 */       this.headers = headers;
/* 300 */       this.fileName = null;
/* 301 */       this.fileItem = null;
/*     */     }
/*     */     
/*     */     FormValueImpl(Path file, String fileName, HeaderMap headers) {
/* 305 */       this.fileItem = new FormData.FileItem(file);
/* 306 */       this.headers = headers;
/* 307 */       this.fileName = fileName;
/* 308 */       this.value = null;
/* 309 */       this.charset = null;
/*     */     }
/*     */     
/*     */     FormValueImpl(byte[] data, String fileName, HeaderMap headers) {
/* 313 */       this.fileItem = new FormData.FileItem(data);
/* 314 */       this.fileName = fileName;
/* 315 */       this.headers = headers;
/* 316 */       this.value = null;
/* 317 */       this.charset = null;
/*     */     }
/*     */ 
/*     */ 
/*     */     
/*     */     public String getValue() {
/* 323 */       if (this.value == null) {
/* 324 */         throw UndertowMessages.MESSAGES.formValueIsAFile();
/*     */       }
/* 326 */       return this.value;
/*     */     }
/*     */ 
/*     */     
/*     */     public String getCharset() {
/* 331 */       return this.charset;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isFile() {
/* 336 */       return (this.fileItem != null && !this.fileItem.isInMemory());
/*     */     }
/*     */ 
/*     */     
/*     */     public Path getPath() {
/* 341 */       if (this.fileItem == null) {
/* 342 */         throw UndertowMessages.MESSAGES.formValueIsAString();
/*     */       }
/* 344 */       if (this.fileItem.isInMemory()) {
/* 345 */         throw UndertowMessages.MESSAGES.formValueIsInMemoryFile();
/*     */       }
/* 347 */       return this.fileItem.getFile();
/*     */     }
/*     */ 
/*     */     
/*     */     public File getFile() {
/* 352 */       return getPath().toFile();
/*     */     }
/*     */ 
/*     */     
/*     */     public FormData.FileItem getFileItem() {
/* 357 */       if (this.fileItem == null) {
/* 358 */         throw UndertowMessages.MESSAGES.formValueIsAString();
/*     */       }
/* 360 */       return this.fileItem;
/*     */     }
/*     */ 
/*     */     
/*     */     public boolean isFileItem() {
/* 365 */       return (this.fileItem != null);
/*     */     }
/*     */ 
/*     */     
/*     */     public HeaderMap getHeaders() {
/* 370 */       return this.headers;
/*     */     }
/*     */     
/*     */     public String getFileName() {
/* 374 */       return this.fileName;
/*     */     }
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\handlers\form\FormData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */