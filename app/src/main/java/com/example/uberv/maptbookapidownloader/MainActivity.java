package com.example.uberv.maptbookapidownloader;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.example.uberv.maptbookapidownloader.Utils.AuthenticationUtils;
import com.example.uberv.maptbookapidownloader.models.BookMetadata;
import com.example.uberv.maptbookapidownloader.models.Chapter;
import com.example.uberv.maptbookapidownloader.models.Page;
import com.example.uberv.maptbookapidownloader.models.Section;
import com.example.uberv.maptbookapidownloader.models.requests.UserCredentials;
import com.example.uberv.maptbookapidownloader.models.responses.AuthData;
import com.example.uberv.maptbookapidownloader.models.responses.BaseResponse;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.example.uberv.maptbookapidownloader.App.getMaptService;

public class MainActivity extends AppCompatActivity {

    private long mBookId = 9781785883309L;
    private BookMetadata mBookMetadata;
    private String mAccessToken = null;
    private List<Page> mPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        AuthenticationUtils.deAuthorize();
        ButterKnife.bind(this);
        Timber.d("HELLO");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.d("Resumed");

        if (!AuthenticationUtils.isAuthorized()) {
            mAccessToken = AuthenticationUtils.getAccessToken();
            Timber.d("Was logged in");
            fetchMetadata();
        } else {
            Timber.d("Not logged in");
            // TODO add
            UserCredentials credentials = new UserCredentials("EMAIL", "PASSWORD");

            callAuthenticationService(credentials);
        }
    }

    private void downloadBookPage(String chapterId, String sectionId) {
        Timber.d("Starting download for chapterId=" + chapterId + ", sectionId=" + sectionId);
        App.getMaptService()
                .getBookPage(mBookId, chapterId, sectionId, "Bearer " + mAccessToken)
                .enqueue(
                        new Callback<BaseResponse<Page>>() {
                            @Override
                            public void onResponse(Call<BaseResponse<Page>> call, Response<BaseResponse<Page>> response) {
                                if (mPages == null) {
                                    mPages = new ArrayList<>();
                                }
                                mPages.add(response.body().getData());
                                Timber.d("response");
                            }

                            @Override
                            public void onFailure(Call<BaseResponse<Page>> call, Throwable t) {
                                Timber.d("failure");
                            }
                        }
                );
    }

    @OnClick(R.id.login_button)
    void onLoginButtonClicked() {
        Timber.d("Logging in");
    }

    private void fetchMetadata() {
        App.getMaptService().getBookMetadata(mBookId)
                .enqueue(new Callback<BaseResponse<BookMetadata>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<BookMetadata>> call, Response<BaseResponse<BookMetadata>> response) {
                        Timber.d("Response");
                        mBookMetadata = response.body().getData();
                        startLoadingBook();
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<BookMetadata>> call, Throwable t) {
                        Timber.d("Failure");
                    }
                });
    }

    private void startLoadingBook() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String chapterIdRegex = "^(\\w+)";
                Pattern pattern = Pattern.compile(chapterIdRegex);
                Matcher m;
                String chapterId;
                String sectionId;
                for (Chapter chapter : mBookMetadata.getChapters()) {
                    for (Section section : chapter.getSections()) {
                        // TODO add support for first chapter instead of skipping
                        if (!section.getId().contains("ch")) continue;

                        Timber.d("Downloading for section:\n" + section.toString());
                        // Prepare data
                        m = pattern.matcher(section.getSeoUrl());
                        if (m.find()) {
                            chapterId = m.group();
                        } else {
                            chapterId = null;
                            Timber.e("Could not find chapter if from seo url at section:\n" + section.toString());
                        }
                        sectionId = section.getId();
//                        downloadBookPage(chapterId, sectionId);
                        try {
                            Response<BaseResponse<Page>> response = App.getMaptService()
                                    .getBookPage(mBookId, chapterId, sectionId, "Bearer " + mAccessToken)
                                    .execute();
                            if (mPages == null) {
                                mPages = new ArrayList<>();
                            }
                            mPages.add(response.body().getData());
                            Timber.d("response");
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    Timber.d("chapter " + chapter.getId());
                }
                Timber.d("finished");
                bakeHtmlFiles();
            }
        }).start();
    }

    private void bakeHtmlFiles() {
        StringBuilder htmlStringBuilder;
        for (Page page : mPages) {
            htmlStringBuilder = new StringBuilder();
            htmlStringBuilder.append("<html>");
            // TODO append styles
            htmlStringBuilder.append(HtmlUtils.STYLES);
            htmlStringBuilder.append(StringEscapeUtils.unescapeJava(page.getContent()));
            htmlStringBuilder.append("</html>");
        }
    }

    private void callAuthenticationService(UserCredentials credentials) {
        getMaptService().authenticate(credentials).enqueue(new Callback<BaseResponse<AuthData>>() {
            @Override
            public void onResponse(Call<BaseResponse<AuthData>> call, Response<BaseResponse<AuthData>> response) {
                Timber.d("Received authentication response");
                if (response.isSuccessful()) {
                    if (response.body() != null && response.body().getData() != null) {
                        Timber.d("Authentication request succeeded!");
                        AuthData authData = response.body().getData();
                        mAccessToken = authData.getAccess();
                        String refreshToken = authData.getRefresh();
                        // TODO validate

                        AuthenticationUtils.authorize(mAccessToken, refreshToken);
                        Timber.d("DONE");

                        fetchMetadata();
                    } else {
                        Timber.d("Authentication request returned no data :(");
                    }
                } else {
                    Timber.d("Authentication request failed :(");
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<AuthData>> call, Throwable t) {
                Timber.d("Request failed: " + t.getLocalizedMessage());
            }
        });
    }
}
