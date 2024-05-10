package com.spring.security.entities;


import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_roles")
@Getter
@Setter
@Builder
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id")
    private Long roleId;
    private String name;

    @Getter
    public enum Values {
        ADMIN(1L),
        BASIC(2L);

        final long roleId;

        Values(long roleId) {
            this.roleId = roleId;
        }
    }
}
