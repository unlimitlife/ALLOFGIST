package com.allofgist.dell.allofgistlite;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Hashtable;

import static com.allofgist.dell.allofgistlite.Allsite_OfficialSiteFragment.getCircledBitmap;
import static com.allofgist.dell.allofgistlite.Allsite_OfficialSiteFragment.recycleBitmap;
import static com.allofgist.dell.allofgistlite.Allsite_OfficialSiteFragment.setSimpleSize;

public class CircleSiteFragment extends Fragment {

    //SImple Image Resizer variables
    private final int REQUEST_WIDTH = 256;
    private final int REQUEST_HEIGHT = 256;

    //dataStorage
    private ArrayList<Site> itemList;
    private Hashtable<String,Boolean> buttonCount;

    private ArrayList<Integer> circleKeylist;

    //GridView 데이터 연결
    private GridView siteList;
    private SiteAdapter siteAdapter;

    private String id;
    private String TAG;

    public CircleSiteFragment() {
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
            buttonCount.put(itemList.get(i).getMsite_name(),false);

        //즐겨찾기 불러올때
        if(TAG.equals("FAVORITE_SETTING")) {
            try {
                circleKeylist = getArguments().getIntegerArrayList("CIRCLE_KEYLIST");
                if (circleKeylist.size() > 0) {
                    for (int i = 0; i < circleKeylist.size(); i++)
                        buttonCount.put(itemList.get(circleKeylist.get(i)).getMsite_name(), true);
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
            BitmapFactory.decodeResource(getContext().getResources(),itemList.get(position).getMsite_imagesource(),options);
            BitmapFactory.decodeResource(getContext().getResources(),itemList.get(position).getMsite_imagesource(),options);
            options.inSampleSize = setSimpleSize(options, REQUEST_WIDTH, REQUEST_HEIGHT);
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(),itemList.get(position).getMsite_imagesource(),options);
            siteHolder.mImageView.setBackground(getContext().getDrawable(R.drawable.circle_layout));
            siteHolder.mImageView.setClipToOutline(true);
            siteHolder.mImageView.requestLayout();
            siteHolder.mImageView.setImageBitmap(getCircledBitmap(bitmap));
            //Glide.with(getContext()).load(bitmap).into(siteHolder.mImageView);
            siteHolder.mLinearlayout.setBackgroundResource(R.drawable.item_unselected_state);

            if(buttonCount.get(siteHolder.mTextView.getText()))
                siteHolder.mLinearlayout.setBackgroundResource(R.drawable.item_selected_state);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    buttonCount.put(itemList.get(position).getMsite_name(),!buttonCount.get(itemList.get(position).getMsite_name()));
                    if(buttonCount.get(itemList.get(position).getMsite_name())){
                        try {
                            switch(TAG){
                                case "FAVORITE_SETTING":
                                    ((FavoriteSettingActivity) getActivity()).editKeyList("CIRCLE", position);
                                    view.setBackgroundResource(R.drawable.item_selected_state);
                                    break;
                                case "TUTORIAL":
                                    ((Tutorial) getActivity()).editKeyList("CIRCLE", position);
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
                                    ((FavoriteSettingActivity) getActivity()).editKeyList("CIRCLE", position);
                                    view.setBackgroundResource(R.drawable.item_unselected_state);
                                    break;
                                case "TUTORIAL":
                                    ((Tutorial) getActivity()).editKeyList("CIRCLE", position);
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

    @Override
    public void onDestroy() {
        Log.d("OOMTEST", "onDestroy");
        int count = siteList.getCount();
        for (int i =0; i<count; i++){
            try {
                ViewGroup viewGroup = (ViewGroup) siteList.getChildAt(i);
                int childSize = viewGroup.getChildCount();
                for (int j = 0; j < childSize; j++) {
                    if (viewGroup.getChildAt(j) instanceof ImageView) {
                        recycleBitmap((ImageView) viewGroup.getChildAt(j));
                    }
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }

        super.onDestroy();
    }


    //데이터 입력
    public static void setData(ArrayList<Site> itemList) {
        itemList.add(new Site("춤 동아리 막무가내", "https://www.facebook.com/gistmacmoo/","https://www.youtube.com/channel/UCHG5tpsQEpnVNzNpGgLfWMw", R.drawable.mackmooganae));
        itemList.add(new Site("힙합 동아리 Ignition", "https://www.facebook.com/GISTignition/","https://www.youtube.com/channel/UCmdXmpzSH7EHwONokx-hYRQ", R.drawable.ignition));
        itemList.add(new Site("노래 동아리 싱송생송", "https://www.facebook.com/gistsingsong/","https://www.youtube.com/channel/UCLr7P2ZBg2SPK6D_3nTmFXQ", R.drawable.singsongsangsong));
        itemList.add(new Site("연극 동아리 지대로", "https://www.facebook.com/GIST-%EC%97%B0%EA%B7%B9-%EB%8F%99%EC%95%84%EB%A6%AC-%EC%A7%80%EB%8C%80%EB%A1%9C-587558981293950/","https://www.youtube.com/channel/UC1ZH8qkJ8jl5uNSyYasIYRw", R.drawable.gidaero));
        itemList.add(new Site("오케스트라 동아리 악동", "https://www.facebook.com/GISTorchestra/","https://www.youtube.com/channel/UCftiqcxbUhfe20Nqxs7ZFGQ", R.drawable.orchestra));
        itemList.add(new Site("기타 동아리 HOTSIX", "https://www.facebook.com/gisthotsix/", R.drawable.hotsix));
        itemList.add(new Site("피아노 동아리 GISRI", "https://www.facebook.com/gistgisri/?ref=py_c", R.drawable.gisri));
        itemList.add(new Site("밴드 Main", "https://www.facebook.com/MAIN-899414343552162/", R.drawable.main));
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
    }
}
