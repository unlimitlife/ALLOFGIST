package com.allofgist.dell.allofgistlite;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
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

import static com.allofgist.dell.allofgistlite.MainActivity.getOpenFacebookIntent;

public class Allsite_OfficialSiteFragment extends Fragment {

    //SImple Image Resizer variables
    public static int REQUEST_WIDTH = 256;
    public static int REQUEST_HEIGHT = 256;

    //dataStorage
    private ArrayList<Site> itemList;
    private ArrayList<Site> major_set = null;


    //GridView 데이터 연결
    private GridView siteList;
    private SiteAdapter siteAdapter;

    private String id;

    //public RequestManager mGlideRequestManager;


    public Allsite_OfficialSiteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_site, container, false);

        //mGlideRequestManager = Glide.with(this);
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
                if (site.getMsite_name().equals( "학과별 사이트")) {
                    PopupMenu popupMj = new PopupMenu(getActivity(), view);
                    MenuInflater menuInflater = new MenuInflater(getActivity());
                    menuInflater.inflate(R.menu.major_site_set_popup_menu, popupMj.getMenu());
                    popupMj.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            for (int i = 0; i < major_set.size(); i++) {
                                if (major_set.get(i).getMsite_name().equals(menuItem.getTitle().toString()))
                                    openWebPage(major_set.get(i).getMsite_url());
                            }
                            return false;
                        }
                    });
                    popupMj.show();
                } else if (site.getMsite_name().equals( "GIST home")) {
                    PopupMenu popupIFWB = new PopupMenu(getActivity(), view);
                    MenuInflater menuInflater = new MenuInflater(getActivity());
                    menuInflater.inflate(R.menu.insta_facebook_web_blog_popup_menu, popupIFWB.getMenu());
                    popupIFWB.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @Override
                        public boolean onMenuItemClick(MenuItem menuItem) {
                            switch (menuItem.getItemId()) {
                                case R.id.webIFWB:
                                    openWebPage(site.getMsite_urlW());
                                    break;
                                case R.id.blogIFWB:
                                    openWebPage(site.getMsite_urlB());
                                    break;
                                case R.id.facebookIFWB:
                                    openWebPage(site.getMsite_urlF());
                                    break;
                                case R.id.instaIFWB:
                                    openWebPage(site.getMsite_urlI());
                                    break;
                            }
                            return false;
                        }
                    });
                    popupIFWB.show();
                } else
                    openWebPage(site.getMsite_url());
            }
        });

        return rootView;
    }

    //gridview - item Adapter
    static class SiteAdapter extends ArrayAdapter<Site> {

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

            siteHolder = new SiteHolder(view);

            siteHolder.mTextView.setText(itemList.get(position).getMsite_name());

            // 비트맵 동그랗게 사이즈 맞게 줄이는 메소드 이용
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getContext().getResources(),itemList.get(position).getMsite_imagesource(),options);
            BitmapFactory.decodeResource(getContext().getResources(),itemList.get(position).getMsite_imagesource(),options);
            BitmapFactory.decodeResource(getContext().getResources(),itemList.get(position).getMsite_imagesource(),options);
            options.inSampleSize = setSimpleSize(options,REQUEST_WIDTH, REQUEST_HEIGHT);
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeResource(getContext().getResources(),itemList.get(position).getMsite_imagesource(),options);
            siteHolder.mImageView.setBackground(getContext().getDrawable(R.drawable.circle_layout));
            //siteHolder.mImageView.setBackground(new ShapeDrawable(new OvalShape()));
            siteHolder.mImageView.setClipToOutline(true);
            siteHolder.mImageView.requestLayout();

            //mGlideRequestManager.load(bitmap).into(siteHolder.mImageView);
            siteHolder.mImageView.setImageBitmap(getCircledBitmap(bitmap));
            siteHolder.mLinearlayout.setBackgroundResource(R.drawable.item_unselected_state);


            return view;
        }

        @Override
        public int getPosition(@Nullable Site item) {
            return itemList.indexOf(item);
        }

    }


    //SiteHolder
    public static class SiteHolder{
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
            if (getOpenFacebookIntent(getContext(), url).contains("https://www.facebook.com")){
                /*String facebookurl = url.replaceFirst("www.", "m.");
                if(!facebookurl.startsWith("https"))
                    facebookurl = "https://"+facebookurl;
                Intent webView = new Intent(MainActivity.this, web_interface.class);
                webView.putExtra("Url", facebookurl);
                startActivity(webView);*/
                Intent chrome = new Intent(Intent.ACTION_VIEW);
                chrome.setData(Uri.parse(url));
                startActivity(chrome);
            }
            else{
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                facebookIntent.setData(Uri.parse(getOpenFacebookIntent(getContext(), url)));
                startActivity(facebookIntent);
            }
        } else {
            /*Intent webView = new Intent(MainActivity.this, web_interface.class);
            webView.putExtra("Url", url);
            startActivity(webView);*/
            Intent chrome = new Intent(Intent.ACTION_VIEW);
            chrome.setData(Uri.parse(url));
            startActivity(chrome);
        }
    }



    public static Bitmap getCircledBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2, bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
    //Bitmap resizing
    public static int setSimpleSize(BitmapFactory.Options options, int requestWidth, int requestHeight){
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

    public static void recycleBitmap(ImageView iv) {
        Drawable d = iv.getDrawable();
        if (d instanceof BitmapDrawable) {
            Bitmap b = ((BitmapDrawable)d).getBitmap();
            b.recycle();
        } // 현재로서는 BitmapDrawable 이외의 drawable 들에 대한 직접적인 메모리 해제는 불가능하다.

        d.setCallback(null);
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
        itemList.add(new Site("학내공지", "https://college.gist.ac.kr/prog/bbsArticle/BBSMSTR_000000005587/list.do", R.drawable.haknaegongji));
        itemList.add(new Site("GIST 대학생", "https://www.facebook.com/groups/giststudent/", R.drawable.giststudent));
        itemList.add(new Site("GIST 대나무숲", "https://www.facebook.com/GISTIT.ac.kr/", R.drawable.gistdaenamoo));
        itemList.add(new Site("GIST 대나무숲 제보함", "http://fbpage.kr/?pi=128#/submit", R.drawable.gistdaenamoojaeboo));
        itemList.add(new Site("언어교육센터", "https://language.gist.ac.kr/", R.drawable.language));
        itemList.add(new Site("창업진흥센터", "https://www.facebook.com/gistbi/?ref=py_c", R.drawable.changup));
        itemList.add(new Site("학과별 사이트", R.drawable.majorset));

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
}
