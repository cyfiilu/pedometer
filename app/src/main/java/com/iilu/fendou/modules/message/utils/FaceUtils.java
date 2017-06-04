
package com.iilu.fendou.modules.message.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.util.ArrayMap;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.widget.TextView;

import com.iilu.fendou.R;

import org.apache.log4j.Logger;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FaceUtils {
    
    private static final Logger mlog = Logger.getLogger(FaceUtils.class.getSimpleName());

    public static String mRegexEmotion = "\\[([\u4e00-\u9fa5\\w])+\\]";
    private static final Spannable.Factory mSpannableFactory = Spannable.Factory.getInstance();

    /**
     * 表情类型标志符
     */
    public static final int FACE_CLASSIC_TYPE = 0x0001;//经典表情

    /**
     * key-表情文字;
     * value-表情图片资源
     */
    private static ArrayMap<String, Integer> EMPTY_MAP;
    private static ArrayMap<String, Integer> FACE_CLASSIC_MAP;

    static {
        EMPTY_MAP = new ArrayMap<>();
        FACE_CLASSIC_MAP = new ArrayMap<>();

        FACE_CLASSIC_MAP.put("[呵呵]", R.mipmap.d_hehe);
        FACE_CLASSIC_MAP.put("[嘻嘻]", R.mipmap.d_xixi);
        FACE_CLASSIC_MAP.put("[哈哈]", R.mipmap.d_haha);
        FACE_CLASSIC_MAP.put("[爱你]", R.mipmap.d_aini);
        FACE_CLASSIC_MAP.put("[挖鼻屎]", R.mipmap.d_wabishi);
        FACE_CLASSIC_MAP.put("[吃惊]", R.mipmap.d_chijing);
        FACE_CLASSIC_MAP.put("[晕]", R.mipmap.d_yun);
        FACE_CLASSIC_MAP.put("[泪]", R.mipmap.d_lei);
        FACE_CLASSIC_MAP.put("[馋嘴]", R.mipmap.d_chanzui);
        FACE_CLASSIC_MAP.put("[抓狂]", R.mipmap.d_zhuakuang);
        FACE_CLASSIC_MAP.put("[哼]", R.mipmap.d_heng);
        FACE_CLASSIC_MAP.put("[可爱]", R.mipmap.d_keai);
        FACE_CLASSIC_MAP.put("[怒]", R.mipmap.d_nu);
        FACE_CLASSIC_MAP.put("[汗]", R.mipmap.d_han);
        FACE_CLASSIC_MAP.put("[害羞]", R.mipmap.d_haixiu);
        FACE_CLASSIC_MAP.put("[睡觉]", R.mipmap.d_shuijiao);
        FACE_CLASSIC_MAP.put("[钱]", R.mipmap.d_qian);
        FACE_CLASSIC_MAP.put("[偷笑]", R.mipmap.d_touxiao);
        FACE_CLASSIC_MAP.put("[笑cry]", R.mipmap.d_xiaoku);
        FACE_CLASSIC_MAP.put("[doge]", R.mipmap.d_doge);
        FACE_CLASSIC_MAP.put("[喵喵]", R.mipmap.d_miao);
        FACE_CLASSIC_MAP.put("[酷]", R.mipmap.d_ku);
        FACE_CLASSIC_MAP.put("[衰]", R.mipmap.d_shuai);
        FACE_CLASSIC_MAP.put("[闭嘴]", R.mipmap.d_bizui);
        FACE_CLASSIC_MAP.put("[鄙视]", R.mipmap.d_bishi);
        FACE_CLASSIC_MAP.put("[花心]", R.mipmap.d_huaxin);
        FACE_CLASSIC_MAP.put("[鼓掌]", R.mipmap.d_guzhang);
        FACE_CLASSIC_MAP.put("[悲伤]", R.mipmap.d_beishang);
        FACE_CLASSIC_MAP.put("[思考]", R.mipmap.d_sikao);
        FACE_CLASSIC_MAP.put("[生病]", R.mipmap.d_shengbing);
        FACE_CLASSIC_MAP.put("[亲亲]", R.mipmap.d_qinqin);
        FACE_CLASSIC_MAP.put("[怒骂]", R.mipmap.d_numa);
        FACE_CLASSIC_MAP.put("[太开心]", R.mipmap.d_taikaixin);
        FACE_CLASSIC_MAP.put("[懒得理你]", R.mipmap.d_landelini);
        FACE_CLASSIC_MAP.put("[右哼哼]", R.mipmap.d_youhengheng);
        FACE_CLASSIC_MAP.put("[左哼哼]", R.mipmap.d_zuohengheng);
        FACE_CLASSIC_MAP.put("[嘘]", R.mipmap.d_xu);
        FACE_CLASSIC_MAP.put("[委屈]", R.mipmap.d_weiqu);
        FACE_CLASSIC_MAP.put("[吐]", R.mipmap.d_tu);
        FACE_CLASSIC_MAP.put("[可怜]", R.mipmap.d_kelian);
        FACE_CLASSIC_MAP.put("[打哈气]", R.mipmap.d_dahaqi);
        FACE_CLASSIC_MAP.put("[挤眼]", R.mipmap.d_jiyan);
        FACE_CLASSIC_MAP.put("[失望]", R.mipmap.d_shiwang);
        FACE_CLASSIC_MAP.put("[顶]", R.mipmap.d_ding);
        FACE_CLASSIC_MAP.put("[疑问]", R.mipmap.d_yiwen);
        FACE_CLASSIC_MAP.put("[困]", R.mipmap.d_kun);
        FACE_CLASSIC_MAP.put("[感冒]", R.mipmap.d_ganmao);
        FACE_CLASSIC_MAP.put("[拜拜]", R.mipmap.d_baibai);
        FACE_CLASSIC_MAP.put("[黑线]", R.mipmap.d_heixian);
        FACE_CLASSIC_MAP.put("[阴险]", R.mipmap.d_yinxian);
        FACE_CLASSIC_MAP.put("[打脸]", R.mipmap.d_dalian);
        FACE_CLASSIC_MAP.put("[傻眼]", R.mipmap.d_shayan);
        FACE_CLASSIC_MAP.put("[猪头]", R.mipmap.d_zhutou);
        FACE_CLASSIC_MAP.put("[熊猫]", R.mipmap.d_xiongmao);
        FACE_CLASSIC_MAP.put("[兔子]", R.mipmap.d_tuzi);
    }

    /**
     * 根据名称获取当前表情图标R值
     *
     * @param EmotionType 表情类型标志符
     * @param imgName     名称
     * @return
     */
    public static int getImgByName(int EmotionType, String imgName) {
        Integer integer = null;
        switch (EmotionType) {
            case FACE_CLASSIC_TYPE:
                integer = FACE_CLASSIC_MAP.get(imgName);
                break;
            default:
                mlog.error("the emojiMap is null!! Handle Yourself ");
                break;
        }
        return integer == null ? -1 : integer;
    }

    /**
     * 根据类型获取表情数据
     *
     * @param EmotionType
     * @return
     */
    public static ArrayMap<String, Integer> getEmojiMap(int EmotionType) {
        ArrayMap EmojiMap;
        switch (EmotionType) {
            case FACE_CLASSIC_TYPE:
                EmojiMap = FACE_CLASSIC_MAP;
                break;
            default:
                EmojiMap = EMPTY_MAP;
                break;
        }
        return EmojiMap;
    }

    public static boolean containsKey(String key){
        boolean hasKey = false;
        SpannableString spannableString = new SpannableString(key);
        Pattern patternEmotion = Pattern.compile(mRegexEmotion);
        Matcher matcher = patternEmotion.matcher(spannableString);
        while(matcher.find()) {
            hasKey = true;
            break;
        }
        return hasKey;
    }

    public static Spannable createFaceText(Context context, TextView textView, CharSequence text) {
        Spannable spannable = mSpannableFactory.newSpannable(text);
        addFaces(context, textView, spannable);
        return spannable;
    }

    private static void addFaces(Context context, TextView textView, Spannable spannable) {
        Pattern patternEmotion = Pattern.compile(mRegexEmotion);
        Matcher matcher = patternEmotion.matcher(spannable);
        while (matcher.find()) {
            boolean set = true;
            for (ImageSpan span : spannable.getSpans(matcher.start(), matcher.end(), ImageSpan.class)) {
                if (spannable.getSpanStart(span) >= matcher.start() && spannable.getSpanEnd(span) <= matcher.end()) {
                    spannable.removeSpan(span);
                } else {
                    set = false;
                    break;
                }
            }
            if (set) {
                Integer imgRes = FaceUtils.getImgByName(FaceUtils.FACE_CLASSIC_TYPE, matcher.group());
                if (imgRes != null) {
                    // 压缩表情图片
                    int size = (int) textView.getTextSize() * 20 / 10;
                    Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), imgRes);
                    Bitmap scaleBitmap = Bitmap.createScaledBitmap(bitmap, size, size, true);
                    ImageSpan imageSpan = new ImageSpan(context, scaleBitmap);
                    spannable.setSpan(imageSpan, matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            }
        }
    }
}
