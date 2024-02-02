package oshi.hardware;

import oshi.annotation.concurrent.Immutable;

@Immutable
public interface Baseboard {
   String getManufacturer();

   String getModel();

   String getVersion();

   String getSerialNumber();
}
