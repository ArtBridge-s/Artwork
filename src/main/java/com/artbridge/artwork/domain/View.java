package com.artbridge.artwork.domain;

import com.artbridge.artwork.domain.valueobject.Member;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.Objects;
import javax.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A View.
 */
@Entity
@Table(name = "view")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class View implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Embedded
    @AttributeOverride(name = "id", column = @Column(name = "member_id"))
    @AttributeOverride(name = "name", column = @Column(name = "member_name"))
    private Member member;

    @ManyToOne
    @JsonIgnoreProperties(value = { "comments", "views", "likes" }, allowSetters = true)
    private Artwork artwork;

    public View id(Long id) {
        this.setId(id);
        return this;
    }

    public View member(Member member) {
        this.setMember(member);
        return this;
    }

    public View artwork(Artwork artwork) {
        this.setArtwork(artwork);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        View view = (View) o;
        return getId() != null && Objects.equals(getId(), view.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
