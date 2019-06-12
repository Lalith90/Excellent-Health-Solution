package lk.solution.health.excellent.util.service;

import org.springframework.stereotype.Service;

@Service
public class ExceptionService {
    public int stringToInt(String paramter) {
        int userEnteredNumber = 0;
        try {
            userEnteredNumber = Integer.parseInt(paramter);
        } catch (NumberFormatException e) {
            System.out.println("You have entered wrong number");
            System.out.println(e.toString());
        }
        return userEnteredNumber;
    }
}
