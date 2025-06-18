package com.sjp.flow;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.sjp.flow.CustomException.CustomErrorCode;
import com.sjp.flow.CustomException.CustomException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class FlowService {

    private final FlowRepository flowRepository;

    public void addNewExtension(String newExtension) {
        boolean exists = flowRepository.existsByExtensionName(newExtension);
        if (exists) {
            throw new CustomException(CustomErrorCode.DUPLICATE_EXTENSION_FOUND);
        }
        if (!newExtension.toLowerCase().matches("^[a-zA-Z0-9/]+$") || newExtension.contains("//")) {
			throw new CustomException(CustomErrorCode.CHARACTER_NOT_ALLOWED);
		}
		else if (newExtension.toLowerCase().length() > 20) {
			throw new CustomException(CustomErrorCode.EXTENSION_LENGTH);
		}
		else if (flowRepository.count() >= 200) {
			throw new CustomException(CustomErrorCode.EXTENSION_MAX);
		}
		else {
            FlowEntity newEntity = FlowEntity.builder()
                                             .extensionName(newExtension)
                                             .type(FlowExtensionType.CUSTOM)
                                             .build();
			flowRepository.save(newEntity);
		}
    }

    public void checkExtension(String extension) {
        String fileExtension = extension.substring(extension.lastIndexOf(".") + 1).toLowerCase();
        if (!flowRepository.existsByExtensionName(fileExtension)) {
            throw new CustomException(CustomErrorCode.NOT_ALLOWED_FILE_EXTENSION);
        }
    }

    public void removeExtension(String extension) {
        FlowEntity toRemoveExtensions = flowRepository.findByExtensionName(extension).orElseThrow(() -> new CustomException(CustomErrorCode.EXTENSION_NOT_FOUND));
        if (toRemoveExtensions.getType() != FlowExtensionType.CUSTOM) {
            throw new CustomException(CustomErrorCode.INAPPROPIRATE_EXTENSION_TYPE);
        }
        flowRepository.delete(toRemoveExtensions);
    }
    
    public List<String> getCustomExtension() {
       return flowRepository.findByType(FlowExtensionType.CUSTOM).stream()
                .map(FlowEntity::getExtensionName)
                .collect(Collectors.toList());
    }
    //     fixedExtensions.add("bat");
    //     fixedExtensions.add("cmd");
    //     fixedExtensions.add("com");
    //     fixedExtensions.add("cpl");
    //     fixedExtensions.add("exe");
    //     fixedExtensions.add("scr");
    //     fixedExtensions.add("js");

    public List<FlowEntity> getExtensionLists() {
        List<FlowEntity> ExtensionLists = flowRepository.findAll();
        return ExtensionLists;
    }
}
