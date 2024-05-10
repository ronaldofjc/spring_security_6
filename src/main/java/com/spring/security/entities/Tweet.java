package com.spring.security.entities;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Instant;

@Entity
@Table(name = "tb_tweets")
@Getter
@Setter
@Builder
public class Tweet {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "tweet_id")
    private Long tweedId;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private String content;
    @CreationTimestamp
    private Instant creationTimestamp;
}
