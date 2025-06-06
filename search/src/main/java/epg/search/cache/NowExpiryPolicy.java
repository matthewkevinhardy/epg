package epg.search.cache;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.function.Supplier;

import org.ehcache.expiry.ExpiryPolicy;

import epg.documents.ProgrammeDoc;
import reactor.core.publisher.Mono;

public class NowExpiryPolicy implements ExpiryPolicy<String, Mono<ProgrammeDoc>> {

	@Override
	public Duration getExpiryForCreation(String key, Mono<ProgrammeDoc> value) {
		Mono<Duration> d = value.map(t -> {
			return Duration.between(ZonedDateTime.now(ZoneOffset.UTC), t.getStop());
			// return t;
		});
		return null;
	}

	@Override
	public Duration getExpiryForAccess(String key, Supplier<? extends Mono<ProgrammeDoc>> value) {
		// ZonedDateTime stop = value.get().map(t->t.getStop());
		return null; // Duration.between(ZonedDateTime.now(ZoneOffset.UTC), value.get().map(t ->
						// t.getStop()));
	}

	@Override
	public Duration getExpiryForUpdate(String key, Supplier<? extends Mono<ProgrammeDoc>> oldValue,
			Mono<ProgrammeDoc> newValue) {
		return null;// Duration.between(ZonedDateTime.now(ZoneOffset.UTC), newValue.getStop());
	}

}
