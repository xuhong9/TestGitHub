package com.usion.demo.service;

import java.util.List;

import com.usion.demo.entity.main.Module;
import com.usion.util.dwz.Page;


/**
 * @author usion
 */

public interface ModuleService extends BaseService<Module, Long>{
	List<Module> find(Long parentId, Page page);
	
	List<Module> find(Long parentId, String name, Page page);
	
	Module getTree();
}
