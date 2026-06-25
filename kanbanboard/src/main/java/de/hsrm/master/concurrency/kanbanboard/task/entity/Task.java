package de.hsrm.master.concurrency.kanbanboard.task.entity;

import de.hsrm.master.concurrency.kanbanboard.boardColumn.entity.BoardColumn;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Task {

    @Id
    @GeneratedValue
    private Long id;

    @Version
    private long version;

    @NotBlank(message = "Task-Titel darf nicht leer sein")
    @Size(min = 1, max = 200, message = "Titel muss zwischen {min} und {max} Zeichen lang sein")
    @Column(nullable = false)
    private String title;

    @Size(max = 2000, message = "Beschreibung darf maximal {max} Zeichen lang sein")
    private String description;

    @Min(0)
    @Column(nullable = false)
    private int position;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "column_id", nullable = false)
    private BoardColumn column;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Task(String title, String description, int position) {
        this.title = title;
        this.description = description;
        this.position = position;
    }
}
