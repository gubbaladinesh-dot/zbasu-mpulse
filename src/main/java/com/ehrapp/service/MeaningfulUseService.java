package com.ehrapp.service;

import java.util.List;

import com.ehrapp.model.StateRecord;


public interface MeaningfulUseService {
	 
	 List<StateRecord> getMeaningfulUse2014() throws Exception;

	    List<StateRecord> getMeaningfulUse(int year) throws Exception;

}
