package com.whiteboard.dao.model;

import jakarta.persistence.*;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.MouseEvent;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Text")
public class TextEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false, columnDefinition = "numeric(10, 2)")
    private Double startX;

    @Column(nullable = false, columnDefinition = "numeric(10, 2)")
    private Double startY;

    @Column(nullable = false, columnDefinition = "varchar(250)")
    private String content;

    @Column(nullable = false, columnDefinition = "varchar(25)")
    private String color;

    @Column(nullable = false, columnDefinition = "varchar(25)")
    private String font;

    @Column(nullable = false, columnDefinition = "numeric(10, 2)")
    private Double fontSize;

    @Column(nullable = false, columnDefinition = "bit")
    private Boolean isActive;

    @Column(columnDefinition = "datetime")
    @UpdateTimestamp
    private LocalDateTime updateDate;
}
