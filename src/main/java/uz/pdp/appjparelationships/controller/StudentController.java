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

import java.util.ArrayList;
import java.util.List;
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
    @GetMapping("/forFaculty/{facultyId}")
    public Page<Student> getStudentListForFaculty(@PathVariable Integer facultyId,
                                                  @RequestParam int page){
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_FacultyId(facultyId, pageable);
        return studentPage;
    }
    //4. GROUP Tutor
    @GetMapping("/forGroupTutor/{groupId}")
    public Page<Student> getStudentListForGroup(@PathVariable Integer groupId,
                                                @RequestParam int page){
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroupId(groupId, pageable);
        return studentPage;
    }


    @PostMapping
    public String post(@RequestBody StudentDto studentDto){
        Optional<Group> optionalGroup = groupRepository.findById(studentDto.getGroupId());
        Optional<Address> optionalAddress = addressRepository.findById(studentDto.getAddressId());
        
        if (optionalAddress.isPresent()&& optionalGroup.isPresent()){
            Student student = new Student();
            student.setFirstName(studentDto.getFirstName());
            student.setLastName(studentDto.getLastName());
            student.setAddress(optionalAddress.get());
            student.setGroup(optionalGroup.get());

            List<Subject> subjects = new ArrayList<>();
            for (Integer subjectId : studentDto.getSubjectsId()) {
                Optional<Subject> optionalSubject = subjectRepository.findById(subjectId);
                optionalSubject.ifPresent(subjects::add);
            }

            student.setSubjects(subjects);
            studentRepository.save(student);
            return "student added";
        }
        return "group or address not found";
    }

    @DeleteMapping("/{id}")
    public String delete(@PathVariable Integer id){
        Optional<Student> optionalStudent = studentRepository.findById(id);
        if (optionalStudent.isPresent()){
            studentRepository.deleteById(id);
            return "student deleted";
        }
        return "student not found";
    }

    @PutMapping("/{id}")
    public String edit(@PathVariable Integer id, @RequestBody StudentDto studentDto){
        Optional<Student> optionalStudent = studentRepository.findById(id);
        Optional<Group> optionalGroup = groupRepository.findById(studentDto.getGroupId());
        Optional<Address> optionalAddress = addressRepository.findById(studentDto.getAddressId());

        if (optionalAddress.isPresent()&& optionalGroup.isPresent()&& optionalStudent.isPresent()){
            Student student = new Student();
            student.setId(id);
            student.setFirstName(studentDto.getFirstName());
            student.setLastName(studentDto.getLastName());
            student.setAddress(optionalAddress.get());
            student.setGroup(optionalGroup.get());

            List<Subject> subjects = new ArrayList<>();
            for (Integer subjectId : studentDto.getSubjectsId()) {
                Optional<Subject> optionalSubject = subjectRepository.findById(subjectId);
                optionalSubject.ifPresent(subjects::add);
            }

            student.setSubjects(subjects);
            studentRepository.save(student);
            return "student added";
        }
        return "group or address not found";
    }
}
