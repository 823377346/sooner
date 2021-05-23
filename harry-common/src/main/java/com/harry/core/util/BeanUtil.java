package com.harry.core.util;

import com.harry.core.http.rest.errors.AlertException;
import com.google.common.base.Verify;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * Static convenience methods for JavaBeans: for instantiating beans, checking bean property types, copying bean
 * properties, etc.
 *
 * <p>Mainly for use within the framework, but to some degree also
 * useful for application classes.
 *
 * @author Tony Luo
 */
public final class BeanUtil extends BeanUtils {

  private BeanUtil() {
  }

  /**
   * Copy the property values of the given source bean into the target bean.
   * <p>Note: The source and target classes do not have to match or even be derived
   * from each other, as long as the properties match. Any bean properties that the source bean exposes but the target
   * bean does not will silently be ignored.
   * <p>This is just a convenience method. For more complex transfer needs,
   * consider using a full BeanWrapper.
   *
   * @param source                the source bean
   * @param target                the target bean
   * @param ignoreSourceNullValue ignore source null value property
   * @throws BeansException if the copying failed
   * @see BeanWrapper
   */
  public static void copyProperties(Object source, Object target, boolean ignoreSourceNullValue)
      throws BeansException {
    copyProperties(source, target, null, ignoreSourceNullValue, (String[]) null);
  }

  /**
   * Copy the property values of the given source bean into the given target bean, only setting properties defined in
   * the given "editable" class (or interface).
   * <p>Note: The source and target classes do not have to match or even be derived
   * from each other, as long as the properties match. Any bean properties that the source bean exposes but the target
   * bean does not will silently be ignored.
   * <p>This is just a convenience method. For more complex transfer needs,
   * consider using a full BeanWrapper.
   *
   * @param source                the source bean
   * @param target                the target bean
   * @param editable              the class (or interface) to restrict property setting to
   * @param ignoreSourceNullValue ignore source null value property
   * @throws BeansException if the copying failed
   * @see BeanWrapper
   */
  public static void copyProperties(Object source, Object target, Class<?> editable,
      boolean ignoreSourceNullValue) throws BeansException {
    copyProperties(source, target, editable, ignoreSourceNullValue, (String[]) null);
  }

  /**
   * Copy the property values of the given source bean into the given target bean, ignoring the given
   * "ignoreProperties".
   * <p>Note: The source and target classes do not have to match or even be derived
   * from each other, as long as the properties match. Any bean properties that the source bean exposes but the target
   * bean does not will silently be ignored.
   * <p>This is just a convenience method. For more complex transfer needs,
   * consider using a full BeanWrapper.
   *
   * @param source                the source bean
   * @param target                the target bean
   * @param ignoreSourceNullValue ignore source null value property
   * @param ignoreProperties      array of property names to ignore
   * @throws BeansException if the copying failed
   * @see BeanWrapper
   */
  public static void copyProperties(Object source, Object target, boolean ignoreSourceNullValue,
      String... ignoreProperties) throws BeansException {
    copyProperties(source, target, null, ignoreSourceNullValue, ignoreProperties);
  }

  /**
   * Copy the property values of the given source bean into the given target bean.
   * <p>Note: The source and target classes do not have to match or even be derived
   * from each other, as long as the properties match. Any bean properties that the source bean exposes but the target
   * bean does not will silently be ignored.
   *
   * @param source                the source bean
   * @param target                the target bean
   * @param editable              the class (or interface) to restrict property setting to
   * @param ignoreSourceNullValue ignore source null value property
   * @param ignoreProperties      array of property names to ignore
   * @throws BeansException if the copying failed
   * @see BeanWrapper
   */
  private static void copyProperties(Object source, Object target, @Nullable Class<?> editable,
      boolean ignoreSourceNullValue,
      @Nullable String... ignoreProperties) throws BeansException {

    Assert.notNull(source, "Source must not be null");
    Assert.notNull(target, "Target must not be null");

    Class<?> actualEditable = target.getClass();
    if (editable != null) {
      if (!editable.isInstance(target)) {
        throw new IllegalArgumentException("Target class [" + target.getClass().getName() +
            "] not assignable to Editable class [" + editable.getName() + "]");
      }
      actualEditable = editable;
    }
    PropertyDescriptor[] targetPds = getPropertyDescriptors(actualEditable);
    List<String> ignoreList = (ignoreProperties != null ? Arrays.asList(ignoreProperties)
        : null);

    for (PropertyDescriptor targetPd : targetPds) {
      Method writeMethod = targetPd.getWriteMethod();
      if (writeMethod != null && (ignoreList == null || !ignoreList
          .contains(targetPd.getName()))) {
        PropertyDescriptor sourcePd = getPropertyDescriptor(source.getClass(),
            targetPd.getName());
        if (sourcePd != null) {
          Method readMethod = sourcePd.getReadMethod();
          if (readMethod != null &&
              ClassUtils.isAssignable(writeMethod.getParameterTypes()[0],
                  readMethod.getReturnType())) {
            try {
              if (!Modifier.isPublic(readMethod.getDeclaringClass().getModifiers())) {
                readMethod.setAccessible(true);
              }
              Object value = readMethod.invoke(source);

              if (!(ignoreSourceNullValue && value == null)) {
                if (!Modifier
                    .isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                  writeMethod.setAccessible(true);
                }
                writeMethod.invoke(target, value);
              }

            } catch (Throwable ex) {
              throw new FatalBeanException(
                  "Could not copy property '" + targetPd.getName()
                      + "' from source to target", ex);
            }
          }
        }
      }
    }
  }


  /**
   * Ensures that an object reference passed as a parameter to the calling method is not null.
   *
   * @param reference    an object reference
   * @param defaultErrorMessage the exception message to use if the check fails; will be converted to a string using {@link
   *                     String#valueOf(Object)}
   * @return the non-null reference that was validated
   * @throws AlertException if {@code reference} is null
   * @see Verify#verifyNotNull Verify.verifyNotNull()
   */
  public static <T extends Object> T checkNotNull(final T reference, final String defaultErrorMessage, final String errorMsgKey,
      Object... errorMsgKeyParams) {
    if (reference == null) {
      AlertException.throwException(defaultErrorMessage, errorMsgKey, errorMsgKeyParams);
    }
    return reference;
  }

  public static <T extends Object> T checkNotNull(final T reference, final String defaultErrorMessage, final String errorMsgKey) {

    return checkNotNull(reference, defaultErrorMessage, errorMsgKey, null);
  }

  /**
   * Ensures that an object reference passed as a parameter to the calling method is not null.
   *
   * @param reference    an object reference
   * @param errorMessage the exception message to use if the check fails; will be converted to a string using {@link
   *                     String#valueOf(Object)}
   * @return the non-null reference that was validated
   * @throws AlertException if {@code reference} is null
   * @see Verify#verifyNotNull Verify.verifyNotNull()
   */
  public static <T extends Object> T checkNotNull(final T reference, final String errorMessage) {
    return checkNotNull(reference, errorMessage, null, null);

  }

  /**
   * Ensures the truth of an expression involving one or more parameters to the calling method.
   *
   * @param expression   a boolean expression
   * @param defaultErrorMessage the exception message to use if the check fails; will be converted to a string using {@link
   *                     String#valueOf(Object)}
   * @throws IllegalArgumentException if {@code expression} is false
   */
  public static void checkArgument(final boolean expression, final String defaultErrorMessage, final String errorMsgKey,
      Object... errorMsgKeyParams) {
    if (!expression) {
      AlertException.throwException(defaultErrorMessage, errorMsgKey, errorMsgKeyParams);
    }
  }

  public static void checkArgument(final boolean expression, final String defaultErrorMessage, final String errorMsgKey) {
    checkArgument(expression, defaultErrorMessage, errorMsgKey, null);
  }

  /**
   * Ensures the truth of an expression involving one or more parameters to the calling method.
   *
   * @param expression   a boolean expression
   * @param errorMessage the exception message to use if the check fails; will be converted to a string using {@link
   *                     String#valueOf(Object)}
   * @throws IllegalArgumentException if {@code expression} is false
   */
  public static void checkArgument(final boolean expression, final String errorMessage) {
    checkArgument(expression, errorMessage, null, null);
  }


}
