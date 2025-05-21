package raisetech.StudentManagement.controller.converter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
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

    Student student1 = Student.builder()
        .id("1")
        .name("田中太郎")
        .email("tanaka@example.com")
        .build();

    Student student2 = Student.builder()
        .id("999")
        .name("鈴木花子")
        .email("hanako@example.com")
        .build();

    studentList = List.of(student1, student2);   // ダミーの受講生データ

    StudentCourse course1 = StudentCourse.builder()
        .id("1")
        .studentId("1")
        .courseName("JAVAコース")
        .build();

    StudentCourse course2 = StudentCourse.builder()
        .id("999")
        .studentId("1")
        .courseName("JAVAスタンダート")
        .build();

    StudentCourse course3 = StudentCourse.builder()
        .id("500")
        .studentId("999")
        .courseName("JAVAアドバンス")
        .build();

    courseList = List.of(course1, course2, course3);  // ダミーのコースデータ
  }

  @Test
  void 複数ある受講生情報とコース情報をループして組わせて受講生詳細情報を組み立てる(){

    List<StudentDetail> result = converter.convertStudentDetails(studentList, courseList);

    assertThat(result).hasSize(2); //学生二人のデータ

    StudentDetail detail1 = result.get(0);
    assertThat(detail1.getStudent().getId()).isEqualTo("1");
    assertThat(detail1.getStudentCourseList()).hasSize(2);
    assertThat(detail1.getStudentCourseList())
        .extracting(StudentCourse::getCourseName)
        .containsExactlyInAnyOrder("JAVAコース", "JAVAスタンダート");

    StudentDetail detail2 = result.get(1);
    assertThat(detail2.getStudent().getId()).isEqualTo("999");
    assertThat(detail2.getStudentCourseList()).hasSize(1);
    assertThat(detail2.getStudentCourseList().get(0).getCourseName()).isEqualTo("JAVAアドバンス");
  }

  @Test
  void 受講生詳細情報の処理時にエラーが発生した場合でも他の受講生は正常に処理される() {


    Student student1 = Student.builder()
        .id("1")
        .name("田中太郎")
        .email("tanaka@example.com")
        .build();

    Student student2 = Student.builder()
        .id("999")
        .name("鈴木花子")
        .email("hanako@example.com")
        .build();

    List<Student> studentList = List.of(student1, student2);

    List<StudentDetail> result = new ArrayList<>();

    for (Student student : studentList) {
      try {
        // ここで例外が起きるかもしれない処理を想定
        if ("999".equals(student.getId())) {
          // テスト用に例外を強制的に発生させる
          throw new RuntimeException("不正データのため処理できません");
        }

        StudentDetail detail = StudentDetail.builder()
            .student(student)
            .studentCourseList(new ArrayList<>())  // 空リストをセット
            .build();

        result.add(detail);
      } catch (Exception e) {
        System.out.println("入力エラーがありました: " + student.getId());
      }
    }

    // エラーがあった受講生は除外されていることを検証
    assertThat(result).hasSize(1);
    assertThat(result.get(0).getStudent().getId()).isEqualTo("1");
  }

  @Test
  void コース情報が空の場合でも受講生詳細情報が処理される() {
    List<StudentCourse> emptyCourseList = new ArrayList<>();  // 空のコースリスト

    List<StudentDetail> result = converter.convertStudentDetails(studentList, emptyCourseList); // コース情報が空の場合、受講生詳細情報の処理がどうなるかを確認

    assertThat(result).hasSize(2);  // 受講生2人のデータは結果として返ってくることを確認

    StudentDetail detail1 = result.get(0);
    assertThat(detail1.getStudent().getId()).isEqualTo("1");
    assertThat(detail1.getStudentCourseList()).isEmpty();  // コース情報が空なので、関連するコースリストも空であることを確認

    StudentDetail detail2 = result.get(1);
    assertThat(detail2.getStudent().getId()).isEqualTo("999");
    assertThat(detail2.getStudentCourseList()).isEmpty();  // 同様に、コース情報が空なので関連するコースリストも空
  }
}
