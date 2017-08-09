package org.oa.websocket;

import java.util.regex.Pattern;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.core.type.filter.RegexPatternTypeFilter;
import org.oa.websocket.RootConfig.WebPackage;

@Configuration
@ComponentScan(basePackages = { "org.oa.websocket" }, excludeFilters = {
		@Filter(type = FilterType.CUSTOM, value = WebPackage.class) })
public class RootConfig {
	public static class WebPackage extends RegexPatternTypeFilter {
		public WebPackage() {
			super(Pattern.compile("org\\.oa\\.websocket"));
		}
	}
}
