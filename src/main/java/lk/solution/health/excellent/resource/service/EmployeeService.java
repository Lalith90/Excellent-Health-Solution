package lk.solution.health.excellent.resource.service;

import lk.solution.health.excellent.util.interfaces.AbstractService;
import lk.solution.health.excellent.resource.dao.EmployeeDao;
import lk.solution.health.excellent.resource.entity.Employee;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmployeeService implements AbstractService<Employee, Integer> {

    private EmployeeDao employeeDao;

    @Autowired
    public EmployeeService(EmployeeDao employeeDao){
        this.employeeDao = employeeDao;
    }
    @Cacheable(value = "employee")
    public List<Employee> findAll() {
        return employeeDao.findAll();
    }

    @CachePut(value = "employee")
    public Employee findById(Integer id) {
        return employeeDao.getOne(id);
    }

    @Transactional// spring transactional annotation need to tell spring to this
    // method work through the project meaning that any failure causes the entire operation to roll back to its previous state,
    // and to re-throw the original exception. This means that none of the people will be added to EMPLOYEE if one person fails to be added
    public Employee persist(Employee employee) {
                 return employeeDao.save(employee);
    }

    public boolean delete(Integer id) {
        employeeDao.deleteById(id);
        return false;
    }

    public List<Employee> search(Employee employee) {
        ExampleMatcher matcher = ExampleMatcher
                .matching()
                .withIgnoreCase()
                .withStringMatcher(ExampleMatcher.StringMatcher.CONTAINING);
        Example<Employee> employeeExample = Example.of(employee, matcher);
        return employeeDao.findAll(employeeExample);
    }

    public boolean isEmployeePresent(Employee employee){
        return employeeDao.equals(employee);
    }

    public Employee lastEmployee(){
        return employeeDao.findFirstByOrderByIdDesc();
    }


}
