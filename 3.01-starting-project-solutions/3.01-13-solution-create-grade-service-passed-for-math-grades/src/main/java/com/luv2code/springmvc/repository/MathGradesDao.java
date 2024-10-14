package com.luv2code.springmvc.repository;

import org.springframework.data.repository.CrudRepository;

public interface MathGradesDao extends CrudRepository<MathGrade, Integer> {

    public Iterable<MathGrade> findGradeByStudentId(int id);
}
