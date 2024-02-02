/*     */ package org.h2.command;
/*     */ 
/*     */ import java.io.IOException;
/*     */ import java.util.ArrayList;
/*     */ import org.h2.engine.GeneratedKeysMode;
/*     */ import org.h2.engine.SessionRemote;
/*     */ import org.h2.engine.SysProperties;
/*     */ import org.h2.expression.ParameterInterface;
/*     */ import org.h2.expression.ParameterRemote;
/*     */ import org.h2.message.DbException;
/*     */ import org.h2.message.Trace;
/*     */ import org.h2.result.ResultInterface;
/*     */ import org.h2.result.ResultRemote;
/*     */ import org.h2.result.ResultWithGeneratedKeys;
/*     */ import org.h2.util.Utils;
/*     */ import org.h2.value.Transfer;
/*     */ import org.h2.value.Value;
/*     */ import org.h2.value.ValueLob;
/*     */ import org.h2.value.ValueNull;
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class CommandRemote
/*     */   implements CommandInterface
/*     */ {
/*     */   private final ArrayList<Transfer> transferList;
/*     */   private final ArrayList<ParameterInterface> parameters;
/*     */   private final Trace trace;
/*     */   private final String sql;
/*     */   private final int fetchSize;
/*     */   private SessionRemote session;
/*     */   private int id;
/*     */   private boolean isQuery;
/*  40 */   private int cmdType = 0;
/*     */   
/*     */   private boolean readonly;
/*     */   private final int created;
/*     */   
/*     */   public CommandRemote(SessionRemote paramSessionRemote, ArrayList<Transfer> paramArrayList, String paramString, int paramInt) {
/*  46 */     this.transferList = paramArrayList;
/*  47 */     this.trace = paramSessionRemote.getTrace();
/*  48 */     this.sql = paramString;
/*  49 */     this.parameters = Utils.newSmallArrayList();
/*  50 */     prepare(paramSessionRemote, true);
/*     */ 
/*     */     
/*  53 */     this.session = paramSessionRemote;
/*  54 */     this.fetchSize = paramInt;
/*  55 */     this.created = paramSessionRemote.getLastReconnect();
/*     */   }
/*     */ 
/*     */ 
/*     */   
/*     */   public void stop() {}
/*     */ 
/*     */   
/*     */   private void prepare(SessionRemote paramSessionRemote, boolean paramBoolean) {
/*  64 */     this.id = paramSessionRemote.getNextId();
/*  65 */     for (byte b1 = 0, b2 = 0; b1 < this.transferList.size(); b1++) {
/*     */       try {
/*  67 */         Transfer transfer = this.transferList.get(b1);
/*     */         
/*  69 */         if (paramBoolean) {
/*  70 */           paramSessionRemote.traceOperation("SESSION_PREPARE_READ_PARAMS2", this.id);
/*  71 */           transfer.writeInt(18)
/*  72 */             .writeInt(this.id).writeString(this.sql);
/*     */         } else {
/*  74 */           paramSessionRemote.traceOperation("SESSION_PREPARE", this.id);
/*  75 */           transfer.writeInt(0)
/*  76 */             .writeInt(this.id).writeString(this.sql);
/*     */         } 
/*  78 */         paramSessionRemote.done(transfer);
/*  79 */         this.isQuery = transfer.readBoolean();
/*  80 */         this.readonly = transfer.readBoolean();
/*     */         
/*  82 */         this.cmdType = paramBoolean ? transfer.readInt() : 0;
/*     */         
/*  84 */         int i = transfer.readInt();
/*  85 */         if (paramBoolean) {
/*  86 */           this.parameters.clear();
/*  87 */           for (byte b = 0; b < i; b++) {
/*  88 */             ParameterRemote parameterRemote = new ParameterRemote(b);
/*  89 */             parameterRemote.readMetaData(transfer);
/*  90 */             this.parameters.add(parameterRemote);
/*     */           } 
/*     */         } 
/*  93 */       } catch (IOException iOException) {
/*  94 */         paramSessionRemote.removeServer(iOException, b1--, ++b2);
/*     */       } 
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public boolean isQuery() {
/* 101 */     return this.isQuery;
/*     */   }
/*     */ 
/*     */   
/*     */   public ArrayList<ParameterInterface> getParameters() {
/* 106 */     return this.parameters;
/*     */   }
/*     */   
/*     */   private void prepareIfRequired() {
/* 110 */     if (this.session.getLastReconnect() != this.created)
/*     */     {
/* 112 */       this.id = Integer.MIN_VALUE;
/*     */     }
/* 114 */     this.session.checkClosed();
/* 115 */     if (this.id <= this.session.getCurrentId() - SysProperties.SERVER_CACHED_OBJECTS)
/*     */     {
/* 117 */       prepare(this.session, false);
/*     */     }
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface getMetaData() {
/* 123 */     synchronized (this.session) {
/* 124 */       if (!this.isQuery) {
/* 125 */         return null;
/*     */       }
/* 127 */       int i = this.session.getNextId();
/* 128 */       ResultRemote resultRemote = null;
/* 129 */       for (byte b1 = 0, b2 = 0; b1 < this.transferList.size(); b1++) {
/* 130 */         prepareIfRequired();
/* 131 */         Transfer transfer = this.transferList.get(b1);
/*     */         try {
/* 133 */           this.session.traceOperation("COMMAND_GET_META_DATA", this.id);
/* 134 */           transfer.writeInt(10)
/* 135 */             .writeInt(this.id).writeInt(i);
/* 136 */           this.session.done(transfer);
/* 137 */           int j = transfer.readInt();
/* 138 */           resultRemote = new ResultRemote(this.session, transfer, i, j, 2147483647);
/*     */           
/*     */           break;
/* 141 */         } catch (IOException iOException) {
/* 142 */           this.session.removeServer(iOException, b1--, ++b2);
/*     */         } 
/*     */       } 
/* 145 */       this.session.autoCommitIfCluster();
/* 146 */       return (ResultInterface)resultRemote;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultInterface executeQuery(long paramLong, boolean paramBoolean) {
/* 152 */     checkParameters();
/* 153 */     synchronized (this.session) {
/* 154 */       int i = this.session.getNextId();
/* 155 */       ResultRemote resultRemote = null;
/* 156 */       for (byte b1 = 0, b2 = 0; b1 < this.transferList.size(); b1++) {
/* 157 */         prepareIfRequired();
/* 158 */         Transfer transfer = this.transferList.get(b1); try {
/*     */           int j;
/* 160 */           this.session.traceOperation("COMMAND_EXECUTE_QUERY", this.id);
/* 161 */           transfer.writeInt(2).writeInt(this.id).writeInt(i);
/* 162 */           transfer.writeRowCount(paramLong);
/*     */           
/* 164 */           if (this.session.isClustered() || paramBoolean) {
/* 165 */             j = Integer.MAX_VALUE;
/*     */           } else {
/* 167 */             j = this.fetchSize;
/*     */           } 
/* 169 */           transfer.writeInt(j);
/* 170 */           sendParameters(transfer);
/* 171 */           this.session.done(transfer);
/* 172 */           int k = transfer.readInt();
/* 173 */           if (resultRemote != null) {
/* 174 */             resultRemote.close();
/* 175 */             resultRemote = null;
/*     */           } 
/* 177 */           resultRemote = new ResultRemote(this.session, transfer, i, k, j);
/* 178 */           if (this.readonly) {
/*     */             break;
/*     */           }
/* 181 */         } catch (IOException iOException) {
/* 182 */           this.session.removeServer(iOException, b1--, ++b2);
/*     */         } 
/*     */       } 
/* 185 */       this.session.autoCommitIfCluster();
/* 186 */       this.session.readSessionState();
/* 187 */       return (ResultInterface)resultRemote;
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public ResultWithGeneratedKeys executeUpdate(Object paramObject) {
/* 193 */     checkParameters();
/* 194 */     int i = GeneratedKeysMode.valueOf(paramObject);
/* 195 */     boolean bool1 = (i != 0) ? true : false;
/* 196 */     boolean bool2 = bool1 ? this.session.getNextId() : false;
/* 197 */     synchronized (this.session) {
/* 198 */       long l = 0L;
/* 199 */       ResultRemote resultRemote = null;
/* 200 */       boolean bool = false;
/* 201 */       for (byte b1 = 0, b2 = 0; b1 < this.transferList.size(); b1++) {
/* 202 */         prepareIfRequired();
/* 203 */         Transfer transfer = this.transferList.get(b1); try {
/*     */           int[] arrayOfInt; String[] arrayOfString;
/* 205 */           this.session.traceOperation("COMMAND_EXECUTE_UPDATE", this.id);
/* 206 */           transfer.writeInt(3).writeInt(this.id);
/* 207 */           sendParameters(transfer);
/* 208 */           transfer.writeInt(i);
/* 209 */           switch (i) {
/*     */             case 2:
/* 211 */               arrayOfInt = (int[])paramObject;
/* 212 */               transfer.writeInt(arrayOfInt.length);
/* 213 */               for (int j : arrayOfInt) {
/* 214 */                 transfer.writeInt(j);
/*     */               }
/*     */               break;
/*     */             
/*     */             case 3:
/* 219 */               arrayOfString = (String[])paramObject;
/* 220 */               transfer.writeInt(arrayOfString.length);
/* 221 */               for (String str : arrayOfString) {
/* 222 */                 transfer.writeString(str);
/*     */               }
/*     */               break;
/*     */           } 
/*     */           
/* 227 */           this.session.done(transfer);
/* 228 */           l = transfer.readRowCount();
/* 229 */           bool = transfer.readBoolean();
/* 230 */           if (bool1) {
/* 231 */             int j = transfer.readInt();
/* 232 */             if (resultRemote != null) {
/* 233 */               resultRemote.close();
/* 234 */               resultRemote = null;
/*     */             } 
/* 236 */             resultRemote = new ResultRemote(this.session, transfer, bool2, j, 2147483647);
/*     */           } 
/* 238 */         } catch (IOException iOException) {
/* 239 */           this.session.removeServer(iOException, b1--, ++b2);
/*     */         } 
/*     */       } 
/* 242 */       this.session.setAutoCommitFromServer(bool);
/* 243 */       this.session.autoCommitIfCluster();
/* 244 */       this.session.readSessionState();
/* 245 */       if (resultRemote != null) {
/* 246 */         return (ResultWithGeneratedKeys)new ResultWithGeneratedKeys.WithKeys(l, (ResultInterface)resultRemote);
/*     */       }
/* 248 */       return ResultWithGeneratedKeys.of(l);
/*     */     } 
/*     */   }
/*     */   
/*     */   private void checkParameters() {
/* 253 */     if (this.cmdType != 60) {
/* 254 */       for (ParameterInterface parameterInterface : this.parameters) {
/* 255 */         parameterInterface.checkSet();
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private void sendParameters(Transfer paramTransfer) throws IOException {
/* 261 */     int i = this.parameters.size();
/* 262 */     paramTransfer.writeInt(i);
/* 263 */     for (ParameterInterface parameterInterface : this.parameters) {
/* 264 */       ValueNull valueNull; Value value = parameterInterface.getParamValue();
/*     */       
/* 266 */       if (value == null && this.cmdType == 60) {
/* 267 */         valueNull = ValueNull.INSTANCE;
/*     */       }
/*     */       
/* 270 */       paramTransfer.writeValue((Value)valueNull);
/*     */     } 
/*     */   }
/*     */ 
/*     */   
/*     */   public void close() {
/* 276 */     if (this.session == null || this.session.isClosed()) {
/*     */       return;
/*     */     }
/* 279 */     synchronized (this.session) {
/* 280 */       this.session.traceOperation("COMMAND_CLOSE", this.id);
/* 281 */       for (Transfer transfer : this.transferList) {
/*     */         try {
/* 283 */           transfer.writeInt(4).writeInt(this.id);
/* 284 */         } catch (IOException iOException) {
/* 285 */           this.trace.error(iOException, "close");
/*     */         } 
/*     */       } 
/*     */     } 
/* 289 */     this.session = null;
/*     */     try {
/* 291 */       for (ParameterInterface parameterInterface : this.parameters) {
/* 292 */         Value value = parameterInterface.getParamValue();
/* 293 */         if (value instanceof ValueLob) {
/* 294 */           ((ValueLob)value).remove();
/*     */         }
/*     */       } 
/* 297 */     } catch (DbException dbException) {
/* 298 */       this.trace.error((Throwable)dbException, "close");
/*     */     } 
/* 300 */     this.parameters.clear();
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public void cancel() {
/* 308 */     this.session.cancelStatement(this.id);
/*     */   }
/*     */ 
/*     */   
/*     */   public String toString() {
/* 313 */     return this.sql + Trace.formatParams(getParameters());
/*     */   }
/*     */ 
/*     */   
/*     */   public int getCommandType() {
/* 318 */     return this.cmdType;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\command\CommandRemote.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */