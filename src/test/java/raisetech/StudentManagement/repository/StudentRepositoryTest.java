package raisetech.StudentManagement.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mybatis.spring.boot.test.autoconfigure.MybatisTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;

@MybatisTest
@Transactional
class StudentRepositoryTest {

  @Autowired
  private StudentRepository sut;

  @Test
  void 受講生の全件検索が行えること() {
    List<Student> actual = sut.search();
    assertThat(actual.size()).isEqualTo(14);
  }

  @Test
  void 受講生コースの全件検索が行えること() {
    List<StudentCourse> actual = sut.searchStudentCourseList();
    assertThat(actual.size()).isEqualTo(10);
  }

  @Test
  void 受講生登録が行えること() {
    Student student = new Student();
    student.setName("山田太郎");
    student.setKanaName("ヤマダタロウ");
    student.setNickname("タロウ");
    student.setEmail("taro@example.com");
    student.setArea("東京");
    student.setAge(25);
    student.setSex("男");
    student.setRemark("特になし");
    student.setDeleted(false);

    sut.registerStudent(student);

    List<Student> actual = sut.search();

    assertThat(actual.size()).isEqualTo(15);
  }

  @Test
  void 受講生コース情報の登録が行えること() {
    Student student = new Student();
    student.setName("山田太郎");
    student.setKanaName("ヤマダタロウ");
    student.setNickname("タロウ");
    student.setEmail("taro@example.com");
    student.setArea("東京");
    student.setAge(25);
    student.setSex("男");
    student.setRemark("特になし");
    student.setDeleted(false);

    sut.registerStudent(student);
    Integer studentId = student.getId();

    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setStudentId(studentId);
    studentCourse.setCourseName("宅建士(宅地建物取引士）");
    studentCourse.setCourseStartAt(LocalDateTime.of(2024, 10, 1, 0, 0, 0));
    studentCourse.setCourseEndAt(LocalDateTime.of(2025, 11, 30, 23, 59, 59));
    studentCourse.setApplicationStatus("仮申込");
    sut.registerStudentCourse(studentCourse);

    List<StudentCourse> actual = sut.searchStudentCourseList();

    assertThat(actual.size()).isEqualTo(11);
  }

  @Test
  void 受講生の検索が行えること() {
    Student student = new Student();
    student.setName("山田太一郎");
    student.setKanaName("ヤマダタイチロウ");
    student.setNickname("タイチ");
    student.setEmail("taithi@example.com");
    student.setArea("東京");
    student.setAge(51);
    student.setSex("男性");
    student.setRemark("");
    student.setDeleted(false);

    sut.registerStudent(student);

    Integer id = student.getId();

    Student actual = sut.searchStudent(id);

    assertNotNull(actual);
    assertEquals("山田太一郎", actual.getName());
  }

 @Test
 void 受講生IDに紐づく受講生コース情報検索が行えること() {
   Student student = new Student();
   student.setName("山田太一郎");
   student.setKanaName("ヤマダタイチロウ");
   student.setNickname("タイチ");
   student.setEmail("taithi@example.com");
   student.setArea("東京");
   student.setAge(51);
   student.setSex("男性");
   student.setRemark("");
   student.setDeleted(false);
   sut.registerStudent(student);
   Integer studentId = student.getId();

   StudentCourse course = new StudentCourse();
   course.setStudentId(studentId);
   course.setCourseName("宅建士(宅地建物取引士）");
   course.setCourseStartAt(LocalDateTime.of(2024, 10, 1, 0, 0, 0));
   course.setCourseEndAt(LocalDateTime.of(2025, 11, 30, 23, 59, 59));
   course.setApplicationStatus("仮申込");
   sut.registerStudentCourse(course);

   Student actual = sut.searchStudent(studentId);

   assertNotNull(actual);
   assertEquals("山田太一郎", actual.getName());

   List<StudentCourse> courses = sut.searchStudentCourse(studentId);
   assertNotNull(courses);
   assertFalse(courses.isEmpty());
   assertEquals("宅建士(宅地建物取引士）", courses.get(0).getCourseName());
 }

 @Test
  void 受講生の更新が行えること() {
    Student student = new Student();
    student.setName("山田太一郎");
    student.setKanaName("ヤマダタイチロウ");
    student.setNickname("タイチ");
    student.setEmail("taithi@example.com");
    student.setArea("東京");
    student.setAge(51);
    student.setSex("男性");
    student.setRemark("");
    student.setDeleted(false);

    sut.registerStudent(student);

    Integer id = student.getId();

    Student actual = sut.searchStudent(id);
    assertEquals("山田太一郎", actual.getName());

    actual.setName("山田次郎");
    actual.setAge(52);
    actual.setRemark("更新テスト");

    sut.updateStudent(actual);

    Student updated = sut.searchStudent(id);
    assertEquals("山田次郎", updated.getName());
    assertEquals(52, updated.getAge());
    assertEquals("更新テスト", updated.getRemark());
  }

  @Test
  void 受講生コース情報の更新が行えること() {
    Student student = new Student();
    student.setName("山田太郎");
    student.setKanaName("ヤマダタロウ");
    student.setNickname("タロウ");
    student.setEmail("taro@example.com");
    student.setArea("東京");
    student.setAge(25);
    student.setSex("男");
    student.setRemark("特になし");
    student.setDeleted(false);
    sut.registerStudent(student);
    Integer studentId = student.getId();

    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setStudentId(studentId);
    studentCourse.setCourseName("宅建士(宅地建物取引士）");
    studentCourse.setCourseStartAt(LocalDateTime.of(2024, 10, 1, 0, 0, 0));
    studentCourse.setCourseEndAt(LocalDateTime.of(2025, 11, 30, 23, 59, 59));
    studentCourse.setApplicationStatus("仮申込");
    sut.registerStudentCourse(studentCourse);

    Student actual = sut.searchStudent(studentId);
    assertEquals("山田太郎", actual.getName());

    List<StudentCourse> courses = sut.searchStudentCourse(studentId);
    assertNotNull(courses);
    assertFalse(courses.isEmpty());
    assertEquals("宅建士(宅地建物取引士）", courses.get(0).getCourseName());

    StudentCourse courseToUpdate = courses.get(0);
    courseToUpdate.setCourseName("JAVAスタンダート");
    courseToUpdate.setApplicationStatus("本申込");
    sut.updateStudentCourse(courseToUpdate);

    List<StudentCourse> updatedCourses = sut.searchStudentCourse(studentId);
    assertEquals("JAVAスタンダート", updatedCourses.get(0).getCourseName());
    assertEquals("本申込", updatedCourses.get(0).getApplicationStatus());
  }

//課題31以降
@Test
void 名前による部分一致検索が行えること() {
  Student student1 = new Student();
  student1.setName("山田太郎");
  student1.setKanaName("ヤマダタロウ");
  student1.setNickname("タロウ");
  student1.setEmail("taro@example.com");
  student1.setArea("東京");
  student1.setAge(25);
  student1.setSex("男");
  student1.setRemark("");
  student1.setDeleted(false);
  sut.registerStudent(student1);

  Student student2 = new Student();
  student2.setName("山田花子");
  student2.setKanaName("ヤマダハナコ");
  student2.setNickname("ハナ");
  student2.setEmail("hana@example.com");
  student2.setArea("大阪");
  student2.setAge(23);
  student2.setSex("女");
  student2.setRemark("");
  student2.setDeleted(false);
  sut.registerStudent(student2);

  // 名前に「山田」を含む受講生の検索
  List<Student> result = sut.findByName("山田");

  // 検索結果の検証
  assertThat(result).isNotEmpty();
  assertThat(result).extracting(Student::getName)
      .contains("山田太郎", "山田花子");
}

  @Test
  void メールアドレスによる完全一致検索が行えること() {
    Student student = new Student();
    student.setName("佐藤美幸");
    student.setKanaName("サトウミユキ");
    student.setNickname("ミユ");
    student.setEmail("satou@example.com");
    student.setArea("山口");
    student.setAge(19);
    student.setSex("女");
    student.setRemark("特になし");
    student.setDeleted(false);
    sut.registerStudent(student);

    List<Student> result = sut.findByEmail("satou@example.com");

    assertThat(result).hasSize(1);
    assertEquals("佐藤美幸", result.get(0).getName());
  }
}