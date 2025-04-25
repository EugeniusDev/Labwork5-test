package edu.froliak.labwork5.model;

/*
  @author eugen
  @project labwork5
  @class Weapon
  @version 1.0.0
  @since 4/13/2025 - 09.34
*/

import lombok.*;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
@Document
public class Weapon {
    @Id
    private String id;
    private String name;
    private String code;
    private String description;

    private LocalDateTime createDate;
    private List<LocalDateTime> updateDates;

    public Weapon(String name, String code, String description) {
        this.description = description;
        this.code = code;
        this.name = name;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof Weapon weapon)) return false;

        return getId().equals(weapon.getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
