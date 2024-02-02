/*     */ package com.mysql.cj.log;
/*     */ 
/*     */ import com.mysql.cj.util.LogUtils;
/*     */ import com.mysql.cj.util.StringUtils;
/*     */ import java.util.Date;
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
/*     */ public class ProfilerEventImpl
/*     */   implements ProfilerEvent
/*     */ {
/*     */   private byte eventType;
/*     */   private String hostName;
/*     */   private String database;
/*     */   private long connectionId;
/*     */   private int statementId;
/*     */   private int resultSetId;
/*     */   private long eventCreationTime;
/*     */   private long eventDuration;
/*     */   private String durationUnits;
/*     */   private String eventCreationPointDesc;
/*     */   private String message;
/*     */   
/*     */   public ProfilerEventImpl(byte eventType, String hostName, String db, long connectionId, int statementId, int resultSetId, long eventDuration, String durationUnits, Throwable eventCreationPoint, String message) {
/*  77 */     this(eventType, hostName, db, connectionId, statementId, resultSetId, System.currentTimeMillis(), eventDuration, durationUnits, 
/*  78 */         LogUtils.findCallingClassAndMethod(eventCreationPoint), message);
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private ProfilerEventImpl(byte eventType, String hostName, String db, long connectionId, int statementId, int resultSetId, long eventCreationTime, long eventDuration, String durationUnits, String eventCreationPointDesc, String message) {
/*  84 */     this.eventType = eventType;
/*  85 */     this.hostName = (hostName == null) ? "" : hostName;
/*  86 */     this.database = (db == null) ? "" : db;
/*  87 */     this.connectionId = connectionId;
/*  88 */     this.statementId = statementId;
/*  89 */     this.resultSetId = resultSetId;
/*  90 */     this.eventCreationTime = eventCreationTime;
/*  91 */     this.eventDuration = eventDuration;
/*  92 */     this.durationUnits = (durationUnits == null) ? "" : durationUnits;
/*  93 */     this.eventCreationPointDesc = (eventCreationPointDesc == null) ? "" : eventCreationPointDesc;
/*  94 */     this.message = (message == null) ? "" : message;
/*     */   }
/*     */ 
/*     */   
/*     */   public byte getEventType() {
/*  99 */     return this.eventType;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getHostName() {
/* 104 */     return this.hostName;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDatabase() {
/* 109 */     return this.database;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getConnectionId() {
/* 114 */     return this.connectionId;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getStatementId() {
/* 119 */     return this.statementId;
/*     */   }
/*     */ 
/*     */   
/*     */   public int getResultSetId() {
/* 124 */     return this.resultSetId;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getEventCreationTime() {
/* 129 */     return this.eventCreationTime;
/*     */   }
/*     */ 
/*     */   
/*     */   public long getEventDuration() {
/* 134 */     return this.eventDuration;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getDurationUnits() {
/* 139 */     return this.durationUnits;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getEventCreationPointAsString() {
/* 144 */     return this.eventCreationPointDesc;
/*     */   }
/*     */ 
/*     */   
/*     */   public String getMessage() {
/* 149 */     return this.message;
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public String toString() {
/* 159 */     StringBuilder buf = new StringBuilder();
/* 160 */     buf.append("[");
/*     */     
/* 162 */     switch (getEventType())
/*     */     { case 4:
/* 164 */         buf.append("EXECUTE");
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
/* 187 */         buf.append("] ");
/*     */         
/* 189 */         buf.append(this.message);
/*     */         
/* 191 */         buf.append(" [Created on: ");
/* 192 */         buf.append(new Date(this.eventCreationTime));
/* 193 */         buf.append(", duration: ");
/* 194 */         buf.append(this.eventDuration);
/* 195 */         buf.append(", connection-id: ");
/* 196 */         buf.append(this.connectionId);
/* 197 */         buf.append(", statement-id: ");
/* 198 */         buf.append(this.statementId);
/* 199 */         buf.append(", resultset-id: ");
/* 200 */         buf.append(this.resultSetId);
/* 201 */         buf.append(",");
/* 202 */         buf.append(this.eventCreationPointDesc);
/* 203 */         buf.append("]");
/*     */         
/* 205 */         return buf.toString();case 5: buf.append("FETCH"); buf.append("] "); buf.append(this.message); buf.append(" [Created on: "); buf.append(new Date(this.eventCreationTime)); buf.append(", duration: "); buf.append(this.eventDuration); buf.append(", connection-id: "); buf.append(this.connectionId); buf.append(", statement-id: "); buf.append(this.statementId); buf.append(", resultset-id: "); buf.append(this.resultSetId); buf.append(","); buf.append(this.eventCreationPointDesc); buf.append("]"); return buf.toString();case 1: buf.append("CONSTRUCT"); buf.append("] "); buf.append(this.message); buf.append(" [Created on: "); buf.append(new Date(this.eventCreationTime)); buf.append(", duration: "); buf.append(this.eventDuration); buf.append(", connection-id: "); buf.append(this.connectionId); buf.append(", statement-id: "); buf.append(this.statementId); buf.append(", resultset-id: "); buf.append(this.resultSetId); buf.append(","); buf.append(this.eventCreationPointDesc); buf.append("]"); return buf.toString();case 2: buf.append("PREPARE"); buf.append("] "); buf.append(this.message); buf.append(" [Created on: "); buf.append(new Date(this.eventCreationTime)); buf.append(", duration: "); buf.append(this.eventDuration); buf.append(", connection-id: "); buf.append(this.connectionId); buf.append(", statement-id: "); buf.append(this.statementId); buf.append(", resultset-id: "); buf.append(this.resultSetId); buf.append(","); buf.append(this.eventCreationPointDesc); buf.append("]"); return buf.toString();case 3: buf.append("QUERY"); buf.append("] "); buf.append(this.message); buf.append(" [Created on: "); buf.append(new Date(this.eventCreationTime)); buf.append(", duration: "); buf.append(this.eventDuration); buf.append(", connection-id: "); buf.append(this.connectionId); buf.append(", statement-id: "); buf.append(this.statementId); buf.append(", resultset-id: "); buf.append(this.resultSetId); buf.append(","); buf.append(this.eventCreationPointDesc); buf.append("]"); return buf.toString();case 0: buf.append("USAGE ADVISOR"); buf.append("] "); buf.append(this.message); buf.append(" [Created on: "); buf.append(new Date(this.eventCreationTime)); buf.append(", duration: "); buf.append(this.eventDuration); buf.append(", connection-id: "); buf.append(this.connectionId); buf.append(", statement-id: "); buf.append(this.statementId); buf.append(", resultset-id: "); buf.append(this.resultSetId); buf.append(","); buf.append(this.eventCreationPointDesc); buf.append("]"); return buf.toString();case 6: buf.append("SLOW QUERY"); buf.append("] "); buf.append(this.message); buf.append(" [Created on: "); buf.append(new Date(this.eventCreationTime)); buf.append(", duration: "); buf.append(this.eventDuration); buf.append(", connection-id: "); buf.append(this.connectionId); buf.append(", statement-id: "); buf.append(this.statementId); buf.append(", resultset-id: "); buf.append(this.resultSetId); buf.append(","); buf.append(this.eventCreationPointDesc); buf.append("]"); return buf.toString(); }  buf.append("UNKNOWN"); buf.append("] "); buf.append(this.message); buf.append(" [Created on: "); buf.append(new Date(this.eventCreationTime)); buf.append(", duration: "); buf.append(this.eventDuration); buf.append(", connection-id: "); buf.append(this.connectionId); buf.append(", statement-id: "); buf.append(this.statementId); buf.append(", resultset-id: "); buf.append(this.resultSetId); buf.append(","); buf.append(this.eventCreationPointDesc); buf.append("]"); return buf.toString();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static ProfilerEvent unpack(byte[] buf) {
/* 216 */     int pos = 0;
/*     */     
/* 218 */     byte eventType = buf[pos++];
/*     */     
/* 220 */     byte[] host = readBytes(buf, pos);
/* 221 */     pos += 4 + host.length;
/*     */     
/* 223 */     byte[] db = readBytes(buf, pos);
/* 224 */     pos += 4 + db.length;
/*     */     
/* 226 */     long connectionId = readLong(buf, pos);
/* 227 */     pos += 8;
/* 228 */     int statementId = readInt(buf, pos);
/* 229 */     pos += 4;
/* 230 */     int resultSetId = readInt(buf, pos);
/* 231 */     pos += 4;
/* 232 */     long eventCreationTime = readLong(buf, pos);
/* 233 */     pos += 8;
/* 234 */     long eventDuration = readLong(buf, pos);
/* 235 */     pos += 8;
/*     */     
/* 237 */     byte[] eventDurationUnits = readBytes(buf, pos);
/* 238 */     pos += 4 + eventDurationUnits.length;
/*     */     
/* 240 */     byte[] eventCreationAsBytes = readBytes(buf, pos);
/* 241 */     pos += 4 + eventCreationAsBytes.length;
/*     */     
/* 243 */     byte[] message = readBytes(buf, pos);
/* 244 */     pos += 4 + message.length;
/*     */ 
/*     */     
/* 247 */     return new ProfilerEventImpl(eventType, StringUtils.toString(host, "ISO8859_1"), StringUtils.toString(db, "ISO8859_1"), connectionId, statementId, resultSetId, eventCreationTime, eventDuration, 
/* 248 */         StringUtils.toString(eventDurationUnits, "ISO8859_1"), 
/* 249 */         StringUtils.toString(eventCreationAsBytes, "ISO8859_1"), StringUtils.toString(message, "ISO8859_1"));
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public byte[] pack() {
/* 255 */     byte[] hostNameAsBytes = StringUtils.getBytes(this.hostName, "ISO8859_1");
/* 256 */     byte[] dbAsBytes = StringUtils.getBytes(this.database, "ISO8859_1");
/* 257 */     byte[] durationUnitsAsBytes = StringUtils.getBytes(this.durationUnits, "ISO8859_1");
/* 258 */     byte[] eventCreationAsBytes = StringUtils.getBytes(this.eventCreationPointDesc, "ISO8859_1");
/* 259 */     byte[] messageAsBytes = StringUtils.getBytes(this.message, "ISO8859_1");
/*     */     
/* 261 */     int len = 1 + 4 + hostNameAsBytes.length + 4 + dbAsBytes.length + 8 + 4 + 4 + 8 + 8 + 4 + durationUnitsAsBytes.length + 4 + eventCreationAsBytes.length + 4 + messageAsBytes.length;
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 266 */     byte[] buf = new byte[len];
/* 267 */     int pos = 0;
/* 268 */     buf[pos++] = this.eventType;
/* 269 */     pos = writeBytes(hostNameAsBytes, buf, pos);
/* 270 */     pos = writeBytes(dbAsBytes, buf, pos);
/* 271 */     pos = writeLong(this.connectionId, buf, pos);
/* 272 */     pos = writeInt(this.statementId, buf, pos);
/* 273 */     pos = writeInt(this.resultSetId, buf, pos);
/* 274 */     pos = writeLong(this.eventCreationTime, buf, pos);
/* 275 */     pos = writeLong(this.eventDuration, buf, pos);
/* 276 */     pos = writeBytes(durationUnitsAsBytes, buf, pos);
/* 277 */     pos = writeBytes(eventCreationAsBytes, buf, pos);
/* 278 */     pos = writeBytes(messageAsBytes, buf, pos);
/*     */     
/* 280 */     return buf;
/*     */   }
/*     */   
/*     */   private static int writeInt(int i, byte[] buf, int pos) {
/* 284 */     buf[pos++] = (byte)(i & 0xFF);
/* 285 */     buf[pos++] = (byte)(i >>> 8);
/* 286 */     buf[pos++] = (byte)(i >>> 16);
/* 287 */     buf[pos++] = (byte)(i >>> 24);
/* 288 */     return pos;
/*     */   }
/*     */   
/*     */   private static int writeLong(long l, byte[] buf, int pos) {
/* 292 */     buf[pos++] = (byte)(int)(l & 0xFFL);
/* 293 */     buf[pos++] = (byte)(int)(l >>> 8L);
/* 294 */     buf[pos++] = (byte)(int)(l >>> 16L);
/* 295 */     buf[pos++] = (byte)(int)(l >>> 24L);
/* 296 */     buf[pos++] = (byte)(int)(l >>> 32L);
/* 297 */     buf[pos++] = (byte)(int)(l >>> 40L);
/* 298 */     buf[pos++] = (byte)(int)(l >>> 48L);
/* 299 */     buf[pos++] = (byte)(int)(l >>> 56L);
/* 300 */     return pos;
/*     */   }
/*     */   
/*     */   private static int writeBytes(byte[] msg, byte[] buf, int pos) {
/* 304 */     pos = writeInt(msg.length, buf, pos);
/* 305 */     System.arraycopy(msg, 0, buf, pos, msg.length);
/* 306 */     return pos + msg.length;
/*     */   }
/*     */   
/*     */   private static int readInt(byte[] buf, int pos) {
/* 310 */     return buf[pos++] & 0xFF | (buf[pos++] & 0xFF) << 8 | (buf[pos++] & 0xFF) << 16 | (buf[pos++] & 0xFF) << 24;
/*     */   }
/*     */   
/*     */   private static long readLong(byte[] buf, int pos) {
/* 314 */     return (buf[pos++] & 0xFF) | (buf[pos++] & 0xFF) << 8L | (buf[pos++] & 0xFF) << 16L | (buf[pos++] & 0xFF) << 24L | (buf[pos++] & 0xFF) << 32L | (buf[pos++] & 0xFF) << 40L | (buf[pos++] & 0xFF) << 48L | (buf[pos++] & 0xFF) << 56L;
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   private static byte[] readBytes(byte[] buf, int pos) {
/* 320 */     int length = readInt(buf, pos);
/* 321 */     byte[] msg = new byte[length];
/* 322 */     System.arraycopy(buf, pos + 4, msg, 0, length);
/* 323 */     return msg;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\log\ProfilerEventImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */