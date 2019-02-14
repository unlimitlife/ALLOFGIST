package com.example.x_note.allofgistlite;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;

public class OfficialSiteFragment extends Fragment {

    //SImple Image Resizer variables
    private final int REQUEST_WIDTH = 256;
    private final int REQUEST_HEIGHT = 256;

    //dataStorage
    private ArrayList<Site> itemList;
    private Hashtable<String,Boolean> buttonCount;

    private ArrayList<Integer> officialKeylist;


    //GridView 데이터 연결
    private GridView siteList;
    private SiteAdapter siteAdapter;

    private String id;
    private String TAG;

    public OfficialSiteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_site, container, false);

        id = getArguments().getString("ID");
        TAG = getArguments().getString("TAG");

        itemList = new ArrayList<Site>();
        setData(itemList);
        //button 클릭 횟수 초기화
        buttonCount = new Hashtable<String, Boolean>();
        for(int i=0;i<itemList.size();i++)
            buttonCount.put(itemList.get(i).getMsite_name(), false);


        //즐겨찾기 불러올때
        if(TAG.equals("FAVORITE_SETTING")) {
            try {
                officialKeylist = getArguments().getIntegerArrayList("OFFICIAL_KEYLIST");
                if (officialKeylist.size() > 0) {
                    for (int i = 0; i < officialKeylist.size(); i++)
                        buttonCount.put(itemList.get(officialKeylist.get(i) - 1).getMsite_name(), true);
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }


        //GridView 데이터 연결
        siteList = (GridView) rootView.findViewById(R.id.allsites);
        siteAdapter = new SiteAdapter(getActivity(),itemList);
        siteList.setAdapter(siteAdapter);

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


            if(buttonCount.get(siteHolder.mTextView.getText()))
            {
                siteHolder.mLinearlayout.setBackgroundResource(R.drawable.item_selected_state);
            }


            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    buttonCount.put(itemList.get(position).getMsite_name(),!buttonCount.get(itemList.get(position).getMsite_name()));
                    if(buttonCount.get(itemList.get(position).getMsite_name())){
                        try {
                            switch(TAG){
                                case "FAVORITE_SETTING":
                                    ((FavoriteSettingActivity) getActivity()).editKeyList("OFFICIAL", position);
                                    view.setBackgroundResource(R.drawable.item_selected_state);
                                    break;
                                case "TUTORIAL":
                                    ((Tutorial) getActivity()).editKeyList("OFFICIAL", position);
                                    view.setBackgroundResource(R.drawable.item_selected_state);
                                    break;
                                default:
                                    break;
                            }
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }
                    }
                    else {
                        try {
                            switch(TAG){
                                case "FAVORITE_SETTING":
                                    ((FavoriteSettingActivity) getActivity()).editKeyList("OFFICIAL", position);
                                    view.setBackgroundResource(R.drawable.item_unselected_state);
                                    break;
                                case "TUTORIAL":
                                    ((Tutorial) getActivity()).editKeyList("OFFICIAL", position);
                                    view.setBackgroundResource(R.drawable.item_unselected_state);
                                    break;
                                default:
                                    break;
                            }
                        }catch (NullPointerException e){
                            e.printStackTrace();
                        }
                    }
                }
            });

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
        itemList.add(new Site("GIST home", "https://www.gist.ac.kr/kr/", "https://blog.naver.com/gist1993", "https://www.facebook.com/GIST.ac.kr/", "https://www.instagram.com/gist1993/", R.drawable.home));
        itemList.add(new Site("Gel", "https://gel.gist.ac.kr/", R.drawable.gel));
        itemList.add(new Site("Zeus system", "https://zeus.gist.ac.kr", R.drawable.zeus));
        itemList.add(new Site("수강 신청 사이트", "https://zeus.gist.ac.kr/sys/lecture/lecture_main.do", R.drawable.courseregisteration));
        itemList.add(new Site("Portal system", "https://portal.gist.ac.kr/intro.jsp", R.drawable.portal));
        itemList.add(new Site("Email system", "https://mail.gist.ac.kr/loginl?locale=ko_KR", R.drawable.email));
        itemList.add(new Site("GIST college", "https://college.gist.ac.kr/", R.drawable.college));
        itemList.add(new Site("GIST library", "https://library.gist.ac.kr/", R.drawable.library));
        itemList.add(new Site("학내공지", "https://college.gist.ac.kr/main/Sub040203", R.drawable.haknaegongji));
        itemList.add(new Site("GIST 대학생", "https://www.facebook.com/groups/giststudent/", R.drawable.giststudent));
        itemList.add(new Site("GIST 대나무숲", "https://www.facebook.com/GISTIT.ac.kr/", R.drawable.gistdaenamoo));
        itemList.add(new Site("GIST 대나무숲 제보함", "https://fbpage.kr/?pi=128#/submit", R.drawable.gistdaenamoojaeboo));
        itemList.add(new Site("언어교육센터", "https://language.gist.ac.kr/", R.drawable.language));
        itemList.add(new Site("창업진흥센터", "https://www.facebook.com/gistbi/?ref=py_c", R.drawable.changup));
        itemList.add(new Site("학과별 사이트", R.drawable.majorset));
    }
}
