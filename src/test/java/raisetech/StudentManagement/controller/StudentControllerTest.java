package raisetech.StudentManagement.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentDetail;
import raisetech.StudentManagement.service.StudentService;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private StudentService service;

  private Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
  private Student createValidStudent() {
    Student student = new Student();
    student.setId(123);
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

 private StudentCourse createValidStudentCourse(Integer studentId) {
   LocalDateTime start = LocalDateTime.of(2024, 4, 1, 0, 0);
   LocalDateTime end = start.plusYears(1);

   StudentCourse course = new StudentCourse();
   course.setId(999);
   course.setStudentId(studentId);
   course.setCourseName("Javaスタンダードコース");
   course.setCourseStartAt(start);
   course.setCourseEndAt(end);
   course.setApplicationStatus("仮申込");
   return course;
 }

  private ObjectMapper objectMapper;

  @BeforeEach
  void setup() {
    objectMapper = new ObjectMapper();
    objectMapper.registerModule(new JavaTimeModule());
    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  @Test
  void 受講生詳細の一覧検索が実行できて空データが入っているリストが返ってくること() throws Exception {
    StudentDetail emptyDetail = new StudentDetail();
    emptyDetail.setStudent(new Student());
    emptyDetail.setStudentCourseList(List.of());

    mockMvc.perform(get("/studentList"))
        .andExpect(status().isOk());

    verify(service, times(1)).searchStudentList();
  }

  @Test
  void 受講生詳細の受講生で適切な値を入力した時に入力チェックに異常が発生しないこと() {
    Student student = new Student();
    student.setId(1);
    student.setName("江並公示");
    student.setKanaName("エナミコウジ");
    student.setNickname("エナミ");
    student.setEmail("test@example.com");
    student.setArea("奈良");
    student.setSex("男性");

    Set<ConstraintViolation<Student>> violations = validator.validate(student);

    assertThat(violations.size()).isEqualTo(0);
  }

  @Test
  void 受講生のIDで特定の受講生情報を検索します() throws Exception {
    Student student = createValidStudent();
    List<StudentCourse> courseList = List.of(createValidStudentCourse(student.getId()));

    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    studentDetail.setStudentCourseList(courseList);

    when(service.searchStudent(123)).thenReturn(studentDetail);

    mockMvc.perform(get("/student/123"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.student.id").value("123"))
        .andExpect(jsonPath("$.student.name").value("山田太郎"))
        .andExpect(jsonPath("$.studentCourseList[0].courseName").value("Javaスタンダードコース"));

    verify(service, times(1)).searchStudent(123);
  }

  @Test
  void 受講生登録で適切な値を入力した時に入力チェックに異常が発生しないこと() throws Exception {
    Student student = createValidStudent();
    StudentCourse studentCourse = createValidStudentCourse(student.getId());

    StudentDetail studentDetail = new StudentDetail();
    studentDetail.setStudent(student);
    studentDetail.setStudentCourseList(List.of(studentCourse));

    mockMvc.perform(post("/registerStudent")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(studentDetail)))
        .andExpect(status().isOk());
  }

  @Test
  void 受講生情報とコース情報で途中でエラーが発生した場合にエラーが返されること() throws Exception {
    String invalidJson = """
        {
          "student": {
            "id": 123,
            "name": "山田太郎",
            "kanaName": "ヤマダタロウ",
            "nickname": "タロウ",
            "email": "invalid-email",
            "area": "東京",
            "age": 25,
            "sex": "男",
            "remark": "特になし",
            "deleted": false
          },
          "studentCourseList": [
            {
              "id": 100,
              "studentId": 123,
              "courseName": "Javaスタンダードコース",
              "courseStartAt": "2024-04-01T00:00:00",
              "courseEndAt": "2025-04-01T00:00:00",
              "ApplicationStatus": "本申込"
            }
          ]
        }
        """;
    mockMvc.perform(post("/registerStudent")
            .contentType(MediaType.APPLICATION_JSON)
            .content(invalidJson))
        .andExpect(status().isBadRequest());  // 400 Bad Request が返されるを確認
  }

  @Test
  void 受講生詳細の更新が実行できて空で返ってくること() throws Exception {
    mockMvc.perform(put("/updateStudent").contentType(MediaType.APPLICATION_JSON).content(
            """
                {
                  "student": {
                  "id" : 12,
                    "name": "江並康介",
                    "kanaName": "エナミ",
                    "nickname": "コウジ",
                    "email": "test@example.com",
                    "area": "東京",
                    "age": 36,
                    "sex": "男",
                    "remark": ""
                  },
                  "studentCourseList": [
                    {
                      "id": 15,
              "studentId": 12,
              "courseName": "Javaコース",
              "courseStartAt": "2024-04-27T10:50:39.833614",
              "courseEndAt": "2025-04-27T10:50:39.833614",
              "applicationStatus": "本申込"
                    }
                  ]
                }
              """
        ))
        .andExpect(status().isOk());

    verify(service, times(1)).updateStudent(any());
  }

  @Test
  void 受講生詳細の例外APIが実行できてステータスが400で返ってくること() throws Exception{
    mockMvc.perform(get("/exception"))
        .andExpect(status().is4xxClientError())
        .andExpect(content().string("このAPIは現在利用できません。古いURLとなっています"));
  }
//課題31以降
@Test
void 名前で部分一致検索ができること() throws Exception {
  Student student = createValidStudent(); // 山田太郎
  StudentCourse course = createValidStudentCourse(student.getId());

  StudentDetail detail = new StudentDetail();
  detail.setStudent(student);
  detail.setStudentCourseList(List.of(course));

  when(service.searchByName("山田")).thenReturn(List.of(detail));

  mockMvc.perform(get("/student/searchByName").param("name", "山田"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$[0].student.name").value("山田太郎"))
      .andExpect(jsonPath("$[0].studentCourseList[0].courseName").value("Javaスタンダードコース"))
      .andExpect(jsonPath("$[0].studentCourseList[0].applicationStatus").value("仮申込"));

  verify(service, times(1)).searchByName("山田");
}

  @Test
  void メールで完全一致検索ができること() throws Exception {
    Student student = createValidStudent(); // 山田太郎
    StudentCourse course = createValidStudentCourse(student.getId());

    StudentDetail detail = new StudentDetail();
    detail.setStudent(student);
    detail.setStudentCourseList(List.of(course));

    when(service.searchByEmail("taro@example.com")).thenReturn(List.of(detail));

    mockMvc.perform(get("/studentList/email").param("email", "taro@example.com"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].student.name").value("山田太郎"))
        .andExpect(jsonPath("$[0].studentCourseList[0].courseName").value("Javaスタンダードコース"))
        .andExpect(jsonPath("$[0].studentCourseList[0].applicationStatus").value("仮申込"));

    verify(service, times(1)).searchByEmail("taro@example.com");
  }

}