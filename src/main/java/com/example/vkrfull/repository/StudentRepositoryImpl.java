package com.example.vkrfull.repository;

import com.example.vkrfull.model.Student;
import com.example.vkrfull.model.StudentFilterBody;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.apache.logging.log4j.util.Strings.isEmpty;

public class StudentRepositoryImpl {
    @PersistenceContext
    private EntityManager entityManager;

    public List<Student> find(StudentFilterBody studentFilterBody) {

        CriteriaBuilder criteriaBuilder  = entityManager.getCriteriaBuilder();
        CriteriaQuery<Student> criteriaQuery = criteriaBuilder.createQuery(Student.class);
        Root<Student> root = criteriaQuery.from(Student.class);
        criteriaQuery.select(root);

        List<Predicate> predicates = new ArrayList<>();
        if (!isEmpty(studentFilterBody.getFirstName())) {
            Predicate predicate = criteriaBuilder.like(root.get("firstName"), "%"+studentFilterBody.getFirstName()+"%");
            predicates.add(predicate);
        }
        if (!isEmpty(studentFilterBody.getLastName())) {
            Predicate predicate = criteriaBuilder.like(root.get("lastName"), "%"+studentFilterBody.getLastName()+"%");
            predicates.add(predicate);
        }
        if (!isEmpty(studentFilterBody.getDepartment())) {
            Predicate predicate = criteriaBuilder.equal(root.get("department"), studentFilterBody.getDepartment());
            predicates.add(predicate);
        }
        if (nonNull(studentFilterBody.getReady())) {
            Predicate predicate = criteriaBuilder.equal(root.get("ready"), studentFilterBody.getReady());
            predicates.add(predicate);
        }
        if (nonNull(studentFilterBody.getMark())) {
            Predicate predicate = criteriaBuilder.equal(root.get("mark"), studentFilterBody.getMark());
            predicates.add(predicate);
        }
        if (!isEmpty(studentFilterBody.getPatronymic())) {
            Predicate predicate = criteriaBuilder.equal(root.get("patronymic"), studentFilterBody.getPatronymic());
            predicates.add(predicate);
        }
        if (nonNull(studentFilterBody.getExerciseId())) {
            Predicate predicate = criteriaBuilder.equal(root.get("exerciseId"), studentFilterBody.getExerciseId());
            predicates.add(predicate);
        }
        Predicate[] predicatesArray = predicates.toArray(new Predicate[0]);
        criteriaQuery.select(root).where(criteriaBuilder.and(predicatesArray));
        TypedQuery<Student> query = entityManager.createQuery(criteriaQuery);

        List<Student> students = query.getResultList();
        System.out.println(students);
        return students;
    }
}
