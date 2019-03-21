package lk.solution.health.excellent.processHandling.helpingClass;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Size;
@Getter
@Setter
public class PasswordChange {
     private String username;
     @Size(min = 4, message = "password should include four characters or symbols")
     private String opsw, npsw, nrepsw;

     public PasswordChange() {
     }

     public PasswordChange(String username, String opsw, String npsw, String nrepsw) {
          this.username = username;
          this.opsw = opsw;
          this.npsw = npsw;
          this.nrepsw = nrepsw;
     }
}
