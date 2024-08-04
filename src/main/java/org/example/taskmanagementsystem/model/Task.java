package org.example.taskmanagementsystem.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;

@Entity
@Table(name = "tasks")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Task {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;




    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private String status;

    @Column(name = "priority")
    @Enumerated(EnumType.STRING)
    private String priority;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User assignee;



    @Column(name = "finished")
    private LocalDate finished;

    @CreatedDate()
    @Column(name = "created", updatable = false)
    private Long created;

    @LastModifiedDate
    @Column(name = "updated")
    private Long updated;





}