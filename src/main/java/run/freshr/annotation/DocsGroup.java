package run.freshr.annotation;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * DocsGroup Annotation.
 *
 * @author FreshR
 * @apiNote Test 클래스에 적용할 Annotation
 * @since 2023. 1. 12. 오후 3:16:08
 */
@Target(TYPE)
@Retention(SOURCE)
public @interface DocsGroup {

  /**
   * 파일 이름
   *
   * @return the string
   * @apiNote 문서의 이름<br>
   * 문서 파일 이름과 URI 도 해당 이름으로 생성되니 영어로 작성
   * @author FreshR
   * @since 2023. 1. 12. 오후 3:16:08
   */
  String name() default "";

}
