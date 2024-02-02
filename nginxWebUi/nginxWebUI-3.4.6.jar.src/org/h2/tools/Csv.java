/*     */ package org.h2.tools;
/*     */ 
/*     */ import java.io.BufferedOutputStream;
/*     */ import java.io.BufferedReader;
/*     */ import java.io.BufferedWriter;
/*     */ import java.io.IOException;
/*     */ import java.io.OutputStream;
/*     */ import java.io.OutputStreamWriter;
/*     */ import java.io.Reader;
/*     */ import java.io.Writer;
/*     */ import java.nio.charset.Charset;
/*     */ import java.nio.charset.StandardCharsets;
/*     */ import java.sql.Connection;
/*     */ import java.sql.ResultSet;
/*     */ import java.sql.ResultSetMetaData;
/*     */ import java.sql.SQLException;
/*     */ import java.sql.Statement;
/*     */ import java.util.ArrayList;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.mvstore.DataUtils;
/*     */ import org.h2.store.fs.FileUtils;
/*     */ import org.h2.util.IOUtils;
/*     */ import org.h2.util.JdbcUtils;
/*     */ import org.h2.util.StringUtils;
/*     */ import org.h2.util.Utils;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class Csv
/*     */   implements SimpleRowSource
/*     */ {
/*     */   private String[] columnNames;
/*     */   private String characterSet;
/*  47 */   private char escapeCharacter = '"';
/*  48 */   private char fieldDelimiter = '"';
/*  49 */   private char fieldSeparatorRead = ',';
/*  50 */   private String fieldSeparatorWrite = ",";
/*     */   private boolean caseSensitiveColumnNames;
/*     */   private boolean preserveWhitespace;
/*     */   private boolean writeColumnHeader = true;
/*     */   private char lineComment;
/*  55 */   private String lineSeparator = System.lineSeparator();
/*  56 */   private String nullString = "";
/*     */   
/*     */   private String fileName;
/*     */   private BufferedReader input;
/*     */   private char[] inputBuffer;
/*     */   private int inputBufferPos;
/*  62 */   private int inputBufferStart = -1; private int inputBufferEnd;
/*     */   private Writer output;
/*     */   private boolean endOfLine;
/*     */   private boolean endOfFile;
/*     */   
/*     */   private int writeResultSet(ResultSet paramResultSet) throws SQLException {
/*     */     try {
/*  69 */       byte b1 = 0;
/*  70 */       ResultSetMetaData resultSetMetaData = paramResultSet.getMetaData();
/*  71 */       int i = resultSetMetaData.getColumnCount();
/*  72 */       String[] arrayOfString = new String[i]; byte b2;
/*  73 */       for (b2 = 0; b2 < i; b2++) {
/*  74 */         arrayOfString[b2] = resultSetMetaData.getColumnLabel(b2 + 1);
/*     */       }
/*  76 */       if (this.writeColumnHeader) {
/*  77 */         writeRow(arrayOfString);
/*     */       }
/*  79 */       while (paramResultSet.next()) {
/*  80 */         for (b2 = 0; b2 < i; b2++) {
/*  81 */           arrayOfString[b2] = paramResultSet.getString(b2 + 1);
/*     */         }
/*  83 */         writeRow(arrayOfString);
/*  84 */         b1++;
/*     */       } 
/*  86 */       this.output.close();
/*  87 */       b2 = b1; return b2;
/*  88 */     } catch (IOException iOException) {
/*  89 */       throw DbException.convertIOException(iOException, null);
/*     */     } finally {
/*  91 */       close();
/*  92 */       JdbcUtils.closeSilently(paramResultSet);
/*     */     } 
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
/*     */   public int write(Writer paramWriter, ResultSet paramResultSet) throws SQLException {
/* 105 */     this.output = paramWriter;
/* 106 */     return writeResultSet(paramResultSet);
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
/*     */   public int write(String paramString1, ResultSet paramResultSet, String paramString2) throws SQLException {
/* 128 */     init(paramString1, paramString2);
/*     */     try {
/* 130 */       initWrite();
/* 131 */       return writeResultSet(paramResultSet);
/* 132 */     } catch (IOException iOException) {
/* 133 */       throw convertException("IOException writing " + paramString1, iOException);
/*     */     } 
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
/*     */   public int write(Connection paramConnection, String paramString1, String paramString2, String paramString3) throws SQLException {
/* 150 */     Statement statement = paramConnection.createStatement();
/* 151 */     ResultSet resultSet = statement.executeQuery(paramString2);
/* 152 */     int i = write(paramString1, resultSet, paramString3);
/* 153 */     statement.close();
/* 154 */     return i;
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
/*     */   
/*     */   public ResultSet read(String paramString1, String[] paramArrayOfString, String paramString2) throws SQLException {
/* 177 */     init(paramString1, paramString2);
/*     */     try {
/* 179 */       return readResultSet(paramArrayOfString);
/* 180 */     } catch (IOException iOException) {
/* 181 */       throw convertException("IOException reading " + paramString1, iOException);
/*     */     } 
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
/*     */   public ResultSet read(Reader paramReader, String[] paramArrayOfString) throws IOException {
/* 197 */     init(null, null);
/* 198 */     this.input = (paramReader instanceof BufferedReader) ? (BufferedReader)paramReader : new BufferedReader(paramReader, 4096);
/*     */     
/* 200 */     return readResultSet(paramArrayOfString);
/*     */   }
/*     */   
/*     */   private ResultSet readResultSet(String[] paramArrayOfString) throws IOException {
/* 204 */     this.columnNames = paramArrayOfString;
/* 205 */     initRead();
/* 206 */     SimpleResultSet simpleResultSet = new SimpleResultSet(this);
/* 207 */     makeColumnNamesUnique();
/* 208 */     for (String str : this.columnNames) {
/* 209 */       simpleResultSet.addColumn(str, 12, 2147483647, 0);
/*     */     }
/* 211 */     return simpleResultSet;
/*     */   }
/*     */   
/*     */   private void makeColumnNamesUnique() {
/* 215 */     for (byte b = 0; b < this.columnNames.length; b++) {
/* 216 */       StringBuilder stringBuilder = new StringBuilder();
/* 217 */       String str = this.columnNames[b];
/* 218 */       if (str == null || str.isEmpty()) {
/* 219 */         stringBuilder.append('C').append(b + 1);
/*     */       } else {
/* 221 */         stringBuilder.append(str);
/*     */       } 
/* 223 */       for (byte b1 = 0; b1 < b; b1++) {
/* 224 */         String str1 = this.columnNames[b1];
/* 225 */         if (stringBuilder.toString().equals(str1)) {
/* 226 */           stringBuilder.append('1');
/* 227 */           b1 = -1;
/*     */         } 
/*     */       } 
/* 230 */       this.columnNames[b] = stringBuilder.toString();
/*     */     } 
/*     */   }
/*     */   
/*     */   private void init(String paramString1, String paramString2) {
/* 235 */     this.fileName = paramString1;
/* 236 */     this.characterSet = paramString2;
/*     */   }
/*     */   
/*     */   private void initWrite() throws IOException {
/* 240 */     if (this.output == null) {
/*     */       try {
/* 242 */         OutputStream outputStream = FileUtils.newOutputStream(this.fileName, false);
/* 243 */         outputStream = new BufferedOutputStream(outputStream, 4096);
/* 244 */         this.output = new BufferedWriter((this.characterSet != null) ? new OutputStreamWriter(outputStream, this.characterSet) : new OutputStreamWriter(outputStream));
/*     */       }
/* 246 */       catch (Exception exception) {
/* 247 */         close();
/* 248 */         throw DataUtils.convertToIOException(exception);
/*     */       } 
/*     */     }
/*     */   }
/*     */   
/*     */   private void writeRow(String[] paramArrayOfString) throws IOException {
/* 254 */     for (byte b = 0; b < paramArrayOfString.length; b++) {
/* 255 */       if (b > 0 && 
/* 256 */         this.fieldSeparatorWrite != null) {
/* 257 */         this.output.write(this.fieldSeparatorWrite);
/*     */       }
/*     */       
/* 260 */       String str = paramArrayOfString[b];
/* 261 */       if (str != null) {
/* 262 */         if (this.escapeCharacter != '\000') {
/* 263 */           if (this.fieldDelimiter != '\000') {
/* 264 */             this.output.write(this.fieldDelimiter);
/*     */           }
/* 266 */           this.output.write(escape(str));
/* 267 */           if (this.fieldDelimiter != '\000') {
/* 268 */             this.output.write(this.fieldDelimiter);
/*     */           }
/*     */         } else {
/* 271 */           this.output.write(str);
/*     */         } 
/* 273 */       } else if (this.nullString != null && this.nullString.length() > 0) {
/* 274 */         this.output.write(this.nullString);
/*     */       } 
/*     */     } 
/* 277 */     this.output.write(this.lineSeparator);
/*     */   }
/*     */   
/*     */   private String escape(String paramString) {
/* 281 */     if (paramString.indexOf(this.fieldDelimiter) < 0 && (
/* 282 */       this.escapeCharacter == this.fieldDelimiter || paramString.indexOf(this.escapeCharacter) < 0)) {
/* 283 */       return paramString;
/*     */     }
/*     */     
/* 286 */     int i = paramString.length();
/* 287 */     StringBuilder stringBuilder = new StringBuilder(i);
/* 288 */     for (byte b = 0; b < i; b++) {
/* 289 */       char c = paramString.charAt(b);
/* 290 */       if (c == this.fieldDelimiter || c == this.escapeCharacter) {
/* 291 */         stringBuilder.append(this.escapeCharacter);
/*     */       }
/* 293 */       stringBuilder.append(c);
/*     */     } 
/* 295 */     return stringBuilder.toString();
/*     */   }
/*     */   
/*     */   private void initRead() throws IOException {
/* 299 */     if (this.input == null) {
/*     */       try {
/* 301 */         this.input = FileUtils.newBufferedReader(this.fileName, (this.characterSet != null) ? 
/* 302 */             Charset.forName(this.characterSet) : StandardCharsets.UTF_8);
/* 303 */       } catch (IOException iOException) {
/* 304 */         close();
/* 305 */         throw iOException;
/*     */       } 
/*     */     }
/* 308 */     this.input.mark(1);
/* 309 */     int i = this.input.read();
/* 310 */     if (i != 65279)
/*     */     {
/*     */       
/* 313 */       this.input.reset();
/*     */     }
/* 315 */     this.inputBuffer = new char[8192];
/* 316 */     if (this.columnNames == null) {
/* 317 */       readHeader();
/*     */     }
/*     */   }
/*     */   
/*     */   private void readHeader() throws IOException {
/* 322 */     ArrayList<String> arrayList = new ArrayList();
/*     */     while (true) {
/* 324 */       String str = readValue();
/* 325 */       if (str == null) {
/* 326 */         if (this.endOfLine) {
/* 327 */           if (this.endOfFile || !arrayList.isEmpty())
/*     */             break; 
/*     */           continue;
/*     */         } 
/* 331 */         str = "COLUMN" + arrayList.size();
/* 332 */         arrayList.add(str);
/*     */         continue;
/*     */       } 
/* 335 */       if (str.isEmpty()) {
/* 336 */         str = "COLUMN" + arrayList.size();
/* 337 */       } else if (!this.caseSensitiveColumnNames && isSimpleColumnName(str)) {
/* 338 */         str = StringUtils.toUpperEnglish(str);
/*     */       } 
/* 340 */       arrayList.add(str);
/* 341 */       if (this.endOfLine) {
/*     */         break;
/*     */       }
/*     */     } 
/*     */     
/* 346 */     this.columnNames = arrayList.<String>toArray(new String[0]);
/*     */   } private static boolean isSimpleColumnName(String paramString) {
/*     */     byte b;
/*     */     int i;
/* 350 */     for (b = 0, i = paramString.length(); b < i; b++) {
/* 351 */       char c = paramString.charAt(b);
/* 352 */       if (b == 0) {
/* 353 */         if (c != '_' && !Character.isLetter(c)) {
/* 354 */           return false;
/*     */         }
/*     */       }
/* 357 */       else if (c != '_' && !Character.isLetterOrDigit(c)) {
/* 358 */         return false;
/*     */       } 
/*     */     } 
/*     */     
/* 362 */     return (paramString.length() != 0);
/*     */   }
/*     */   
/*     */   private void pushBack() {
/* 366 */     this.inputBufferPos--;
/*     */   }
/*     */   
/*     */   private int readChar() throws IOException {
/* 370 */     if (this.inputBufferPos >= this.inputBufferEnd) {
/* 371 */       return readBuffer();
/*     */     }
/* 373 */     return this.inputBuffer[this.inputBufferPos++];
/*     */   }
/*     */   private int readBuffer() throws IOException {
/*     */     byte b;
/* 377 */     if (this.endOfFile) {
/* 378 */       return -1;
/*     */     }
/*     */     
/* 381 */     if (this.inputBufferStart >= 0) {
/* 382 */       b = this.inputBufferPos - this.inputBufferStart;
/* 383 */       if (b > 0) {
/* 384 */         char[] arrayOfChar = this.inputBuffer;
/* 385 */         if (b + 4096 > arrayOfChar.length) {
/* 386 */           this.inputBuffer = new char[arrayOfChar.length * 2];
/*     */         }
/* 388 */         System.arraycopy(arrayOfChar, this.inputBufferStart, this.inputBuffer, 0, b);
/*     */       } 
/* 390 */       this.inputBufferStart = 0;
/*     */     } else {
/* 392 */       b = 0;
/*     */     } 
/* 394 */     this.inputBufferPos = b;
/* 395 */     int i = this.input.read(this.inputBuffer, b, 4096);
/* 396 */     if (i == -1) {
/*     */ 
/*     */       
/* 399 */       this.inputBufferEnd = -1024;
/* 400 */       this.endOfFile = true;
/*     */ 
/*     */       
/* 403 */       this.inputBufferPos++;
/* 404 */       return -1;
/*     */     } 
/* 406 */     this.inputBufferEnd = b + i;
/* 407 */     return this.inputBuffer[this.inputBufferPos++];
/*     */   }
/*     */   private String readValue() throws IOException {
/*     */     int i;
/* 411 */     this.endOfLine = false;
/* 412 */     this.inputBufferStart = this.inputBufferPos;
/*     */     while (true) {
/* 414 */       i = readChar();
/* 415 */       if (i == this.fieldDelimiter) {
/*     */         byte b;
/* 417 */         boolean bool = false;
/* 418 */         this.inputBufferStart = this.inputBufferPos;
/*     */         
/*     */         while (true) {
/* 421 */           i = readChar();
/* 422 */           if (i == this.fieldDelimiter) {
/* 423 */             i = readChar();
/* 424 */             if (i != this.fieldDelimiter) {
/* 425 */               b = 2;
/*     */               break;
/*     */             } 
/* 428 */             bool = true; continue;
/* 429 */           }  if (i == this.escapeCharacter) {
/* 430 */             i = readChar();
/* 431 */             if (i < 0) {
/* 432 */               b = 1;
/*     */               break;
/*     */             } 
/* 435 */             bool = true; continue;
/* 436 */           }  if (i < 0) {
/* 437 */             b = 1;
/*     */             break;
/*     */           } 
/*     */         } 
/* 441 */         String str1 = new String(this.inputBuffer, this.inputBufferStart, this.inputBufferPos - this.inputBufferStart - b);
/*     */         
/* 443 */         if (bool) {
/* 444 */           str1 = unEscape(str1);
/*     */         }
/* 446 */         this.inputBufferStart = -1;
/*     */         
/* 448 */         while (i != this.fieldSeparatorRead) {
/*     */           
/* 450 */           if (i == 10 || i < 0 || i == 13) {
/* 451 */             this.endOfLine = true; break;
/*     */           } 
/* 453 */           if (i == 32 || i == 9) {
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */             
/* 459 */             i = readChar(); continue;
/*     */           }  pushBack(); break;
/* 461 */         }  return str1;
/* 462 */       }  if (i == 10 || i < 0 || i == 13) {
/* 463 */         this.endOfLine = true;
/* 464 */         return null;
/* 465 */       }  if (i == this.fieldSeparatorRead)
/*     */       {
/* 467 */         return null; } 
/* 468 */       if (i <= 32)
/*     */         continue;  break;
/* 470 */     }  if (this.lineComment != '\000' && i == this.lineComment) {
/*     */       
/* 472 */       this.inputBufferStart = -1;
/*     */       do {
/* 474 */         i = readChar();
/* 475 */       } while (i != 10 && i >= 0 && i != 13);
/* 476 */       this.endOfLine = true;
/* 477 */       return null;
/*     */     } 
/*     */     
/*     */     while (true) {
/* 481 */       i = readChar();
/* 482 */       if (i == this.fieldSeparatorRead)
/*     */         break; 
/* 484 */       if (i == 10 || i < 0 || i == 13) {
/* 485 */         this.endOfLine = true;
/*     */         break;
/*     */       } 
/*     */     } 
/* 489 */     String str = new String(this.inputBuffer, this.inputBufferStart, this.inputBufferPos - this.inputBufferStart - 1);
/*     */     
/* 491 */     if (!this.preserveWhitespace) {
/* 492 */       str = str.trim();
/*     */     }
/* 494 */     this.inputBufferStart = -1;
/*     */     
/* 496 */     return readNull(str);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private String readNull(String paramString) {
/* 502 */     return paramString.equals(this.nullString) ? null : paramString;
/*     */   }
/*     */   
/*     */   private String unEscape(String paramString) {
/* 506 */     StringBuilder stringBuilder = new StringBuilder(paramString.length());
/* 507 */     int i = 0;
/* 508 */     char[] arrayOfChar = null;
/*     */     while (true) {
/* 510 */       int j = paramString.indexOf(this.escapeCharacter, i);
/* 511 */       if (j < 0) {
/* 512 */         j = paramString.indexOf(this.fieldDelimiter, i);
/* 513 */         if (j < 0) {
/*     */           break;
/*     */         }
/*     */       } 
/* 517 */       if (arrayOfChar == null) {
/* 518 */         arrayOfChar = paramString.toCharArray();
/*     */       }
/* 520 */       stringBuilder.append(arrayOfChar, i, j - i);
/* 521 */       if (j == paramString.length() - 1) {
/* 522 */         i = paramString.length();
/*     */         break;
/*     */       } 
/* 525 */       stringBuilder.append(arrayOfChar[j + 1]);
/* 526 */       i = j + 2;
/*     */     } 
/* 528 */     stringBuilder.append(paramString, i, paramString.length());
/* 529 */     return stringBuilder.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public Object[] readRow() throws SQLException {
/* 537 */     if (this.input == null) {
/* 538 */       return null;
/*     */     }
/* 540 */     String[] arrayOfString = new String[this.columnNames.length];
/*     */     try {
/* 542 */       byte b = 0;
/*     */       while (true) {
/* 544 */         String str = readValue();
/* 545 */         if (str == null && 
/* 546 */           this.endOfLine) {
/* 547 */           if (!b) {
/* 548 */             if (this.endOfFile) {
/* 549 */               return null;
/*     */             }
/*     */             
/*     */             continue;
/*     */           } 
/*     */           
/*     */           break;
/*     */         } 
/* 557 */         if (b < arrayOfString.length) {
/* 558 */           arrayOfString[b++] = str;
/*     */         }
/* 560 */         if (this.endOfLine) {
/*     */           break;
/*     */         }
/*     */       } 
/* 564 */     } catch (IOException iOException) {
/* 565 */       throw convertException("IOException reading from " + this.fileName, iOException);
/*     */     } 
/* 567 */     return (Object[])arrayOfString;
/*     */   }
/*     */   
/*     */   private static SQLException convertException(String paramString, Exception paramException) {
/* 571 */     return DbException.getJdbcSQLException(90028, paramException, new String[] { paramString });
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void close() {
/* 579 */     IOUtils.closeSilently(this.input);
/* 580 */     this.input = null;
/* 581 */     IOUtils.closeSilently(this.output);
/* 582 */     this.output = null;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void reset() throws SQLException {
/* 590 */     throw new SQLException("Method is not supported", "CSV");
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFieldSeparatorWrite(String paramString) {
/* 599 */     this.fieldSeparatorWrite = paramString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getFieldSeparatorWrite() {
/* 608 */     return this.fieldSeparatorWrite;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setCaseSensitiveColumnNames(boolean paramBoolean) {
/* 618 */     this.caseSensitiveColumnNames = paramBoolean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getCaseSensitiveColumnNames() {
/* 627 */     return this.caseSensitiveColumnNames;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFieldSeparatorRead(char paramChar) {
/* 636 */     this.fieldSeparatorRead = paramChar;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public char getFieldSeparatorRead() {
/* 645 */     return this.fieldSeparatorRead;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setLineCommentCharacter(char paramChar) {
/* 655 */     this.lineComment = paramChar;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public char getLineCommentCharacter() {
/* 664 */     return this.lineComment;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setFieldDelimiter(char paramChar) {
/* 674 */     this.fieldDelimiter = paramChar;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public char getFieldDelimiter() {
/* 683 */     return this.fieldDelimiter;
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
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setEscapeCharacter(char paramChar) {
/* 713 */     this.escapeCharacter = paramChar;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public char getEscapeCharacter() {
/* 722 */     return this.escapeCharacter;
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
/*     */   public void setLineSeparator(String paramString) {
/* 734 */     this.lineSeparator = paramString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getLineSeparator() {
/* 743 */     return this.lineSeparator;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setNullString(String paramString) {
/* 753 */     this.nullString = paramString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String getNullString() {
/* 762 */     return this.nullString;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setPreserveWhitespace(boolean paramBoolean) {
/* 771 */     this.preserveWhitespace = paramBoolean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getPreserveWhitespace() {
/* 780 */     return this.preserveWhitespace;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void setWriteColumnHeader(boolean paramBoolean) {
/* 789 */     this.writeColumnHeader = paramBoolean;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public boolean getWriteColumnHeader() {
/* 798 */     return this.writeColumnHeader;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String setOptions(String paramString) {
/* 809 */     String str = null;
/* 810 */     String[] arrayOfString = StringUtils.arraySplit(paramString, ' ', false);
/* 811 */     for (String str1 : arrayOfString) {
/* 812 */       if (!str1.isEmpty()) {
/*     */ 
/*     */         
/* 815 */         int i = str1.indexOf('=');
/* 816 */         String str2 = StringUtils.trim(str1.substring(0, i), true, true, " ");
/* 817 */         String str3 = str1.substring(i + 1);
/* 818 */         boolean bool = str3.isEmpty() ? false : str3.charAt(0);
/* 819 */         if (isParam(str2, new String[] { "escape", "esc", "escapeCharacter" })) {
/* 820 */           setEscapeCharacter(bool);
/* 821 */         } else if (isParam(str2, new String[] { "fieldDelimiter", "fieldDelim" })) {
/* 822 */           setFieldDelimiter(bool);
/* 823 */         } else if (isParam(str2, new String[] { "fieldSeparator", "fieldSep" })) {
/* 824 */           setFieldSeparatorRead(bool);
/* 825 */           setFieldSeparatorWrite(str3);
/* 826 */         } else if (isParam(str2, new String[] { "lineComment", "lineCommentCharacter" })) {
/* 827 */           setLineCommentCharacter(bool);
/* 828 */         } else if (isParam(str2, new String[] { "lineSeparator", "lineSep" })) {
/* 829 */           setLineSeparator(str3);
/* 830 */         } else if (isParam(str2, new String[] { "null", "nullString" })) {
/* 831 */           setNullString(str3);
/* 832 */         } else if (isParam(str2, new String[] { "charset", "characterSet" })) {
/* 833 */           str = str3;
/* 834 */         } else if (isParam(str2, new String[] { "preserveWhitespace" })) {
/* 835 */           setPreserveWhitespace(Utils.parseBoolean(str3, false, false));
/* 836 */         } else if (isParam(str2, new String[] { "writeColumnHeader" })) {
/* 837 */           setWriteColumnHeader(Utils.parseBoolean(str3, true, false));
/* 838 */         } else if (isParam(str2, new String[] { "caseSensitiveColumnNames" })) {
/* 839 */           setCaseSensitiveColumnNames(Utils.parseBoolean(str3, false, false));
/*     */         } else {
/* 841 */           throw DbException.getUnsupportedException(str2);
/*     */         } 
/*     */       } 
/* 844 */     }  return str;
/*     */   }
/*     */   
/*     */   private static boolean isParam(String paramString, String... paramVarArgs) {
/* 848 */     for (String str : paramVarArgs) {
/* 849 */       if (paramString.equalsIgnoreCase(str)) {
/* 850 */         return true;
/*     */       }
/*     */     } 
/* 853 */     return false;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\tools\Csv.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */