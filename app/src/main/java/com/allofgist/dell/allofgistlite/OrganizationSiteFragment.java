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

public class OrganizationSiteFragment extends Fragment {

    //SImple Image Resizer variables
    private final int REQUEST_WIDTH = 256;
    private final int REQUEST_HEIGHT = 256;

    //dataStorage
    private ArrayList<Site> itemList;
    private Hashtable<String,Boolean> buttonCount;

    private ArrayList<Integer> organizationKeylist;

    //GridView 데이터 연결
    private GridView siteList;
    private SiteAdapter siteAdapter;

    private String id;
    private String TAG;

    public OrganizationSiteFragment() {
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
                organizationKeylist = getArguments().getIntegerArrayList("ORGANIZATION_KEYLIST");
                if (organizationKeylist.size() > 0) {
                    for (int i = 0; i < organizationKeylist.size(); i++)
                        buttonCount.put(itemList.get(organizationKeylist.get(i)).getMsite_name(), true);
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
                view = LayoutInflater.from(getContext()).inflate(R.layout.gridviewitem_allsites_w_img, parent, false);

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
                                    ((FavoriteSettingActivity) getActivity()).editKeyList("ORGANIZATION", position);
                                    view.setBackgroundResource(R.drawable.item_selected_state);
                                    break;
                                case "TUTORIAL":
                                    ((Tutorial) getActivity()).editKeyList("ORGANIZATION", position);
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
                                    ((FavoriteSettingActivity) getActivity()).editKeyList("ORGANIZATION", position);
                                    view.setBackgroundResource(R.drawable.item_unselected_state);
                                    break;
                                case "TUTORIAL":
                                    ((Tutorial) getActivity()).editKeyList("ORGANIZATION", position);
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
        itemList.add(new Site("GIST 총학생회", "https://www.facebook.com/gistunion/", R.drawable.gistunion));
        itemList.add(new Site("GIST 동아리연합회", "https://www.facebook.com/gistclubunite/", R.drawable.clubnight));
        itemList.add(new Site("GIST 하우스", "https://www.facebook.com/GISTcollegeHOUSE/", R.drawable.gisthouse));
        itemList.add(new Site("GIST 문화행사위원회", "https://www.facebook.com/Moonhangwe/", R.drawable.moonhangwe));
        itemList.add(new Site("GIST 신문","http://gistnews.co.kr/", "https://www.facebook.com/GistSinmoon/", R.drawable.gistnews));
        itemList.add(new Site("GIST 홍보대사", "http://blog.naver.com/PostList.nhn?blogId=gist1993&from=postList&categoryNo=28", R.drawable.gionnare));
    }

}
