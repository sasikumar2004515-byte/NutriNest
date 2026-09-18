package com.nutrinest.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String categoryName;

    @Column(length = 500)
    private String description;

    private String imageUrl;

    private Boolean active = true;

    @Transient
    public String getDisplayImageUrl() {
        if (imageUrl == null || imageUrl.isBlank()) return "/images/OIP.jpeg";
        if (imageUrl.startsWith("/")) return imageUrl;
        if (imageUrl.startsWith("uploads/")) return "/" + imageUrl;
        return "/images/" + imageUrl;
    }

}
