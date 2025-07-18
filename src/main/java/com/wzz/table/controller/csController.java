package com.wzz.table.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.wzz.table.DTO.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SaCheckRole("1")
@RequestMapping("/api/cs")
public class csController {
    @GetMapping("/get")
    public Result<String> cs (){
        return Result.success("成功");
    }
}
