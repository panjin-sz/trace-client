/**
 * Copyright (c) 2011-2016 All Rights Reserved.
 */
package com.panjin.backend.trace.common.sql;

import java.util.Iterator;

import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.create.index.CreateIndex;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.view.CreateView;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.drop.Drop;
import net.sf.jsqlparser.statement.execute.Execute;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.replace.Replace;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectVisitor;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.truncate.Truncate;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.deparser.*;

/**
 *
 *
 * @author panjin
 * @version $Id: HideValueDeParser.java 2016年7月21日 下午5:15:25 $
 */
public class HideValueDeParser implements StatementVisitor {

    private StringBuilder buffer;

    public HideValueDeParser(StringBuilder buffer) {
        this.buffer = buffer;
    }

    @Override
    public void visit(CreateIndex createIndex) {
        CreateIndexDeParser createIndexDeParser = new CreateIndexDeParser(buffer);
        createIndexDeParser.deParse(createIndex);
    }

    @Override
    public void visit(CreateTable createTable) {
        CreateTableDeParser createTableDeParser = new CreateTableDeParser(buffer);
        createTableDeParser.deParse(createTable);
    }

    @Override
    public void visit(CreateView createView) {
        CreateViewDeParser createViewDeParser = new CreateViewDeParser(buffer);
        createViewDeParser.deParse(createView);
    }

    @Override
    public void visit(Delete delete) {
        SelectDeParser selectDeParser = new SelectDeParser();
        selectDeParser.setBuffer(buffer);
        ExpressionDeParser expressionDeParser = new HideValueExpressionDeParser(selectDeParser, buffer);
        selectDeParser.setExpressionVisitor(expressionDeParser);
        DeleteDeParser deleteDeParser = new DeleteDeParser(expressionDeParser, buffer);
        deleteDeParser.deParse(delete);
    }

    @Override
    public void visit(Drop drop) {
        // Auto-generated method stub
    }

    @Override
    public void visit(Insert insert) {
        SelectDeParser selectDeParser = new SelectDeParser();
        selectDeParser.setBuffer(buffer);
        ExpressionDeParser expressionDeParser = new HideValueExpressionDeParser(selectDeParser, buffer);
        selectDeParser.setExpressionVisitor(expressionDeParser);
        InsertDeParser insertDeParser = new InsertDeParser(expressionDeParser, selectDeParser, buffer);
        insertDeParser.deParse(insert);
    }

    @Override
    public void visit(Replace replace) {
        SelectDeParser selectDeParser = new SelectDeParser();
        selectDeParser.setBuffer(buffer);
        ExpressionDeParser expressionDeParser = new HideValueExpressionDeParser(selectDeParser, buffer);
        selectDeParser.setExpressionVisitor(expressionDeParser);
        ReplaceDeParser replaceDeParser = new ReplaceDeParser(expressionDeParser, selectDeParser, buffer);
        replaceDeParser.deParse(replace);
    }

    @Override
    public void visit(Select select) {
        SelectDeParser selectDeParser = new SelectDeParser();
        selectDeParser.setBuffer(buffer);
        ExpressionDeParser expressionDeParser = new HideValueExpressionDeParser(selectDeParser, buffer);
        selectDeParser.setExpressionVisitor(expressionDeParser);
        if (select.getWithItemsList() != null && !select.getWithItemsList().isEmpty()) {
            buffer.append("WITH ");
            for (Iterator<WithItem> iter = select.getWithItemsList().iterator(); iter.hasNext();) {
                WithItem withItem = iter.next();
                buffer.append(withItem);
                if (iter.hasNext()) {
                    buffer.append(",");
                }
                buffer.append(" ");
            }
        }
        select.getSelectBody().accept(selectDeParser);
    }

    @Override
    public void visit(Truncate truncate) {
    }

    @Override
    public void visit(Update update) {
        SelectDeParser selectDeParser = new SelectDeParser();
        selectDeParser.setBuffer(buffer);
        ExpressionDeParser expressionDeParser = new HideValueExpressionDeParser(selectDeParser, buffer);
        UpdateDeParser updateDeParser = new UpdateDeParser(expressionDeParser, buffer);
        selectDeParser.setExpressionVisitor(expressionDeParser);
        updateDeParser.deParse(update);
    }

    public StringBuilder getBuffer() {
        return buffer;
    }

    public void setBuffer(StringBuilder buffer) {
        this.buffer = buffer;
    }

    @Override
    public void visit(Alter alter) {

    }

    @Override
    public void visit(Statements stmts) {
        stmts.accept(this);
    }

    @Override
    public void visit(Execute execute) {
        SelectDeParser selectDeParser = new SelectDeParser();
        selectDeParser.setBuffer(buffer);
        ExpressionDeParser expressionDeParser = new HideValueExpressionDeParser(selectDeParser, buffer);
        ExecuteDeParser executeDeParser = new ExecuteDeParser(expressionDeParser, buffer);
        selectDeParser.setExpressionVisitor(expressionDeParser);
        executeDeParser.deParse(execute);
    }

    private static class HideValueExpressionDeParser extends ExpressionDeParser {
        public HideValueExpressionDeParser(SelectVisitor selectVisitor, StringBuilder buffer) {
            super(selectVisitor, buffer);
        }

        @Override
        public void visit(StringValue stringValue) {
            this.getBuffer().append('?');
        }

        @Override
        public void visit(LongValue longValue) {
            this.getBuffer().append('?');
        }

        @Override
        public void visit(DoubleValue doubleValue) {
            this.getBuffer().append('?');
        }

        @Override
        public void visit(NullValue nullValue) {
            this.getBuffer().append('?');
        }

        @Override
        public void visit(DateValue dateValue) {
            this.getBuffer().append('?');
        }

        @Override
        public void visit(TimestampValue timestampValue) {
            this.getBuffer().append('?');
        }

        @Override
        public void visit(TimeValue timeValue) {
            this.getBuffer().append('?');
        }
    }
}
