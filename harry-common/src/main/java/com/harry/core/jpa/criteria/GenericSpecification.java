package com.harry.core.jpa.criteria;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.*;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Stack;

/**
 * GenericSpecification
 *
 * @author Tony Luo 2019-09-25
 */
public class GenericSpecification<T> implements Specification<T> {
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 1L;

    private final static Logger log = LoggerFactory.getLogger(GenericSpecification.class);

    private Expression expression;

    public static <T> GenericSpecification<T> of(final String whereClause) throws JSQLParserException {
        return new GenericSpecification<>(whereClause);
    }

    public GenericSpecification(final String whereClause) throws JSQLParserException {
        super();
        this.expression = CCJSqlParserUtil.parseCondExpression(whereClause);
    }

    @Override
    public Predicate toPredicate(final Root<T> root, final CriteriaQuery<?> query, final CriteriaBuilder builder) {
        PredicateExpressionVisitor<T> predicateExpressVisitor = new PredicateExpressionVisitor<>(root, builder);
        expression.accept(predicateExpressVisitor);
        return predicateExpressVisitor.getPredicate();

    }

    public static class PredicateExpressionVisitor<T> extends ExpressionVisitorAdapter {
        private final Root<T> root;
        private final CriteriaBuilder criteriaBuilder;
        final Stack<Predicate> predicateStack = new Stack<Predicate>();

        public Predicate getPredicate() {
            return predicateStack.pop();
        }

        public PredicateExpressionVisitor(final Root<T> root, final CriteriaBuilder builder) {
            this.root = root;
            this.criteriaBuilder = builder;
        }

        int depth = 0;

        public void processLogicalExpression(BinaryExpression expr, String logic) {

            if (log.isDebugEnabled()) {
                log.debug(StringUtils.repeat("-", depth) + logic);
                depth++;
            }
            expr.getLeftExpression().accept(this);
            expr.getRightExpression().accept(this);

            if (log.isDebugEnabled()) {
                if (depth != 0) {
                    depth--;
                }
            }

        }

        @Override
        protected void visitBinaryExpression(BinaryExpression expr) {
            if (log.isDebugEnabled()) {
                log.debug("{} {} {} {}", StringUtils.repeat("-", depth), expr.getLeftExpression(),
                        expr.getStringExpression(), expr.getRightExpression());
            }

            super.visitBinaryExpression(expr);

        }

        @Override
        public void visit(AndExpression expr) {
            processLogicalExpression(expr, "AND");
            Predicate predicate = criteriaBuilder.and(predicateStack.pop(), predicateStack.pop());
            predicateStack.push(predicate);

        }

        @Override
        public void visit(OrExpression expr) {
            processLogicalExpression(expr, "OR");
            Predicate predicate = criteriaBuilder.or(predicateStack.pop(), predicateStack.pop());
            predicateStack.push(predicate);
        }

        @Override
        public void visit(Between expr) {
            if (log.isDebugEnabled()) {
                log.debug("{} {} {} {} {} {}", StringUtils.repeat("-", depth), expr.getLeftExpression(), "between",
                        expr.getBetweenExpressionStart(), "and", expr.getBetweenExpressionEnd());
            }
            Predicate predicate = PredicateBuilder.Between.build(root, criteriaBuilder, expr);
            predicateStack.push(predicate);
            super.visit(expr);
        }

        @Override
        public void visit(EqualsTo expr) {

            if (log.isDebugEnabled()) {
                log.debug("{} {} {} {}", StringUtils.repeat("-", depth), expr.getLeftExpression(),
                        expr.getStringExpression(), expr.getRightExpression());
            }
            Predicate predicate = PredicateBuilder.EqualsTo.build(root, criteriaBuilder, expr);

            predicateStack.push(predicate);
            super.visitBinaryExpression(expr);

        }

        @Override
        public void visit(GreaterThan expr) {

            Predicate predicate = PredicateBuilder.GreaterThan.build(root, criteriaBuilder, expr);

            predicateStack.push(predicate);
            super.visitBinaryExpression(expr);
        }

        @Override
        public void visit(GreaterThanEquals expr) {

            Predicate predicate = PredicateBuilder.GreaterThanEquals.build(root, criteriaBuilder, expr);
            predicateStack.push(predicate);
            super.visitBinaryExpression(expr);
        }

        @Override
        public void visit(InExpression expr) {
            if (log.isDebugEnabled()) {
                log.debug("{} {} {} {}", StringUtils.repeat("-", depth), expr.getLeftExpression(), "in",
                        expr.getRightItemsList());
            }
            Predicate predicate = PredicateBuilder.In.build(root, criteriaBuilder, expr);
            predicateStack.push(predicate);
            super.visit(expr);
        }

        @Override
        public void visit(IsNullExpression expr) {
            if (log.isDebugEnabled()) {
                log.debug("{} {}", StringUtils.repeat("-", depth), expr.toString());
            }
            Predicate predicate = PredicateBuilder.IsNull.build(root, criteriaBuilder, expr);
            predicateStack.push(predicate);
            super.visit(expr);

        }

        @Override
        public void visit(FullTextSearch expr) {
            super.visit(expr);

        }

        @Override
        public void visit(IsBooleanExpression expr) {
            if (log.isDebugEnabled()) {
                log.debug("{} {}", StringUtils.repeat("-", depth), expr.toString());
            }
            Predicate predicate = PredicateBuilder.IsBoolean.build(root, criteriaBuilder, expr);
            predicateStack.push(predicate);

        }

        @Override
        public void visit(LikeExpression expr) {
            Predicate predicate = PredicateBuilder.Like.build(root, criteriaBuilder, expr);
            predicateStack.push(predicate);
            super.visit(expr);

        }

        @Override
        public void visit(MinorThan expr) {
            Predicate predicate = PredicateBuilder.MinorThan.build(root, criteriaBuilder, expr);
            predicateStack.push(predicate);
            super.visit(expr);

        }

        @Override
        public void visit(MinorThanEquals expr) {
            Predicate predicate = PredicateBuilder.MinorThanEquals.build(root, criteriaBuilder, expr);
            predicateStack.push(predicate);
            super.visit(expr);

        }

        @Override
        public void visit(NotEqualsTo expr) {

            Predicate predicate = PredicateBuilder.NotEqualsTo.build(root, criteriaBuilder, expr);
            predicateStack.push(predicate);

            super.visit(expr);

        }

        @Override
        public void visit(Parenthesis parenthesis) {
            parenthesis.getExpression().accept(this);
        }

    }

}
