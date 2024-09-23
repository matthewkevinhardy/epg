package epg.search.cache;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Optional;
import java.util.function.Supplier;

import org.ehcache.expiry.ExpiryPolicy;

import epg.documents.ProgrammeDoc;

public class NowAndNextExpiryPolicy implements ExpiryPolicy<String, ProgDocList> {

	@Override
	public Duration getExpiryForCreation(String key, ProgDocList values) {
		Optional<ZonedDateTime> minStop = values.stream().min(Comparator.comparing(ProgrammeDoc::getStop))
				.map(p -> p.getStop());
		return Duration.between(ZonedDateTime.now(), minStop.get());
	}

	@Override
	public Duration getExpiryForAccess(String key, Supplier<? extends ProgDocList> values) {
		Optional<ZonedDateTime> minStop = values.get().stream().min(Comparator.comparing(ProgrammeDoc::getStop))
				.map(p -> p.getStop());
		return Duration.between(ZonedDateTime.now(), minStop.get());
	}

	@Override
	public Duration getExpiryForUpdate(String key, Supplier<? extends ProgDocList> oldValues, ProgDocList newValues) {
		Optional<ZonedDateTime> minStop = newValues.stream().min(Comparator.comparing(ProgrammeDoc::getStop))
				.map(p -> p.getStop());
		return Duration.between(ZonedDateTime.now(), minStop.get());
	}

}
