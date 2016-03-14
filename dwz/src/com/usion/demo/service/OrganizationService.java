package com.usion.demo.service;

import java.util.List;

import com.usion.demo.entity.main.Organization;
import com.usion.util.dwz.Page;

/**
 * @author usion
 */

public interface OrganizationService extends BaseService<Organization, Long>{
	
	List<Organization> find(Long parentId, Page page);
	
	List<Organization> find(Long parentId, String name, Page page);
	
	Organization getTree();
}
