package ca.uqtr.tp2;
import java.sql.SQLException;
import java.util.List;

import ca.uqtr.tp2.Beans.Inscription;
import ca.uqtr.tp2.Persistence.PersistenceManager;
import ca.uqtr.tp2.Persistence.exception.PersitenceException;
import ca.uqtr.tp2.Persistence.interfaces.EntityManager;

import ca.uqtr.tp2.Beans.Course;
import ca.uqtr.tp2.Beans.Student;
import ca.uqtr.tp2.service.CourseServiceImpl;
import ca.uqtr.tp2.service.InscriptionServiceImpl;
import ca.uqtr.tp2.service.StudentServiceImpl;
import ca.uqtr.tp2.service.interfaces.CourseService;
import ca.uqtr.tp2.service.interfaces.InscriptionService;
import ca.uqtr.tp2.service.interfaces.StudentService;

public class Application {
	private static StudentService studentService;
	private static CourseService courseService;
	private static InscriptionService inscriptionService;

	public static EntityManager getPersistenceManager() throws SQLException {
		return new PersistenceManager();
	}
	public static void main(String[] args) {
		try {
			//Persistence manager
			EntityManager em = getPersistenceManager();
			// Les 3 services: StudentService, CourseService et InscriptionService
			studentService = new StudentServiceImpl(em);
			courseService = new CourseServiceImpl(em);
			inscriptionService = new InscriptionServiceImpl(em);

			//Examples
			//---------------------------------------Student (with the courses list)-----------------------------------------------------
			//String query1 = "SELECT * FROM etudiant WHERE etudiantid = 2";
			//System.out.println(studentService.getStudentBySqlQuery(query1));

			//String query2 = "SELECT * FROM etudiant WHERE age < 23";
			//print(studentService.getListStudentBySqlQuery(query2));

			print(studentService.getStudentsList());

			//Student s1 = studentService.getStudentByFirstName("sarah");
			//System.out.println(s1);

			//Student s2 = studentService.getStudentById(1);
			//System.out.println(s2);

			//-----------------------------------------Course-------------------------------------------------------
			//print(courseService.getCourseList());

			//Course c1 = courseService.getCourseBySigle("INF1013");
			//System.out.println(c1);

			//Course c2 = courseService.getCourseById(1);
			//System.out.println(c2);

			//-----------------------------------------Inscription-------------------------------------------------------
			//print(inscriptionService.getAllInscription());
			//System.out.println(inscriptionService.getInscriptionByStudentAndByCourse(s1, c1));


		} catch (Exception e) {  
			System.out.println(e.getMessage());
		}
	}
	
	private static void print(List<?> list) {
		list.forEach(System.out::println);
	}


}
