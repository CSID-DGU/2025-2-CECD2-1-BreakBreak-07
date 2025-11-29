package com.owlearn.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "child_words",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"child_id", "word"}) // 같은 자녀에게 같은 단어는 한 번만
        }
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChildWord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "child_id", nullable = false)
    private Child child;

    @Column(nullable = false, length = 100)
    private String word;        // 영단어

    @Column(nullable = false, length = 255)
    private String meaning;     // 한글 뜻

}
