package com.harry.core.jpa.criteria;

import com.harry.core.util.DateTimeUtil;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.relational.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.sql.Date;
import java.sql.Time;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GenericSpecification
 *
 * @author Tony Luo 2019-09-25
 */
public enum PredicateBuilder implements PredicateStrategy {
    /**
     * Create a predicate for testing the arguments for equality.
     */
    Between {
        @Override
        public Predicate build(final Root root, final CriteriaBuilder criteriaBuilder, Expression expression) {
            Predicate predicate;
            Between expr = (Between) expression;

            // 判断参数类型是数字还是日期
            final Path path = root.get(escapeQuote(expr.getLeftExpression().toString()));
            Class type = path.getJavaType();
            if (isInstant(type)) {
                String startStr = escapeQuote(expr.getBetweenExpressionStart().toString());
                String endStr = escapeQuote(expr.getBetweenExpressionEnd().toString());
                Instant start = getInstant(startStr);
                Instant end = getInstant(endStr);
                predicate = criteriaBuilder.between(path, start, end);

            } else {
                Object startValue = getExpressionValue(expr.getBetweenExpressionStart());
                Object endValue = getExpressionValue(expr.getBetweenExpressionEnd());
                if (startValue instanceof String) {
                    predicate = criteriaBuilder.between(path, (String) startValue, (String) endValue);
                } else if (startValue instanceof Long) {
                    predicate = criteriaBuilder.between(path, (Long) startValue, (Long) endValue);
                } else if (startValue instanceof Double) {
                    predicate = criteriaBuilder.between(path, (Double) startValue, (Double) endValue);
                } else if (startValue instanceof Date) {
                    predicate = criteriaBuilder.between(path, (Date) startValue, (Date) endValue);
                } else if (startValue instanceof Time) {
                    predicate = criteriaBuilder.between(path, (Time) startValue, (Time) endValue);
                } else {
                    predicate = criteriaBuilder.between(path, startValue.toString(), endValue.toString());
                }
            }
            return predicate;

        }

        @Override
        public Predicate operate(final Path path, final CriteriaBuilder criteriaBuilder, final Object value) {
            return null;
        }
    },
    /**
     * Create a predicate for testing the arguments for equality.
     */
    EqualsTo {
        @Override
        public Predicate build(final Root root, final CriteriaBuilder criteriaBuilder, Expression expression) {
            return getBinaryExpressionPredicate(root, criteriaBuilder, (BinaryExpression) expression, this);

        }

        @Override
        public Predicate operate(final Path path, final CriteriaBuilder criteriaBuilder, final Object value) {
            return criteriaBuilder.equal(path, value);
        }
    },
    GreaterThan {
        @Override
        public Predicate build(final Root root, final CriteriaBuilder criteriaBuilder, Expression expression) {
            return getBinaryExpressionPredicate(root, criteriaBuilder, (BinaryExpression) expression, this);
        }

        @Override
        public Predicate operate(final Path path, final CriteriaBuilder criteriaBuilder, final Object value) {

            if (value instanceof Instant) {
                return criteriaBuilder.greaterThan(path, (Instant) value);
            } else {
                return criteriaBuilder.greaterThan(path, value.toString());
            }

        }
    },
    GreaterThanEquals {
        @Override
        public Predicate build(final Root root, final CriteriaBuilder criteriaBuilder, Expression expression) {
            return getBinaryExpressionPredicate(root, criteriaBuilder, (BinaryExpression) expression, this);

        }

        @Override
        public Predicate operate(final Path path, final CriteriaBuilder criteriaBuilder, final Object value) {
            if (value instanceof Instant) {
                return criteriaBuilder.greaterThanOrEqualTo(path, (Instant) value);
            } else {
                return criteriaBuilder.greaterThanOrEqualTo(path, value.toString());
            }
        }
    },
    In {
        @Override
        public Predicate build(final Root root, final CriteriaBuilder criteriaBuilder, Expression expression) {
            InExpression expr = (InExpression) expression;
            final Path path = root.get(escapeQuote(expr.getLeftExpression().toString()));
            // 处理非字符串的参数类型
            ExpressionList list = (ExpressionList) expr.getRightItemsList();

            List valueList;
            valueList = list.getExpressions().stream().map(PredicateBuilder::getExpressionValue)
                .collect(Collectors.toList());
            Predicate predicate = path.in(valueList);
            return predicate;

        }

        @Override
        public Predicate operate(final Path path, final CriteriaBuilder criteriaBuilder, final Object value) {
            return null;
        }
    },
    IsNull {
        @Override
        public Predicate build(final Root root, final CriteriaBuilder criteriaBuilder, Expression expression) {
            IsNullExpression expr = (IsNullExpression) expression;

            Predicate predicate = expr.isNot() ? root.get(escapeQuote(expr.getLeftExpression().toString())).isNotNull()
                : root.get(escapeQuote(expr.getLeftExpression().toString())).isNull();
            return predicate;

        }

        @Override
        public Predicate operate(final Path path, final CriteriaBuilder criteriaBuilder, final Object value) {
            return null;
        }
    },
    IsBoolean {
        @Override
        public Predicate build(final Root root, final CriteriaBuilder criteriaBuilder, Expression expression) {
            IsBooleanExpression expr = (IsBooleanExpression) expression;
            final Path path = root.get(escapeQuote(expr.getLeftExpression().toString()));
            Predicate predicate = expr.isTrue() ? criteriaBuilder.isTrue(path) : criteriaBuilder.isFalse(path);
            return predicate;

        }

        @Override
        public Predicate operate(final Path path, final CriteriaBuilder criteriaBuilder, final Object value) {
            return null;
        }
    },
    Like {
        @Override
        public Predicate build(final Root root, final CriteriaBuilder criteriaBuilder, Expression expression) {
            LikeExpression expr = (LikeExpression) expression;
            final Path path = root.get(escapeQuote(expr.getLeftExpression().toString()));
            String rightExpress = escapeQuote(expr.getRightExpression().toString());
            rightExpress = rightExpress.replaceAll("\\*", "%");
            if (Number.class.isAssignableFrom(path.getJavaType())) {
                return criteriaBuilder.like(path.as(String.class), rightExpress);
            } else {
                return criteriaBuilder.like(path, rightExpress);
            }

        }

        @Override
        public Predicate operate(final Path path, final CriteriaBuilder criteriaBuilder, final Object value) {
            return null;
        }
    },

    MinorThan {
        @Override
        public Predicate build(final Root root, final CriteriaBuilder criteriaBuilder, Expression expression) {
            return getBinaryExpressionPredicate(root, criteriaBuilder, (BinaryExpression) expression, this);

        }

        @Override
        public Predicate operate(final Path path, final CriteriaBuilder criteriaBuilder, final Object value) {
            if (value instanceof Instant) {
                return criteriaBuilder.lessThan(path, (Instant) value);
            } else {
                return criteriaBuilder.lessThan(path, value.toString());
            }
        }
    },
    MinorThanEquals {
        @Override
        public Predicate build(final Root root, final CriteriaBuilder criteriaBuilder, Expression expression) {
            return getBinaryExpressionPredicate(root, criteriaBuilder, (BinaryExpression) expression, this);

        }

        @Override
        public Predicate operate(final Path path, final CriteriaBuilder criteriaBuilder, final Object value) {
            if (value instanceof Instant) {
                return criteriaBuilder.lessThanOrEqualTo(path, (Instant) value);
            } else {
                return criteriaBuilder.lessThanOrEqualTo(path, value.toString());
            }
        }
    },

    NotEqualsTo {
        @Override
        public Predicate build(final Root root, final CriteriaBuilder criteriaBuilder, Expression expression) {
            return getBinaryExpressionPredicate(root, criteriaBuilder, (BinaryExpression) expression, this);

        }

        @Override
        public Predicate operate(final Path path, final CriteriaBuilder criteriaBuilder, final Object value) {
            if (value instanceof Instant) {
                return criteriaBuilder.notEqual(path, (Instant) value);
            } else {
                return criteriaBuilder.notEqual(path, value.toString());
            }
        }
    };

    private static Predicate getBinaryExpressionPredicate(final Root root, final CriteriaBuilder criteriaBuilder,
                                                          final BinaryExpression expression, final PredicateBuilder predicateBuilder) {
        BinaryExpression expr = expression;
        Predicate predicate;
        final Path path = root.get(escapeQuote(expr.getLeftExpression().toString()));
        Class type = path.getJavaType();
        if (isInstant(type)) {
            String rightExpression = escapeQuote(expr.getRightExpression().toString());
            Instant instant = getInstant(rightExpression);
            predicate = predicateBuilder.operate(path, criteriaBuilder, instant);
        } else {
            predicate = predicateBuilder.operate(path, criteriaBuilder, getExpressionValue(expr.getRightExpression()));
        }
        return predicate;
    }

    private static boolean isInstant(Class type) {
        return Instant.class.getTypeName().equals(type.getTypeName());
    }

    /**
     * escapeQuote
     *
     * @param escapedValue
     * @return
     */
    private static String escapeQuote(String escapedValue) {
        escapedValue = escapedValue.trim();
        // removing "'" or "\"" at the start and at the end
        if (escapedValue.startsWith("'") && escapedValue.endsWith("'")) {
            escapedValue = escapedValue.substring(1, escapedValue.length() - 1);
        } else if (escapedValue.startsWith("\"") && escapedValue.endsWith("\"")) {
            escapedValue = escapedValue.substring(1, escapedValue.length() - 1);
        }
        return escapedValue;

    }

    /**
     * time format: 2019-09-27 10:26 (yyyy-MM-dd HH:mm)
     */

    private static Instant getInstant(String rightExpression) {
        final String rightExp = escapeQuote(rightExpression);
        return DateTimeUtil.getInstant(rightExp);
    }

    private static Object getExpressionValue(Expression expression) {

        if (expression instanceof StringValue) {
            return escapeQuote(expression.toString());
        } else if (expression instanceof LongValue) {
            return ((LongValue) expression).getValue();
        } else if (expression instanceof DoubleValue) {
            return ((DoubleValue) expression).getValue();
        } else if (expression instanceof DateValue) {
            return ((DateValue) expression).getValue();
        } else if (expression instanceof TimeValue) {
            return ((TimeValue) expression).getValue();
        } else {
            return escapeQuote(expression.toString());
        }

    }

}
