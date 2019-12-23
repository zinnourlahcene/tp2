package ca.uqtr.tp2.service.interfaces;

import ca.uqtr.tp2.Beans.Student;
import ca.uqtr.tp2.Persistence.exception.PersitenceException;

import java.util.List;

public interface StudentService {

    List<Student> getStudentsList() throws PersitenceException;

    Student getStudentByFirstName(String firstName) throws PersitenceException ;

    Student getStudentById(int id) throws PersitenceException ;

    Student getStudentBySqlQuery(String query) throws PersitenceException;

    List<Student> getListStudentBySqlQuery(String query) throws PersitenceException;
}
