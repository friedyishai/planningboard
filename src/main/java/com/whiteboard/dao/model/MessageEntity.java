package com.whiteboard.dao.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Message")
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false, columnDefinition = "varchar(150)")
    private String content;

    @Column(nullable = false, columnDefinition = "bit")
    private Boolean isActive;

    @Column(columnDefinition = "datetime")
    @CreatedDate
    private LocalDateTime createDate;
}
