package com.pushtorefresh.storio.sqlite.annotations.processor.generate;

import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteColumnMeta;
import com.pushtorefresh.storio.sqlite.annotations.processor.introspection.StorIOSQLiteTypeMeta;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class QueryGenerator {

    public static final String WHERE_CLAUSE = "where";
    public static final String WHERE_ARGS = "whereArgs";

    @NotNull
    public static Map<String, String> createWhere(@NotNull StorIOSQLiteTypeMeta storIOSQLiteTypeMeta, @NotNull String varName) {
        final StringBuilder whereClause = new StringBuilder();
        final StringBuilder whereArgs = new StringBuilder();

        int i = 0;

        for (final StorIOSQLiteColumnMeta columnMeta : storIOSQLiteTypeMeta.columns.values()) {
            if (columnMeta.storIOSQLiteColumn.key()) {
                if (i == 0) {
                    whereClause
                            .append(columnMeta.storIOSQLiteColumn.name())
                            .append(" = ?");

                    whereArgs
                            .append(varName)
                            .append(".")
                            .append(columnMeta.fieldName);
                } else {
                    whereClause
                            .append(" AND ")
                            .append(columnMeta.storIOSQLiteColumn.name())
                            .append(" = ?");

                    whereArgs
                            .append(", ")
                            .append(varName)
                            .append(".")
                            .append(columnMeta.fieldName);
                }

                i++;
            }
        }

        if (whereClause.length() == 0 || whereArgs.length() == 0) {
            return Collections.emptyMap();
        } else {
            final Map<String, String> result = new HashMap<String, String>(2);

            result.put(WHERE_CLAUSE, whereClause.toString()); // example: "email = ? AND user_id = ?"
            result.put(WHERE_ARGS, whereArgs.toString()); // example: "object.email, object.userId"

            return result;
        }
    }
}
