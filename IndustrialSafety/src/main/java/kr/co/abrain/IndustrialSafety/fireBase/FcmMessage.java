/**
 * 
 */
package kr.co.abrain.IndustrialSafety.fireBase;

/**
 * Copyright 2022 Abrain All right reserved.
 * @author Liberator / Afarms - J.H.Oh / R&D Team Leader
 * liberator@kakao.com / 010-8323-5634
 */
import lombok.Builder;

@Builder
public class FcmMessage {
	private boolean validate_only;
	private Message message;
	
	public boolean getValidate_only() {
		return this.validate_only;
	}
	
	public Message getMessage() {
		return this.message;
	}
	
	FcmMessage(boolean validate_only, Message message){
		this.validate_only = validate_only;
		this.message = message;
	}
	@Builder
	public static class Message{
		private Notification notification;
		private String token;
		
		public Notification getNotification() {
			return this.notification;
		}
		
		public String getToken() {
			return this.token;
		}
		
		Message(Notification notification, String token){
			this.notification = notification;
			this.token = token;
		}
	}
	@Builder
	public static class Notification{
		private String title;
		private String body;
		private String image;
		
		public String getTitle() {
			return this.title;
		}
		
		public String getBody() {
			return this.body;
		}
		
		public String getImage() {
			return this.image;
		}
		
		Notification(String title, String body, String image){
			this.title = title;
			this.body = body;
			this.image = image;
		}
	}
}