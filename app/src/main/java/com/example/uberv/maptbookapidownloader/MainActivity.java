package com.example.uberv.maptbookapidownloader;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.uberv.maptbookapidownloader.Utils.AuthenticationUtils;
import com.example.uberv.maptbookapidownloader.Utils.JsoupParser;
import com.example.uberv.maptbookapidownloader.Utils.Utils;
import com.example.uberv.maptbookapidownloader.models.BookMetadata;
import com.example.uberv.maptbookapidownloader.models.Chapter;
import com.example.uberv.maptbookapidownloader.models.Page;
import com.example.uberv.maptbookapidownloader.models.Section;
import com.example.uberv.maptbookapidownloader.models.requests.UserCredentials;
import com.example.uberv.maptbookapidownloader.models.responses.AuthData;
import com.example.uberv.maptbookapidownloader.models.responses.BaseResponse;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.apache.commons.lang3.StringEscapeUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import timber.log.Timber;

import static com.example.uberv.maptbookapidownloader.App.getMaptService;

public class MainActivity extends AppCompatActivity {

    public static final String IMAGE_FILENAME_REGEX = "(image.+.jpg)";
    private static final String ERROR_LOG_FILE_NAME = "ERRORS";
    private static final long DELAY = 100;

    @BindView(R.id.status_tv)
    TextView mStatusTv;
    @BindView(R.id.book_id_et)
    EditText mBookIdEt;
    @BindView(R.id.email_et)
    EditText mLoginEt;
    @BindView(R.id.password_et)
    EditText mPassEt;
    @BindView(R.id.download_progress)
    ProgressBar mProgress;

    private long mBookId = 9781785887949L;
    private BookMetadata mBookMetadata;
    private String mAccessToken = null;
    public final long mSessionId = System.currentTimeMillis();
    private List<Page> mPages;
    private Pattern mImageFileNamePattern = Pattern.compile(IMAGE_FILENAME_REGEX);
    private int mEnqueuedImages = 0;

    private List<Target> mTargets = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        AuthenticationUtils.deAuthorize();
        ButterKnife.bind(this);
        Timber.d("HELLO");
        Picasso.with(MainActivity.this).setLoggingEnabled(true);

        Pair<String, String> credentials = AuthenticationUtils.getCredentials();
        String email = credentials.first;
        String pwd = credentials.second;

        if (email != null && pwd != null) {
            mLoginEt.setText(email);
            mPassEt.setText(pwd);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Timber.d("Resumed");
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
        String mBookIdString = mBookIdEt.getText().toString();
        if (!TextUtils.isEmpty(mBookIdString)) {
            mBookId = Long.parseLong(mBookIdString);
        }

        String email = mLoginEt.getText().toString();
        String pass = mPassEt.getText().toString();
        AuthenticationUtils.setCredentials(email, pass);

        if (!AuthenticationUtils.isAuthorized()) {
            mAccessToken = AuthenticationUtils.getAccessToken();
            Timber.d("Was logged in");
            fetchMetadata();
        } else {
            Timber.d("Not logged in");
            // TODO add
            UserCredentials credentials = new UserCredentials(email, pass);

            callAuthenticationService(credentials);
        }
    }

    private void setStatus(final String status) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mStatusTv.setText(status);
            }
        });
    }

    private void fetchMetadata() {
        setStatus("Fetching metadata for book #" + mBookId);
        App.getMaptService().getBookMetadata(mBookId)
                .enqueue(new Callback<BaseResponse<BookMetadata>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<BookMetadata>> call, Response<BaseResponse<BookMetadata>> response) {
                        Timber.d("Response");
                        mBookMetadata = response.body().getData();
                        int chaptersCount = 0;
                        for (Chapter chapter : mBookMetadata.getChapters()) {
                            chaptersCount += chapter.getSections().size();
                        }
                        mProgress.setMax(chaptersCount);
                        startLoadingBook();
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<BookMetadata>> call, Throwable t) {
                        Timber.d("Failure");
                    }
                });
    }

    private void startLoadingBook() {
        setStatus("Starting loading book #" + mBookId);
        new Thread(new Runnable() {
            @Override
            public void run() {
                String chapterIdRegex = "^(\\w+)";
                Pattern pattern = Pattern.compile(chapterIdRegex);
                Matcher m;
                String chapterId;
                String sectionId;
                Chapter chapter;
                Section section;
                for (int chapterIndex = 0; chapterIndex < mBookMetadata.getChapters().size(); chapterIndex++) {
                    chapter = mBookMetadata.getChapters().get(chapterIndex);
                    for (int sectionIndex = 0; sectionIndex < chapter.getSections().size(); sectionIndex++) {
                        section = chapter.getSections().get(sectionIndex);
                        // TODO add support for first chapter instead of skipping
                        // notes: if Sections.seoUrl is not of format d/xyz/xyz, but is X, then it
                        // is start of chapter url
                        // https://www.packtpub.com/mapt-rest/users/me/products/9781787124417/chapters/8
                        // https://www.packtpub.com/mapt-rest/users/me/products/9781787124417/chapters/8/sections/ch08lvl1sec71

                        Timber.d("Downloading for section:\n" + section.toString());
                        // Prepare data
                        m = pattern.matcher(section.getSeoUrl());
                        if (m.find()) {
                            chapterId = "section/" + m.group();
                        } else {
                            chapterId = "XYZ";
                            Timber.e("Could not find chapter if from seo url at section:\n" + section.toString());
//                            continue;
                        }
                        // TODO replace with regex
                        if (!chapterId.contains("ch")) {
                            chapterId = null;
                        }
                        sectionId = section.getId();

                        setStatus(String.format("Chapter %s: '%s'\nSection %s: '%s",
                                chapterId, chapter.getTitle(), sectionId, section.getTitle()));

//                        downloadBookPage(chapterId, sectionId);
                        try {
                            Response<BaseResponse<Page>> response = App.getMaptService()
                                    .getBookPage(mBookId, chapterId == null ? sectionId : chapterId, chapterId == null ? "" : sectionId, "Bearer " + mAccessToken)
                                    .execute();
                            while (response.code() == 429) {
                                Timber.d("ERROR code 429");
                                Timber.d("Halting for 60 seconds.");
                                Utils.delay(60000);
                                Timber.d("Repeating request...");
                                response = App.getMaptService()
                                        .getBookPage(mBookId, chapterId == null ? sectionId : chapterId, chapterId == null ? "" : sectionId, "Bearer " + mAccessToken)
                                        .execute();
                            }
                            Timber.d("Request succeeded");

                            Page page = response.body().getData();
                            page.setContent(formatPageContent(page.getContent()));
                            // TODO use JSOP to integrate images

                            if (mPages == null) {
                                mPages = new ArrayList<>();
                            }
                            mPages.add(page);

                            String fileName = String.format("c%d_s%d_%s.html",
                                    chapterIndex + 1, sectionIndex + 1, section.getTitle().replaceAll("/", "_"));
                            String htmlText = prepareHtml(page.getContent());
                            // TODO fetch images
                            List<String> imageUrls = JsoupParser.parse(htmlText);
                            for (String imageUrl : imageUrls) {
                                downloadImage(imageUrl);
                            }
                            htmlText = htmlText.replaceAll("/graphics/" + mBookId + "/", "");

                            FileUtils.writeToFile(FileUtils.createBookFile(mBookMetadata.getTitle() + "_" + mSessionId, fileName), htmlText);

                            mProgress.setProgress(mProgress.getProgress() + 1);

                            Timber.d("response");
                        } catch (IOException e) {
                            e.printStackTrace();
                            logError(e.getMessage());
                        }

                        // TODO adjust delay to aboid response 429 (too many requests)
                        try {
                            Thread.sleep(DELAY);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    Timber.d("chapter " + chapter.getId());
                }
                Timber.d("finished");
                bakeHtmlFiles();
                setStatus("Finished");

                String[] files = new String[]{FileUtils.createDirectory(mBookMetadata.getTitle() + "_" + mSessionId).getPath()};
                // TODO zip
                FileUtils.zip(files, "ZIP_" + mBookMetadata.getTitle() + "_" + mSessionId);
            }
        }).start();
    }

    private void downloadImage(final String imageUrl) {
        Timber.d("downloading image at url: " + imageUrl);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mEnqueuedImages++;
                String url = Constants.MAPT_GRAPHICS_BASE_URL + imageUrl;
                Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Timber.d("Loaded image at url " + imageUrl);
                        saveImage(bitmap, imageUrl);
                        mEnqueuedImages--;
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {
//                        Timber.d("Failed loading image at url " + imageUrl);
                        logError("Failed loading image at url " + imageUrl);
                        mEnqueuedImages--;
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                };
                mTargets.add(target);
                Picasso.with(MainActivity.this)
                        .load(Constants.MAPT_GRAPHICS_BASE_URL + imageUrl)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .networkPolicy(NetworkPolicy.NO_CACHE)
                        .into(target);
            }
        });
    }

    private void saveImage(Bitmap bitmap, String imageUrl) {
        Timber.d("saving bitmap for url " + imageUrl);
        Matcher m = mImageFileNamePattern.matcher(imageUrl);
        String fileName;
        if (m.find()) {
            fileName = m.group();
            Timber.d("Extracted image file name " + fileName);
        } else {
            logError("Could not find image file name at url " + imageUrl);
            return;
        }
        FileUtils.saveImageToFile(bitmap, mBookMetadata.getTitle() + "_" + mSessionId, fileName);
    }

    private String formatPageContent(String content) {
        String result;
        try {
            result = StringEscapeUtils.unescapeJava(content);
        } catch (Exception e) {
//            Timber.e(e, "Failed formatting for " + content);
            logError("Failed formatting for " + content + "\n" + e.getMessage());
            result = content;
        }
        return result.replace((char) 160, (char) 32)
                .replace((char) 8211, (char) 45)
                .replaceAll("…", "...");
    }

    private void bakeHtmlFiles() {
        StringBuilder htmlStringBuilder;
        for (Page page : mPages) {
            htmlStringBuilder = new StringBuilder();
            htmlStringBuilder.append("<html>");
            // TODO append and adjust styles (padding is wrong)
            htmlStringBuilder.append(HtmlUtils.STYLES);
            // TODO fix escaping problems (?,! and etc results into unreadable characters
            // TODO or configure converter
            try {
                htmlStringBuilder.append(page.getContent());
            } catch (Exception e) {
                logError(e.getMessage());
                // TODO fix or add some note above "PARSE FAILED"
                htmlStringBuilder.append(page.getContent());
            }
            htmlStringBuilder.append("</html>");
        }
    }

    private String prepareHtml(String data) {
        StringBuilder htmlStringBuilder;
        htmlStringBuilder = new StringBuilder();
        htmlStringBuilder.append("<html>");
        // TODO append styles
        htmlStringBuilder.append(HtmlUtils.STYLES);
        htmlStringBuilder.append(data);
        htmlStringBuilder.append("</html>");
        return htmlStringBuilder.toString();
    }

    private void logError(String message) {
        Timber.d(message);
        File directory = new File(mBookMetadata.getTitle());
        directory.mkdir();
        File file = new File(directory, ERROR_LOG_FILE_NAME);
        FileUtils.writeToFile(file, message);
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
                        logError("Authentication request returned no data :(");
                    }
                } else {
                    Timber.d("Authentication request failed :(");
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<AuthData>> call, Throwable t) {
                logError("Request failed: " + t.getLocalizedMessage());
            }
        });
    }
}
