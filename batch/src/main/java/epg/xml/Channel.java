package epg.xml;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "channel")
public class Channel {

	private String id;

	private String url;

	private DisplayName displayName;

	private List<Icon> icons;

	public String getId() {
		return id;
	}

	@XmlAttribute(name = "id")
	public void setid(String id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	@XmlElement(name = "url")
	public void setUrl(String url) {
		this.url = url;
	}

	public DisplayName getDisplayName() {
		return displayName;
	}

	@XmlElement(name = "display-name")
	public void setDisplayName(DisplayName displayName) {
		this.displayName = displayName;
	}

	public List<Icon> getIcon() {
		return icons;
	}

	@XmlElement(name = "icon")
	public void setIcon(List<Icon> icons) {
		this.icons = icons;
	}

}