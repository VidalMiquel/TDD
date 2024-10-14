package com.luv2code.springmvc.repository;

import org.springframework.data.repository.CrudRepository;

import com.luv2code.springmvc.models.MathGrade;

public interface MathGradeDao extends CrudRepository<MathGrade, Integer>{
    public Iterable<MathGrade> findGradeByStudentId(int studentID);
    public void deleteByStudentId(int studentId);
}
