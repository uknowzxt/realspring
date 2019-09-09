package com.uknowzxt.service.v4;

import com.uknowzxt.beans.factory.annotation.Autowired;
import com.uknowzxt.dao.v4.AccountDao;
import com.uknowzxt.dao.v4.ItemDao;
import com.uknowzxt.stereotype.Component;

@Component(value="petStore")
public class PetStoreService {
	@Autowired
	private AccountDao accountDao;
	@Autowired
	private ItemDao  itemDao;
	
	public AccountDao getAccountDao() {
		return accountDao;
	}

	public ItemDao getItemDao() {
		return itemDao;
	}
	
	
}