package pl.mojastrona.post;

import java.time.LocalDateTime;
import java.util.Set;

public record FindPostRequest(Set<PostStatus> postStatuses,
                              String text,
                              LocalDateTime publicationDate,
                              LocalDateTime createDateTimeMin,
                              LocalDateTime createDateTimeMax) {

}
