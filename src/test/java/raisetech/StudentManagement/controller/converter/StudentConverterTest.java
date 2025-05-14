package raisetech.StudentManagement.controller.converter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.repository.StudentRepository;

@SpringBootTest
class StudentConverterTest {

  @MockBean
  private StudentRepository repository;

  @Autowired
  private StudentConverter converter;

  private List<Student> studentList;
  private List<StudentCourse> courseList;

  @BeforeEach
  void setUp() {
    Student student1 = new Student();
    student1.setId(1);
    student1.setName("田中太郎");
    student1.setEmail("tanaka@example.com");

    Student student2 = new Student();
    student2.setId(999);
    student2.setName("鈴木花子");
    student2.setEmail("hanako@example.com");

    studentList = List.of(student1, student2);   // ダミーの受講生データ

    StudentCourse course1 = new StudentCourse();
    course1.setId(1);
    course1.setStudentId(1);
    course1.setCourseName("JAVAコース");
    course1.setApplicationStatus("受講中");

    StudentCourse course2 = new StudentCourse();
    course2.setId(999);
    course2.setStudentId(1);
    course2.setCourseName("JAVAスタンダート");
    course2.setApplicationStatus("受講終了");

    StudentCourse course3 = new StudentCourse();
    course3.setId(500);
    course3.setStudentId(999);
    course3.setCourseName("JAVAアドバンス");
    course3.setApplicationStatus("本申込");

    courseList = List.of(course1, course2, course3);  // ダミーのコースデータ
  }

  @Test
  void 複数ある受講生情報とコース情報をループして組わせて受講生詳細情報を組み立てる(){

    List<StudentDetail> result = converter.convertStudentDetails(studentList, courseList);

    assertThat(result).hasSize(2); //学生二人のデータ

    StudentDetail detail1 = result.get(0);
    assertThat(detail1.getStudent().getId()).isEqualTo(1);
    assertThat(detail1.getStudentCourseList()).hasSize(2);

    assertThat(detail1.getStudentCourseList())
        .extracting(StudentCourse::getCourseName)
        .containsExactlyInAnyOrder("JAVAコース", "JAVAスタンダート");

    assertThat(detail1.getStudentCourseList())
        .extracting(StudentCourse::getApplicationStatus)
        .containsExactlyInAnyOrder("受講中", "受講終了");

    StudentDetail detail2 = result.get(1);
    assertThat(detail2.getStudent().getId()).isEqualTo(999);
    assertThat(detail2.getStudentCourseList()).hasSize(1);
    assertThat(detail2.getStudentCourseList().get(0).getCourseName()).isEqualTo("JAVAアドバンス");
  assertThat(detail2.getStudentCourseList().get(0).getApplicationStatus()).isEqualTo("本申込");

  }

  @Test
  void 受講生詳細情報の処理時にエラーが発生した場合でも他の受講生は正常に処理される() {

    Student student1 = new Student();
    student1.setId(1);
    student1.setName("田中太郎");
    student1.setEmail("tanaka@example.com");

    Student student2 = new Student();
    student2.setId(999);
    student2.setName("鈴木花子");
    student2.setEmail("hanako@example.com");

    List<Student> studentList = List.of(student1, student2);

    List<StudentDetail> result = new ArrayList<>();

    for (Student student : studentList) {
      try {
        // ここで例外が起きるかもしれない処理を想定
        if (student.getId() == 999) {
          // テスト用に例外を強制的に発生させる
          throw new RuntimeException("不正データのため処理できません");
        }

        StudentDetail detail = new StudentDetail();
        detail.setStudent(student);
        detail.setStudentCourseList(new ArrayList<>());  // 空リストをセット

        result.add(detail);
      } catch (Exception e) {
        System.out.println("入力エラーがありました: " + student.getId());
      }
    }

    // エラーがあった受講生は除外されていることを検証
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getStudent().getId()).isEqualTo(1);
  }

  @Test
  void コース情報が空の場合でも受講生詳細情報が処理される() {
    List<StudentCourse> emptyCourseList = new ArrayList<>();  // 空のコースリスト

    List<StudentDetail> result = converter.convertStudentDetails(studentList, emptyCourseList); // コース情報が空の場合、受講生詳細情報の処理がどうなるかを確認

    assertThat(result).hasSize(2);  // 受講生2人のデータは結果として返ってくることを確認

    StudentDetail detail1 = result.get(0);
    assertThat(detail1.getStudent().getId()).isEqualTo(1);
    assertThat(detail1.getStudentCourseList()).isEmpty();  // コース情報が空なので、関連するコースリストも空であることを確認

    StudentDetail detail2 = result.get(1);
    assertThat(detail2.getStudent().getId()).isEqualTo(999);
    assertThat(detail2.getStudentCourseList()).isEmpty();  // 同様に、コース情報が空なので関連するコースリストも空
  }
//課題31以降

  @Test
  void 名前で部分一致検索できる() {
    Student student = new Student();
    student.setId(1);
    student.setName("山田太一郎");
    student.setEmail("yamada@example.com");

    when(repository.findByName("山田")).thenReturn(List.of(student));

    List<Student> result = repository.findByName("山田");
    assertThat(result).isNotEmpty();
    assertThat(result.get(0).getName()).contains("山田");
  }


}
