package raisetech.StudentManagement.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.util.List;
import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.service.StudentService;

/**
 * 受講生の検索や登録、更新などを行うREST APIとして実行されるControllerです。
 */
@Validated
@RestController
public class StudentController {

  private StudentService service;

  @Autowired
  public StudentController(StudentService service) {
    this.service = service;
  }

  /**
   * 受講生詳細の一覧検索です。全件検索を行うので、条件指定は行いません。
   *
   * @return 受講生詳細一覧（全件）
   */
  @Operation(summary = "一覧検索", description = "受講生の一覧を検索します。")
  @GetMapping("/studentList")
  public List<StudentDetail> getStudentList() {
    return service.searchStudentList();
  }

  /**
   * 受講生詳細の検索です。IDに紐づく任意の受講生の情報を取得します。
   *
   * @param id 受講生ID
   * @return 受講生
   */
  @Operation(summary = "受講生検索", description = "受講生IDで特定の受講生情報を取得します。")
  @GetMapping("/student/{id}")
  public StudentDetail getStudent(@PathVariable Integer id) {
    return service.searchStudent(id);
  }

  /**
   * 受講生詳細の登録を行います。
   *
   * @param studentDetail 受講生詳細
   * @return 実行結果
   */
  @Operation(summary = "受講生登録", description = "受講生を登録します。")
  @PostMapping("/registerStudent")
  public ResponseEntity<StudentDetail> registerStudent(
      @RequestBody @Valid StudentDetail studentDetail) {
    StudentDetail responseStudentDetail = service.registerStudent(studentDetail);
    return ResponseEntity.ok(responseStudentDetail);
  }

  /**
   * 受講生詳細の更新を行います。 キャンセルフラグの更新もここで行います（論理削除）
   *
   * @param studentDetail 受講生詳細
   * @return 実行結果
   */
  @Operation(summary = "受講生情報更新", description = "既存の受講生情報を更新します。キャンセルフラグも含めた更新を行います（論理削除）。")
  @PutMapping("/updateStudent")
  public ResponseEntity<String> updateStudent(@RequestBody @Valid StudentDetail studentDetail) {
    service.updateStudent(studentDetail);
    return ResponseEntity.ok("更新処理が成功しました。");
  }

  @Operation(summary = "エラー発生のテスト", description = "例外をスローしてエラー処理をテストするエンドポイントです。")
  @GetMapping("/exception")
  public ResponseEntity<String> throwException() throws NotFoundException{
    throw new NotFoundException("このAPIは現在利用できません。古いURLとなっています");
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<String> handleNotFoundException(NotFoundException ex) {
    return ResponseEntity.badRequest().body(ex.getMessage());
  }
//課題31以降
  /**
   * 名前で受講生を検索します（部分一致）。
   *
   * @param name 名前（部分一致）
   * @return 条件に一致する受講生リスト
   */
  @Operation(summary = "名前で受講生検索", description = "部分一致で受講生の名前を検索します。")
  @GetMapping("/student/searchByName")
  public List<StudentDetail> searchByName(@RequestParam String name) {
    return service.searchByName(name);
  }

  /**
   * メールで受講生を検索します（完全一致）。
   *
   * @param email メール（完全一致）
   * @return 条件に一致する受講生リスト
   */
  @GetMapping("/studentList/email")
  public List<StudentDetail> searchByEmail(@RequestParam String email) {
    return service.searchByEmail(email);
  }

  /**
   * 受講生コース情報の一覧取得を行います。
   *
   * @return 受講生コース情報のリスト（全件）
   */
  @Operation(summary = "受講生コース一覧取得", description = "全ての受講生コース情報を取得します。")
  @GetMapping("/studentCourses")
  public List<StudentCourse> getStudentCourseList() {
    return service.getAllStudentCourses();
  }

}
