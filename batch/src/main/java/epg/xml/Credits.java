package epg.xml;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "credits")
public class Credits {
	private String actor;

	public String getActor() {
		return actor;
	}

	@XmlElement(name = "actor")
	public void setActor(String actor) {
		this.actor = actor;
	}

}
