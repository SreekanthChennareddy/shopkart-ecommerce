package com.shopkart.common.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Wrapper for paginated API responses.
 * Used by: product listing, order history, search results, etc.
 *
 * @param <T> the type of items in the list
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PagedResponse<T>  {

    private List<T> content;    // the actual items
    private int pageNumber;     // current page (0-based)
    private int pageSize;       // items per page
    private int totalPages;     // total items across all pages
    private int totalElements;  // total number of pages
    private boolean last;       // is this the last page?
}
