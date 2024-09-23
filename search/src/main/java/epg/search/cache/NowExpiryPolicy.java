package epg.search.cache;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.function.Supplier;

import org.ehcache.expiry.ExpiryPolicy;

import epg.documents.ProgrammeDoc;

public class NowExpiryPolicy implements ExpiryPolicy<String, ProgrammeDoc> {

	@Override
	public Duration getExpiryForCreation(String key, ProgrammeDoc value) {
		return Duration.between(ZonedDateTime.now(), value.getStop());
	}

	@Override
	public Duration getExpiryForAccess(String key, Supplier<? extends ProgrammeDoc> value) {
		return Duration.between(ZonedDateTime.now(), value.get().getStop());
	}

	@Override
	public Duration getExpiryForUpdate(String key, Supplier<? extends ProgrammeDoc> oldValue, ProgrammeDoc newValue) {
		return Duration.between(ZonedDateTime.now(), newValue.getStop());
	}

}
