package com.sjp.flow;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@RequestMapping("/")
@Controller
public class FlowController {
    private final FlowService flowService;

    @GetMapping("")
    public String getMainPage(Model model) {
        model.addAttribute("extenstions", flowService.getExtensionLists());
        return "main";
    }

    @PostMapping("uploadFile")
    public String postMethodName(@RequestParam("file") MultipartFile file, Model model) {
        String fileName = file.getOriginalFilename();
        flowService.checkExtension(fileName);
        model.addAttribute("message", "성공적으로 업로드하셨습니다.");
        return "";
    }
    
    

}
