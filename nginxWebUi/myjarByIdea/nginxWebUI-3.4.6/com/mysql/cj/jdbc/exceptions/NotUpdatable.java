package com.mysql.cj.jdbc.exceptions;

import com.mysql.cj.Messages;
import java.sql.SQLException;

public class NotUpdatable extends SQLException {
   private static final long serialVersionUID = 6004153665887216929L;

   public NotUpdatable(String reason) {
      super(reason + Messages.getString("NotUpdatable.1"), "S1000");
   }
}
