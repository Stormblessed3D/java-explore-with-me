package ru.practicum.controller.adminapi;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.CommentDtoRequest;
import ru.practicum.dto.CommentDtoResponse;
import ru.practicum.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

@Controller
@RequestMapping(path = "admin/comments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CommentAdminController {
    private final CommentService commentService;

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDtoResponse> updateComment(@RequestBody @Valid CommentDtoRequest commentDtoRequest,
                                                            @PathVariable @Positive Long commentId) {
        log.info("POST Admin запрос на изменение комментария с id {}", commentId);
        return ResponseEntity.ok(commentService.updateCommentAdmin(commentDtoRequest,commentId));
    }

    @GetMapping
    public ResponseEntity<List<CommentDtoResponse>> getCommentsByUser(@RequestParam(value = "userId") @Positive Long userId,
                                                                      @RequestParam(value = "from", defaultValue = "0") @Min(0) Integer from,
                                                                      @RequestParam(value = "size", defaultValue = "10") @Min(1) @Max(100) Integer size) {
        log.info("GET Admin запрос на получение комментариев пользователя с id {}", userId);
        return ResponseEntity.ok(commentService.getCommentsByUser(userId, from, size));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteUser(@PathVariable @Positive Long commentId) {
        log.info("DELETE Admin запрос на удаление комментария с id {}", commentId);
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }
}
