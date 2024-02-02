package org.h2.command;

import java.util.ArrayList;
import org.h2.expression.ParameterInterface;
import org.h2.result.ResultInterface;
import org.h2.result.ResultWithGeneratedKeys;

public interface CommandInterface extends AutoCloseable {
   int UNKNOWN = 0;
   int ALTER_INDEX_RENAME = 1;
   int ALTER_SCHEMA_RENAME = 2;
   int ALTER_TABLE_ADD_CONSTRAINT_CHECK = 3;
   int ALTER_TABLE_ADD_CONSTRAINT_UNIQUE = 4;
   int ALTER_TABLE_ADD_CONSTRAINT_REFERENTIAL = 5;
   int ALTER_TABLE_ADD_CONSTRAINT_PRIMARY_KEY = 6;
   int ALTER_TABLE_ADD_COLUMN = 7;
   int ALTER_TABLE_ALTER_COLUMN_NOT_NULL = 8;
   int ALTER_TABLE_ALTER_COLUMN_DROP_NOT_NULL = 9;
   int ALTER_TABLE_ALTER_COLUMN_DEFAULT = 10;
   int ALTER_TABLE_ALTER_COLUMN_CHANGE_TYPE = 11;
   int ALTER_TABLE_DROP_COLUMN = 12;
   int ALTER_TABLE_ALTER_COLUMN_SELECTIVITY = 13;
   int ALTER_TABLE_DROP_CONSTRAINT = 14;
   int ALTER_TABLE_RENAME = 15;
   int ALTER_TABLE_ALTER_COLUMN_RENAME = 16;
   int ALTER_USER_ADMIN = 17;
   int ALTER_USER_RENAME = 18;
   int ALTER_USER_SET_PASSWORD = 19;
   int ALTER_VIEW = 20;
   int ANALYZE = 21;
   int CREATE_AGGREGATE = 22;
   int CREATE_CONSTANT = 23;
   int CREATE_ALIAS = 24;
   int CREATE_INDEX = 25;
   int CREATE_LINKED_TABLE = 26;
   int CREATE_ROLE = 27;
   int CREATE_SCHEMA = 28;
   int CREATE_SEQUENCE = 29;
   int CREATE_TABLE = 30;
   int CREATE_TRIGGER = 31;
   int CREATE_USER = 32;
   int CREATE_DOMAIN = 33;
   int CREATE_VIEW = 34;
   int DEALLOCATE = 35;
   int DROP_AGGREGATE = 36;
   int DROP_CONSTANT = 37;
   int DROP_ALL_OBJECTS = 38;
   int DROP_ALIAS = 39;
   int DROP_INDEX = 40;
   int DROP_ROLE = 41;
   int DROP_SCHEMA = 42;
   int DROP_SEQUENCE = 43;
   int DROP_TABLE = 44;
   int DROP_TRIGGER = 45;
   int DROP_USER = 46;
   int DROP_DOMAIN = 47;
   int DROP_VIEW = 48;
   int GRANT = 49;
   int REVOKE = 50;
   int PREPARE = 51;
   int COMMENT = 52;
   int TRUNCATE_TABLE = 53;
   int ALTER_SEQUENCE = 54;
   int ALTER_TABLE_SET_REFERENTIAL_INTEGRITY = 55;
   int BACKUP = 56;
   int CALL = 57;
   int DELETE = 58;
   int EXECUTE = 59;
   int EXPLAIN = 60;
   int INSERT = 61;
   int MERGE = 62;
   int REPLACE = 63;
   int NO_OPERATION = 63;
   int RUNSCRIPT = 64;
   int SCRIPT = 65;
   int SELECT = 66;
   int SET = 67;
   int UPDATE = 68;
   int SET_AUTOCOMMIT_TRUE = 69;
   int SET_AUTOCOMMIT_FALSE = 70;
   int COMMIT = 71;
   int ROLLBACK = 72;
   int CHECKPOINT = 73;
   int SAVEPOINT = 74;
   int ROLLBACK_TO_SAVEPOINT = 75;
   int CHECKPOINT_SYNC = 76;
   int PREPARE_COMMIT = 77;
   int COMMIT_TRANSACTION = 78;
   int ROLLBACK_TRANSACTION = 79;
   int SHUTDOWN = 80;
   int SHUTDOWN_IMMEDIATELY = 81;
   int SHUTDOWN_COMPACT = 82;
   int BEGIN = 83;
   int SHUTDOWN_DEFRAG = 84;
   int ALTER_TABLE_RENAME_CONSTRAINT = 85;
   int EXPLAIN_ANALYZE = 86;
   int ALTER_TABLE_ALTER_COLUMN_VISIBILITY = 87;
   int CREATE_SYNONYM = 88;
   int DROP_SYNONYM = 89;
   int ALTER_TABLE_ALTER_COLUMN_ON_UPDATE = 90;
   int EXECUTE_IMMEDIATELY = 91;
   int ALTER_DOMAIN_ADD_CONSTRAINT = 92;
   int ALTER_DOMAIN_DROP_CONSTRAINT = 93;
   int ALTER_DOMAIN_DEFAULT = 94;
   int ALTER_DOMAIN_ON_UPDATE = 95;
   int ALTER_DOMAIN_RENAME = 96;
   int HELP = 97;
   int ALTER_TABLE_ALTER_COLUMN_DROP_EXPRESSION = 98;
   int ALTER_TABLE_ALTER_COLUMN_DROP_IDENTITY = 99;
   int ALTER_TABLE_ALTER_COLUMN_DEFAULT_ON_NULL = 100;
   int ALTER_DOMAIN_RENAME_CONSTRAINT = 101;

   int getCommandType();

   boolean isQuery();

   ArrayList<? extends ParameterInterface> getParameters();

   ResultInterface executeQuery(long var1, boolean var3);

   ResultWithGeneratedKeys executeUpdate(Object var1);

   void stop();

   void close();

   void cancel();

   ResultInterface getMetaData();
}
