package com.github.olevinov.keycloak.extensions.events.simple;

import org.jboss.logging.Logger;
import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;
import org.keycloak.events.admin.ResourceType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.RealmProvider;
import org.keycloak.models.UserModel;
//import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class PlaceholderEventListenerProvider implements EventListenerProvider {

    private static final Logger log = Logger.getLogger(PlaceholderEventListenerProvider.class);

    private final KeycloakSession session;

    public PlaceholderEventListenerProvider(KeycloakSession session) {
        this.session = session;
    }

    @Override
    public void onEvent(Event event) {
        log.infof("## NEW %s EVENT", event.getType());
        log.info("-----------------------------------------------------------");
        if (EventType.UPDATE_PROFILE.equals(event.getType())) {
            RealmModel realm = this.session.realms().getRealm(event.getRealmId());
            UserModel user = this.session.users().getUserById(event.getUserId(), realm);
            if (user != null && user.getEmail() != null) {
                sendMessage(user.getEmail());
            }
        }
        log.info("-----------------------------------------------------------");
    }

    @Override
    public void onEvent(AdminEvent adminEvent, boolean b) {
        log.info("## NEW ADMIN EVENT");
        log.info("-----------------------------------------------------------");
        log.info("Resource path: " + adminEvent.getResourcePath());
        log.info("Resource type: " + adminEvent.getResourceType());
        log.info("Operation type: " + adminEvent.getOperationType());
        log.info("Realm id: " + adminEvent.getRealmId());
        log.info("Representation: " + adminEvent.getRepresentation());

        if (ResourceType.USER.equals(adminEvent.getResourceType())) {
            if (OperationType.CREATE.equals(adminEvent.getOperationType()) ||
                OperationType.UPDATE.equals(adminEvent.getOperationType()))
            {
                //JSONObject obj = new JSONObject(adminEvent.getRepresentation());
                //String email = obj.getString("email");
                //if (email != null) {
                //    sendMessage(email);
                //}
                Pattern pattern = Pattern.compile(",\"email\":\"(.*?)\",");
                Matcher matcher = pattern.matcher(adminEvent.getRepresentation());
                if (matcher.find()) {
                    sendMessage(matcher.group(1));
                } else {
                    log.info("email not found");
                }
            }
        }
        log.info("-----------------------------------------------------------");
    }

    @Override
    public void close() {
        // Nothing to close
    }

    private void sendMessage(String email) {
        log.info("sendMessage: email = " + email);
        try {
            HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_2)
                .connectTimeout(Duration.ofSeconds(1))
                .build();
            HttpRequest request = HttpRequest.newBuilder()
                .POST(HttpRequest.BodyPublishers.ofString("email=" + email))
                .uri(URI.create("http://localhost:8089"))
                .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            log.info("#### STATUS CODE");
            log.info(response.statusCode());
        } catch (Exception e) {
            log.info("#### EXCEPTION");
            log.info(e.toString());
        }
    }

}
