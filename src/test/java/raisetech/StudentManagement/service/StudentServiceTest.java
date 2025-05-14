package raisetech.StudentManagement.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import raisetech.StudentManagement.controller.converter.StudentConverter;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.repository.StudentRepository;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

  @Mock
  private StudentRepository repository;

  @Mock
  private StudentConverter converter;

  private StudentService sut;

  @BeforeEach
  void before(){
    sut = new StudentService(repository, converter);
  }

  private Student createValidStudent() {
    Student student = new Student();
    student.setId(1);
    student.setName("山田太郎");
    student.setKanaName("ヤマダタロウ");
    student.setNickname("タロウ");
    student.setEmail("taro@example.com");
    student.setArea("東京");
    student.setAge(25);
    student.setSex("男");
    student.setRemark("特になし");
    student.setDeleted(false);
    return student;
  }

  private StudentCourse createValidStudentCourse(int studentId) {
    StudentCourse course = new StudentCourse();
    course.setId(999);
    course.setStudentId(studentId);
    course.setCourseName("Javaスタンダードコース");
    course.setCourseStartAt(LocalDateTime.now());
    course.setCourseEndAt(LocalDateTime.now().plusYears(1));
    course.setApplicationStatus("仮申込");
    return course;
  }

  @Test
  void 受講生詳細の一覧検索_リポジトリとコンバーターの処理が適切に呼び出せていること() {
    List<Student> studentList = new ArrayList<>();
    List<StudentCourse> studentCourseList = new ArrayList<>();
    when(repository.search()).thenReturn(studentList);
    when(repository.searchStudentCourseList()).thenReturn(studentCourseList);

    sut.searchStudentList();

    verify(repository, times(1)).search();
    verify(repository, times(1)).searchStudentCourseList();
    verify(converter, times(1)).convertStudentDetails(studentList, studentCourseList);
  }

  @Test
  void 受講生詳細の検索_リポジトリの処理が適切に呼び出せていること() {
    Integer id = 999;
    Student student = new Student();
    student.setId(id);
    when(repository.searchStudent(id)).thenReturn(student);
    when(repository.searchStudentCourse(id)).thenReturn(new ArrayList<>());

    StudentDetail expected = new StudentDetail();
    expected.setStudent(student);
    expected.setStudentCourseList(new ArrayList<>());

    StudentDetail actual = sut.searchStudent(id);

    verify(repository, times(1)).searchStudent(id);
    verify(repository, times(1)).searchStudentCourse(id);
    assertEquals(expected.getStudent().getId(), actual.getStudent().getId());
  }

  @Test
  void 受講生詳細の登録_リポジトリの処理が適切に呼び出せていること() {
    Student student = new Student();
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setApplicationStatus("仮申込");
    List<StudentCourse> studentCourseList = List.of(studentCourse);

    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    studentDetail.setStudentCourseList(studentCourseList);

    sut.registerStudent(studentDetail);

    verify(repository, times(1)).registerStudent(student);
    verify(repository, times(1)).registerStudentCourse(studentCourse);
  }

  @Test
  void 受講生詳細の登録_初期化が行われていること() {
    Integer id = 999;
    Student student = new Student();
    student.setId(id);

    StudentCourse studentCourse = new StudentCourse();

    sut.initStudentsCourse(studentCourse, student.getId());

    assertEquals(id, studentCourse.getStudentId());
    assertEquals(LocalDateTime.now().getHour(), studentCourse.getCourseStartAt().getHour());
    assertEquals(LocalDateTime.now().plusYears(1).getYear(), studentCourse.getCourseEndAt().getYear());
  }

  @Test
  void 受講生詳細の更新_リポジトリの処理が適切に呼び出せていること() {
    Student student = new Student();
    StudentCourse studentCourse = new StudentCourse();
    studentCourse.setApplicationStatus("仮申込");
    List<StudentCourse> studentCourseList = List.of(studentCourse);

    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    studentDetail.setStudentCourseList(studentCourseList);

    sut.updateStudent(studentDetail);

    verify(repository, times(1)).updateStudent(student);
    verify(repository, times(1)).updateStudentCourse(studentCourse);
  }
//課題31以降
@Test
void 名前で部分一致検索ができること() {
  List<Student> students = List.of(createValidStudent());
  when(repository.findByName("山田")).thenReturn(students);

  StudentCourse course = createValidStudentCourse(students.get(0).getId());
  when(repository.searchStudentCourse(students.get(0).getId())).thenReturn(List.of(course));

  List<StudentDetail> result = sut.searchByName("山田");

  verify(repository, times(1)).findByName("山田");
  verify(repository, times(1)).searchStudentCourse(students.get(0).getId());

  assertEquals("山田太郎", result.get(0).getStudent().getName());
  assertEquals("Javaスタンダードコース", result.get(0).getStudentCourseList().get(0).getCourseName());
  assertEquals("仮申込", result.get(0).getStudentCourseList().get(0).getApplicationStatus());
  }

  @Test
  void メールアドレスで完全一致検索ができること() {
    Student student = createValidStudent();

    when(repository.findByEmail("taro@example.com")).thenReturn(List.of(student));
    when(repository.searchStudentCourse(student.getId())).thenReturn(List.of());

    List<StudentDetail> result = sut.searchByEmail("taro@example.com");

    assertEquals(1, result.size());
    assertEquals("taro@example.com", result.get(0).getStudent().getEmail());

    verify(repository).findByEmail("taro@example.com");
    verify(repository).searchStudentCourse(student.getId());
  }
}