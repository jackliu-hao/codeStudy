package oshi.hardware;

import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface Sensors {
   double getCpuTemperature();

   int[] getFanSpeeds();

   double getCpuVoltage();
}
