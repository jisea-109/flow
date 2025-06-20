package com.sjp.flow;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Table(name="extensions")
public class FlowEntity {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    @Column(name = "extension_name", nullable = false, unique = true)
    private String extensionName; // 예: exe, js, bat

    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private FlowExtensionType type; // 고정 확장자 또는 커스텀 확장자

    @Column(name = "status")
    private boolean checked; // 커스텀 확장자 = true, 선택된 고정 확장자 = true, 선택안된 고정 확장자 = false

    public void setChecked (boolean checked) { // 고정 확장자 setter
        this.checked = checked;
    }
}
