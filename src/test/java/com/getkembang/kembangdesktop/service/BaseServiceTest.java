package com.getkembang.kembangdesktop.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.mockito.Spy;

public abstract class BaseServiceTest {
    
    @Spy
    protected ObjectMapper objectMapper;
}
