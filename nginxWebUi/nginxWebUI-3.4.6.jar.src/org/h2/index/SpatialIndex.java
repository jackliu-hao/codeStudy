package org.h2.index;

import org.h2.engine.SessionLocal;
import org.h2.result.SearchRow;

public interface SpatialIndex {
  Cursor findByGeometry(SessionLocal paramSessionLocal, SearchRow paramSearchRow1, SearchRow paramSearchRow2, SearchRow paramSearchRow3);
}


/* Location:              G:\git\codeReviewLog\nginxWebUi\nginxWebUI-3.4.6.jar!\org\h2\index\SpatialIndex.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */