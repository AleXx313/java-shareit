package ru.practicum.shareit.item.comment.model;

import lombok.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter @Setter @ToString
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "content")
    private String text;
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "item_id")
    private Item item;
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "user_id")
    private User author;
    private LocalDateTime created;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(getId(), comment.getId()) && Objects.equals(getText(),
                comment.getText()) && Objects.equals(getItem(), comment.getItem()) && Objects.equals(getAuthor(),
                comment.getAuthor()) && Objects.equals(getCreated(), comment.getCreated());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getText(), getItem(), getAuthor(), getCreated());
    }
}
