package org.blackacrelabs.pdfboxminer;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.util.PDFTextStripper;
import org.apache.pdfbox.util.TextPosition;
import org.json.JSONArray;
import org.yaml.snakeyaml.Yaml;

import com.thoughtworks.xstream.XStream;

public class TextEmitter extends PDFTextStripper {

	// Valid output formats
	public enum Format {
		YAML, XML, JSON
	}

	// Pages >> Text Characters >> [X, Y, character, font size, font]
	protected ArrayList<ArrayList<ArrayList<Object>>> pages;
	protected ArrayList<ArrayList<Object>> currentPage;

	public TextEmitter() throws IOException {
		super("UTF-8");
		// Parse all PDF pages
		super.setStartPage(1);
		super.setEndPage(Integer.MAX_VALUE);
		pages = new ArrayList<ArrayList<ArrayList<Object>>>();
	}

	public void tokenize(PDDocument document) throws IOException {
		getText(document);
	}

	@Override
	protected void startPage(PDPage page) throws IOException {
		currentPage = new ArrayList<ArrayList<Object>>();
		pages.add(currentPage);
	}

	@Override
	protected void processTextPosition(TextPosition text) {
		ArrayList<Object> textInfo = new ArrayList<Object>(5);
		textInfo.add(text.getX());
		textInfo.add(text.getY());
		textInfo.add(text.getCharacter());
		textInfo.add(text.getFontSizeInPt());
		textInfo.add(text.getFont().getBaseFont());
		currentPage.add(textInfo);
	}

	public String getOutput(Format f) {
		switch (f) {
		case XML:
			return getXML();
		case JSON:
			return getJSON();
		default:
			return getYAML();
		}
	}

	public String getYAML() {
		Yaml yaml = new Yaml();
		return yaml.dump(pages);
	}

	public String getJSON() {
		return new JSONArray(pages).toString();
	}

	public String getXML() {
		XStream xstream = new XStream();
		return xstream.toXML(pages);
	}
}
