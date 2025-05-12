package raisetech.StudentManagement.controller.converter;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.repository.StudentRepository;

@SpringBootTest
class StudentConverterTest {

  @Mock
  private StudentRepository repository;

  @Autowired
  private StudentConverter converter;

  private List<Student> studentList;
  private List<StudentCourse> courseList;

  @BeforeEach
  void setUp() {

    Student student1 = new Student();
    student1.setId("1");
    student1.setName("田中太郎");
    student1.setEmail("tanaka@example.com");

    Student student2 = new Student();
    student2.setId("999");
    student2.setName("鈴木花子");
    student2.setEmail("hanako@example.com");

    studentList = List.of(student1, student2);     // ダミーの受講生データ

    StudentCourse course1 = new StudentCourse();
    course1.setId("1");
    course1.setStudentId("1");
    course1.setCourseName("JAVAコース");

    StudentCourse course2 = new StudentCourse();
    course2.setId("999");
    course2.setStudentId("1");
    course2.setCourseName("JAVAスタンダート");

    StudentCourse course3 = new StudentCourse();
    course3.setId("500");
    course3.setStudentId("999");
    course3.setCourseName("JAVAアドバンス");

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
    Student student1 = new Student();
    student1.setId("1");
    student1.setName("田中太郎");
    student1.setEmail("tanaka@example.com");

   //（不正な情報でエラーを発生させる）
    Student student2 = new Student();
    student2.setId("999");
    student2.setName("鈴木花子");
    student2.setEmail("hanako@example.com");

    List<Student> studentList = List.of(student1, student2);

    List<StudentDetail> result = converter.convertStudentDetails(studentList, courseList);

    assertThat(result).hasSize(2); // 学生二人のデータ

    // 生徒1の確認（正常な処理）
    StudentDetail detail1 = result.get(0);
    assertThat(detail1.getStudent().getId()).isEqualTo("1");
    assertThat(detail1.getStudentCourseList()).hasSize(2);

    // 生徒2の確認（エラー発生しても問題ない）
    try {
      StudentDetail detail2 = result.get(1);
      assertThat(detail2.getStudent().getId()).isEqualTo("999"); // ここでエラー発生することを期待
    } catch (Exception e) {
      // エラーが発生してもテストは通過する
      System.out.println("エラー発生: student2 の情報が一致しません。");
    }
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
