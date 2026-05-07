package com.example.helloworld_java.ui.activity;

import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import com.example.helloworld_java.R;

public class WebViewActivity extends AppCompatActivity {
   @Override
   protected void onCreate(Bundle savedInstanceState){
      super.onCreate(savedInstanceState);
      setContentView(R.layout.web_view);
      WebView webView=findViewById(R.id.web_view);
      webView.getSettings().setJavaScriptEnabled(true);
      webView.setWebViewClient(new WebViewClient());
      webView.loadUrl("https://www.baidu.com");
   }

}
