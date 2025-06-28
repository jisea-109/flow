package com.sjp.flow.CustomException;

import java.util.ArrayList;
import java.util.List;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.sjp.flow.FlowEntity;
import com.sjp.flow.FlowService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@ControllerAdvice
public class CustomExceptionHandler {

    private final FlowService flowService;

    @ExceptionHandler(CustomException.class)
    public ModelAndView handleCustomException(CustomException ex, Model model) {
        String viewName = ex.getTargetView(); // html 이름

        ModelAndView mav = new ModelAndView(viewName);
        mav.addObject("message", ex.getErrorMessage()); // 에러 메시지 전달

        if ("main".equals(viewName)) { // 만약 html 이름이 main일 시 커스텀 확장자와 고정 확장자 다시 전달
            List<FlowEntity> fixedExtensions = flowService.getFixedExtension();
            List<FlowEntity> customExtensions = flowService.getCustomExtension();

            model.addAttribute("fixedExtensions", fixedExtensions);
            model.addAttribute("customExtensions", customExtensions);
        }

        if ("upload".equals(viewName)) { // 만약 html 이름이 uploaad일 시 선택된 고정 확장자와 커스텀 확장자 전달
            List<String> extensions = flowService.getCheckedExtension();
            model.addAttribute("extensions", extensions);
        }

        return mav;
    }
}
