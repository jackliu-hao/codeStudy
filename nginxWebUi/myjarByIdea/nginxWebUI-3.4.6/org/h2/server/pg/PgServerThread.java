package org.h2.server.pg;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.Socket;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.regex.Pattern;
import org.h2.command.Command;
import org.h2.command.CommandInterface;
import org.h2.engine.ConnectionInfo;
import org.h2.engine.Database;
import org.h2.engine.Engine;
import org.h2.engine.SessionLocal;
import org.h2.engine.SysProperties;
import org.h2.expression.ParameterInterface;
import org.h2.message.DbException;
import org.h2.result.ResultInterface;
import org.h2.schema.Schema;
import org.h2.table.Column;
import org.h2.table.Table;
import org.h2.util.DateTimeUtils;
import org.h2.util.MathUtils;
import org.h2.util.NetUtils;
import org.h2.util.NetworkConnectionInfo;
import org.h2.util.ScriptReader;
import org.h2.util.StringUtils;
import org.h2.util.TimeZoneProvider;
import org.h2.util.Utils;
import org.h2.util.Utils10;
import org.h2.value.CaseInsensitiveMap;
import org.h2.value.TypeInfo;
import org.h2.value.Value;
import org.h2.value.ValueArray;
import org.h2.value.ValueBigint;
import org.h2.value.ValueDate;
import org.h2.value.ValueDouble;
import org.h2.value.ValueInteger;
import org.h2.value.ValueNull;
import org.h2.value.ValueReal;
import org.h2.value.ValueSmallint;
import org.h2.value.ValueTime;
import org.h2.value.ValueTimeTimeZone;
import org.h2.value.ValueTimestamp;
import org.h2.value.ValueTimestampTimeZone;
import org.h2.value.ValueVarbinary;
import org.h2.value.ValueVarchar;

public final class PgServerThread implements Runnable {
   private static final boolean INTEGER_DATE_TYPES = false;
   private static final Pattern SHOULD_QUOTE = Pattern.compile(".*[\",\\\\{}].*");
   private final PgServer server;
   private Socket socket;
   private SessionLocal session;
   private boolean stop;
   private DataInputStream dataInRaw;
   private DataInputStream dataIn;
   private OutputStream out;
   private int messageType;
   private ByteArrayOutputStream outBuffer = new ByteArrayOutputStream();
   private DataOutputStream dataOut;
   private Thread thread;
   private boolean initDone;
   private String userName;
   private String databaseName;
   private int processId;
   private final int secret;
   private CommandInterface activeRequest;
   private String clientEncoding;
   private String dateStyle;
   private TimeZoneProvider timeZone;
   private final HashMap<String, Prepared> prepared;
   private final HashMap<String, Portal> portals;
   private static final int[] POWERS10 = new int[]{1, 10, 100, 1000, 10000};
   private static final int MAX_GROUP_SCALE = 4;
   private static final int MAX_GROUP_SIZE;

   private static String pgTimeZone(String var0) {
      if (var0.startsWith("GMT+")) {
         return convertTimeZone(var0, "GMT-");
      } else if (var0.startsWith("GMT-")) {
         return convertTimeZone(var0, "GMT+");
      } else if (var0.startsWith("UTC+")) {
         return convertTimeZone(var0, "UTC-");
      } else {
         return var0.startsWith("UTC-") ? convertTimeZone(var0, "UTC+") : var0;
      }
   }

   private static String convertTimeZone(String var0, String var1) {
      int var2 = var0.length();
      return (new StringBuilder(var2)).append(var1).append(var0, 4, var2).toString();
   }

   PgServerThread(Socket var1, PgServer var2) {
      this.clientEncoding = SysProperties.PG_DEFAULT_CLIENT_ENCODING;
      this.dateStyle = "ISO, MDY";
      this.timeZone = DateTimeUtils.getTimeZone();
      this.prepared = new CaseInsensitiveMap();
      this.portals = new CaseInsensitiveMap();
      this.server = var2;
      this.socket = var1;
      this.secret = (int)MathUtils.secureRandomLong();
   }

   public void run() {
      try {
         this.server.trace("Connect");
         InputStream var1 = this.socket.getInputStream();
         this.out = this.socket.getOutputStream();
         this.dataInRaw = new DataInputStream(var1);

         while(!this.stop) {
            this.process();
            this.out.flush();
         }
      } catch (EOFException var6) {
      } catch (Exception var7) {
         this.server.traceError(var7);
      } finally {
         this.server.trace("Disconnect");
         this.close();
      }

   }

   private String readString() throws IOException {
      ByteArrayOutputStream var1 = new ByteArrayOutputStream();

      while(true) {
         int var2 = this.dataIn.read();
         if (var2 <= 0) {
            return Utils10.byteArrayOutputStreamToString(var1, this.getEncoding());
         }

         var1.write(var2);
      }
   }

   private int readInt() throws IOException {
      return this.dataIn.readInt();
   }

   private short readShort() throws IOException {
      return this.dataIn.readShort();
   }

   private byte readByte() throws IOException {
      return this.dataIn.readByte();
   }

   private void readFully(byte[] var1) throws IOException {
      this.dataIn.readFully(var1);
   }

   private void process() throws IOException {
      int var1;
      if (this.initDone) {
         var1 = this.dataInRaw.read();
         if (var1 < 0) {
            this.stop = true;
            return;
         }
      } else {
         var1 = 0;
      }

      int var2 = this.dataInRaw.readInt();
      var2 -= 4;
      byte[] var3 = Utils.newBytes(var2);
      this.dataInRaw.readFully(var3, 0, var2);
      this.dataIn = new DataInputStream(new ByteArrayInputStream(var3, 0, var2));
      String var5;
      String var7;
      int var107;
      int var109;
      String var112;
      Prepared var117;
      Portal var120;
      int var124;
      int var127;
      switch (var1) {
         case 0:
            this.server.trace("Init");
            int var4 = this.readInt();
            if (var4 == 80877102) {
               this.server.trace("CancelRequest");
               var107 = this.readInt();
               var109 = this.readInt();
               PgServerThread var126 = this.server.getThread(var107);
               if (var126 != null && var109 == var126.secret) {
                  var126.cancelRequest();
               } else {
                  this.server.trace("Invalid CancelRequest: pid=" + var107 + ", key=" + var109);
               }

               this.close();
            } else if (var4 == 80877103) {
               this.server.trace("SSLRequest");
               this.out.write(78);
            } else {
               this.server.trace("StartupMessage");
               this.server.trace(" version " + var4 + " (" + (var4 >> 16) + "." + (var4 & 255) + ")");

               while(true) {
                  var5 = this.readString();
                  if (var5.isEmpty()) {
                     this.sendAuthenticationCleartextPassword();
                     this.initDone = true;
                     return;
                  }

                  var112 = this.readString();
                  switch (var5) {
                     case "user":
                        this.userName = var112;
                        break;
                     case "database":
                        this.databaseName = this.server.checkKeyAndGetDatabaseName(var112);
                        break;
                     case "client_encoding":
                        var127 = var112.length();
                        if (var127 >= 2 && var112.charAt(0) == '\'' && var112.charAt(var127 - 1) == '\'') {
                           var112 = var112.substring(1, var127 - 1);
                        }

                        this.clientEncoding = var112;
                        break;
                     case "DateStyle":
                        if (var112.indexOf(44) < 0) {
                           var112 = var112 + ", MDY";
                        }

                        this.dateStyle = var112;
                        break;
                     case "TimeZone":
                        try {
                           this.timeZone = TimeZoneProvider.ofId(pgTimeZone(var112));
                        } catch (Exception var97) {
                           this.server.trace("Unknown TimeZone: " + var112);
                        }
                  }

                  this.server.trace(" param " + var5 + "=" + var112);
               }
            }
            break;
         case 66:
            this.server.trace("Bind");
            Portal var110 = new Portal();
            var110.name = this.readString();
            var112 = this.readString();
            var117 = (Prepared)this.prepared.get(var112);
            if (var117 == null) {
               this.sendErrorResponse("Prepared not found");
            } else {
               var110.prep = var117;
               this.portals.put(var110.name, var110);
               short var123 = this.readShort();
               int[] var129 = new int[var123];

               for(var124 = 0; var124 < var123; ++var124) {
                  var129[var124] = this.readShort();
               }

               short var130 = this.readShort();

               int var12;
               try {
                  ArrayList var132 = var117.prep.getParameters();

                  for(var12 = 0; var12 < var130; ++var12) {
                     this.setParameter(var132, var117.paramType[var12], var12, var129);
                  }
               } catch (Exception var104) {
                  this.sendErrorResponse(var104);
                  break;
               }

               short var133 = this.readShort();
               var110.resultColumnFormat = new int[var133];

               for(var12 = 0; var12 < var133; ++var12) {
                  var110.resultColumnFormat[var12] = this.readShort();
               }

               this.sendBindComplete();
            }
            break;
         case 67:
            var107 = (char)this.readByte();
            var112 = this.readString();
            this.server.trace("Close");
            if (var107 == 83) {
               var117 = (Prepared)this.prepared.remove(var112);
               if (var117 != null) {
                  var117.close();
               }
            } else {
               if (var107 != 80) {
                  this.server.trace("expected S or P, got " + var107);
                  this.sendErrorResponse("expected S or P");
                  break;
               }

               var120 = (Portal)this.portals.remove(var112);
               if (var120 != null) {
                  var120.prep.closeResult();
               }
            }

            this.sendCloseComplete();
            break;
         case 68:
            var107 = (char)this.readByte();
            var112 = this.readString();
            this.server.trace("Describe");
            if (var107 == 83) {
               var117 = (Prepared)this.prepared.get(var112);
               if (var117 == null) {
                  this.sendErrorResponse("Prepared not found: " + var112);
               } else {
                  try {
                     this.sendParameterDescription(var117.prep.getParameters(), var117.paramType);
                     this.sendRowDescription(var117.prep.getMetaData(), (int[])null);
                  } catch (Exception var95) {
                     this.sendErrorResponse(var95);
                  }
               }
            } else if (var107 == 80) {
               var120 = (Portal)this.portals.get(var112);
               if (var120 == null) {
                  this.sendErrorResponse("Portal not found: " + var112);
               } else {
                  CommandInterface var121 = var120.prep.prep;

                  try {
                     this.sendRowDescription(var121.getMetaData(), var120.resultColumnFormat);
                  } catch (Exception var94) {
                     this.sendErrorResponse(var94);
                  }
               }
            } else {
               this.server.trace("expected S or P, got " + var107);
               this.sendErrorResponse("expected S or P");
            }
            break;
         case 69:
            var5 = this.readString();
            this.server.trace("Execute");
            Portal var111 = (Portal)this.portals.get(var5);
            if (var111 == null) {
               this.sendErrorResponse("Portal not found: " + var5);
            } else {
               int var116 = this.readInt();
               Prepared var119 = var111.prep;
               CommandInterface var128 = var119.prep;
               this.server.trace(var119.sql);

               try {
                  this.setActiveRequest(var128);
                  if (var128.isQuery()) {
                     this.executeQuery(var119, var128, var111.resultColumnFormat, var116);
                  } else {
                     this.sendCommandComplete(var128, var128.executeUpdate((Object)null).getUpdateCount());
                  }
               } catch (Exception var92) {
                  this.sendErrorOrCancelResponse(var92);
               } finally {
                  this.setActiveRequest((CommandInterface)null);
               }
            }
            break;
         case 80:
            this.server.trace("Parse");
            Prepared var106 = new Prepared();
            var106.name = this.readString();
            var106.sql = this.getSQL(this.readString());
            var109 = this.readShort();
            int[] var113 = null;
            if (var109 > 0) {
               var113 = new int[var109];

               for(int var115 = 0; var115 < var109; ++var115) {
                  var113[var115] = this.readInt();
               }
            }

            try {
               var106.prep = this.session.prepareLocal(var106.sql);
               ArrayList var118 = var106.prep.getParameters();
               var127 = var118.size();
               var106.paramType = new int[var127];

               for(var124 = 0; var124 < var127; ++var124) {
                  int var131;
                  if (var124 < var109 && var113[var124] != 0) {
                     var131 = var113[var124];
                     this.server.checkType(var131);
                  } else {
                     var131 = PgServer.convertType(((ParameterInterface)var118.get(var124)).getType());
                  }

                  var106.paramType[var124] = var131;
               }

               this.prepared.put(var106.name, var106);
               this.sendParseComplete();
            } catch (Exception var105) {
               this.sendErrorResponse(var105);
            }
            break;
         case 81:
            this.server.trace("Query");
            var5 = this.readString();
            ScriptReader var108 = new ScriptReader(new StringReader(var5));

            while(true) {
               var7 = var108.readStatement();
               if (var7 == null) {
                  break;
               }

               var7 = this.getSQL(var7);

               try {
                  Command var114 = this.session.prepareLocal(var7);
                  Throwable var122 = null;

                  try {
                     this.setActiveRequest(var114);
                     if (var114.isQuery()) {
                        ResultInterface var10 = var114.executeQuery(0L, false);
                        Throwable var11 = null;

                        try {
                           this.sendRowDescription(var10, (int[])null);

                           while(var10.next()) {
                              this.sendDataRow(var10, (int[])null);
                           }

                           this.sendCommandComplete(var114, 0L);
                        } catch (Throwable var98) {
                           var11 = var98;
                           throw var98;
                        } finally {
                           if (var10 != null) {
                              if (var11 != null) {
                                 try {
                                    var10.close();
                                 } catch (Throwable var91) {
                                    var11.addSuppressed(var91);
                                 }
                              } else {
                                 var10.close();
                              }
                           }

                        }
                     } else {
                        this.sendCommandComplete(var114, var114.executeUpdate((Object)null).getUpdateCount());
                     }
                  } catch (Throwable var100) {
                     var122 = var100;
                     throw var100;
                  } finally {
                     if (var114 != null) {
                        if (var122 != null) {
                           try {
                              var114.close();
                           } catch (Throwable var90) {
                              var122.addSuppressed(var90);
                           }
                        } else {
                           var114.close();
                        }
                     }

                  }
               } catch (Exception var102) {
                  this.sendErrorOrCancelResponse(var102);
                  break;
               } finally {
                  this.setActiveRequest((CommandInterface)null);
               }
            }

            this.sendReadyForQuery();
            break;
         case 83:
            this.server.trace("Sync");
            this.sendReadyForQuery();
            break;
         case 88:
            this.server.trace("Terminate");
            this.close();
            break;
         case 112:
            this.server.trace("PasswordMessage");
            var5 = this.readString();

            try {
               Properties var6 = new Properties();
               var6.put("MODE", "PostgreSQL");
               var6.put("DATABASE_TO_LOWER", "TRUE");
               var6.put("DEFAULT_NULL_ORDERING", "HIGH");
               var7 = "jdbc:h2:" + this.databaseName;
               ConnectionInfo var8 = new ConnectionInfo(var7, var6, this.userName, var5);
               String var9 = this.server.getBaseDir();
               if (var9 == null) {
                  var9 = SysProperties.getBaseDir();
               }

               if (var9 != null) {
                  var8.setBaseDir(var9);
               }

               if (this.server.getIfExists()) {
                  var8.setProperty("FORBID_CREATION", "TRUE");
               }

               var8.setNetworkConnectionInfo(new NetworkConnectionInfo(NetUtils.ipToShortForm(new StringBuilder("pg://"), this.socket.getLocalAddress().getAddress(), true).append(':').append(this.socket.getLocalPort()).toString(), this.socket.getInetAddress().getAddress(), this.socket.getPort(), (String)null));
               this.session = Engine.createSession(var8);
               this.initDb();
               this.sendAuthenticationOk();
            } catch (Exception var96) {
               var96.printStackTrace();
               this.stop = true;
            }
            break;
         default:
            this.server.trace("Unsupported: " + var1 + " (" + (char)var1 + ")");
      }

   }

   private void executeQuery(Prepared var1, CommandInterface var2, int[] var3, int var4) throws Exception {
      ResultInterface var5 = var1.result;
      if (var5 == null) {
         var5 = var2.executeQuery(0L, false);
      }

      try {
         if (var4 == 0) {
            while(var5.next()) {
               this.sendDataRow(var5, var3);
            }
         } else {
            while(var4 > 0 && var5.next()) {
               this.sendDataRow(var5, var3);
               --var4;
            }

            if (var5.hasNext()) {
               var1.result = var5;
               this.sendCommandSuspended();
               return;
            }
         }

         var1.closeResult();
         this.sendCommandComplete(var2, 0L);
      } catch (Exception var7) {
         var1.closeResult();
         throw var7;
      }
   }

   private String getSQL(String var1) {
      String var2 = StringUtils.toLowerEnglish(var1);
      if (var2.startsWith("show max_identifier_length")) {
         var1 = "CALL 63";
      } else if (var2.startsWith("set client_encoding to")) {
         var1 = "set DATESTYLE ISO";
      }

      if (this.server.getTrace()) {
         this.server.trace(var1 + ";");
      }

      return var1;
   }

   private void sendCommandComplete(CommandInterface var1, long var2) throws IOException {
      this.startMessage(67);
      switch (var1.getCommandType()) {
         case 57:
         case 66:
            this.writeString("SELECT");
            break;
         case 58:
            this.writeStringPart("DELETE ");
            this.writeString(Long.toString(var2));
            break;
         case 61:
            this.writeStringPart("INSERT 0 ");
            this.writeString(Long.toString(var2));
            break;
         case 68:
            this.writeStringPart("UPDATE ");
            this.writeString(Long.toString(var2));
            break;
         case 83:
            this.writeString("BEGIN");
            break;
         default:
            this.server.trace("check CommandComplete tag for command " + var1);
            this.writeStringPart("UPDATE ");
            this.writeString(Long.toString(var2));
      }

      this.sendMessage();
   }

   private void sendCommandSuspended() throws IOException {
      this.startMessage(115);
      this.sendMessage();
   }

   private void sendDataRow(ResultInterface var1, int[] var2) throws IOException {
      int var3 = var1.getVisibleColumnCount();
      this.startMessage(68);
      this.writeShort(var3);
      Value[] var4 = var1.currentRow();

      for(int var5 = 0; var5 < var3; ++var5) {
         int var6 = PgServer.convertType(var1.getColumnType(var5));
         boolean var7 = formatAsText(var6, var2, var5);
         this.writeDataColumn(var4[var5], var6, var7);
      }

      this.sendMessage();
   }

   private static long toPostgreDays(long var0) {
      return DateTimeUtils.absoluteDayFromDateValue(var0) - 10957L;
   }

   private void writeDataColumn(Value var1, int var2, boolean var3) throws IOException {
      if (var1 == ValueNull.INSTANCE) {
         this.writeInt(-1);
      } else {
         byte[] var14;
         if (var3) {
            int var7;
            byte[] var21;
            switch (var2) {
               case 16:
                  this.writeInt(1);
                  this.dataOut.writeByte(var1.getBoolean() ? 116 : 102);
                  break;
               case 17:
                  var14 = var1.getBytesNoCopy();
                  int var18 = var14.length;
                  int var20 = var18;

                  int var23;
                  for(var7 = 0; var7 < var18; ++var7) {
                     var23 = var14[var7];
                     if (var23 >= 32 && var23 <= 126) {
                        if (var23 == 92) {
                           ++var20;
                        }
                     } else {
                        var20 += 3;
                     }
                  }

                  var21 = new byte[var20];
                  var23 = 0;

                  for(int var24 = 0; var23 < var18; ++var23) {
                     byte var25 = var14[var23];
                     if (var25 >= 32 && var25 <= 126) {
                        if (var25 == 92) {
                           var21[var24++] = 92;
                           var21[var24++] = 92;
                        } else {
                           var21[var24++] = var25;
                        }
                     } else {
                        var21[var24++] = 92;
                        var21[var24++] = (byte)((var25 >>> 6 & 3) + 48);
                        var21[var24++] = (byte)((var25 >>> 3 & 7) + 48);
                        var21[var24++] = (byte)((var25 & 7) + 48);
                     }
                  }

                  this.writeInt(var21.length);
                  this.write(var21);
                  break;
               case 1005:
               case 1007:
               case 1015:
                  ByteArrayOutputStream var4 = new ByteArrayOutputStream();
                  var4.write(123);
                  Value[] var5 = ((ValueArray)var1).getList();
                  Charset var6 = this.getEncoding();

                  for(var7 = 0; var7 < var5.length; ++var7) {
                     if (var7 > 0) {
                        var4.write(44);
                     }

                     String var8 = var5[var7].getString();
                     if (SHOULD_QUOTE.matcher(var8).matches()) {
                        ArrayList var9 = new ArrayList();
                        String[] var10 = var8.split("\\\\");
                        int var11 = var10.length;

                        for(int var12 = 0; var12 < var11; ++var12) {
                           String var13 = var10[var12];
                           var9.add(var13.replace("\"", "\\\""));
                        }

                        var8 = "\"" + String.join("\\\\", var9) + "\"";
                     }

                     var4.write(var8.getBytes(var6));
                  }

                  var4.write(125);
                  this.writeInt(var4.size());
                  this.write(var4);
                  break;
               default:
                  var21 = var1.getString().getBytes(this.getEncoding());
                  this.writeInt(var21.length);
                  this.write(var21);
            }
         } else {
            long var19;
            long var22;
            switch (var2) {
               case 16:
                  this.writeInt(1);
                  this.dataOut.writeByte(var1.getBoolean() ? 1 : 0);
                  break;
               case 17:
                  var14 = var1.getBytesNoCopy();
                  this.writeInt(var14.length);
                  this.write(var14);
                  break;
               case 20:
                  this.writeInt(8);
                  this.dataOut.writeLong(var1.getLong());
                  break;
               case 21:
                  this.writeInt(2);
                  this.writeShort(var1.getShort());
                  break;
               case 23:
                  this.writeInt(4);
                  this.writeInt(var1.getInt());
                  break;
               case 700:
                  this.writeInt(4);
                  this.dataOut.writeFloat(var1.getFloat());
                  break;
               case 701:
                  this.writeInt(8);
                  this.dataOut.writeDouble(var1.getDouble());
                  break;
               case 1082:
                  this.writeInt(4);
                  this.writeInt((int)toPostgreDays(((ValueDate)var1).getDateValue()));
                  break;
               case 1083:
                  this.writeTimeBinary(((ValueTime)var1).getNanos(), 8);
                  break;
               case 1114:
                  ValueTimestamp var17 = (ValueTimestamp)var1;
                  var19 = toPostgreDays(var17.getDateValue()) * 86400L;
                  var22 = var17.getTimeNanos();
                  this.writeTimestampBinary(var19, var22);
                  break;
               case 1184:
                  ValueTimestampTimeZone var16 = (ValueTimestampTimeZone)var1;
                  var19 = toPostgreDays(var16.getDateValue()) * 86400L;
                  var22 = var16.getTimeNanos() - (long)var16.getTimeZoneOffsetSeconds() * 1000000000L;
                  if (var22 < 0L) {
                     --var19;
                     var22 += 86400000000000L;
                  }

                  this.writeTimestampBinary(var19, var22);
                  break;
               case 1266:
                  ValueTimeTimeZone var15 = (ValueTimeTimeZone)var1;
                  var19 = var15.getNanos();
                  this.writeTimeBinary(var19, 12);
                  this.dataOut.writeInt(-var15.getTimeZoneOffsetSeconds());
                  break;
               case 1700:
                  this.writeNumericBinary(var1.getBigDecimal());
                  break;
               default:
                  throw new IllegalStateException("output binary format is undefined");
            }
         }

      }
   }

   private static int divide(BigInteger[] var0, int var1) {
      BigInteger[] var2 = var0[0].divideAndRemainder(BigInteger.valueOf((long)var1));
      var0[0] = var2[0];
      return var2[1].intValue();
   }

   private void writeNumericBinary(BigDecimal var1) throws IOException {
      int var2 = 0;
      ArrayList var3 = new ArrayList();
      int var4 = var1.scale();
      int var5 = var1.signum();
      int var7;
      if (var5 != 0) {
         BigInteger[] var6 = new BigInteger[]{null};
         if (var4 < 0) {
            var6[0] = var1.setScale(0).unscaledValue();
            var4 = 0;
         } else {
            var6[0] = var1.unscaledValue();
         }

         if (var5 < 0) {
            var6[0] = var6[0].negate();
         }

         var2 = -var4 / 4 - 1;
         var7 = 0;
         int var8 = var4 % 4;
         if (var8 > 0) {
            var7 = divide(var6, POWERS10[var8]) * POWERS10[4 - var8];
            if (var7 != 0) {
               --var2;
            }
         }

         if (var7 == 0) {
            while((var7 = divide(var6, MAX_GROUP_SIZE)) == 0) {
               ++var2;
            }
         }

         var3.add(var7);

         while(var6[0].signum() != 0) {
            var3.add(divide(var6, MAX_GROUP_SIZE));
         }
      }

      int var9 = var3.size();
      if (var9 + var2 <= 32767 && var4 <= 32767) {
         this.writeInt(8 + var9 * 2);
         this.writeShort(var9);
         this.writeShort(var9 + var2);
         this.writeShort(var5 < 0 ? 16384 : 0);
         this.writeShort(var4);

         for(var7 = var9 - 1; var7 >= 0; --var7) {
            this.writeShort((Integer)var3.get(var7));
         }

      } else {
         throw DbException.get(22003, (String)var1.toString());
      }
   }

   private void writeTimeBinary(long var1, int var3) throws IOException {
      this.writeInt(var3);
      var1 = Double.doubleToLongBits((double)var1 * 1.0E-9);
      this.dataOut.writeLong(var1);
   }

   private void writeTimestampBinary(long var1, long var3) throws IOException {
      this.writeInt(8);
      var1 = Double.doubleToLongBits((double)var1 + (double)var3 * 1.0E-9);
      this.dataOut.writeLong(var1);
   }

   private Charset getEncoding() {
      return "UNICODE".equals(this.clientEncoding) ? StandardCharsets.UTF_8 : Charset.forName(this.clientEncoding);
   }

   private void setParameter(ArrayList<? extends ParameterInterface> var1, int var2, int var3, int[] var4) throws IOException {
      boolean var5 = true;
      if (var4.length == 1) {
         var5 = var4[0] == 0;
      } else if (var3 < var4.length) {
         var5 = var4[var3] == 0;
      }

      int var6 = this.readInt();
      Object var7;
      if (var6 == -1) {
         var7 = ValueNull.INSTANCE;
      } else {
         byte[] var8;
         if (var5) {
            var8 = Utils.newBytes(var6);
            this.readFully(var8);
            String var9 = new String(var8, this.getEncoding());
            int var10;
            switch (var2) {
               case 1082:
                  var10 = var9.indexOf(32);
                  if (var10 > 0) {
                     var9 = var9.substring(0, var10);
                  }
                  break;
               case 1083:
                  var10 = var9.indexOf(43);
                  if (var10 <= 0) {
                     var10 = var9.indexOf(45);
                  }

                  if (var10 > 0) {
                     var9 = var9.substring(0, var10);
                  }
            }

            var7 = ValueVarchar.get(var9, this.session);
         } else {
            switch (var2) {
               case 17:
                  var8 = Utils.newBytes(var6);
                  this.readFully(var8);
                  var7 = ValueVarbinary.getNoCopy(var8);
                  break;
               case 20:
                  checkParamLength(8, var6);
                  var7 = ValueBigint.get(this.dataIn.readLong());
                  break;
               case 21:
                  checkParamLength(2, var6);
                  var7 = ValueSmallint.get(this.readShort());
                  break;
               case 23:
                  checkParamLength(4, var6);
                  var7 = ValueInteger.get(this.readInt());
                  break;
               case 700:
                  checkParamLength(4, var6);
                  var7 = ValueReal.get(this.dataIn.readFloat());
                  break;
               case 701:
                  checkParamLength(8, var6);
                  var7 = ValueDouble.get(this.dataIn.readDouble());
                  break;
               default:
                  this.server.trace("Binary format for type: " + var2 + " is unsupported");
                  byte[] var11 = Utils.newBytes(var6);
                  this.readFully(var11);
                  var7 = ValueVarchar.get(new String(var11, this.getEncoding()), this.session);
            }
         }
      }

      ((ParameterInterface)var1.get(var3)).setValue((Value)var7, true);
   }

   private static void checkParamLength(int var0, int var1) {
      if (var0 != var1) {
         throw DbException.getInvalidValueException("paramLen", var1);
      }
   }

   private void sendErrorOrCancelResponse(Exception var1) throws IOException {
      if (var1 instanceof DbException && ((DbException)var1).getErrorCode() == 57014) {
         this.sendCancelQueryResponse();
      } else {
         this.sendErrorResponse(var1);
      }

   }

   private void sendErrorResponse(Exception var1) throws IOException {
      SQLException var2 = DbException.toSQLException(var1);
      this.server.traceError(var2);
      this.startMessage(69);
      this.write(83);
      this.writeString("ERROR");
      this.write(67);
      this.writeString(var2.getSQLState());
      this.write(77);
      this.writeString(var2.getMessage());
      this.write(68);
      this.writeString(var2.toString());
      this.write(0);
      this.sendMessage();
   }

   private void sendCancelQueryResponse() throws IOException {
      this.server.trace("CancelSuccessResponse");
      this.startMessage(69);
      this.write(83);
      this.writeString("ERROR");
      this.write(67);
      this.writeString("57014");
      this.write(77);
      this.writeString("canceling statement due to user request");
      this.write(0);
      this.sendMessage();
   }

   private void sendParameterDescription(ArrayList<? extends ParameterInterface> var1, int[] var2) throws Exception {
      int var3 = var1.size();
      this.startMessage(116);
      this.writeShort(var3);

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5;
         if (var2 != null && var2[var4] != 0) {
            var5 = var2[var4];
         } else {
            var5 = 1043;
         }

         this.server.checkType(var5);
         this.writeInt(var5);
      }

      this.sendMessage();
   }

   private void sendNoData() throws IOException {
      this.startMessage(110);
      this.sendMessage();
   }

   private void sendRowDescription(ResultInterface var1, int[] var2) throws IOException {
      if (var1 == null) {
         this.sendNoData();
      } else {
         int var3 = var1.getVisibleColumnCount();
         int[] var4 = new int[var3];
         int[] var5 = new int[var3];
         int[] var6 = new int[var3];
         int[] var7 = new int[var3];
         String[] var8 = new String[var3];
         Database var9 = this.session.getDatabase();

         int var10;
         for(var10 = 0; var10 < var3; ++var10) {
            String var11 = var1.getColumnName(var10);
            Schema var12 = var9.findSchema(var1.getSchemaName(var10));
            if (var12 != null) {
               Table var13 = var12.findTableOrView(this.session, var1.getTableName(var10));
               if (var13 != null) {
                  var4[var10] = var13.getId();
                  Column var14 = var13.findColumn(var11);
                  if (var14 != null) {
                     var5[var10] = var14.getColumnId() + 1;
                  }
               }
            }

            var8[var10] = var11;
            TypeInfo var15 = var1.getColumnType(var10);
            int var16 = PgServer.convertType(var15);
            var7[var10] = var15.getDisplaySize();
            if (var15.getValueType() != 0) {
               this.server.checkType(var16);
            }

            var6[var10] = var16;
         }

         this.startMessage(84);
         this.writeShort(var3);

         for(var10 = 0; var10 < var3; ++var10) {
            this.writeString(StringUtils.toLowerEnglish(var8[var10]));
            this.writeInt(var4[var10]);
            this.writeShort(var5[var10]);
            this.writeInt(var6[var10]);
            this.writeShort(getTypeSize(var6[var10], var7[var10]));
            this.writeInt(-1);
            this.writeShort(formatAsText(var6[var10], var2, var10) ? 0 : 1);
         }

         this.sendMessage();
      }

   }

   private static boolean formatAsText(int var0, int[] var1, int var2) {
      boolean var3 = true;
      if (var1 != null && var1.length > 0) {
         if (var1.length == 1) {
            var3 = var1[0] == 0;
         } else if (var2 < var1.length) {
            var3 = var1[var2] == 0;
         }
      }

      return var3;
   }

   private static int getTypeSize(int var0, int var1) {
      switch (var0) {
         case 16:
            return 1;
         case 1043:
            return Math.max(255, var1 + 10);
         default:
            return var1 + 4;
      }
   }

   private void sendErrorResponse(String var1) throws IOException {
      this.server.trace("Exception: " + var1);
      this.startMessage(69);
      this.write(83);
      this.writeString("ERROR");
      this.write(67);
      this.writeString("08P01");
      this.write(77);
      this.writeString(var1);
      this.sendMessage();
   }

   private void sendParseComplete() throws IOException {
      this.startMessage(49);
      this.sendMessage();
   }

   private void sendBindComplete() throws IOException {
      this.startMessage(50);
      this.sendMessage();
   }

   private void sendCloseComplete() throws IOException {
      this.startMessage(51);
      this.sendMessage();
   }

   private void initDb() {
      this.session.setTimeZone(this.timeZone);
      Command var1 = this.session.prepareLocal("set search_path = public, pg_catalog");
      Throwable var2 = null;

      try {
         var1.executeUpdate((Object)null);
      } catch (Throwable var47) {
         var2 = var47;
         throw var47;
      } finally {
         if (var1 != null) {
            if (var2 != null) {
               try {
                  var1.close();
               } catch (Throwable var46) {
                  var2.addSuppressed(var46);
               }
            } else {
               var1.close();
            }
         }

      }

      HashSet var53 = this.server.getTypeSet();
      if (var53.isEmpty()) {
         Command var54 = this.session.prepareLocal("select oid from pg_catalog.pg_type");
         Throwable var3 = null;

         try {
            ResultInterface var4 = var54.executeQuery(0L, false);
            Throwable var5 = null;

            try {
               while(var4.next()) {
                  var53.add(var4.currentRow()[0].getInt());
               }
            } catch (Throwable var49) {
               var5 = var49;
               throw var49;
            } finally {
               if (var4 != null) {
                  if (var5 != null) {
                     try {
                        var4.close();
                     } catch (Throwable var45) {
                        var5.addSuppressed(var45);
                     }
                  } else {
                     var4.close();
                  }
               }

            }
         } catch (Throwable var51) {
            var3 = var51;
            throw var51;
         } finally {
            if (var54 != null) {
               if (var3 != null) {
                  try {
                     var54.close();
                  } catch (Throwable var44) {
                     var3.addSuppressed(var44);
                  }
               } else {
                  var54.close();
               }
            }

         }
      }

   }

   void close() {
      Iterator var1 = this.prepared.values().iterator();

      while(var1.hasNext()) {
         Prepared var2 = (Prepared)var1.next();
         var2.close();
      }

      try {
         this.stop = true;

         try {
            this.session.close();
         } catch (Exception var3) {
         }

         if (this.socket != null) {
            this.socket.close();
         }

         this.server.trace("Close");
      } catch (Exception var4) {
         this.server.traceError(var4);
      }

      this.session = null;
      this.socket = null;
      this.server.remove(this);
   }

   private void sendAuthenticationCleartextPassword() throws IOException {
      this.startMessage(82);
      this.writeInt(3);
      this.sendMessage();
   }

   private void sendAuthenticationOk() throws IOException {
      this.startMessage(82);
      this.writeInt(0);
      this.sendMessage();
      this.sendParameterStatus("client_encoding", this.clientEncoding);
      this.sendParameterStatus("DateStyle", this.dateStyle);
      this.sendParameterStatus("is_superuser", "off");
      this.sendParameterStatus("server_encoding", "SQL_ASCII");
      this.sendParameterStatus("server_version", "8.2.23");
      this.sendParameterStatus("session_authorization", this.userName);
      this.sendParameterStatus("standard_conforming_strings", "off");
      this.sendParameterStatus("TimeZone", pgTimeZone(this.timeZone.getId()));
      String var1 = "off";
      this.sendParameterStatus("integer_datetimes", var1);
      this.sendBackendKeyData();
      this.sendReadyForQuery();
   }

   private void sendReadyForQuery() throws IOException {
      this.startMessage(90);
      this.write((byte)(this.session.getAutoCommit() ? 73 : 84));
      this.sendMessage();
   }

   private void sendBackendKeyData() throws IOException {
      this.startMessage(75);
      this.writeInt(this.processId);
      this.writeInt(this.secret);
      this.sendMessage();
   }

   private void writeString(String var1) throws IOException {
      this.writeStringPart(var1);
      this.write(0);
   }

   private void writeStringPart(String var1) throws IOException {
      this.write(var1.getBytes(this.getEncoding()));
   }

   private void writeInt(int var1) throws IOException {
      this.dataOut.writeInt(var1);
   }

   private void writeShort(int var1) throws IOException {
      this.dataOut.writeShort(var1);
   }

   private void write(byte[] var1) throws IOException {
      this.dataOut.write(var1);
   }

   private void write(ByteArrayOutputStream var1) throws IOException {
      var1.writeTo(this.dataOut);
   }

   private void write(int var1) throws IOException {
      this.dataOut.write(var1);
   }

   private void startMessage(int var1) {
      this.messageType = var1;
      if (this.outBuffer.size() <= 65536) {
         this.outBuffer.reset();
      } else {
         this.outBuffer = new ByteArrayOutputStream();
      }

      this.dataOut = new DataOutputStream(this.outBuffer);
   }

   private void sendMessage() throws IOException {
      this.dataOut.flush();
      this.dataOut = new DataOutputStream(this.out);
      this.write(this.messageType);
      this.writeInt(this.outBuffer.size() + 4);
      this.write(this.outBuffer);
      this.dataOut.flush();
   }

   private void sendParameterStatus(String var1, String var2) throws IOException {
      this.startMessage(83);
      this.writeString(var1);
      this.writeString(var2);
      this.sendMessage();
   }

   void setThread(Thread var1) {
      this.thread = var1;
   }

   Thread getThread() {
      return this.thread;
   }

   void setProcessId(int var1) {
      this.processId = var1;
   }

   int getProcessId() {
      return this.processId;
   }

   private synchronized void setActiveRequest(CommandInterface var1) {
      this.activeRequest = var1;
   }

   private synchronized void cancelRequest() {
      if (this.activeRequest != null) {
         this.activeRequest.cancel();
         this.activeRequest = null;
      }

   }

   static {
      MAX_GROUP_SIZE = POWERS10[4];
   }

   static class Portal {
      String name;
      int[] resultColumnFormat;
      Prepared prep;
   }

   static class Prepared {
      String name;
      String sql;
      CommandInterface prep;
      ResultInterface result;
      int[] paramType;

      void close() {
         try {
            this.closeResult();
            this.prep.close();
         } catch (Exception var2) {
         }

      }

      void closeResult() {
         ResultInterface var1 = this.result;
         if (var1 != null) {
            this.result = null;
            var1.close();
         }

      }
   }
}
