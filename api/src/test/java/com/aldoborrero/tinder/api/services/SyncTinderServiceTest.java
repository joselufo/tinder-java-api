/*
 * Copyright 2015 Aldo Borrero <aldo@aldoborrero.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.aldoborrero.tinder.api.services;

import com.aldoborrero.tinder.api.Configuration;
import com.aldoborrero.tinder.api.Tinder;
import com.aldoborrero.tinder.api.entities.*;
import com.aldoborrero.tinder.api.gson.TinderGsonFactory;
import com.aldoborrero.tinder.api.mock.Assertions;
import com.aldoborrero.tinder.api.mock.MockResponsesFactory;
import com.aldoborrero.tinder.api.mock.ResourcesLoader;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.squareup.okhttp.mockwebserver.MockWebServer;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import retrofit.Endpoint;
import retrofit.Endpoints;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.TestCase.assertNotNull;

public class SyncTinderServiceTest {

    private Gson gson;
    private MockWebServer webServer;
    private SyncTinderService tinderService;

    @Before
    public void setUp() {
        setUpGson();
        setUpWebServer();
        setUpTinderService();
    }

    private void setUpGson() {
        gson = TinderGsonFactory.create();
    }

    private void setUpWebServer() {
        webServer = new MockWebServer();
        try {
            webServer.play();
        } catch (IOException e) {
            throw new RuntimeException("Something went wrong while creating MockWebServer! Nooooo... :'(", e);
        }
    }

    private void setUpTinderService() {
        tinderService = Tinder.create(new Configuration.BaseConfiguration() {
            @NotNull
            @Override
            public Endpoint getEndPoint() {
                return Endpoints.newFixedEndpoint(webServer.getUrl("/").toString());
            }
        }).createSyncService();
    }

    @Test
    public void shouldParseAuthInformation() {
        webServer.enqueue(MockResponsesFactory.createAuthResponse());

        Auth auth = tinderService.auth(new AuthData("token", "en"));
        Auth expectedAuth = gson.fromJson(ResourcesLoader.loadAsString(Assertions.AUTH), Auth.class);

        JsonElement authElement = gson.toJsonTree(auth);
        JsonElement expectedElement = gson.toJsonTree(expectedAuth);
        
        assertEquals(expectedElement, authElement);
    }
    
    @Test
    public void shouldParseUserRecommendations() {
        webServer.enqueue(MockResponsesFactory.createUserRecommendationsResponse());

        MultipleResponse<User> recommendations = tinderService.getUserRecommendations();
        
        assertNotNull(recommendations);
        
        assertNotNull(recommendations.getResults());
        assertEquals(40, recommendations.getResults().size());
        
        User user = recommendations.getResults().get(0);
        User expectedUser = gson.fromJson(ResourcesLoader.loadAsString(Assertions.FIRST_USER_RECOMMENDATION), User.class);
        
        JsonElement userElement = gson.toJsonTree(user);
        JsonElement expectedElement = gson.toJsonTree(expectedUser);
        
        assertEquals(expectedElement, userElement);
    }
    
    @Test
    public void shouldParseUserInfo() {
        webServer.enqueue(MockResponsesFactory.createUserInfoResponse());
        
        SingleResponse<User> userResponse = tinderService.getUserInfo("whateverid");
        User expectedUser = gson.fromJson(ResourcesLoader.loadAsString(Assertions.USER_INFO), User.class);
        
        assertNotNull(userResponse.getResult());
        assertEquals(MultipleResponse.Status.OK, userResponse.getStatus());

        JsonElement userElement = gson.toJsonTree(userResponse.getResult());
        JsonElement expectedElement = gson.toJsonTree(expectedUser);

        assertEquals(expectedElement, userElement);
    }
    
    @Test
    public void shouldParsePopularLocations() {
        webServer.enqueue(MockResponsesFactory.createPopularLocationsResponse());

        MultipleResponse<PopularLocation> popularResponse = tinderService.getPopularLocations();

        assertNotNull(popularResponse.getResults());
        assertEquals(21, popularResponse.getResults().size());

        PopularLocation location = popularResponse.getResults().get(0);
        PopularLocation expectedLocation = gson.fromJson(ResourcesLoader.loadAsString(Assertions.POPULAR_LOCATION), PopularLocation.class);

        JsonElement locationElement = gson.toJsonTree(location);
        JsonElement expectedElement = gson.toJsonTree(expectedLocation);

        assertEquals(expectedElement, locationElement);
    }

    @After
    public void tearDown() throws IOException {
        webServer.shutdown();
    }

}
