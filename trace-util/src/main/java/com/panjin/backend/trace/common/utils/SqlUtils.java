/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.common.utils;

import com.panjin.backend.trace.common.sql.HideValueDeParser;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;

/**
 *
 *
 * @author panjin
 * @version $Id: SqlUtils.java 2016年7月21日 下午5:14:38 $
 */
public class SqlUtils {

    /**
     * 将SQL语句中的变量值替换为'?'
     * 
     * @param sql SQL语句
     * @return SQL解析出错则返回原始值
     */
    public static String hideSqlValues(String sql) {
        try {
            Statement stmt = CCJSqlParserUtil.parse(sql);
            StringBuilder buffer = new StringBuilder();
            StatementVisitor statementVisitor = new HideValueDeParser(buffer);
            stmt.accept(statementVisitor);
            return buffer.toString();
        } catch (Exception e) {
            return sql;
        }
    }
}
