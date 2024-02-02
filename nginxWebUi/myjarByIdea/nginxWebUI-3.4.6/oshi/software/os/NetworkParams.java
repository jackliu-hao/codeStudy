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
