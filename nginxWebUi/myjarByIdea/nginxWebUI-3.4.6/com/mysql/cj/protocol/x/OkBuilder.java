package com.mysql.cj.protocol.x;

import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.ProtocolEntity;
import com.mysql.cj.protocol.ResultBuilder;

public class OkBuilder implements ResultBuilder<Ok> {
   public boolean addProtocolEntity(ProtocolEntity entity) {
      if (entity instanceof Notice) {
         return false;
      } else if (entity instanceof Ok) {
         return true;
      } else {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Unexpected protocol entity " + entity);
      }
   }

   public Ok build() {
      return new Ok();
   }
}
