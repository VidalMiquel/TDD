package com.luv2code.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;

import com.luv2code.component.MvcTestingExampleApplication;
import com.luv2code.component.dao.ApplicationDao;
import com.luv2code.component.models.CollegeStudent;
import com.luv2code.component.models.StudentGrades;
import com.luv2code.component.service.ApplicationService;

@SpringBootTest(classes = MvcTestingExampleApplication.class)
public class MockAnnotationTest {

    @Autowired
    ApplicationContext context;

    @Autowired
    CollegeStudent studentOne;

    @Autowired
    StudentGrades studentGrades;

    //@Mock
    @MockBean
    private ApplicationDao applicationDao;

    //@InjectMocks
    @Autowired
    private ApplicationService applicationService;

    @BeforeEach
    public void BeforeEach() {
        studentOne.setFirstname("Eric");
        studentOne.setLastname("Roby");
        studentOne.setEmailAddress("eric.roby@riu.com");
        studentOne.setStudentGrades(studentGrades);
    }

    @DisplayName("When & Verify")
    @Test
    public void assertEqualsTestAddGrades() {
        //Set up expectations
        when(applicationDao.addGradeResultsForSingleClass(
                studentGrades.
                        getMathGradeResults())).
                thenReturn(100.0);
        //Verify expectations
        assertEquals(100.0,
                applicationService.addGradeResultsForSingleClass(
                        studentOne.
                                getStudentGrades().
                                getMathGradeResults()));
        verify(applicationDao, times(1)).
                addGradeResultsForSingleClass(studentGrades.getMathGradeResults());

    }

    @DisplayName("Find Gpa")
    @Test
    public void assertEqualsTestFindGpa() {
        when(applicationDao.findGradePointAverage((studentGrades.getMathGradeResults()))).thenReturn(88.31);

        assertEquals(88.31, applicationService.findGradePointAverage(studentOne.getStudentGrades().getMathGradeResults()));
    }

    @DisplayName("Not null")
    @Test
    public void testAssertNotNull() {
        when(applicationDao.checkNull(studentGrades.getMathGradeResults())).thenReturn(true);

        assertNotNull(applicationService.checkNull(studentOne.getStudentGrades().getMathGradeResults()), "Objest should not be null");
    }

    @DisplayName("ThorwRuntimeError")
    @Test
    public void thorwRuntimeError() {
        CollegeStudent nullStudent = (CollegeStudent) context.getBean("collegeStudent");
        //Thorw the exception when the method is called.
        doThrow(new RuntimeException()).when(applicationDao).checkNull(nullStudent);

        assertThrows(RuntimeException.class, () -> {
            applicationService.checkNull(nullStudent);
        });

        verify(applicationDao, times(1)).checkNull(nullStudent);
    }

    //Multiple calls
    @DisplayName("Multiple calls")
    @Test
    public void stubbingConsecutiveCalls() {
        CollegeStudent nullStudent = (CollegeStudent) context.getBean("collegeStudent");

        when(applicationDao.checkNull(nullStudent)).thenThrow(new RuntimeException()).thenReturn("Do not throw exception second time");

        //First call managment
        assertThrows(RuntimeException.class, () -> {
            applicationService.checkNull(nullStudent);
        });

        //Second call managment
        assertEquals("Do not throw exception second time", applicationService.checkNull(nullStudent));

        verify(applicationDao, times(2)).checkNull(nullStudent);
        
    }
}
