package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.entity.Subject;
import uz.pdp.appjparelationships.payload.StudentDto;
import uz.pdp.appjparelationships.repository.AddressRepository;
import uz.pdp.appjparelationships.repository.GroupRepository;
import uz.pdp.appjparelationships.repository.StudentRepository;
import uz.pdp.appjparelationships.repository.SubjectRepository;

import java.sql.Struct;
import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    SubjectRepository subjectRepository;

    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAll(pageable);
        return studentPage;
    }

    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
        return studentPage;
    }

    //3. FACULTY DEKANAT
    @GetMapping(value = "forFaculty/{facultyId}")
    public Page<Student> getStudentListForFaculty(@PathVariable Integer facultyId,
                                                  @RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAllByGroup_Faculty_Id(facultyId, pageable);
    }

    //4. GROUP OWNER
    @GetMapping(value = "forGroup/{groupId}")
    public Page<Student> getStudentListForGroup(@PathVariable Integer groupId,
                                                  @RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAllByGroupId(groupId, pageable);
    }

    @PostMapping
    public String addStudent(@RequestBody StudentDto dto) {
        Optional<Address> optionalAddress =
                addressRepository.findById(dto.getAddressId());
        if (!optionalAddress.isPresent()) {
            return "Address is not found";
        }

        Optional<Group> optionalGroup =
                groupRepository.findById(dto.getGroupId());
        if (!optionalGroup.isPresent()) {
            return "Group is not found";
        }

        Optional<Subject> optionalSubject =
                subjectRepository.findById(dto.getSubjectId());
        if (!optionalSubject.isPresent()) {
            return "Subject is not found";
        }

        Student student = new Student();
        student.setAddress(optionalAddress.get());
        student.setGroup(optionalGroup.get());
        student.setSubjects(Collections.singletonList(optionalSubject.get()));
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());

        studentRepository.save(student);

        return "Student is added";
    }

    @DeleteMapping(value = "/{id}")
    public String deleteStudent(@PathVariable Integer id) {
        try {
            studentRepository.deleteById(id);
            return "Student is deleted";
        } catch (Exception e) {
            return "Student is not deleted";
        }
    }

    @PutMapping(value = "/{id}")
    public String editStudent(@PathVariable Integer id, @RequestBody StudentDto dto) {
        Optional<Address> optionalAddress =
                addressRepository.findById(dto.getAddressId());
        if (!optionalAddress.isPresent()) {
            return "Address is not found";
        }

        Optional<Group> optionalGroup =
                groupRepository.findById(dto.getGroupId());
        if (!optionalGroup.isPresent()) {
            return "Group is not found";
        }

        Optional<Subject> optionalSubject =
                subjectRepository.findById(dto.getSubjectId());
        if (!optionalSubject.isPresent()) {
            return "Subject is not found";
        }

        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (!optionalStudent.isPresent()) {
            return "Student not found";
        }

        Student student = optionalStudent.get();
        student.setFirstName(dto.getFirstName());
        student.setLastName(dto.getLastName());
        student.setGroup(optionalGroup.get());
        student.setAddress(optionalAddress.get());
        student.getSubjects().add(optionalSubject.get());

        studentRepository.save(student);

        return "Student is edited";
    }
}

