package run.freshr.model;

import java.util.List;
import lombok.Builder;
import lombok.Data;

/**
 * DocsGroup 모델.
 *
 * @author FreshR
 * @apiNote DocsGroup Annotation 을 적용한 클래스의 정보를<br>
 * 가공하기 편리하도록 정의한 클래스
 * @since 2023. 1. 12. 오후 3:20:39
 */
@Data
@Builder
public class DocsGroupModel {

  /**
   * include 경로
   *
   * @apiNote 클래스 이름을 lower-hyphen 으로 변환한 데이터
   * @since 2023. 1. 12. 오후 3:20:39
   */
  private String path;
  /**
   * 이름
   *
   * @apiNote 이름<br>
   * 문서 파일 이름과 URI 도 해당 이름으로 생성
   * @since 2023. 1. 12. 오후 3:20:39
   */
  private String name;
  /**
   * 설명
   *
   * @apiNote 설명
   * @since 2023. 1. 12. 오후 3:20:39
   */
  private String description;
  /**
   * Test 목록
   *
   * @apiNote Test 목록
   * @since 2023. 1. 12. 오후 3:20:40
   */
  private List<DocsModel> docsList;

}
