package com.spring.security.controller.dto;

public record FeedItemDto(
    long tweetId,
    String content,
    String username
) {
}
