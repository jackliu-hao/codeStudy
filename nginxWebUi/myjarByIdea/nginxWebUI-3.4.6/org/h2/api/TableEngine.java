package org.h2.api;

import org.h2.command.ddl.CreateTableData;
import org.h2.table.Table;

public interface TableEngine {
   Table createTable(CreateTableData var1);
}
