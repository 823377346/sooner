package com.harry.core.http;


import io.swagger.annotations.ApiModelProperty;
//import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;

import java.util.Optional;

@lombok.ToString
public class ResultEntity<T> {


    @ApiModelProperty(value = "返回状态信息")
    private final Object status;

    @Nullable
    @ApiModelProperty(value = "返回具体内容")
    private final T result;

//    private final HttpHeaders headers;

    private Integer errcode;
    private String errmsg;

    /**
     * Create a new {@code ResultEntity} with the given status status, and no result.
     *
     * @param status the status status
     */
    public ResultEntity(ResultStatus status) {
        this(null, status);
    }

    /**
     * Create a new {@code HttpEntity} with the given headers and no body.
     *
     * @param headers the entity headers
     */
    public ResultEntity(MultiValueMap<String, String> headers) {
        this(null, headers);
    }

    /**
     * Create a new {@code ResultEntity} with the given body and headers.
     *
     * @param result  the entity body
     * @param headers the entity headers
     */
    public ResultEntity(@Nullable T result, Object status) {
        this.result = result;
        this.status = status;
    }
//
//
//    /**
//     * Returns the headers of this entity.
//     */
//    public HttpHeaders getHeaders() {
//        return this.headers;
//    }

    /**
     * Return the  status of the response.
     *
     * @return the  status as an ResultStatus enumconstant entry
     */
    public String getErrmsg() {
        if (errmsg != null) {
            return errmsg;
        }
        if (this.status instanceof ResultStatus) {
            return ((ResultStatus) this.status).getReasonPhrase();
        } else {
            return ResultStatus.valueOf((Integer) this.status).getReasonPhrase();
        }
    }


    /**
     * Return the HTTP status status of the response.
     *
     * @return the HTTP status as an int value
     * @since 1.0.0
     */
    public int getErrcode() {
        if (errcode != null) {
            return errcode;
        }
        if (this.status instanceof ResultStatus) {
            return ((ResultStatus) this.status).value();
        } else {
            return (Integer) this.status;
        }
    }

    /**
     * Returns the result of this entity.
     */
    @Nullable
    public T getResult() {
        return this.result;
    }

    /**
     * Indicates whether this entity has a result.
     */
    public boolean hasResult() {
        return (this.result != null);
    }

    /**
     * Create a builder with the given status.
     *
     * @param status the response status
     * @return the created builder
     * @since 1.0.0
     */
    public static ResultBuilder status(ResultStatus status) {
        Assert.notNull(status, "ResultStatus must not be null");
        return new DefaultBuilder(status);
    }


    /**
     * Wrap the optional into a {@link ResultStatus} with an {@link ResultStatus#OK} status, or if it's empty, it
     * returns a {@link ResultEntity} with {@link ResultStatus#NOT_FOUND}.
     *
     * @param <X>           type of the response
     * @param maybeResponse response to return if present
     * @return response containing {@code maybeResponse} if present or {@link ResultStatus#NOT_FOUND}
     */
    public static <X> ResultEntity<X> wrapOrNotFound(Optional<X> maybeResponse) {
        return maybeResponse.map(response -> ResultEntity.ok(response))
                .orElse(ResultEntity.status(ResultStatus.NOT_FOUND).result(null));
    }


    /**
     * Create a builder with the given status.
     *
     * @param status the response status
     * @return the created builder
     * @since 1.0.0
     */
//    public static ResultBuilder status(int status) {
//        return new DefaultBuilder(status);
//    }


    /**
     * Create a builder with the status set to {@linkplain ResultStatus#OK OK}.
     *
     * @return the created builder
     * @since 1.0.0
     */
    public static ResultBuilder ok() {
        return status(ResultStatus.OK);
    }


    /**
     * A shortcut for creating a {@code ResultEntity} with the given body and
     * the status set to {@linkplain ResultStatus#OK OK}.
     *
     * @return the created {@code ResultEntity}
     * @since 1.0.0
     */
    public static <T> ResultEntity<T> ok(T result) {
        ResultBuilder builder = ok();
        return builder.result(result);
    }




    /**
     * Wrap the optional into a {@link ResultStatus} with an {@link ResultStatus#OK} status, or if it's empty, it
     * returns a {@link ResultEntity} with {@link ResultStatus#NOT_FOUND}.
     *
     * @param <X>           type of the response
     * @param maybeResponse response to return if present
     * @return response containing {@code maybeResponse} if present or {@link ResultStatus#NOT_FOUND}
     */
    public static <X> ResultEntity<X> ok(Optional<X> maybeResponse) {
        return wrapOrNotFound(maybeResponse);
    }

    /**
     * Create a builder with the status set to {@linkplain ResultStatus#ALERT ALERT}.
     *
     * @return the created builder
     * @since 1.0.0
     */
    public static ResultBuilder alert() {
        return status(ResultStatus.ALERT);
    }


    /**
     * A shortcut for creating a {@code ResultEntity} with the given body and
     * the status set to {@linkplain ResultStatus#ALERT ALERT}.
     *
     * @return the created {@code ResultEntity}
     * @since 1.0.0
     */
    public static <T> ResultEntity<T> alert(T result) {
        ResultBuilder builder = alert();
        return builder.result(result);
    }

    public static <T> ResultEntity<T> alert(T result,String errMsg) {
        ResultBuilder builder = alert();
        return builder.result(result);
    }

    /**
     * A shortcut for creating a {@code ResultEntity} with the given body and
     * the status set to {@linkplain ResultStatus#OK OK}.
     *
     * @return the created {@code ResultEntity}
     * @since 1.0.0
     */
    public static ResultEntity<String> success() {

        return ok("SUCCESS");
    }

    /**
     * Defines a builder that adds a body to the response entity.
     *
     * @since 1.0.0
     */
    public interface ResultBuilder {

        /**
         * Set the body of the response entity and returns it.
         *
         * @param <T>    the type of the body
         * @param result the body of the response entity
         * @return the built response entity
         */
        <T> ResultEntity<T> result(@Nullable T result);


        <T> ResultEntity<T> build();


    }

    private static class DefaultBuilder implements ResultBuilder {

        private final Object status;


        public DefaultBuilder(Object status) {
            this.status = status;

        }


        @Override
        public <T> ResultEntity<T> build() {
            return result(null);
        }

        @Override
        public <T> ResultEntity<T> result(@Nullable T result) {
            return new ResultEntity<>(result, this.status);
        }




    }
}
