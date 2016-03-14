package com.usion.demo.service;

import java.util.List;

import com.usion.demo.entity.main.Role;
import com.usion.util.dwz.Page;

/**
 * @author usion
 */

public interface RoleService extends BaseService<Role, Long>{
	
	List<Role> find(Page page, String name);
}
