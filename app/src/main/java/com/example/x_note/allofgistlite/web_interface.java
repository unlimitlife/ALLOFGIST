package com.example.x_note.allofgistlite;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.PopupMenu;

public class web_interface extends AppCompatActivity {

    private WebView webView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_interface);
        final ProgressDialog webdialog = new ProgressDialog(web_interface.this);
        webdialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        webdialog.setCancelable(false);

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        Intent url = getIntent();
        webView.loadUrl(url.getStringExtra("Url"));
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return true;
            }
        });
        webView.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int progress) {
                if(!web_interface.this.isFinishing()) {
                    webdialog.show();
                    webdialog.setProgress(0);
                    web_interface.this.setProgress(progress * 1000);

                    webdialog.incrementProgressBy(progress);
                }
                if(progress == 100 && webdialog.isShowing())
                    webdialog.dismiss();
            }
        });

        //웹뷰 멀티 터치 가능하게(줌기능)
        webView.getSettings().setBuiltInZoomControls(true);  //줌 아이콘 사용 설정
        webView.getSettings().setDisplayZoomControls(false);
        webView.getSettings().setSupportZoom(true);

        //웹뷰 화면 조정(사이트 마다 잘릴 수 있으니까 화면이)
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);

        webView.getSettings().setDomStorageEnabled(true);

        //웹뷰 종료 버튼
        ImageButton clear = (ImageButton)findViewById(R.id.clearBack);
        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //웹뷰 설정버튼
        ImageButton more = (ImageButton)findViewById(R.id.moreVert);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMore = new PopupMenu(getApplicationContext(), view);

                getMenuInflater().inflate(R.menu.webview_more_popup_menu, popupMore.getMenu());
                popupMore.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.copylink:
                                setClipBoardLink(getApplicationContext(),webView.getUrl());
                                break;
                            case R.id.openchrome:
                                openWebPage(webView.getUrl());
                                break;
                        }
                        return false;
                    }
                });
                popupMore.show();
            }
        });

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()){
            webView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private class WebViewClientClass extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    public static void setClipBoardLink(Context context , String link){

        ClipboardManager clipboardManager = (ClipboardManager)context.getSystemService(context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("label", link);
        clipboardManager.setPrimaryClip(clipData);
        //Toast.makeText(context, context.getString(R.string.toast_text_clipboard_adress), Toast.LENGTH_SHORT).show();

    }

    public void openWebPage(String url){
        Uri webpage = Uri.parse(url);
        Intent siteMove = new Intent(Intent.ACTION_VIEW, webpage);
        if (siteMove.resolveActivity(getApplicationContext().getPackageManager()) != null)
            startActivity(siteMove);
    }
}
