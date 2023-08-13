package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Objects;

@Getter
@Setter
@ToString
@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "items")
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "user_id")
    private User owner;
    @NotBlank
    @Column(name = "name")
    private String name;
    @NotBlank
    @Column(name = "description")
    private String description;
    @NotNull
    @Column(name = "available")
    private Boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    @JoinColumn(name = "request_id")
    private ItemRequest request;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Item item = (Item) o;
        return Objects.equals(getId(), item.getId()) && Objects.equals(getOwner(),
                item.getOwner()) && Objects.equals(getName(), item.getName()) && Objects.equals(getDescription(),
                item.getDescription()) && Objects.equals(getAvailable(), item.getAvailable());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getOwner(), getName(), getDescription(), getAvailable());
    }
}
