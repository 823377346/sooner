package com.harry.core.annotation;

import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * DistributedLock
 *
 * @author Tony Luo
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DistLock {
    // By default lock watchdog timeout is 30 seconds and can be changed through Config.lockWatchdogTimeout setting.

    /**
     * Alias for the {@link #key} attribute.
     * <p>Allows for more concise annotation declarations &mdash; for example,
     * {@code @DistLock("org.my.key")} is equivalent to
     * {@code @DistLock(key = "org.my.key")}.
     * @since 1.0
     * @see #key
     */
    @AliasFor("key")
    String value() default "";

    /**
     * Distributed lock key, support SpEL
     * <p>Distributed lock key that used by redission Distributed lock &mdash; for example,
     * {@code @DistLock(key = "org.my.key")}
     * <p>{@link #value} is an alias for this attribute, simply allowing for
     * more concise use of the annotation.
     * @since 1.0
     */
    @AliasFor("value")
    String key() default "";

    /**
     * Default key prefix is method signature;
     * keyPrefix must be set while several methods share the same lock key
     */
    String keyPrefix() default "";

    /**
     * @return
     */
    LockType type() default LockType.unFairLock;

    /**
     * Returns <code>true</code> as soon as the lock is acquired.
     * If the lock is currently held by another thread in this or any
     * other process in the distributed system this method keeps trying
     * to acquire the lock for up to <code>waitTime</code> before
     * giving up and returning <code>false</code>. If the lock is acquired,
     * it is held until <code>unlock</code> is invoked, or until <code>leaseTime</code>
     * have passed since the lock was granted - whichever comes first.
     *
     */
    boolean tryLock() default false;

    /**
     * leaseTime
     * the maximum time to hold the lock after granting it, before automatically releasing
     * it if it hasn't already been released by invoking <code>unlock</code>. If timeout is -1,
     * hold the lock until explicitly unlocked.
     */
    long leaseTime() default -1;


    /**
     * waitTime the maximum time to aquire the lock
     * If the lock is currently held by another thread in this or any
     * other process in the distributed system this method keeps trying
     * to acquire the lock for up to <code>waitTime</code> before
     * giving up and returning <code>false</code>. If the lock is acquired,
     * it is held until <code>unlock</code> is invoked, or until <code>leaseTime</code>
     * have passed since the lock was granted - whichever comes first.
     */
    long waitTime() default 5;




    // 分类
    enum LockType {



        /**
         * 公平锁
         */
        fairLock,
        /**
         * 公平锁
         */
        unFairLock;

    }

}
