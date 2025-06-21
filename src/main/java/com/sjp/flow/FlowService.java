package com.sjp.flow;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.sjp.flow.CustomException.CustomErrorCode;
import com.sjp.flow.CustomException.CustomException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FlowService {

    private final FlowRepository flowRepository; 

    /** 새로운 확장자 추가
     * 새로운 확장자를 추가하기 전 여러가지 요소를 검사한 뒤 소문자로 바꿔서 저장
     * @param newExtension 새로 추가할 확장자
     */
    public void addNewExtension(String newExtension) {
        boolean exists = flowRepository.existsByExtensionName(newExtension);
        if (exists) { // 추가할 확장자가 이미 있을 시
            throw new CustomException(CustomErrorCode.DUPLICATE_EXTENSION_FOUND, "main");
        }
		else if (flowRepository.count() >= 200) { // 확장자 개수가 200개가 넘을 시 에러
			throw new CustomException(CustomErrorCode.EXTENSION_MAX, "main");
		}
		else { // 아무런 문제가 없을 시 커스텀 확장자로 저장. 단, 소문자로 저장하고 커스텀 확장자는 Default가 checked = true이다.
            FlowEntity newEntity = FlowEntity.builder()
                                             .extensionName(newExtension.toLowerCase())
                                             .type(FlowExtensionType.CUSTOM)
                                             .checked(true)
                                             .build();
			flowRepository.save(newEntity); // 저장함
		}
    }

    /** 업로드 파일 확장자 확인
     * 업로드 파일 전체 이름을 가져와서 파일 확장자를 검사하고 만약 부적절한 확장자로 확인되면 에러
     * @param filename 파일 전체 이름
     * @param selectedExtensions 선택된 고정 확장자와 커스텀 확장자 모음
     */
    public void checkExtension(String filename) {

        if (filename == null || !filename.contains(".")) { // 만약 파일 이름이 없거나 '.' 자체가 없을 시
            throw new CustomException(CustomErrorCode.NOT_ALLOWED_FILE_EXTENSION, "upload");
        }
        else if (filename.startsWith(".") || filename.endsWith(".")) { // 확장자 회피 시도하는 파일 방지
            throw new CustomException(CustomErrorCode.NOT_ALLOWED_FILE_EXTENSION, "upload");
        }

        String fileExtension = filename.substring(filename.lastIndexOf(".") + 1).toLowerCase(); // 파일 이름 . 뒤로 전체 소문자

        if (!fileExtension.toLowerCase().matches("^[a-zA-Z0-9]+$") || fileExtension.contains("//")) { // 특수문자나 파일변형 방지 체크
			throw new CustomException(CustomErrorCode.CHARACTER_NOT_ALLOWED, "upload");
		}
		else if (fileExtension.toLowerCase().length() > 20) { // 확장자 이름 20글자 초과 시 에러
			throw new CustomException(CustomErrorCode.EXTENSION_LENGTH, "upload");
		}
        
        List<String> selectedExtensions = getCheckedExtension(); // 체크된 확장자와 커스텀 확장자 둘 다 가져오기

        if (selectedExtensions.contains(fileExtension)) { // 만약 파일 확장자가 포함이 될 시 에러
            throw new CustomException(CustomErrorCode.NOT_ALLOWED_FILE_EXTENSION, "upload");
        }
        
    }

    /** 커스텀 확장자 제거
     * 추가한 커스텀 확장자를 DB에서 제거하는 함수
     * @param extension 제거할 커스텀 확장자 이름
     */
    public void removeExtension(String extension) {
        // DB에서 커스텀 확장자 찾는데 만약 확장자 이름을 찾을 수 없으면 에러
        FlowEntity toRemoveExtensions = flowRepository.findByExtensionName(extension).orElseThrow(() -> new CustomException(CustomErrorCode.EXTENSION_NOT_FOUND, "main"));

        // 만약 확장자를 찾았는데 커스텀 확장자가 아닌 고정 확장자면 에러
        if (toRemoveExtensions.getType() != FlowExtensionType.CUSTOM) {
            throw new CustomException(CustomErrorCode.INAPPROPIRATE_EXTENSION_TYPE, "main");
        }

        // 커스텀 확장자 삭제
        flowRepository.delete(toRemoveExtensions);
    }

    /** 고정 확장자 상태 변경
     * 고정 확장자를 선택하게 되면 파일 업로드할 때 제한해야 하기에 고정 확장자의 상태 변경을 해주는 함수
     * @param selectedExtensions 선택된 고정 확장자
     */
    @Transactional // 엔티티 변경하고 DB에 반영하기에 필요한 annotation
    public void changeExtensionStatus(List<String> selectedExtensions) {
        List<FlowEntity> fixedExtensions = flowRepository.findByType(FlowExtensionType.FIXED); // 고정 함수 찾기

        if(fixedExtensions.isEmpty()) { // 만약 고정 함수가 없을 시 에러
            throw new CustomException(CustomErrorCode.EXTENSION_NOT_FOUND, "main");
        }

        for (FlowEntity extension : fixedExtensions) { // 선택된 고정 확장자는 true, 선택안된 고정 확장자는 false로 변경
            boolean shouldBeChecked = selectedExtensions != null && selectedExtensions.contains(extension.getExtensionName());
            extension.setChecked(shouldBeChecked); 
        }
    }

    /*
     * 커스텀 확장자 리스트 가져오기
     */
    public List<FlowEntity> getCustomExtension() { 
       return flowRepository.findByType(FlowExtensionType.CUSTOM);
    }

    /*
     * 고정 확장자 리스트 가져오기
     */
    public List<FlowEntity> getFixedExtension() {
       return flowRepository.findByType(FlowExtensionType.FIXED);
    }

    /*
     * 고정 확장자중 선택된 고정 확장자와 커스텀 확장자를 가져옴
     */
    public List<String> getCheckedExtension() {
       return flowRepository.findByCheckedTrue().stream()
                .map(FlowEntity::getExtensionName)
                .collect(Collectors.toList());
    }
}
