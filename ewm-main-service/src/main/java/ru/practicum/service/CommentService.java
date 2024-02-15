package ru.practicum.service;

import ru.practicum.dto.CommentDtoRequest;
import ru.practicum.dto.CommentDtoResponse;

import java.util.List;

public interface CommentService {
    CommentDtoResponse createCommentPrivate(CommentDtoRequest commentDtoRequest, Long userId, Long eventId);

    CommentDtoResponse updateCommentPrivate(CommentDtoRequest commentDtoRequest, Long userId, Long commentId);

    CommentDtoResponse updateCommentAdmin(CommentDtoRequest commentDtoRequest, Long commentId);

    List<CommentDtoResponse> getCommentsByUser(Long userId, Integer from, Integer size);

    void deleteComment(Long commentId);

    List<CommentDtoResponse> getCommentsByEvent(Long eventId, Integer from, Integer size);
}
