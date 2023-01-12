package run.freshr.annotation;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Docs Annotation.
 *
 * @author FreshR
 * @apiNote 단위 Test 에 적용할 Annotation
 * @since 2023. 1. 12. 오후 3:14:18
 */
@Target(METHOD)
@Retention(SOURCE)
public @interface Docs {

  /**
   * Path Parameter 문서 정의 여부
   *
   * @return the boolean
   * @apiNote Path Parameter 문서 정의 여부
   * @author FreshR
   * @since 2023. 1. 12. 오후 3:14:18
   */
  boolean existsPathParameters() default false;

  /**
   * Request Part 문서 정의 여부
   *
   * @return the boolean
   * @apiNote Request Part 문서 정의 여부
   * @author FreshR
   * @since 2023. 1. 12. 오후 3:14:18
   */
  boolean existsRequestParts() default false;

  /**
   * Request Parameter 문서 정의 여부
   *
   * @return the boolean
   * @apiNote Request Parameter 문서 정의 여부
   * @author FreshR
   * @since 2023. 1. 12. 오후 3:14:18
   */
  boolean existsRequestParameters() default false;

  /**
   * Request Fields 문서 정의 여부
   *
   * @return the boolean
   * @apiNote Request Fields 문서 정의 여부
   * @author FreshR
   * @since 2023. 1. 12. 오후 3:14:18
   */
  boolean existsRequestFields() default false;

  /**
   * Response Fields 문서 정의 여부
   *
   * @return the boolean
   * @apiNote Response Fields 문서 정의 여부
   * @author FreshR
   * @since 2023. 1. 12. 오후 3:14:18
   */
  boolean existsResponseFields() default false;

}
