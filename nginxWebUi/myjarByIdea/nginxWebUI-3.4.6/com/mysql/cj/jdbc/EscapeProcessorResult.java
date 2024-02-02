package com.mysql.cj.jdbc;

class EscapeProcessorResult {
   boolean callingStoredFunction = false;
   String escapedSql;
   byte usesVariables = 0;
}
