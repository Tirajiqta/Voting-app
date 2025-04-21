package com.tu.votingapp.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MaintenanceController {

    /**
     * Health check endpoint.
     * Returns "OK" when the application is running.
     */
    @GetMapping("/maintenance")
    public String maintenance() {
        return "OK";
    }
}