package lk.solution.health.excellent.util.service;

import org.springframework.stereotype.Service;

@Service
public class ExceptionService {
    public int stringToInt(String parameter) {
        int userEnteredNumber = 0;
        try {
            userEnteredNumber = Integer.parseInt(parameter);
        } catch (NumberFormatException e) {
            System.out.println("You have entered wrong number");
            System.out.println(e.toString());
        }
        return userEnteredNumber;
    }
}
