package com.wzz.table.controller;

import com.wzz.table.DTO.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/cs")
public class csController {
    @GetMapping("/get")
    public Result<String> cs (){
        return Result.success("成功");
    }
}
