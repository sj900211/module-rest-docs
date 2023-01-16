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
   * Path Parameters 문서 정의 여부
   *
   * @return the boolean
   * @apiNote Path Parameters 문서 정의 여부
   * @author FreshR
   * @since 2023. 1. 12. 오후 3:14:18
   */
  boolean existsPathParameters() default false;

  /**
   * Request Parts 문서 정의 여부
   *
   * @return the boolean
   * @apiNote Request Parts 문서 정의 여부
   * @author FreshR
   * @since 2023. 1. 12. 오후 3:14:18
   */
  boolean existsRequestParts() default false;

  /**
   * Query Parameters 문서 정의 여부
   *
   * @return the boolean
   * @apiNote Query Parameters 문서 정의 여부
   * @author FreshR
   * @since 2023. 1. 12. 오후 3:14:18
   */
  boolean existsQueryParameters() default false;

  /**
   * Form Parameters 문서 정의 여부
   *
   * @return the boolean
   * @apiNote Form Parameters 문서 정의 여부
   * @author FreshR
   * @since 2023. 1. 12. 오후 3:14:18
   */
  boolean existsFormParameters() default false;

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

  DocsPopup[] popup() default {};

}
