package pl.mojastrona.comment;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    private final CommentService commentService;


    @PostMapping
    public void create(@Valid @RequestBody CreateCommentRequest commentRequest) {

        commentService.create(commentRequest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadCommentResponse> read(@PathVariable("id") Long id) {

        ReadCommentResponse comment = commentService.findById(id);
        return ResponseEntity.ok(comment);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable("id") Long id, @Valid @RequestBody UpdateCommentRequest updateCommentRequest) {

        commentService.update(id, updateCommentRequest);
        return ResponseEntity.ok().build();
    }


    @GetMapping
    public ResponseEntity<Page<ReadCommentResponse>> find(@RequestParam(value = "pid") Long postId,
                                                          Pageable pageable){
        return ResponseEntity.ok(commentService.find(postId, pageable));
    }

}
