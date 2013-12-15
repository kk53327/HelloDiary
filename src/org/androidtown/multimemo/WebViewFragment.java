package org.androidtown.multimemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

@SuppressLint({ "SetJavaScriptEnabled", "ValidFragment" })
public class WebViewFragment extends Fragment {
	
	private EditText et;
	private WebView web;
	private WebSettings webs;
	private String inputUrl = "http://plasticjoy119.appspot.com/";
	private ProgressBar proBar;
	private InputMethodManager inputMethodManager;
	private Context mContext;
	
	public WebViewFragment(Context context) {
		mContext = context;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.webview2, null);
//		view.findViewById(R.id.btn_go).setOnClickListener(onClickListener);
//		view.findViewById(R.id.btn_pre).setOnClickListener(onClickListener);
//		et = (EditText)view.findViewById(R.id.et);
		web = (WebView)view.findViewById(R.id.webview);
		proBar = (ProgressBar)view.findViewById(R.id.progressBar);
		inputMethodManager = (InputMethodManager)mContext.
							getSystemService(Context.INPUT_METHOD_SERVICE);
		
		web.setWebChromeClient(new webViewChrome());
		web.setWebViewClient(new webViewClient());
		
		web.getSettings().setUseWideViewPort(true);
		web.setInitialScale(95);
		
		/*웹 세팅 컨트롤을 이용해서 줌 버튼 컨트롤을
		 * 화면에 보이도록 합니다. */
		webs = web.getSettings();
		//webs.setBuiltInZoomControls(true);
		
		
		// inputUrl에 저장된 url 주소를 로딩시킴. 
		// 기본값 'http://www.naver.com'로 이동
		web.loadUrl(inputUrl);
//		et.setHint(inputUrl);
		return view;
	}
	/*
	View.OnClickListener onClickListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.btn_go:
				inputMethodManager.hideSoftInputFromWindow(et.getWindowToken(), 0);
				
				// 에디트 텍스트에 입력되어진 값을 읽어와 문자열로 변환 후 
				// httpInputCheck() 메소드를 통해 반환된 값을 inputUrl에 저장.
				inputUrl = httpInputCheck(et.getText().toString());
				if(inputUrl == null) break;
				// inputUrl에 저장된 url 주소를 로딩시킴. 
				web.loadUrl(inputUrl);
				et.setText("");
				et.setHint(inputUrl);
				break;
			case R.id.btn_pre:
				web.goBack();
				break;
			}
		}
	};*/
	
	class webViewChrome extends WebChromeClient {
		
		@Override
		public void onReceivedTitle(WebView view, String title) {
			// TODO Auto-generated method stub
			super.onReceivedTitle(view, title);
		}
		
		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			if(newProgress < 100) {
				proBar.setProgress(newProgress);
			} else {
				proBar.setVisibility(View.INVISIBLE);
				proBar.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
			}
		}
	}
	
	class webViewClient extends WebViewClient {
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			proBar.setVisibility(View.VISIBLE);
			proBar.setLayoutParams(new LinearLayout.LayoutParams
					(LinearLayout.LayoutParams.MATCH_PARENT, 15));
			view.loadUrl(url);
			return true;
		}
		
		// 이전 버튼 클릭시 프로그래스바 진행 표시를 위해 추가하였습니다.
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			proBar.setVisibility(View.VISIBLE);
			proBar.setLayoutParams(new LinearLayout.LayoutParams
					(LinearLayout.LayoutParams.MATCH_PARENT, 15));
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			webs.setJavaScriptEnabled(true);
//			et.setHint(url);
			super.onPageFinished(view, url);
		}
		
		@Override
		public void onScaleChanged(WebView view, float oldScale, float newScale)
		{
			// TODO Auto-generated method stub
			Log.v("WebViewMainFragmnet", "odlScale " + oldScale + 
					" newScale " + newScale);
			super.onScaleChanged(view, oldScale, newScale);
		}
	}
	
	private String httpInputCheck(String url) {
		if(url.isEmpty()) return null;
		
		if(url.indexOf("http://") == ("http://").length()) return url;
		else return "http://" + url;
	}
}
