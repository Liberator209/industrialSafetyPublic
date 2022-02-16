/**
 * 
 */
package kr.co.abrain.IndustrialSafety.fireBase;

import java.io.IOException;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import lombok.RequiredArgsConstructor;

/**
 * Copyright 2022 Abrain All right reserved.
 * @author Liberator / Afarms - J.H.Oh / R&D Team Leader
 * liberator@kakao.com / 010-8323-5634
 */
@Component
@RequiredArgsConstructor
public class FirebaseCloudMessageService {
	private final String API_URL ="https://fcm.googleapis.com/v1/projects/workmanager-8f04b/messages:send";
	//private final String API_URL = "https://fcm.googleapis.com/v1/projects/xenon-marker-316903/messages:send";
	private final ObjectMapper objectMapper;
	
	public void sendMessageTo(String targetToken, String title, String body) throws IOException{
		
		String firebaseConfigPath = "firebase/workmanager-8f04b-firebase-adminsdk-y9bgh-8e14c1a73d.json";
		//String firebaseConfigPath = "firebase/firebase_service_key.json";
		
		FirebaseOptions options = FirebaseOptions.builder()
		    .setCredentials(GoogleCredentials.fromStream(new 
					ClassPathResource(firebaseConfigPath).getInputStream())
					.createScoped(List.of("https://www.googleapis.com/auth/cloud-platform")))
		    .build();
		
		FirebaseApp.initializeApp(options);
		
		String registrationToken = targetToken;
		Message message = Message.builder()
				.setNotification(Notification.builder()
						.setTitle(title)
						.setBody(body)
						.build())
//				.putData("image_url", "https://opgg-com-image.akamaized.net/attach/images/20210423012506.1656961.jpg")
//				.putData("body", "asdfasdfdasf")
				.setToken(registrationToken)
				.build();
		
		try {
			String response = FirebaseMessaging.getInstance().send(message);
			 System.out.println("Successfully sent message: " + response);
		} catch (FirebaseMessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
