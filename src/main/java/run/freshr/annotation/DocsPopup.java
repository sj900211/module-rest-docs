package run.freshr.annotation;

import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * DocsPopup Annotation.
 *
 * @author FreshR
 * @apiNote 팝업 문서 관리 Annotation
 * @since 2023. 1. 13. 오후 2:36:19
 */
@Target({})
@Retention(SOURCE)
public @interface DocsPopup {

  /**
   * 팝업 파일 이름.
   *
   * @return string
   * @apiNote 팝업 파일 이름
   * @author FreshR
   * @since 2023. 1. 13. 오후 2:36:19
   */
  String name();

  /**
   * Include 팝업 내용 파일 경로.
   *
   * @return string
   * @apiNote Include 팝업 내용 파일 경로
   * @author FreshR
   * @since 2023. 1. 13. 오후 2:36:19
   */
  String include();

}
