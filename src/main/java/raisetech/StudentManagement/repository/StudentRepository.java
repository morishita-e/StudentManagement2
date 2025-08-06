package raisetech.StudentManagement.repository;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import raisetech.StudentManagement.data.Student;
import raisetech.StudentManagement.data.StudentCourse;

@Mapper
public interface StudentRepository {

  List<Student> search();

  Student searchStudent(Integer id);

  List<StudentCourse> searchStudentCourseList();

  List<StudentCourse> searchStudentCourse(Integer studentId);

  void registerStudent(Student student);

  void registerStudentCourse(StudentCourse studentCourse);

  void updateStudent(Student student);

  void updateStudentCourse(StudentCourse studentCourse);

  List<Student> findByName(String name);

  List<Student> findByEmail(String email);
}