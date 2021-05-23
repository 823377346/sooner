package com.harry.core.http.rest.errors;//package com.harry.weathertask.core.http.rest.errors;
//
//import java.util.HashMap;
//import java.util.Map;
//import org.zalando.problem.AbstractThrowableProblem;
//import org.zalando.problem.Status;
//
///**
// * @author Tony Luo
// */
//public class MsgException extends AbstractThrowableProblem {
//
//  private static final long serialVersionUID = 1L;
//
//
//  private final String errorKey;
//
//  private Object[] errorParams;
//
//  public MsgException(String defaultMessage, String msgKey) {
//    this(defaultMessage, msgKey, null);
//  }
//
//
//  public MsgException(String defaultMessage, String errorKey, Object... errorParams) {
//    super(ErrorConstants.DEFAULT_TYPE, defaultMessage, Status.BAD_REQUEST, null, null, null,
//        getAlertParameters(errorKey));
//    this.errorKey = errorKey;
//    if (errorParams != null) {
//      this.errorParams = errorParams;
//    }
//  }
//
//  public static MsgException throwException(String defaultMessage, String msgKey) {
//    throw new MsgException(defaultMessage, msgKey);
//  }
//
//
//  public static MsgException throwException(String defaultMessage, String msgKey, Object... errorParams) {
//    throw new MsgException(defaultMessage, msgKey, errorParams);
//  }
//
//
//  public String getErrorKey() {
//    return errorKey;
//  }
//
//  public MsgException setErrorParams(Object... errorParams) {
//    this.errorParams = errorParams;
//    return this;
//  }
//
//  public Object[] getErrorParams() {
//    return errorParams;
//  }
//
//  private static Map<String, Object> getAlertParameters(String errorKey) {
//    Map<String, Object> parameters = new HashMap<>(16);
//    parameters.put("errMsgKey", "msg." + errorKey);
//    return parameters;
//  }
//
//}
