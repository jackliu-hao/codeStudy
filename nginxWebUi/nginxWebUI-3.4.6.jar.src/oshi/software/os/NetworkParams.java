package oshi.software.os;

import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface NetworkParams {
  String getHostName();
  
  String getDomainName();
  
  String[] getDnsServers();
  
  String getIpv4DefaultGateway();
  
  String getIpv6DefaultGateway();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\software\os\NetworkParams.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */