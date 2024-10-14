package com.luv2code.springmvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertIterableEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.ModelAndViewAssert;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.web.servlet.ModelAndView;

import com.luv2code.springmvc.models.CollegeStudent;
import com.luv2code.springmvc.models.GradebookCollegeStudent;
import com.luv2code.springmvc.repository.StudentDao;
import com.luv2code.springmvc.service.StudentAndGradeService;


@TestPropertySource("/application.properties")
@AutoConfigureMockMvc
@SpringBootTest
public class GradeBookControllerTest {

    private static MockHttpServletRequest request;

    @Autowired
    private JdbcTemplate jdbc;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StudentDao studentDao;
    
    @Mock
    private StudentAndGradeService studentCreateServiceMock;

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

    @BeforeAll
    //BeforeAll MUST be static.
    //Generating post request.
    public static void setup(){
        //USed to simulate HTTP request in text enviroments.
        request = new MockHttpServletRequest();
        request.setParameter("firstname", "Chad");
        request.setParameter("lastname", "Darby");
        request.setParameter("emailAddress", "chad.darby@luv2code_school.com");
    }

    @BeforeEach
    public void beforeEach() {
        jdbc.execute(sqlAddStudent);
        jdbc.execute(sqlAddMathGrade);
        jdbc.execute(sqlAddScienceGrade);
        jdbc.execute(sqlAddHistoryGrade);
    }

    @Test
    //Testing GET studentInformation from GradebookController
    public void getStudentHttpRequest() throws Exception{
        CollegeStudent studentOne = new GradebookCollegeStudent("Eric", "Roby", "eric_roby@riu.com");
        CollegeStudent studentTwo = new GradebookCollegeStudent("Chad", "Darby", "chad_darby@riu.com");
        
        List<CollegeStudent> collegeStudentList = new ArrayList<>(Arrays.asList(studentOne,studentTwo));
        
        //It allows you to control what data the service returns without needing an actual implementation.
        when(studentCreateServiceMock.getGradebook()).thenReturn(collegeStudentList);

        //Check that the list returned by mocked services matches the expected.
        assertIterableEquals(collegeStudentList, studentCreateServiceMock.getGradebook());

        //GET request.
        //We indicate the route and the it expects to retunr 200 OK,
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/")).andExpect(status().isOk()).andReturn();

        //We get the view and model attriibutes.
        ModelAndView mav = mvcResult.getModelAndView();

        //Check the view (data provided by the DDBB) value is the expected one.
        ModelAndViewAssert.assertViewName(mav, "index");
    }

    @Test
    //We are testing POST createStudent() from GRadebookController.
    public void createStudentHttpRequest() throws Exception{

        CollegeStudent studentOne = new CollegeStudent("Eric", "Roby", "eric_roby@luv2code.com");

        List<CollegeStudent> collegeStudentsList = new ArrayList(Arrays.asList(studentOne));

        when(studentCreateServiceMock.getGradebook()).thenReturn(collegeStudentsList);

        assertIterableEquals(collegeStudentsList, studentCreateServiceMock.getGradebook());

        //mockMvc allows Http request in resting context.
        //Perform is used to simulate HTTP request, which indicaate the URL
        MvcResult mvcResult = this.mockMvc.perform(post("/")
        .contentType(MediaType.APPLICATION_JSON)
        .param("firstname", request.getParameterValues("firstname"))
        .param("lastname", request.getParameterValues("lastname"))
        .param("emailAddress", request.getParameterValues("emailAddress")))
        .andExpect(status().isOk()).andReturn();
        
        //Contains the view name and the model attriibutes passed to the view
        ModelAndView mav = mvcResult.getModelAndView();

        //Checks that the view (data provided by the DDBB) name returned is "index".
        ModelAndViewAssert.assertViewName(mav, "index");

        //Get object from DDBB
        CollegeStudent verifyStudent = studentDao.findByEmailAddress("chad.darby@luv2code_school.com");
        
        //Assert the object is not null;
        assertNotNull(verifyStudent, " It must not be null");
    }

    @Test
    public void deleteStudentHttpRequest() throws Exception{
        assertTrue(studentDao.findById(1).isPresent());

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
        .get("/delete/student/{id}", 1)).andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "index");

        assertFalse(studentDao.findById(1).isPresent());
    }

    @Test 
    public void delteStudemtHttpRequestErrorPage() throws Exception{

        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders
        .get("/delete/student/{id}", 0)).andExpect(status().isOk()).andReturn();

        ModelAndView mav = mvcResult.getModelAndView();

        ModelAndViewAssert.assertViewName(mav, "error");
    }

    @AfterEach
    public void setupAfterTransaction() {
        jdbc.execute(sqlDeleteStudent);
        jdbc.execute(sqlDeleteMathGrade);
        jdbc.execute(sqlDeleteScienceGrade);
        jdbc.execute(sqlDeleteHistoryGrade);
    }



}
