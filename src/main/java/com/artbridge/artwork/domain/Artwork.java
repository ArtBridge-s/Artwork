package com.artbridge.artwork.domain;

import com.artbridge.artwork.domain.enumeration.Status;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * A Artwork.
 */
@Entity
@Table(name = "artwork")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@SuppressWarnings("common-java:DuplicatedBlocks")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Artwork implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    @Column(name = "id")
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "short_description")
    private String shortDescription;

    @Column(name = "long_description")
    private String longDescription;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "artistname")
    private String artistname;

    @Column(name = "makingday")
    private String makingday;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private Status status;

    @OneToMany(mappedBy = "artwork")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "artwork" }, allowSetters = true)
    @ToString.Exclude
    private Set<Comment> comments = new HashSet<>();

    @OneToMany(mappedBy = "artwork")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "artwork" }, allowSetters = true)
    @ToString.Exclude
    private Set<View> views = new HashSet<>();

    @OneToMany(mappedBy = "artwork")
    @Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
    @JsonIgnoreProperties(value = { "artwork" }, allowSetters = true)
    @ToString.Exclude
    private Set<Like> likes = new HashSet<>();

    public Artwork id(Long id) {
        this.setId(id);
        return this;
    }

    public Artwork title(String title) {
        this.setTitle(title);
        return this;
    }

    public Artwork shortDescription(String shortDescription) {
        this.setShortDescription(shortDescription);
        return this;
    }

    public Artwork longDescription(String longDescription) {
        this.setLongDescription(longDescription);
        return this;
    }

    public Artwork imageUrl(String imageUrl) {
        this.setImageUrl(imageUrl);
        return this;
    }

    public Artwork artistname(String artistname) {
        this.setArtistname(artistname);
        return this;
    }

    public Artwork makingday(String makingday) {
        this.setMakingday(makingday);
        return this;
    }

    public Artwork status(Status status) {
        this.setStatus(status);
        return this;
    }

    public void setComments(Set<Comment> comments) {
        if (this.comments != null) {
            this.comments.forEach(i -> i.setArtwork(null));
        }
        if (comments != null) {
            comments.forEach(i -> i.setArtwork(this));
        }
        this.comments = comments;
    }

    public Artwork comments(Set<Comment> comments) {
        this.setComments(comments);
        return this;
    }

    public Artwork addComments(Comment comment) {
        this.comments.add(comment);
        comment.setArtwork(this);
        return this;
    }

    public Artwork removeComments(Comment comment) {
        this.comments.remove(comment);
        comment.setArtwork(null);
        return this;
    }

    public void setViews(Set<View> views) {
        if (this.views != null) {
            this.views.forEach(i -> i.setArtwork(null));
        }
        if (views != null) {
            views.forEach(i -> i.setArtwork(this));
        }
        this.views = views;
    }

    public Artwork views(Set<View> views) {
        this.setViews(views);
        return this;
    }

    public Artwork addViews(View view) {
        this.views.add(view);
        view.setArtwork(this);
        return this;
    }

    public Artwork removeViews(View view) {
        this.views.remove(view);
        view.setArtwork(null);
        return this;
    }

    public Set<Like> getLikes() {
        return this.likes;
    }

    public void setLikes(Set<Like> likes) {
        if (this.likes != null) {
            this.likes.forEach(i -> i.setArtwork(null));
        }
        if (likes != null) {
            likes.forEach(i -> i.setArtwork(this));
        }
        this.likes = likes;
    }

    public Artwork likes(Set<Like> likes) {
        this.setLikes(likes);
        return this;
    }

    public Artwork addLikes(Like like) {
        this.likes.add(like);
        like.setArtwork(this);
        return this;
    }

    public Artwork removeLikes(Like like) {
        this.likes.remove(like);
        like.setArtwork(null);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Artwork)) {
            return false;
        }
        return id != null && id.equals(((Artwork) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
