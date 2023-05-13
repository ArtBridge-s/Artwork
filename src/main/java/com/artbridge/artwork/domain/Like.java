package com.artbridge.artwork.domain;

import com.artbridge.artwork.domain.valueobject.Member;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import javax.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Like.
 */
@Entity
@Table(name = "jhi_like")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Like implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Embedded
    private Member member;

    @ManyToOne
    @JsonIgnoreProperties(value = { "comments", "views", "likes" }, allowSetters = true)
    private Artwork artwork;

    public Like id(Long id) {
        this.setId(id);
        return this;
    }

    public Like member(Member member) {
        this.setMember(member);
        return this;
    }

    public Like artwork(Artwork artwork) {
        this.setArtwork(artwork);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Like)) {
            return false;
        }
        return id != null && id.equals(((Like) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
