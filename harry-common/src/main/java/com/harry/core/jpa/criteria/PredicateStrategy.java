package com.harry.core.jpa.criteria;

import net.sf.jsqlparser.expression.Expression;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

/**
 * PredicateStrategy
 *
 * @author Tony Luo 2019-09-25
 */
public interface PredicateStrategy<T> {
    /**
     * PredicateStrategy
     *
     * @param root
     * @param builder
     * @param expression
     * @return
     */
    Predicate build(final Root<T> root, final CriteriaBuilder builder, final Expression expression);

    /**
     * Binary operate
     *
     * @param path
     * @param builder
     * @param value
     * @return
     */
    Predicate operate(final Path<T> path, final CriteriaBuilder builder, final Object value);
}
