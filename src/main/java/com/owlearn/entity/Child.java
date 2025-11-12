package com.owlearn.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="childs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Child {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer age;

    @Column
    private String prefer;

    @Column
    private String characterImageUrl;
}
