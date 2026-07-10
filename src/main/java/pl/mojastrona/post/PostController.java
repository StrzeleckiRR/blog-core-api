package pl.mojastrona.post;

import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.mojastrona.accountant.AccountantRepository;
import pl.mojastrona.user.UserRepository;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    public PostController(PostService postService, UserRepository userRepository, AccountantRepository accountantRepository) {
        this.postService = postService;
    }

    @PostMapping
    public void create(@Valid @RequestBody CreatePostRequest postRequest) {

        postService.create(postRequest);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReadPostResponse> read(@PathVariable("id") Long id) {
        return ResponseEntity.ok(postService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> update(@PathVariable("id") Long id, @Valid @RequestBody UpdatePostRequest updatePostRequest) {

        postService.update(id, updatePostRequest);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/archive")
    public ResponseEntity<Void> update(@PathVariable("id") Long id) {

        postService.archive(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        postService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<FindPostResponse>> find(@RequestParam(value = "q", defaultValue = "") String textContaining,
                                     @RequestParam int page,
                                     @RequestParam int size){
        return ResponseEntity.ok(postService.find(textContaining, page, size));
    }

    @PostMapping("/find")
    public ResponseEntity<Page<FindPostResponse>> find(@RequestBody FindPostRequest findPostRequest, Pageable pageable){
        Page<FindPostResponse> body = postService.find(findPostRequest, pageable);

        return ResponseEntity.ok(body);
    }

    @PostMapping("/findForLogged")
    public ResponseEntity<Page<FindPostResponse>> findForLoggedUser(@RequestBody FindPostRequest findPostRequest, Pageable pageable){
        Page<FindPostResponse> body = postService.findForLogged(findPostRequest, pageable);
        return ResponseEntity.ok(body);
    }

}

