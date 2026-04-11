package com.example.zadanieslave.service;

import com.example.zadanieslave.model.entity.ConstructionProject;
import com.example.zadanieslave.model.entity.User;
import com.example.zadanieslave.repository.ConstructionProjectRepository;
import com.example.zadanieslave.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TestDataService {

    private final UserRepository userRepository;
    private final ConstructionProjectRepository projectRepository;

    @Transactional
    public User createUser(String username, String fullName, String role) {
        User user = User.builder()
                .username(username)
                .passwordHash("temp") // в реальном проекте хэшировать
                .fullName(fullName)
                .role(role)
                .build();
        return userRepository.save(user);
    }

    @Transactional
    public ConstructionProject createProject(String name, String address, String projectCode) {
        ConstructionProject project = ConstructionProject.builder()
                .name(name)
                .address(address)
                .projectCode(projectCode)
                .build();
        return projectRepository.save(project);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<ConstructionProject> getAllProjects() {
        return projectRepository.findAll();
    }
}