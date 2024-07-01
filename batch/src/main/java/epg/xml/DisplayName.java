package epg.xml;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlValue;

@XmlRootElement(name = "display-name")
public class DisplayName {

	private String lang;

	private String value;

	public String getLang() {
		return lang;
	}

	@XmlAttribute(name = "lang")
	public void setLang(String lang) {
		this.lang = lang;
	}

	@XmlValue
	public String getValue() {
		return value;
	}

	@XmlValue
	public void setValue(String value) {
		this.value = value;
	}

}
