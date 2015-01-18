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

import com.aldoborrero.tinder.api.entities.*;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Path;

public interface SyncTinderService {

    @POST("/auth")
    Auth auth(@Body AuthData authData);

    @GET("/user/recs")
    Response<User> getUserRecommendations();

    @GET("/user/{id}")
    Response<User> getUserInfo(@Path("id") String id);

    @GET("/like/{id}")
    Like like(@Path("id") String id);

    @GET("/pass/{id}")
    Pass pass(@Path("id") String id);

    @POST("/feed/moments")
    Moments postMomentsFeed();

    @POST("/moment/{id}/like")
    Response<LikeMoment> likeMoment(@Path("id") String id);

    @POST("/moment/{id}/pass")
    Response<PassMoment> passMoment(@Path("id") String id);

    @GET("/location/popular")
    Response<PopularLocation> getPopularLocations();

    @POST("/updates")
    Updates postUpdates();

    @POST("/profile")
    User updateUserInformation();

}
