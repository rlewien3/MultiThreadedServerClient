package server;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson.*;
import org.apache.commons.io.FileUtils;

public class DictFileReader {
	
	public ConcurrentHashMap<String, List<Result>> readDict() { //ConcurrentHashMap<String, String>
		
		String dataPath = "C:\\Users\\rlewi\\Documents\\MultiThreadedServerClient\\src\\server\\data.json";
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