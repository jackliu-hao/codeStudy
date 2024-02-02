package oshi.hardware;

import oshi.annotation.concurrent.ThreadSafe;

@ThreadSafe
public interface Sensors {
  double getCpuTemperature();
  
  int[] getFanSpeeds();
  
  double getCpuVoltage();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\oshi\hardware\Sensors.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */