package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CommentDtoRequest;
import ru.practicum.dto.CommentDtoResponse;
import ru.practicum.exception.CommentException;
import ru.practicum.exception.EventStatusException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.EventState;
import ru.practicum.model.User;
import ru.practicum.repository.CommentRepository;
import ru.practicum.repository.EventRepository;
import ru.practicum.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CommentMapper commentMapper;

    @Override
    @Transactional
    public CommentDtoResponse createCommentPrivate(CommentDtoRequest commentDtoRequest, Long userId, Long eventId) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id=%d was not found", userId)));
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id=%d was not found", eventId)));
        Comment comment = commentMapper.commentDtoToComment(commentDtoRequest, event, author);
        return commentMapper.commentToCommentDtoResponse(commentRepository.save(comment));
    }

    @Override
    @Transactional
    public CommentDtoResponse updateCommentPrivate(CommentDtoRequest commentDtoRequest, Long userId, Long commentId) {
        Comment commentToUpdate = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Comment with id=%d was not found", commentId)));
        checkIsAuthor(commentToUpdate.getAuthor().getId(), userId);
        LocalDateTime modifiedOn = LocalDateTime.now();
        checkTimeUpdate(commentToUpdate.getCreatedOn(), modifiedOn);
        commentToUpdate.setText(commentDtoRequest.getText());
        commentToUpdate.setModifiedOn(modifiedOn);
        return commentMapper.commentToCommentDtoResponse(commentRepository.save(commentToUpdate));
    }

    @Override
    @Transactional
    public CommentDtoResponse updateCommentAdmin(CommentDtoRequest commentDtoRequest, Long commentId) {
        Comment commentToUpdate = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("User with id=%d was not found", commentId)));
        commentToUpdate.setText(commentDtoRequest.getText());
        commentToUpdate.setModifiedOn(LocalDateTime.now());
        return commentMapper.commentToCommentDtoResponse(commentRepository.save(commentToUpdate));
    }

    @Override
    public List<CommentDtoResponse> getCommentsByUser(Long userId, Integer from, Integer size) {
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdOn"));
        Page<Comment> commentsPageable = commentRepository.findByAuthorId(userId, pageRequest);
        List<Comment> comments = commentsPageable.getContent();
        return commentMapper.commentToCommentDtoResponse(comments);
    }

    @Override
    @Transactional
    public void deleteComment(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new EntityNotFoundException(String.format("Comment with id=%d was not found", commentId));
        }
        commentRepository.deleteById(commentId);
    }

    @Override
    @Transactional
    public List<CommentDtoResponse> getCommentsByEvent(Long eventId, Integer from, Integer size) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Event with id=%d was not found", eventId)));
        if (event.getState() != EventState.PUBLISHED) {
            throw new EventStatusException(String.format("Event with id=%d is not published", eventId));
        }
        int page = from / size;
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdOn"));
        Page<Comment> commentsPageable = commentRepository.findByEventId(eventId, pageRequest);
        List<Comment> comments = commentsPageable.getContent();
        return commentMapper.commentToCommentDtoResponse(comments);
    }

    private void checkIsAuthor(Long authorId, Long userId) {
        if (!Objects.equals(authorId, userId)) {
            throw new CommentException("The comment can be updated only by its author");
        }
    }

    private void checkTimeUpdate(LocalDateTime createdOn, LocalDateTime modifiedOn) {
        if (!createdOn.plusHours(1).isAfter(modifiedOn)) {
            throw new CommentException("The comment can be updated only within one hour after creation");
        }
    }
}
