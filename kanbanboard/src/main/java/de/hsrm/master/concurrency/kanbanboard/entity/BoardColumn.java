package de.hsrm.master.concurrency.kanbanboard.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class BoardColumn {

    @Id
    @GeneratedValue
    private Long id;

    @Version
    private long version;

    @NotBlank(message = "Spaltenname darf nicht leer sein")
    @Size(min = 1, max = 80)
    @NotNull
    private String name;

    @Min(value = 1, message = "WIP-Limit muss mindestens {value} sein")
    private Integer wipLimit;

    @Min(value = 0, message = "Position muss mindestens {value} sein")
    private int position;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private Board board;

    @OneToMany(mappedBy = "column", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<Task> tasks = new ArrayList<>();

    public BoardColumn(String name, Integer wipLimit, int position) {
        this.name = name;
        this.wipLimit = wipLimit;
        this.position = position;
    }

    public void addTask(Task task) {
        tasks.add(task);
        task.setColumn(this);
    }

    public void removeTask(Task task) {
        tasks.remove(task);
        task.setColumn(null);
    }
}
