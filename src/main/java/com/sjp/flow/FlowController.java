package com.sjp.flow;

import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@RequestMapping("/")
@Controller
public class FlowController {

    private final FlowService flowService;

    @GetMapping("main")
    public String getMainPage(Model model) {
        model.addAttribute("extensions", flowService.getExtensionLists()); // FlowEntity로 넘김
        return "main";
    }

    @PostMapping("addExtension")
    public String addExtension(@RequestParam("newExtension") String newExtension, Model model) {
        flowService.addNewExtension(newExtension);
        model.addAttribute("message", "성공적으로 추가하였습니다.");
        return "redirect:/main";
    }
    
    @PostMapping("removeExtension")
    public String removeExtension(@RequestParam("addedExtension") String addedExtension, Model model) {
        flowService.removeExtension(addedExtension);
        model.addAttribute("message", "성공적으로 삭제하였습니다.");
        return "redirect:/main";
    }
    
    @PostMapping("selectExtension")
    public String selectExtensions(@RequestParam(value = "fixedSelected", required = false) List<String> fixedSelected,
                                    HttpSession session, 
                                    Model model) {
        List<String> selectedExtensions = new ArrayList<>();
        if (fixedSelected != null) {
            selectedExtensions.addAll(fixedSelected);
        }
        selectedExtensions.addAll(flowService.getCustomExtension());

        session.setAttribute("selectedExtensions", selectedExtensions);
        return "redirect:/main";
    }

    @GetMapping("uploadPage")
    public String uploadPage(Model model, HttpSession session) {
        List<String> selectedExtensions = new ArrayList<>();

        Object sessionAttr = session.getAttribute("selectedExtensions");
        if (sessionAttr instanceof List<?>) {
            for (Object item : (List<?>) sessionAttr) {
                if (item instanceof String) {
                    selectedExtensions.add((String) item);
                }
            }
        }
        model.addAttribute("extensions", selectedExtensions);
        return "upload";
    }
    
    @PostMapping("uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, Model model) {
        String fileName = file.getOriginalFilename();
        flowService.checkExtension(fileName);
        model.addAttribute("message", "성공적으로 업로드하셨습니다.");
        return "redirect:/upload";
    }

}
