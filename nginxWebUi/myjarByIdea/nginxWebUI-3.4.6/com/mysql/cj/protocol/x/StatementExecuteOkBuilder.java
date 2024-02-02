package com.mysql.cj.protocol.x;

import com.mysql.cj.exceptions.ExceptionFactory;
import com.mysql.cj.exceptions.WrongArgumentException;
import com.mysql.cj.protocol.ProtocolEntity;
import com.mysql.cj.protocol.ResultBuilder;
import com.mysql.cj.protocol.Warning;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class StatementExecuteOkBuilder implements ResultBuilder<StatementExecuteOk> {
   private long rowsAffected = 0L;
   private Long lastInsertId = null;
   private List<String> generatedIds = Collections.emptyList();
   private List<Warning> warnings = new ArrayList();

   public boolean addProtocolEntity(ProtocolEntity entity) {
      if (entity instanceof Notice) {
         this.addNotice((Notice)entity);
         return false;
      } else if (entity instanceof FetchDoneEntity) {
         return false;
      } else if (entity instanceof StatementExecuteOk) {
         return true;
      } else {
         throw (WrongArgumentException)ExceptionFactory.createException(WrongArgumentException.class, "Unexpected protocol entity " + entity);
      }
   }

   public StatementExecuteOk build() {
      return new StatementExecuteOk(this.rowsAffected, this.lastInsertId, this.generatedIds, this.warnings);
   }

   private void addNotice(Notice notice) {
      if (notice instanceof Notice.XWarning) {
         this.warnings.add((Notice.XWarning)notice);
      } else if (notice instanceof Notice.XSessionStateChanged) {
         switch (((Notice.XSessionStateChanged)notice).getParamType()) {
            case 1:
            case 2:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
            case 10:
            case 11:
            default:
               break;
            case 3:
               this.lastInsertId = ((Notice.XSessionStateChanged)notice).getValue().getVUnsignedInt();
               break;
            case 4:
               this.rowsAffected = ((Notice.XSessionStateChanged)notice).getValue().getVUnsignedInt();
               break;
            case 12:
               this.generatedIds = (List)((Notice.XSessionStateChanged)notice).getValueList().stream().map((v) -> {
                  return v.getVOctets().getValue().toStringUtf8();
               }).collect(Collectors.toList());
         }
      }

   }
}
