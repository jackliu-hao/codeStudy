package com.google.protobuf;

import java.util.List;

public interface ProtocolStringList extends List<String> {
  List<ByteString> asByteStringList();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\com\google\protobuf\ProtocolStringList.class
 * Java compiler version: 7 (51.0)
 * JD-Core Version:       1.1.3
 */