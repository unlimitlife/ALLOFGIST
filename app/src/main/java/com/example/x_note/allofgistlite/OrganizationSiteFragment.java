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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Hashtable;

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

    public OrganizationSiteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_site, container, false);

        id = getArguments().getString("ID");

        itemList = new ArrayList<Site>();
        setData(itemList);
        //button 클릭 횟수 초기화
        buttonCount = new Hashtable<String, Boolean>();
        for(int i=0;i<itemList.size();i++)
            buttonCount.put(itemList.get(i).getMsite_name(),false);

        //즐겨찾기 불러올때
        try{
            organizationKeylist = getArguments().getIntegerArrayList("ORGANIZATION_KEYLIST");
            if(organizationKeylist.size()>0){
                for(int i=0;i<organizationKeylist.size();i++)
                    buttonCount.put(itemList.get(organizationKeylist.get(i)).getMsite_name(),true);
            }
        }catch(NullPointerException e){
            e.printStackTrace();
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


        //임시로
        protected void AllRemove(){
            SharedPreferences sharedPreferences = mContext.getSharedPreferences("tutorial", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.commit();
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
                        FavoriteTask favoriteTask = new FavoriteTask();
                        favoriteTask.execute("http://13.124.99.123/favoriteinsert.php",id,(position+15)+"");
                        SharedPreferences.Editor favoriteEditor = getActivity().getSharedPreferences("FAVORITE_KEYLIST",Context.MODE_PRIVATE).edit();
                        favoriteEditor.putString("KEYLIST_"+id+"_"+(position+15),"OK");
                        favoriteEditor.apply();
                        view.setBackgroundResource(R.drawable.item_selected_state);
                    }
                    else {
                        FavoriteTask favoriteTask = new FavoriteTask();
                        favoriteTask.execute("http://13.124.99.123/favoritedelete.php",id,(position+15)+"");
                        SharedPreferences.Editor favoriteEditor = getActivity().getSharedPreferences("FAVORITE_KEYLIST",Context.MODE_PRIVATE).edit();
                        favoriteEditor.putString("KEYLIST_"+id+"_"+(position+15),"NONE");
                        favoriteEditor.apply();
                        view.setBackgroundResource(R.drawable.item_unselected_state);
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


    //즐겨찾기 DB 저장, 삭제
    class FavoriteTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings) {
            String ID = (String)strings[1];
            String siteNum = (String)strings[2];

            String serverUrl = (String)strings[0];
            String postParameters = "id="+ID+"&favoritehomepage="+siteNum;

            try{
                URL url = new URL(serverUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

                httpURLConnection.setReadTimeout(5000);
                httpURLConnection.setConnectTimeout(5000);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.connect();


                OutputStream outputStream = httpURLConnection.getOutputStream();
                outputStream.write(postParameters.getBytes("UTF-8"));
                outputStream.flush();
                outputStream.close();

                int responseStatusCode = httpURLConnection.getResponseCode();
                Log.d("phptest","POST response code - "+responseStatusCode);

                InputStream inputStream;
                if(responseStatusCode == httpURLConnection.HTTP_OK){
                    inputStream = httpURLConnection.getInputStream();
                }
                else{
                    inputStream = httpURLConnection.getErrorStream();
                }

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder sb = new StringBuilder();
                String line = null;

                while((line = bufferedReader.readLine())!=null){
                    sb.append(line);
                }

                bufferedReader.close();

                return sb.toString();

            }catch (Exception e){
                Log.d("phptest", "Signup: Error", e);

                return new String("Error: "+e.getMessage());
            }
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
