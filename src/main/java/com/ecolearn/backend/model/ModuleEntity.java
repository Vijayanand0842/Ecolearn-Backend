package com.ecolearn.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "modules")
public class ModuleEntity { // named ModuleEntity because Module is a reserved keyword in java 9+
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="lesson_id")
    private Long lessonId;

    private String title;
    
    @Column(name="order_index")
    private Integer orderIndex;

    @Transient
    private Integer completed; // Used for frontend mapping

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getLessonId() { return lessonId; }
    public void setLessonId(Long lessonId) { this.lessonId = lessonId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Integer getOrderIndex() { return orderIndex; }
    public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }

    public Integer getCompleted() { return completed; }
    public void setCompleted(Integer completed) { this.completed = completed; }
}
