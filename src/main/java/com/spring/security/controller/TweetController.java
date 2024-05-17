package com.spring.security.controller;

import com.spring.security.controller.dto.CreateTweetDto;
import com.spring.security.controller.dto.FeedDto;
import com.spring.security.controller.dto.FeedItemDto;
import com.spring.security.entities.Role;
import com.spring.security.entities.Tweet;
import com.spring.security.repository.TweetRepository;
import com.spring.security.repository.UserRepository;
import jakarta.persistence.Id;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
public class TweetController {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public TweetController(TweetRepository tweetRepository, UserRepository userRepository) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/feed")
    public ResponseEntity<FeedDto> feed(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize
    ) {
        var tweets = tweetRepository
            .findAll(PageRequest.of(page, pageSize, Sort.Direction.DESC, "creationTimestamp"))
            .map(tweet -> new FeedItemDto(tweet.getTweedId(), tweet.getContent(), tweet.getUser().getUsername()));

        return ResponseEntity.ok(new FeedDto(
            tweets.getContent(), page, pageSize, tweets.getTotalPages(), tweets.getNumberOfElements())
        );
    }

    @PostMapping("/tweets")
    public ResponseEntity<Void> createTweet(@RequestBody CreateTweetDto dto, JwtAuthenticationToken token) {
        var user = userRepository.findById(UUID.fromString(token.getName()));
        var tweet = Tweet.builder()
            .content(dto.content())
            .user(user.orElse(null))
            .build();

        tweetRepository.save(tweet);

        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @DeleteMapping("/tweets/{id}")
    public ResponseEntity<Id> deleteTweet(@PathVariable("id") Long tweetId, JwtAuthenticationToken token) {
        var user = userRepository.findById(UUID.fromString(token.getName()))
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        var tweet = tweetRepository.findById(tweetId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var isAdmin = user.getRoles()
            .stream()
            .anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        if (isAdmin || tweet.getUser().getUserId().equals(UUID.fromString(token.getName()))) {
            tweetRepository.deleteById(tweetId);
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok().build();
    }
}
