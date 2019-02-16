package textview.html.sty.com.htmltextviewdemo;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnTextView;
    private Button btnWebView;
    private TextView tvContent;
    private WebView wvContent;

    private static final String richText =
            "<html><head><title>TextView也会解析Title的内容</title></head><body>"
                    + "<p style=\"color: red;\">TextView支持的标签:</p>"
                    + "<p><strong>强调</strong>&nbsp;&nbsp;&nbsp;&nbsp;<em>斜体</em>&nbsp;&nbsp;&nbsp;&nbsp;"
                    + "正常字体<sup>上标</sup><sub>下标</sub>&nbsp;&nbsp;&nbsp;&nbsp;<u>带有下划线字体</u></p>"
                    + "<p><a href=\"http://www.baidu.com/xhtml/\">超链接HTML入门</a>学习HTML! 学习HTML! 学习HTML! 学习HTML!</p>"
                    + "<h1>标题1</h1><h6>标题6</h6>"
                    + "<p><font color=\"#00bbaa\">颜色1</font>&nbsp;&nbsp;&nbsp;&nbsp;<font color=\"#aabb00\">颜色2</font></p>"
                    + "<p style=\"color: red;\">TextView不支持的标签样式:</p>"
                    + "<p><font color=\"rgb(255, 0, 0)\">颜色rgb的差异</font>&nbsp;&nbsp;"
                    + "<font size=\"1\">font-size的差异</font>&nbsp;&nbsp;"
                    + "<font face=\"Times New Roman, verdana, arial,sans-serif, SimKai\">font-family的差异</font></p>"
                    + "<p><img src=\"http://avatar.csdn.net/0/3/8/2_zhang957411207.jpg\"/></p>"
                    + "<p><img src=\"http://test.img.huaguosun.com/images/MarketingCampaign/1545792379256.jpg\"/></p>"
                    + "<p><img src=\"http://test.img.huaguosun.com/images/MarketingCampaign/1545792379256.jpg\"/></p>"
                    + "<p><img src=\"http://test.img.huaguosun.com/images/MarketingCampaign/1545792379256.jpg\"/></p>"
                    + "<p><img src=\"http://pic36.photophoto.cn/20150708/0012025199649765_b.jpg\"/></p>"
                    + "<p><img src=\"http://test.img.huaguosun.com/images/MarketingCampaign/1545792379256.jpg\"/></p>"
                    + "<p><img src=\"http://pic41.nipic.com/20140519/18505720_094740582159_2.jpg\"/></p>"
                    + "<p><img src=\"http://pic6.photophoto.cn/20080311/0034034854076462_b.jpg\"/></p>"
                    + "<p><img src=\"http://avatar.csdn.net/0/3/8/2_zhang957411207.jpg\"/></p>"
                    + "<p><img src=\"http://test.img.huaguosun.com/images/MarketingCampaign/1545792379262.jpg\"/></p></body></html>";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initTextView();
    }

    private void initView(){
        btnTextView = findViewById(R.id.btn_text_view);
        btnWebView = findViewById(R.id.btn_web_view);
        tvContent = findViewById(R.id.tv_content);
        wvContent = findViewById(R.id.wv_content);
        btnTextView.setOnClickListener(this);
        btnWebView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_text_view:
                initTextView();
                break;
            case R.id.btn_web_view:
                initWebView();
                break;
            default:
                break;
        }
    }

    /******************************* TextView 加载 HTML ***********************************/

    private void initTextView() {
        //tvContent.setMovementMethod(ScrollingMovementMethod.getInstance());  //可滚动
        //超链接可点击-->LinkMovementMethod继承自ScrollingMovementMethod,使用该属性时变具有可滚动属性(这种滑动不流畅,采用ScrollView嵌套)
        //tvContent.setMovementMethod(LinkMovementMethod.getInstance());

        URLImageParser imageGetter = new URLImageParser(tvContent);
        //注意添加Internet权限
        final Spanned sp = Html.fromHtml(richText, imageGetter, null);
        tvContent.setText(sp);


        tvContent.setVisibility(View.VISIBLE);
        wvContent.setVisibility(View.GONE);
    }

    private class URLDrawable extends BitmapDrawable {
        protected Bitmap bitmap;

        @Override
        public void draw(Canvas canvas) {
            if(bitmap != null) {
                canvas.drawBitmap(bitmap, 0, 0, getPaint());
            }
        }
    }

    //注意添加Internet权限
    private class URLImageParser implements Html.ImageGetter {
        TextView textView;

        public URLImageParser(TextView textView) {
            this.textView = textView;
        }

        @Override
        public Drawable getDrawable(String source) {
            final URLDrawable urlDrawable = new URLDrawable();

            Glide.with(MainActivity.this).load(source).asBitmap().fitCenter().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    urlDrawable.bitmap = resource;
                    urlDrawable.setBounds(0, 0, resource.getWidth(), resource.getHeight());
                    //Log.i("sty", "->" + Thread.currentThread().getName());

                    textView.invalidate();
                    textView.setText(textView.getText());
                }
            });
            return urlDrawable;
        }
    }

    /******************************* WebView 加载 HTML ***********************************/

    private void initWebView() {
        wvContent.getSettings().setJavaScriptEnabled(true);
        wvContent.loadDataWithBaseURL(null, changeImageWidth(richText), "text/html", "utf-8", null);
        wvContent.setHorizontalScrollBarEnabled(false); //隐藏水平滚动条
        wvContent.setVerticalScrollBarEnabled(false); //隐藏竖直滚动条

        wvContent.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(url);
                return true;
            }
        });

        tvContent.setVisibility(View.GONE);
        wvContent.setVisibility(View.VISIBLE);
    }

    /**
     * 借用Jsoup来修改图片的宽度为100%自适应,防止图片过大造成WebView横向滚动的情况
     * @param htmlText
     * @return
     */
    private String changeImageWidth(String htmlText) {
        Document document = Jsoup.parse(htmlText);
        Elements elementImages = document.getElementsByTag("img");
        if(elementImages.size() > 0) {
            for(Element elementImage : elementImages) {
                elementImage.attr("style", "width: 100%");
            }
        }
        return document.toString();
    }

}
