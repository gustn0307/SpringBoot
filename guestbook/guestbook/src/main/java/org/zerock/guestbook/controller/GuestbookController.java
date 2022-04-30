package org.zerock.guestbook.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.zerock.guestbook.dto.GuestbookDTO;
import org.zerock.guestbook.dto.PageRequestDTO;
import org.zerock.guestbook.service.GuestbookService;

@Controller
@RequestMapping("/guestbook")
@Log4j2
@RequiredArgsConstructor // 자동 주입을 위한 어노테이션
public class GuestbookController {

    private final GuestbookService service; // final로 선언

    @GetMapping("/")
    public String index(){

        return "redirect:/guestbook/list";
    }

    // 스프링 MVC는 파라미터를 자동으로 수집해주는 기능이 있어 화면에서 page와 size라는 파라미터를 전달하면 PageRequestDTO 객체로 자동으로 수집되고
    // Model은 결과 데이터를 화면에 전달하기 위해 사용
    // Model을 이용해서 GuestbookServiceImpl에서 반환하는 PageResultDTO를 'result'라는 이름으로 전달하고
    // 실제 내용을 출력하는 list.html에서는 부트스트랩의 테이블 구조를 이용해서 출력함
    @GetMapping("/list")
    public void list(PageRequestDTO pageRequestDTO, Model model){
        log.info("list.............."+pageRequestDTO);

        model.addAttribute("result",service.getList(pageRequestDTO));
    }

    @GetMapping("/register") // GET 방식에서는 화면을 보여주도록 설계
    public void register(){
        log.info("register get...");
    }

    @PostMapping("/register") // POST 방식에서는 처리 후에 목록 페이지로 이동하도록 설계
    public String registerPost(GuestbookDTO dto, RedirectAttributes redirectAttributes){
        log.info("dto..."+dto);

        // 새로 추가된 엔티티의 번호
        Long gno = service.register(dto);

        // RedirectAttributes를 이용해서 한 번만 화면에서 'msg'라는 이름의 변수를 사용할 수 있도록 처리
        // addFlashAttribute()는 단 한 번만 데이터를 전달하는 용도로 사용
        // 브라우저에 전달되는 'msg'를 이용해서 화면상에 모달 창(화면 위에 하나의 작은 화면)을 보여주는 용도로 사용하는데 글이 등록된 후 자동으로 모달 창 보이게 처리
        redirectAttributes.addFlashAttribute("msg",gno);

        return "redirect:/guestbook/list";
    }
    
    // GET 방식으로 gno 값을 받아서 Model에 GuestbookDTO 객체를 담아서 전달하도록 코드를 작성
    // 추가로 나중에 다시 목록 페이지로 돌아가는 데이터를 같이 저장하기 위해서 PageRequestDTO를 파라미터로 같이 사용
    // @ModelAttribute는 없어도 처리가 가능하지만 명시적으로 requestDTO라는 이름으로 처리해둠
    @GetMapping({"/read","/modify"})
    public void read(long gno, @ModelAttribute("requestDTO") PageRequestDTO requestDTO, Model model){
        log.info("gno: "+gno);
        
        GuestbookDTO dto = service.read(gno);
        
        model.addAttribute("dto",dto);
    }

    // POST 방식으로 gno 값을 전달하고, 삭제 후에는 다시 list로 이동
    @PostMapping("/remove")
    public String remove(long gno, RedirectAttributes redirectAttributes){
        log.info("gno: "+gno);

        service.remove(gno);

        redirectAttributes.addFlashAttribute("msg", gno);

        return "redirect:/guestbook/list";
    }

    // POST 방식으로 수정 시에 수정해야하는 (제목, 내용)이 전달되고
    // 수정된 후에는 목록 페이지(list) 혹은 조회 페이지(read)로 이동하고 기존의 페이지 번호를 유지
    // 수정해야 하는 글의 정보를 가지는 GuestbookDTO, 기존의 페이지 정보를 유지하기 위한 PageRequestDTO, 리다이렉트로 이동하기 위한 RedirectAttributes를 인자로
    @PostMapping("/modify")
    public String modify(GuestbookDTO dto, @ModelAttribute("requestDTO") PageRequestDTO requestDTO, RedirectAttributes redirectAttributes){
        log.info("post modify......................");
        log.info("dto: " + dto);

        service.modify(dto);

        // 수정한 후에 조회페이지로 리다이렉트 처리될 때 검색 조건을 유지하도록 추가
        redirectAttributes.addAttribute("page",requestDTO.getPage());
        redirectAttributes.addAttribute("type",requestDTO.getType());
        redirectAttributes.addAttribute("keyword",requestDTO.getKeyword());
        redirectAttributes.addAttribute("gno",dto.getGno());

        return "redirect:/guestbook/read";
    }
}
