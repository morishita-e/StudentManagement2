package raisetech.StudentManagement.data;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "受講生コース情報")
public class StudentCourse {

  private Integer id;

  private Integer studentId;

  @NotBlank
  private String courseName;

  private LocalDateTime courseStartAt;
  private LocalDateTime courseEndAt;

  @NotBlank
  @Schema(description = "申込状況（仮申込・本申込・受講中・受講終了）")
  private String applicationStatus;
}
