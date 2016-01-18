package com.ajayinkingston.main;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;


public class Main {

	static int hours = 1;
	static String folder = "Java Projects";
	static HttpTransport transport;
	static JsonFactory factory = JacksonFactory.getDefaultInstance();
	static java.io.File dataloc = new java.io.File(System.getProperty("user.home"), ".credentials/driveautouploader");
	static FileDataStoreFactory datafact;
	static List<String> scopes = Arrays.asList(DriveScopes.DRIVE_METADATA_READONLY, DriveScopes.DRIVE_FILE, DriveScopes.DRIVE_APPDATA, DriveScopes.DRIVE);
	
	public static void main(String[] args){
//		try {
//			datafact = new FileDataStoreFactory(dataloc);
//			transport = GoogleNetHttpTransport.newTrustedTransport();
//		} catch (GeneralSecurityException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		// Build a new authorized API client service.
//        Drive service = null;
//		try {
//			service = getDriveService();
//		} catch (IOException e1) {
//			e1.printStackTrace();
//		}
//		 
//	    java.io.File fileContent = new java.io.File("C:/Users/Ajay/Pictures/vlcsnap-2015-02-10-16h12m58s28.png");
//	    File body = new File();
//		    body.setTitle(fileContent.getName());
//	    FileContent mediaContent = new FileContent(body.getMimeType(), fileContent);
//	    
//	    File extra = new File();
//	    extra.setTitle("hi");
//	    extra.setMimeType("application/vnd.google-apps.folder");
//	    
//		try {
//			
//			String folderid = service.files().insert(extra).execute().getId();
//			
//			body.setParents(Arrays.asList(new ParentReference().setId(folderid)));
//					
//			service.files().insert(body, mediaContent).execute();
//			
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
	    Runnable runnable = new Runnable() {
	        public void run(){
	        	//set up service
	        	try {
	    			datafact = new FileDataStoreFactory(dataloc);
	    			transport = GoogleNetHttpTransport.newTrustedTransport();
	    		} catch (GeneralSecurityException e) {
	    			e.printStackTrace();
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
	    		// Build a new authorized API client service.
	            Drive service = null;
	    		try {
	    			service = getDriveService();
	    		} catch (IOException e1) {
	    			e1.printStackTrace();
	    		}
	    		
	    		//get folder id
	    		String folderid = null;
	    		try {
					folderid = service.files().list().setQ("mimeType = 'application/vnd.google-sapps.folder' and name contains ' '").execute().getItems().get(0).getId();
				} catch (IOException e) {
					e.printStackTrace();
				}
	    		
	    		System.out.println(folderid);
	    		
	        }
	    };
	    service.schedule(runnable, hours, TimeUnit.SECONDS);
	}
	
	
	//test area
//	ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
//    Runnable runnable = new Runnable() {
//        public void run()
//        {
//            // do something
//        }
//    };
//    service.schedule(runnable, 8, TimeUnit.HOURS);
    //service.shutdownNow();
	
	
	
	
	
	public static Credential authorize() throws IOException {
        // Load client secrets.
        InputStream in =
            Main.class.getResourceAsStream("/client_secret.json");
        GoogleClientSecrets clientSecrets =
            GoogleClientSecrets.load(factory, new InputStreamReader(in));

        // Build flow and trigger user authorization request.
        GoogleAuthorizationCodeFlow flow =
                new GoogleAuthorizationCodeFlow.Builder(
                        transport, factory, clientSecrets, scopes)
                .setDataStoreFactory(datafact)
                .setAccessType("offline")
                .build();
        Credential credential = new AuthorizationCodeInstalledApp(
            flow, new LocalServerReceiver()).authorize("user");
        System.out.println(
                "Credentials saved to " + dataloc.getAbsolutePath());
        return credential;
    }


    public static Drive getDriveService() throws IOException {
        Credential credential = authorize();
        return new Drive.Builder(
        		transport, factory, credential)
                .setApplicationName("Drive Auto Uploader")
                .build();
    }
}
