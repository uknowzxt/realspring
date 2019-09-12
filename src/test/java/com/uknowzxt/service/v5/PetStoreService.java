package com.uknowzxt.service.v5;


import com.uknowzxt.beans.factory.annotation.Autowired;
import com.uknowzxt.dao.v5.AccountDao;
import com.uknowzxt.dao.v5.ItemDao;
import com.uknowzxt.stereotype.Component;
import com.uknowzxt.util.MessageTracker;

@Component(value="petStore")
public class PetStoreService {		
	@Autowired
	AccountDao accountDao;
	@Autowired
	ItemDao itemDao;
	
	public PetStoreService() {		
		
	}
	
	public ItemDao getItemDao() {
		return itemDao;
	}

	public AccountDao getAccountDao() {
		return accountDao;
	}
	
	public void placeOrder(){
		System.out.println("place order");
		MessageTracker.addMsg("place order");
		
	}	
}
