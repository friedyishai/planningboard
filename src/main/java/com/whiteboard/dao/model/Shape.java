package com.whiteboard.dao.model;

import jakarta.persistence.*;
import javafx.scene.shape.Polygon;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Shape")
public class Shape {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false, columnDefinition = "numeric(10, 2)")
    private Double centerX;

    @Column(nullable = false, columnDefinition = "numeric(10, 2)")
    private Double centerY;

    @Column(columnDefinition = "numeric(10, 2)")
    private Double width;

    @Column(columnDefinition = "numeric(10, 2)")
    private Double height;

    @Column(columnDefinition = "numeric(10, 2)")
    private Double radius;

//    @Column(columnDefinition = "geometry")
//    private Polygon points;

    @Column(columnDefinition = "numeric(10, 2)")
    private Double startX;

    @Column(columnDefinition = "numeric(10, 2)")
    private Double startY;

    @Column(columnDefinition = "numeric(10, 2)")
    private Double endX;

    @Column(columnDefinition = "numeric(10, 2)")
    private Double endY;

    @Column(nullable = false, columnDefinition = "varchar(25)")
    private String shapeType;

    @Column(nullable = false, columnDefinition = "varchar(25)")
    private String frameColor;

    @Column(nullable = false, columnDefinition = "varchar(25)")
    private String fillColor;

    @Column(nullable = false, columnDefinition = "bit")
    private Boolean isActive;
}
