/*     */ package org.h2.command.dml;
/*     */ 
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.IOException;
/*     */ import java.io.InputStream;
/*     */ import java.io.InputStreamReader;
/*     */ import java.io.OutputStream;
/*     */ import java.nio.charset.Charset;
/*     */ import org.h2.command.Prepared;
/*     */ import org.h2.engine.Database;
/*     */ import org.h2.engine.SessionLocal;
/*     */ import org.h2.engine.SysProperties;
/*     */ import org.h2.expression.Expression;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.security.SHA256;
/*     */ import org.h2.store.DataHandler;
/*     */ import org.h2.store.FileStore;
/*     */ import org.h2.store.FileStoreInputStream;
/*     */ import org.h2.store.FileStoreOutputStream;
/*     */ import org.h2.store.fs.FileUtils;
/*     */ import org.h2.tools.CompressTool;
/*     */ import org.h2.util.IOUtils;
/*     */ import org.h2.util.StringUtils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ abstract class ScriptBase
/*     */   extends Prepared
/*     */ {
/*     */   private static final String SCRIPT_SQL = "script.sql";
/*     */   protected OutputStream out;
/*     */   protected BufferedReader reader;
/*     */   private Expression fileNameExpr;
/*     */   private Expression password;
/*     */   private String fileName;
/*     */   private String cipher;
/*     */   private FileStore store;
/*     */   private String compressionAlgorithm;
/*     */   
/*     */   ScriptBase(SessionLocal paramSessionLocal) {
/*  66 */     super(paramSessionLocal);
/*     */   }
/*     */   
/*     */   public void setCipher(String paramString) {
/*  70 */     this.cipher = paramString;
/*     */   }
/*     */   
/*     */   private boolean isEncrypted() {
/*  74 */     return (this.cipher != null);
/*     */   }
/*     */   
/*     */   public void setPassword(Expression paramExpression) {
/*  78 */     this.password = paramExpression;
/*     */   }
/*     */   
/*     */   public void setFileNameExpr(Expression paramExpression) {
/*  82 */     this.fileNameExpr = paramExpression;
/*     */   }
/*     */   
/*     */   protected String getFileName() {
/*  86 */     if (this.fileNameExpr != null && this.fileName == null) {
/*  87 */       this.fileName = this.fileNameExpr.optimize(this.session).getValue(this.session).getString();
/*  88 */       if (this.fileName == null || StringUtils.isWhitespaceOrEmpty(this.fileName)) {
/*  89 */         this.fileName = "script.sql";
/*     */       }
/*  91 */       this.fileName = SysProperties.getScriptDirectory() + this.fileName;
/*     */     } 
/*  93 */     return this.fileName;
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isTransactional() {
/*  98 */     return false;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void deleteStore() {
/* 105 */     String str = getFileName();
/* 106 */     if (str != null) {
/* 107 */       FileUtils.delete(str);
/*     */     }
/*     */   }
/*     */   
/*     */   private void initStore() {
/* 112 */     Database database = this.session.getDatabase();
/* 113 */     byte[] arrayOfByte = null;
/* 114 */     if (this.cipher != null && this.password != null) {
/*     */       
/* 116 */       char[] arrayOfChar = this.password.optimize(this.session).getValue(this.session).getString().toCharArray();
/* 117 */       arrayOfByte = SHA256.getKeyPasswordHash("script", arrayOfChar);
/*     */     } 
/* 119 */     String str = getFileName();
/* 120 */     this.store = FileStore.open((DataHandler)database, str, "rw", this.cipher, arrayOfByte);
/* 121 */     this.store.setCheckedWriting(false);
/* 122 */     this.store.init();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void openOutput() {
/* 129 */     String str = getFileName();
/* 130 */     if (str == null) {
/*     */       return;
/*     */     }
/* 133 */     if (isEncrypted()) {
/* 134 */       initStore();
/* 135 */       this.out = (OutputStream)new FileStoreOutputStream(this.store, this.compressionAlgorithm);
/*     */       
/* 137 */       this.out = new BufferedOutputStream(this.out, 131072);
/*     */     } else {
/*     */       OutputStream outputStream;
/*     */       try {
/* 141 */         outputStream = FileUtils.newOutputStream(str, false);
/* 142 */       } catch (IOException iOException) {
/* 143 */         throw DbException.convertIOException(iOException, null);
/*     */       } 
/* 145 */       this.out = new BufferedOutputStream(outputStream, 4096);
/* 146 */       this.out = CompressTool.wrapOutputStream(this.out, this.compressionAlgorithm, "script.sql");
/*     */     } 
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void openInput(Charset paramCharset) {
/*     */     InputStream inputStream;
/* 156 */     String str = getFileName();
/* 157 */     if (str == null) {
/*     */       return;
/*     */     }
/*     */     
/* 161 */     if (isEncrypted()) {
/* 162 */       initStore();
/* 163 */       FileStoreInputStream fileStoreInputStream = new FileStoreInputStream(this.store, (this.compressionAlgorithm != null), false);
/*     */     } else {
/*     */       try {
/* 166 */         inputStream = FileUtils.newInputStream(str);
/* 167 */       } catch (IOException iOException) {
/* 168 */         throw DbException.convertIOException(iOException, str);
/*     */       } 
/* 170 */       inputStream = CompressTool.wrapInputStream(inputStream, this.compressionAlgorithm, "script.sql");
/* 171 */       if (inputStream == null) {
/* 172 */         throw DbException.get(90124, "script.sql in " + str);
/*     */       }
/*     */     } 
/* 175 */     this.reader = new BufferedReader(new InputStreamReader(inputStream, paramCharset), 4096);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   void closeIO() {
/* 182 */     IOUtils.closeSilently(this.out);
/* 183 */     this.out = null;
/* 184 */     IOUtils.closeSilently(this.reader);
/* 185 */     this.reader = null;
/* 186 */     if (this.store != null) {
/* 187 */       this.store.closeSilently();
/* 188 */       this.store = null;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean needRecompile() {
/* 194 */     return false;
/*     */   }
/*     */   
/*     */   public void setCompressionAlgorithm(String paramString) {
/* 198 */     this.compressionAlgorithm = paramString;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\dml\ScriptBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */