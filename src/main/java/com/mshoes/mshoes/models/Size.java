package com.mshoes.mshoes.models;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.mshoes.mshoes.config.LongDeserializer;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "SIZES")
public class Size {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @JsonDeserialize(using = LongDeserializer.class)
    private Long id;

    @Column
    private String value;

    @Column
    private int total;

    @Column
    private int sold;

    @ManyToOne()
    @JoinColumn(name = "color_id")
    private Color color;

    @OneToMany(mappedBy = "size")
    private List<OrderItem> orderItems;
}
