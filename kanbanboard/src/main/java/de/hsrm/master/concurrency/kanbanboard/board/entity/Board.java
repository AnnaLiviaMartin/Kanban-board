package de.hsrm.master.concurrency.kanbanboard.board.entity;

import de.hsrm.master.concurrency.kanbanboard.boardColumn.entity.BoardColumn;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Board {

    @Id
    @GeneratedValue
    private Long id;

    @Version
    private long version;

    @NotBlank(message = "Board-Name darf nicht leer sein")
    @Size(min = 1, max = 100, message = "Board-Name muss zwischen {min} und {max} Zeichen lang sein")
    @NotNull
    private String name;

    @Column(updatable = false)
    @NotNull
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("position ASC")
    private List<BoardColumn> columns = new ArrayList<>();

    public Board(String name) {
        this.name = name;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public void addColumn(BoardColumn column) {
        columns.add(column);
        column.setBoard(this);
    }

    public void removeColumn(BoardColumn column) {
        columns.remove(column);
        column.setBoard(null);
    }
}
