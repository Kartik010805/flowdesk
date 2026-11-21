
package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.entity.ApprovalStep;
import com.example.demo.entity.Department;

import com.example.demo.enums.EmploymentStatus;
import com.example.demo.enums.Role;
import com.example.demo.enums.RequestType;

import com.example.demo.repository.UserRepository;
import com.example.demo.repository.ApprovalStepRepository;
import com.example.demo.repository.DepartmentRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Bean
    CommandLineRunner initializeData(
            UserRepository userRepository,
            ApprovalStepRepository approvalStepRepository,
            DepartmentRepository departmentRepository,
            PasswordEncoder passwordEncoder) {

        return args -> {

            // Create initial ADMIN account
            if (userRepository.findByEmail(adminEmail).isEmpty()) {

                User admin = new User();
                admin.setFullName("System Administrator");
                admin.setEmail(adminEmail);
                admin.setPassword(passwordEncoder.encode(adminPassword));
                admin.setRole(Role.ADMIN);
                admin.setStatus(EmploymentStatus.ACTIVE);

                userRepository.save(admin);

                System.out.println("Initial ADMIN account created.");
            }

            // Create default departments
            String[][] departments = {
                    {"Human Resources", "Employee management and recruitment"},
                    {"Information Technology", "IT support and infrastructure"},
                    {"Finance", "Financial operations and expense management"},
                    {"Engineering", "Software development and engineering"},
                    {"Operations", "Business operations and administration"},
                    {"Marketing", "Marketing and communications"}
            };

            for (String[] departmentData : departments) {

                if (departmentRepository.findByName(departmentData[0]).isEmpty()) {

                    Department department = new Department();
                    department.setName(departmentData[0]);
                    department.setDescription(departmentData[1]);

                    departmentRepository.save(department);
                }
            }

            System.out.println("Default departments initialized.");

            // Create default manager approval steps
            for (RequestType type : RequestType.values()) {

                if (approvalStepRepository
                        .findByRequestTypeAndActiveTrueOrderByStepOrderAsc(type)
                        .isEmpty()) {

                    ApprovalStep step = new ApprovalStep();

                    step.setStepName("Manager Approval");
                    step.setStepOrder(1);
                    step.setApproverRole("MANAGER");
                    step.setActive(true);
                    step.setRequestType(type);

                    approvalStepRepository.save(step);
                }
            }

            System.out.println("Default manager approval steps initialized.");
        };
    }
}
