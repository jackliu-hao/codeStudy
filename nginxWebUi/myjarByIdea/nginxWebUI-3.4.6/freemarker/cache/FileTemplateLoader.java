package freemarker.cache;

import freemarker.log.Logger;
import freemarker.template.utility.SecurityUtilities;
import freemarker.template.utility.StringUtil;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;

public class FileTemplateLoader implements TemplateLoader {
   public static String SYSTEM_PROPERTY_NAME_EMULATE_CASE_SENSITIVE_FILE_SYSTEM = "org.freemarker.emulateCaseSensitiveFileSystem";
   private static final boolean EMULATE_CASE_SENSITIVE_FILE_SYSTEM_DEFAULT;
   private static final int CASE_CHECH_CACHE_HARD_SIZE = 50;
   private static final int CASE_CHECK_CACHE__SOFT_SIZE = 1000;
   private static final boolean SEP_IS_SLASH;
   private static final Logger LOG;
   public final File baseDir;
   private final String canonicalBasePath;
   private boolean emulateCaseSensitiveFileSystem;
   private MruCacheStorage correctCasePaths;

   /** @deprecated */
   @Deprecated
   public FileTemplateLoader() throws IOException {
      this(new File(SecurityUtilities.getSystemProperty("user.dir")));
   }

   public FileTemplateLoader(File baseDir) throws IOException {
      this(baseDir, false);
   }

   public FileTemplateLoader(final File baseDir, final boolean disableCanonicalPathCheck) throws IOException {
      try {
         Object[] retval = (Object[])AccessController.doPrivileged(new PrivilegedExceptionAction<Object[]>() {
            public Object[] run() throws IOException {
               if (!baseDir.exists()) {
                  throw new FileNotFoundException(baseDir + " does not exist.");
               } else if (!baseDir.isDirectory()) {
                  throw new IOException(baseDir + " is not a directory.");
               } else {
                  Object[] retval = new Object[2];
                  if (disableCanonicalPathCheck) {
                     retval[0] = baseDir;
                     retval[1] = null;
                  } else {
                     retval[0] = baseDir.getCanonicalFile();
                     String basePath = ((File)retval[0]).getPath();
                     if (!basePath.endsWith(File.separator)) {
                        basePath = basePath + File.separatorChar;
                     }

                     retval[1] = basePath;
                  }

                  return retval;
               }
            }
         });
         this.baseDir = (File)retval[0];
         this.canonicalBasePath = (String)retval[1];
         this.setEmulateCaseSensitiveFileSystem(this.getEmulateCaseSensitiveFileSystemDefault());
      } catch (PrivilegedActionException var4) {
         throw (IOException)var4.getException();
      }
   }

   public Object findTemplateSource(final String name) throws IOException {
      try {
         return AccessController.doPrivileged(new PrivilegedExceptionAction<File>() {
            public File run() throws IOException {
               File source = new File(FileTemplateLoader.this.baseDir, FileTemplateLoader.SEP_IS_SLASH ? name : name.replace('/', File.separatorChar));
               if (!source.isFile()) {
                  return null;
               } else {
                  if (FileTemplateLoader.this.canonicalBasePath != null) {
                     String normalized = source.getCanonicalPath();
                     if (!normalized.startsWith(FileTemplateLoader.this.canonicalBasePath)) {
                        throw new SecurityException(source.getAbsolutePath() + " resolves to " + normalized + " which  doesn't start with " + FileTemplateLoader.this.canonicalBasePath);
                     }
                  }

                  return FileTemplateLoader.this.emulateCaseSensitiveFileSystem && !FileTemplateLoader.this.isNameCaseCorrect(source) ? null : source;
               }
            }
         });
      } catch (PrivilegedActionException var3) {
         throw (IOException)var3.getException();
      }
   }

   public long getLastModified(final Object templateSource) {
      return (Long)AccessController.doPrivileged(new PrivilegedAction<Long>() {
         public Long run() {
            return ((File)templateSource).lastModified();
         }
      });
   }

   public Reader getReader(final Object templateSource, final String encoding) throws IOException {
      try {
         return (Reader)AccessController.doPrivileged(new PrivilegedExceptionAction<Reader>() {
            public Reader run() throws IOException {
               if (!(templateSource instanceof File)) {
                  throw new IllegalArgumentException("templateSource wasn't a File, but a: " + templateSource.getClass().getName());
               } else {
                  return new InputStreamReader(new FileInputStream((File)templateSource), encoding);
               }
            }
         });
      } catch (PrivilegedActionException var4) {
         throw (IOException)var4.getException();
      }
   }

   private boolean isNameCaseCorrect(File source) throws IOException {
      String sourcePath = source.getPath();
      synchronized(this.correctCasePaths) {
         if (this.correctCasePaths.get(sourcePath) != null) {
            return true;
         }
      }

      File parentDir = source.getParentFile();
      if (parentDir != null) {
         if (!this.baseDir.equals(parentDir) && !this.isNameCaseCorrect(parentDir)) {
            return false;
         }

         String[] listing = parentDir.list();
         if (listing != null) {
            String fileName = source.getName();
            boolean identicalNameFound = false;

            int i;
            for(i = 0; !identicalNameFound && i < listing.length; ++i) {
               if (fileName.equals(listing[i])) {
                  identicalNameFound = true;
               }
            }

            if (!identicalNameFound) {
               for(i = 0; i < listing.length; ++i) {
                  String listingEntry = listing[i];
                  if (fileName.equalsIgnoreCase(listingEntry)) {
                     if (LOG.isDebugEnabled()) {
                        LOG.debug("Emulating file-not-found because of letter case differences to the real file, for: " + sourcePath);
                     }

                     return false;
                  }
               }
            }
         }
      }

      synchronized(this.correctCasePaths) {
         this.correctCasePaths.put(sourcePath, Boolean.TRUE);
         return true;
      }
   }

   public void closeTemplateSource(Object templateSource) {
   }

   public File getBaseDirectory() {
      return this.baseDir;
   }

   public void setEmulateCaseSensitiveFileSystem(boolean nameCaseChecked) {
      if (nameCaseChecked) {
         if (this.correctCasePaths == null) {
            this.correctCasePaths = new MruCacheStorage(50, 1000);
         }
      } else {
         this.correctCasePaths = null;
      }

      this.emulateCaseSensitiveFileSystem = nameCaseChecked;
   }

   public boolean getEmulateCaseSensitiveFileSystem() {
      return this.emulateCaseSensitiveFileSystem;
   }

   protected boolean getEmulateCaseSensitiveFileSystemDefault() {
      return EMULATE_CASE_SENSITIVE_FILE_SYSTEM_DEFAULT;
   }

   public String toString() {
      return TemplateLoaderUtils.getClassNameForToString(this) + "(baseDir=\"" + this.baseDir + "\"" + (this.canonicalBasePath != null ? ", canonicalBasePath=\"" + this.canonicalBasePath + "\"" : "") + (this.emulateCaseSensitiveFileSystem ? ", emulateCaseSensitiveFileSystem=true" : "") + ")";
   }

   static {
      String s = SecurityUtilities.getSystemProperty(SYSTEM_PROPERTY_NAME_EMULATE_CASE_SENSITIVE_FILE_SYSTEM, "false");

      boolean emuCaseSensFS;
      try {
         emuCaseSensFS = StringUtil.getYesNo(s);
      } catch (Exception var3) {
         emuCaseSensFS = false;
      }

      EMULATE_CASE_SENSITIVE_FILE_SYSTEM_DEFAULT = emuCaseSensFS;
      SEP_IS_SLASH = File.separatorChar == '/';
      LOG = Logger.getLogger("freemarker.cache");
   }
}
