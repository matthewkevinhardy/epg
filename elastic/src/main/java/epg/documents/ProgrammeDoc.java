package epg.documents;

import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "programme")
public class ProgrammeDoc {

	@Id
	@Field(name = "progId", type = FieldType.Keyword)
	private String progId;

	@Field(name = "channel", type = FieldType.Keyword)
	private String channel;

	@Field(name = "start", type = FieldType.Date, format = DateFormat.date_time_no_millis)
	private ZonedDateTime start;

	@Field(name = "stop", type = FieldType.Date, format = DateFormat.date_time_no_millis)
	private ZonedDateTime stop;

	@Field(name = "title", type = FieldType.Text)
	private String title;

	@Field(name = "description", type = FieldType.Text)
	private String description;

	@Field(name = "description-lang", type = FieldType.Keyword)
	private String descriptionLang;

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

	@Field(name = "credits", type = FieldType.Text)
	private List<String> credits;

	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	public ZonedDateTime getStart() {
		return start;
	}

	public void setStart(ZonedDateTime start) {
		this.start = start;
	}

	public ZonedDateTime getStop() {
		return stop;
	}

	public void setStop(ZonedDateTime stop) {
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

	public String getDescriptionLang() {
		return descriptionLang;
	}

	public void setDescriptionLang(String descriptionLang) {
		this.descriptionLang = descriptionLang;
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

	public String getProgId() {
		return progId;
	}

	public void setProgId(String progId) {
		this.progId = progId;
	}

	public List<String> getCredits() {
		return credits;
	}

	public void setCredits(List<String> credits) {
		this.credits = credits;
	}

}
