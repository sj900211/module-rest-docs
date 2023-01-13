package run.freshr.processor;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_HYPHEN;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static java.lang.System.lineSeparator;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.util.Objects.isNull;
import static java.util.Optional.ofNullable;
import static javax.lang.model.element.ElementKind.CLASS;
import static javax.tools.Diagnostic.Kind.ERROR;
import static javax.tools.Diagnostic.Kind.NOTE;
import static org.springframework.util.StringUtils.hasLength;

import com.google.auto.service.AutoService;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.StandardLocation;
import org.junit.jupiter.api.DisplayName;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import run.freshr.annotation.Docs;
import run.freshr.annotation.DocsGroup;
import run.freshr.annotation.DocsPopup;
import run.freshr.model.DocsGroupModel;
import run.freshr.model.DocsGroupModel.DocsGroupModelBuilder;
import run.freshr.model.DocsModel;

/**
 * Process
 *
 * @author FreshR
 * @apiNote Test Compile 에서 동작할 Process 정의<br>
 * 해당 프로젝트는 Test 코드를 기반으로 동작하기 때문에 Test Compile 에서 동작하도록 설정해야 한다.
 * @since 2023. 1. 12. 오후 2:59:33
 */
@AutoService(Processor.class)
public class RestDocsProcessor extends AbstractProcessor {

  /**
   * 지원 Annotation 유형 설정
   *
   * @return the supported annotation types
   * @apiNote 지원 Annotation 유형 설정
   * @author FreshR
   * @since 2023. 1. 12. 오후 3:24:32
   */
  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return Set.of(DocsGroup.class.getName());
  }

  /**
   * 지원 소스 버전 설정
   *
   * @return the supported source version
   * @apiNote 지원 소스 버전 설정
   * @author FreshR
   * @since 2023. 1. 12. 오후 3:24:32
   */
  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }

  /**
   * Process
   *
   * @param annotations the annotations
   * @param roundEnv    the round env
   * @return the boolean
   * @apiNote Test Compile 에서 동작할 로직 정의
   * @author FreshR
   * @since 2023. 1. 12. 오후 3:24:32
   */
  @Override
  public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
    try {
      if (annotations.isEmpty()) {
        return false;
      }

      processingEnv.getMessager().printMessage(NOTE, "RestDocsProcessor.process");

      Filer filer = processingEnv.getFiler();
      /*
       * TODO: gradle 빌드에서 빌드 경로를 찾기 위해서 아래와 같은 코드를 작성
       * 임시 파일을 생성해서 경로를 찾아가는데...
       * 해당 방법 말고 더 세련된 방법을 찾아야 함
       */
      FileObject CLASS_OUTPUT_TEMP = filer.createResource(StandardLocation.CLASS_OUTPUT,
          "", "temp", (Element[]) null);
      Path ROOT_PATH = Paths.get(CLASS_OUTPUT_TEMP.toUri())
          .getParent().getParent().getParent().getParent().getParent();
      // AsciiDoc 문서를 생성할 Path
      Path DOCS_PATH = ROOT_PATH.resolve(Path.of("src", "docs", "asciidoc"));

      // TODO: 임시파일 삭제...
      CLASS_OUTPUT_TEMP.delete();

      // AsciiDoc 문서를 생성할 Directory 가 없다면 생성
      if (!Files.isDirectory(DOCS_PATH)) {
        Files.createDirectories(DOCS_PATH);
      }

      /*
       * index 문서는 프로젝트에서 자유롭게 수정 가능하도록
       * src/main/resources/asciidocs/default-bottom.adoc
       * src/main/resources/asciidocs/default-top.adoc
       * src/main/resources/asciidocs/index.adoc
       * src/main/resources/asciidocs/popup-bottom.adoc
       * src/main/resources/asciidocs/popup-top.adoc
       * 파일을 읽어오도록 설정
       */
      Path projectDocsPath = Path.of("src", "main", "resources", "asciidocs");
      Path customIndexPath = ROOT_PATH
          .resolve(projectDocsPath.resolve("index.adoc"));
      Path customDefaultTopPath = ROOT_PATH
          .resolve(projectDocsPath.resolve("default-top.adoc"));
      Path customDefaultBottomPath = ROOT_PATH
          .resolve(projectDocsPath.resolve("default-bottom.adoc"));
      Path customPopupTopPath = ROOT_PATH
          .resolve(projectDocsPath.resolve("popup-top.adoc"));
      Path customPopupBottomPath = ROOT_PATH
          .resolve(projectDocsPath.resolve("popup-bottom.adoc"));
      // 프로젝트에 *.adoc 문서가 없다면 해당 모듈에 있는 *.adoc 으로 대체
      byte[] indexByte = readDoc(customIndexPath,
          "asciidocs/index.adoc");
      byte[] defaultTopByte = readDoc(customDefaultTopPath,
          "asciidocs/default-top.adoc");
      byte[] defaultBottomByte = readDoc(customDefaultBottomPath,
          "asciidocs/default-bottom.adoc");
      byte[] popupTopByte = readDoc(customPopupTopPath,
          "asciidocs/popup-top.adoc");
      byte[] popupBottomByte = readDoc(customPopupBottomPath,
          "asciidocs/popup-bottom.adoc");

      String indexContents = new String(indexByte, UTF_8);
      String defaultTopContents = new String(defaultTopByte, UTF_8);
      String defaultBottomContents = new String(defaultBottomByte, UTF_8);
      String popupTopContents = new String(popupTopByte, UTF_8);
      String popupBottomContents = new String(popupBottomByte, UTF_8);
      StringBuilder indexBuilder = new StringBuilder();
      StringBuilder defaultTopBuilder = new StringBuilder();
      StringBuilder defaultBottomBuilder = new StringBuilder();
      StringBuilder popupTopBuilder = new StringBuilder();
      StringBuilder popupBottomBuilder = new StringBuilder();
      defaultTopBuilder.append(defaultTopContents).append(lineSeparator());
      defaultBottomBuilder.append(defaultBottomContents);
      popupTopBuilder.append(popupTopContents);
      popupBottomBuilder.append(popupBottomContents);

      List<DocsGroupModel> docsGroupList = new ArrayList<>();
      List<DocsPopup> popupList = new ArrayList<>();

      /*
       * DcosGroup 과 Docs Annotation 정보를
       * DocsGroupModel 과 DocsModel 로 변환
       */
      for (Element element : roundEnv.getRootElements()) {
        if (element.getKind() != CLASS) {
          continue;
        }

        DocsGroup group = element.getAnnotation(DocsGroup.class);

        if (isNull(group)) {
          continue;
        }

        String className = UPPER_CAMEL.to(LOWER_HYPHEN, element.getSimpleName().toString());
        String filename = group.name();
        DocsGroupModelBuilder groupBuilder = DocsGroupModel.builder()
            .name(filename)
            .description(ofNullable(group.description()).orElse(filename))
            .path(className);
        List<DocsModel> docsList = new ArrayList<>();
        List<? extends Element> testList = element.getEnclosedElements()
            .stream()
            .filter(item -> !isNull(item.getAnnotation(Docs.class)))
            .toList();

        for (Element method : testList) {
          Docs docs = method.getAnnotation(Docs.class);

          if (isNull(docs)) {
            continue;
          }

          String methodName = LOWER_CAMEL.to(LOWER_HYPHEN, method.getSimpleName().toString());

          DisplayName displayName = method.getAnnotation(DisplayName.class);
          String description = !isNull(displayName) ? displayName.value() :
              methodName;
          DocsModel docsModel = DocsModel.builder()
              .path(methodName)
              .description(description)
              .existsPathParameters(docs.existsPathParameters())
              .existsRequestParts(docs.existsRequestParts())
              .existsRequestParameters(docs.existsRequestParameters())
              .existsRequestFields(docs.existsRequestFields())
              .existsResponseFields(docs.existsResponseFields())
              .build();

          docsList.add(docsModel);

          DocsPopup[] popups = docs.popup();

          if (!isNull(popups) && popups.length > 0) {
            popupList.addAll(List.of(popups));
          }
        }

        DocsGroupModel groupModel = groupBuilder.docsList(docsList).build();

        docsGroupList.add(groupModel);
      }

      // 변환한 정보로 navigation 생성
      StringBuilder navBuilder = new StringBuilder();

      navBuilder.append("== 메뉴").append(lineSeparator())
          .append("=== link:index[Home]").append(lineSeparator());

      for (DocsGroupModel groupModel : docsGroupList) {
        navBuilder.append("=== link:")
            .append(groupModel.getName())
            .append("[").append(groupModel.getDescription()).append("]")
            .append(lineSeparator());
      }

      // 문서의 타이틀등 상단 영역 작성
      if (!docsGroupList.isEmpty()) {
        Path indexPath = DOCS_PATH.resolve("index.adoc");

        indexBuilder.append(defaultTopBuilder);
        indexBuilder.append("= Rest Document").append(lineSeparator());
        indexBuilder.append(lineSeparator());
        indexBuilder.append(navBuilder).append(lineSeparator());
        indexBuilder.append(indexContents).append(lineSeparator());
        indexBuilder.append(defaultBottomBuilder);

        if (!Files.exists(indexPath)) {
          Path indexDocs = Files.createFile(indexPath);

          Files.writeString(indexDocs, indexBuilder.toString());
        }
      }

      // 문서의 내용을 작성
      for (DocsGroupModel groupModel : docsGroupList) {
        if (isNull(groupModel)) {
          continue;
        }

        if (isNull(groupModel.getDocsList())) {
          continue;
        }

        String groupPath = groupModel.getPath();
        Path documentPath = DOCS_PATH
            .resolve(ofNullable(groupModel.getName()).orElse(groupPath) + ".adoc");
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(defaultTopBuilder)
            .append("= ").append(groupModel.getDescription()).append(lineSeparator())
            .append(lineSeparator())
            .append(navBuilder).append(lineSeparator())
            .append("== ").append(groupModel.getDescription()).append(lineSeparator());

        List<DocsModel> docsList = groupModel.getDocsList();

        for (DocsModel docsModel : docsList) {
          String include = "include::{snippets}/" + groupPath + "/" + docsModel.getPath() + "/";

          stringBuilder
              .append("[[").append(docsModel.getPath()).append("]]").append(lineSeparator())
              .append("=== ")
              .append(ofNullable(docsModel.getDescription()).orElse(docsModel.getPath()))
              .append(lineSeparator())
              .append(".fold").append(lineSeparator())
              .append("[%collapsible]").append(lineSeparator())
              .append("====").append(lineSeparator());

          if (docsModel.getExistsPathParameters()) {
            stringBuilder.append(include)
                .append("path-parameters.adoc[]")
                .append(lineSeparator()).append(lineSeparator());
          }

          if (docsModel.getExistsRequestParts()) {
            stringBuilder.append(include)
                .append("request-parts.adoc[]")
                .append(lineSeparator()).append(lineSeparator());
          }

          if (docsModel.getExistsRequestParameters()) {
            stringBuilder.append(include)
                .append("request-parameters.adoc[]")
                .append(lineSeparator()).append(lineSeparator());
          }

          if (docsModel.getExistsRequestFields()) {
            stringBuilder.append(include)
                .append("request-fields.adoc[]")
                .append(lineSeparator()).append(lineSeparator());
          }

          if (docsModel.getExistsResponseFields()) {
            stringBuilder.append(include)
                .append("response-fields.adoc[]")
                .append(lineSeparator()).append(lineSeparator());
          }

          stringBuilder.append("Request HTTP Example").append(lineSeparator())
              .append(include).append("http-request.adoc[]").append(lineSeparator())
              .append(lineSeparator())
              .append("Response HTTP Example").append(lineSeparator())
              .append(include).append("http-response.adoc[]").append(lineSeparator())
              .append(lineSeparator())
              .append("Curl Example").append(lineSeparator())
              .append(include).append("curl-request.adoc[]").append(lineSeparator())
              .append(lineSeparator())
              .append("====").append(lineSeparator())
              .append("'''")
              .append(lineSeparator())
              .append(lineSeparator());
        }

        stringBuilder.append(defaultBottomBuilder);

        // 파일 생성
        if (!Files.exists(documentPath)) {
          Path documentDocs = Files.createFile(documentPath);

          Files.writeString(documentDocs, stringBuilder.toString());
        }
      }

      // 팝업 파일 생성
      for (DocsPopup popup : popupList) {
        if (!hasLength(popup.name())) {
          processingEnv.getMessager().printMessage(ERROR, "Not found popup name");
          continue;
        }

        if (!hasLength(popup.include())) {
          processingEnv.getMessager().printMessage(ERROR, "Not found popup include path");
          continue;
        }

        Path documentPath = DOCS_PATH.resolve("popup-" + popup.name() + ".adoc");
        StringBuilder stringBuilder = new StringBuilder();

        stringBuilder.append(popupTopBuilder).append(popup.include()).append("[]")
            .append(lineSeparator())
            .append(popupBottomBuilder);

        // 파일 생성
        if (!Files.exists(documentPath)) {
          Path documentDocs = Files.createFile(documentPath);

          Files.writeString(documentDocs, stringBuilder.toString());
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return true;
  }

  private byte[] readDoc(Path path, String resource) throws IOException {
    byte[] bytes;

    if (Files.exists(path)) {
      bytes = Files.readAllBytes(path);
    } else {
      ClassPathResource indexResource = new ClassPathResource(resource,
          this.getClass().getClassLoader());
      InputStream indexStream = indexResource.getInputStream();
      bytes = FileCopyUtils.copyToByteArray(indexStream);
    }

    return bytes;
  }

}
