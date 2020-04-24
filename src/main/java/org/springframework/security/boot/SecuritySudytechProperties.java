package org.springframework.security.boot;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ConfigurationProperties(prefix = SecuritySudytechProperties.PREFIX)
@Getter
@Setter
@ToString
public class SecuritySudytechProperties {

	public static final String PREFIX = "spring.security.sudytech";

	/** Whether Enable Sudytech IDS Authentication. */
	private boolean enabled = false;


}
