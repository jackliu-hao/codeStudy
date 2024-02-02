package io.undertow.server.protocol.framed;

public interface FrameHeaderData {
  long getFrameLength();
  
  AbstractFramedStreamSourceChannel<?, ?, ?> getExistingChannel();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\i\\undertow\server\protocol\framed\FrameHeaderData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */