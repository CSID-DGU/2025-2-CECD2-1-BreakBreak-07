package com.owlearn.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private  String category;
    private  String name;
}
