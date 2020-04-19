package common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.alibaba.fastjson.JSON;

public class Result {

	private String definition;
	private String partOfSpeech;
	private List<String> synonyms;
	private List<String> typeOf;
	private List<String> hasTypes;
	private List<String> derivation;
	private List<String> examples;
	
	public Result() {
		definition = "";
		partOfSpeech = "";
		synonyms = Collections.synchronizedList(new ArrayList<String>());
		typeOf = Collections.synchronizedList(new ArrayList<String>());
		hasTypes = Collections.synchronizedList(new ArrayList<String>());
		derivation = Collections.synchronizedList(new ArrayList<String>());
		examples = Collections.synchronizedList(new ArrayList<String>());
	}
	
	public String getDefinition() { return definition; }
	public String getPartOfSpeech() { return partOfSpeech; }
	public List<String> getSynonyms() { return synonyms; }
	public List<String> getTypeOf() { return typeOf; }
	public List<String> getHasTypes() { return hasTypes; }
	public List<String> getDerivation() { return derivation; }
	public List<String> getExamples() { return examples; }
	
	public void setDefinition(String definition) { this.definition = definition; }
	public void setPartOfSpeech(String partOfSpeech) { this.partOfSpeech = partOfSpeech; }
	public void setSynonyms(List<String> synonyms) { this.synonyms = synonyms; }
	public void setTypeOf(List<String> typeOf) { this.typeOf = typeOf; }
	public void setHasTypes(List<String> hasTypes) { this.hasTypes = hasTypes; }
	public void setDerivation(List<String> derivation) { this.derivation = derivation; }
	public void setExamples(List<String> examples) { this.examples = examples; }
	
	public String toString() {
		return JSON.toJSONString(this);
	}
}
