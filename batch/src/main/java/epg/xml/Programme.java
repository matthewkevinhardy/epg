package epg.xml;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "programme")
public class Programme {
	private String channel;
	private String start;
	private String stop;
	private Title title;
	private Description description;
	private String date;
	private List<Category> catagories;
	private Icon icon;
	private Rating rating;

	public String getChannel() {
		return channel;
	}

	@XmlAttribute(name = "channel")
	public void setChannel(String channel) {
		this.channel = channel;
	}

	public String getStart() {
		return start;
	}

	@XmlAttribute(name = "start")
	public void setStart(String start) {
		this.start = start;
	}

	public String getStop() {
		return stop;
	}

	@XmlAttribute(name = "stop")
	public void setStop(String stop) {
		this.stop = stop;
	}

	public Title getTitle() {
		return title;
	}

	@XmlElement(name = "title")
	public void setTitle(Title title) {
		this.title = title;
	}

	public Description getDescription() {
		return description;
	}

	@XmlElement(name = "desc")
	public void setDescription(Description description) {
		this.description = description;
	}

	public String getDate() {
		return date;
	}

	@XmlElement(name = "date")
	public void setDate(String date) {
		this.date = date;
	}

	public List<Category> getCatagories() {
		return catagories;
	}

	@XmlElement(name = "category")
	public void setCatagories(List<Category> catagories) {
		this.catagories = catagories;
	}

	public Icon getIcon() {
		return icon;
	}

	@XmlElement(name = "icon")
	public void setIcon(Icon icon) {
		this.icon = icon;
	}

	public Rating getRating() {
		return rating;
	}

	@XmlElement(name = "rating")
	public void setRating(Rating rating) {
		this.rating = rating;
	}
}
