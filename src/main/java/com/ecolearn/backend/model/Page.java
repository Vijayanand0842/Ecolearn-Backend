package com.ecolearn.backend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "pages")
public class Page {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="module_id")
    private Long moduleId;

    @Column(columnDefinition = "TEXT")
    private String content;

    @Column(name="page_number")
    private Integer pageNumber;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getModuleId() { return moduleId; }
    public void setModuleId(Long moduleId) { this.moduleId = moduleId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Integer getPageNumber() { return pageNumber; }
    public void setPageNumber(Integer pageNumber) { this.pageNumber = pageNumber; }
}
