package com.gitlab.muhammadkholidb.bianglala.service;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseService {
   
    @Autowired
    protected ObjectMapper objectMapper;

}
