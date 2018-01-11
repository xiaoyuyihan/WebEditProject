package com.webedit;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.Html;
import android.widget.Toast;

import com.utils.TypeConverUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lw on 2017/11/30.
 */

public class HtmlTextBean implements Parcelable {
    //起始位置
    private int start = 0;
    //结束位置      ps：end-start=text.size      this.end=next.start
    private int end = 0;
    //颜色
    private String Color = "#272636";
    //存储标签
    private ArrayList<Integer> HtmlLabelList = new ArrayList();
    //超链接地址
    private String hyperlinks;
    //编译后的html
    private String mHtmlText = "";
    //文本信息
    private StringBuffer mText = new StringBuffer("");

    public HtmlTextBean(int position){
        this.start=position;
        this.end=position;
    }

    public HtmlTextBean(int start, int end) {
        this.start = start;
        this.end = end;
    }

    protected HtmlTextBean(Parcel in) {
        start = in.readInt();
        end = in.readInt();
        Color = in.readString();
        HtmlLabelList = TypeConverUtil.intToList(in);
        hyperlinks = in.readString();
        mHtmlText = in.readString();
    }

    public static final Creator<HtmlTextBean> CREATOR = new Creator<HtmlTextBean>() {
        @Override
        public HtmlTextBean createFromParcel(Parcel in) {
            return new HtmlTextBean(in);
        }

        @Override
        public HtmlTextBean[] newArray(int size) {
            return new HtmlTextBean[size];
        }
    };

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public String getColor() {
        return Color;
    }

    public void setColor(String color) {
        Color = color;
    }

    public ArrayList<Integer> getHtmlLabelList() {
        return HtmlLabelList;
    }

    public void setHtmlLabelList(ArrayList<Integer> htmlLabelList) {
        HtmlLabelList.clear();
        HtmlLabelList.addAll(htmlLabelList);
    }

    public void addHtmlLabelList(ArrayList<Integer> htmlLabelList) {
        HtmlLabelList.addAll(htmlLabelList);
    }

    public void addHtmlLabel(Integer htmlLabel) {
        HtmlLabelList.add(htmlLabel);
    }

    public void removeHtmlLabelList(ArrayList<Integer> htmlLabelList) {
        HtmlLabelList.removeAll(htmlLabelList);
    }

    public String getHyperlinks() {
        return hyperlinks;
    }

    public void setHyperlinks(String hyperlinks) {
        this.hyperlinks = hyperlinks;
    }

    public String getmHtmlText() {
        return mHtmlText;
    }

    public StringBuffer getText() {
        return mText;
    }

    public void setText(StringBuffer mText) {
        this.mText = mText;
        upDataEndPosition(mText.length());
    }

    public void addHtmlLabelList(int HtmlLabel) {
        if (HtmlLabelList.contains(HtmlLabel)) {
            HtmlLabelList.remove(new Integer(HtmlLabel));
        } else
            HtmlLabelList.add(HtmlLabel);
    }

    public void addHyperlinks(ArrayList<HtmlTextBean> paramArrayList,
                              HtmlTextBean paramHtmlTextBean, int position) {
        int beanPosition = paramArrayList.indexOf(this);//0
        int length = paramHtmlTextBean.getText().length();
        // 超链接 位于next bean 前
        if (position == getStart()) {
            paramArrayList.add(beanPosition, paramHtmlTextBean);
        }else {
            //超链接 在bean 内部
            CharSequence localCharSequence = mText.subSequence(0, position - start);
            mText = mText.delete(0, position - start);
            //新建bean
            if (!localCharSequence.equals("")) {
                HtmlTextBean localHtmlTextBean = new HtmlTextBean(start);
                localHtmlTextBean.setText(new StringBuffer(localCharSequence));
                localHtmlTextBean.setHtmlLabelList(HtmlLabelList);
                localHtmlTextBean.setColor(getColor());
                paramArrayList.add(beanPosition, localHtmlTextBean);
                //更新bean 位置
                beanPosition++;
            }
            //更新超链接位置
            paramHtmlTextBean.upDataAllPosition(position);
            paramArrayList.add(beanPosition, paramHtmlTextBean);
        }
        beanPosition = beanPosition + 1;
        setStart(paramHtmlTextBean.getStart());
        //更新位置
        for (int m = beanPosition; m < paramArrayList.size(); m++)
            paramArrayList.get(m).upDataAllPosition(length);
    }

    /**
     * @param Char     字符串
     * @param position 光标位置
     */
    public boolean addStringBuffer(CharSequence Char, int position) {
        if (position == this.end) {
            this.mText.append(Char);
            this.end += Char.length();
            return true;
        } else if (position > this.start && position < this.end) {
            try {
                this.mText.insert(position - this.start, Char);
                this.end += Char.length();
                return true;
            } catch (StringIndexOutOfBoundsException e) {
                return false;
            }
        }
        return false;
    }

    public String createHtmlText() {
        mHtmlText = ("<font color='" + this.Color + "'>" + this.mText.toString().replaceAll(" ", "&nbsp;") + "</font>");
        for (int htmlEnum : HtmlLabelList) {
            switch (htmlEnum) {
                case 2:
                    this.mHtmlText = ("<a href=\"" + this.hyperlinks + "\">" + this.mHtmlText + "</a>");
                    break;
                case 5:
                    this.mHtmlText = ("<strong>" + this.mHtmlText + "</strong>");
                    break;
                case 6:
                    this.mHtmlText = ("<i>" + this.mHtmlText + "</i>");
                    break;
                case 4:
                    this.mHtmlText = ("<u>" + this.mHtmlText + "</u>");
            }
        }
        return mHtmlText;
    }

    /**
     * 追加标签
     *
     * @param paramArrayList
     * @param start
     * @param end
     * @param htmlLabel
     */
    public void divisionHtmlText(ArrayList<HtmlTextBean> paramArrayList, int start, int end, int htmlLabel) {
        if (start == this.start && end == this.end || HtmlLabelList.contains(Integer.valueOf(HtmlLabelEnum.LABEL_A))) {
            addHtmlLabelList(htmlLabel);
            paramArrayList.add(this);
            return;
        }
        divisionHtmlText(paramArrayList, start, end);
        addHtmlLabelList(htmlLabel);
    }

    public void divisionHtmlText(ArrayList<HtmlTextBean> paramArrayList, int start, int end, String color) {
        if (start == this.start && end == this.end || HtmlLabelList.contains(Integer.valueOf(HtmlLabelEnum.LABEL_A))) {
            setColor(color);
            paramArrayList.add(this);
            return;
        }
        divisionHtmlText(paramArrayList, start, end);
        setColor(color);
    }

    public void divisionHtmlText(ArrayList<HtmlTextBean> paramArrayList, int start, int end) {
        mHtmlText = "";
        CharSequence unCheckLastChar = mText.subSequence(0, start - this.start);
        mText = mText.delete(0, start - this.start);
        if (!unCheckLastChar.equals("")) {
            paramArrayList.add(createHtmlTextBean(this.start, unCheckLastChar, HtmlLabelList, getColor()));
        }
        paramArrayList.add(this);
        int checkLength = end - start;
        int textLength = mText.length();
        CharSequence unCheckNextChar = mText.subSequence(checkLength, textLength);
        //选中的文本
        mText = mText.delete(checkLength, textLength);
        if (!unCheckNextChar.equals("")) {
            paramArrayList.add(createHtmlTextBean(end, unCheckNextChar, this.HtmlLabelList, getColor()));
        }
        setStart(start);
        setEnd(end);
    }

    /**
     * 单字符删除
     *
     * @param position     光标位置
     * @param HtmlTextList html text bean 集合
     * @param listPosition 光标所在bean位置
     */
    public void subStringBuffer(int position, ArrayList<HtmlTextBean> HtmlTextList, int listPosition) {
        this.end = end - 1;
        int subLength = 0;
        int i;
        if (this.start == this.end ||
                this.HtmlLabelList.contains(Integer.valueOf(HtmlLabelEnum.LABEL_A))) {
            HtmlTextList.remove(this);
            subLength = -mText.length();
            i=listPosition;
        } else {
            this.mText = this.mText.delete(position - this.start - 1, position - this.start);
            subLength = -1;
            i = listPosition + 1;
        }
        for (; i < HtmlTextList.size(); i++) {
            HtmlTextList.get(i).upDataAllPosition(subLength);
        }
    }

    public void upDataAllPosition(int paramInt) {
        this.start = (paramInt + this.start);
        this.end = (paramInt + this.end);
    }

    public void upDataEndPosition(int length) {
        this.end = (length + this.end);
    }

    public void upDataStartPositon(int paramInt) {
        this.start = (paramInt + this.start);
    }

    private HtmlTextBean createHtmlTextBean(int position, CharSequence text, ArrayList<Integer> htmlLabelList, String color) {
        HtmlTextBean htmlBean = new HtmlTextBean(position);
        htmlBean.setText(new StringBuffer(text));
        htmlBean.addHtmlLabelList(htmlLabelList);
        htmlBean.setColor(color);
        return htmlBean;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(start);
        dest.writeInt(end);
        dest.writeString(Color);
        dest.writeInt(HtmlLabelList.size());
        dest.writeIntArray(TypeConverUtil.listToInt(HtmlLabelList));
        dest.writeString(hyperlinks);
        dest.writeString(mHtmlText);
    }
}
