package org.zerock.club.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.zerock.club.security.dto.NoteDTO;
import org.zerock.club.service.NoteService;

import java.util.List;

@RestController
@Log4j2
@RequestMapping("/notes/")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    // @RequestBody를 이용해 JSON 데이터를 받아서 NoteDTO로 변환할 수 있도록 처리
    @PostMapping(value = "")
    public ResponseEntity<Long> register(@RequestBody NoteDTO noteDTO){ // POST 방식으로 새로운 Note를 등록록

       log.info("----------------register-------------------");
        log.info(noteDTO);

        Long num=noteService.register(noteDTO);

        return new ResponseEntity<>(num, HttpStatus.OK);
    }

    // @PathVariable을 이용해 경로에 있는 Note의 num을 얻어서 해당 Note 정보를 반환환
   @GetMapping(value = "/{num}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<NoteDTO> read(@PathVariable("num") Long num){ // GET 방식으로 특정한 번호의 Note를 확인하는 기능

        log.info("-----------------read-------------------");
        log.info(num);
        return new ResponseEntity<>(noteService.get(num), HttpStatus.OK);
    }

    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<List<NoteDTO>> getList(String email){ // GET 방식으로 특정한 이메일을 가진 회원이 작성한 모든 Note를 조회하는 기능

        log.info("-----------------getList-------------------");
        log.info(email);
        return new ResponseEntity<>(noteService.getAllWithWriter(email), HttpStatus.OK);
    }

    @DeleteMapping(value = "/{num}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> remove(@PathVariable("num") Long num){ // Note 삭제

        log.info("-------------remove--------------");
        log.info(num);

        noteService.remove(num);

        return new ResponseEntity<>("removed", HttpStatus.OK);
    }

    @PutMapping(value = "/{num}", produces = MediaType.TEXT_PLAIN_VALUE)
    public ResponseEntity<String> modify(@RequestBody NoteDTO noteDTO){ // Note를 수정할 때는 등록과 달리 JSON 데이터에 num 속성을 포함해서 전송

        log.info("-------------modify--------------");
        log.info(noteDTO);

        noteService.modify(noteDTO);

        return new ResponseEntity<>("modified", HttpStatus.OK);
    }
}
