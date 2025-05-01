package ru.hpclab.hl.module1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/core")
public class CrashController {

    @GetMapping("/crash")
    public void crash() {
        System.exit(1);
    }
}
