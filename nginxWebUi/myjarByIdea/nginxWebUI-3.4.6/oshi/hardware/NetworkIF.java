package oshi.hardware;

import java.net.NetworkInterface;
import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface NetworkIF {
   NetworkInterface queryNetworkInterface();

   String getName();

   String getDisplayName();

   int getMTU();

   String getMacaddr();

   String[] getIPv4addr();

   Short[] getSubnetMasks();

   String[] getIPv6addr();

   Short[] getPrefixLengths();

   int getIfType();

   int getNdisPhysicalMediumType();

   boolean isConnectorPresent();

   long getBytesRecv();

   long getBytesSent();

   long getPacketsRecv();

   long getPacketsSent();

   long getInErrors();

   long getOutErrors();

   long getInDrops();

   long getCollisions();

   long getSpeed();

   long getTimeStamp();

   boolean isKnownVmMacAddr();

   boolean updateAttributes();
}
