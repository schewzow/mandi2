package de.conti.tires.mandi.backend.payload;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T> {
    //private List<T> content;
    private EmbeddedContent<T> embedded;
    private PageInfo page;
}

