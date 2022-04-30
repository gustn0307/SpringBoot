package org.zerock.guestbook.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Builder
@AllArgsConstructor
@Data
public class PageRequestDTO { // 화면에서 전달되는 목록 관련된 데이터에 대한 DTO ( From 화면)
    private int page; // 현재 페이지 번호
    private int size; // 한번에 보여질 페이지 갯수
    private String type;
    private String keyword;

    public PageRequestDTO(){ // 기본값으로 각각 1과 10을 설정
        this.page=1;
        this.size=10;
    }

    public Pageable getPageable(Sort sort){
        return PageRequest.of(page -1,size,sort);
    }
}
