package com.example.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.example.service.BoardService;
import com.example.vo.BoardVO;
import com.example.vo.PageVO;

@Controller
public class BoardController {

	@Autowired
	private BoardService boardService;

	//게시판 입력폼 매핑
	@RequestMapping(value="/board_write", method=RequestMethod.GET)
	public String board_write(HttpServletRequest request,
			Model m) {
		int page=1;
		if(request.getParameter("page") != null) {
			page=Integer.parseInt(request.getParameter("page"));			
		}
		m.addAttribute("page",page);//키,값 쌍으로 쪽번호 저장
		return "board/board_write";//WEB-INF/views/board/
		//board_write.jsp 뷰페이지 경로와 파일명
	}//board_write()
	
	//게시판 저장
		@RequestMapping(value="/board_write_ok",
				method=RequestMethod.POST)
		//POST방식으로 접근하는 매핑주소를 처리
		public ModelAndView board_write_ok(
				@ModelAttribute BoardVO b) {
			//@ModelAttribute BoardVO b라고 하면 네임피라미터 이름과
			//빈클래스 변수명이 같으면 b객체에 값이 벌써 저장됨.
			this.boardService.insertBoard(b);//게시판 저장
			return new ModelAndView("redirect:/board_list");
			//board_list로 이동
		}//board_write_ok()
	
	//목록보기
		@RequestMapping("/board_list")
		//get or post 방식으로 접근하는 매핑주소를 처리
		public ModelAndView board_list(
				PageVO p,
				HttpServletRequest request) {
			int page=1;//쪽번호
			int limit=10;//한페이지에 보여지는 목록개수
			if(request.getParameter("page") != null) {
				page=Integer.parseInt(request.getParameter("page"));			
			}
			String find_name=request.getParameter("find_name");//검색어
			String find_field=request.getParameter("find_field");//검색
			//필드
			p.setFind_field(find_field);
			p.setFind_name("%"+find_name+"%");
			//%는 오라클 와일드 카드 문자로서 하나이상의 임의의 문자와
			//매핑 대응

			int listcount=this.boardService.getListCount(p);
			//전체 레코드 개수 또는 검색전후 레코드 개수
			//System.out.println("총 게시물수:"+listcount+"개");

			p.setStartrow((page-1)*10+1);//시작행번호
			p.setEndrow(p.getStartrow()+limit-1);//끝행번호

			List<BoardVO> blist=this.boardService.getBoardList(p);
			//목록

			//총페이지수
			int maxpage=(int)((double)listcount/limit+0.95);
			//현재 페이지에 보여질 시작페이지 수(1,11,21)
			int startpage=(((int)((double)page/10+0.9))-1)*10+1;
			//현재 페이지에 보여줄 마지막 페이지 수(10,20,30)
			int endpage=maxpage;
			if(endpage > startpage+10-1) endpage=startpage+10-1;

			ModelAndView listM=new ModelAndView();
			listM.addObject("blist",blist);//blist 키이름에 값
			//저장
			listM.addObject("page",page);
			listM.addObject("startpage",startpage);
			listM.addObject("endpage",endpage);
			listM.addObject("maxpage",maxpage);
			listM.addObject("listcount",listcount);	
			listM.addObject("find_field",find_field);
			listM.addObject("find_name", find_name);

			listM.setViewName("board/board_list");//뷰페이지 폴더
			//경로와 파일명 지정
			return listM;
		}
		
		/*내용보기+수정폼+답변폼+삭제폼 -> 추후에 ui 페이지변경되지 않고 제자리에서 모달창 띄우는걸로 변경하기 */
		@RequestMapping("/board_cont")
		public String board_cont(
				@RequestParam("no") int bno,
				@RequestParam("state") String state,
				HttpServletRequest request,
				Model m,@ModelAttribute BoardVO b)
						throws Exception{
			int page=1;
			if(request.getParameter("page") != null) {
				page=Integer.parseInt(request.getParameter("page"));			
			}

			if(state.equals("cont")) {//내용보기일때만 조회수 증가
				b=this.boardService.getBoardCont(bno);
				//this.boardService.updateHit(board_no);
			}else {
				b=this.boardService.getBoardCont2(bno);
			}

			String board_cont=b.getBcont().replace("\n","<br/>");
			//textarea태그 영역에서 엔터키 친부분을 웹브라우에 출력할때 줄바
			//꿈처리

			m.addAttribute("b",b);
			m.addAttribute("bcont",board_cont);
			m.addAttribute("page",page);//키,값 쌍으로 저장

			if(state.equals("cont")) {//내용보기
				return "/board/board_cont";// /WEB-INF/board/
				//board_cont.jsp 뷰페이지로 이동
			}else if(state.equals("reply")) {//답변글 폼
				return "/board/board_reply";
			}else if(state.equals("edit")) {//수정폼
				return "/board/board_edit";
			}else if(state.equals("del")) {//삭제폼
				return "/board/board_del";
			}
			return null;
		}//board_cont()
		
		

}
