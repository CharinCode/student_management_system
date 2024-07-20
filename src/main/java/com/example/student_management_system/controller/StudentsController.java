package com.example.student_management_system.controller;

import com.example.student_management_system.controller.dto.StudentDTO;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class StudentsController {

    @PersistenceContext
    private EntityManager entityManager;

    @GetMapping("/students")
    public List<Map<String, Object>> getStudents() {
        List<Tuple> getStudents = entityManager.createNativeQuery("SELECT * FROM students", Tuple.class).getResultList();
        List<Map<String, Object>> students = new ArrayList<>();
        for (Tuple tuple : getStudents) {
            Map<String, Object> student = new HashMap<>();
            student.put("student_id", tuple.get("student_id", Integer.class));
            student.put("student_name", tuple.get("student_name", String.class));
            students.add(student);
        }
        return students;
    }

    @GetMapping("/student")
    public Object student(
            @RequestParam("id") int id
    ){
        List<Tuple> getStudent =  entityManager.createNativeQuery("SELECT * FROM students WHERE student_id = :id",
                Tuple.class).setParameter("id",id).getResultList();
        System.out.println(getStudent);
        if (getStudent.isEmpty()){
            return "ไม่มีข้อมูลในระบบ";
        }else {
            Tuple tuple = getStudent.get(0);
            StudentDTO studentDTO = new StudentDTO();
            studentDTO.setStudent_id(tuple.get("student_id",Integer.class));
            studentDTO.setStudent_name(tuple.get("student_name",String.class));
            return studentDTO;
        }
    }

    @Transactional
    @PostMapping("insert")
    public Object addStudent (@RequestBody StudentDTO studentDTO){
     int row = entityManager.createNativeQuery("INSERT INTO students (student_name) VALUES (:student_name)")
             .setParameter("student_name",studentDTO.getStudent_name()).executeUpdate();
    if (row > 0){
        return  "บันทึกข้อมูลเรียบร้อย";
    }
    else {
        return false;
    }
    }

    @Transactional
    @PutMapping("update")
    public Object updateStudent (@RequestBody StudentDTO studentDTO){
        int row = entityManager.createNativeQuery("UPDATE students SET student_name = :newStudent_name WHERE student_id = :student_id")
                .setParameter("student_id",studentDTO.getStudent_id())
                .setParameter("newStudent_name",studentDTO.getStudent_name()).executeUpdate();
        if (row > 0){
            return  "อัพเดทข้อมูลเรียบร้อย";
        }
        else {
            return false;
        }
    }

    @Transactional
    @DeleteMapping("delete")
    public Object deleteStudent (@RequestParam("id") int id){
        int row = entityManager.createNativeQuery("DELETE FROM students WHERE student_id = :id")
                .setParameter("id",id).executeUpdate();
        if (row > 0){
            return  "ลบข้อมูลเรียบร้อย";
        }
        else {
            return false;
        }
    }
}
