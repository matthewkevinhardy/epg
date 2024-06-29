package elastic.document;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

public class Programme {

	@Field(name = "channel", type = FieldType.Keyword)
	private String channel;

	@Field(name = "start", type = FieldType.Date)
	private LocalDateTime start;

	@Field(name = "stop", type = FieldType.Date)
	private LocalDateTime stop;

	@Field(name = "title", type = FieldType.Keyword)
	private String title;

	@Field(name = "description", type = FieldType.Keyword)
	private String description;

	@Field(name = "category", type = FieldType.Keyword)
	private List<String> category;

	@Field(name = "icon", type = FieldType.Keyword)
	private String icon;

	@Field(name = "country", type = FieldType.Keyword)
	private String country;

	@Field(name = "previouslyShown", type = FieldType.Keyword)
	private String previouslyShown;

	@Field(name = "ratingSystem", type = FieldType.Keyword)
	private String ratingSystem;

	@Field(name = "ratingValue", type = FieldType.Keyword)
	private String ratingValue;

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public LocalDateTime getStart() {
		return start;
	}

	public void setStart(LocalDateTime start) {
		this.start = start;
	}

	public LocalDateTime getStop() {
		return stop;
	}

	public void setStop(LocalDateTime stop) {
		this.stop = stop;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<String> getCategory() {
		return category;
	}

	public void setCategory(List<String> category) {
		this.category = category;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getPreviouslyShown() {
		return previouslyShown;
	}

	public void setPreviouslyShown(String previouslyShown) {
		this.previouslyShown = previouslyShown;
	}

	public String getRatingSystem() {
		return ratingSystem;
	}

	public void setRatingSystem(String ratingSystem) {
		this.ratingSystem = ratingSystem;
	}

	public String getRatingValue() {
		return ratingValue;
	}

	public void setRatingValue(String ratingValue) {
		this.ratingValue = ratingValue;
	}

}
