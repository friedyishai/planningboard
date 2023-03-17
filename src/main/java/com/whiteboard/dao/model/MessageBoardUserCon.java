package com.whiteboard.dao.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDate;

@Data
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "CON_MessageToBoardAndUser")
public class MessageBoardUserCon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(nullable = false)
    private Integer id;

    @Column(nullable = false)
    private Integer messageId;

    @Column(nullable = false)
    private Integer boardId;

    @Column(nullable = false)
    private Integer userId;

    @Column(columnDefinition = "date")
    @CreatedDate
    private LocalDate createDate;
}
