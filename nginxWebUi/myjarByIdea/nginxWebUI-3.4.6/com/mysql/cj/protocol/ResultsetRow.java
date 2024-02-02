package com.mysql.cj.protocol;

import com.mysql.cj.result.Row;

public interface ResultsetRow extends Row, ProtocolEntity {
   default boolean isBinaryEncoded() {
      return false;
   }
}
