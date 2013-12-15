package org.androidtown.multimemo;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.StringTokenizer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.essence.chart.Chart;
import com.essence.chart.GridData;

@SuppressLint({ "SetJavaScriptEnabled", "ValidFragment" })

public class SearchFragment extends Fragment {

	private Context mContext;
	TextView textview;
	String received_data;
	
	private	Chart m_Chart = null;
	
	public SearchFragment(Context context) {
		mContext = context;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.webview, null);
		view.findViewById(R.id.btn_go).setOnClickListener(onClickListener);
		textview = (TextView)view.findViewById(R.id.textview);
		
		m_Chart = (Chart)view.findViewById(R.id.chart01);
		 

		 String[] strColumns = { " ", " ", "Column2", "Column3", "Column4", "Column5", "Column6", "Column7", "Column8" };
		 String[] strRows = { " ", "처음듣니", "대답좀해라", "실리콘벨리", "열정", "엄청난일", "커널", "창조경제" };
		 double[][] dValue = {
		 {1000,10},
		 {4000,7},
		 {6000,6},
		 {6000,5},
		 {6000,5},
		 {6000,3},
		 {6000,2},
		 {6000,1}
		 };
		 GridData gridData = new GridData(8,2);

		 for(int nRow = 0; nRow < 8; nRow++) {
		 for(int nColumn = 0; nColumn < 2; nColumn++) {
		 if (nRow == 0) {
		 gridData.setCell(nRow, nColumn, strColumns[nColumn]); // Column Title 
		 } else if (nColumn == 0) {
		 gridData.setCell(nRow, nColumn, strRows[nRow]); // Row Title --> 범례
		} else {
		 gridData.setCell(nRow, nColumn, dValue[nRow][nColumn]); // 실제 Data
		 }
		 }
		 }

		 m_Chart.setTitle("어휘 빈도수");
		 m_Chart.setLegendVisible(true);
		 m_Chart.setChartType(Chart.Chart_Type_3D_Column);
		 //m_Chart.setChartType(Chart.Chart_Type_Exploded_pie_in_3D);
		 m_Chart.setSourceData(gridData, 1);
		 
		 if(m_Chart.isAnimation() == true){
			 m_Chart.beginAnimation(true);
		 }
		 
		 if(m_Chart.getAnimationStatus() == 1){
			 m_Chart.beginAnimation(false);
		 }

		
		return view;
	}
	
	View.OnClickListener onClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.btn_go:
				/* server로부터 어휘 정보를 받아옴 */
				AnalyzeThread thread = new AnalyzeThread("server_addr");
				thread.start();
				try {
					thread.join();
					textview.setText(received_data);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					textview.setText("Err Cannot Connect");
					e.printStackTrace();
				}
				
				/* 받아온 문자열을 필요한 자료형에 맞게 자른다 */
				String[] term = new String[7];
				String[] freq_str = new String[7];
				int[] freq = new int[7];
				
				StringTokenizer st = new StringTokenizer(received_data);
				int idx = 0;
				while(st.hasMoreTokens()){
					term[idx] = st.nextToken();
					freq_str[idx] = st.nextToken();
					System.out.println(term[idx]);
					idx++;
				}
				
				for(int i=0; i<7; i++)
				{
					freq[i] = Integer.parseInt(freq_str[i]);
					System.out.println(freq[i]);
				}
				
				/* 그래프를 그린다 */
				 String[] strColumns = { " ", " "};
				 String[] strRows = new String[8];
				 
				 for(int i=1; i<8; i++)
				 {
					 strRows[i] = term[i-1];
				 }
				 
				 double[][] dValue = new double[8][2];
				 
				 for(int i=1; i<8; i++)
				 {
					 dValue[i][1] = freq[i-1];
				 }
				 
				 GridData gridData = new GridData(8,2);

				 for(int nRow = 0; nRow < 8; nRow++) {
				 for(int nColumn = 0; nColumn < 2; nColumn++) {
				 if (nRow == 0) {
				 gridData.setCell(nRow, nColumn, strColumns[nColumn]); // Column Title 
				 } else if (nColumn == 0) {
				 gridData.setCell(nRow, nColumn, strRows[nRow]); // Row Title --> 범례
				} else {
				 gridData.setCell(nRow, nColumn, dValue[nRow][nColumn]); // 실제 Data
				 }
				 }
				 }

				 m_Chart.setTitle("어휘 빈도수");
				 m_Chart.setLegendVisible(true);
				 m_Chart.setChartType(Chart.Chart_Type_3D_Column);
				 //m_Chart.setChartType(Chart.Chart_Type_Exploded_pie_in_3D);
				 m_Chart.setSourceData(gridData, 1);
				 
				 if(m_Chart.isAnimation() == true){
					 m_Chart.beginAnimation(true);
				 }
				 
				 if(m_Chart.getAnimationStatus() == 1){
					 m_Chart.beginAnimation(false);
				 }
				
				break;
			}
		}
	};
	
    class AnalyzeThread extends Thread {
    	String hostname;
    	
    	public AnalyzeThread(String addr) {
    		hostname = "192.168.0.190";
    	}
    	
    	public void run() {

    		try {
    			int port = 20001;
    			
    			Socket sock = new Socket(hostname, port);
 
    			BufferedOutputStream dos=new BufferedOutputStream(sock.getOutputStream()); //output stream
    			BufferedReader dis=new BufferedReader(new InputStreamReader(sock.getInputStream(),"EUC_KR")); //input stream.

    			String cmd = "analyze";
    			dos.write(cmd.getBytes("EUC_KR"));
    			dos.flush();

    			char[] in = new char[1000];
    			dis.read(in, 0, in.length);
    			String input = new String(in, 0, in.length);  // byte형을 string으로 바꿔 초기화
    			received_data = input.trim();
    			
    			Log.d("MainActivity", "서버에서 받은 메시지 : " + received_data);
    			
    			sock.close();

    		} catch(Exception ex) {
    			/* Fail Tag Creation */
    			Log.d("쓰레드 생성 실패", " ");
    			received_data = " ";
    			ex.printStackTrace();
    		}

    	}
    }

	
}
