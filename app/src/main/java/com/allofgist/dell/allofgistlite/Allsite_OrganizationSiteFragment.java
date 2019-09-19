package com.allofgist.dell.allofgistlite;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;

import static com.allofgist.dell.allofgistlite.Allsite_OfficialSiteFragment.recycleBitmap;
import static com.allofgist.dell.allofgistlite.MainActivity.getOpenFacebookIntent;
import static com.allofgist.dell.allofgistlite.OrganizationSiteFragment.setData;

public class Allsite_OrganizationSiteFragment extends Fragment {

    //SImple Image Resizer variables
    private final int REQUEST_WIDTH = 256;
    private final int REQUEST_HEIGHT = 256;

    //dataStorage
    private ArrayList<Site> itemList;

    //GridView 데이터 연결
    private GridView siteList;
    //private RecyclerView siteList;
    private Allsite_OfficialSiteFragment.SiteAdapter siteAdapter;

    private String id;
    //public RequestManager mGlideRequestManager;

    public Allsite_OrganizationSiteFragment() {
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
        siteAdapter = new Allsite_OfficialSiteFragment.SiteAdapter(getActivity(),itemList);
        siteList.setAdapter(siteAdapter);

        siteList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                final Site site = itemList.get(position);
                if (site.getMsite_name().equals( "GIST 신문")) {

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

        //RECYCLERVIEW MODE
        /*siteList = (RecyclerView) rootView.findViewById(R.id.allsites);
        siteAdapter = new Allsite_OfficialSiteFragment.SiteAdapter(itemList);
        siteList.setAdapter(siteAdapter);
        siteList.setLayoutManager(new LinearLayoutManager(getContext()));


        siteList.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), siteList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                final Site site = itemList.get(position);
                if (site.getMsite_name().equals( "GIST 신문")) {

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

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));
*/
        return rootView;
    }

    //사이트 접속
    public void openWebPage(String url) {
        if (url.contains("https://www.facebook.com")) {
            if (getOpenFacebookIntent(getContext(), url).contains("https://www.facebook.com")){
                /*String facebookurl = url.replaceFirst("www.", "m.");
                if(!facebookurl.startsWith("https"))
                    facebookurl = "https://"+facebookurl;*/
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
            Intent chrome = new Intent(Intent.ACTION_VIEW);
            chrome.setData(Uri.parse(url));
            startActivity(chrome);
        }
    }



    /*@Override
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
*/
    /*
    @Override
    public void onPause() {
        int count = siteList.getCount();
        for (int i =0; i<count; i++){
            try {
                ViewGroup viewGroup = (ViewGroup) siteList.getChildAt(i);
                int childSize = viewGroup.getChildCount();
                for (int j = 0; j < childSize; j++) {
                    if (viewGroup.getChildAt(j) instanceof ImageView) {
                        ((ImageView) viewGroup.getChildAt(j)).setImageBitmap(null);
                    }
                    else if (viewGroup.getChildAt(j) instanceof TextView) {
                        ((TextView) viewGroup.getChildAt(j)).setText(null);
                    }
                    else if (viewGroup.getChildAt(j) instanceof LinearLayout) {
                        ((LinearLayout) viewGroup.getChildAt(j)).setBackground(null);
                    }
                }
            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }

        itemList = null;
        super.onPause();
    }*/
}
