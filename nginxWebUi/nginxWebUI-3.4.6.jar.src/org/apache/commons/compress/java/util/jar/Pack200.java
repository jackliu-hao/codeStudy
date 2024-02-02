/*     */ package org.apache.commons.compress.java.util.jar;
/*     */ 
/*     */ import java.beans.PropertyChangeListener;
/*     */ import java.io.File;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.OutputStream;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.util.SortedMap;
/*     */ import java.util.jar.JarFile;
/*     */ import java.util.jar.JarInputStream;
/*     */ import java.util.jar.JarOutputStream;
/*     */ import org.apache.commons.compress.harmony.archive.internal.nls.Messages;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public abstract class Pack200
/*     */ {
/*     */   private static final String SYSTEM_PROPERTY_PACKER = "java.util.jar.Pack200.Packer";
/*     */   private static final String SYSTEM_PROPERTY_UNPACKER = "java.util.jar.Pack200.Unpacker";
/*     */   
/*     */   public static Packer newPacker() {
/*  60 */     return 
/*  61 */       AccessController.<Packer>doPrivileged(new PrivilegedAction()
/*     */         {
/*     */           public Object run() {
/*  64 */             String className = System.getProperty("java.util.jar.Pack200.Packer", "org.apache.commons.compress.harmony.pack200.Pack200PackerAdapter");
/*     */ 
/*     */ 
/*     */             
/*     */             try {
/*  69 */               return ClassLoader.getSystemClassLoader()
/*  70 */                 .loadClass(className).newInstance();
/*  71 */             } catch (Exception e) {
/*  72 */               throw new Error(Messages.getString("archive.3E", className), e);
/*     */             } 
/*     */           }
/*     */         });
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
/*     */   public static Unpacker newUnpacker() {
/*  90 */     return 
/*  91 */       AccessController.<Unpacker>doPrivileged(new PrivilegedAction()
/*     */         {
/*     */           public Object run() {
/*  94 */             String className = System.getProperty("java.util.jar.Pack200.Unpacker", "org.apache.commons.compress.harmony.unpack200.Pack200UnpackerAdapter");
/*     */             
/*     */             try {
/*  97 */               return ClassLoader.getSystemClassLoader()
/*  98 */                 .loadClass(className).newInstance();
/*  99 */             } catch (Exception e) {
/* 100 */               throw new Error(Messages.getString("archive.3E", className), e);
/*     */             } 
/*     */           }
/*     */         });
/*     */   }
/*     */   
/*     */   public static interface Packer {
/*     */     public static final String CLASS_ATTRIBUTE_PFX = "pack.class.attribute.";
/*     */     public static final String CODE_ATTRIBUTE_PFX = "pack.code.attribute.";
/*     */     public static final String DEFLATE_HINT = "pack.deflate.hint";
/*     */     public static final String EFFORT = "pack.effort";
/*     */     public static final String ERROR = "error";
/*     */     public static final String FALSE = "false";
/*     */     public static final String FIELD_ATTRIBUTE_PFX = "pack.field.attribute.";
/*     */     public static final String KEEP = "keep";
/*     */     public static final String KEEP_FILE_ORDER = "pack.keep.file.order";
/*     */     public static final String LATEST = "latest";
/*     */     public static final String METHOD_ATTRIBUTE_PFX = "pack.method.attribute.";
/*     */     public static final String MODIFICATION_TIME = "pack.modification.time";
/*     */     public static final String PASS = "pass";
/*     */     public static final String PASS_FILE_PFX = "pack.pass.file.";
/*     */     public static final String PROGRESS = "pack.progress";
/*     */     public static final String SEGMENT_LIMIT = "pack.segment.limit";
/*     */     public static final String STRIP = "strip";
/*     */     public static final String TRUE = "true";
/*     */     public static final String UNKNOWN_ATTRIBUTE = "pack.unknown.attribute";
/*     */     
/*     */     SortedMap<String, String> properties();
/*     */     
/*     */     void pack(JarFile param1JarFile, OutputStream param1OutputStream) throws IOException;
/*     */     
/*     */     void pack(JarInputStream param1JarInputStream, OutputStream param1OutputStream) throws IOException;
/*     */     
/*     */     void addPropertyChangeListener(PropertyChangeListener param1PropertyChangeListener);
/*     */     
/*     */     void removePropertyChangeListener(PropertyChangeListener param1PropertyChangeListener);
/*     */   }
/*     */   
/*     */   public static interface Unpacker {
/*     */     public static final String DEFLATE_HINT = "unpack.deflate.hint";
/*     */     public static final String FALSE = "false";
/*     */     public static final String KEEP = "keep";
/*     */     public static final String PROGRESS = "unpack.progress";
/*     */     public static final String TRUE = "true";
/*     */     
/*     */     SortedMap<String, String> properties();
/*     */     
/*     */     void unpack(InputStream param1InputStream, JarOutputStream param1JarOutputStream) throws IOException;
/*     */     
/*     */     void unpack(File param1File, JarOutputStream param1JarOutputStream) throws IOException;
/*     */     
/*     */     void addPropertyChangeListener(PropertyChangeListener param1PropertyChangeListener);
/*     */     
/*     */     void removePropertyChangeListener(PropertyChangeListener param1PropertyChangeListener);
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\apache\commons\compress\jav\\util\jar\Pack200.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */