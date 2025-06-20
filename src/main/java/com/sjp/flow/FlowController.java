package com.sjp.flow;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;

@RequiredArgsConstructor
@RequestMapping("/")
@Controller
public class FlowController {

    private final FlowService flowService;

    /** 메인 페이지로 이동
     * main.html로 이동하는데 필요한 커스텀, 고정 확장자를 가져와서 main.html에 등록한다
     * @return main.html로 리턴
     */
    @GetMapping("")
    public String getMainPage(Model model) {
        List<FlowEntity> fixedExtensions = flowService.getFixedExtension(); // 고정 확장자 가져오기
        List<FlowEntity> customExtensions = flowService.getCustomExtension(); // 커스텀 확장자 가져오기

        model.addAttribute("fixedExtensions", fixedExtensions); // 고정 확장자 등록
        model.addAttribute("customExtensions", customExtensions); // 커스텀 확장자 등록
        return "main";
    }

    /** 메인 페이지에서 커스텀 확장자 추가
     * @return main.html로 redirect
     */
    @PostMapping("addExtension")
    public String addExtension(@RequestParam("newExtension") String newExtension,
                                RedirectAttributes redirectAttributes) {
        flowService.addNewExtension(newExtension); // 커스텀 확장자를 DB에 추가, 실패 시 main.html에 에러 표시
        redirectAttributes.addFlashAttribute("message", "성공적으로 추가하였습니다."); // 추가 성공 후 메세지
        return "redirect:/"; // main.html로 redirect
    }
    
    /** 메인 페이지에서 커스텀 확장자 제거
     * @return main.html로 redirect
     */
    @PostMapping("removeExtension")
    public String removeExtension(@RequestParam("customExtensions") String customExtensions, 
                                  RedirectAttributes redirectAttributes) {
        flowService.removeExtension(customExtensions); // 커스텀 확장자를 DB에서 삭제, 실패 시 main.html에 에러 표시
        redirectAttributes.addFlashAttribute("message", "성공적으로 삭제하였습니다."); // 삭제 성공 후 메세지
        return "redirect:/"; // main.html로 redirect
    }
    
    /** 메인 페이지에서 선택한 고정 확장자 DB에 반영
     * @return main.html로 redirect
     */
    @PostMapping("selectExtension")
    public String selectExtensions(@RequestParam(value = "fixedSelected", required = false) List<String> fixedSelected) {
        flowService.changeExtensionStatus(fixedSelected); // 선택된 고정 확장자를 DB에 반영, 실패 시 main.html에 에러 표시
        return "redirect:/";
    }

    /** 파일 업로드 페이지로 이동
     * 선택된 고정 확장자와 커스텀 확장자를 가져와서 upload.html에 등록한다
     * @return upload.html로 return
     */
    @GetMapping("uploadPage")
    public String uploadPage(Model model) {
        List<String> extensions = flowService.getCheckedExtension(); // 선택된 고정 확장자 + 커스텀 확장자 가져오기
        model.addAttribute("extensions", extensions); // 선택된 고정 확장자 + 커스텀 확장자 등록
        return "upload";
    }
    
    /** 파일 업로드 하기
     * upload.html에서 파일 등록 후 파일 확장자를 검사한다 (파일은 따로 저장하진 않는다)
     * @return upload.html로 redirect
     */
    @PostMapping("uploadFile")
    public String uploadFile(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
        String fileName = file.getOriginalFilename(); // 파일 이름

        if (file.isEmpty() || fileName == null || fileName.isBlank()) { // 만약 파일이 비었거나 파일 이름이 공백일 시 등록하라는 메세지 표시
            redirectAttributes.addFlashAttribute("messageUpload", "업로드할 파일을 선택하세요.");
            return "redirect:/uploadPage";
        }
        flowService.checkExtension(fileName); // 파일 확장자 확인, 부적절한 확장자일 시 에러
        redirectAttributes.addFlashAttribute("message", "성공적으로 업로드하셨습니다.");
        return "redirect:/uploadPage";
    }
}
