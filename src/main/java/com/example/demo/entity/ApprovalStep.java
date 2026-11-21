
package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "approval_steps")
@Getter
@Setter
@NoArgsConstructor
public class ApprovalStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String stepName;

    @Column(nullable = false)
    private Integer stepOrder;

    @Column(nullable = false, length = 30)
    private String approverRole;

    @Column(nullable = false)
    private boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private com.example.demo.enums.RequestType requestType;
}
