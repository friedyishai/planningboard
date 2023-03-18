package com.whiteboard.dao.model;

import com.whiteboard.enums.ShapeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Shape")
public class ShapeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false, columnDefinition = "numeric(10, 2)")
    private Double x1;

    @Column(nullable = false, columnDefinition = "numeric(10, 2)")
    private Double y1;

    @Column(columnDefinition = "numeric(10, 2)")
    private Double x2;

    @Column(columnDefinition = "numeric(10, 2)")
    private Double y2;

    @Column(columnDefinition = "numeric(10, 2)")
    private Double x3;

    @Column(columnDefinition = "numeric(10, 2)")
    private Double y3;

    @Column(columnDefinition = "numeric(10, 2)")
    private Double width;

    @Column(columnDefinition = "numeric(10, 2)")
    private Double height;

    @Column(columnDefinition = "numeric(10, 2)")
    private Double radiusX;

    @Column(columnDefinition = "numeric(10, 2)")
    private Double radiusY;

    @Column(nullable = false, columnDefinition = "int")
    private ShapeEnum shapeType;

    @Column(nullable = false, columnDefinition = "varchar(25)")
    private String fillColor;

    @Column(nullable = false, columnDefinition = "varchar(25)")
    private String strokeColor;

    @Column(nullable = false, columnDefinition = "bit")
    private Boolean isActive;
}
