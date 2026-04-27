package com.ecolearn.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "quizzes")
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="module_id")
    private Long moduleId;

    @Column(name="questions_json", columnDefinition = "TEXT")
    private String questionsJson;

    @Transient
    private Object questions; // to map JSON response

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getModuleId() { return moduleId; }
    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }

    public String getQuestionsJson() { return questionsJson; }
    public void setQuestionsJson(String questionsJson) { this.questionsJson = questionsJson; }

    public Object getQuestions() { return questions; }
    public void setQuestions(Object questions) { this.questions = questions; }
}
