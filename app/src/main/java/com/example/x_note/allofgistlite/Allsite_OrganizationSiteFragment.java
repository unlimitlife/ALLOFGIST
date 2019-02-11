package com.example.x_note.allofgistlite;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;

public class Allsite_OrganizationSiteFragment extends Fragment {

    //SImple Image Resizer variables
    private final int REQUEST_WIDTH = 256;
    private final int REQUEST_HEIGHT = 256;

    //dataStorage
    private ArrayList<Site> itemList;


    //GridView 데이터 연결
    private GridView siteList;
    private SiteAdapter siteAdapter;

    private String id;

    public Allsite_OrganizationSiteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_site, container, false);

        id = getArguments().getString("ID");

        itemList = new ArrayList<Site>();
        setData(itemList);


        //GridView 데이터 연결
        siteList = (GridView) rootView.findViewById(R.id.allsites);
        siteAdapter = new SiteAdapter(getActivity(),itemList);
        siteList.setAdapter(siteAdapter);

        siteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final Site site = itemList.get(position);
                if (site.getMsite_name() == "GIST 신문") {

                    PopupMenu popupWF = new PopupMenu(getActivity(), view);
                    MenuInflater menuInflater = new MenuInflater(getActivity());
                    menuInflater.inflate(R.menu.web_facebook_popup_menu, popupWF.getMenu());
                    popupWF.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.webWF:
                                    openWebPage(site.getMsite_urlF());
                                    break;
                                case R.id.facebookWF:
                                    openWebPage(site.getMsite_urlY());
                                    break;
                            }
                            return false;
                        }
                    });
                    popupWF.show();
                } else
                    openWebPage(site.getMsite_url());
            }
        });

        return rootView;
    }

    //gridview - item Adapter
    class SiteAdapter extends ArrayAdapter<Site> {

        private ArrayList<Site> itemList;
        private Context mContext;
        private SiteHolder siteHolder;



        public SiteAdapter(Context context, ArrayList<Site> itemList){
            super(context,0,itemList);
            this.itemList = itemList;
            this.mContext = context;
        }


        @Override
        public int getCount() {
            return itemList.size();
        }


        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;

            if(view==null){
                view = LayoutInflater.from(getContext()).inflate(R.layout.gridviewitem_allsites, parent, false);

                siteHolder = new SiteHolder(view);
                view.setTag(siteHolder);

            }
            else
                siteHolder = (SiteHolder) view.getTag();


            siteHolder.mTextView.setText(itemList.get(position).getMsite_name());

            // 비트맵 동그랗게 사이즈 맞게 줄이는 메소드 이용
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getContext().getResources(),itemList.get(position).getMsite_imagesource(),options);
            options.inSampleSize = setSimpleSize(options, REQUEST_WIDTH, REQUEST_HEIGHT);
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(),itemList.get(position).getMsite_imagesource(),options);
            siteHolder.mImageView.setBackground(new ShapeDrawable(new OvalShape()));
            siteHolder.mImageView.setClipToOutline(true);
            siteHolder.mImageView.requestLayout();
            siteHolder.mImageView.setImageBitmap(bitmap);
            siteHolder.mLinearlayout.setBackgroundResource(R.drawable.item_unselected_state);

            return view;
        }

        @Override
        public int getPosition(@Nullable Site item) {
            return itemList.indexOf(item);
        }

    }


    //SiteHolder
    public class SiteHolder{
        public ImageView mImageView;
        public TextView mTextView;
        public LinearLayout mLinearlayout;

        public SiteHolder(View view){
            mImageView = (ImageView) view.findViewById(R.id.image);
            mTextView = (TextView) view.findViewById(R.id.name);
            mLinearlayout = (LinearLayout) view.findViewById(R.id.clickicon);
        }
    }


    //사이트 접속
    public void openWebPage(String url) {
        if (url.contains("https://www.facebook.com")) {
            if (getOpenFacebookIntent(getActivity(), url).contains("https://www.facebook.com")){
                String facebookurl = url.replaceFirst("www.", "m.");
                if(!facebookurl.startsWith("https"))
                    facebookurl = "https://"+facebookurl;
                Intent webView = new Intent(getActivity(), web_interface.class);
                webView.putExtra("Url", facebookurl);
                startActivity(webView);
            }
            else{
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                facebookIntent.setData(Uri.parse(getOpenFacebookIntent(getActivity(), url)));
                startActivity(facebookIntent);
            }
        } else {
            Intent webView = new Intent(getActivity(), web_interface.class);
            webView.putExtra("Url", url);
            startActivity(webView);
        }
    }

    //페이스북 앱 연동
    public String getOpenFacebookIntent(Context context, String facebookUrl) {
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

    //데이터 입력
    public void setData(ArrayList<Site> itemList) {
        itemList.add(new Site("GIST 총학생회", "https://www.facebook.com/gistunion/", R.drawable.gistunion));
        itemList.add(new Site("GIST 동아리연합회", "https://www.facebook.com/gistclubunite/", R.drawable.clubnight));
        itemList.add(new Site("GIST 하우스", "https://www.facebook.com/GISTcollegeHOUSE/", R.drawable.gisthouse));
        itemList.add(new Site("GIST 문화행사위원회", "https://www.facebook.com/Moonhangwe/", R.drawable.moonhangwe));
        itemList.add(new Site("GIST 신문","http://gistnews.co.kr/", "https://www.facebook.com/GistSinmoon/", R.drawable.gistnews));
        itemList.add(new Site("GIST 홍보대사", "http://blog.naver.com/PostList.nhn?blogId=gist1993&from=postList&categoryNo=28", R.drawable.gionnare));
    }
}
