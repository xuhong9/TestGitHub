package com.usion.demo.service;

import java.util.List;

import com.usion.demo.entity.main.User;
import com.usion.util.dwz.Page;

/**
 * @author usion
 */

public interface UserService extends BaseService<User, Long>{
	
	User get(String username);
	
	List<User> find(Page page, String name);
}
