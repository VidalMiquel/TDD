package com.luv2code.springmvc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.jdbc.Sql;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.GradebookCollegeStudent;
import com.luv2code.springmvc.models.HistoryGrade;
import com.luv2code.springmvc.models.MathGrade;
import com.luv2code.springmvc.models.ScienceGrade;
import com.luv2code.springmvc.repository.HistoryGradeDao;
import com.luv2code.springmvc.repository.MathGradeDao;
import com.luv2code.springmvc.repository.ScienceGradeDao;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;

@TestPropertySource("/application-test.properties")
@SpringBootTest
public class StudentAndGradeServiceTest {

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private StudentAndGradeService studentService;

    @Autowired
    private StudentDao studentDao;

    @Autowired
    MathGradeDao mathGradeDao;
    
    @Autowired
    ScienceGradeDao scienceGradeDao;

        
    @Autowired
    HistoryGradeDao historyGradeDao;

    @Value("${sql.scripts.create.student}")
    private String sqlAddStudent;
    
    @Value("${sql.scripts.create.math.grade}")
    private String sqlAddMathGrade;
    
    @Value("${sql.scripts.create.science.grade}")
    private String sqlAddScienceGrade;

    @Value("${sql.scripts.create.history.grade}")
    private String sqlAddHistoryGrade;

    @Value("${sql.scripts.delete.student}")
    private String sqlDeleteStudent;

    @Value("${sql.scripts.delete.math.grade}")
    private String sqlDeleteMathGrade;

    @Value("${sql.scripts.delete.science.grade}")
    private String sqlDeleteScienceGrade;

    @Value("${sql.scripts.delete.history.grade}")
    private String sqlDeleteHistoryGrade;

    @BeforeEach
    public void setupDatabase() {
        jdbc.execute(sqlAddStudent);
        jdbc.execute(sqlAddMathGrade);
        jdbc.execute(sqlAddScienceGrade);
        jdbc.execute(sqlAddHistoryGrade);
    }

    @Test
    public void createStudentService() {

        studentService.createStudent("Chad", "Darby",
                "chad.darby@luv2code_school.com");

         CollegeStudent student = studentDao.
                 findByEmailAddress("chad.darby@luv2code_school.com");

         assertEquals("chad.darby@luv2code_school.com",
                 student.getEmailAddress(), "find by email");
    }

    @Test
    public void isStudentNullCheck() {

        assertTrue(studentService.checkIfStudentIsNull(1));

        assertFalse(studentService.checkIfStudentIsNull(0));
    }


    @Test
    public void deleteStudentService(){
        Optional<CollegeStudent> deletedCollegeStudent = studentDao.findById(1);

        Optional<MathGrade> deleteMathGrade = mathGradeDao.findById(1);
        Optional<ScienceGrade> deleteScienceGrade = scienceGradeDao.findById(1);
        Optional<HistoryGrade> deleteHistoryGrade = historyGradeDao.findById(1);

        assertTrue(deletedCollegeStudent.isPresent(), "Return true");
        assertTrue(deleteMathGrade.isPresent());
        assertTrue(deleteScienceGrade.isPresent());
        assertTrue(deleteHistoryGrade.isPresent());

        studentService.deleteStudent(1);

        deletedCollegeStudent = studentDao.findById(1);
        deleteMathGrade = mathGradeDao.findById(1);
        deleteScienceGrade = scienceGradeDao.findById(1);
        deleteHistoryGrade = historyGradeDao.findById(1);

        assertFalse(deletedCollegeStudent.isPresent(), "return false");
        assertFalse(deleteMathGrade.isPresent(), "return false");
        assertFalse(deleteScienceGrade.isPresent(), "return false");
        assertFalse(deleteHistoryGrade.isPresent(), "return false");
    }

    //Execute the SQL before the test method
    @Sql("/insertData.sql")
    @Test
    public void getGradebookService(){
        Iterable<CollegeStudent> iterableCollegeStudent =  studentService.getGradebook();

        List<CollegeStudent> collegeStudents = new ArrayList<>();

        for(CollegeStudent collegeStudent: iterableCollegeStudent){
            collegeStudents.add(collegeStudent);
        }

        assertEquals(5, collegeStudents.size());
    }

    @Test
    public void createGradeService(){
        //Create the grade
        assertTrue(studentService.createGrade(80.50,1,"math"));
        assertTrue(studentService.createGrade(80.50,1,"science"));
        assertTrue(studentService.createGrade(80.50,1,"history"));

        assertFalse(studentService.createGrade(-10, 1, "math"));
        assertFalse(studentService.createGrade(110, 1, "math"));
        assertFalse(studentService.createGrade(10, 1, "english"));

        //Get all grades with studentId
        Iterable<MathGrade> mathGrades = mathGradeDao.findGradeByStudentId(1);
        Iterable<ScienceGrade> scienceGrades = scienceGradeDao.findGradeByStudentId(1);
        Iterable<HistoryGrade> historyGrades = historyGradeDao.findGradeByStudentId(1);

        //Verify there sis grades
        assertTrue(((Collection<MathGrade>) mathGrades).size() == 2,
                "Student has math grades");        
        assertTrue(((Collection<ScienceGrade>) scienceGrades).size() == 2,
        "Student has science grades");     
        assertTrue(((Collection<HistoryGrade>) historyGrades).size() == 2,
                "Student has history grades");      
    }


    @Test
    public void deleteGradeService(){
        assertEquals(1, studentService.deleteGrade(1, "math"), "returns id after detele");

        assertEquals(1, studentService.deleteGrade(1, "science"), "returns id after detele");

        assertEquals(1, studentService.deleteGrade(1, "history"), "returns id after detele");
    }

    @Test
    public void deleteGradeServiceReturnStudentIdZero(){
        assertEquals(0, studentService.deleteGrade(0, "science"), "no student should have 0 id");

        assertEquals(0, studentService.deleteGrade(0, "literature"), "no student should have a literature class");

    }

    @Test
    public void studentInformation(){
        GradebookCollegeStudent gradebookCollegesCollegeStudent = studentService.studentInformation(1);
        assertNotNull(gradebookCollegesCollegeStudent);
        assertEquals(1, gradebookCollegesCollegeStudent.getId());
        assertEquals("Eric", gradebookCollegesCollegeStudent.getFirstname());
        assertEquals("Roby", gradebookCollegesCollegeStudent.getLastname());
        assertEquals("eric.roby@luv2code_school.com", gradebookCollegesCollegeStudent.getEmailAddress());
        assertTrue(gradebookCollegesCollegeStudent.getStudentGrades().getMathGradeResults().size()==1);
        assertTrue(gradebookCollegesCollegeStudent.getStudentGrades().getScienceGradeResults().size()==1);
        assertTrue(gradebookCollegesCollegeStudent.getStudentGrades().getMathGradeResults().size()==1);

    }

    @AfterEach
    public void setupAfterTransaction() {
        jdbc.execute(sqlDeleteStudent);
        jdbc.execute(sqlDeleteMathGrade);
        jdbc.execute(sqlDeleteScienceGrade);
        jdbc.execute(sqlDeleteHistoryGrade);
    }
}
