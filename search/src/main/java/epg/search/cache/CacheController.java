package epg.search.cache;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.Collection;
import java.util.List;
import java.util.stream.StreamSupport;

import javax.cache.Cache.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.jcache.JCacheCache;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/cache")
public class CacheController {

	@Autowired
	private CacheManager cacheManager;

	@GetMapping(path = "/listAll", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<Collection<String>> byChannel() {

		return ResponseEntity.ok(cacheManager.getCacheNames());
	}

	@GetMapping(path = "/{cacheName}/contents", produces = APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Object>> cacheContents(@PathVariable String cacheName) {
		JCacheCache theCache = (JCacheCache) cacheManager.getCache(cacheName);

		List<Object> keys = StreamSupport.stream(theCache.getNativeCache().spliterator(), false).map(Entry::getKey)
				.toList();

		return ResponseEntity.ok(keys);
	}
}
