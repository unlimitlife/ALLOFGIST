package com.allofgist.dell.allofgistlite;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.allofgist.dell.allofgistlite.Allsite_OfficialSiteFragment.getCircledBitmap;
import static com.allofgist.dell.allofgistlite.Allsite_OfficialSiteFragment.setSimpleSize;

public class AllsiteActivity extends AppCompatActivity {

    private Fragment OfficialFragment;
    private Fragment OrganizationFragment;
    private Fragment CircleFragment;

    private TabLayout tabLayout;
    private ViewPager fmViewPager;

    private ImageButton beforeButton;

    Button siteTutorialButton;
    View popupView;
    PopupWindow siteTutorialPopupWindow;
    RecyclerView siteTutorialList;
    ImageButton siteTutorialBackbutton;
    private ArrayList<Site> itemList;
    TutorialListAdapter tutorialListAdapter;
    LinearLayoutManager linearLayoutManager;

    //login information
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_site);


        //로그인 되어있는 ID 불러오기
        id = getIntent().getStringExtra("ID");
        if(id.equals("LOGIN_ERROR")){
            Toast.makeText(AllsiteActivity.this,"로그인 오류!",Toast.LENGTH_LONG).show();
            finish();
        }

        InitialSetting();
        setData();

        beforeButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        siteTutorialBackbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(siteTutorialPopupWindow.isShowing())
                    siteTutorialPopupWindow.dismiss();
            }
        });

        siteTutorialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!siteTutorialPopupWindow.isShowing()){
                    siteTutorialPopupWindow.showAtLocation(popupView, Gravity.CENTER,0,0);
                    siteTutorialList.setAdapter(tutorialListAdapter);
                    siteTutorialList.setLayoutManager(linearLayoutManager);
                }
            }
        });


        fmViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int position) {
                try {
                    tabLayout.getTabAt(position).select();
                }catch (NullPointerException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                fmViewPager.setCurrentItem(tabLayout.getSelectedTabPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private class PagerAdapter extends FragmentStatePagerAdapter {


        public PagerAdapter(FragmentManager fm){
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch(position){
                case 0:
                    return OfficialFragment;
                case 1:
                    return OrganizationFragment;
                case 2:
                    return CircleFragment;
                default:
                    return null;
            }

        }


        @Override
        public int getCount() {
            return 3;
        }
    }


    private View createTabView(String tabName) {

        View tabView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_tab, null);
        TextView txt_name = (TextView) tabView.findViewById(R.id.tab_name);
        txt_name.setText(tabName);
        return tabView;

    }


    public class TutorialListAdapter extends RecyclerView.Adapter<TutorialListAdapter.TutorialHolder>{

        TutorialHolder tutorialHolder;
        private Context context;
        private final int REQUEST_WIDTH = 256;
        private final int REQUEST_HEIGHT = 256;

        private TutorialListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getItemCount() {
            return itemList.size();
        }

        @NonNull
        @Override
        public TutorialListAdapter.TutorialHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {

            View view = null;

            view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.recyclerviewiem_site_tutorial, parent, false);

            tutorialHolder = new TutorialHolder(view);

            return tutorialHolder;
        }

        @Override
        public void onBindViewHolder(@NonNull TutorialHolder tutorialHolder, int position) {

            Site currentTutorial = itemList.get(position);
            tutorialHolder.title.setText(currentTutorial.getMsite_name());

            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(context.getResources(), currentTutorial.getMsite_imagesource(), options);
            BitmapFactory.decodeResource(context.getResources(), currentTutorial.getMsite_imagesource(), options);
            BitmapFactory.decodeResource(context.getResources(), currentTutorial.getMsite_imagesource(), options);
            options.inSampleSize = setSimpleSize(options, REQUEST_WIDTH, REQUEST_HEIGHT);
            options.inJustDecodeBounds = false;
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), currentTutorial.getMsite_imagesource(), options);
            tutorialHolder.image.setBackground(getDrawable(R.drawable.circle_layout));
            tutorialHolder.image.setClipToOutline(true);

            tutorialHolder.image.requestLayout();
            tutorialHolder.image.setImageBitmap(getCircledBitmap(bitmap));

            tutorialHolder.explanation.setText(currentTutorial.getMsite_explanation());

        }



        private class TutorialHolder extends RecyclerView.ViewHolder{

            TextView title;
            ImageView image;
            TextView explanation;

            private TutorialHolder(View view){
                super(view);
                title = (TextView)view.findViewById(R.id.name_site_tutorial);
                image = (ImageView)view.findViewById(R.id.image_site_tutorial);
                explanation = (TextView)view.findViewById(R.id.explanation_site_tutorial);
            }

        }

    }




    public void InitialSetting(){
        Bundle bundle = new Bundle();
        bundle.putString("ID",id);

        OfficialFragment = new Allsite_OfficialSiteFragment();
        OrganizationFragment = new Allsite_OrganizationSiteFragment();
        CircleFragment = new Allsite_CircleSiteFragment();
        OfficialFragment.setArguments(bundle);
        OrganizationFragment.setArguments(bundle);
        CircleFragment.setArguments(bundle);

        tabLayout = (TabLayout)findViewById(R.id.tab_layout_allsite);

        tabLayout.addTab(tabLayout.newTab().setCustomView(createTabView(getString(R.string.tab_official_text))));
        tabLayout.addTab(tabLayout.newTab().setCustomView(createTabView(getString(R.string.tab_organization_text))));
        tabLayout.addTab(tabLayout.newTab().setCustomView(createTabView(getString(R.string.tab_circle_text))));


        fmViewPager = (ViewPager)findViewById(R.id.allsite_switch_layout);
        fmViewPager.setAdapter(new PagerAdapter(getSupportFragmentManager()));
        fmViewPager.setOffscreenPageLimit(2);

        beforeButton = (ImageButton)findViewById(R.id.allsite_back_button);

        siteTutorialButton = (Button)findViewById(R.id.allsite_tutorial);
        popupView = getLayoutInflater().inflate(R.layout.site_tutorial_popup_window,null);
        siteTutorialPopupWindow = new PopupWindow(popupView, LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT, true);
        siteTutorialPopupWindow.setBackgroundDrawable(new ColorDrawable(Color.argb(76,220,35,20)));
        siteTutorialList = (RecyclerView)popupView.findViewById(R.id.recyclerview_site_tuorial);
        siteTutorialBackbutton = (ImageButton)popupView.findViewById(R.id.site_tutorial_back_button);
        tutorialListAdapter = new TutorialListAdapter(getApplicationContext());
        linearLayoutManager = new LinearLayoutManager(getApplicationContext());

    }


    public void setData(){

        itemList = new ArrayList<Site>();
        itemList.add(new Site("GIST home", R.drawable.home,"GIST 관련 모든 사이트가 시작되는 메인 사이트입니다."));
        itemList.add(new Site("Gel",R.drawable.gel,"강의 자료와 과제 공지가 올라오는 곳입니다."));
        itemList.add(new Site("Zeus system", R.drawable.zeus,"개설강좌, 성적, 각 강의 계획표, 등록금 납입 영수증등을 조회 할 수 있으며,  교과평가, 전공 선언,  조교 및 상담 신청,  인터넷 신청, 시설 예약 등을 할 수 있습니다.  "));
        itemList.add(new Site("수강 신청 사이트",R.drawable.courseregisteration,"수강 신청을 하는 사이트 입니다. 실제 수강 신청을 연습할 수 있는 모의 수강 신청도 할 수 있습니다."));
        itemList.add(new Site("Portal system",  R.drawable.portal,"개인을 위한 GIST 홈페이지라고 생각하면 됩니다. GIST에서 제공하는 여러 서비스(메일, 제우스, 식단, 내 강의시간표, 업무 연락처..)들을 한 화면에서 제공해줍니다. "));
        itemList.add(new Site("Email system",  R.drawable.email,"GIST 구성원들이 이용하는 메일 서비스입니다. 메일을 보내고 싶은 사람이 GIST 구성원일 경우, 이름만 기입해도 보내는 사람의 주소를 알 수 있어 편합니다."));
        itemList.add(new Site("GIST college",  R.drawable.college,"GIST 대학 홈페이지 입니다(대학원과 구별). (부)전공 과정, 졸업 요건, 학사 편람등의 정보를 볼 수 있으며, 학내공지(필수), 자료실(휴학원, 복학원, 취업정보) 등을 볼 수 있습니다."));
        itemList.add(new Site("GIST library",  R.drawable.library,"도서관 홈페이지. 도서 주문, 대여 예약, 대여기간 갱신 등의 도서관 업무가 가능합니다. 이외에도 모의토익, 모의토플, 전자책 등의 서비스가 유용합니다."));
        itemList.add(new Site("학내공지",  R.drawable.haknaegongji,"GIST와 관련된 모든 공지사항이 올라오는 게시판입니다(항상 들어가봐야 합니다!). 장학금 지원, 해외 봉사 프로그램등 이외에도 재학생이 해택을 많이 받을 수 있는 정보들이 항상 올라옵니다."));
        itemList.add(new Site("GIST 대학생",R.drawable.giststudent,"GIST 학부생들간의 소통을 할 수 있는 공식 페이스북 페이지입니다."));
        itemList.add(new Site("GIST 대나무숲",R.drawable.gistdaenamoo,"GIST와 관련된 이야기들이 올라오는 대나무숲 페이지입니다. 신입생 캠프 기간, 시험 기간, 축제 때 재미있는 이야기가 많이 올라옵니다. (학기 중의 유일한 낙..)"));
        itemList.add(new Site("GIST 대나무숲 제보함",R.drawable.gistdaenamoojaeboo,"대나무숲에 제보할 때 보통 쓰지만, 과거의 대나무 숲 글을 검색할 때도 유용합니다."));
        itemList.add(new Site("언어교육센터",R.drawable.language,"영어 관련 도움이나 공부 지도를 받을 수 있는 영어 클리닉 수업을 신청할 떄 사용합니다.(영어 클리닉 수업 때는 외국인 교수님과 학생들이 자유롭게 대화를 하거나 영어 기사등을 읽기도 합니다.)"));
        itemList.add(new Site("창업진흥센터", R.drawable.changup,"GIST 내 창업 관련 뉴스가 올라옵니다. GIST는 창업프로그램을 정말 전폭적으로 지원합니다. 창업에 관심 있는 사람이라면 많이 들릴 센터입니다."));


        itemList.add(new Site("학과별 사이트",R.drawable.majorset,"GIST에서 연구하는 분야별 사이트 모음입니다."));
        itemList.add(new Site("GIST 총학생회", R.drawable.gistunion,"GIST 학생들의 소리를 대변하는 총학생회입니다. 사무국, 대내협력국, 대외협력국, 소통국, 학술국으로 이루어져 있습니다."));
        itemList.add(new Site("GIST 동아리연합회", R.drawable.clubnight,"동아리들의 활동을 지원하고 동아리와 관련된 행사를 주관하는 자치기구입니다. 동아리를 창설하고 싶다면 동아리 연합회의 심의를 통과해야 합니다."));
        itemList.add(new Site("GIST 하우스", R.drawable.gisthouse,"GIST 기숙사를 이루는 G,I,S,T 하우스를 관리하는 학생 자치 기구입니다. 하우스 구성원 간 행사를 통한 친목 도모 및 생활 규칙에 근거해 전반적인 기숙사 관리를 합니다."));
        itemList.add(new Site("GIST 문화행사위원회",  R.drawable.moonhangwe,"GIST 축제, 체육대회를 비롯해 학교에서 진행되는 많은 행사들을 기획하고 진행하는 학생 자치기구입니다. "));
        itemList.add(new Site("GIST 신문", R.drawable.gistnews,"지스트에서 일어나는 모든 일들을 대상으로 기사를 쓰며 정기적으로 신문을 출판하는 신문사입니다. 디자이너, 기자, 편집장, 부편집장으로 구성되어 있습니다."));
        itemList.add(new Site("GIST 홍보대사", R.drawable.gionnare,"GIST를 원내, 원외로 활발히 알리는 대외협력국 소속의 학생 홍보단체입니다. 정기소식지, SNS 콘텐츠, 오프라인 행사, 캠퍼스 투어등 많은 홍보 활동을 진행합니다. "));


        itemList.add(new Site("춤 동아리 막무가내", R.drawable.mackmooganae,"춤 경험이 많은 사람부터 처음 춤을 추는 사람까지 다양한 구성원으로 이루어진 춤 동아리이다. 정기공연, 축제공연을 비롯하여 타 과기원에 초청 공연도 한다. "));
        itemList.add(new Site("힙합 동아리 Ignition", R.drawable.ignition,"보컬, 프로듀서, 래퍼들로 구성되어 있는 GIST 힙합 동아리 입니다. GIST뿐만 아니라 타 과기원, 광주의 각종 지역 축제에서도 공연을 합니다. 동아리 방에는 녹음시설이 갖추어져 있어 곡 녹음도 할 수 있습니다."));
        itemList.add(new Site("노래 동아리 싱송생송", R.drawable.singsongsangsong,"발라드, 락, 랩 등 다양한 장르의 노래를 공연하는 GIST 보컬 동아리입니다. 30만원 상당의 음향장비를 갖추고 있어 자신의 노래 녹음 작업을 할 수 있습니다."));
        itemList.add(new Site("연극 동아리 지대로", R.drawable.gidaero,"지스트 대학로의 줄임말로 대학로의 심장인 연극동아리입니다. 연극뿐만 아니라 다양한 문화생활을 좋아하는 학생들이 있습니다. 연극 뿐만 아니라 뮤지컬도 공연합니다."));
        itemList.add(new Site("오케스트라 동아리 악동", R.drawable.orchestra,"GIST대학의 오케스트라 동아리입니다. 클래식 곡뿐 아니라 OST, 팝송, 뉴에이지 음악 등등 여러 장르의 곡을 연주합니다. 1년에 2번의 정기공연과 2~3번의 소공연을 하고 있습니다."));
        itemList.add(new Site("기타 동아리 HOTSIX", R.drawable.hotsix,"활발하고 주기적인 멘토-멘티 활동을 통해 기타를 배울 수 있고,  다 같이 공연 준비하고 친목도 다질 수 있는 GIST의 기타 동아리입니다."));
        itemList.add(new Site("피아노 동아리 GISRI", R.drawable.gisri,"피아노를 좋아하는 사람들이 모인 동아리입니다. 함께 모여 연습하고 피드백을 주고 받는 시간을 주로 가지며 그랜드 피아노에서 공연할 수 있는 기회도 주어집니다."));
        itemList.add(new Site("밴드 Main", R.drawable.main,"GIST를 대표하는 실력파 밴드로 보컬을 제외한 모든 세션을 소화하실 수 있는 분, 음악을 듣고 악보로 따낼 수 있으신 분등 수많은 음악적 인재들이 왔다 가셨다고 합니다."));
        itemList.add(new Site("밴드 휴강익스프레스",R.drawable.hugangexpress,"GIST의 탄생과 동시에 시작한 밴드입니다. 가장 오래된 밴드답게 많은 선배들과 함께 정기공연, 버스킹, 야유회, 정기합주, 회식등 여러 활동을 진행합니다."));
        itemList.add(new Site("밴드 도도한 쭈쭈바", R.drawable.dozzu,"악기를 배워 본적 없는 초급자들도 밴드를 할 수 있다는 것을 보여주기 위해 만들어진 밴드이다. 단순 공연 뿐만 아니라, MT, 굿즈 제작등 구성원간의 친밀도를 높이는 활동도 합니다. "));
        itemList.add(new Site("영화 동아리 Cinergy", R.drawable.cinergy,"1년에 자체제작 영화 한 편 완성을 목표로 하고 있는 영화 제작 동아리입니다. 장편 영화 말고도 여러 액션씬 공모전등과 같은 내부 활동과 팀별 단편 영화 제작등의 활동도 합니다."));
        itemList.add(new Site("영상편집 동아리 The GIST", R.drawable.thegist,"영상제작을 주로 하는 종합예술창작 엔터테인먼트 동아리입니다. UCC, 홍보영상, 뮤직비디오, 합성 사진 제작, 이글루 제작등 하고 싶은 모든 활동을 할 수 있습니다. 동아리 공식 유튜브 채널도 있습니다. "));
        itemList.add(new Site("문예창작 동아리 사각사각", R.drawable.sagaksagak,"문예 창작 동아리로 글/그림 및 기타 창작활동을 하는 동아리입니다. 시문, 그림 스터디등을 구성하여 모임을 가지거나 1~2개월에 걸쳐 개인당 하나의 작품을 완성하여 자체 전시회에 게시하기도 합니다."));
        itemList.add(new Site("GIST 고양이 지냥이", R.drawable.giscat,"GIST의 대표 명물! 학부 기숙사 앞 뒤쪽에 만들어진 보금자리에서 살고 있는 고양이들을 보살펴주는 사람들의 모임입니다."));
        itemList.add(new Site("요리동아리 이쑤시개", R.drawable.essosigae,"요리를 좋아하는 사람들이 모인 동아리입니다. GIST 학부 기숙사 A동 1층에 있는 조리실에서 요리를 하며, 팀별로 요리를 하기도 하며 요리한 음식을 나눔하기도 합니다."));
        itemList.add(new Site("칵테일 동아리 MixoloGIST", R.drawable.mixologist,"칵테일 동아리로 이주에 한 번씩 자체적인 칵테일바를 운영하며, 다른 동아리(Bgm, 스페이스 바, 이그니션 등등) 칵테일바를 운영하기도 합니다. 또 동아리 팀원들과 외부 칵테일바를 방문하기도 합니다. "));
        itemList.add(new Site("보드게임 동아리 BGM", R.drawable.bgm,"보드게임을 좋아하고 즐기는 학생들로 구성된 동아리입니다. 많은 보드게임들이 구비되어 있습니다.  정기적으로 모여 보드게임을 하거나 보드 게임 대여, 리뷰 작성, 동아리 연합행사를 합니다."));
        itemList.add(new Site("성소수자 모임 speQtrum", R.drawable.speqtrum,"GIST 성소수자 모임입니다. 광주지역의 성소수자 모임 폴라리스로 확대 개편되었습니다."));
        itemList.add(new Site("만화 동아리 erutlucbus", R.drawable.eru,"만화, 애니메이션, 소설, 게임, 철도, 밀리터리 등의 서브 컬처를 즐기는 동아리입니다. 다른 동아리와 달리 컴퓨터, 수많은 만화책, 소설등 놀거리가 구비되어 있습니다."));
        itemList.add(new Site("천체관측 동아리 SpaceBar", R.drawable.spacebar,"별과 함께하는 것을 좋아하는 사람들이 모인 천체관측 동아리입니다. 천체망원경 3대와 천체사진을 찍을 카메라가 한 대 있습니다. 정기적인 관측활동을 진행합니다."));
        itemList.add(new Site("전산 동아리 WING", R.drawable.wing,"전산계열 학술 동아리로 학교에서 다루지 않는 분야에 대한 경험 및 웹과 모바일 서비스 출시를 목표로 하고 있습니다. 추가로 스터디, 수업, 포트폴리오등에 대한 활동도 진행합니다."));
        itemList.add(new Site("환경 동아리 온새미로", R.drawable.onsaemiro,"지구환경공학 트랙에서 창설된 환경동아리입닌다. 환경과 관련된 다양한 캠페인, 봉사활동 등을 진행합니다. 물의날, 식목일 등 환경 기념일 캠페인도 진행합니다."));
        itemList.add(new Site("축구 동아리 Kickass", R.drawable.kickass,"과기원 축구 교류전 준우승, 원내 체육대회 우승 등 화려한 수상 경력을 가지고 있는 축구 동아리입니다. GIST의 자랑 거리 시설중 하나인 FIFA 규격의 축구장에서 매주 활동합니다."));
        itemList.add(new Site("자유 사상 동아리 Freethinkers", R.drawable.freethinkers,"대학 연합 자유사상 동아리입니다. GIST, 서울대, KAIST 등 여러 대학교에서 함께 활동하고 있으며 다양한 사회적, 철학적 주제들로 이야기를 나눕니다."));
        itemList.add(new Site("스피치 동아리 Toastmasters", R.drawable.toastmasters,"리더쉽과 소통능력향상을 위한 국제 비영리 단체 Toastmasters의 GIST 지부입니다. 매주 자유롭게 미팅에 참여하여 발표하고 피드백을 받습니다. 클럽 스피치 콘테스트에도 참여합니다."));

        //학과별 사이트 Array
        /*major_set = new ArrayList<Site>();

        major_set.add(new Site("전기전자컴퓨터공학부", "https://eecs.gist.ac.kr/"));
        major_set.add(new Site("신소재공학부", "https://mse.gist.ac.kr"));
        major_set.add(new Site("기계공학부", "https://me.gist.ac.kr"));
        major_set.add(new Site("지구·환경공학부", "https://env1.gist.ac.kr"));
        major_set.add(new Site("생명과학부", "https://life1.gist.ac.kr"));
        major_set.add(new Site("물리·광과학과", "https://phys.gist.ac.kr/"));
        major_set.add(new Site("화학과", "https://chem.gist.ac.kr"));
        major_set.add(new Site("융합기술원", "https://iit.gist.ac.kr/"));
        major_set.add(new Site("나노바이오재료전자공학과", "https://wcu.gist.ac.kr/"));*/
    }


}
