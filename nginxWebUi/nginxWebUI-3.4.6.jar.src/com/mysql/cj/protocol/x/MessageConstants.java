/*     */ package com.mysql.cj.protocol.x;
/*     */ 
/*     */ import com.google.protobuf.GeneratedMessageV3;
/*     */ import com.google.protobuf.MessageLite;
/*     */ import com.google.protobuf.Parser;
/*     */ import com.mysql.cj.exceptions.AssertionFailedException;
/*     */ import com.mysql.cj.exceptions.WrongArgumentException;
/*     */ import com.mysql.cj.x.protobuf.Mysqlx;
/*     */ import com.mysql.cj.x.protobuf.MysqlxConnection;
/*     */ import com.mysql.cj.x.protobuf.MysqlxCrud;
/*     */ import com.mysql.cj.x.protobuf.MysqlxExpect;
/*     */ import com.mysql.cj.x.protobuf.MysqlxNotice;
/*     */ import com.mysql.cj.x.protobuf.MysqlxPrepare;
/*     */ import com.mysql.cj.x.protobuf.MysqlxResultset;
/*     */ import com.mysql.cj.x.protobuf.MysqlxSession;
/*     */ import com.mysql.cj.x.protobuf.MysqlxSql;
/*     */ import java.util.Collections;
/*     */ import java.util.HashMap;
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
/*     */ public class MessageConstants
/*     */ {
/*     */   public static final Map<Class<? extends GeneratedMessageV3>, Parser<? extends GeneratedMessageV3>> MESSAGE_CLASS_TO_PARSER;
/*     */   public static final Map<Class<? extends GeneratedMessageV3>, Integer> MESSAGE_CLASS_TO_TYPE;
/*     */   public static final Map<Integer, Class<? extends GeneratedMessageV3>> MESSAGE_TYPE_TO_CLASS;
/*     */   public static final Map<Class<? extends MessageLite>, Integer> MESSAGE_CLASS_TO_CLIENT_MESSAGE_TYPE;
/*     */   
/*     */   static {
/* 105 */     Map<Class<? extends GeneratedMessageV3>, Parser<? extends GeneratedMessageV3>> messageClassToParser = new HashMap<>();
/* 106 */     Map<Class<? extends GeneratedMessageV3>, Integer> messageClassToType = new HashMap<>();
/* 107 */     Map<Integer, Class<? extends GeneratedMessageV3>> messageTypeToClass = new HashMap<>();
/*     */     
/* 109 */     messageClassToParser.put(Mysqlx.Error.class, Mysqlx.Error.getDefaultInstance().getParserForType());
/* 110 */     messageClassToParser.put(Mysqlx.Ok.class, Mysqlx.Ok.getDefaultInstance().getParserForType());
/* 111 */     messageClassToParser.put(MysqlxSession.AuthenticateContinue.class, MysqlxSession.AuthenticateContinue.getDefaultInstance().getParserForType());
/* 112 */     messageClassToParser.put(MysqlxSession.AuthenticateOk.class, MysqlxSession.AuthenticateOk.getDefaultInstance().getParserForType());
/* 113 */     messageClassToParser.put(MysqlxConnection.Capabilities.class, MysqlxConnection.Capabilities.getDefaultInstance().getParserForType());
/* 114 */     messageClassToParser.put(MysqlxResultset.ColumnMetaData.class, MysqlxResultset.ColumnMetaData.getDefaultInstance().getParserForType());
/* 115 */     messageClassToParser.put(MysqlxResultset.FetchDone.class, MysqlxResultset.FetchDone.getDefaultInstance().getParserForType());
/* 116 */     messageClassToParser.put(MysqlxResultset.FetchDoneMoreResultsets.class, MysqlxResultset.FetchDoneMoreResultsets.getDefaultInstance().getParserForType());
/* 117 */     messageClassToParser.put(MysqlxNotice.Frame.class, MysqlxNotice.Frame.getDefaultInstance().getParserForType());
/* 118 */     messageClassToParser.put(MysqlxResultset.Row.class, MysqlxResultset.Row.getDefaultInstance().getParserForType());
/* 119 */     messageClassToParser.put(MysqlxSql.StmtExecuteOk.class, MysqlxSql.StmtExecuteOk.getDefaultInstance().getParserForType());
/* 120 */     messageClassToParser.put(MysqlxConnection.Compression.class, MysqlxConnection.Compression.getDefaultInstance().getParserForType());
/*     */     
/* 122 */     messageClassToParser.put(MysqlxNotice.SessionStateChanged.class, MysqlxNotice.SessionStateChanged.getDefaultInstance().getParserForType());
/* 123 */     messageClassToParser.put(MysqlxNotice.SessionVariableChanged.class, MysqlxNotice.SessionVariableChanged.getDefaultInstance().getParserForType());
/* 124 */     messageClassToParser.put(MysqlxNotice.Warning.class, MysqlxNotice.Warning.getDefaultInstance().getParserForType());
/*     */     
/* 126 */     messageClassToType.put(Mysqlx.Error.class, Integer.valueOf(1));
/* 127 */     messageClassToType.put(Mysqlx.Ok.class, Integer.valueOf(0));
/* 128 */     messageClassToType.put(MysqlxSession.AuthenticateContinue.class, Integer.valueOf(3));
/* 129 */     messageClassToType.put(MysqlxSession.AuthenticateOk.class, Integer.valueOf(4));
/* 130 */     messageClassToType.put(MysqlxConnection.Capabilities.class, Integer.valueOf(2));
/* 131 */     messageClassToType.put(MysqlxResultset.ColumnMetaData.class, Integer.valueOf(12));
/* 132 */     messageClassToType.put(MysqlxResultset.FetchDone.class, Integer.valueOf(14));
/* 133 */     messageClassToType.put(MysqlxResultset.FetchDoneMoreResultsets.class, Integer.valueOf(16));
/* 134 */     messageClassToType.put(MysqlxNotice.Frame.class, Integer.valueOf(11));
/* 135 */     messageClassToType.put(MysqlxResultset.Row.class, Integer.valueOf(13));
/* 136 */     messageClassToType.put(MysqlxSql.StmtExecuteOk.class, Integer.valueOf(17));
/* 137 */     messageClassToType.put(MysqlxConnection.Compression.class, Integer.valueOf(19));
/* 138 */     for (Map.Entry<Class<? extends GeneratedMessageV3>, Integer> entry : messageClassToType.entrySet()) {
/* 139 */       messageTypeToClass.put(entry.getValue(), entry.getKey());
/*     */     }
/* 141 */     MESSAGE_CLASS_TO_PARSER = Collections.unmodifiableMap(messageClassToParser);
/* 142 */     MESSAGE_CLASS_TO_TYPE = Collections.unmodifiableMap(messageClassToType);
/* 143 */     MESSAGE_TYPE_TO_CLASS = Collections.unmodifiableMap(messageTypeToClass);
/*     */ 
/*     */ 
/*     */ 
/*     */     
/* 148 */     Map<Class<? extends MessageLite>, Integer> messageClassToClientMessageType = new HashMap<>();
/* 149 */     messageClassToClientMessageType.put(MysqlxSession.AuthenticateStart.class, Integer.valueOf(4));
/* 150 */     messageClassToClientMessageType.put(MysqlxSession.AuthenticateContinue.class, Integer.valueOf(5));
/* 151 */     messageClassToClientMessageType.put(MysqlxConnection.CapabilitiesGet.class, Integer.valueOf(1));
/* 152 */     messageClassToClientMessageType.put(MysqlxConnection.CapabilitiesSet.class, Integer.valueOf(2));
/* 153 */     messageClassToClientMessageType.put(MysqlxSession.Close.class, Integer.valueOf(7));
/* 154 */     messageClassToClientMessageType.put(MysqlxCrud.Delete.class, Integer.valueOf(20));
/* 155 */     messageClassToClientMessageType.put(MysqlxCrud.Find.class, Integer.valueOf(17));
/* 156 */     messageClassToClientMessageType.put(MysqlxCrud.Insert.class, Integer.valueOf(18));
/* 157 */     messageClassToClientMessageType.put(MysqlxSession.Reset.class, Integer.valueOf(6));
/* 158 */     messageClassToClientMessageType.put(MysqlxSql.StmtExecute.class, Integer.valueOf(12));
/* 159 */     messageClassToClientMessageType.put(MysqlxCrud.Update.class, Integer.valueOf(19));
/* 160 */     messageClassToClientMessageType.put(MysqlxCrud.CreateView.class, Integer.valueOf(30));
/* 161 */     messageClassToClientMessageType.put(MysqlxCrud.ModifyView.class, Integer.valueOf(31));
/* 162 */     messageClassToClientMessageType.put(MysqlxCrud.DropView.class, Integer.valueOf(32));
/* 163 */     messageClassToClientMessageType.put(MysqlxExpect.Open.class, Integer.valueOf(24));
/* 164 */     messageClassToClientMessageType.put(MysqlxPrepare.Prepare.class, Integer.valueOf(40));
/* 165 */     messageClassToClientMessageType.put(MysqlxPrepare.Execute.class, Integer.valueOf(41));
/* 166 */     messageClassToClientMessageType.put(MysqlxPrepare.Deallocate.class, Integer.valueOf(42));
/* 167 */     MESSAGE_CLASS_TO_CLIENT_MESSAGE_TYPE = Collections.unmodifiableMap(messageClassToClientMessageType);
/*     */   }
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */   
/*     */   public static int getTypeForMessageClass(Class<? extends MessageLite> msgClass) {
/* 178 */     Integer tag = MESSAGE_CLASS_TO_CLIENT_MESSAGE_TYPE.get(msgClass);
/* 179 */     if (tag == null) {
/* 180 */       throw new WrongArgumentException("No mapping to ClientMessages for message class " + msgClass.getSimpleName());
/*     */     }
/* 182 */     return tag.intValue();
/*     */   }
/*     */   
/*     */   public static Class<? extends GeneratedMessageV3> getMessageClassForType(int type) {
/* 186 */     Class<? extends GeneratedMessageV3> messageClass = MESSAGE_TYPE_TO_CLASS.get(Integer.valueOf(type));
/* 187 */     if (messageClass == null) {
/*     */       
/* 189 */       Mysqlx.ServerMessages.Type serverMessageMapping = Mysqlx.ServerMessages.Type.forNumber(type);
/* 190 */       throw AssertionFailedException.shouldNotHappen("Unknown message type: " + type + " (server messages mapping: " + serverMessageMapping + ")");
/*     */     } 
/* 192 */     return messageClass;
/*     */   }
/*     */ }


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\mysql\cj\protocol\x\MessageConstants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */