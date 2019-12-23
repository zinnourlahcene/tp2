package ca.uqtr.tp2.service.interfaces;


import ca.uqtr.tp2.Beans.Course;
import ca.uqtr.tp2.Beans.Inscription;
import ca.uqtr.tp2.Beans.Student;
import ca.uqtr.tp2.Persistence.exception.PersitenceException;

import java.util.List;

public interface InscriptionService {
    List<Inscription> getAllInscription() throws PersitenceException;

    Inscription getInscriptionByStudentAndByCourse(Student student, Course course) throws PersitenceException ;

}
