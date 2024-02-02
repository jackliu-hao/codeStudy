package com.mysql.cj.protocol.x;

import com.google.protobuf.GeneratedMessageV3;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.MessageListener;
import com.mysql.cj.protocol.ProtocolEntity;
import com.mysql.cj.protocol.ProtocolEntityFactory;
import com.mysql.cj.protocol.ResultBuilder;
import com.mysql.cj.x.protobuf.Mysqlx;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class ResultMessageListener<R> implements MessageListener<XMessage> {
   private ResultBuilder<?> resultBuilder;
   private CompletableFuture<R> future;
   private Map<Class<? extends GeneratedMessageV3>, ProtocolEntityFactory<? extends ProtocolEntity, XMessage>> messageToProtocolEntityFactory = new HashMap();

   public ResultMessageListener(Map<Class<? extends GeneratedMessageV3>, ProtocolEntityFactory<? extends ProtocolEntity, XMessage>> messageToProtocolEntityFactory, ResultBuilder<R> resultBuilder, CompletableFuture<R> future) {
      this.messageToProtocolEntityFactory = messageToProtocolEntityFactory;
      this.resultBuilder = resultBuilder;
      this.future = future;
   }

   public boolean processMessage(XMessage message) {
      Class<? extends GeneratedMessageV3> msgClass = message.getMessage().getClass();
      if (Mysqlx.Error.class.equals(msgClass)) {
         this.future.completeExceptionally(new XProtocolError((Mysqlx.Error)Mysqlx.Error.class.cast(message.getMessage())));
      } else if (!this.messageToProtocolEntityFactory.containsKey(msgClass)) {
         this.future.completeExceptionally(new WrongArgumentException("Unhandled msg class (" + msgClass + ") + msg=" + message.getMessage()));
      } else {
         if (!this.resultBuilder.addProtocolEntity((ProtocolEntity)((ProtocolEntityFactory)this.messageToProtocolEntityFactory.get(msgClass)).createFromMessage(message))) {
            return false;
         }

         this.future.complete(this.resultBuilder.build());
      }

      return true;
   }

   public void error(Throwable ex) {
      this.future.completeExceptionally(ex);
   }
}
