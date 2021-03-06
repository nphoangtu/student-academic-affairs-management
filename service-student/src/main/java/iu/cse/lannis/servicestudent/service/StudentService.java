package iu.cse.lannis.servicestudent.service;

import iu.cse.lannis.servicestudent.dto.CreateStudentDto;
import iu.cse.lannis.servicestudent.entity.Student;
import iu.cse.lannis.servicestudent.exception.ServiceStudentException;
import iu.cse.lannis.servicestudent.kafka.producer.Sender;
import iu.cse.lannis.servicestudent.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StudentService {

    @Autowired
    private final StudentRepository studentRepository;

    @Value("${spring.kafka.topic.studentCreated}")
    private String STUDENT_CREATED_TOPIC;

    @Autowired
    private final Sender sender;

    public List<Student> getAllStudents() {
        return this.studentRepository.findAll();
    }

    public Student getStudentByEmail(String email) {
        Optional<Student> student = this.studentRepository.findByEmail(email);
        if (student.isEmpty()) {
            throw new ServiceStudentException("Student not found", HttpStatus.NOT_FOUND);
        }
        return student.get();
    }

    public Student getStudentById(Long id) {
        Optional<Student> student = this.studentRepository.findById(id);
        if (student.isEmpty()) {
            throw new ServiceStudentException("Student not found", HttpStatus.NOT_FOUND);
        }
        return student.get();
    }

    public Student registerStudent(CreateStudentDto createStudentDto) {

        Student createdStudent = new Student();
        createdStudent.setStudentName(createStudentDto.getStudentName());
        createdStudent.setEmail(createStudentDto.getEmail());
        createdStudent.setUsername(createStudentDto.getUsername());
        createdStudent.setPassword(createStudentDto.getPassword());
        this.studentRepository.save(createdStudent);
        boolean isStudentSaved = this.studentRepository.exists(Example.of(createdStudent));
        if (isStudentSaved) {
            // send kafka message
            sender.send(STUDENT_CREATED_TOPIC, createdStudent);
            return createdStudent;
        } else {
            throw new ServiceStudentException("Cannot create student", HttpStatus.BAD_REQUEST);
        }
    }

    public Student getStudentByUsername (String username) {
        return this.studentRepository
                .findByUsername(username)
                .orElseThrow(() -> new ServiceStudentException("Student Not found", HttpStatus.NOT_FOUND));
    }
}
