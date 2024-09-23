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

		CacheConfigurationBuilder<String, ProgrammeDoc> nowConfiguration = CacheConfigurationBuilder
				.newCacheConfigurationBuilder(String.class, ProgrammeDoc.class,
						ResourcePoolsBuilder.newResourcePoolsBuilder().heap(100, EntryUnit.ENTRIES))
				.withExpiry(new NowExpiryPolicy());

		cacheManager.createCache("nowCache", Eh107Configuration.fromEhcacheCacheConfiguration(nowConfiguration));
		return cacheManager;

	}
}
