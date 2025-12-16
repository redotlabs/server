package redot.redot_server.domain.redot.app.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import redot.redot_server.domain.redot.plan.entity.Plan;
import redot.redot_server.domain.redot.member.entity.RedotMember;
import redot.redot_server.global.common.entity.BaseTimeEntity;

@Getter
@Entity
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@AllArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Builder(access = lombok.AccessLevel.PRIVATE)
@Table(name = "redot_apps")
public class RedotApp extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "redot_member_id")
    private RedotMember owner;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RedotAppStatus status;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "text")
    private String remark;

    // 초기 관리자 계정 생성 여부(default=false)
    @Column(nullable = false)
    @Builder.Default
    private boolean isCreatedManager = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan plan;

    public static RedotApp create(String name, RedotMember owner, Plan plan) {
        return RedotApp.builder()
                .name(name)
                .owner(owner)
                .status(RedotAppStatus.ACTIVE)
                .plan(plan)
                .build();
    }

    public static RedotApp createWithoutOwner(String name, Plan plan) {
        return RedotApp.builder()
                .name(name)
                .status(RedotAppStatus.ACTIVE)
                .plan(plan)
                .build();
    }

    public void markManagerCreated() {
        this.isCreatedManager = true;
    }

    public boolean isOwner(Long redotMemberId) {
        return this.owner != null && this.owner.getId().equals(redotMemberId);
    }

    public void updatePlan(Plan plan) {
        this.plan = plan;
    }

    public void updateStatus(RedotAppStatus status, String remark) {
        this.status = status;
        this.remark = remark;
    }
}
