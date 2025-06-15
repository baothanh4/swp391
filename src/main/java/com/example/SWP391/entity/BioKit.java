package com.example.SWP391.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
public class BioKit {
    @Id
    private String KitID;

    private String name;
    private boolean isAvailable;
    private int quantity;
    @Column(name = "IsSelled")
    private int isSelled;
}
