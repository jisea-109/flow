package com.sjp.flow;

import java.util.ArrayList;
import java.util.List;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor
@RequestMapping("/")
@Controller
public class FlowController {

    private final FlowService flowService;

    @GetMapping("")
    public String getMainPage(Model model, HttpSession session) {
        List<FlowEntity> extensions = flowService.getExtensionLists();

        List<String> selectedExtensions = (List<String>) session.getAttribute("selectedExtensions");

        if (selectedExtensions == null) {
            selectedExtensions = flowService.getCustomExtension(); // 커스텀 확장자만 불러오기
            session.setAttribute("selectedExtensions", selectedExtensions);
        }

        model.addAttribute("extensions", extensions);
        model.addAttribute("selectedExtensions", selectedExtensions);
        return "main";
    }

    @PostMapping("addExtension")
    public String addExtension(@RequestParam("newExtension") String newExtension,
                                HttpSession session,
                                RedirectAttributes redirectAttributes) {

        flowService.addNewExtension(newExtension);
        List<String> addedExtensions = (List<String>) session.getAttribute("selectedExtensions");
        if (addedExtensions == null) {
            addedExtensions = new ArrayList<>();
        }

        // 3. 중복 체크 후 추가
        if (!addedExtensions.contains(newExtension)) {
            addedExtensions.add(newExtension);
        }

        // 4. 세션에 반영
        session.setAttribute("addedExtensions", addedExtensions);

        // 5. 메시지 설정 후 리다이렉트
        redirectAttributes.addFlashAttribute("message", "성공적으로 추가하였습니다.");
        return "redirect:/";
    }
    
    @PostMapping("removeExtension")
    public String removeExtension(@RequestParam("addedExtension") String addedExtension, 
                                  RedirectAttributes redirectAttributes) {
        flowService.removeExtension(addedExtension);
        redirectAttributes.addFlashAttribute("message", "성공적으로 삭제하였습니다.");
        return "redirect:/";
    }
    
    @PostMapping("selectExtension")
    public String selectExtensions(@RequestParam(value = "fixedSelected", required = false) List<String> fixedSelected,
                                    HttpSession session) {
        List<String> selectedExtensions = new ArrayList<>();
        if (fixedSelected != null) {
            selectedExtensions.addAll(fixedSelected);
        }
        session.setAttribute("selectedExtensions", selectedExtensions);
        return "redirect:/";
    }

    @GetMapping("uploadPage")
    public String uploadPage(Model model, HttpSession session) {

        List<String> selectedExtensions = (List<String>) session.getAttribute("selectedExtensions");

        if (selectedExtensions == null) {
            selectedExtensions = new ArrayList<>();
        }

        List<String> customExtensions = flowService.getCustomExtension();

        model.addAttribute("extensions", selectedExtensions);
        model.addAttribute("customExtensions", customExtensions);
        return "upload";
    }
    
    @PostMapping("uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, 
                              RedirectAttributes redirectAttributes,
                              HttpSession session) {
        String fileName = file.getOriginalFilename();

        if (file.isEmpty() || fileName == null || fileName.isBlank()) {
            redirectAttributes.addFlashAttribute("messageUpload", "업로드할 파일을 선택하세요.");
            return "redirect:/uploadPage";
        }
        List<String> selectedExtensions = (List<String>) session.getAttribute("selectedExtensions");
        flowService.checkExtension(fileName, selectedExtensions);
        redirectAttributes.addFlashAttribute("message", "성공적으로 업로드하셨습니다.");
        return "redirect:/uploadPage";
    }

}
