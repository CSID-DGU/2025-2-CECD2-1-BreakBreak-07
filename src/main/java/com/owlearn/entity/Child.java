package com.owlearn.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy = "child",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true)
    private List<ChildWord> words = new ArrayList<>();

}
