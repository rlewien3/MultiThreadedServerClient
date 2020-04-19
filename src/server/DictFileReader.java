package server;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.*;

import common.Result;

import org.apache.commons.io.FileUtils;


/**
 * Reads a JSON file of english definitions into a workable dictionary object
 * Created by Ryan Lewien
 * 746528
 * For Distributed Systems (COMP90015)
 * The University of Melbourne
 * Words obtained from https://www.wordsapi.com/
 */
public class DictFileReader {
	
	public ConcurrentHashMap<String, List<Result>> readDict(String dataPath) { //ConcurrentHashMap<String, String>
		
		File dataFile = FileUtils.getFile(dataPath);
		
		String str = null;
		try {
			str = FileUtils.readFileToString(dataFile, "utf-8");
		} catch (IOException e) {
			System.out.println("Failed reading the data file!");
			e.printStackTrace();
		}

	    JSONObject words = JSONObject.parseObject(str);
	    
	    ConcurrentHashMap<String, List<Result>> dictionary = new ConcurrentHashMap<String, List<Result>>();
	    
	    for (String word : words.keySet()) {
	    	
	    	JSONObject wordData = JSONObject.parseObject(words.get(word).toString());

	    	if (wordData.containsKey("definitions")) {
	    		dictionary.put(word, JSONArray.parseArray(wordData.get("definitions").toString(), Result.class));
	    	}
	    }
	    
	    return dictionary;
	}
}