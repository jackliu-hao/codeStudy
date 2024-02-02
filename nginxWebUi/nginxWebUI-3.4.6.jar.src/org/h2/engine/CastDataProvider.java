package org.h2.engine;

import org.h2.api.JavaObjectSerializer;
import org.h2.util.TimeZoneProvider;
import org.h2.value.ValueTimestampTimeZone;

public interface CastDataProvider {
  ValueTimestampTimeZone currentTimestamp();
  
  TimeZoneProvider currentTimeZone();
  
  Mode getMode();
  
  JavaObjectSerializer getJavaObjectSerializer();
  
  boolean zeroBasedEnums();
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\engine\CastDataProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */