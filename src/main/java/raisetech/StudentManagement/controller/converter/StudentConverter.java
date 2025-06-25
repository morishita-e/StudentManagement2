package raisetech.StudentManagement.controller.converter;

import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;
import raisetech.StudentManagement.domain.StudentDetail;

/**
 *受講生詳細を受講生や受講生コース情報、もしくはその逆の変換を行うコンバーターです。
 */
@Component
public class StudentConverter {

  /**
   * 受講生に紐づく受講生コース情報をマッピングする。
   * 受講生コース情報は受講生に対して複数存在するのでループを回して受講生詳細情報を組み立てる。
   *
   * @param studentList 受講生一覧
   * @param studentsCourseList 受講生コース情報のリスト
   * @return 受講生詳細情報のリスト
   */
  public List<StudentDetail> convertStudentDetails(List<Student> studentList,
      List<StudentCourse> studentsCourseList) {
    return studentList.stream()
        .map(student -> {
          List<StudentCourse> courses = studentsCourseList.stream()
              .filter(course -> student.getId().equals(course.getStudentId()))
              .collect(Collectors.toList());

          // セットコンストラクタ方式に修正
          StudentDetail studentDetail = new StudentDetail();
          studentDetail.setStudent(student);
          studentDetail.setStudentCourseList(courses);

          return studentDetail;
        })
        .collect(Collectors.toList());
  }

}

