package de.conti.tires.mandi.backend.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageInfo {
    private Integer page;
    private Integer size;
    private Long totalElements;
    private Integer totalPages;
    private boolean lastPage;
}
