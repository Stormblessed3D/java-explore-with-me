package ru.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.dto.CommentDtoRequest;
import ru.practicum.dto.CommentDtoResponse;
import ru.practicum.model.Comment;
import ru.practicum.model.Event;
import ru.practicum.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public abstract class CommentMapper {
    public CommentDtoResponse commentToCommentDtoResponse(Comment comment) {
        return CommentDtoResponse.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .createdOn(comment.getCreatedOn())
                .modifiedOn(comment.getModifiedOn())
                .build();
    }

    public List<CommentDtoResponse> commentToCommentDtoResponse(Iterable<Comment> comments) {
        List<CommentDtoResponse> dtos = new ArrayList<>();
        for (Comment comment : comments) {
            dtos.add(commentToCommentDtoResponse(comment));
        }
        return dtos;
    }

    public Comment commentDtoToComment(CommentDtoRequest commentDtoRequest, Event event, User author) {
        return Comment.builder()
                .text(commentDtoRequest.getText())
                .event(event)
                .author(author)
                .createdOn(LocalDateTime.now())
                .modifiedOn(null)
                .build();
    }
}
