package com.luv2code.springmvc.repository;

import org.springframework.data.repository.CrudRepository;

import com.luv2code.springmvc.models.ScienceGrade;

public interface ScienceGradeDao extends CrudRepository<ScienceGrade, Integer> {
    public Iterable<ScienceGrade> findGradeByStudentId(int id);
    public void deleteByStudentId(int studentId);
}
