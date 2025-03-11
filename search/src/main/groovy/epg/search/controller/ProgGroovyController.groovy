package epg.search.controller

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

import epg.search.service.ProgrammeService

@RestController
@RequestMapping('/v1/groovy/programme')
class ProgGroovyController {

	@Autowired
	ProgrammeService programmeService

	//	@GetMapping(path = "/description/{desc}", produces = APPLICATION_JSON_VALUE)
	//	public ResponseEntity<Page<ProgrammeDoc>> byDescription(@PathVariable String desc,
	//			@RequestParam(required = false) String lang,
	//			@PageableDefault(sort = { "start" }, direction = Direction.ASC) Pageable pageable) {
	//
	//		return ResponseEntity.ok(programmeService.byDescription(desc, lang, pageable));
	//	}

	@GetMapping(path = "/description/{desc}", produces=APPLICATION_JSON_VALUE)
	ResponseEntity byDescription(@PathVariable desc, @RequestParam(required=false) lang,
			@PageableDefault(sort="start", direction = Sort.Direction.ASC) Pageable p) {
		ResponseEntity.ok(programmeService.byDescription(desc, lang, p))
	}
}
