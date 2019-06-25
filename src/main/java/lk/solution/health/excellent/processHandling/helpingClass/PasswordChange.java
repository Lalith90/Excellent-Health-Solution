package lk.solution.health.excellent.processHandling.helpingClass;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Size;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PasswordChange {
     private String username;
     @Size(min = 4, message = "password should include four characters or symbols")
     private String opsw, npsw, nrepsw;


}
