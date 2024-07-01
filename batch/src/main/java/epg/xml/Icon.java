package epg.xml;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "icon")
public class Icon {
	private String src;

	@XmlAttribute(name = "src")
	public void setSrc(String src) {
		this.src = src;
	}

	public String getSrc() {
		return src;
	}

}
