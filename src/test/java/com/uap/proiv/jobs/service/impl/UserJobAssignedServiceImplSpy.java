package com.uap.proiv.jobs.service.impl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import com.uap.proiv.jobs.dto.Job;
import com.uap.proiv.jobs.dto.User;
import com.uap.proiv.jobs.dto.UserApiResponse;
import com.uap.proiv.jobs.dto.UserJobAssigned;
import com.uap.proiv.jobs.service.AssignedService;
import com.uap.proiv.jobs.service.JobService;
import com.uap.proiv.jobs.service.UserService;

@ExtendWith(MockitoExtension.class)
public class UserJobAssignedServiceImplSpy {

    @Mock
    JobService jobService;

    @Mock
    UserService userService;

    @Spy
    AssignedService assignedService = new AssignedServiceImpl();

    @InjectMocks
    UserJobAssignedServiceImpl serviceImpl;

    List<Job> jobs;
    List<User> users;
    UserApiResponse userApiResponse;

    @BeforeEach
    void setup() {
        jobs = new ArrayList<>();

        Job job1 = new Job();
        job1.setId(1);
        job1.setName("Developer");
        job1.setSalary(5000);
        job1.setHours(2000);
        job1.setResources(1);
        jobs.add(job1);

        Job job2 = new Job();
        job2.setId(2);
        job2.setName("Designer");
        job2.setSalary(4500);
        job2.setHours(1500);
        job2.setResources(1);
        jobs.add(job2);

        users = new ArrayList<>();
        User user1 = new User();
        user1.setId(1);
        user1.setEmail("ejemplo@as.com");
        user1.setAvatar(null);
        user1.setFirstName("juan");
        user1.setLastName("Garcia");
        users.add(user1);

        User user2 = new User();
        user2.setId(2);
        user2.setEmail("ejemplo2@as.com");
        user2.setAvatar(null);
        user2.setFirstName("diane");
        user2.setLastName("perez");
        users.add(user2);

        userApiResponse = new UserApiResponse();
        userApiResponse.setPage(1);
        userApiResponse.setPerPage(2);
        userApiResponse.setTotal(2);
        userApiResponse.setTotalPages(1);
        userApiResponse.setData(users);
    }

    @Test
    @DisplayName("Verifica que assign() use la logica real de AssignedServiceImpl (spy) para asignar usuarios a jobs")
    void assign_successOnePage_withRealAssignedService() {
        when(jobService.getAllJobs()).thenReturn(jobs);
        when(userService.search(1)).thenReturn(userApiResponse);
        when(userService.search(2)).thenReturn(null);

        List<UserJobAssigned> result = serviceImpl.assign();

        assertNotNull(result);
        assertEquals(2, result.size());


        for (UserJobAssigned uja : result) {
            assertEquals(1, uja.getUsers().size());
            int assignedUserId = uja.getUsers().get(0).getId();
            assertTrue(assignedUserId == 1 || assignedUserId == 2);
        }

        verify(jobService, times(1)).getAllJobs();
        verify(userService, times(1)).search(1);
        verify(assignedService, times(1)).create(eq(jobs), any());
    }
}