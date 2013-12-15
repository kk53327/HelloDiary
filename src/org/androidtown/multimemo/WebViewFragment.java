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
		
		/*�� ���� ��Ʈ���� �̿��ؼ� �� ��ư ��Ʈ����
		 * ȭ�鿡 ���̵��� �մϴ�. */
		webs = web.getSettings();
		//webs.setBuiltInZoomControls(true);
		
		
		// inputUrl�� ����� url �ּҸ� �ε���Ŵ. 
		// �⺻�� 'http://www.naver.com'�� �̵�
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
				
				// ����Ʈ �ؽ�Ʈ�� �ԷµǾ��� ���� �о�� ���ڿ��� ��ȯ �� 
				// httpInputCheck() �޼ҵ带 ���� ��ȯ�� ���� inputUrl�� ����.
				inputUrl = httpInputCheck(et.getText().toString());
				if(inputUrl == null) break;
				// inputUrl�� ����� url �ּҸ� �ε���Ŵ. 
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
		
		// ���� ��ư Ŭ���� ���α׷����� ���� ǥ�ø� ���� �߰��Ͽ����ϴ�.
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
