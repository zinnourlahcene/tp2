package ca.uqtr.tp2.service;


import ca.uqtr.tp2.Beans.Course;
import ca.uqtr.tp2.Beans.Inscription;
import ca.uqtr.tp2.Beans.Student;
import ca.uqtr.tp2.Persistence.exception.PersitenceException;
import ca.uqtr.tp2.Persistence.interfaces.EntityManager;
import ca.uqtr.tp2.service.interfaces.InscriptionService;

import java.util.List;

public class InscriptionServiceImpl implements InscriptionService {

    private EntityManager entityManager;

    public InscriptionServiceImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Inscription> getAllInscription() throws PersitenceException {
        return this.entityManager.retrieve(Inscription.class, "SELECT * FROM inscription");
    }

    @Override
    public Inscription getInscriptionByStudentAndByCourse(Student student, Course course) throws PersitenceException {
        return this.entityManager.retrieve(Inscription.class, "SELECT * FROM inscription WHERE etudiantid = "+student.getStudentId()+" AND coursid = "+course.getCourseId()+";").get(0);

    }

}
