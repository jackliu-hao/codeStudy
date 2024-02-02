/*      */ package org.h2.server.pg;
/*      */ 
/*      */ import java.io.ByteArrayInputStream;
/*      */ import java.io.ByteArrayOutputStream;
/*      */ import java.io.DataInputStream;
/*      */ import java.io.DataOutputStream;
/*      */ import java.io.EOFException;
/*      */ import java.io.IOException;
/*      */ import java.io.InputStream;
/*      */ import java.io.OutputStream;
/*      */ import java.io.StringReader;
/*      */ import java.math.BigDecimal;
/*      */ import java.math.BigInteger;
/*      */ import java.net.Socket;
/*      */ import java.nio.charset.Charset;
/*      */ import java.nio.charset.StandardCharsets;
/*      */ import java.sql.SQLException;
/*      */ import java.util.ArrayList;
/*      */ import java.util.HashMap;
/*      */ import java.util.HashSet;
/*      */ import java.util.Properties;
/*      */ import java.util.regex.Pattern;
/*      */ import org.h2.command.Command;
/*      */ import org.h2.command.CommandInterface;
/*      */ import org.h2.engine.CastDataProvider;
/*      */ import org.h2.engine.ConnectionInfo;
/*      */ import org.h2.engine.Database;
/*      */ import org.h2.engine.Engine;
/*      */ import org.h2.engine.SessionLocal;
/*      */ import org.h2.engine.SysProperties;
/*      */ import org.h2.expression.ParameterInterface;
/*      */ import org.h2.message.DbException;
/*      */ import org.h2.result.ResultInterface;
/*      */ import org.h2.schema.Schema;
/*      */ import org.h2.table.Column;
/*      */ import org.h2.table.Table;
/*      */ import org.h2.util.DateTimeUtils;
/*      */ import org.h2.util.MathUtils;
/*      */ import org.h2.util.NetUtils;
/*      */ import org.h2.util.NetworkConnectionInfo;
/*      */ import org.h2.util.ScriptReader;
/*      */ import org.h2.util.StringUtils;
/*      */ import org.h2.util.TimeZoneProvider;
/*      */ import org.h2.util.Utils;
/*      */ import org.h2.util.Utils10;
/*      */ import org.h2.value.CaseInsensitiveMap;
/*      */ import org.h2.value.TypeInfo;
/*      */ import org.h2.value.Value;
/*      */ import org.h2.value.ValueArray;
/*      */ import org.h2.value.ValueBigint;
/*      */ import org.h2.value.ValueDate;
/*      */ import org.h2.value.ValueDouble;
/*      */ import org.h2.value.ValueInteger;
/*      */ import org.h2.value.ValueNull;
/*      */ import org.h2.value.ValueReal;
/*      */ import org.h2.value.ValueSmallint;
/*      */ import org.h2.value.ValueTime;
/*      */ import org.h2.value.ValueTimeTimeZone;
/*      */ import org.h2.value.ValueTimestamp;
/*      */ import org.h2.value.ValueTimestampTimeZone;
/*      */ import org.h2.value.ValueVarbinary;
/*      */ import org.h2.value.ValueVarchar;
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
/*      */ public final class PgServerThread
/*      */   implements Runnable
/*      */ {
/*      */   private static final boolean INTEGER_DATE_TYPES = false;
/*   78 */   private static final Pattern SHOULD_QUOTE = Pattern.compile(".*[\",\\\\{}].*"); private final PgServer server; private Socket socket; private SessionLocal session; private boolean stop; private DataInputStream dataInRaw; private DataInputStream dataIn; private OutputStream out; private int messageType;
/*      */   
/*      */   private static String pgTimeZone(String paramString) {
/*   81 */     if (paramString.startsWith("GMT+"))
/*   82 */       return convertTimeZone(paramString, "GMT-"); 
/*   83 */     if (paramString.startsWith("GMT-"))
/*   84 */       return convertTimeZone(paramString, "GMT+"); 
/*   85 */     if (paramString.startsWith("UTC+"))
/*   86 */       return convertTimeZone(paramString, "UTC-"); 
/*   87 */     if (paramString.startsWith("UTC-")) {
/*   88 */       return convertTimeZone(paramString, "UTC+");
/*      */     }
/*   90 */     return paramString;
/*      */   }
/*      */ 
/*      */   
/*      */   private static String convertTimeZone(String paramString1, String paramString2) {
/*   95 */     int i = paramString1.length();
/*   96 */     return (new StringBuilder(i)).append(paramString2).append(paramString1, 4, i).toString();
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
/*  107 */   private ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
/*      */   private DataOutputStream dataOut;
/*      */   private Thread thread;
/*      */   private boolean initDone;
/*      */   private String userName;
/*      */   private String databaseName;
/*      */   private int processId;
/*      */   private final int secret;
/*      */   private CommandInterface activeRequest;
/*  116 */   private String clientEncoding = SysProperties.PG_DEFAULT_CLIENT_ENCODING;
/*  117 */   private String dateStyle = "ISO, MDY";
/*  118 */   private TimeZoneProvider timeZone = DateTimeUtils.getTimeZone();
/*  119 */   private final HashMap<String, Prepared> prepared = (HashMap<String, Prepared>)new CaseInsensitiveMap();
/*      */   
/*  121 */   private final HashMap<String, Portal> portals = (HashMap<String, Portal>)new CaseInsensitiveMap();
/*      */ 
/*      */   
/*      */   PgServerThread(Socket paramSocket, PgServer paramPgServer) {
/*  125 */     this.server = paramPgServer;
/*  126 */     this.socket = paramSocket;
/*  127 */     this.secret = (int)MathUtils.secureRandomLong();
/*      */   }
/*      */ 
/*      */   
/*      */   public void run() {
/*      */     try {
/*  133 */       this.server.trace("Connect");
/*  134 */       InputStream inputStream = this.socket.getInputStream();
/*  135 */       this.out = this.socket.getOutputStream();
/*  136 */       this.dataInRaw = new DataInputStream(inputStream);
/*  137 */       while (!this.stop) {
/*  138 */         process();
/*  139 */         this.out.flush();
/*      */       } 
/*  141 */     } catch (EOFException eOFException) {
/*      */     
/*  143 */     } catch (Exception exception) {
/*  144 */       this.server.traceError(exception);
/*      */     } finally {
/*  146 */       this.server.trace("Disconnect");
/*  147 */       close();
/*      */     } 
/*      */   }
/*      */   
/*      */   private String readString() throws IOException {
/*  152 */     ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
/*      */     while (true) {
/*  154 */       int i = this.dataIn.read();
/*  155 */       if (i <= 0) {
/*      */         break;
/*      */       }
/*  158 */       byteArrayOutputStream.write(i);
/*      */     } 
/*  160 */     return Utils10.byteArrayOutputStreamToString(byteArrayOutputStream, getEncoding());
/*      */   }
/*      */   
/*      */   private int readInt() throws IOException {
/*  164 */     return this.dataIn.readInt();
/*      */   }
/*      */   
/*      */   private short readShort() throws IOException {
/*  168 */     return this.dataIn.readShort();
/*      */   }
/*      */   
/*      */   private byte readByte() throws IOException {
/*  172 */     return this.dataIn.readByte();
/*      */   }
/*      */   
/*      */   private void readFully(byte[] paramArrayOfbyte) throws IOException {
/*  176 */     this.dataIn.readFully(paramArrayOfbyte); } private void process() throws IOException { boolean bool; int j; String str2; Prepared prepared1; Portal portal1; char c; String str1; short s; String str3;
/*      */     Portal portal2;
/*      */     ScriptReader scriptReader;
/*      */     int[] arrayOfInt;
/*      */     Prepared prepared2;
/*  181 */     if (this.initDone) {
/*  182 */       bool = this.dataInRaw.read();
/*  183 */       if (bool < 0) {
/*  184 */         this.stop = true;
/*      */         return;
/*      */       } 
/*      */     } else {
/*  188 */       bool = false;
/*      */     } 
/*  190 */     int i = this.dataInRaw.readInt();
/*  191 */     i -= 4;
/*  192 */     byte[] arrayOfByte = Utils.newBytes(i);
/*  193 */     this.dataInRaw.readFully(arrayOfByte, 0, i);
/*  194 */     this.dataIn = new DataInputStream(new ByteArrayInputStream(arrayOfByte, 0, i));
/*  195 */     switch (bool) {
/*      */       case false:
/*  197 */         this.server.trace("Init");
/*  198 */         j = readInt();
/*  199 */         if (j == 80877102) {
/*  200 */           this.server.trace("CancelRequest");
/*  201 */           int k = readInt();
/*  202 */           int m = readInt();
/*  203 */           PgServerThread pgServerThread = this.server.getThread(k);
/*  204 */           if (pgServerThread != null && m == pgServerThread.secret) {
/*  205 */             pgServerThread.cancelRequest();
/*      */           
/*      */           }
/*      */           else {
/*      */             
/*  210 */             this.server.trace("Invalid CancelRequest: pid=" + k + ", key=" + m);
/*      */           } 
/*  212 */           close();
/*  213 */         } else if (j == 80877103) {
/*  214 */           this.server.trace("SSLRequest");
/*  215 */           this.out.write(78);
/*      */         } else {
/*  217 */           this.server.trace("StartupMessage");
/*  218 */           this.server.trace(" version " + j + " (" + (j >> 16) + "." + (j & 0xFF) + ")");
/*      */           while (true) {
/*      */             int k;
/*  221 */             String str4 = readString();
/*  222 */             if (str4.isEmpty()) {
/*      */               break;
/*      */             }
/*  225 */             String str5 = readString();
/*  226 */             switch (str4) {
/*      */               case "user":
/*  228 */                 this.userName = str5;
/*      */                 break;
/*      */               case "database":
/*  231 */                 this.databaseName = this.server.checkKeyAndGetDatabaseName(str5);
/*      */                 break;
/*      */               
/*      */               case "client_encoding":
/*  235 */                 k = str5.length();
/*  236 */                 if (k >= 2 && str5.charAt(0) == '\'' && str5
/*  237 */                   .charAt(k - 1) == '\'') {
/*  238 */                   str5 = str5.substring(1, k - 1);
/*      */                 }
/*      */                 
/*  241 */                 this.clientEncoding = str5;
/*      */                 break;
/*      */               case "DateStyle":
/*  244 */                 if (str5.indexOf(',') < 0) {
/*  245 */                   str5 = str5 + ", MDY";
/*      */                 }
/*  247 */                 this.dateStyle = str5;
/*      */                 break;
/*      */               case "TimeZone":
/*      */                 try {
/*  251 */                   this.timeZone = TimeZoneProvider.ofId(pgTimeZone(str5));
/*  252 */                 } catch (Exception exception) {
/*  253 */                   this.server.trace("Unknown TimeZone: " + str5);
/*      */                 } 
/*      */                 break;
/*      */             } 
/*      */ 
/*      */             
/*  259 */             this.server.trace(" param " + str4 + "=" + str5);
/*      */           } 
/*  261 */           sendAuthenticationCleartextPassword();
/*  262 */           this.initDone = true;
/*      */         } 
/*      */         return;
/*      */       case true:
/*  266 */         this.server.trace("PasswordMessage");
/*  267 */         str2 = readString();
/*      */         try {
/*  269 */           Properties properties = new Properties();
/*  270 */           properties.put("MODE", "PostgreSQL");
/*  271 */           properties.put("DATABASE_TO_LOWER", "TRUE");
/*  272 */           properties.put("DEFAULT_NULL_ORDERING", "HIGH");
/*  273 */           String str4 = "jdbc:h2:" + this.databaseName;
/*  274 */           ConnectionInfo connectionInfo = new ConnectionInfo(str4, properties, this.userName, str2);
/*  275 */           String str5 = this.server.getBaseDir();
/*  276 */           if (str5 == null) {
/*  277 */             str5 = SysProperties.getBaseDir();
/*      */           }
/*  279 */           if (str5 != null) {
/*  280 */             connectionInfo.setBaseDir(str5);
/*      */           }
/*  282 */           if (this.server.getIfExists()) {
/*  283 */             connectionInfo.setProperty("FORBID_CREATION", "TRUE");
/*      */           }
/*  285 */           connectionInfo.setNetworkConnectionInfo(new NetworkConnectionInfo(
/*  286 */                 NetUtils.ipToShortForm(new StringBuilder("pg://"), this.socket
/*  287 */                   .getLocalAddress().getAddress(), true)
/*  288 */                 .append(':').append(this.socket.getLocalPort()).toString(), this.socket
/*  289 */                 .getInetAddress().getAddress(), this.socket.getPort(), null));
/*  290 */           this.session = Engine.createSession(connectionInfo);
/*  291 */           initDb();
/*  292 */           sendAuthenticationOk();
/*  293 */         } catch (Exception exception) {
/*  294 */           exception.printStackTrace();
/*  295 */           this.stop = true;
/*      */         } 
/*      */         return;
/*      */       
/*      */       case true:
/*  300 */         this.server.trace("Parse");
/*  301 */         prepared1 = new Prepared();
/*  302 */         prepared1.name = readString();
/*  303 */         prepared1.sql = getSQL(readString());
/*  304 */         s = readShort();
/*  305 */         arrayOfInt = null;
/*  306 */         if (s > 0) {
/*  307 */           arrayOfInt = new int[s];
/*  308 */           for (byte b = 0; b < s; b++) {
/*  309 */             arrayOfInt[b] = readInt();
/*      */           }
/*      */         } 
/*      */         try {
/*  313 */           prepared1.prep = (CommandInterface)this.session.prepareLocal(prepared1.sql);
/*  314 */           ArrayList<ParameterInterface> arrayList = prepared1.prep.getParameters();
/*  315 */           int k = arrayList.size();
/*  316 */           prepared1.paramType = new int[k];
/*  317 */           for (byte b = 0; b < k; b++) {
/*      */             int m;
/*  319 */             if (b < s && arrayOfInt[b] != 0) {
/*  320 */               m = arrayOfInt[b];
/*  321 */               this.server.checkType(m);
/*      */             } else {
/*  323 */               m = PgServer.convertType(((ParameterInterface)arrayList.get(b)).getType());
/*      */             } 
/*  325 */             prepared1.paramType[b] = m;
/*      */           } 
/*  327 */           this.prepared.put(prepared1.name, prepared1);
/*  328 */           sendParseComplete();
/*  329 */         } catch (Exception exception) {
/*  330 */           sendErrorResponse(exception);
/*      */         } 
/*      */         return;
/*      */       
/*      */       case true:
/*  335 */         this.server.trace("Bind");
/*  336 */         portal1 = new Portal();
/*  337 */         portal1.name = readString();
/*  338 */         str3 = readString();
/*  339 */         prepared2 = this.prepared.get(str3);
/*  340 */         if (prepared2 == null) {
/*  341 */           sendErrorResponse("Prepared not found");
/*      */         } else {
/*      */           
/*  344 */           portal1.prep = prepared2;
/*  345 */           this.portals.put(portal1.name, portal1);
/*  346 */           short s1 = readShort();
/*  347 */           int[] arrayOfInt1 = new int[s1]; short s2;
/*  348 */           for (s2 = 0; s2 < s1; s2++) {
/*  349 */             arrayOfInt1[s2] = readShort();
/*      */           }
/*  351 */           s2 = readShort();
/*      */           try {
/*  353 */             ArrayList<? extends ParameterInterface> arrayList = prepared2.prep.getParameters();
/*  354 */             for (byte b1 = 0; b1 < s2; b1++) {
/*  355 */               setParameter(arrayList, prepared2.paramType[b1], b1, arrayOfInt1);
/*      */             }
/*  357 */           } catch (Exception exception) {
/*  358 */             sendErrorResponse(exception);
/*      */           } 
/*      */           
/*  361 */           short s3 = readShort();
/*  362 */           portal1.resultColumnFormat = new int[s3];
/*  363 */           for (byte b = 0; b < s3; b++) {
/*  364 */             portal1.resultColumnFormat[b] = readShort();
/*      */           }
/*  366 */           sendBindComplete();
/*      */         } 
/*      */         return;
/*      */       case true:
/*  370 */         c = (char)readByte();
/*  371 */         str3 = readString();
/*  372 */         this.server.trace("Close");
/*  373 */         if (c == 'S') {
/*  374 */           prepared2 = this.prepared.remove(str3);
/*  375 */           if (prepared2 != null) {
/*  376 */             prepared2.close();
/*      */           }
/*  378 */         } else if (c == 'P') {
/*  379 */           Portal portal = this.portals.remove(str3);
/*  380 */           if (portal != null) {
/*  381 */             portal.prep.closeResult();
/*      */           }
/*      */         } else {
/*  384 */           this.server.trace("expected S or P, got " + c);
/*  385 */           sendErrorResponse("expected S or P");
/*      */           return;
/*      */         } 
/*  388 */         sendCloseComplete();
/*      */         return;
/*      */       
/*      */       case true:
/*  392 */         c = (char)readByte();
/*  393 */         str3 = readString();
/*  394 */         this.server.trace("Describe");
/*  395 */         if (c == 'S') {
/*  396 */           prepared2 = this.prepared.get(str3);
/*  397 */           if (prepared2 == null) {
/*  398 */             sendErrorResponse("Prepared not found: " + str3);
/*      */           } else {
/*      */             try {
/*  401 */               sendParameterDescription(prepared2.prep.getParameters(), prepared2.paramType);
/*  402 */               sendRowDescription(prepared2.prep.getMetaData(), null);
/*  403 */             } catch (Exception exception) {
/*  404 */               sendErrorResponse(exception);
/*      */             } 
/*      */           } 
/*  407 */         } else if (c == 'P') {
/*  408 */           Portal portal = this.portals.get(str3);
/*  409 */           if (portal == null) {
/*  410 */             sendErrorResponse("Portal not found: " + str3);
/*      */           } else {
/*  412 */             CommandInterface commandInterface = portal.prep.prep;
/*      */             try {
/*  414 */               sendRowDescription(commandInterface.getMetaData(), portal.resultColumnFormat);
/*  415 */             } catch (Exception exception) {
/*  416 */               sendErrorResponse(exception);
/*      */             } 
/*      */           } 
/*      */         } else {
/*  420 */           this.server.trace("expected S or P, got " + c);
/*  421 */           sendErrorResponse("expected S or P");
/*      */         } 
/*      */         return;
/*      */       
/*      */       case true:
/*  426 */         str1 = readString();
/*  427 */         this.server.trace("Execute");
/*  428 */         portal2 = this.portals.get(str1);
/*  429 */         if (portal2 == null) {
/*  430 */           sendErrorResponse("Portal not found: " + str1);
/*      */         } else {
/*      */           
/*  433 */           int k = readInt();
/*  434 */           Prepared prepared = portal2.prep;
/*  435 */           CommandInterface commandInterface = prepared.prep;
/*  436 */           this.server.trace(prepared.sql);
/*      */           try {
/*  438 */             setActiveRequest(commandInterface);
/*  439 */             if (commandInterface.isQuery()) {
/*  440 */               executeQuery(prepared, commandInterface, portal2.resultColumnFormat, k);
/*      */             } else {
/*  442 */               sendCommandComplete(commandInterface, commandInterface.executeUpdate(null).getUpdateCount());
/*      */             } 
/*  444 */           } catch (Exception exception) {
/*  445 */             sendErrorOrCancelResponse(exception);
/*      */           } finally {
/*  447 */             setActiveRequest(null);
/*      */           } 
/*      */         } 
/*      */         return;
/*      */       case true:
/*  452 */         this.server.trace("Sync");
/*  453 */         sendReadyForQuery();
/*      */         return;
/*      */       
/*      */       case true:
/*  457 */         this.server.trace("Query");
/*  458 */         str1 = readString();
/*      */         
/*  460 */         scriptReader = new ScriptReader(new StringReader(str1));
/*      */         while (true) {
/*  462 */           String str = scriptReader.readStatement();
/*  463 */           if (str == null) {
/*      */             break;
/*      */           }
/*  466 */           str = getSQL(str);
/*  467 */           try (Command null = this.session.prepareLocal(str)) {
/*  468 */             setActiveRequest((CommandInterface)command);
/*  469 */             if (command.isQuery()) {
/*  470 */               try (ResultInterface null = command.executeQuery(0L, false)) {
/*  471 */                 sendRowDescription(resultInterface, null);
/*  472 */                 while (resultInterface.next()) {
/*  473 */                   sendDataRow(resultInterface, null);
/*      */                 }
/*  475 */                 sendCommandComplete((CommandInterface)command, 0L);
/*      */               } 
/*      */             } else {
/*  478 */               sendCommandComplete((CommandInterface)command, command.executeUpdate(null).getUpdateCount());
/*      */             } 
/*  480 */           } catch (Exception exception) {
/*  481 */             sendErrorOrCancelResponse(exception);
/*      */             break;
/*      */           } finally {
/*  484 */             setActiveRequest(null);
/*      */           } 
/*      */         } 
/*  487 */         sendReadyForQuery();
/*      */         return;
/*      */       
/*      */       case true:
/*  491 */         this.server.trace("Terminate");
/*  492 */         close();
/*      */         return;
/*      */     } 
/*      */     
/*  496 */     this.server.trace("Unsupported: " + bool + " (" + (char)bool + ")"); }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void executeQuery(Prepared paramPrepared, CommandInterface paramCommandInterface, int[] paramArrayOfint, int paramInt) throws Exception {
/*  503 */     ResultInterface resultInterface = paramPrepared.result;
/*  504 */     if (resultInterface == null) {
/*  505 */       resultInterface = paramCommandInterface.executeQuery(0L, false);
/*      */     }
/*      */     
/*      */     try {
/*  509 */       if (paramInt == 0) {
/*  510 */         while (resultInterface.next()) {
/*  511 */           sendDataRow(resultInterface, paramArrayOfint);
/*      */         }
/*      */       } else {
/*  514 */         for (; paramInt > 0 && resultInterface.next(); paramInt--) {
/*  515 */           sendDataRow(resultInterface, paramArrayOfint);
/*      */         }
/*  517 */         if (resultInterface.hasNext()) {
/*  518 */           paramPrepared.result = resultInterface;
/*  519 */           sendCommandSuspended();
/*      */           return;
/*      */         } 
/*      */       } 
/*  523 */       paramPrepared.closeResult();
/*  524 */       sendCommandComplete(paramCommandInterface, 0L);
/*  525 */     } catch (Exception exception) {
/*  526 */       paramPrepared.closeResult();
/*  527 */       throw exception;
/*      */     } 
/*      */   }
/*      */   
/*      */   private String getSQL(String paramString) {
/*  532 */     String str = StringUtils.toLowerEnglish(paramString);
/*  533 */     if (str.startsWith("show max_identifier_length")) {
/*  534 */       paramString = "CALL 63";
/*  535 */     } else if (str.startsWith("set client_encoding to")) {
/*  536 */       paramString = "set DATESTYLE ISO";
/*      */     } 
/*      */     
/*  539 */     if (this.server.getTrace()) {
/*  540 */       this.server.trace(paramString + ";");
/*      */     }
/*  542 */     return paramString;
/*      */   }
/*      */   
/*      */   private void sendCommandComplete(CommandInterface paramCommandInterface, long paramLong) throws IOException {
/*  546 */     startMessage(67);
/*  547 */     switch (paramCommandInterface.getCommandType()) {
/*      */       case 61:
/*  549 */         writeStringPart("INSERT 0 ");
/*  550 */         writeString(Long.toString(paramLong));
/*      */         break;
/*      */       case 68:
/*  553 */         writeStringPart("UPDATE ");
/*  554 */         writeString(Long.toString(paramLong));
/*      */         break;
/*      */       case 58:
/*  557 */         writeStringPart("DELETE ");
/*  558 */         writeString(Long.toString(paramLong));
/*      */         break;
/*      */       case 57:
/*      */       case 66:
/*  562 */         writeString("SELECT");
/*      */         break;
/*      */       case 83:
/*  565 */         writeString("BEGIN");
/*      */         break;
/*      */       default:
/*  568 */         this.server.trace("check CommandComplete tag for command " + paramCommandInterface);
/*  569 */         writeStringPart("UPDATE ");
/*  570 */         writeString(Long.toString(paramLong)); break;
/*      */     } 
/*  572 */     sendMessage();
/*      */   }
/*      */   
/*      */   private void sendCommandSuspended() throws IOException {
/*  576 */     startMessage(115);
/*  577 */     sendMessage();
/*      */   }
/*      */   
/*      */   private void sendDataRow(ResultInterface paramResultInterface, int[] paramArrayOfint) throws IOException {
/*  581 */     int i = paramResultInterface.getVisibleColumnCount();
/*  582 */     startMessage(68);
/*  583 */     writeShort(i);
/*  584 */     Value[] arrayOfValue = paramResultInterface.currentRow();
/*  585 */     for (byte b = 0; b < i; b++) {
/*  586 */       int j = PgServer.convertType(paramResultInterface.getColumnType(b));
/*  587 */       boolean bool = formatAsText(j, paramArrayOfint, b);
/*  588 */       writeDataColumn(arrayOfValue[b], j, bool);
/*      */     } 
/*  590 */     sendMessage();
/*      */   }
/*      */   
/*      */   private static long toPostgreDays(long paramLong) {
/*  594 */     return DateTimeUtils.absoluteDayFromDateValue(paramLong) - 10957L;
/*      */   }
/*      */   
/*      */   private void writeDataColumn(Value paramValue, int paramInt, boolean paramBoolean) throws IOException {
/*  598 */     if (paramValue == ValueNull.INSTANCE) {
/*  599 */       writeInt(-1);
/*      */       return;
/*      */     } 
/*  602 */     if (paramBoolean) {
/*      */       byte[] arrayOfByte1; ByteArrayOutputStream byteArrayOutputStream; int i; Value[] arrayOfValue; int j; Charset charset; byte b2; byte[] arrayOfByte3; byte b1, b3, b4;
/*  604 */       switch (paramInt) {
/*      */         case 16:
/*  606 */           writeInt(1);
/*  607 */           this.dataOut.writeByte(paramValue.getBoolean() ? 116 : 102);
/*      */           return;
/*      */         case 17:
/*  610 */           arrayOfByte1 = paramValue.getBytesNoCopy();
/*  611 */           i = arrayOfByte1.length;
/*  612 */           j = i;
/*  613 */           for (b2 = 0; b2 < i; b2++) {
/*  614 */             byte b = arrayOfByte1[b2];
/*  615 */             if (b < 32 || b > 126) {
/*  616 */               j += 3;
/*  617 */             } else if (b == 92) {
/*  618 */               j++;
/*      */             } 
/*      */           } 
/*  621 */           arrayOfByte3 = new byte[j];
/*  622 */           for (b3 = 0, b4 = 0; b3 < i; b3++) {
/*  623 */             byte b = arrayOfByte1[b3];
/*  624 */             if (b < 32 || b > 126) {
/*  625 */               arrayOfByte3[b4++] = 92;
/*  626 */               arrayOfByte3[b4++] = (byte)((b >>> 6 & 0x3) + 48);
/*  627 */               arrayOfByte3[b4++] = (byte)((b >>> 3 & 0x7) + 48);
/*  628 */               arrayOfByte3[b4++] = (byte)((b & 0x7) + 48);
/*  629 */             } else if (b == 92) {
/*  630 */               arrayOfByte3[b4++] = 92;
/*  631 */               arrayOfByte3[b4++] = 92;
/*      */             } else {
/*  633 */               arrayOfByte3[b4++] = b;
/*      */             } 
/*      */           } 
/*  636 */           writeInt(arrayOfByte3.length);
/*  637 */           write(arrayOfByte3);
/*      */           return;
/*      */         
/*      */         case 1005:
/*      */         case 1007:
/*      */         case 1015:
/*  643 */           byteArrayOutputStream = new ByteArrayOutputStream();
/*  644 */           byteArrayOutputStream.write(123);
/*  645 */           arrayOfValue = ((ValueArray)paramValue).getList();
/*  646 */           charset = getEncoding();
/*  647 */           for (b1 = 0; b1 < arrayOfValue.length; b1++) {
/*  648 */             if (b1 > 0) {
/*  649 */               byteArrayOutputStream.write(44);
/*      */             }
/*  651 */             String str = arrayOfValue[b1].getString();
/*  652 */             if (SHOULD_QUOTE.matcher(str).matches()) {
/*  653 */               ArrayList<String> arrayList = new ArrayList();
/*  654 */               for (String str1 : str.split("\\\\")) {
/*  655 */                 arrayList.add(str1.replace("\"", "\\\""));
/*      */               }
/*  657 */               str = "\"" + String.join("\\\\", (Iterable)arrayList) + "\"";
/*      */             } 
/*  659 */             byteArrayOutputStream.write(str.getBytes(charset));
/*      */           } 
/*  661 */           byteArrayOutputStream.write(125);
/*  662 */           writeInt(byteArrayOutputStream.size());
/*  663 */           write(byteArrayOutputStream);
/*      */           return;
/*      */       } 
/*  666 */       byte[] arrayOfByte2 = paramValue.getString().getBytes(getEncoding());
/*  667 */       writeInt(arrayOfByte2.length);
/*  668 */       write(arrayOfByte2);
/*      */     } else {
/*      */       byte[] arrayOfByte; ValueTimeTimeZone valueTimeTimeZone; ValueTimestamp valueTimestamp; ValueTimestampTimeZone valueTimestampTimeZone; long l1;
/*      */       long l2;
/*  672 */       switch (paramInt) {
/*      */         case 16:
/*  674 */           writeInt(1);
/*  675 */           this.dataOut.writeByte(paramValue.getBoolean() ? 1 : 0);
/*      */           return;
/*      */         case 21:
/*  678 */           writeInt(2);
/*  679 */           writeShort(paramValue.getShort());
/*      */           return;
/*      */         case 23:
/*  682 */           writeInt(4);
/*  683 */           writeInt(paramValue.getInt());
/*      */           return;
/*      */         case 20:
/*  686 */           writeInt(8);
/*  687 */           this.dataOut.writeLong(paramValue.getLong());
/*      */           return;
/*      */         case 700:
/*  690 */           writeInt(4);
/*  691 */           this.dataOut.writeFloat(paramValue.getFloat());
/*      */           return;
/*      */         case 701:
/*  694 */           writeInt(8);
/*  695 */           this.dataOut.writeDouble(paramValue.getDouble());
/*      */           return;
/*      */         case 1700:
/*  698 */           writeNumericBinary(paramValue.getBigDecimal());
/*      */           return;
/*      */         case 17:
/*  701 */           arrayOfByte = paramValue.getBytesNoCopy();
/*  702 */           writeInt(arrayOfByte.length);
/*  703 */           write(arrayOfByte);
/*      */           return;
/*      */         
/*      */         case 1082:
/*  707 */           writeInt(4);
/*  708 */           writeInt((int)toPostgreDays(((ValueDate)paramValue).getDateValue()));
/*      */           return;
/*      */         case 1083:
/*  711 */           writeTimeBinary(((ValueTime)paramValue).getNanos(), 8);
/*      */           return;
/*      */         case 1266:
/*  714 */           valueTimeTimeZone = (ValueTimeTimeZone)paramValue;
/*  715 */           l1 = valueTimeTimeZone.getNanos();
/*  716 */           writeTimeBinary(l1, 12);
/*  717 */           this.dataOut.writeInt(-valueTimeTimeZone.getTimeZoneOffsetSeconds());
/*      */           return;
/*      */         
/*      */         case 1114:
/*  721 */           valueTimestamp = (ValueTimestamp)paramValue;
/*  722 */           l1 = toPostgreDays(valueTimestamp.getDateValue()) * 86400L;
/*  723 */           l2 = valueTimestamp.getTimeNanos();
/*  724 */           writeTimestampBinary(l1, l2);
/*      */           return;
/*      */         
/*      */         case 1184:
/*  728 */           valueTimestampTimeZone = (ValueTimestampTimeZone)paramValue;
/*  729 */           l1 = toPostgreDays(valueTimestampTimeZone.getDateValue()) * 86400L;
/*  730 */           l2 = valueTimestampTimeZone.getTimeNanos() - valueTimestampTimeZone.getTimeZoneOffsetSeconds() * 1000000000L;
/*  731 */           if (l2 < 0L) {
/*  732 */             l1--;
/*  733 */             l2 += 86400000000000L;
/*      */           } 
/*  735 */           writeTimestampBinary(l1, l2);
/*      */           return;
/*      */       } 
/*  738 */       throw new IllegalStateException("output binary format is undefined");
/*      */     } 
/*      */   }
/*      */ 
/*      */   
/*  743 */   private static final int[] POWERS10 = new int[] { 1, 10, 100, 1000, 10000 };
/*      */   private static final int MAX_GROUP_SCALE = 4;
/*  745 */   private static final int MAX_GROUP_SIZE = POWERS10[4];
/*      */   
/*      */   private static int divide(BigInteger[] paramArrayOfBigInteger, int paramInt) {
/*  748 */     BigInteger[] arrayOfBigInteger = paramArrayOfBigInteger[0].divideAndRemainder(BigInteger.valueOf(paramInt));
/*  749 */     paramArrayOfBigInteger[0] = arrayOfBigInteger[0];
/*  750 */     return arrayOfBigInteger[1].intValue();
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private void writeNumericBinary(BigDecimal paramBigDecimal) throws IOException {
/*  757 */     int i = 0;
/*  758 */     ArrayList<Integer> arrayList = new ArrayList();
/*  759 */     int j = paramBigDecimal.scale();
/*  760 */     int k = paramBigDecimal.signum();
/*  761 */     if (k != 0) {
/*  762 */       BigInteger[] arrayOfBigInteger = { null };
/*  763 */       if (j < 0) {
/*  764 */         arrayOfBigInteger[0] = paramBigDecimal.setScale(0).unscaledValue();
/*  765 */         j = 0;
/*      */       } else {
/*  767 */         arrayOfBigInteger[0] = paramBigDecimal.unscaledValue();
/*      */       } 
/*  769 */       if (k < 0) {
/*  770 */         arrayOfBigInteger[0] = arrayOfBigInteger[0].negate();
/*      */       }
/*  772 */       i = -j / 4 - 1;
/*  773 */       int i1 = 0;
/*  774 */       int i2 = j % 4;
/*  775 */       if (i2 > 0) {
/*  776 */         i1 = divide(arrayOfBigInteger, POWERS10[i2]) * POWERS10[4 - i2];
/*  777 */         if (i1 != 0) {
/*  778 */           i--;
/*      */         }
/*      */       } 
/*  781 */       if (i1 == 0) {
/*  782 */         while ((i1 = divide(arrayOfBigInteger, MAX_GROUP_SIZE)) == 0) {
/*  783 */           i++;
/*      */         }
/*      */       }
/*  786 */       arrayList.add(Integer.valueOf(i1));
/*  787 */       while (arrayOfBigInteger[0].signum() != 0) {
/*  788 */         arrayList.add(Integer.valueOf(divide(arrayOfBigInteger, MAX_GROUP_SIZE)));
/*      */       }
/*      */     } 
/*  791 */     int m = arrayList.size();
/*  792 */     if (m + i > 32767 || j > 32767) {
/*  793 */       throw DbException.get(22003, paramBigDecimal.toString());
/*      */     }
/*  795 */     writeInt(8 + m * 2);
/*  796 */     writeShort(m);
/*  797 */     writeShort(m + i);
/*  798 */     writeShort((k < 0) ? 16384 : 0);
/*  799 */     writeShort(j);
/*  800 */     for (int n = m - 1; n >= 0; n--) {
/*  801 */       writeShort(((Integer)arrayList.get(n)).intValue());
/*      */     }
/*      */   }
/*      */   
/*      */   private void writeTimeBinary(long paramLong, int paramInt) throws IOException {
/*  806 */     writeInt(paramInt);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  812 */     paramLong = Double.doubleToLongBits(paramLong * 1.0E-9D);
/*      */     
/*  814 */     this.dataOut.writeLong(paramLong);
/*      */   }
/*      */   
/*      */   private void writeTimestampBinary(long paramLong1, long paramLong2) throws IOException {
/*  818 */     writeInt(8);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*  824 */     paramLong1 = Double.doubleToLongBits(paramLong1 + paramLong2 * 1.0E-9D);
/*      */     
/*  826 */     this.dataOut.writeLong(paramLong1);
/*      */   }
/*      */   
/*      */   private Charset getEncoding() {
/*  830 */     if ("UNICODE".equals(this.clientEncoding)) {
/*  831 */       return StandardCharsets.UTF_8;
/*      */     }
/*  833 */     return Charset.forName(this.clientEncoding);
/*      */   }
/*      */   
/*      */   private void setParameter(ArrayList<? extends ParameterInterface> paramArrayList, int paramInt1, int paramInt2, int[] paramArrayOfint) throws IOException {
/*      */     Value value;
/*  838 */     boolean bool = true;
/*  839 */     if (paramArrayOfint.length == 1) {
/*  840 */       bool = (paramArrayOfint[0] == 0) ? true : false;
/*  841 */     } else if (paramInt2 < paramArrayOfint.length) {
/*  842 */       bool = (paramArrayOfint[paramInt2] == 0) ? true : false;
/*      */     } 
/*  844 */     int i = readInt();
/*      */     
/*  846 */     if (i == -1) {
/*  847 */       ValueNull valueNull = ValueNull.INSTANCE;
/*  848 */     } else if (bool) {
/*      */       int j;
/*  850 */       byte[] arrayOfByte = Utils.newBytes(i);
/*  851 */       readFully(arrayOfByte);
/*  852 */       String str = new String(arrayOfByte, getEncoding());
/*  853 */       switch (paramInt1) {
/*      */         
/*      */         case 1082:
/*  856 */           j = str.indexOf(' ');
/*  857 */           if (j > 0) {
/*  858 */             str = str.substring(0, j);
/*      */           }
/*      */           break;
/*      */ 
/*      */         
/*      */         case 1083:
/*  864 */           j = str.indexOf('+');
/*  865 */           if (j <= 0) {
/*  866 */             j = str.indexOf('-');
/*      */           }
/*  868 */           if (j > 0) {
/*  869 */             str = str.substring(0, j);
/*      */           }
/*      */           break;
/*      */       } 
/*      */       
/*  874 */       value = ValueVarchar.get(str, (CastDataProvider)this.session);
/*      */     } else {
/*      */       ValueSmallint valueSmallint; ValueInteger valueInteger; ValueBigint valueBigint; ValueReal valueReal; ValueDouble valueDouble; ValueVarbinary valueVarbinary; byte[] arrayOfByte1; byte[] arrayOfByte2;
/*  877 */       switch (paramInt1) {
/*      */         case 21:
/*  879 */           checkParamLength(2, i);
/*  880 */           valueSmallint = ValueSmallint.get(readShort());
/*      */           break;
/*      */         case 23:
/*  883 */           checkParamLength(4, i);
/*  884 */           valueInteger = ValueInteger.get(readInt());
/*      */           break;
/*      */         case 20:
/*  887 */           checkParamLength(8, i);
/*  888 */           valueBigint = ValueBigint.get(this.dataIn.readLong());
/*      */           break;
/*      */         case 700:
/*  891 */           checkParamLength(4, i);
/*  892 */           valueReal = ValueReal.get(this.dataIn.readFloat());
/*      */           break;
/*      */         case 701:
/*  895 */           checkParamLength(8, i);
/*  896 */           valueDouble = ValueDouble.get(this.dataIn.readDouble());
/*      */           break;
/*      */         case 17:
/*  899 */           arrayOfByte1 = Utils.newBytes(i);
/*  900 */           readFully(arrayOfByte1);
/*  901 */           valueVarbinary = ValueVarbinary.getNoCopy(arrayOfByte1);
/*      */           break;
/*      */         default:
/*  904 */           this.server.trace("Binary format for type: " + paramInt1 + " is unsupported");
/*  905 */           arrayOfByte2 = Utils.newBytes(i);
/*  906 */           readFully(arrayOfByte2);
/*  907 */           value = ValueVarchar.get(new String(arrayOfByte2, getEncoding()), (CastDataProvider)this.session); break;
/*      */       } 
/*      */     } 
/*  910 */     ((ParameterInterface)paramArrayList.get(paramInt2)).setValue(value, true);
/*      */   }
/*      */   
/*      */   private static void checkParamLength(int paramInt1, int paramInt2) {
/*  914 */     if (paramInt1 != paramInt2) {
/*  915 */       throw DbException.getInvalidValueException("paramLen", Integer.valueOf(paramInt2));
/*      */     }
/*      */   }
/*      */   
/*      */   private void sendErrorOrCancelResponse(Exception paramException) throws IOException {
/*  920 */     if (paramException instanceof DbException && ((DbException)paramException).getErrorCode() == 57014) {
/*  921 */       sendCancelQueryResponse();
/*      */     } else {
/*  923 */       sendErrorResponse(paramException);
/*      */     } 
/*      */   }
/*      */   
/*      */   private void sendErrorResponse(Exception paramException) throws IOException {
/*  928 */     SQLException sQLException = DbException.toSQLException(paramException);
/*  929 */     this.server.traceError(sQLException);
/*  930 */     startMessage(69);
/*  931 */     write(83);
/*  932 */     writeString("ERROR");
/*  933 */     write(67);
/*  934 */     writeString(sQLException.getSQLState());
/*  935 */     write(77);
/*  936 */     writeString(sQLException.getMessage());
/*  937 */     write(68);
/*  938 */     writeString(sQLException.toString());
/*  939 */     write(0);
/*  940 */     sendMessage();
/*      */   }
/*      */   
/*      */   private void sendCancelQueryResponse() throws IOException {
/*  944 */     this.server.trace("CancelSuccessResponse");
/*  945 */     startMessage(69);
/*  946 */     write(83);
/*  947 */     writeString("ERROR");
/*  948 */     write(67);
/*  949 */     writeString("57014");
/*  950 */     write(77);
/*  951 */     writeString("canceling statement due to user request");
/*  952 */     write(0);
/*  953 */     sendMessage();
/*      */   }
/*      */ 
/*      */   
/*      */   private void sendParameterDescription(ArrayList<? extends ParameterInterface> paramArrayList, int[] paramArrayOfint) throws Exception {
/*  958 */     int i = paramArrayList.size();
/*  959 */     startMessage(116);
/*  960 */     writeShort(i);
/*  961 */     for (byte b = 0; b < i; b++) {
/*      */       char c;
/*  963 */       if (paramArrayOfint != null && paramArrayOfint[b] != 0) {
/*  964 */         c = paramArrayOfint[b];
/*      */       } else {
/*  966 */         c = 'Г';
/*      */       } 
/*  968 */       this.server.checkType(c);
/*  969 */       writeInt(c);
/*      */     } 
/*  971 */     sendMessage();
/*      */   }
/*      */   
/*      */   private void sendNoData() throws IOException {
/*  975 */     startMessage(110);
/*  976 */     sendMessage();
/*      */   }
/*      */   
/*      */   private void sendRowDescription(ResultInterface paramResultInterface, int[] paramArrayOfint) throws IOException {
/*  980 */     if (paramResultInterface == null) {
/*  981 */       sendNoData();
/*      */     } else {
/*  983 */       int i = paramResultInterface.getVisibleColumnCount();
/*  984 */       int[] arrayOfInt1 = new int[i];
/*  985 */       int[] arrayOfInt2 = new int[i];
/*  986 */       int[] arrayOfInt3 = new int[i];
/*  987 */       int[] arrayOfInt4 = new int[i];
/*  988 */       String[] arrayOfString = new String[i];
/*  989 */       Database database = this.session.getDatabase(); byte b;
/*  990 */       for (b = 0; b < i; b++) {
/*  991 */         String str = paramResultInterface.getColumnName(b);
/*  992 */         Schema schema = database.findSchema(paramResultInterface.getSchemaName(b));
/*  993 */         if (schema != null) {
/*  994 */           Table table = schema.findTableOrView(this.session, paramResultInterface.getTableName(b));
/*  995 */           if (table != null) {
/*  996 */             arrayOfInt1[b] = table.getId();
/*  997 */             Column column = table.findColumn(str);
/*  998 */             if (column != null) {
/*  999 */               arrayOfInt2[b] = column.getColumnId() + 1;
/*      */             }
/*      */           } 
/*      */         } 
/* 1003 */         arrayOfString[b] = str;
/* 1004 */         TypeInfo typeInfo = paramResultInterface.getColumnType(b);
/* 1005 */         int j = PgServer.convertType(typeInfo);
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */         
/* 1013 */         arrayOfInt4[b] = typeInfo.getDisplaySize();
/* 1014 */         if (typeInfo.getValueType() != 0) {
/* 1015 */           this.server.checkType(j);
/*      */         }
/* 1017 */         arrayOfInt3[b] = j;
/*      */       } 
/* 1019 */       startMessage(84);
/* 1020 */       writeShort(i);
/* 1021 */       for (b = 0; b < i; b++) {
/* 1022 */         writeString(StringUtils.toLowerEnglish(arrayOfString[b]));
/*      */         
/* 1024 */         writeInt(arrayOfInt1[b]);
/*      */         
/* 1026 */         writeShort(arrayOfInt2[b]);
/*      */         
/* 1028 */         writeInt(arrayOfInt3[b]);
/*      */         
/* 1030 */         writeShort(getTypeSize(arrayOfInt3[b], arrayOfInt4[b]));
/*      */         
/* 1032 */         writeInt(-1);
/*      */         
/* 1034 */         writeShort(formatAsText(arrayOfInt3[b], paramArrayOfint, b) ? 0 : 1);
/*      */       } 
/* 1036 */       sendMessage();
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
/*      */   private static boolean formatAsText(int paramInt1, int[] paramArrayOfint, int paramInt2) {
/* 1049 */     boolean bool = true;
/* 1050 */     if (paramArrayOfint != null && paramArrayOfint.length > 0) {
/* 1051 */       if (paramArrayOfint.length == 1) {
/* 1052 */         bool = (paramArrayOfint[0] == 0) ? true : false;
/* 1053 */       } else if (paramInt2 < paramArrayOfint.length) {
/* 1054 */         bool = (paramArrayOfint[paramInt2] == 0) ? true : false;
/*      */       } 
/*      */     }
/* 1057 */     return bool;
/*      */   }
/*      */   
/*      */   private static int getTypeSize(int paramInt1, int paramInt2) {
/* 1061 */     switch (paramInt1) {
/*      */       case 16:
/* 1063 */         return 1;
/*      */       case 1043:
/* 1065 */         return Math.max(255, paramInt2 + 10);
/*      */     } 
/* 1067 */     return paramInt2 + 4;
/*      */   }
/*      */ 
/*      */   
/*      */   private void sendErrorResponse(String paramString) throws IOException {
/* 1072 */     this.server.trace("Exception: " + paramString);
/* 1073 */     startMessage(69);
/* 1074 */     write(83);
/* 1075 */     writeString("ERROR");
/* 1076 */     write(67);
/*      */     
/* 1078 */     writeString("08P01");
/* 1079 */     write(77);
/* 1080 */     writeString(paramString);
/* 1081 */     sendMessage();
/*      */   }
/*      */   
/*      */   private void sendParseComplete() throws IOException {
/* 1085 */     startMessage(49);
/* 1086 */     sendMessage();
/*      */   }
/*      */   
/*      */   private void sendBindComplete() throws IOException {
/* 1090 */     startMessage(50);
/* 1091 */     sendMessage();
/*      */   }
/*      */   
/*      */   private void sendCloseComplete() throws IOException {
/* 1095 */     startMessage(51);
/* 1096 */     sendMessage();
/*      */   }
/*      */   
/*      */   private void initDb() {
/* 1100 */     this.session.setTimeZone(this.timeZone);
/* 1101 */     try (Command null = this.session.prepareLocal("set search_path = public, pg_catalog")) {
/* 1102 */       command.executeUpdate(null);
/*      */     } 
/* 1104 */     HashSet<Integer> hashSet = this.server.getTypeSet();
/* 1105 */     if (hashSet.isEmpty()) {
/* 1106 */       try(Command null = this.session.prepareLocal("select oid from pg_catalog.pg_type"); 
/* 1107 */           ResultInterface null = command1.executeQuery(0L, false)) {
/* 1108 */         while (resultInterface.next()) {
/* 1109 */           hashSet.add(Integer.valueOf(resultInterface.currentRow()[0].getInt()));
/*      */         }
/*      */       } 
/*      */     }
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   void close() {
/* 1119 */     for (Prepared prepared : this.prepared.values()) {
/* 1120 */       prepared.close();
/*      */     }
/*      */     try {
/* 1123 */       this.stop = true;
/*      */       try {
/* 1125 */         this.session.close();
/* 1126 */       } catch (Exception exception) {}
/*      */ 
/*      */       
/* 1129 */       if (this.socket != null) {
/* 1130 */         this.socket.close();
/*      */       }
/* 1132 */       this.server.trace("Close");
/* 1133 */     } catch (Exception exception) {
/* 1134 */       this.server.traceError(exception);
/*      */     } 
/* 1136 */     this.session = null;
/* 1137 */     this.socket = null;
/* 1138 */     this.server.remove(this);
/*      */   }
/*      */   
/*      */   private void sendAuthenticationCleartextPassword() throws IOException {
/* 1142 */     startMessage(82);
/* 1143 */     writeInt(3);
/* 1144 */     sendMessage();
/*      */   }
/*      */   
/*      */   private void sendAuthenticationOk() throws IOException {
/* 1148 */     startMessage(82);
/* 1149 */     writeInt(0);
/* 1150 */     sendMessage();
/* 1151 */     sendParameterStatus("client_encoding", this.clientEncoding);
/* 1152 */     sendParameterStatus("DateStyle", this.dateStyle);
/* 1153 */     sendParameterStatus("is_superuser", "off");
/* 1154 */     sendParameterStatus("server_encoding", "SQL_ASCII");
/* 1155 */     sendParameterStatus("server_version", "8.2.23");
/* 1156 */     sendParameterStatus("session_authorization", this.userName);
/* 1157 */     sendParameterStatus("standard_conforming_strings", "off");
/* 1158 */     sendParameterStatus("TimeZone", pgTimeZone(this.timeZone.getId()));
/*      */     
/* 1160 */     String str = "off";
/* 1161 */     sendParameterStatus("integer_datetimes", str);
/* 1162 */     sendBackendKeyData();
/* 1163 */     sendReadyForQuery();
/*      */   }
/*      */   
/*      */   private void sendReadyForQuery() throws IOException {
/* 1167 */     startMessage(90);
/* 1168 */     write((byte)(this.session.getAutoCommit() ? 73 : 84));
/* 1169 */     sendMessage();
/*      */   }
/*      */   
/*      */   private void sendBackendKeyData() throws IOException {
/* 1173 */     startMessage(75);
/* 1174 */     writeInt(this.processId);
/* 1175 */     writeInt(this.secret);
/* 1176 */     sendMessage();
/*      */   }
/*      */   
/*      */   private void writeString(String paramString) throws IOException {
/* 1180 */     writeStringPart(paramString);
/* 1181 */     write(0);
/*      */   }
/*      */   
/*      */   private void writeStringPart(String paramString) throws IOException {
/* 1185 */     write(paramString.getBytes(getEncoding()));
/*      */   }
/*      */   
/*      */   private void writeInt(int paramInt) throws IOException {
/* 1189 */     this.dataOut.writeInt(paramInt);
/*      */   }
/*      */   
/*      */   private void writeShort(int paramInt) throws IOException {
/* 1193 */     this.dataOut.writeShort(paramInt);
/*      */   }
/*      */   
/*      */   private void write(byte[] paramArrayOfbyte) throws IOException {
/* 1197 */     this.dataOut.write(paramArrayOfbyte);
/*      */   }
/*      */   
/*      */   private void write(ByteArrayOutputStream paramByteArrayOutputStream) throws IOException {
/* 1201 */     paramByteArrayOutputStream.writeTo(this.dataOut);
/*      */   }
/*      */   
/*      */   private void write(int paramInt) throws IOException {
/* 1205 */     this.dataOut.write(paramInt);
/*      */   }
/*      */   
/*      */   private void startMessage(int paramInt) {
/* 1209 */     this.messageType = paramInt;
/* 1210 */     if (this.outBuffer.size() <= 65536) {
/* 1211 */       this.outBuffer.reset();
/*      */     } else {
/* 1213 */       this.outBuffer = new ByteArrayOutputStream();
/*      */     } 
/* 1215 */     this.dataOut = new DataOutputStream(this.outBuffer);
/*      */   }
/*      */   
/*      */   private void sendMessage() throws IOException {
/* 1219 */     this.dataOut.flush();
/* 1220 */     this.dataOut = new DataOutputStream(this.out);
/* 1221 */     write(this.messageType);
/* 1222 */     writeInt(this.outBuffer.size() + 4);
/* 1223 */     write(this.outBuffer);
/* 1224 */     this.dataOut.flush();
/*      */   }
/*      */ 
/*      */   
/*      */   private void sendParameterStatus(String paramString1, String paramString2) throws IOException {
/* 1229 */     startMessage(83);
/* 1230 */     writeString(paramString1);
/* 1231 */     writeString(paramString2);
/* 1232 */     sendMessage();
/*      */   }
/*      */   
/*      */   void setThread(Thread paramThread) {
/* 1236 */     this.thread = paramThread;
/*      */   }
/*      */   
/*      */   Thread getThread() {
/* 1240 */     return this.thread;
/*      */   }
/*      */   
/*      */   void setProcessId(int paramInt) {
/* 1244 */     this.processId = paramInt;
/*      */   }
/*      */   
/*      */   int getProcessId() {
/* 1248 */     return this.processId;
/*      */   }
/*      */   
/*      */   private synchronized void setActiveRequest(CommandInterface paramCommandInterface) {
/* 1252 */     this.activeRequest = paramCommandInterface;
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   private synchronized void cancelRequest() {
/* 1259 */     if (this.activeRequest != null) {
/* 1260 */       this.activeRequest.cancel();
/* 1261 */       this.activeRequest = null;
/*      */     } 
/*      */   }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */   
/*      */   static class Prepared
/*      */   {
/*      */     String name;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     String sql;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     CommandInterface prep;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     ResultInterface result;
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     int[] paramType;
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     void close() {
/*      */       try {
/* 1300 */         closeResult();
/* 1301 */         this.prep.close();
/* 1302 */       } catch (Exception exception) {}
/*      */     }
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */ 
/*      */     
/*      */     void closeResult() {
/* 1311 */       ResultInterface resultInterface = this.result;
/* 1312 */       if (resultInterface != null) {
/* 1313 */         this.result = null;
/* 1314 */         resultInterface.close();
/*      */       } 
/*      */     }
/*      */   }
/*      */   
/*      */   static class Portal {
/*      */     String name;
/*      */     int[] resultColumnFormat;
/*      */     PgServerThread.Prepared prep;
/*      */   }
/*      */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\server\pg\PgServerThread.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */