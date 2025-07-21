package com.wzz.table.controller;

import cn.dev33.satoken.annotation.SaCheckRole;
import com.wzz.table.DTO.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@SaCheckRole("1")
@RequestMapping("/api/cs")
public class csController {
    @GetMapping("/get")
    public Result<String> cs (){
        LocalDateTime sixAm = LocalDateTime.now().withHour(6).withMinute(0).withSecond(0).withNano(0);
        System.out.println(sixAm);
        return Result.success("成功");
    }
}
