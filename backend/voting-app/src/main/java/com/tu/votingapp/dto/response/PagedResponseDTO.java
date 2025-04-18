package com.tu.votingapp.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponseDTO<T> {
    private List<T> content;
    private int page;           // current page number (0‑based)
    private int size;           // size per page
    private long totalElements; // total items across all pages
    private int totalPages;     // total number of pages
    private boolean last;       // is this the last page?
}