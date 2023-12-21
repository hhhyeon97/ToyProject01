package com.example.service;

import java.util.List;

import com.example.vo.AdminVO;
import com.example.vo.MemberVO;
import com.example.vo.PageVO;

public interface AdminService {
	
	
	void insertAdmin(AdminVO ab);
	AdminVO adminLogin(String aid);
	
	int getListCount(PageVO p);
	List<MemberVO> getMemberList(PageVO p);
	MemberVO getMember(String mid);
	void editMember(MemberVO m);
	void deleteMem(String mid);


}
