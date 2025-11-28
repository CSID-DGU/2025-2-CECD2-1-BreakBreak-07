package com.owlearn.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="child_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class ChildItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="child_id")
    private Child child;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="item_id")
    private Item item;

    private Boolean owned;  // true / false
}
