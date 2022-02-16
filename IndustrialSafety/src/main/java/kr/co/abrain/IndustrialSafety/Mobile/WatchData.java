/**
 * 
 */
package kr.co.abrain.IndustrialSafety.Mobile;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

/**
 * Copyright 2022 Abrain All right reserved.
 * @author Liberator / Afarms - J.H.Oh / R&D Team Leader
 * liberator@kakao.com / 010-8323-5634
 */
@Data
@Document(collection = "watch")
public class WatchData {
	@Id
	private String id;
	private String user_id;
	private String energy_burned;
	private String distance_walking;
	private String acceleration;
	private String datetime;
	private String heart_rate;
	private String latitude;
	private String longitude;
	private String o3_value;
	private String pm10_value;
	private String pressure;
	private String step_count;
	private String temperature;
}
