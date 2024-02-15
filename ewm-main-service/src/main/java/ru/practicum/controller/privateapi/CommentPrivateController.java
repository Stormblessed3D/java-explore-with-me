package ru.practicum.controller.privateapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.CommentDtoRequest;
import ru.practicum.dto.CommentDtoResponse;
import ru.practicum.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Controller
@RequestMapping(path = "/users/{userId}/comments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CommentPrivateController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDtoResponse> createComment(@RequestBody @Valid CommentDtoRequest commentDtoRequest,
                                                            @PathVariable @Positive Long userId,
                                                            @RequestParam(value = "eventId") @Positive Long eventId) {
        log.info("POST Private запрос на создание комментария пользователем с id {} к событию с id {}", userId, eventId);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createCommentPrivate(commentDtoRequest,
                userId, eventId));
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDtoResponse> updateComment(@RequestBody @Valid CommentDtoRequest commentDtoRequest,
                                                            @PathVariable @Positive Long userId,
                                                            @PathVariable @Positive Long commentId) {
        log.info("POST Private запрос на изменение комментария с id {} пользователем с id {}", commentId, userId);
        return ResponseEntity.ok(commentService.updateCommentPrivate(commentDtoRequest,
                userId, commentId));
    }
}
