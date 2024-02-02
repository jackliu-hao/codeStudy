package com.mysql.cj.protocol.x;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;
import com.mysql.cj.exceptions.AssertionFailedException;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.x.protobuf.Mysqlx;
import com.mysql.cj.x.protobuf.MysqlxConnection;
import com.mysql.cj.x.protobuf.MysqlxCrud;
import com.mysql.cj.x.protobuf.MysqlxExpect;
import com.mysql.cj.x.protobuf.MysqlxNotice;
import com.mysql.cj.x.protobuf.MysqlxPrepare;
import com.mysql.cj.x.protobuf.MysqlxResultset;
import com.mysql.cj.x.protobuf.MysqlxSession;
import com.mysql.cj.x.protobuf.MysqlxSql;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MessageConstants {
   public static final Map<Class<? extends GeneratedMessageV3>, Parser<? extends GeneratedMessageV3>> MESSAGE_CLASS_TO_PARSER;
   public static final Map<Class<? extends GeneratedMessageV3>, Integer> MESSAGE_CLASS_TO_TYPE;
   public static final Map<Integer, Class<? extends GeneratedMessageV3>> MESSAGE_TYPE_TO_CLASS;
   public static final Map<Class<? extends MessageLite>, Integer> MESSAGE_CLASS_TO_CLIENT_MESSAGE_TYPE;

   public static int getTypeForMessageClass(Class<? extends MessageLite> msgClass) {
      Integer tag = (Integer)MESSAGE_CLASS_TO_CLIENT_MESSAGE_TYPE.get(msgClass);
      if (tag == null) {
         throw new WrongArgumentException("No mapping to ClientMessages for message class " + msgClass.getSimpleName());
      } else {
         return tag;
      }
   }

   public static Class<? extends GeneratedMessageV3> getMessageClassForType(int type) {
      Class<? extends GeneratedMessageV3> messageClass = (Class)MESSAGE_TYPE_TO_CLASS.get(type);
      if (messageClass == null) {
         Mysqlx.ServerMessages.Type serverMessageMapping = Mysqlx.ServerMessages.Type.forNumber(type);
         throw AssertionFailedException.shouldNotHappen("Unknown message type: " + type + " (server messages mapping: " + serverMessageMapping + ")");
      } else {
         return messageClass;
      }
   }

   static {
      Map<Class<? extends GeneratedMessageV3>, Parser<? extends GeneratedMessageV3>> messageClassToParser = new HashMap();
      Map<Class<? extends GeneratedMessageV3>, Integer> messageClassToType = new HashMap();
      Map<Integer, Class<? extends GeneratedMessageV3>> messageTypeToClass = new HashMap();
      messageClassToParser.put(Mysqlx.Error.class, Mysqlx.Error.getDefaultInstance().getParserForType());
      messageClassToParser.put(Mysqlx.Ok.class, Mysqlx.Ok.getDefaultInstance().getParserForType());
      messageClassToParser.put(MysqlxSession.AuthenticateContinue.class, MysqlxSession.AuthenticateContinue.getDefaultInstance().getParserForType());
      messageClassToParser.put(MysqlxSession.AuthenticateOk.class, MysqlxSession.AuthenticateOk.getDefaultInstance().getParserForType());
      messageClassToParser.put(MysqlxConnection.Capabilities.class, MysqlxConnection.Capabilities.getDefaultInstance().getParserForType());
      messageClassToParser.put(MysqlxResultset.ColumnMetaData.class, MysqlxResultset.ColumnMetaData.getDefaultInstance().getParserForType());
      messageClassToParser.put(MysqlxResultset.FetchDone.class, MysqlxResultset.FetchDone.getDefaultInstance().getParserForType());
      messageClassToParser.put(MysqlxResultset.FetchDoneMoreResultsets.class, MysqlxResultset.FetchDoneMoreResultsets.getDefaultInstance().getParserForType());
      messageClassToParser.put(MysqlxNotice.Frame.class, MysqlxNotice.Frame.getDefaultInstance().getParserForType());
      messageClassToParser.put(MysqlxResultset.Row.class, MysqlxResultset.Row.getDefaultInstance().getParserForType());
      messageClassToParser.put(MysqlxSql.StmtExecuteOk.class, MysqlxSql.StmtExecuteOk.getDefaultInstance().getParserForType());
      messageClassToParser.put(MysqlxConnection.Compression.class, MysqlxConnection.Compression.getDefaultInstance().getParserForType());
      messageClassToParser.put(MysqlxNotice.SessionStateChanged.class, MysqlxNotice.SessionStateChanged.getDefaultInstance().getParserForType());
      messageClassToParser.put(MysqlxNotice.SessionVariableChanged.class, MysqlxNotice.SessionVariableChanged.getDefaultInstance().getParserForType());
      messageClassToParser.put(MysqlxNotice.Warning.class, MysqlxNotice.Warning.getDefaultInstance().getParserForType());
      messageClassToType.put(Mysqlx.Error.class, 1);
      messageClassToType.put(Mysqlx.Ok.class, 0);
      messageClassToType.put(MysqlxSession.AuthenticateContinue.class, 3);
      messageClassToType.put(MysqlxSession.AuthenticateOk.class, 4);
      messageClassToType.put(MysqlxConnection.Capabilities.class, 2);
      messageClassToType.put(MysqlxResultset.ColumnMetaData.class, 12);
      messageClassToType.put(MysqlxResultset.FetchDone.class, 14);
      messageClassToType.put(MysqlxResultset.FetchDoneMoreResultsets.class, 16);
      messageClassToType.put(MysqlxNotice.Frame.class, 11);
      messageClassToType.put(MysqlxResultset.Row.class, 13);
      messageClassToType.put(MysqlxSql.StmtExecuteOk.class, 17);
      messageClassToType.put(MysqlxConnection.Compression.class, 19);
      Iterator var3 = messageClassToType.entrySet().iterator();

      while(var3.hasNext()) {
         Map.Entry<Class<? extends GeneratedMessageV3>, Integer> entry = (Map.Entry)var3.next();
         messageTypeToClass.put(entry.getValue(), entry.getKey());
      }

      MESSAGE_CLASS_TO_PARSER = Collections.unmodifiableMap(messageClassToParser);
      MESSAGE_CLASS_TO_TYPE = Collections.unmodifiableMap(messageClassToType);
      MESSAGE_TYPE_TO_CLASS = Collections.unmodifiableMap(messageTypeToClass);
      Map<Class<? extends MessageLite>, Integer> messageClassToClientMessageType = new HashMap();
      messageClassToClientMessageType.put(MysqlxSession.AuthenticateStart.class, 4);
      messageClassToClientMessageType.put(MysqlxSession.AuthenticateContinue.class, 5);
      messageClassToClientMessageType.put(MysqlxConnection.CapabilitiesGet.class, 1);
      messageClassToClientMessageType.put(MysqlxConnection.CapabilitiesSet.class, 2);
      messageClassToClientMessageType.put(MysqlxSession.Close.class, 7);
      messageClassToClientMessageType.put(MysqlxCrud.Delete.class, 20);
      messageClassToClientMessageType.put(MysqlxCrud.Find.class, 17);
      messageClassToClientMessageType.put(MysqlxCrud.Insert.class, 18);
      messageClassToClientMessageType.put(MysqlxSession.Reset.class, 6);
      messageClassToClientMessageType.put(MysqlxSql.StmtExecute.class, 12);
      messageClassToClientMessageType.put(MysqlxCrud.Update.class, 19);
      messageClassToClientMessageType.put(MysqlxCrud.CreateView.class, 30);
      messageClassToClientMessageType.put(MysqlxCrud.ModifyView.class, 31);
      messageClassToClientMessageType.put(MysqlxCrud.DropView.class, 32);
      messageClassToClientMessageType.put(MysqlxExpect.Open.class, 24);
      messageClassToClientMessageType.put(MysqlxPrepare.Prepare.class, 40);
      messageClassToClientMessageType.put(MysqlxPrepare.Execute.class, 41);
      messageClassToClientMessageType.put(MysqlxPrepare.Deallocate.class, 42);
      MESSAGE_CLASS_TO_CLIENT_MESSAGE_TYPE = Collections.unmodifiableMap(messageClassToClientMessageType);
   }
}
