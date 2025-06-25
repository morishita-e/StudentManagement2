package raisetech.StudentManagement.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import raisetech.StudentManagement.controller.converter.StudentConverter;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.repository.StudentRepository;

/**
 * 受講生情報を取り扱うサービスです。受講生の検索や登録・更新処理を行います。
 */
@Service
public class StudentService {

  private StudentRepository repository;
  private StudentConverter converter;

  @Autowired
  public StudentService(StudentRepository repository, StudentConverter converter) {
    this.repository = repository;
    this.converter = converter;
  }

  /**
   * 受講生詳細の一覧検索です。全件検索を行うので、条件指定は行いません。
   *
   * @return 受講生詳細一覧（全件）
   */
  public List<StudentDetail> searchStudentList() {
    List<Student> studentList = repository.search();
    List<StudentCourse> studentCourseList = repository.searchStudentCourseList();
    return converter.convertStudentDetails(studentList, studentCourseList);
  }

  /**
   * 受講生詳細検索です。IDに紐づく受講生情報を取得したあと、その受講生に紐づく受講生コース情報を取得して設定します。
   *
   * @param id 受講生ID
   * @return 受講生詳細
   */
  public StudentDetail searchStudent(Integer id) {
    Student student = repository.searchStudent(id);
    List<StudentCourse> studentsCourse = repository.searchStudentCourse(student.getId());
    return new StudentDetail(student, studentsCourse); // ← ← ← こちらに変更
  }

    /**
     * 受講生詳細の登録を行います。 受講生と受講生コース情報を個別に登録し、受講生コース情報には受講生情報を紐づける値とコース開始日、コース終了日を設定します。
     *
     * @param studentDetail 受講生詳細
     * @return 登録情報を付与した受講生詳細
     */
  @Transactional
  public StudentDetail registerStudent(StudentDetail studentDetail) {
    Student student = studentDetail.getStudent();

    repository.registerStudent(student);
    studentDetail.getStudentCourseList().forEach(studentsCourse -> {
      initStudentsCourse(studentsCourse, student.getId());
      repository.registerStudentCourse(studentsCourse);
    });
    return studentDetail;
  }

  /**
   * 受講生コース情報を登録する際の初期情報を設定する。
   *
   * @param studentsCourse 受講生コース情報
   * @param id        受講生
   */
  void initStudentsCourse(StudentCourse studentsCourse, Integer id) {
    LocalDateTime now = LocalDateTime.now();

    studentsCourse.setStudentId(id);
    studentsCourse.setCourseStartAt(now);
    studentsCourse.setCourseEndAt(now.plusYears(1));

    // applicationStatus が未設定なら「仮申込」にする
    if (studentsCourse.getApplicationStatus() == null) {
      studentsCourse.setApplicationStatus("仮申込");
    }
  }

  /**
   * 受講生詳細の更新を行います。 受講生と受講生コース情報をそれぞれ更新します。
   *
   * @param studentDetail 受講生詳細
   */
  @Transactional
  public void updateStudent(StudentDetail studentDetail) {
    repository.updateStudent(studentDetail.getStudent());
    studentDetail.getStudentCourseList()
        .forEach(studentsCourse -> repository.updateStudentCourse(studentsCourse));
  }

  /**
   * 名前で受講生を検索します（部分一致）。
   *
   * @param name 名前（部分一致）
   * @return 条件に一致する受講生リスト
   */
  public List<StudentDetail> searchByName(String name) {
    List<Student> students = repository.findByName(name);
    return students.stream()
        .map(student -> new StudentDetail(student, repository.searchStudentCourse(student.getId())))
        .toList();
  }

  /**
   * メールで受講生を検索します（完全一致）。
   *
   * @param email メール（完全一致）
   * @return 条件に一致する受講生リスト
   */
  public List<StudentDetail> searchByEmail(String email) {
    List<Student> students = repository.findByEmail(email);
    return students.stream()
        .map(s -> new StudentDetail(s, repository.searchStudentCourse(s.getId())))
        .collect(Collectors.toList());
  }

  /**
   * 受講生コース情報の全件取得を行います。
   *
   * @return 受講生コース情報のリスト（全件）
   */
  public List<StudentCourse> getAllStudentCourses() {
    return repository.searchStudentCourseList();
  }
}
