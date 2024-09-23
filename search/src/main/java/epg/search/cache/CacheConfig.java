package epg.search.cache;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.config.units.EntryUnit;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import epg.documents.ProgrammeDoc;

@Configuration
@EnableCaching
public class CacheConfig {

	@Bean
	public CacheManager ehCacheManager() {
		CachingProvider provider = Caching.getCachingProvider();
		CacheManager cacheManager = provider.getCacheManager();

		cacheManager.createCache("nowCache", Eh107Configuration.fromEhcacheCacheConfiguration(getNowConfig()));
		cacheManager.createCache("nowAndNextCache",
				Eh107Configuration.fromEhcacheCacheConfiguration(getNowAndNextConfig()));

		return cacheManager;

	}

	private CacheConfigurationBuilder<String, ProgrammeDoc> getNowConfig() {
		return CacheConfigurationBuilder
				.newCacheConfigurationBuilder(String.class, ProgrammeDoc.class,
						ResourcePoolsBuilder.newResourcePoolsBuilder().heap(100, EntryUnit.ENTRIES))
				.withExpiry(new NowExpiryPolicy());
	}

	private CacheConfigurationBuilder<String, ProgDocList> getNowAndNextConfig() {
		return CacheConfigurationBuilder
				.newCacheConfigurationBuilder(String.class, ProgDocList.class,
						ResourcePoolsBuilder.newResourcePoolsBuilder().heap(100, EntryUnit.ENTRIES))
				.withExpiry(new NowAndNextExpiryPolicy());
	}
}
