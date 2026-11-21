package com.example.demo.service;

import com.example.demo.entity.Request;
import com.example.demo.repository.RequestRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminRequestService {

    private final RequestRepository requestRepository;

    public AdminRequestService(RequestRepository requestRepository) {
        this.requestRepository = requestRepository;
    }

    @Transactional(readOnly = true)
    public List<Request> getAllRequests() {
        return requestRepository.findAllByOrderByCreatedAtDesc();
    }
}