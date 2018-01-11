package com.webedit;

import android.content.Context;
import android.media.Image;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.InputFilter;
import android.text.Spanned;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.webeditproject.R;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by lw on 2017/11/30.
 */

public class HWebEditView extends LinearLayout implements View.OnClickListener ,
        HyperlinksFragment.OnClickSubject{


    private boolean InputFlag = false;

    private String HWebTextColor = FontColor.KEY_COLOR_BLACK;
    private String HWebTextGravity = "left";
    private ArrayList<Integer> HWebTextEnum = new ArrayList<>();
    private int HWebTextSize = 14;
    private int hyperlinksLength = 0;

    private ArrayList<HtmlTextBean> addList = new ArrayList<>();
    private ArrayList<HtmlTextBean> HWebHtmlList = new ArrayList<>();
    private ArrayList<HtmlTextBean> removeList = new ArrayList<>();

    private Context context;

    private View contentView;
    private ImageView close;
    private ImageView subject;

    private OnEnumClickListener onEnumClickListener;

    public void setOnEnumClickListener(OnEnumClickListener onEnumClickListener) {
        this.onEnumClickListener = onEnumClickListener;
    }

    private InputFilter inputFilter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence charSequence, int i, int i1, Spanned spanned, int i2, int i3) {
            if (InputFlag) {
                InputFlag = !InputFlag;
                return charSequence;
            } else {
                retrieveSetText(charSequence, i3);
                int end = HTMLText.getSelectionEnd();
                InputFlag = !InputFlag;
                setEditText(createHtml());
                setSelection(charSequence, end);     //shezhiguangbiao
                return null;
            }
        }
    };

    private EditText HTMLText;

    private HyperlinksFragment hyperlinksFragment;

    private ImageView mFontColorBlack;
    private ImageView mFontColorBlackgray;
    private ImageView mFontColorBlue;
    private ImageView mFontColorGray;
    private ImageView mFontColorGreen;
    private ImageView mFontColorRed;
    private ImageView mFontColorViolet;
    private ImageView mFontColorWhite;
    private ImageView mFontColorYellow;

    private ImageView mFontPositionCenter;
    private ImageView mFontPositionLeft;
    private ImageView mFontPositionRight;

    private ImageView mFontSizeAdd;
    private ImageView mFontSizeNormal;
    private ImageView mFontSizeSub;

    private ImageView mFontStyleBold;
    private ImageView mFontStyleItalics;
    private ImageView mFontStyleUnderline;

    private ImageView mFontColor;
    private ImageView mFontPosition;
    private ImageView mFontSize;
    private ImageView mFontStyle;
    private ImageView mHyperlinks;

    private HorizontalScrollView tTextColor;
    private LinearLayout tTextPosition;
    private LinearLayout tTextSize;
    private LinearLayout tTextStyle;

    public HWebEditView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public HWebEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        contentView = LayoutInflater.from(this.context).inflate(R.layout.view_html_edittext, this);
        findView();
    }

    private void findView() {
        HTMLText = (EditText) findViewById(R.id.view_html_edit);
        HTMLText.setFilters(new InputFilter[]{inputFilter, new InputFilter.LengthFilter(6000)});

        close = (ImageView) findViewById(R.id.view_html_bottom_back);
        close.setOnClickListener(this);
        mHyperlinks = (ImageView) findViewById(R.id.view_html_bottom_hyperlink);
        mHyperlinks.setOnClickListener(this);
        mFontStyle = (ImageView) findViewById(R.id.view_html_bottom_style);
        mFontStyle.setOnClickListener(this);
        mFontSize = (ImageView) findViewById(R.id.view_html_bottom_size);
        mFontSize.setOnClickListener(this);
        mFontPosition = (ImageView) findViewById(R.id.view_html_bottom_position);
        mFontPosition.setOnClickListener(this);
        mFontColor = (ImageView) findViewById(R.id.view_html_bottom_color);
        mFontColor.setOnClickListener(this);
        subject = (ImageView) findViewById(R.id.view_html_bottom_subject);
        subject.setOnClickListener(this);

        tTextStyle = (LinearLayout) findViewById(R.id.view_html_bottom_font_style);
        mFontStyleBold = (ImageView) findViewById(R.id.view_html_bottom_font_style_bold);
        mFontStyleBold.setOnClickListener(this);
        mFontStyleItalics = (ImageView) findViewById(R.id.view_html_bottom_font_style_italics);
        mFontStyleItalics.setOnClickListener(this);
        mFontStyleUnderline = (ImageView) findViewById(R.id.view_html_bottom_font_style_underline);
        mFontStyleUnderline.setOnClickListener(this);

        tTextSize = (LinearLayout) findViewById(R.id.view_html_bottom_font_size);
        mFontSizeAdd = (ImageView) findViewById(R.id.view_html_bottom_font_size_add);
        mFontSizeAdd.setOnClickListener(this);
        mFontSizeNormal = (ImageView) findViewById(R.id.view_html_bottom_font_size_default);
        mFontSizeNormal.setOnClickListener(this);
        mFontSizeSub = (ImageView) findViewById(R.id.view_html_bottom_font_size_sub);
        mFontSizeSub.setOnClickListener(this);

        tTextPosition = (LinearLayout) findViewById(R.id.view_html_bottom_font_layout);
        mFontPositionLeft = (ImageView) findViewById(R.id.view_html_bottom_font_layout_left);
        mFontPositionLeft.setOnClickListener(this);
        mFontPositionCenter = (ImageView) findViewById(R.id.view_html_bottom_font_layout_center);
        mFontPositionCenter.setOnClickListener(this);
        mFontPositionRight = (ImageView) findViewById(R.id.view_html_bottom_font_layout_right);
        mFontPositionRight.setOnClickListener(this);

        tTextColor = (HorizontalScrollView) findViewById(R.id.view_html_bottom_font_color);
        mFontColorBlack = (ImageView) findViewById(R.id.view_html_bottom_font_color_black);
        mFontColorBlack.setOnClickListener(this);
        mFontColorBlackgray = (ImageView) findViewById(R.id.view_html_bottom_font_color_black_gray);
        mFontColorBlackgray.setOnClickListener(this);
        mFontColorBlue = (ImageView) findViewById(R.id.view_html_bottom_font_color_blue);
        mFontColorBlue.setOnClickListener(this);
        mFontColorGray = (ImageView) findViewById(R.id.view_html_bottom_font_color_gray);
        mFontColorGray.setOnClickListener(this);
        mFontColorGreen = (ImageView) findViewById(R.id.view_html_bottom_font_color_green);
        mFontColorGreen.setOnClickListener(this);
        mFontColorRed = (ImageView) findViewById(R.id.view_html_bottom_font_color_red);
        mFontColorRed.setOnClickListener(this);
        mFontColorViolet = (ImageView) findViewById(R.id.view_html_bottom_font_color_violet);
        mFontColorViolet.setOnClickListener(this);
        mFontColorWhite = (ImageView) findViewById(R.id.view_html_bottom_font_color_white);
        mFontColorWhite.setOnClickListener(this);
        mFontColorYellow = (ImageView) findViewById(R.id.view_html_bottom_font_color_yellow);
        mFontColorYellow.setOnClickListener(this);
    }

    /**
     * create html set Edit Text
     *
     * @param html
     */
    private void setEditText(String html) {
        if (Build.VERSION.SDK_INT >= 24) {
            this.HTMLText.setText(Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY));
        } else {
            HTMLText.setText(Html.fromHtml(html));
        }
    }

    /**
     * set Edit label
     *
     * @param label
     */
    private void setHWebEditLabel(int label) {
        if (HWebTextEnum.contains(label)) {
            HWebTextEnum.remove(new Integer(label));
        } else
            HWebTextEnum.add(label);
    }

    /**
     * set selection position
     *
     * @param inputText    text
     * @param selectionEnd selection end
     */
    private void setSelection(CharSequence inputText, int selectionEnd) {
        if (inputText.equals("") && selectionEnd > 0)
            selectionEnd = selectionEnd - 1;
        else
            selectionEnd = selectionEnd + inputText.length();
        this.HTMLText.setSelection(selectionEnd + hyperlinksLength);
        this.hyperlinksLength = 0;
    }

    private void showAddHyperlinksFragment() {
        hyperlinksFragment=new HyperlinksFragment();
        hyperlinksFragment.setOnClickSubject(this);
        hyperlinksFragment.show(((AppCompatActivity)context).getSupportFragmentManager(),"");
    }

    /**
     *
     */
    private void updateView() {
        detectBean(HWebHtmlList);
        this.InputFlag = true;
        int end = HTMLText.getSelectionEnd();
        int start = HTMLText.getSelectionStart();
        setEditText(createHtml());
        HTMLText.setSelection(start + hyperlinksLength, end + hyperlinksLength);
        hyperlinksLength = 0;
    }

    /***
     * detect bean repetition
     *
     * @param beanArrayList
     */
    public void detectBean(ArrayList<HtmlTextBean> beanArrayList) {
        removeList.clear();
        int next = 1;
        for (int position = 0; position < beanArrayList.size(); position++) {
            HtmlTextBean localHtmlTextBean = beanArrayList.get(position);
            if (position != 0 && (localHtmlTextBean.getHtmlLabelList().equals(
                    (beanArrayList.get(position - next)).getHtmlLabelList())) &&
                    (localHtmlTextBean.getColor().equals(beanArrayList.get(position - next).getColor()))) {
                boolean flag=beanArrayList.get(position - next).addStringBuffer(localHtmlTextBean.getText(),
                        localHtmlTextBean.getStart());
                if (flag){
                    removeList.add(localHtmlTextBean);
                }
                next++;
            }
        }
        beanArrayList.removeAll(this.removeList);
    }

    /**
     * 改变标签
     *
     * @param label
     */
    public void checkEditLabel(int label) {
        if (this.HTMLText.getSelectionStart() >= this.HTMLText.getSelectionEnd()) {
            //setHWebEditLabel(label);
            //updateView();
            HtmlTextBean lastBean=HWebHtmlList.get(HWebHtmlList.size()-1);
            HtmlTextBean newBean=new HtmlTextBean(lastBean.getEnd());
            newBean.addHtmlLabel(label);
            return;
        }
        divisionHtmlTextList(label);
        updateView();
    }

    private void checkEditColor(String color) {
        if (this.HTMLText.getSelectionStart() >= this.HTMLText.getSelectionEnd()) {
            //this.HWebTextColor = color;
            //updateView();
            HtmlTextBean lastBean=HWebHtmlList.get(HWebHtmlList.size()-1);
            HtmlTextBean newBean=new HtmlTextBean(lastBean.getEnd());
            newBean.setColor(color);
            return;
        }
        divisionHtmlTextList(color);
        updateView();
    }

    private void divisionHtmlTextList(int label) {
        int start = this.HTMLText.getSelectionStart();
        int end = this.HTMLText.getSelectionEnd();
        int position = 0;
        boolean Flag = false;
        addList.clear();
        removeList.clear();
        for (HtmlTextBean htmlTextBean : HWebHtmlList) {
            //选择区域位于一个bean中
            if ((start >= htmlTextBean.getStart()) && (end - 1 <= htmlTextBean.getEnd()) && (start < htmlTextBean.getEnd())) {
                removeList.add(htmlTextBean);
                if (!Flag) {
                    position = HWebHtmlList.indexOf(htmlTextBean);
                    Flag = !Flag;
                }
                htmlTextBean.divisionHtmlText(this.addList, start, end, label);
            } else if ((start >= htmlTextBean.getStart()) && (end - 1 > htmlTextBean.getEnd())
                    && (htmlTextBean.getEnd() > start)) {
                removeList.add(htmlTextBean);
                if (!Flag) {
                    position = HWebHtmlList.indexOf(htmlTextBean);
                    Flag = !Flag;
                }
                htmlTextBean.divisionHtmlText(this.addList, start, htmlTextBean.getEnd(), label);
                start += htmlTextBean.getEnd() - htmlTextBean.getStart();
            }
        }
        HWebHtmlList.removeAll(removeList);
        HWebHtmlList.addAll(position, addList);
    }

    private void divisionHtmlTextList(String color) {
        int start = this.HTMLText.getSelectionStart();
        int end = this.HTMLText.getSelectionEnd();
        int position = 0;
        boolean Flag = false;
        addList.clear();
        removeList.clear();
        for (HtmlTextBean htmlTextBean : HWebHtmlList) {
            //选择区域位于一个bean中
            if ((start >= htmlTextBean.getStart()) && (end - 1 <= htmlTextBean.getEnd()) && (start < htmlTextBean.getEnd())) {
                removeList.add(htmlTextBean);
                if (!Flag) {
                    position = HWebHtmlList.indexOf(htmlTextBean);
                    Flag = !Flag;
                }
                htmlTextBean.divisionHtmlText(this.addList, start, end, color);
            } else if (start >= htmlTextBean.getStart() && end > htmlTextBean.getEnd()
                    && htmlTextBean.getEnd() > start) {
                removeList.add(htmlTextBean);
                if (!Flag) {
                    position = HWebHtmlList.indexOf(htmlTextBean);
                    Flag = !Flag;
                }
                htmlTextBean.divisionHtmlText(this.addList, start, htmlTextBean.getEnd(), color);
                start += htmlTextBean.getEnd() - htmlTextBean.getStart();
            }
        }
        this.HWebHtmlList.removeAll(this.removeList);
        this.HWebHtmlList.addAll(position, this.addList);
    }

    public void upDataView(ArrayList<HtmlTextBean> paramArrayList, ArrayList<Integer> paramArrayList1) {
        HWebHtmlList = paramArrayList;
        HWebTextEnum = paramArrayList1;
        updateView();
    }

    private String createHtml() {
        String str1 = getCreateHtmlContent();
        String str2 = getCreateHtmlTop();
        String str3 = getCreateHtmlBottom();
        return str2 + str1 + str3;
    }

    private String getCreateHtmlBottom() {
        String str = "";
        for (int labelEnum : HWebTextEnum) {
            switch (labelEnum) {
                default:
                    break;
                case 4:
                    str = str + "</i>";
                    break;
                case 5:
                    str = str + "</strong>";
                    break;
                case 6:
                    str = str + "</u>";
            }
        }
        return str + "</font></div>";
    }

    private String getCreateHtmlContent() {
        String str = "";
        Iterator localIterator = this.HWebHtmlList.iterator();
        while (localIterator.hasNext()) {
            HtmlTextBean localHtmlTextBean = (HtmlTextBean) localIterator.next();
            str = str + localHtmlTextBean.createHtmlText();
        }
        return str.replaceAll("\n", "<br/>");
    }

    private String getCreateHtmlTop() {
        String str = "<div style=\"text-align:" + this.HWebTextGravity + " font-size:" + this.HWebTextSize + "px\"><font color=\"" + this.HWebTextColor + "\">";
        Iterator localIterator = this.HWebTextEnum.iterator();
        while (localIterator.hasNext())
            switch (((Integer) localIterator.next()).intValue()) {
                default:
                    break;
                case 4:
                    str = str + "<u>";
                    break;
                case 5:
                    str = str + "<strong>";
                    break;
                case 6:
                    str = str + "<i>";
            }
        return str;
    }

    private void createHtmlTextBean(int position, CharSequence charSequence) {

        int end = HWebHtmlList.get(HWebHtmlList.size() - 1).getEnd();
        HtmlTextBean localHtmlTextBean = new HtmlTextBean(end);
        if (position - end == 1) {
            localHtmlTextBean.addStringBuffer("\n", end);
        }
        if (position - end == 2) {
            localHtmlTextBean.addStringBuffer("\n", end);
            localHtmlTextBean.addStringBuffer("\n", end + 1);
        }
        localHtmlTextBean.addStringBuffer(charSequence, position);
        HWebHtmlList.add(localHtmlTextBean);
    }

    private void retrieveSetText(HtmlTextBean paraHWebHtmlListBean, int position) {
        if (this.HWebHtmlList.size() == 0)
            this.HWebHtmlList.add(paraHWebHtmlListBean);
        else {
            for (HtmlTextBean htmlBean : HWebHtmlList) {
                if (htmlBean.getStart() <= position && position <= htmlBean.getEnd()) {
                    htmlBean.addHyperlinks(this.HWebHtmlList, paraHWebHtmlListBean, position);
                    return;
                }
            }
        }
    }

    /**
     * @param inputChar 输入的字符
     * @param position  输入位置
     */
    private void retrieveSetText(CharSequence inputChar, int position) {
        if (this.HWebHtmlList.size() == 0)
            this.HWebHtmlList.add(new HtmlTextBean(0));
        HtmlTextBean lastBean=HWebHtmlList.get(HWebHtmlList.size()-1);
        if (!inputChar.equals("")&&position==lastBean.getStart()){
            lastBean.addStringBuffer(inputChar, position);
        }
        for (HtmlTextBean htmlBean : HWebHtmlList) {
            if (htmlBean.getStart() <= position && position <= htmlBean.getEnd()) {
                if (inputChar.equals("")) {
                    if (htmlBean.getHtmlLabelList().contains(Integer.valueOf(HtmlLabelEnum.LABEL_A)))
                        hyperlinksLength = -htmlBean.getText().length()+1;
                    htmlBean.subStringBuffer(position, HWebHtmlList, HWebHtmlList.indexOf(htmlBean));
                } else {
                    if (htmlBean.getHtmlLabelList().contains(Integer.valueOf(HtmlLabelEnum.LABEL_A))){
                        HtmlTextBean newHtmlBean=new HtmlTextBean(htmlBean.getEnd());
                        newHtmlBean.addStringBuffer(inputChar,position);
                        this.HWebHtmlList.add(newHtmlBean);
                    }else
                        htmlBean.addStringBuffer(inputChar, position);
                }
                return;
            }
        }
        detectBean(HWebHtmlList);
        createHtmlTextBean(position, inputChar);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.view_html_bottom_back:
                if (onEnumClickListener!=null)
                    onEnumClickListener.onBack();
                break;
            case R.id.view_html_bottom_hyperlink:
                showAddHyperlinksFragment();
                break;
            case R.id.view_html_bottom_style:
                setViewVisibility(tTextStyle);
                break;
            case R.id.view_html_bottom_size:
                setViewVisibility(tTextSize);
                break;
            case R.id.view_html_bottom_position:
                setViewVisibility(tTextPosition);
                break;
            case R.id.view_html_bottom_color:
                setViewVisibility(tTextColor);
                break;
            case R.id.view_html_bottom_subject:
                if (onEnumClickListener!=null)
                    onEnumClickListener.onSubject(HWebHtmlList,HWebTextEnum,HWebTextGravity,HWebTextSize,createHtml());
                break;
            case R.id.view_html_bottom_font_style_bold:
                checkEditLabel(HtmlLabelEnum.LABEL_B);
                break;
            case R.id.view_html_bottom_font_style_italics:
                checkEditLabel(HtmlLabelEnum.LABEL_I);
                break;
            case R.id.view_html_bottom_font_style_underline:
                checkEditLabel(HtmlLabelEnum.LABEL_U);
                break;
            case R.id.view_html_bottom_font_size_add:
                HWebTextSize += 2;
                HTMLText.setTextSize(HWebTextSize);
                break;
            case R.id.view_html_bottom_font_size_default:
                HWebTextSize = 14;
                HTMLText.setTextSize(HWebTextSize);
                break;
            case R.id.view_html_bottom_font_size_sub:
                HWebTextSize -= 2;
                HTMLText.setTextSize(HWebTextSize);
                break;
            case R.id.view_html_bottom_font_layout_left:
                this.HTMLText.setGravity(Gravity.LEFT);
                this.HWebTextGravity = "left";
                break;
            case R.id.view_html_bottom_font_layout_center:
                this.HTMLText.setGravity(Gravity.CENTER);
                this.HWebTextGravity = "center";
                break;
            case R.id.view_html_bottom_font_layout_right:
                this.HTMLText.setGravity(Gravity.RIGHT);
                this.HWebTextGravity = "right";
                break;
            case R.id.view_html_bottom_font_color_black:
                checkEditColor(FontColor.KEY_COLOR_BLACK);
                break;
            case R.id.view_html_bottom_font_color_white:
                checkEditColor(FontColor.KEY_COLOR_WHITE);
                break;
            case R.id.view_html_bottom_font_color_black_gray:
                checkEditColor(FontColor.KEY_COLOR_GRAY);
                break;
            case R.id.view_html_bottom_font_color_blue:
                checkEditColor(FontColor.KEY_COLOR_BLUE);
                break;
            case R.id.view_html_bottom_font_color_gray:
                checkEditColor(FontColor.KEY_COLOR_GRAY);
                break;
            case R.id.view_html_bottom_font_color_green:
                checkEditColor(FontColor.KEY_COLOR_GREEN);
                break;
            case R.id.view_html_bottom_font_color_red:
                checkEditColor(FontColor.KEY_COLOR_RED);
                break;
            case R.id.view_html_bottom_font_color_violet:
                checkEditColor(FontColor.KEY_COLOR_VOILET);
                break;
            case R.id.view_html_bottom_font_color_yellow:
                checkEditColor(FontColor.KEY_COLOR_YELLOW);
                break;
        }
    }

    public void setViewVisibility(View layout) {
        if (layout.getVisibility() == GONE) {
            tTextColor.setVisibility(GONE);
            tTextSize.setVisibility(GONE);
            tTextPosition.setVisibility(GONE);
            tTextStyle.setVisibility(GONE);
            layout.setVisibility(VISIBLE);
        } else {
            layout.setVisibility(GONE);
        }
    }

    public ArrayList<HtmlTextBean> getHWebHtmlList() {
        return HWebHtmlList;
    }

    @Override
    public void subjectHyperlinks(String key, String value) {
        HtmlTextBean htmlTextBean=new HtmlTextBean(0);
        htmlTextBean.setText(new StringBuffer(key));
        htmlTextBean.addHtmlLabel(HtmlLabelEnum.LABEL_A);
        htmlTextBean.setHyperlinks(value);
        retrieveSetText(htmlTextBean,HTMLText.getSelectionStart());
        hyperlinksLength=key.length();
        updateView();
        hyperlinksFragment.dismiss();
    }

    interface OnEnumClickListener{
        void onBack();
        void onSubject(ArrayList<HtmlTextBean> childHtmlBeans,ArrayList<Integer> HWebHtmlLabel,String gravity,int Size,String html);
    }
}
