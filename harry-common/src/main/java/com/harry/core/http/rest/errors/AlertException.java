package com.harry.core.http.rest.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Tony Luo
 */
public class AlertException extends AbstractThrowableProblem {

    private static final long serialVersionUID = 1L;

    private final String entityName;

    private final String errorKey;

    private Object[] errorParams;

    public static AlertException throwException(String defaultMessage, String msgKey) {
        throw new AlertException(defaultMessage, msgKey);
    }

    public AlertException(String defaultMessage, String msgKey) {
        this(defaultMessage, null, msgKey);
    }

    public static AlertException throwException(String defaultMessage, String msgKey, Object... errorParams) {
        throw new AlertException(defaultMessage, msgKey, errorParams);
    }

    public AlertException(String defaultMessage, String msgKey, Object... errorParams) {
        this(defaultMessage, null, msgKey, errorParams);
    }

    public static AlertException throwException(String message, String entityName, String msgKey) {
        throw new AlertException(message, entityName, msgKey);
    }

    public AlertException(String message, String entityName, String msgKey) {
        this(ErrorConstants.DEFAULT_TYPE, message, entityName, msgKey);
    }

    public static AlertException throwException(String message, String entityName, String msgKey, Object... errorParams) {
        throw new AlertException(message, entityName, msgKey, errorParams);
    }

    public AlertException(String message, String entityName, String msgKey, Object... errorParams) {
        this(ErrorConstants.DEFAULT_TYPE, message, entityName, msgKey, errorParams);
    }

    public static AlertException throwException(URI type, String defaultMessage, String entityName, String errorKey) {
        throw new AlertException(type, defaultMessage, entityName, errorKey);
    }

    public AlertException(URI type, String defaultMessage, String entityName, String errorKey) {

        this(type, defaultMessage, entityName, errorKey, null);
    }

    public static AlertException throwException(URI type, String defaultMessage, String entityName, String errorKey,
                                                Object... errorParams) {
        throw new AlertException(type, defaultMessage, entityName, errorKey, errorParams);

    }

    public AlertException(URI type, String defaultMessage, String entityName, String errorKey,
                          Object... errorParams) {
        super(type, defaultMessage, Status.BAD_REQUEST, null, null, null,
                getAlertParameters(entityName, errorKey));
        this.entityName = entityName;
        this.errorKey = errorKey;
        if (errorParams != null) {
            this.errorParams = errorParams;
        }
    }

    public String getEntityName() {
        return entityName;
    }

    public String getErrorKey() {
        return errorKey;
    }

    public AlertException setErrorParams(Object... errorParams) {
        this.errorParams = errorParams;
        return this;
    }

    public Object[] getErrorParams() {
        return errorParams;
    }

    private static Map<String, Object> getAlertParameters(String entityName, String errorKey) {
        Map<String, Object> parameters = new HashMap<>(16);
        parameters.put("errMsgKey", "alert." + errorKey);
        parameters.put("params", entityName);
        return parameters;
    }
}
