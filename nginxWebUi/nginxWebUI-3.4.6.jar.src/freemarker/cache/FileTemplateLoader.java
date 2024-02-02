/*     */ package freemarker.cache;
/*     */ 
/*     */ import freemarker.log.Logger;
/*     */ import freemarker.template.utility.SecurityUtilities;
/*     */ import freemarker.template.utility.StringUtil;
/*     */ import java.io.File;
/*     */ import java.io.FileInputStream;
/*     */ import java.io.FileNotFoundException;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.Reader;
/*     */ import java.security.AccessController;
/*     */ import java.security.PrivilegedAction;
/*     */ import java.security.PrivilegedActionException;
/*     */ import java.security.PrivilegedExceptionAction;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FileTemplateLoader
/*     */   implements TemplateLoader
/*     */ {
/*     */   static {
/*     */     boolean emuCaseSensFS;
/*     */   }
/*     */   
/*  51 */   public static String SYSTEM_PROPERTY_NAME_EMULATE_CASE_SENSITIVE_FILE_SYSTEM = "org.freemarker.emulateCaseSensitiveFileSystem";
/*     */   private static final boolean EMULATE_CASE_SENSITIVE_FILE_SYSTEM_DEFAULT;
/*     */   
/*     */   static {
/*  55 */     String s = SecurityUtilities.getSystemProperty(SYSTEM_PROPERTY_NAME_EMULATE_CASE_SENSITIVE_FILE_SYSTEM, "false");
/*     */ 
/*     */     
/*     */     try {
/*  59 */       emuCaseSensFS = StringUtil.getYesNo(s);
/*  60 */     } catch (Exception e) {
/*  61 */       emuCaseSensFS = false;
/*     */     } 
/*  63 */     EMULATE_CASE_SENSITIVE_FILE_SYSTEM_DEFAULT = emuCaseSensFS;
/*     */   }
/*     */   
/*     */   private static final int CASE_CHECH_CACHE_HARD_SIZE = 50;
/*     */   private static final int CASE_CHECK_CACHE__SOFT_SIZE = 1000;
/*  68 */   private static final boolean SEP_IS_SLASH = (File.separatorChar == '/');
/*     */   
/*  70 */   private static final Logger LOG = Logger.getLogger("freemarker.cache");
/*     */ 
/*     */   
/*     */   public final File baseDir;
/*     */ 
/*     */   
/*     */   private final String canonicalBasePath;
/*     */ 
/*     */   
/*     */   private boolean emulateCaseSensitiveFileSystem;
/*     */ 
/*     */   
/*     */   private MruCacheStorage correctCasePaths;
/*     */ 
/*     */   
/*     */   @Deprecated
/*     */   public FileTemplateLoader() throws IOException {
/*  87 */     this(new File(SecurityUtilities.getSystemProperty("user.dir")));
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public FileTemplateLoader(File baseDir) throws IOException {
/*  98 */     this(baseDir, false);
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
/*     */   public FileTemplateLoader(final File baseDir, final boolean disableCanonicalPathCheck) throws IOException {
/*     */     try {
/* 119 */       Object[] retval = AccessController.<Object[]>doPrivileged(new PrivilegedExceptionAction<Object[]>()
/*     */           {
/*     */             public Object[] run() throws IOException {
/* 122 */               if (!baseDir.exists()) {
/* 123 */                 throw new FileNotFoundException(baseDir + " does not exist.");
/*     */               }
/* 125 */               if (!baseDir.isDirectory()) {
/* 126 */                 throw new IOException(baseDir + " is not a directory.");
/*     */               }
/* 128 */               Object[] retval = new Object[2];
/* 129 */               if (disableCanonicalPathCheck) {
/* 130 */                 retval[0] = baseDir;
/* 131 */                 retval[1] = null;
/*     */               } else {
/* 133 */                 retval[0] = baseDir.getCanonicalFile();
/* 134 */                 String basePath = ((File)retval[0]).getPath();
/*     */ 
/*     */                 
/* 137 */                 if (!basePath.endsWith(File.separator)) {
/* 138 */                   basePath = basePath + File.separatorChar;
/*     */                 }
/* 140 */                 retval[1] = basePath;
/*     */               } 
/* 142 */               return retval;
/*     */             }
/*     */           });
/* 145 */       this.baseDir = (File)retval[0];
/* 146 */       this.canonicalBasePath = (String)retval[1];
/*     */       
/* 148 */       setEmulateCaseSensitiveFileSystem(getEmulateCaseSensitiveFileSystemDefault());
/* 149 */     } catch (PrivilegedActionException e) {
/* 150 */       throw (IOException)e.getException();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public Object findTemplateSource(final String name) throws IOException {
/*     */     try {
/* 157 */       return AccessController.doPrivileged(new PrivilegedExceptionAction<File>()
/*     */           {
/*     */             public File run() throws IOException
/*     */             {
/* 161 */               File source = new File(FileTemplateLoader.this.baseDir, FileTemplateLoader.SEP_IS_SLASH ? name : name.replace('/', File.separatorChar));
/* 162 */               if (!source.isFile()) {
/* 163 */                 return null;
/*     */               }
/*     */ 
/*     */ 
/*     */               
/* 168 */               if (FileTemplateLoader.this.canonicalBasePath != null) {
/* 169 */                 String normalized = source.getCanonicalPath();
/* 170 */                 if (!normalized.startsWith(FileTemplateLoader.this.canonicalBasePath)) {
/* 171 */                   throw new SecurityException(source.getAbsolutePath() + " resolves to " + normalized + " which  doesn't start with " + FileTemplateLoader.this
/*     */                       
/* 173 */                       .canonicalBasePath);
/*     */                 }
/*     */               } 
/*     */               
/* 177 */               if (FileTemplateLoader.this.emulateCaseSensitiveFileSystem && !FileTemplateLoader.this.isNameCaseCorrect(source)) {
/* 178 */                 return null;
/*     */               }
/*     */               
/* 181 */               return source;
/*     */             }
/*     */           });
/* 184 */     } catch (PrivilegedActionException e) {
/* 185 */       throw (IOException)e.getException();
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public long getLastModified(final Object templateSource) {
/* 191 */     return ((Long)AccessController.<Long>doPrivileged(new PrivilegedAction<Long>()
/*     */         {
/*     */           public Long run() {
/* 194 */             return Long.valueOf(((File)templateSource).lastModified());
/*     */           }
/* 196 */         })).longValue();
/*     */   }
/*     */ 
/*     */   
/*     */   public Reader getReader(final Object templateSource, final String encoding) throws IOException {
/*     */     try {
/* 202 */       return AccessController.<Reader>doPrivileged(new PrivilegedExceptionAction<Reader>()
/*     */           {
/*     */             public Reader run() throws IOException {
/* 205 */               if (!(templateSource instanceof File)) {
/* 206 */                 throw new IllegalArgumentException("templateSource wasn't a File, but a: " + templateSource
/*     */                     
/* 208 */                     .getClass().getName());
/*     */               }
/* 210 */               return new InputStreamReader(new FileInputStream((File)templateSource), encoding);
/*     */             }
/*     */           });
/* 213 */     } catch (PrivilegedActionException e) {
/* 214 */       throw (IOException)e.getException();
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   private boolean isNameCaseCorrect(File source) throws IOException {
/* 222 */     String sourcePath = source.getPath();
/* 223 */     synchronized (this.correctCasePaths) {
/* 224 */       if (this.correctCasePaths.get(sourcePath) != null) {
/* 225 */         return true;
/*     */       }
/*     */     } 
/*     */     
/* 229 */     File parentDir = source.getParentFile();
/* 230 */     if (parentDir != null) {
/* 231 */       if (!this.baseDir.equals(parentDir) && !isNameCaseCorrect(parentDir)) {
/* 232 */         return false;
/*     */       }
/*     */       
/* 235 */       String[] listing = parentDir.list();
/* 236 */       if (listing != null) {
/* 237 */         String fileName = source.getName();
/*     */         
/* 239 */         boolean identicalNameFound = false; int i;
/* 240 */         for (i = 0; !identicalNameFound && i < listing.length; i++) {
/* 241 */           if (fileName.equals(listing[i])) {
/* 242 */             identicalNameFound = true;
/*     */           }
/*     */         } 
/*     */         
/* 246 */         if (!identicalNameFound)
/*     */         {
/* 248 */           for (i = 0; i < listing.length; i++) {
/* 249 */             String listingEntry = listing[i];
/* 250 */             if (fileName.equalsIgnoreCase(listingEntry)) {
/* 251 */               if (LOG.isDebugEnabled()) {
/* 252 */                 LOG.debug("Emulating file-not-found because of letter case differences to the real file, for: " + sourcePath);
/*     */               }
/*     */               
/* 255 */               return false;
/*     */             } 
/*     */           } 
/*     */         }
/*     */       } 
/*     */     } 
/*     */     
/* 262 */     synchronized (this.correctCasePaths) {
/* 263 */       this.correctCasePaths.put(sourcePath, Boolean.TRUE);
/*     */     } 
/* 265 */     return true;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void closeTemplateSource(Object templateSource) {}
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public File getBaseDirectory() {
/* 280 */     return this.baseDir;
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
/*     */   public void setEmulateCaseSensitiveFileSystem(boolean nameCaseChecked) {
/* 298 */     if (nameCaseChecked) {
/* 299 */       if (this.correctCasePaths == null) {
/* 300 */         this.correctCasePaths = new MruCacheStorage(50, 1000);
/*     */       }
/*     */     } else {
/* 303 */       this.correctCasePaths = null;
/*     */     } 
/*     */     
/* 306 */     this.emulateCaseSensitiveFileSystem = nameCaseChecked;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getEmulateCaseSensitiveFileSystem() {
/* 315 */     return this.emulateCaseSensitiveFileSystem;
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
/*     */   protected boolean getEmulateCaseSensitiveFileSystemDefault() {
/* 327 */     return EMULATE_CASE_SENSITIVE_FILE_SYSTEM_DEFAULT;
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
/*     */   public String toString() {
/* 339 */     return TemplateLoaderUtils.getClassNameForToString(this) + "(baseDir=\"" + this.baseDir + "\"" + ((this.canonicalBasePath != null) ? (", canonicalBasePath=\"" + this.canonicalBasePath + "\"") : "") + (this.emulateCaseSensitiveFileSystem ? ", emulateCaseSensitiveFileSystem=true" : "") + ")";
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\freemarker\cache\FileTemplateLoader.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */