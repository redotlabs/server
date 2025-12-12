package redot.redot_server.domain.redot.consultation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import redot.redot_server.domain.admin.dto.request.ConsultationUpdateRequest;
import redot.redot_server.domain.redot.consultation.dto.request.ConsultationCreateRequest;
import redot.redot_server.global.common.entity.BaseTimeEntity;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Builder(access = lombok.AccessLevel.PRIVATE)
@Table(name = "consultations")
public class Consultation extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String email;

    private String phone;

    @Column(length = 1000, nullable = false)
    private String content;

    private String currentWebsiteUrl;

    private String page;

    @Column(length = 1000)
    private String remark;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ConsultationStatus status;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ConsultationType type;

    public static Consultation create(ConsultationCreateRequest request) {
        return Consultation.builder()
                .email(request.email())
                .phone(request.phone())
                .content(request.content())
                .currentWebsiteUrl(request.currentWebsiteUrl())
                .page(request.page())
                .status(ConsultationStatus.PENDING)
                .type(request.type())
                .build();
    }

    public void update(ConsultationUpdateRequest request) {
        this.email = request.email();
        this.phone = request.phone();
        this.content = request.content();
        this.currentWebsiteUrl = request.currentWebsiteUrl();
        this.page = request.page();
        this.remark = request.remark();
        this.status = request.status();
        this.type = request.type();
    }
}
