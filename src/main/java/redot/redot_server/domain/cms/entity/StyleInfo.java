package redot.redot_server.domain.cms.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Builder(access = AccessLevel.PRIVATE)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Table(name = "style_info")
public class StyleInfo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "customer_id", nullable = false, unique = true)
    private Customer customer;

    @Column(nullable = false)
    private String font;
    @Column(nullable = false)
    private String color;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Theme theme;

    public static StyleInfo create(String font, String color, Theme theme, Customer customer) {
        return StyleInfo.builder()
                .font(font)
                .color(color)
                .theme(theme)
                .customer(customer)
                .build();
    }

    public void update(String font, String color, Theme theme) {
        this.font = font;
        this.color = color;
        this.theme = theme;
    }
}
