package com.mshoes.mshoes.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "COLORS")
public class Color {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String value;

    @OneToMany(mappedBy = "color",fetch = FetchType.EAGER)
    private List<Size> sizes = new ArrayList<>();

    @ManyToOne()
    @JoinColumn(name = "product_id")
    private Product product;
}
