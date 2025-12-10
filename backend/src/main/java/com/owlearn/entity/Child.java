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
    private String preferSubject;

    @Column
    private String preferTone;

    @Column
    private String preferArtstyle;

    @Column
    private String preferAge;

    @Column
    private String characterImageUrl;

    @Column
    private Integer credit = 0;

    @OneToMany(mappedBy = "child",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true)
    private List<ChildWord> words = new ArrayList<>();

    @OneToMany(mappedBy = "child",
            cascade = CascadeType.REMOVE,
            orphanRemoval = true)
    private List<TaleReview> reviews = new ArrayList<>();

    public void addCredit(int delta) {
        if (delta <= 0) return;
        if (this.credit == null) this.credit = 0;
        this.credit += delta;
    }

}
