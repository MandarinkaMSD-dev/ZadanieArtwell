package com.example.zadanieslave.controller;

import com.example.zadanieslave.model.entity.ConstructionProject;
import com.example.zadanieslave.model.entity.User;
import com.example.zadanieslave.service.TestDataService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final TestDataService testDataService;

    @GetMapping("/users/create")
    public User createUser(@RequestParam String username,
                           @RequestParam String fullName,
                           @RequestParam String role) {
        return testDataService.createUser(username, fullName, role);
    }

    @GetMapping("/projects/create")
    public ConstructionProject createProject(@RequestParam String name,
                                             @RequestParam String address,
                                             @RequestParam String projectCode) {
        return testDataService.createProject(name, address, projectCode);
    }

    @GetMapping("/users")
    public List<User> getAllUsers() {
        return testDataService.getAllUsers();
    }

    @GetMapping("/projects")
    public List<ConstructionProject> getAllProjects() {
        return testDataService.getAllProjects();
    }
}