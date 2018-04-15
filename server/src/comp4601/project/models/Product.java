package comp4601.project.models;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.analysis.tokenattributes.CharTermAttributeImpl;
import org.apache.lucene.analysis.tokenattributes.KeywordAttributeImpl;
import org.apache.lucene.analysis.tokenattributes.PackedTokenAttributeImpl;

import com.mongodb.BasicDBObject;

public class Product extends BasicDBObject {
	String title;
	String brand;
	String store;
	String url;
	List<String> tokens;
	double price;

	public static enum Condition {ALL,IN}
	private Analyzer analyzer;
	
	public Product(StandardAnalyzer analyzer) {
		this.analyzer = analyzer;//new StandardAnalyzer();
	}

	public Product(String title, String store,  String url, double d) {
		this.title = title;
		this.store = store;
		this.url = url;
		this.price = d;
		try {
			this.appendAndAnalyzeFullTextString("title", title);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void appendAndAnalyzeFullTextString(String name, String text) throws IOException {
		this.analyzer = new StandardAnalyzer();
		this.tokens = tokenize(analyzer.tokenStream(name, new StringReader(text)));
	}
	
	public Product appendAndAnalyzeFullText(String name, String text) throws IOException {
		append(name, tokenize(analyzer.tokenStream(name, new StringReader(text))));
		return this;
	}

	private List<String> tokenize(TokenStream stream) throws IOException {
		List<String> tokens = new ArrayList<String>();
		CharTermAttribute charTermAttribute = (CharTermAttribute) stream.addAttribute(CharTermAttribute.class);
		stream.reset();
		while (stream.incrementToken()) {
			// Not sure if we somehow can use termBuffer() to get a char[]
			// so we do no have to create a new String for each term
		    String term = charTermAttribute.toString();
		    tokens.add(term);
		}
		stream.end();
		stream.close();
		return tokens;
	}
	
	public Product createQuery(String name,String text, Condition condition) throws IOException {
		List<String> tokens = tokenize(analyzer.tokenStream(name, new StringReader(text)));
        append(name,new BasicDBObject(
        		String.format("$%s",condition.toString().toLowerCase()),
        		tokens.toArray(new String[0])));
        return this;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}
	
	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getStore() {
		return store;
	}

	public void setStore(String store) {
		this.store = store;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public void setPrice(double price) {
		this.price = price;
	}

	public Analyzer getAnalyzer() {
		return analyzer;
	}

	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	public List<String> getTokens() {
		return tokens;
	}

	public void setTokens(List<String> tokens) {
		this.tokens = tokens;
	}
}
