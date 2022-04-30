package org.zerock.mreview.dto;

import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
public class PageResultDTO<DTO, EN> { // 화면에서 필요한 결과 DTO ( To 화면)
    private List<DTO> dtoList; // DTO 리스트
    
    private  int totalPage; // 총 페이지 번호
    private int page; // 현재 페이지 번호
    private int size; // 목록 사이즈
    private int start,end; // 시작 페이지 번호, 끝 페이지 번호
    private boolean prev,next; // 이전, 다음
    private List<Integer> pageList; // 페이지 번호 목록

    public PageResultDTO(Page<EN> result, Function<EN, DTO> fn){
        dtoList = result.stream().map(fn).collect(Collectors.toList());
        totalPage = result.getTotalPages();
        makePageList(result.getPageable());
    }

    private void makePageList(Pageable pageable) {
        this.page=pageable.getPageNumber()+1; // 0부터 시작하므로 1을 추가
        this.size=pageable.getPageSize();

        int tempEnd=(int)(Math.ceil(page/10.0))*10; // 현재 페이지를 기반으로 임시 마지막 페이지 계산
        start=tempEnd-9;
        prev=start>1; // 시작 페이지가 1보다 큰 경우 prev true
        end=totalPage>tempEnd?tempEnd:totalPage; // 실제 마지막 페이지 설정(현재 페이지 기반 임시 마지막 페이지와 실제 마지막 페이지 비교해서 작은쪽이 진짜)
        next=totalPage>tempEnd; // 실제 마지막 페이지가 임시 마지막 페이지보다 큰 경우 next true
        pageList= IntStream.rangeClosed(start,end).boxed().collect(Collectors.toList());
    }
}
