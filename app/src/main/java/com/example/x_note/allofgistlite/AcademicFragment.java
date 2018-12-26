package com.example.x_note.allofgistlite;

import android.content.Context;
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
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class AcademicFragment extends Fragment {
    private int mPageNumber;
    // get currentMonth
    private Calendar today = Calendar.getInstance();
    int month = today.get(Calendar.MONTH)+1;
    private HashMap<Integer,ArrayList<CalendarContext>> calendarData;
    ListView context;


    public static AcademicFragment create(int pageNumber) {
        AcademicFragment fragment = new AcademicFragment();
        Bundle args = new Bundle();
        args.putInt("page", pageNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPageNumber = getArguments().getInt("page");
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.page_academic_list, container, false);

        MyAsyncTask setdata = new MyAsyncTask();
        context = (ListView) rootView.findViewById(R.id.calendar_context);


        new MyAsyncTask().execute();

        //calendarData = setdata.doInBackground();
        //AcademicAdapter academicAdapter = new AcademicAdapter(getContext(),calendarData.get((mPageNumber+month)-1));
        //context.setAdapter(academicAdapter);
        return rootView;
    }



    private class AcademicAdapter extends ArrayAdapter<CalendarContext> {
        private ArrayList<CalendarContext> calendarContext;
        private ArrayList<String> rawData;
        private HashMap<Integer,ArrayList<String>> data;

        public AcademicAdapter(Context context, ArrayList<CalendarContext> calendarContext){
            super(context,0, calendarContext);
            this.calendarContext = calendarContext;
        }

        @Override
        public int getCount() {
            return calendarContext.size();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View view = convertView;
            calendarHolder holder;

            if(view == null){
                LayoutInflater layoutInflater = (LayoutInflater)getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = layoutInflater.inflate(R.layout.context_academic_list,null);

                holder = new calendarHolder(view);

                view.setTag(holder);
            }
            else
                holder = (calendarHolder)view.getTag();
            if(calendarContext.get(position).getDate_title().contains("~")){
                holder.dateView.setText(calendarContext.get(position).getDate_title().replace(" ~ ","\n~\n"));
            }
            else
                holder.dateView.setText(calendarContext.get(position).getDate_title());
            holder.contextView.setText(calendarContext.get(position).getContext());
            return view;
        }
    }




    public class calendarHolder{
        private TextView dateView;
        private TextView contextView;

        public calendarHolder(View view){
            dateView = (TextView) view.findViewById(R.id.dateView);
            contextView = (TextView) view.findViewById(R.id.contextView);
        }
    }


    //크롤링 데이터 수집
    public class MyAsyncTask extends AsyncTask<Integer,Void,HashMap<Integer,ArrayList<CalendarContext>>> {

        @Override
        protected HashMap<Integer, ArrayList<CalendarContext>> doInBackground(Integer... params) {
            String Address;
            URL url;
            BufferedReader br;
            HttpURLConnection conn;
            String protocol = "GET";
            HashMap<Integer,ArrayList<CalendarContext>> data = new HashMap<Integer,ArrayList<CalendarContext>>();
            try {
                Address = "https://www.gist.ac.kr/kr/html/sub05/0501.html";
                url = new URL(Address);
                conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod(protocol);
                br = new BufferedReader(new InputStreamReader(conn.getInputStream(),"UTF-8"));

                ArrayList<String> rawData = new ArrayList<String>();
                String line;
                while((line = br.readLine())!=null) {
                    if(line.startsWith("					<li><b>")) {
                        line = line.replace("					 					 ", "");
                        line = line.replace("					","");
                        line = line.replaceAll("</b><span>","Y");
                        line = line.replaceAll("</span></li><li><b>","Z"+"X");
                        line = line.replaceAll("<li><b>","X");
                        line = line.replaceAll("</span></li>","ZK");
                        rawData.add(line);
                        //System.out.print(rawData);
                    }
                    //System.out.println(line);
                }
///*
                int rawDataSize = rawData.size();
                ArrayList<String> Data = new ArrayList<String>();
                for(int i=0;i<rawDataSize;i++) {
                    int rawDatapositionSize = rawData.get(i).length();
                    String replaceData;
                    for(int j=0;j<rawDatapositionSize;j++) {
                        if(rawData.get(i).startsWith("X")) {
                            Data.add(rawData.get(i).substring(rawData.get(i).indexOf("X")+1, rawData.get(i).indexOf("Y")));

                            replaceData = Data.get(Data.size()-1).replace("(", "\\(");
                            replaceData = replaceData.replace(")", "\\)");

                            rawData.set(i, rawData.get(i).replaceFirst("X"+replaceData, ""));
                        }
                        else if(rawData.get(i).startsWith("Y")) {
                            Data.add(rawData.get(i).substring(rawData.get(i).indexOf("Y")+1, rawData.get(i).indexOf("Z")));

                            replaceData = Data.get(Data.size()-1).replace("(", "\\(");
                            replaceData = replaceData.replace(")", "\\)");

                            rawData.set(i, rawData.get(i).replaceFirst("Y"+replaceData+"Z", ""));
                        }
                        else if(rawData.get(i).startsWith("K")) {
                            Data.add("MonthChange");
                            break;
                        }
                    }
                }

                Log.v("AcademicFragment","data(hashmap)"+rawData);

                int num = 0;
                CalendarContext savedcontext = new CalendarContext(null,null);
                for(int i=0;i<rawDataSize;i++) {

                    data.put(i, new ArrayList<CalendarContext>());

                    while (Data.get(num) != null) {
                        if (Data.get(num) == "MonthChange") {
                            num++;
                            break;
                        }
                        savedcontext.setDate_title(Data.get(num));
                        num++;
                        savedcontext.setContext(Data.get(num));
                        if (savedcontext.getContext() != null && savedcontext.getDate_title() != null) {
                            data.get(i).add(new CalendarContext(savedcontext.getDate_title(),savedcontext.getContext()));
                        }
                        num++;
                    }
                }
            }catch (IOException e) {
                e.printStackTrace();
            }

            return data;
        }

        @Override
        protected void onPostExecute(HashMap<Integer, ArrayList<CalendarContext>> integerArrayListHashMap) {
            AcademicAdapter academicAdapter = new AcademicAdapter(getContext(),integerArrayListHashMap.get(mPageNumber+month-1));
            context.setAdapter(academicAdapter);


            /*  리스트뷰 차일드 수에 맞게 height 조절*/
            if (academicAdapter == null) {
                // pre-condition
                return;
            }

            int totalHeight = 0;
            int addlittle = 10000;        //원래 이 코드에서 빠져야 하는 부분
            //for (int i = 0; i < academicAdapter.getCount(); i++) {
            for (int i = 0; i <integerArrayListHashMap.get(mPageNumber+month-1).size(); i++) {
                View listItem = academicAdapter.getView(i, null, context);
                listItem.measure(0, 0);
                if(addlittle>listItem.getMeasuredHeight())     //원래 이 코드에서 빠져야 하는 부분
                    addlittle = listItem.getMeasuredHeight();   //원래 이 코드에서 빠져야 하는 부분
                totalHeight += listItem.getMeasuredHeight();
            }
            totalHeight += (addlittle*2);  //원래 이 코드에서 빠져야 하는 부분(본 코드가 litview를 완전히 fit하지 못해서 자체적으로 붙임)  -> calendar item의 각각에 marginBottom이 부여되서 그럼
            ViewGroup.LayoutParams params = context.getLayoutParams();
            //params.height = totalHeight + (context.getDividerHeight() * (academicAdapter.getCount() - 1));
            params.height = totalHeight + (context.getDividerHeight() * (integerArrayListHashMap.get(mPageNumber+month-1).size() - 1));
            context.setLayoutParams(params);

        }
    }
}
