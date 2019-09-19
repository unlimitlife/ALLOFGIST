package com.allofgist.dell.allofgistlite;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import static com.allofgist.dell.allofgistlite.Allsite_OfficialSiteFragment.getCircledBitmap;
import static com.allofgist.dell.allofgistlite.Allsite_OfficialSiteFragment.setSimpleSize;

import java.util.ArrayList;

public class AllsiteActivity_Search extends AppCompatActivity {

    ProgressBar progressBar;

    ImageButton backButton;
    RelativeLayout searchClassificationLayout;
    TextView searchClassificationText;

    EditText searchEditText;
    RelativeLayout searchTextEraseLayout;
    ImageView searchTextEraseButton;
    ImageButton searchButton;

    RecyclerView searchList;
    private ArrayList<Site> itemList;
    private ArrayList<Site> itemList_official;
    private ArrayList<Site> itemList_organ;
    private ArrayList<Site> itemList_circle;
    private ArrayList<Site> searchResult;
    private ArrayList<String> indexList;


    private ArrayList<Site> major_set = null;
    SearchListAdapter searchListAdapter;
    LinearLayoutManager linearLayoutManager;

    LinearLayout noResultNotice;
    private Context mContext;

    private InputMethodManager imm;


    @Override
    public void onPause() {
        int count = searchList.getChildCount();
        for (int i =0; i<count; i++){
            try {
                ViewGroup viewGroup = (ViewGroup) searchList.getChildAt(i);
                int childSize = viewGroup.getChildCount();
                for (int j = 0; j < childSize; j++) {
                    if (viewGroup.getChildAt(j) instanceof ImageView) {
                        ((ImageView) viewGroup.getChildAt(j)).setImageBitmap(null);
                    }
                    else if (viewGroup.getChildAt(j) instanceof TextView) {
                        ((TextView) viewGroup.getChildAt(j)).setText(null);
                    }
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }
        itemList = null;
        itemList_official = null;
        itemList_organ = null;
        itemList_circle = null;
        searchResult = null;
        indexList = null;


        major_set = null;

        super.onPause();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allsite__search);

        mContext = getApplicationContext();

        InitialSettting();
        setData();

        searchClassificationText.setText("전체");
        searchTextEraseLayout.setVisibility(View.GONE);

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);


        searchClassificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupC = new PopupMenu(getApplicationContext(), v);
                MenuInflater menuInflater = new MenuInflater(getApplicationContext());
                menuInflater.inflate(R.menu.allsites_search_classification_menu, popupC.getMenu());
                popupC.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.search_overall:
                                searchClassificationText.setText("전체");
                                break;
                            case R.id.search_official:
                                searchClassificationText.setText("공식");
                                break;
                            case R.id.search_organ:
                                searchClassificationText.setText("조직");
                                break;
                            case R.id.search_circle:
                                searchClassificationText.setText("동아리");
                                break;
                        }

                        return false;
                    }
                });
                popupC.show();
            }
        });

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(count>0)
                    searchTextEraseLayout.setVisibility(View.VISIBLE);
                else if(count==0)
                    searchTextEraseLayout.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        searchTextEraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchEditText.setText("");
            }
        });

        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String condition;
                if(searchEditText.getText().toString().equals("")||searchEditText.getText().length()==0){
                    GrayToast(getApplicationContext(),"한글자 이상의 검색어를 입력해주세요.");
                }
                switch (searchClassificationText.getText().toString()) {
                    case "전체":
                        condition = "overall";
                        break;
                    case "공식":
                        condition = "official";
                        break;
                    case "조직":
                        condition = "organ";
                        break;
                    case "동아리":
                        condition = "circle";
                        break;
                    default:
                        condition = "overall";
                        break;
                }
                if (searchEditText.getText().toString().isEmpty())
                    GrayToast(getApplicationContext(), "검색어를 입력해주세요.");
                else {
                    imm.hideSoftInputFromWindow(searchEditText.getWindowToken(),0);
                    progressBar.setVisibility(View.VISIBLE);
                    SearchByCondition(condition,searchEditText.getText().toString());
                }
            }

        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        searchList.addOnItemTouchListener(
                new RecyclerItemClickListener(getApplicationContext(), searchList, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        final Site site = searchListAdapter.siteList.get(position);


                        if(site.getMsite_name().equals("")){
                            //
                        }
                        else if (site.getMsite_name().equals("학과별 사이트")) {
                            PopupMenu popupMj = new PopupMenu(mContext, view);
                            MenuInflater menuInflater = new MenuInflater(mContext);
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
                        } else if (site.getMsite_name().equals("GIST home")){
                            PopupMenu popupIFWB = new PopupMenu(mContext, view);
                            MenuInflater menuInflater = new MenuInflater(mContext);
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
                        } else if (site.getMsite_name().equals("GIST 신문")) {

                            PopupMenu popupWF = new PopupMenu(mContext, view);
                            MenuInflater menuInflater = new MenuInflater(mContext);
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
                        } else if (site.getMsite_name().equals("춤 동아리 막무가내") || site.getMsite_name().equals("힙합 동아리 Ignition") || site.getMsite_name().equals("노래 동아리 싱송생송") || site.getMsite_name().equals("오케스트라 동아리 악동") || site.getMsite_name().equals("연극 동아리 지대로") || site.getMsite_name().equals("영상편집 동아리 The GIST")) {

                            PopupMenu popupYF = new PopupMenu(mContext, view);
                            MenuInflater menuInflater = new MenuInflater(mContext);
                            menuInflater.inflate(R.menu.youtube_facebook_popup_menu, popupYF.getMenu());
                            popupYF.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                                @Override
                                public boolean onMenuItemClick(MenuItem menuItem) {
                                    switch (menuItem.getItemId()) {
                                        case R.id.facebookYF:
                                            openWebPage(site.getMsite_urlF());
                                            break;
                                        case R.id.youtubeYF:
                                            openWebPage(site.getMsite_urlY());
                                            break;
                                    }
                                    return false;
                                }
                            });
                            popupYF.show();
                        } else
                            openWebPage(site.getMsite_url());
                    }

                    @Override

                    public void onLongItemClick(View view, int position) {

                    }
                })
        );
    }


    public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.SearchHolder>{

        SearchHolder searchHolder;
        private Context context;
        private final int REQUEST_WIDTH = 256;
        private final int REQUEST_HEIGHT = 256;
        private ArrayList<Site> siteList;

        private SearchListAdapter(Context context, ArrayList<Site> searchList) {
            this.context = context;
            siteList = searchList;
        }

        @Override
        public int getItemCount() {
            if(siteList.size()==0)
                progressBar.setVisibility(View.GONE);
            return siteList.size();
        }

        @NonNull
        @Override
        public SearchListAdapter.SearchHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

            View view = null;

            view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.recyclerviewitem_site_search, parent, false);

            searchHolder = new SearchHolder(view);

            return searchHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull SearchHolder searchHolder, int position) {

            Site currentSearch = siteList.get(position);
            searchHolder.title.setText(currentSearch.getMsite_name());

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(context.getResources(), currentSearch.getMsite_imagesource(), options);
            BitmapFactory.decodeResource(context.getResources(), currentSearch.getMsite_imagesource(), options);
            BitmapFactory.decodeResource(context.getResources(), currentSearch.getMsite_imagesource(), options);
            options.inSampleSize = setSimpleSize(options, REQUEST_WIDTH, REQUEST_HEIGHT);
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), currentSearch.getMsite_imagesource(), options);
            searchHolder.image.setBackground(getDrawable(R.drawable.circle_layout));
            searchHolder.image.setClipToOutline(true);

            searchHolder.image.requestLayout();
            searchHolder.image.setImageBitmap(getCircledBitmap(bitmap));

            searchHolder.explanation.setText(currentSearch.getMsite_explanation());

            int index = indexList.indexOf(currentSearch.getMsite_name());
            if(0 <= index && index <= 14)
                searchHolder.class_tv.setText("공식");
            else if(15 <= index && index <= 20)
                searchHolder.class_tv.setText("조직");
            else
                searchHolder.class_tv.setText("동아리");

            progressBar.setVisibility(View.GONE);


        }



        private class SearchHolder extends RecyclerView.ViewHolder{

            TextView title;
            ImageView image;
            TextView class_tv;
            TextView explanation;

            private SearchHolder(View view){
                super(view);
                title = (TextView)view.findViewById(R.id.name_site_search);
                image = (ImageView)view.findViewById(R.id.image_site_search);
                class_tv = (TextView)view.findViewById(R.id.class_site_search);
                explanation = (TextView)view.findViewById(R.id.explanation_site_search);
            }

        }

    }


    public void InitialSettting(){
        backButton = (ImageButton)findViewById(R.id.allsites_search_back_button);
        searchClassificationLayout = (RelativeLayout)findViewById(R.id.allsites_search_classification_layout);
        searchClassificationText = (TextView)findViewById(R.id.allsites_search_classification_text);

        searchEditText =  (EditText)findViewById(R.id.allsites_search_text);
        searchTextEraseLayout = (RelativeLayout)findViewById(R.id.allsites_search_text_erase_layout);
        searchTextEraseButton = (ImageView)findViewById(R.id.allsites_search_text_erase_button);
        searchButton = (ImageButton)findViewById(R.id.allsites_search_button);

        searchList = (RecyclerView)findViewById(R.id.allsites_search_list);

        progressBar = (ProgressBar)findViewById(R.id.allsites_search_progress_bar);
        progressBar.setVisibility(View.GONE);

        noResultNotice = (LinearLayout)findViewById(R.id.no_search_result_notice_allsites);
        noResultNotice.setVisibility(View.GONE);
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());

    }




    public void GrayToast(Context context, String message){
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        View toastView = toast.getView();
        toastView.setBackgroundResource(R.drawable.gray_toast_design);
        toast.show();
    }



    public void SearchByCondition(String condition, String input){
        searchResult = new ArrayList<Site>();

        switch (condition) {
            case "overall":
                searchResult.addAll(itemList);
                break;
            case "official":
                searchResult.addAll(itemList_official);
                break;
            case "organ":
                searchResult.addAll(itemList_organ);
                break;
            case "circle":
                searchResult.addAll(itemList_circle);
                break;
            default:
                searchResult.addAll(itemList);
                break;
        }

        input = input.replaceAll(" ","");

        String input_gist = input;
        input = input.toLowerCase();


        input_gist = input_gist.replace("지스트","GIST");
        input_gist = input_gist.toLowerCase();

        int size = searchResult.size();

        for (int i = size-1; i >= 0 ; i--){

            String cmp = searchResult.get(i).getMsite_name().replaceAll(" ","");
            cmp = cmp.toLowerCase();

            if(cmp.contains(input)||cmp.contains(input_gist))
                continue;
            else
                 searchResult.remove(i);

        }


        searchListAdapter = new SearchListAdapter(getApplicationContext(),searchResult);
        searchList.setAdapter(searchListAdapter);
        searchList.setLayoutManager(linearLayoutManager);

    }

    public void setData(){


        itemList = new ArrayList<Site>();

        itemList.add(new Site("GIST home", "https://www.gist.ac.kr/kr/","https://blog.naver.com/gist1993","https://www.facebook.com/GIST.ac.kr/","https://www.instagram.com/gist1993/", R.drawable.home,"GIST 관련 모든 사이트가 시작되는 메인 사이트입니다."));
        itemList.add(new Site("Gel","https://gel.gist.ac.kr/",R.drawable.gel,"강의 자료와 과제 공지가 올라오는 곳입니다."));
        itemList.add(new Site("Zeus system", "https://zeus.gist.ac.kr", R.drawable.zeus,"개설강좌, 성적, 각 강의 계획표, 등록금 납입 영수증등을 조회 할 수 있으며,  교과평가, 전공 선언,  조교 및 상담 신청,  인터넷 신청, 시설 예약 등을 할 수 있습니다."));
        itemList.add(new Site("수강 신청 사이트","https://zeus.gist.ac.kr/sys/lecture/lecture_main.do",R.drawable.courseregisteration,"수강 신청을 하는 사이트 입니다. 실제 수강 신청을 연습할 수 있는 모의 수강 신청도 할 수 있습니다."));
        itemList.add(new Site("Portal system", "https://portal.gist.ac.kr/intro.jsp", R.drawable.portal,"개인을 위한 GIST 홈페이지라고 생각하면 됩니다. GIST에서 제공하는 여러 서비스(메일, 제우스, 식단, 내 강의시간표, 업무 연락처..)들을 한 화면에서 제공해줍니다."));
        itemList.add(new Site("Email system", "https://mail.gist.ac.kr/", R.drawable.email,"GIST 구성원들이 이용하는 메일 서비스입니다. 메일을 보내고 싶은 사람이 GIST 구성원일 경우, 이름만 기입해도 보내는 사람의 주소를 알 수 있어 편합니다."));
        itemList.add(new Site("GIST college", "https://college.gist.ac.kr/", R.drawable.college,"GIST 대학 홈페이지 입니다(대학원과 구별). (부)전공 과정, 졸업 요건, 학사 편람등의 정보를 볼 수 있으며, 학내공지(필수), 자료실(휴학원, 복학원, 취업정보) 등을 볼 수 있습니다."));
        itemList.add(new Site("GIST library", "https://library.gist.ac.kr/", R.drawable.library,"도서관 홈페이지. 도서 주문, 대여 예약, 대여기간 갱신 등의 도서관 업무가 가능합니다. 이외에도 모의토익, 모의토플, 전자책 등의 서비스가 유용합니다."));
        itemList.add(new Site("학내공지", "https://college.gist.ac.kr/prog/bbsArticle/BBSMSTR_000000005587/list.do", R.drawable.haknaegongji,"GIST와 관련된 모든 공지사항이 올라오는 게시판입니다(항상 들어가봐야 합니다!). 장학금 지원, 해외 봉사 프로그램등 이외에도 재학생이 해택을 많이 받을 수 있는 정보들이 항상 올라옵니다."));
        itemList.add(new Site("GIST 대학생","https://www.facebook.com/groups/giststudent/",R.drawable.giststudent,"GIST 학부생들간의 소통을 할 수 있는 공식 페이스북 페이지입니다."));
        itemList.add(new Site("GIST 대나무숲","https://www.facebook.com/GISTIT.ac.kr/",R.drawable.gistdaenamoo,"GIST와 관련된 이야기들이 올라오는 대나무숲 페이지입니다. 신입생 캠프 기간, 시험 기간, 축제 때 재미있는 이야기가 많이 올라옵니다. (학기 중의 유일한 낙..)"));
        itemList.add(new Site("GIST 대나무숲 제보함","https://fbpage.kr/?pi=128#/submit",R.drawable.gistdaenamoojaeboo,"대나무숲에 제보할 때 보통 쓰지만, 과거의 대나무 숲 글을 검색할 때도 유용합니다."));
        itemList.add(new Site("언어교육센터","https://language.gist.ac.kr/",R.drawable.language,"영어 관련 도움이나 공부 지도를 받을 수 있는 영어 클리닉 수업을 신청할 떄 사용합니다.(영어 클리닉 수업 때는 외국인 교수님과 학생들이 자유롭게 대화를 하거나 영어 기사등을 읽기도 합니다.)"));
        itemList.add(new Site("창업진흥센터", "https://www.facebook.com/gistbi/?ref=py_c", R.drawable.changup,"GIST 내 창업 관련 뉴스가 올라옵니다. GIST는 창업프로그램을 정말 전폭적으로 지원합니다. 창업에 관심 있는 사람이라면 많이 들릴 센터입니다."));
        itemList.add(new Site("학과별 사이트",R.drawable.majorset,"GIST에서 연구하는 분야별 사이트 모음입니다."));



        itemList.add(new Site("GIST 총학생회", "https://www.facebook.com/gistunion/", R.drawable.gistunion,"GIST 학생들의 소리를 대변하는 총학생회입니다. 사무국, 대내협력국, 대외협력국, 소통국, 학술국으로 이루어져 있습니다."));
        itemList.add(new Site("GIST 동아리연합회", "https://www.facebook.com/gistclubunite/", R.drawable.clubnight,"동아리들의 활동을 지원하고 동아리와 관련된 행사를 주관하는 자치기구입니다. 동아리를 창설하고 싶다면 동아리 연합회의 심의를 통과해야 합니다."));
        itemList.add(new Site("GIST 하우스", "https://www.facebook.com/GISTcollegeHOUSE/", R.drawable.gisthouse,"GIST 기숙사를 이루는 G,I,S,T 하우스를 관리하는 학생 자치 기구입니다. 하우스 구성원 간 행사를 통한 친목 도모 및 생활 규칙에 근거해 전반적인 기숙사 관리를 합니다."));
        itemList.add(new Site("GIST 문화행사위원회", "https://www.facebook.com/Moonhangwe/", R.drawable.moonhangwe,"GIST 축제, 체육대회를 비롯해 학교에서 진행되는 많은 행사들을 기획하고 진행하는 학생 자치기구입니다."));
        itemList.add(new Site("GIST 신문","https://gistnews.co.kr/", "https://www.facebook.com/GistSinmoon/", R.drawable.gistnews,"지스트에서 일어나는 모든 일들을 대상으로 기사를 쓰며 정기적으로 신문을 출판하는 신문사입니다. 디자이너, 기자, 편집장, 부편집장으로 구성되어 있습니다."));
        itemList.add(new Site("GIST 홍보대사", "https://blog.naver.com/PostList.nhn?blogId=gist1993&from=postList&categoryNo=28", R.drawable.gionnare,"GIST를 원내, 원외로 활발히 알리는 대외협력국 소속의 학생 홍보단체입니다. 정기소식지, SNS 콘텐츠, 오프라인 행사, 캠퍼스 투어등 많은 홍보 활동을 진행합니다. "));




        itemList.add(new Site("춤 동아리 막무가내", "https://www.facebook.com/gistmacmoo/","https://www.youtube.com/channel/UCHG5tpsQEpnVNzNpGgLfWMw", R.drawable.mackmooganae,"춤 경험이 많은 사람부터 처음 춤을 추는 사람까지 다양한 구성원으로 이루어진 춤 동아리이다. 정기공연, 축제공연을 비롯하여 타 과기원에 초청 공연도 한다. "));
        itemList.add(new Site("힙합 동아리 Ignition", "https://www.facebook.com/GISTignition/","https://www.youtube.com/channel/UCmdXmpzSH7EHwONokx-hYRQ", R.drawable.ignition,"보컬, 프로듀서, 래퍼들로 구성되어 있는 GIST 힙합 동아리 입니다. GIST뿐만 아니라 타 과기원, 광주의 각종 지역 축제에서도 공연을 합니다. 동아리 방에는 녹음시설이 갖추어져 있어 곡 녹음도 할 수 있습니다."));
        itemList.add(new Site("노래 동아리 싱송생송", "https://www.facebook.com/gistsingsong/","https://www.youtube.com/channel/UCLr7P2ZBg2SPK6D_3nTmFXQ", R.drawable.singsongsangsong,"발라드, 락, 랩 등 다양한 장르의 노래를 공연하는 GIST 보컬 동아리입니다. 30만원 상당의 음향장비를 갖추고 있어 자신의 노래 녹음 작업을 할 수 있습니다."));
        itemList.add(new Site("연극 동아리 지대로", "https://www.facebook.com/GIST-%EC%97%B0%EA%B7%B9-%EB%8F%99%EC%95%84%EB%A6%AC-%EC%A7%80%EB%8C%80%EB%A1%9C-587558981293950/","https://www.youtube.com/channel/UC1ZH8qkJ8jl5uNSyYasIYRw", R.drawable.gidaero,"지스트 대학로의 줄임말로 대학로의 심장인 연극동아리입니다. 연극뿐만 아니라 다양한 문화생활을 좋아하는 학생들이 있습니다. 연극 뿐만 아니라 뮤지컬도 공연합니다."));
        itemList.add(new Site("오케스트라 동아리 악동", "https://www.facebook.com/GISTorchestra/","https://www.youtube.com/channel/UCftiqcxbUhfe20Nqxs7ZFGQ", R.drawable.orchestra,"GIST대학의 오케스트라 동아리입니다. 클래식 곡뿐 아니라 OST, 팝송, 뉴에이지 음악 등등 여러 장르의 곡을 연주합니다. 1년에 2번의 정기공연과 2~3번의 소공연을 하고 있습니다."));
        itemList.add(new Site("기타 동아리 HOTSIX", "https://www.facebook.com/gisthotsix/", R.drawable.hotsix,"활발하고 주기적인 멘토-멘티 활동을 통해 기타를 배울 수 있고,  다 같이 공연 준비하고 친목도 다질 수 있는 GIST의 기타 동아리입니다."));
        itemList.add(new Site("피아노 동아리 GISRI", "https://www.facebook.com/gistgisri/?ref=py_c", R.drawable.gisri,"피아노를 좋아하는 사람들이 모인 동아리입니다. 함께 모여 연습하고 피드백을 주고 받는 시간을 주로 가지며 그랜드 피아노에서 공연할 수 있는 기회도 주어집니다."));
        itemList.add(new Site("밴드 Main", "https://www.facebook.com/MAIN-899414343552162/", R.drawable.main,"GIST를 대표하는 실력파 밴드로 보컬을 제외한 모든 세션을 소화하실 수 있는 분, 음악을 듣고 악보로 따낼 수 있으신 분등 수많은 음악적 인재들이 왔다 가셨다고 합니다."));
        itemList.add(new Site("밴드 휴강익스프레스","https://www.facebook.com/%ED%9C%B4%EA%B0%95%EC%9D%B5%EC%8A%A4%ED%94%84%EB%A0%88%EC%8A%A4-1798215557148106/",R.drawable.hugangexpress,"GIST의 탄생과 동시에 시작한 밴드입니다. 가장 오래된 밴드답게 많은 선배들과 함께 정기공연, 버스킹, 야유회, 정기합주, 회식등 여러 활동을 진행합니다."));
        itemList.add(new Site("밴드 도도한 쭈쭈바", "https://www.facebook.com/dozzu/", R.drawable.dozzu,"악기를 배워 본적 없는 초급자들도 밴드를 할 수 있다는 것을 보여주기 위해 만들어진 밴드이다. 단순 공연 뿐만 아니라, MT, 굿즈 제작등 구성원간의 친밀도를 높이는 활동도 합니다. "));
        itemList.add(new Site("영화 동아리 Cinergy", "https://www.facebook.com/gistcinergy/", R.drawable.cinergy,"1년에 자체제작 영화 한 편 완성을 목표로 하고 있는 영화 제작 동아리입니다. 장편 영화 말고도 여러 액션씬 공모전등과 같은 내부 활동과 팀별 단편 영화 제작등의 활동도 합니다."));
        itemList.add(new Site("영상편집 동아리 The GIST", "https://www.facebook.com/Gentletist/","https://www.youtube.com/channel/UCMUDHS0SZvQilFe5h6eI9rA/videos", R.drawable.thegist,"영상제작을 주로 하는 종합예술창작 엔터테인먼트 동아리입니다. UCC, 홍보영상, 뮤직비디오, 합성 사진 제작, 이글루 제작등 하고 싶은 모든 활동을 할 수 있습니다. 동아리 공식 유튜브 채널도 있습니다. "));
        itemList.add(new Site("문예창작 동아리 사각사각", "https://www.facebook.com/GIST-%EC%82%AC%EA%B0%81%EC%82%AC%EA%B0%81-238788459851229/", R.drawable.sagaksagak,"문예 창작 동아리로 글/그림 및 기타 창작활동을 하는 동아리입니다. 시문, 그림 스터디등을 구성하여 모임을 가지거나 1~2개월에 걸쳐 개인당 하나의 작품을 완성하여 자체 전시회에 게시하기도 합니다."));
        itemList.add(new Site("GIST 고양이 지냥이", "https://www.facebook.com/giscats/", R.drawable.giscat,"GIST의 대표 명물! 학부 기숙사 앞 뒤쪽에 만들어진 보금자리에서 살고 있는 고양이들을 보살펴주는 사람들의 모임입니다."));
        itemList.add(new Site("요리동아리 이쑤시개", "https://www.facebook.com/%EC%9D%B4%EC%91%A4%EC%8B%9C%EA%B0%9C-%EC%9A%94%EB%A6%AC%ED%95%98%EB%8A%94-GIST%EC%83%9D-272551203239747/?ref=py_c", R.drawable.essosigae,"요리를 좋아하는 사람들이 모인 동아리입니다. GIST 학부 기숙사 A동 1층에 있는 조리실에서 요리를 하며, 팀별로 요리를 하기도 하며 요리한 음식을 나눔하기도 합니다."));
        itemList.add(new Site("칵테일 동아리 MixoloGIST", "https://www.facebook.com/Mixologist-1584231725157207", R.drawable.mixologist,"칵테일 동아리로 이주에 한 번씩 자체적인 칵테일바를 운영하며, 다른 동아리(Bgm, 스페이스 바, 이그니션 등등) 칵테일바를 운영하기도 합니다. 또 동아리 팀원들과 외부 칵테일바를 방문하기도 합니다. "));
        itemList.add(new Site("보드게임 동아리 BGM", "https://www.facebook.com/GISTBGM", R.drawable.bgm,"보드게임을 좋아하고 즐기는 학생들로 구성된 동아리입니다. 많은 보드게임들이 구비되어 있습니다.  정기적으로 모여 보드게임을 하거나 보드 게임 대여, 리뷰 작성, 동아리 연합행사를 합니다."));
        itemList.add(new Site("성소수자 모임 speQtrum", "https://www.facebook.com/gistspeqtrum/", R.drawable.speqtrum,"GIST 성소수자 모임입니다. 광주지역의 성소수자 모임 폴라리스로 확대 개편되었습니다."));
        itemList.add(new Site("만화 동아리 erutlucbus", "https://www.facebook.com/Erutlucbus/", R.drawable.eru,"만화, 애니메이션, 소설, 게임, 철도, 밀리터리 등의 서브 컬처를 즐기는 동아리입니다. 다른 동아리와 달리 컴퓨터, 수많은 만화책, 소설등 놀거리가 구비되어 있습니다."));
        itemList.add(new Site("천체관측 동아리 SpaceBar", "https://www.facebook.com/GISTspacebar/", R.drawable.spacebar,"별과 함께하는 것을 좋아하는 사람들이 모인 천체관측 동아리입니다. 천체망원경 3대와 천체사진을 찍을 카메라가 한 대 있습니다. 정기적인 관측활동을 진행합니다."));
        itemList.add(new Site("전산 동아리 WING", "https://www.facebook.com/GISTWING/", R.drawable.wing,"전산계열 학술 동아리로 학교에서 다루지 않는 분야에 대한 경험 및 웹과 모바일 서비스 출시를 목표로 하고 있습니다. 추가로 스터디, 수업, 포트폴리오등에 대한 활동도 진행합니다."));
        itemList.add(new Site("환경 동아리 온새미로", "https://www.facebook.com/onsaemiro123/", R.drawable.onsaemiro,"지구환경공학 트랙에서 창설된 환경동아리입닌다. 환경과 관련된 다양한 캠페인, 봉사활동 등을 진행합니다. 물의날, 식목일 등 환경 기념일 캠페인도 진행합니다."));
        itemList.add(new Site("축구 동아리 Kickass", "https://www.facebook.com/gistkickass/?ref=br_rs", R.drawable.kickass,"과기원 축구 교류전 준우승, 원내 체육대회 우승 등 화려한 수상 경력을 가지고 있는 축구 동아리입니다. GIST의 자랑 거리 시설중 하나인 FIFA 규격의 축구장에서 매주 활동합니다."));
        itemList.add(new Site("자유 사상 동아리 Freethinkers", "https://www.facebook.com/freethinkingunion/", R.drawable.freethinkers,"대학 연합 자유사상 동아리입니다. GIST, 서울대, KAIST 등 여러 대학교에서 함께 활동하고 있으며 다양한 사회적, 철학적 주제들로 이야기를 나눕니다."));
        itemList.add(new Site("스피치 동아리 Toastmasters","https://www.facebook.com/gisttoastmasters/",R.drawable.toastmasters,"리더쉽과 소통능력향상을 위한 국제 비영리 단체 Toastmasters의 GIST 지부입니다. 매주 자유롭게 미팅에 참여하여 발표하고 피드백을 받습니다. 클럽 스피치 콘테스트에도 참여합니다."));




        itemList_official = new ArrayList<Site>();

        itemList_official.add(new Site("GIST home", "https://www.gist.ac.kr/kr/","https://blog.naver.com/gist1993","https://www.facebook.com/GIST.ac.kr/","https://www.instagram.com/gist1993/", R.drawable.home,"GIST 관련 모든 사이트가 시작되는 메인 사이트입니다."));
        itemList_official.add(new Site("Gel","https://gel.gist.ac.kr/",R.drawable.gel,"강의 자료와 과제 공지가 올라오는 곳입니다."));
        itemList_official.add(new Site("Zeus system", "https://zeus.gist.ac.kr", R.drawable.zeus,"개설강좌, 성적, 각 강의 계획표, 등록금 납입 영수증등을 조회 할 수 있으며,  교과평가, 전공 선언,  조교 및 상담 신청,  인터넷 신청, 시설 예약 등을 할 수 있습니다."));
        itemList_official.add(new Site("수강 신청 사이트","https://zeus.gist.ac.kr/sys/lecture/lecture_main.do",R.drawable.courseregisteration,"수강 신청을 하는 사이트 입니다. 실제 수강 신청을 연습할 수 있는 모의 수강 신청도 할 수 있습니다."));
        itemList_official.add(new Site("Portal system", "https://portal.gist.ac.kr/intro.jsp", R.drawable.portal,"개인을 위한 GIST 홈페이지라고 생각하면 됩니다. GIST에서 제공하는 여러 서비스(메일, 제우스, 식단, 내 강의시간표, 업무 연락처..)들을 한 화면에서 제공해줍니다."));
        itemList_official.add(new Site("Email system", "https://mail.gist.ac.kr/", R.drawable.email,"GIST 구성원들이 이용하는 메일 서비스입니다. 메일을 보내고 싶은 사람이 GIST 구성원일 경우, 이름만 기입해도 보내는 사람의 주소를 알 수 있어 편합니다."));
        itemList_official.add(new Site("GIST college", "https://college.gist.ac.kr/", R.drawable.college,"GIST 대학 홈페이지 입니다(대학원과 구별). (부)전공 과정, 졸업 요건, 학사 편람등의 정보를 볼 수 있으며, 학내공지(필수), 자료실(휴학원, 복학원, 취업정보) 등을 볼 수 있습니다."));
        itemList_official.add(new Site("GIST library", "https://library.gist.ac.kr/", R.drawable.library,"도서관 홈페이지. 도서 주문, 대여 예약, 대여기간 갱신 등의 도서관 업무가 가능합니다. 이외에도 모의토익, 모의토플, 전자책 등의 서비스가 유용합니다."));
        itemList_official.add(new Site("학내공지", "https://college.gist.ac.kr/prog/bbsArticle/BBSMSTR_000000005587/list.do", R.drawable.haknaegongji,"GIST와 관련된 모든 공지사항이 올라오는 게시판입니다(항상 들어가봐야 합니다!). 장학금 지원, 해외 봉사 프로그램등 이외에도 재학생이 해택을 많이 받을 수 있는 정보들이 항상 올라옵니다."));
        itemList_official.add(new Site("GIST 대학생","https://www.facebook.com/groups/giststudent/",R.drawable.giststudent,"GIST 학부생들간의 소통을 할 수 있는 공식 페이스북 페이지입니다."));
        itemList_official.add(new Site("GIST 대나무숲","https://www.facebook.com/GISTIT.ac.kr/",R.drawable.gistdaenamoo,"GIST와 관련된 이야기들이 올라오는 대나무숲 페이지입니다. 신입생 캠프 기간, 시험 기간, 축제 때 재미있는 이야기가 많이 올라옵니다. (학기 중의 유일한 낙..)"));
        itemList_official.add(new Site("GIST 대나무숲 제보함","https://fbpage.kr/?pi=128#/submit",R.drawable.gistdaenamoojaeboo,"대나무숲에 제보할 때 보통 쓰지만, 과거의 대나무 숲 글을 검색할 때도 유용합니다."));
        itemList_official.add(new Site("언어교육센터","https://language.gist.ac.kr/",R.drawable.language,"영어 관련 도움이나 공부 지도를 받을 수 있는 영어 클리닉 수업을 신청할 떄 사용합니다.(영어 클리닉 수업 때는 외국인 교수님과 학생들이 자유롭게 대화를 하거나 영어 기사등을 읽기도 합니다.)"));
        itemList_official.add(new Site("창업진흥센터", "https://www.facebook.com/gistbi/?ref=py_c", R.drawable.changup,"GIST 내 창업 관련 뉴스가 올라옵니다. GIST는 창업프로그램을 정말 전폭적으로 지원합니다. 창업에 관심 있는 사람이라면 많이 들릴 센터입니다."));
        itemList_official.add(new Site("학과별 사이트",R.drawable.majorset,"GIST에서 연구하는 분야별 사이트 모음입니다."));




        itemList_organ = new ArrayList<Site>();
        itemList_organ.add(new Site("GIST 총학생회", "https://www.facebook.com/gistunion/", R.drawable.gistunion,"GIST 학생들의 소리를 대변하는 총학생회입니다. 사무국, 대내협력국, 대외협력국, 소통국, 학술국으로 이루어져 있습니다."));
        itemList_organ.add(new Site("GIST 동아리연합회", "https://www.facebook.com/gistclubunite/", R.drawable.clubnight,"동아리들의 활동을 지원하고 동아리와 관련된 행사를 주관하는 자치기구입니다. 동아리를 창설하고 싶다면 동아리 연합회의 심의를 통과해야 합니다."));
        itemList_organ.add(new Site("GIST 하우스", "https://www.facebook.com/GISTcollegeHOUSE/", R.drawable.gisthouse,"GIST 기숙사를 이루는 G,I,S,T 하우스를 관리하는 학생 자치 기구입니다. 하우스 구성원 간 행사를 통한 친목 도모 및 생활 규칙에 근거해 전반적인 기숙사 관리를 합니다."));
        itemList_organ.add(new Site("GIST 문화행사위원회", "https://www.facebook.com/Moonhangwe/", R.drawable.moonhangwe,"GIST 축제, 체육대회를 비롯해 학교에서 진행되는 많은 행사들을 기획하고 진행하는 학생 자치기구입니다."));
        itemList_organ.add(new Site("GIST 신문","https://gistnews.co.kr/", "https://www.facebook.com/GistSinmoon/", R.drawable.gistnews,"지스트에서 일어나는 모든 일들을 대상으로 기사를 쓰며 정기적으로 신문을 출판하는 신문사입니다. 디자이너, 기자, 편집장, 부편집장으로 구성되어 있습니다."));
        itemList_organ.add(new Site("GIST 홍보대사", "https://blog.naver.com/PostList.nhn?blogId=gist1993&from=postList&categoryNo=28", R.drawable.gionnare,"GIST를 원내, 원외로 활발히 알리는 대외협력국 소속의 학생 홍보단체입니다. 정기소식지, SNS 콘텐츠, 오프라인 행사, 캠퍼스 투어등 많은 홍보 활동을 진행합니다. "));




        itemList_circle = new ArrayList<Site>();
        itemList_circle.add(new Site("춤 동아리 막무가내", "https://www.facebook.com/gistmacmoo/","https://www.youtube.com/channel/UCHG5tpsQEpnVNzNpGgLfWMw", R.drawable.mackmooganae,"춤 경험이 많은 사람부터 처음 춤을 추는 사람까지 다양한 구성원으로 이루어진 춤 동아리이다. 정기공연, 축제공연을 비롯하여 타 과기원에 초청 공연도 한다. "));
        itemList_circle.add(new Site("힙합 동아리 Ignition", "https://www.facebook.com/GISTignition/","https://www.youtube.com/channel/UCmdXmpzSH7EHwONokx-hYRQ", R.drawable.ignition,"보컬, 프로듀서, 래퍼들로 구성되어 있는 GIST 힙합 동아리 입니다. GIST뿐만 아니라 타 과기원, 광주의 각종 지역 축제에서도 공연을 합니다. 동아리 방에는 녹음시설이 갖추어져 있어 곡 녹음도 할 수 있습니다."));
        itemList_circle.add(new Site("노래 동아리 싱송생송", "https://www.facebook.com/gistsingsong/","https://www.youtube.com/channel/UCLr7P2ZBg2SPK6D_3nTmFXQ", R.drawable.singsongsangsong,"발라드, 락, 랩 등 다양한 장르의 노래를 공연하는 GIST 보컬 동아리입니다. 30만원 상당의 음향장비를 갖추고 있어 자신의 노래 녹음 작업을 할 수 있습니다."));
        itemList_circle.add(new Site("연극 동아리 지대로", "https://www.facebook.com/GIST-%EC%97%B0%EA%B7%B9-%EB%8F%99%EC%95%84%EB%A6%AC-%EC%A7%80%EB%8C%80%EB%A1%9C-587558981293950/","https://www.youtube.com/channel/UC1ZH8qkJ8jl5uNSyYasIYRw", R.drawable.gidaero,"지스트 대학로의 줄임말로 대학로의 심장인 연극동아리입니다. 연극뿐만 아니라 다양한 문화생활을 좋아하는 학생들이 있습니다. 연극 뿐만 아니라 뮤지컬도 공연합니다."));
        itemList_circle.add(new Site("오케스트라 동아리 악동", "https://www.facebook.com/GISTorchestra/","https://www.youtube.com/channel/UCftiqcxbUhfe20Nqxs7ZFGQ", R.drawable.orchestra,"GIST대학의 오케스트라 동아리입니다. 클래식 곡뿐 아니라 OST, 팝송, 뉴에이지 음악 등등 여러 장르의 곡을 연주합니다. 1년에 2번의 정기공연과 2~3번의 소공연을 하고 있습니다."));
        itemList_circle.add(new Site("기타 동아리 HOTSIX", "https://www.facebook.com/gisthotsix/", R.drawable.hotsix,"활발하고 주기적인 멘토-멘티 활동을 통해 기타를 배울 수 있고,  다 같이 공연 준비하고 친목도 다질 수 있는 GIST의 기타 동아리입니다."));
        itemList_circle.add(new Site("피아노 동아리 GISRI", "https://www.facebook.com/gistgisri/?ref=py_c", R.drawable.gisri,"피아노를 좋아하는 사람들이 모인 동아리입니다. 함께 모여 연습하고 피드백을 주고 받는 시간을 주로 가지며 그랜드 피아노에서 공연할 수 있는 기회도 주어집니다."));
        itemList_circle.add(new Site("밴드 Main", "https://www.facebook.com/MAIN-899414343552162/", R.drawable.main,"GIST를 대표하는 실력파 밴드로 보컬을 제외한 모든 세션을 소화하실 수 있는 분, 음악을 듣고 악보로 따낼 수 있으신 분등 수많은 음악적 인재들이 왔다 가셨다고 합니다."));
        itemList_circle.add(new Site("밴드 휴강익스프레스","https://www.facebook.com/%ED%9C%B4%EA%B0%95%EC%9D%B5%EC%8A%A4%ED%94%84%EB%A0%88%EC%8A%A4-1798215557148106/",R.drawable.hugangexpress,"GIST의 탄생과 동시에 시작한 밴드입니다. 가장 오래된 밴드답게 많은 선배들과 함께 정기공연, 버스킹, 야유회, 정기합주, 회식등 여러 활동을 진행합니다."));
        itemList_circle.add(new Site("밴드 도도한 쭈쭈바", "https://www.facebook.com/dozzu/", R.drawable.dozzu,"악기를 배워 본적 없는 초급자들도 밴드를 할 수 있다는 것을 보여주기 위해 만들어진 밴드이다. 단순 공연 뿐만 아니라, MT, 굿즈 제작등 구성원간의 친밀도를 높이는 활동도 합니다. "));
        itemList_circle.add(new Site("영화 동아리 Cinergy", "https://www.facebook.com/gistcinergy/", R.drawable.cinergy,"1년에 자체제작 영화 한 편 완성을 목표로 하고 있는 영화 제작 동아리입니다. 장편 영화 말고도 여러 액션씬 공모전등과 같은 내부 활동과 팀별 단편 영화 제작등의 활동도 합니다."));
        itemList_circle.add(new Site("영상편집 동아리 The GIST", "https://www.facebook.com/Gentletist/","https://www.youtube.com/channel/UCMUDHS0SZvQilFe5h6eI9rA/videos", R.drawable.thegist,"영상제작을 주로 하는 종합예술창작 엔터테인먼트 동아리입니다. UCC, 홍보영상, 뮤직비디오, 합성 사진 제작, 이글루 제작등 하고 싶은 모든 활동을 할 수 있습니다. 동아리 공식 유튜브 채널도 있습니다. "));
        itemList_circle.add(new Site("문예창작 동아리 사각사각", "https://www.facebook.com/GIST-%EC%82%AC%EA%B0%81%EC%82%AC%EA%B0%81-238788459851229/", R.drawable.sagaksagak,"문예 창작 동아리로 글/그림 및 기타 창작활동을 하는 동아리입니다. 시문, 그림 스터디등을 구성하여 모임을 가지거나 1~2개월에 걸쳐 개인당 하나의 작품을 완성하여 자체 전시회에 게시하기도 합니다."));
        itemList_circle.add(new Site("GIST 고양이 지냥이", "https://www.facebook.com/giscats/", R.drawable.giscat,"GIST의 대표 명물! 학부 기숙사 앞 뒤쪽에 만들어진 보금자리에서 살고 있는 고양이들을 보살펴주는 사람들의 모임입니다."));
        itemList_circle.add(new Site("요리동아리 이쑤시개", "https://www.facebook.com/%EC%9D%B4%EC%91%A4%EC%8B%9C%EA%B0%9C-%EC%9A%94%EB%A6%AC%ED%95%98%EB%8A%94-GIST%EC%83%9D-272551203239747/?ref=py_c", R.drawable.essosigae,"요리를 좋아하는 사람들이 모인 동아리입니다. GIST 학부 기숙사 A동 1층에 있는 조리실에서 요리를 하며, 팀별로 요리를 하기도 하며 요리한 음식을 나눔하기도 합니다."));
        itemList_circle.add(new Site("칵테일 동아리 MixoloGIST", "https://www.facebook.com/Mixologist-1584231725157207", R.drawable.mixologist,"칵테일 동아리로 이주에 한 번씩 자체적인 칵테일바를 운영하며, 다른 동아리(Bgm, 스페이스 바, 이그니션 등등) 칵테일바를 운영하기도 합니다. 또 동아리 팀원들과 외부 칵테일바를 방문하기도 합니다. "));
        itemList_circle.add(new Site("보드게임 동아리 BGM", "https://www.facebook.com/GISTBGM", R.drawable.bgm,"보드게임을 좋아하고 즐기는 학생들로 구성된 동아리입니다. 많은 보드게임들이 구비되어 있습니다.  정기적으로 모여 보드게임을 하거나 보드 게임 대여, 리뷰 작성, 동아리 연합행사를 합니다."));
        itemList_circle.add(new Site("성소수자 모임 speQtrum", "https://www.facebook.com/gistspeqtrum/", R.drawable.speqtrum,"GIST 성소수자 모임입니다. 광주지역의 성소수자 모임 폴라리스로 확대 개편되었습니다."));
        itemList_circle.add(new Site("만화 동아리 erutlucbus", "https://www.facebook.com/Erutlucbus/", R.drawable.eru,"만화, 애니메이션, 소설, 게임, 철도, 밀리터리 등의 서브 컬처를 즐기는 동아리입니다. 다른 동아리와 달리 컴퓨터, 수많은 만화책, 소설등 놀거리가 구비되어 있습니다."));
        itemList_circle.add(new Site("천체관측 동아리 SpaceBar", "https://www.facebook.com/GISTspacebar/", R.drawable.spacebar,"별과 함께하는 것을 좋아하는 사람들이 모인 천체관측 동아리입니다. 천체망원경 3대와 천체사진을 찍을 카메라가 한 대 있습니다. 정기적인 관측활동을 진행합니다."));
        itemList_circle.add(new Site("전산 동아리 WING", "https://www.facebook.com/GISTWING/", R.drawable.wing,"전산계열 학술 동아리로 학교에서 다루지 않는 분야에 대한 경험 및 웹과 모바일 서비스 출시를 목표로 하고 있습니다. 추가로 스터디, 수업, 포트폴리오등에 대한 활동도 진행합니다."));
        itemList_circle.add(new Site("환경 동아리 온새미로", "https://www.facebook.com/onsaemiro123/", R.drawable.onsaemiro,"지구환경공학 트랙에서 창설된 환경동아리입닌다. 환경과 관련된 다양한 캠페인, 봉사활동 등을 진행합니다. 물의날, 식목일 등 환경 기념일 캠페인도 진행합니다."));
        itemList_circle.add(new Site("축구 동아리 Kickass", "https://www.facebook.com/gistkickass/?ref=br_rs", R.drawable.kickass,"과기원 축구 교류전 준우승, 원내 체육대회 우승 등 화려한 수상 경력을 가지고 있는 축구 동아리입니다. GIST의 자랑 거리 시설중 하나인 FIFA 규격의 축구장에서 매주 활동합니다."));
        itemList_circle.add(new Site("자유 사상 동아리 Freethinkers", "https://www.facebook.com/freethinkingunion/", R.drawable.freethinkers,"대학 연합 자유사상 동아리입니다. GIST, 서울대, KAIST 등 여러 대학교에서 함께 활동하고 있으며 다양한 사회적, 철학적 주제들로 이야기를 나눕니다."));
        itemList_circle.add(new Site("스피치 동아리 Toastmasters","https://www.facebook.com/gisttoastmasters/",R.drawable.toastmasters,"리더쉽과 소통능력향상을 위한 국제 비영리 단체 Toastmasters의 GIST 지부입니다. 매주 자유롭게 미팅에 참여하여 발표하고 피드백을 받습니다. 클럽 스피치 콘테스트에도 참여합니다."));




        indexList = new ArrayList<String>();
        for(int i = 0; i < itemList.size(); i++)
            indexList.add(itemList.get(i).getMsite_name());



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


    //사이트 접속
    public void openWebPage(String url) {
        if (url.contains("https://www.facebook.com")) {
            if (getOpenFacebookIntent(AllsiteActivity_Search.this, url).contains("https://www.facebook.com")){
                /*String facebookurl = url.replaceFirst("www.", "m.");
                if(!facebookurl.startsWith("https"))
                    facebookurl = "https://"+facebookurl;*/
                Intent chrome = new Intent(Intent.ACTION_VIEW);
                chrome.setData(Uri.parse(url));
                startActivity(chrome);
            }
            else{
                Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
                facebookIntent.setData(Uri.parse(getOpenFacebookIntent(AllsiteActivity_Search.this, url)));
                startActivity(facebookIntent);
            }
        } else {
            Intent chrome = new Intent(Intent.ACTION_VIEW);
            chrome.setData(Uri.parse(url));
            startActivity(chrome);
        }
    }
    //페이스북 앱 연동
    public static String getOpenFacebookIntent(Context context, String facebookUrl) {
        try {
            int versionCode = (int)context.getPackageManager().getPackageInfo("com.facebook.katana", 0).versionCode;
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
