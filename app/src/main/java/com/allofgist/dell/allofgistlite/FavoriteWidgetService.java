package com.allofgist.dell.allofgistlite;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import javax.net.ssl.HttpsURLConnection;

import static com.allofgist.dell.allofgistlite.Allsite_OfficialSiteFragment.getCircledBitmap;

public class FavoriteWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new FavoriteWidgetServiceFactory(this.getApplicationContext(), intent);
    }
}

class FavoriteWidgetServiceFactory implements RemoteViewsService.RemoteViewsFactory {
    public static ArrayList<Integer> keylist;
    private Context mContext;
    private ArrayList<Site> itemList;
    private ArrayList<Site> major_set;

    private final int REQUEST_WIDTH = 256;
    private final int REQUEST_HEIGHT = 256;

    public FavoriteWidgetServiceFactory(Context context, Intent intent) {
        mContext = context;
        //keylist = intent.getIntegerArrayListExtra("KEYLIST");
        //keylist = new ArrayList<Integer>();
        /*if(MainActivity.keylist != null)
            this.keylist = MainActivity.keylist;
        else {
            keylist = new ArrayList<Integer>();
            keylist.add(0);
        }*/
    }

    // Initialize the data set.
    public void onCreate() {
        setData();
        keylist = new ArrayList<Integer>();
        new FavoriteLoadTask().execute(MainActivity.id);
    }

    public RemoteViews getViewAt(int position) {

        RemoteViews rv = new RemoteViews(mContext.getPackageName(), R.layout.gridviewitem_widget);

        if(keylist.get(0)==0) {
            //  rv.setEmptyView(R.id.favoritelist_widget, R.id.favorite_notice_widget);
        }
        else {
            try {
                Site favoriteSite = itemList.get(keylist.get(position));
                String siteUrl = "https://www.google.com";

                final BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeResource(mContext.getResources(), favoriteSite.getMsite_imagesource(), options);
                BitmapFactory.decodeResource(mContext.getResources(), favoriteSite.getMsite_imagesource(), options);
                BitmapFactory.decodeResource(mContext.getResources(), favoriteSite.getMsite_imagesource(), options);
                options.inSampleSize = setSimpleSize(options, REQUEST_WIDTH, REQUEST_HEIGHT);
                options.inJustDecodeBounds = false;
                Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), favoriteSite.getMsite_imagesource(), options);

                rv.setImageViewBitmap(R.id.image_widget, getCircledBitmap(bitmap));

                if (favoriteSite.getMsite_name().equals("")) {

                } else if (favoriteSite.getMsite_name().equals("학과별 사이트")) {
                    //
                } else if (favoriteSite.getMsite_name().equals("GIST home")) {
                    siteUrl = favoriteSite.getMsite_urlW();
                } else if (favoriteSite.getMsite_name().equals("GIST 신문")) {
                    siteUrl = favoriteSite.getMsite_urlF();
                } else if (favoriteSite.getMsite_name().equals("춤 동아리 막무가내") || favoriteSite.getMsite_name().equals("힙합 동아리 Ignition") || favoriteSite.getMsite_name().equals("노래 동아리 싱송생송") || favoriteSite.getMsite_name().equals("오케스트라 동아리 악동") || favoriteSite.getMsite_name().equals("연극 동아리 지대로") || favoriteSite.getMsite_name().equals("영상편집 동아리 The GIST")) {
                    siteUrl = favoriteSite.getMsite_urlF();
                } else
                    siteUrl = favoriteSite.getMsite_url();


                Intent intent = new Intent(Intent.ACTION_VIEW);
                if (siteUrl.contains("https://www.facebook.com")) {
                    if (getOpenFacebookIntent(mContext, siteUrl).contains("https://www.facebook.com")) {
                        intent.setData(Uri.parse(siteUrl));
                    } else {
                        intent.setData(Uri.parse(getOpenFacebookIntent(mContext, siteUrl)));
                    }
                } else {
                    intent.setData(Uri.parse(siteUrl));
                }

                rv.setOnClickFillInIntent(R.id.clickicon_widget, intent);

            } catch (NullPointerException e) {
                e.printStackTrace();
                //rv.setEmptyView(R.id.favoritelist_widget, R.id.favorite_notice_widget);
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
                onDataSetChanged();
            }
        }
        return rv;

    }

    @Override
    public void onDataSetChanged() {
        new FavoriteLoadTask().execute(MainActivity.id);
    }

    @Override
    public void onDestroy(){
    }

    @Override
    public int getCount() {
        if(keylist.size()>0) {
            if (keylist.get(0) == 0)
                return 0;
        }
        else
            return 0;

        return keylist.size();
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }



    class FavoriteLoadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String data = "";
            String ID = (String)strings[0];

            String serverUrl = "https://server.allofgist.com/favoriteload.php";
            String postParameters = "id="+ID;

            try{
                URL url = new URL(serverUrl);
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();

                httpsURLConnection.setReadTimeout(5000);
                httpsURLConnection.setConnectTimeout(5000);
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.connect();


                OutputStream outputStream = httpsURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpsURLConnection.getResponseCode();
                Log.d("phptest","POST response code - "+responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == httpsURLConnection.HTTP_OK){
                    inputStream = httpsURLConnection.getInputStream();
                }
                else{
                    inputStream = httpsURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuffer sb = new StringBuffer();
                String line = null;

                while((line = bufferedReader.readLine())!=null){
                    sb.append(line);
                }

                data = sb.toString();
                bufferedReader.close();

            }catch (Exception e){
                Log.d("phptest", "Signup: Error", e);
            }
            return data;

        }

        @Override
        protected void onPostExecute(String s) {
            int status=1;
            if(s.equals("")) {
                //GrayToast(getApplicationContext(), "서버 접속을 실패하였습니다.");
            }
            else{
                if(getCount()==0)
                    status = 0;
                String[] splitFavorite = s.split(",");

                keylist = new ArrayList<Integer>();
                for (int i=0; i<splitFavorite.length; i++)
                    keylist.add(Integer.parseInt(splitFavorite[i]));
                if(keylist.size()>1){
                    keylist.remove(Integer.valueOf(0));
                }



                //위젯 업데이트
                if(status==0) {
                    AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(mContext);
                    int appWidgetIds[] = appWidgetManager.getAppWidgetIds(new ComponentName(mContext, FavoriteWidget.class));
                    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.favoritelist_widget);
                    status=1;
                }
            }
        }
    }


    public void setData(){
        itemList = new ArrayList<Site>();
        itemList.add(new Site("",R.drawable.default_image));
        itemList.add(new Site("GIST home", "https://www.gist.ac.kr/kr/","https://blog.naver.com/gist1993","https://www.facebook.com/GIST.ac.kr/","https://www.instagram.com/gist1993/", R.drawable.home));
        itemList.add(new Site("Gel","https://gel.gist.ac.kr/",R.drawable.gel));
        itemList.add(new Site("Zeus system", "https://zeus.gist.ac.kr", R.drawable.zeus));
        itemList.add(new Site("수강 신청 사이트","https://zeus.gist.ac.kr/sys/lecture/lecture_main.do",R.drawable.courseregisteration));
        itemList.add(new Site("Portal system", "https://portal.gist.ac.kr/intro.jsp", R.drawable.portal));
        itemList.add(new Site("Email system", "https://mail.gist.ac.kr/", R.drawable.email));
        itemList.add(new Site("GIST college", "https://college.gist.ac.kr/", R.drawable.college));
        itemList.add(new Site("GIST library", "https://library.gist.ac.kr/", R.drawable.library));
        itemList.add(new Site("학내공지", "https://college.gist.ac.kr/prog/bbsArticle/BBSMSTR_000000005587/list.do", R.drawable.haknaegongji));
        itemList.add(new Site("GIST 대학생","https://www.facebook.com/groups/giststudent/",R.drawable.giststudent));
        itemList.add(new Site("GIST 대나무숲","https://www.facebook.com/GISTIT.ac.kr/",R.drawable.gistdaenamoo));
        itemList.add(new Site("GIST 대나무숲 제보함","https://fbpage.kr/?pi=128#/submit",R.drawable.gistdaenamoojaeboo));
        itemList.add(new Site("언어교육센터","https://language.gist.ac.kr/",R.drawable.language));
        itemList.add(new Site("창업진흥센터", "https://www.facebook.com/gistbi/?ref=py_c", R.drawable.changup));
        itemList.add(new Site("학과별 사이트",R.drawable.majorset));
        itemList.add(new Site("GIST 총학생회", "https://www.facebook.com/gistunion/", R.drawable.gistunion));
        itemList.add(new Site("GIST 동아리연합회", "https://www.facebook.com/gistclubunite/", R.drawable.clubnight));
        itemList.add(new Site("GIST 하우스", "https://www.facebook.com/GISTcollegeHOUSE/", R.drawable.gisthouse));
        itemList.add(new Site("GIST 문화행사위원회", "https://www.facebook.com/Moonhangwe/", R.drawable.moonhangwe));
        itemList.add(new Site("GIST 신문","https://gistnews.co.kr/", "https://www.facebook.com/GistSinmoon/", R.drawable.gistnews));
        itemList.add(new Site("GIST 홍보대사", "https://blog.naver.com/PostList.nhn?blogId=gist1993&from=postList&categoryNo=28", R.drawable.gionnare));
        itemList.add(new Site("춤 동아리 막무가내", "https://www.facebook.com/gistmacmoo/","https://www.youtube.com/channel/UCHG5tpsQEpnVNzNpGgLfWMw", R.drawable.mackmooganae));
        itemList.add(new Site("힙합 동아리 Ignition", "https://www.facebook.com/GISTignition/","https://www.youtube.com/channel/UCmdXmpzSH7EHwONokx-hYRQ", R.drawable.ignition));
        itemList.add(new Site("노래 동아리 싱송생송", "https://www.facebook.com/gistsingsong/","https://www.youtube.com/channel/UCLr7P2ZBg2SPK6D_3nTmFXQ", R.drawable.singsongsangsong));
        itemList.add(new Site("연극 동아리 지대로", "https://www.facebook.com/GIST-%EC%97%B0%EA%B7%B9-%EB%8F%99%EC%95%84%EB%A6%AC-%EC%A7%80%EB%8C%80%EB%A1%9C-587558981293950/","https://www.youtube.com/channel/UC1ZH8qkJ8jl5uNSyYasIYRw", R.drawable.gidaero));
        itemList.add(new Site("오케스트라 동아리 악동", "https://www.facebook.com/GISTorchestra/","https://www.youtube.com/channel/UCftiqcxbUhfe20Nqxs7ZFGQ", R.drawable.orchestra));
        itemList.add(new Site("기타 동아리 HOTSIX", "https://www.facebook.com/gisthotsix/", R.drawable.hotsix));
        itemList.add(new Site("피아노 동아리 GISRI", "https://www.facebook.com/gistgisri/?ref=py_c", R.drawable.gisri));
        itemList.add(new Site("밴드 Main", "https://www.facebook.com/MAIN-899414343552162/", R.drawable.main));
        itemList.add(new Site("밴드 휴강익스프레스","https://www.facebook.com/%ED%9C%B4%EA%B0%95%EC%9D%B5%EC%8A%A4%ED%94%84%EB%A0%88%EC%8A%A4-1798215557148106/",R.drawable.hugangexpress));
        itemList.add(new Site("밴드 도도한 쭈쭈바", "https://www.facebook.com/dozzu/", R.drawable.dozzu));
        itemList.add(new Site("영화 동아리 Cinergy", "https://www.facebook.com/gistcinergy/", R.drawable.cinergy));
        itemList.add(new Site("영상편집 동아리 The GIST", "https://www.facebook.com/Gentletist/","https://www.youtube.com/channel/UCMUDHS0SZvQilFe5h6eI9rA/videos", R.drawable.thegist));
        itemList.add(new Site("문예창작 동아리 사각사각", "https://www.facebook.com/GIST-%EC%82%AC%EA%B0%81%EC%82%AC%EA%B0%81-238788459851229/", R.drawable.sagaksagak));
        itemList.add(new Site("GIST 고양이 지냥이", "https://www.facebook.com/giscats/", R.drawable.giscat));
        itemList.add(new Site("요리동아리 이쑤시개", "https://www.facebook.com/%EC%9D%B4%EC%91%A4%EC%8B%9C%EA%B0%9C-%EC%9A%94%EB%A6%AC%ED%95%98%EB%8A%94-GIST%EC%83%9D-272551203239747/?ref=py_c", R.drawable.essosigae));
        itemList.add(new Site("칵테일 동아리 MixoloGIST", "https://www.facebook.com/Mixologist-1584231725157207", R.drawable.mixologist));
        itemList.add(new Site("보드게임 동아리 BGM", "https://www.facebook.com/GISTBGM", R.drawable.bgm));
        itemList.add(new Site("성소수자 모임 speQtrum", "https://www.facebook.com/gistspeqtrum/", R.drawable.speqtrum));
        itemList.add(new Site("만화 동아리 erutlucbus", "https://www.facebook.com/Erutlucbus/", R.drawable.eru));
        itemList.add(new Site("천체관측 동아리 SpaceBar", "https://www.facebook.com/GISTspacebar/", R.drawable.spacebar));
        itemList.add(new Site("전산 동아리 WING", "https://www.facebook.com/GISTWING/", R.drawable.wing));
        itemList.add(new Site("환경 동아리 온새미로", "https://www.facebook.com/onsaemiro123/", R.drawable.onsaemiro));
        itemList.add(new Site("축구 동아리 Kickass", "https://www.facebook.com/gistkickass/?ref=br_rs", R.drawable.kickass));
        itemList.add(new Site("자유 사상 동아리 Freethinkers", "https://www.facebook.com/freethinkingunion/", R.drawable.freethinkers));
        itemList.add(new Site("스피치 동아리 Toastmasters","https://www.facebook.com/gisttoastmasters/",R.drawable.toastmasters));

        //학과별 사이트 Array
        major_set = new ArrayList<Site>();

        major_set.add(new Site("전기전자컴퓨터공학부", "https://eecs.gist.ac.kr/"));
        major_set.add(new Site("신소재공학부", "https://mse.gist.ac.kr"));
        major_set.add(new Site("기계공학부", "https://me.gist.ac.kr"));
        major_set.add(new Site("지구·환경공학부", "https://env1.gist.ac.kr"));
        major_set.add(new Site("생명과학부", "https://life1.gist.ac.kr"));
        major_set.add(new Site("물리·광과학과", "https://phys.gist.ac.kr/"));
        major_set.add(new Site("화학과", "https://chem.gist.ac.kr"));
        major_set.add(new Site("융합기술원", "https://iit.gist.ac.kr/"));
        major_set.add(new Site("나노바이오재료전자공학과", "https://wcu.gist.ac.kr/"));
    }

    //Bitmap resizing
    private int setSimpleSize(BitmapFactory.Options options, int requestWidth, int requestHeight){
        // 이미지 사이즈를 체크할 원본 이미지 가로/세로 사이즈를 임시 변수에 대입.
        int originalWidth = options.outWidth;
        int originalHeight = options.outHeight;

        // 원본 이미지 비율인 1로 초기화
        int size = 1;

        // 해상도가 깨지지 않을만한 요구되는 사이즈까지 2의 배수의 값으로 원본 이미지를 나눈다.
        while(requestWidth < originalWidth || requestHeight < originalHeight){
            originalWidth = originalWidth / 2;
            originalHeight = originalHeight / 2;

            size = size * 2;
        }
        return size;
    }

    //페이스북 앱 연동
    public static String getOpenFacebookIntent(Context context, String facebookUrl) {
        try {
            int versionCode = context.getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) {
                facebookUrl = facebookUrl.replaceFirst("www.", "m.");
                if(!facebookUrl.startsWith("https"))
                    facebookUrl = "https://"+facebookUrl;
                return "fb://facewebmodal/f?href=" + facebookUrl;
            }
            else
                return "fb://page/" + facebookUrl.replaceFirst("https://www.facebook.com/", "");
        } catch (Exception e) {
            return facebookUrl;
        }
    }
}
