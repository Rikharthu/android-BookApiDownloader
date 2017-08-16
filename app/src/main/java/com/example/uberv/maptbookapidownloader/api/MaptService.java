package com.example.uberv.maptbookapidownloader.api;

import com.example.uberv.maptbookapidownloader.models.BookMetadata;
import com.example.uberv.maptbookapidownloader.models.Page;
import com.example.uberv.maptbookapidownloader.models.requests.UserCredentials;
import com.example.uberv.maptbookapidownloader.models.responses.AuthData;
import com.example.uberv.maptbookapidownloader.models.responses.BaseResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MaptService {
    @POST("users/tokens")
    Call<BaseResponse<AuthData>> authenticate(@Body UserCredentials credentials);

    @GET("products/{book_id}/metadata")
    Call<BaseResponse<BookMetadata>> getBookMetadata(@Path("book_id") long bookId);

    @GET("users/me/products/{book_id}/chapters/{chapter_id}/sections/{section_id}")
    Call<BaseResponse<Page>> getBookPage(@Path("book_id") long bookId, @Path("chapter_id") String chapterId, @Path("section_id") String sectionId,
                                         @Header("Authorization") String authHeader);
}
