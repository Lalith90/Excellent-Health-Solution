package lk.solution.health.excellent.resource.service;

import lk.solution.health.excellent.common.interfaces.AbstractService;
import lk.solution.health.excellent.processHandling.helpingClass.PasswordChange;
import lk.solution.health.excellent.resource.dao.UserDao;
import lk.solution.health.excellent.resource.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class UserService implements AbstractService<User, Integer> {
    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(PasswordEncoder passwordEncoder, UserDao userDao) {
        this.passwordEncoder = passwordEncoder;
        this.userDao = userDao;
    }



    public List<User> findAll() {
        return userDao.findAll();
    }

    @Transactional
    public User findById(Integer id) {
        return userDao.getOne(id);
    }

    @Transactional
    public User persist(User user) {
        user.setEnabled(true);

        if (user.getPassword() != null)
            user.setPassword(passwordEncoder.encode(user.getPassword()));
        else
            user.setPassword(userDao.getOne(user.getId()).getPassword());
        return userDao.save(user);
        }


    public boolean delete(Integer id) {
        userDao.deleteById(id);
        return false;
    }


    public List<User> search(User user) {
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<User> userExample = Example.of(user, matcher);
        return userDao.findAll(userExample);
    }
    @Cacheable(value = "user", key = "#id")
    public Integer findByEmployeeId(Integer id){
        return userDao.findByEmployeeId(id);
    }


    public Integer findByUserIdByUserName(String userName){
        return userDao.findUserIdByUserName(userName);
    }

    @Transactional
    public String passwordChange(PasswordChange passwordChange){
        User user = userDao.getOne(userDao.findUserIdByUserName(passwordChange.getUsername()));
        if(passwordEncoder.matches(passwordChange.getOpsw(),user.getPassword()) && passwordChange.getNpsw().equals(passwordChange.getNrepsw())){
            user.setPassword(passwordEncoder.encode(passwordChange.getNpsw()));
            user.setEnabled(true);
            System.out.println("lalalalala");
            System.out.println(user.getPassword());
            System.out.println("change password");
            userDao.save(user);
        }
        return "Successfully Password Changed";
    }

    public User findByUserName(String name) {
        return userDao.findByUsername(name);
    }
}
