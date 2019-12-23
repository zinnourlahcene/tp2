package ca.uqtr.tp2.service.interfaces;


import ca.uqtr.tp2.Beans.Course;
import ca.uqtr.tp2.Persistence.exception.PersitenceException;

import java.util.List;

public interface CourseService {


    List<Course> getCourseList() throws PersitenceException;

    Course getCourseBySigle(String sigle) throws PersitenceException ;

    Course  getCourseById(int id) throws PersitenceException ;
}
